package moze_intel.projecte.expansion.events;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.integrations.top.TOPIntegration;
import moze_intel.projecte.integration.IntegrationHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

/**
 * 26.3 note: {@code EventBusSubscriber} no longer has a {@code bus} attribute; {@link InterModEnqueueEvent}
 * is an {@code IModBusEvent} so FML puts it on the mod bus on its own.
 */
@EventBusSubscriber(modid = PECore.MODID)
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
