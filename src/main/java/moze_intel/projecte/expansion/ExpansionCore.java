package moze_intel.projecte.expansion;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.registries.ExpansionAttachmentTypes;
import moze_intel.projecte.expansion.registries.ExpansionAttributes;
import moze_intel.projecte.expansion.registries.ExpansionBlockEntityTypes;
import moze_intel.projecte.expansion.registries.ExpansionBlocks;
import moze_intel.projecte.expansion.registries.ExpansionBlockTypes;
import moze_intel.projecte.expansion.registries.ExpansionCreativeTabs;
import moze_intel.projecte.expansion.registries.ExpansionEnchantments;
import moze_intel.projecte.expansion.registries.ExpansionItems;
import moze_intel.projecte.expansion.registries.ExpansionMenus;
import moze_intel.projecte.expansion.registries.ExpansionSoundEvents;
import moze_intel.projecte.expansion.util.AdvancedAlchemicalChest;
import moze_intel.projecte.expansion.util.AlchemicalCollectionCollector;
import moze_intel.projecte.expansion.util.Fuel;
import moze_intel.projecte.expansion.util.Matter;
import moze_intel.projecte.expansion.util.Star;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import org.slf4j.Logger;

/**
 * Entry point for everything ProjectExpansion adds to ProjectE.
 * <p>
 * This is intentionally <b>not</b> a {@code @Mod} class: it is driven from {@link PECore} so that there is only ever a single mod
 * container. Call {@link #register(IEventBus)} from the mod bus and {@link #init()} for the common setup.
 */
public class ExpansionCore {

	@SuppressWarnings("unused")
	public static final Logger Logger = LogUtils.getLogger();

	private static boolean initialized;

	private ExpansionCore() {
	}

	/**
	 * Registers all Expansion registries on the given mod event bus. Must be called during mod construction, before the registry events fire.
	 */
	public static void register(IEventBus modEventBus) {

		//These add their entries to the registers below, and 1.20.4 only picks up the entries that exist when
		//the DeferredRegister is attached to the bus, so they have to run first
		Fuel.registerAll();
		Matter.registerAll();
		Star.registerAll();
		AdvancedAlchemicalChest.register();
		ExpansionAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
		ExpansionAttributes.ATTRIBUTES.register(modEventBus);
		ExpansionBlockTypes.BLOCK_TYPES.register(modEventBus);
		ExpansionBlocks.BLOCKS.register(modEventBus);
		ExpansionItems.ITEMS.register(modEventBus);
		ExpansionBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
		ExpansionMenus.MENUS.register(modEventBus);
		ExpansionCreativeTabs.CREATIVE_TABS.register(modEventBus);
		ExpansionSoundEvents.SOUND_EVENTS.register(modEventBus);
		ExpansionEnchantments.ENCHANTMENTS.register(modEventBus);
	}

	/**
	 * Common setup that is not tied to the mod bus. Safe to call multiple times, only the first call has an effect.
	 */
	public static void init() {
		if (initialized) return;
		initialized = true;
		NeoForge.EVENT_BUS.addListener(ExpansionCore::serverTick);
	}

	private static void serverTick(TickEvent.ServerTickEvent event) {
		AlchemicalCollectionCollector.process();
		for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
			for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
				ItemStack stack = player.getInventory().getItem(i);
				if (stack.getItem().equals(ExpansionItems.INFINITE_FUEL.get()) && !stack.hasData(ExpansionAttachmentTypes.OWNER)) {
					stack.setData(ExpansionAttachmentTypes.OWNER, new ExpansionAttachmentTypes.OwnerData(player.getUUID(), player.getName().getString()));
					continue;
				}
				boolean hasEnch = EnchantmentHelper.getTagEnchantmentLevel(ExpansionEnchantments.ALCHEMICAL_COLLECTION.get(), stack) > 0;
				if (hasEnch && !stack.hasData(PEAttachmentTypes.ACTIVE)) {
					stack.setData(PEAttachmentTypes.ACTIVE, true);
				}
			}
		}
	}
}
