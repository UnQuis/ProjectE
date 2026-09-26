package moze_intel.projecte.expansion.events;

/**
 * Placeholder for the addon's inter mod communication.
 * <p>
 * The only IMC message it ever sent was the TheOneProbe registration, and TheOneProbe has no Minecraft 26.3 build
 * (the same reason {@code TOPIntegration} is a stub now). The class is kept so the shape of the code does not
 * change, and so there is an obvious place to add the next integration that does ship for 26.3.
 */
public final class IMCEvents {
	private IMCEvents() {}

	//Note: TheOneProbe has no Minecraft 26.3 build, so there is nothing to send it here anymore. The
	//TOPIntegration class is kept as a stub so the code shape (and the IMC hook) stays the same for when 26.3 lands.
}
