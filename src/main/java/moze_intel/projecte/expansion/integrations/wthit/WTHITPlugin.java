package moze_intel.projecte.expansion.integrations.wthit;

/**
 * 26.3 note: WTHIT has not been ported to 26.3, so this class can no longer implement
 * {@code mcp.mobius.waila.api.IWailaPlugin} and {@code src/main/resources/wthit_plugins.json} no longer has anything
 * to point at for the addon. The plugin body is a no-op; restoring it means:
 * <ol>
 *     <li>adding the {@code mcp.mobius.waila} dependency back to {@code build.gradle}</li>
 *     <li>implementing {@code IWailaPlugin#register(IRegistrar)} and registering
 *     {@link WTHITDataProvider#INSTANCE} for {@code Block.class} at {@code TooltipPosition.BODY}</li>
 *     <li>adding a {@code projecte:expansion_plugin} entry to {@code wthit_plugins.json}</li>
 * </ol>
 */
@SuppressWarnings("unused")
public class WTHITPlugin {
	//TODO 26.3: re-enable once wthit-api ships for 26.3
}
