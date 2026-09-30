package moze_intel.projecte.emc;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Regression tests for EMC conservation across the transmutation tablet exchange.
 * <p>
 * Taking an item out of the tablet debits its full value ({@code getValue}), and putting the very same item back into
 * it credits its sell value ({@code getSellValue}). That exchange has to be non-profitable, otherwise the tablet turns
 * into an endless source of EMC. This has happened in practice: a per-player gain bonus applied to the crediting side
 * only, so a single round trip paid out the bonus percent every time. Any per-player multiplier or rounding rule that
 * ends up on the crediting side alone reintroduces it, so the invariant is pinned here over a spread of values that
 * includes the awkward ones, where a value that is not a multiple of the stack factor lands just under its output.
 */
@ExtendWith(EphemeralTestServerProvider.class)
@DisplayName("Test EMC conservation of the transmutation tablet exchange")
class TransmutationEmcConservationTest {

	/**
	 * Real vanilla items with the values this build gives them, plus the edge cases where rounding bites.
	 */
	private static final Item[] ITEMS = {
			Items.STONE, Items.COBBLESTONE, Items.DIRT, Items.SAND, Items.GRAVEL, Items.OBSIDIAN,
			Items.REDSTONE, Items.AMETHYST_SHARD, Items.IRON_INGOT, Items.IRON_NUGGET, Items.GOLD_INGOT,
			Items.GOLD_NUGGET, Items.COPPER_INGOT, Items.AMETHYST_BLOCK, Items.DIAMOND,
			Items.EMERALD, Items.DIAMOND_BLOCK, Items.EMERALD_BLOCK, Items.NETHERITE_INGOT, Items.NETHERITE_SCRAP,
			Items.QUARTZ, Items.REDSTONE_BLOCK, Items.QUARTZ_BLOCK, Items.COAL, Items.GLOWSTONE,
			Items.CRYING_OBSIDIAN, Items.TNT, Items.GOLD_BLOCK, Items.NETHER_STAR
	};

	private static final long[] VALUES = {
			1, 1, 1, 1, 4, 64,
			64, 32, 256, 28, 2048,
			227, 128, 128, 8192,
			16384, 73728, 147456, 8_192_000, 816,
			256, 576, 1024, 128, 1536,
			768, 32, 18_432, 0
	};

	@BeforeEach
	void setup(MinecraftServer server) {
		Object2LongMap<ItemInfo> map = new Object2LongOpenHashMap<>();
		for (int i = 0; i < ITEMS.length; i++) {
			map.put(ItemInfo.fromItem(ITEMS[i]), VALUES[i]);
		}
		EMCMappingHandler.updateEmcValues(map);
	}

	@AfterEach
	void cleanup() {
		EMCMappingHandler.clearEmcMap();
	}

	@Test
	@DisplayName("Selling an item back never credits more than the tablet debited for it")
	void testSellValueNeverExceedsValue() {
		for (ItemInfo info : EMCMappingHandler.getMappedItems()) {
			long value = IEMCProxy.INSTANCE.getValue(info);
			long sellValue = IEMCProxy.INSTANCE.getSellValue(info);

			Assertions.assertTrue(sellValue <= value,
					() -> "Item " + info + " is worth " + value + " EMC but the tablet credits " + sellValue
							+ " for it, so taking it out and putting it back again is profitable");
		}
	}

	@Test
	@DisplayName("A round trip through the tablet is never profitable")
	void testRoundTripIsNeverProfitable() {
		for (ItemInfo info : EMCMappingHandler.getMappedItems()) {
			if (IEMCProxy.INSTANCE.getValue(info) <= 0) {
				//Items without a value cannot be taken out of the tablet in the first place
				continue;
			}
			long debited = IEMCProxy.INSTANCE.getValue(info);
			long credited = IEMCProxy.INSTANCE.getSellValue(info);

			Assertions.assertTrue(credited <= debited,
					() -> "Item " + info + " nets " + (credited - debited) + " EMC per round trip");
		}
	}

	@Test
	@DisplayName("A whole stack is credited at most what the tablet debited for it")
	void testStackedRoundTripIsNeverProfitable() {
		for (int count : new int[] {1, 9, 16, 64}) {
			for (Item item : ITEMS) {
				ItemInfo info = ItemInfo.fromStack(new ItemStack(item, count));
				long debited = IEMCProxy.INSTANCE.getValue(info) * count;
				long credited = IEMCProxy.INSTANCE.getSellValue(info) * count;

				Assertions.assertTrue(credited <= debited,
						() -> count + " " + item + " nets " + (credited - debited) + " EMC per round trip");
			}
		}
	}
}
