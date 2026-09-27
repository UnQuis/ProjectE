package moze_intel.projecte.expansion.client;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.gui.GUIArcaneTransmutationTablet;
import moze_intel.projecte.expansion.gui.GUICollector;
import moze_intel.projecte.expansion.gui.GUICondenserMK3Input;
import moze_intel.projecte.expansion.gui.GUICondenserMK3Output;
import moze_intel.projecte.expansion.registries.ExpansionMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * Client side registration for the ProjectExpansion content that is merged into ProjectE.
 * <p>
 * Note: the block entity renderers and the TOP inter mod message register themselves through
 * {@code moze_intel.projecte.expansion.events.RenderingEvent} and
 * {@code moze_intel.projecte.expansion.events.IMCEvents}, and the JEI/Jade/WTHIT integrations register themselves
 * through their own entrypoint annotations ({@code @JeiPlugin}, {@code @WailaPlugin} and the
 * {@code wthit_plugins.json} resource), mirroring how ProjectE does it in
 * {@code moze_intel.projecte.integration}. Only the screens and the key mappings are left here.
 */
@Mod.EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ExpansionClient {

	@SubscribeEvent
	public static void registerScreens(RegisterMenuScreensEvent event) {
		event.register(ExpansionMenus.COLLECTOR_TIER_1.get(), GUICollector.Tier1::new);
		event.register(ExpansionMenus.COLLECTOR_TIER_2.get(), GUICollector.Tier2::new);
		event.register(ExpansionMenus.COLLECTOR_TIER_3.get(), GUICollector.Tier3::new);
		event.register(ExpansionMenus.CONDENSER_MK3_INPUT.get(), GUICondenserMK3Input::new);
		event.register(ExpansionMenus.CONDENSER_MK3_OUTPUT.get(), GUICondenserMK3Output::new);
		event.register(ExpansionMenus.ARCANE_TRANSMUTATION_TABLET.get(), GUIArcaneTransmutationTablet::new);
	}

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
	}
}
