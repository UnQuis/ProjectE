package moze_intel.projecte.expansion.events;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.client.Keybinds;
import moze_intel.projecte.expansion.gui.GUIArcaneTransmutationTablet;
import moze_intel.projecte.expansion.gui.GUICollector;
import moze_intel.projecte.expansion.gui.GUICondenserMK3Input;
import moze_intel.projecte.expansion.gui.GUICondenserMK3Output;
import moze_intel.projecte.expansion.net.packets.to_server.PacketOpenTransmutationTablet;
import moze_intel.projecte.expansion.registries.ExpansionMenus;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class ClientEvents {
	private ClientEvents() {}

	@EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
	public static class ModEvents {
		@SubscribeEvent
		public static void registerMenuScreens(RegisterMenuScreensEvent event) {
			event.register(ExpansionMenus.COLLECTOR_TIER_1.get(), GUICollector.Tier1::new);
			event.register(ExpansionMenus.COLLECTOR_TIER_2.get(), GUICollector.Tier2::new);
			event.register(ExpansionMenus.COLLECTOR_TIER_3.get(), GUICollector.Tier3::new);
			event.register(ExpansionMenus.CONDENSER_MK3_INPUT.get(), GUICondenserMK3Input::new);
			event.register(ExpansionMenus.CONDENSER_MK3_OUTPUT.get(), GUICondenserMK3Output::new);
			event.register(ExpansionMenus.ARCANE_TRANSMUTATION_TABLET.get(), GUIArcaneTransmutationTablet::new);
		}

		@SubscribeEvent
		public static void registerKeyMappingsEvent(RegisterKeyMappingsEvent event) {
			Keybinds.register(event);
		}
	}

	@EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
	public static class GameEvents {
		@SubscribeEvent
		public static void clientTickEvent(ClientTickEvent.Pre event) {
			if (!Keybinds.REGISTERED) return;

			boolean openTransmutationTablet = Keybinds.OPEN_TRANSMUTATION_TABLET.consumeClick();
			Minecraft mc = Minecraft.getInstance();
			if (mc.screen != null || mc.player == null) return;

			if (openTransmutationTablet) PacketDistributor.sendToServer(PacketOpenTransmutationTablet.INSTANCE);
		}
	}
}
