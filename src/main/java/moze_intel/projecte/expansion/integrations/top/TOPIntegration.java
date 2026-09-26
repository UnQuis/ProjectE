package moze_intel.projecte.expansion.integrations.top;

import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

/**
 * 26.3 note: The One Probe has not been ported to 26.3 (the newest API on CurseForge/ModMaven is 26.2), so the addon
 * can no longer be compiled against {@code mcjty.theoneprobe}. The provider itself is kept in
 * {@link ProbeInfoProvider} but has been turned into a plain no-op class; restoring the integration means:
 * <ol>
 *     <li>adding {@code compileOnly("modmaven:theoneprobe:26.3_neo-<version>-api")} to {@code build.gradle}</li>
 *     <li>making {@link ProbeInfoProvider} implement {@code IProbeInfoProvider} again, with
 *     {@code getID()} returning an {@code Identifier}</li>
 *     <li>re-enabling the {@code getTheOneProbe} IMC sent from {@link #sendIMC(InterModEnqueueEvent)}</li>
 * </ol>
 * The IMC entry point itself is kept so the (common side) caller keeps compiling, it just sends nothing.
 */
public class TOPIntegration {
	public static final String TOP_MODID = "theoneprobe";

	/**
	 * No-op: see the class level note. Sending the IMC would require the The One Probe API on the compile classpath.
	 */
	public static void sendIMC(@SuppressWarnings("unused") InterModEnqueueEvent event) {
		//TODO 26.3: re-enable InterModComms.sendTo(TOP_MODID, "getTheOneProbe", ProbeInfoProvider::new) once
		// mcjty.theoneprobe ships a 26.3 API
	}
}
