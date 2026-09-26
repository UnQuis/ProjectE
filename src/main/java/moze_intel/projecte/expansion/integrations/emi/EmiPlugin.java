package moze_intel.projecte.expansion.integrations.emi;

/**
 * 26.3 note: EMI has not been released for 26.x, so {@code dev.emi} is not on the compile classpath and this class
 * can no longer implement {@code dev.emi.emi.api.EmiPlugin} (nor carry {@code @EmiEntrypoint}). The recipe viewer
 * selection in {@code SearchType} therefore also has to ignore the "emi" id, which it already does because
 * {@code SearchSync#register} is never called with it.
 *
 * <p>Restoring it means adding the {@code dev.emi:emi-neoforge} 26.3 dependency back to {@code build.gradle} and
 * re-implementing {@code register(EmiRegistry)}.</p>
 */@SuppressWarnings("unused")
public class EmiPlugin {
	public static final String EMI_MODID = "emi";

	//TODO 26.3: re-enable EmiEntrypoint once EMI ships for 26.3
}
