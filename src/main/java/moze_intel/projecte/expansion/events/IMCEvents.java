package moze_intel.projecte.expansion.events;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.integrations.top.TOPIntegration;
import moze_intel.projecte.integration.IntegrationHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

@Mod.EventBusSubscriber(modid = PECore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IMCEvents {
	private IMCEvents() {}

	@SubscribeEvent
	public static void interModEnqueueEvent(InterModEnqueueEvent event) {
		ModList modList = ModList.get();
		if (modList.isLoaded(IntegrationHelper.TOP_MODID)) {
			TOPIntegration.sendIMC(event);
		}
	}
}
