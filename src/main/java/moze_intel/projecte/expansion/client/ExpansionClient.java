package moze_intel.projecte.expansion.client;

import moze_intel.projecte.expansion.block.entity.BlockEntityAdvancedAlchemicalChest;
import moze_intel.projecte.expansion.block.entity.BlockEntityCondenserMK3;
import moze_intel.projecte.expansion.gui.GUIArcaneTransmutationTablet;
import moze_intel.projecte.expansion.gui.GUICollector;
import moze_intel.projecte.expansion.gui.GUICondenserMK3Input;
import moze_intel.projecte.expansion.gui.GUICondenserMK3Output;
import moze_intel.projecte.expansion.registries.ExpansionBlockEntityTypes;
import moze_intel.projecte.expansion.registries.ExpansionMenus;
import moze_intel.projecte.expansion.rendering.ChestRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * Client side registration for the ProjectExpansion content that is merged into ProjectE.
 * <p>
 * Called from {@code moze_intel.projecte.client.PEClient} with the mod event bus. This is the <b>only</b> place the
 * addon's screens and block entity renderers are registered, ProjectE's own registrations live in {@code PEClient} and
 * the recipe viewer / waila integrations register through their entrypoint annotations.
 * <p>
 * Note: the JEI/Jade integrations register themselves through their own entrypoint annotations
 * ({@code @JeiPlugin} and {@code @WailaPlugin}), mirroring how ProjectE does it in
 * {@code moze_intel.projecte.integration}. The EMI/TOP/WTHIT integrations are compiled out stubs on this branch
 * because those mods have not been ported to 26.3.
 */
public class ExpansionClient {

	public static void register(IEventBus modEventBus) {
		modEventBus.addListener(ExpansionClient::registerScreens);
		modEventBus.addListener(ExpansionClient::registerRenderers);
		modEventBus.addListener(ExpansionClient::registerKeyMappings);
	}

	private static void registerScreens(RegisterMenuScreensEvent event) {
		event.register(ExpansionMenus.COLLECTOR_TIER_1.get(), GUICollector.Tier1::new);
		event.register(ExpansionMenus.COLLECTOR_TIER_2.get(), GUICollector.Tier2::new);
		event.register(ExpansionMenus.COLLECTOR_TIER_3.get(), GUICollector.Tier3::new);
		event.register(ExpansionMenus.CONDENSER_MK3_INPUT.get(), GUICondenserMK3Input::new);
		event.register(ExpansionMenus.CONDENSER_MK3_OUTPUT.get(), GUICondenserMK3Output::new);
		event.register(ExpansionMenus.ARCANE_TRANSMUTATION_TABLET.get(), GUIArcaneTransmutationTablet::new);
	}

	private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ExpansionBlockEntityTypes.ADVANCED_ALCHEMICAL_CHEST.get(),
				context -> new ChestRenderer<BlockEntityAdvancedAlchemicalChest>(context, ExpansionBlockEntityTypes.ADVANCED_ALCHEMICAL_CHEST.get()));
		event.registerBlockEntityRenderer(ExpansionBlockEntityTypes.CONDENSER_MK3.get(),
				context -> new ChestRenderer<BlockEntityCondenserMK3>(context, ExpansionBlockEntityTypes.CONDENSER_MK3.get()));
	}

	private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
	}
}
