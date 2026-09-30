package moze_intel.projecte.emc;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.math.BigInteger;
import java.util.UUID;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.utils.EmcGainBonus;
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
 * into an endless source of EMC. It used to be profitable: the gain side additionally multiplied the credited amount by
 * the per-player {@link EmcGainBonus} while the debit side did not, so a single round trip paid out the bonus percent
 * every single time. These tests pin the invariant that no per-player bonus may take part in the exchange rate.
 */
@ExtendWith(EphemeralTestServerProvider.class)
@DisplayName("Test EMC conservation of the transmutation tablet exchange")
class EmcGainBonusConservationTest {

	private static final UUID PLAYER = UUID.nameUUIDFromBytes("test-player".getBytes());

	/** A deliberately huge bonus: even one percent was enough to make the round trip profitable. */
	private static final BigInteger HUGE_BONUS = BigInteger.valueOf(100_000);

	/**
	 * Real vanilla items with the values this build gives them, plus the edge cases where rounding bites: values that are
	 * not multiples of nine make the nugget style conversions land just under their output.
	 */
	private static final Item[] ITEMS = {
			Items.STONE, Items.COBBLESTONE, Items.DIRT, Items.SAND, Items.GRAVEL, Items.OBSIDIAN,
			Items.REDSTONE, Items.AMETHYST_SHARD, Items.IRON_INGOT, Items.IRON_NUGGET, Items.GOLD_INGOT,
			Items.GOLD_NUGGET, Items.COPPER_INGOT, Items.COPPER_NUGGET, Items.AMETHYST_BLOCK, Items.DIAMOND,
			Items.EMERALD, Items.DIAMOND_BLOCK, Items.EMERALD_BLOCK, Items.NETHERITE_INGOT, Items.NETHERITE_SCRAP,
			Items.QUARTZ, Items.REDSTONE_BLOCK, Items.QUARTZ_BLOCK, Items.COAL, Items.GLOWSTONE,
			Items.CRYING_OBSIDIAN, Items.TNT, Items.GOLD_BLOCK, Items.NETHER_STAR
	};

	private static final long[] VALUES = {
			1, 1, 1, 1, 4, 64,
			64, 32, 256, 28, 2048,
			227, 128, 12, 128, 8192,
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
		EmcGainBonus.setPercent(PLAYER, BigInteger.ZERO);
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
	@DisplayName("A round trip through the tablet is never profitable, even with a huge gain bonus")
	void testRoundTripIsNeverProfitableWithBonus() {
		EmcGainBonus.setPercent(PLAYER, HUGE_BONUS);

		for (ItemInfo info : EMCMappingHandler.getMappedItems()) {
			if (IEMCProxy.INSTANCE.getValue(info) <= 0) {
				//Items without a value cannot be taken out of the tablet in the first place
				continue;
			}
			long debited = IEMCProxy.INSTANCE.getValue(info);
			long credited = IEMCProxy.INSTANCE.getSellValue(info);

			Assertions.assertTrue(credited <= debited,
					() -> "Item " + info + " nets " + (credited - debited) + " EMC per round trip with a "
							+ HUGE_BONUS + "% gain bonus active");
		}
	}

	@Test
	@DisplayName("The exchange rate does not depend on the per-player gain bonus")
	void testExchangeRateIgnoresGainBonus() {
		long[] withoutBonus = new long[ITEMS.length];
		for (int i = 0; i < ITEMS.length; i++) {
			withoutBonus[i] = IEMCProxy.INSTANCE.getSellValue(ItemInfo.fromItem(ITEMS[i]));
		}

		EmcGainBonus.setPercent(PLAYER, HUGE_BONUS);

		for (int i = 0; i < ITEMS.length; i++) {
			ItemInfo info = ItemInfo.fromItem(ITEMS[i]);
			Assertions.assertEquals(withoutBonus[i], IEMCProxy.INSTANCE.getSellValue(info),
					() -> "The sell value of " + info + " changed because a gain bonus was active. The gain bonus may "
							+ "only be applied where EMC is created from nothing, never to the item exchange rate");
		}
	}

	@Test
	@DisplayName("The gain bonus still multiplies EMC that is genuinely created from nothing")
	void testGainBonusStillAppliesToFreeGains() {
		//The adaptation reward keeps the bonus, it is the one place where EMC is granted rather than converted
		EmcGainBonus.setPercent(PLAYER, BigInteger.TEN);

		Assertions.assertEquals(BigInteger.valueOf(1100), EmcGainBonus.apply(BigInteger.valueOf(1000), PLAYER));
	}

	@Test
	@DisplayName("A stacked item is credited exactly once per item, with no rounding profit")
	void testStackedSellValueIsExact() {
		EmcGainBonus.setPercent(PLAYER, HUGE_BONUS);

		ItemStack stack = new ItemStack(Items.DIAMOND, 64);
		long perItem = IEMCProxy.INSTANCE.getSellValue(ItemInfo.fromStack(stack));

		Assertions.assertEquals(8_192L, perItem);
		Assertions.assertEquals(8_192L * 64, perItem * stack.getCount(),
				"Selling 64 diamonds must credit exactly 64 times their value");
	}
}
