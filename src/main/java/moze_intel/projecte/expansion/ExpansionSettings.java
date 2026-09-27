package moze_intel.projecte.expansion;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.config.Config;
import moze_intel.projecte.utils.EMCHelper;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * Holds the settings of the parts of ProjectE that the expansion replaces with its own implementation.
 * <p>
 * Every setting is either backed by a config option of the expansion section of ProjectE's configs, or is off
 * unless it is explicitly enabled, so that nothing changes for the parts of ProjectE that are not affected.
 */
public final class ExpansionSettings {

	private ExpansionSettings() {}

	/**
	 * @return If EMC values should be displayed in the abbreviated (1.23M) format instead of ProjectE's default
	 */
	public static boolean abbreviateEmc() {
		//Note: The abbreviated format relies on client only classes to know if shift is being held,
		// so it can never be used on a dedicated server
		return FMLEnvironment.getDist().isClient() && Config.client.formatEMC.getOrDefault();
	}

	/**
	 * @return If ProjectE's emc value commands should point at the expansion's {@code /px reloademc} command
	 * instead of telling the user to restart
	 */
	public static boolean reloadEmcCommandNotice() {
		return Config.server.enableReloadEMCCommand.getOrDefault();
	}

	/**
	 * @return If only enchanted books should keep their enchantments when ProjectE generates its emc values
	 */
	public static boolean persistEnchantedBooksOnly() {
		return Config.server.persistEnchantedBooksOnly.getOrDefault();
	}

	/**
	 * If the tome of knowledge should have the enchantment glint.
	 * <p>
	 * Note: This is not a config option, as it is part of how the tome looks, but it is kept in one place so that
	 * it can be turned off without having to modify the item.
	 */
	public static volatile boolean tomeGlint = true;

	/**
	 * @return If ProjectE's client side "received transmutation data" debug messages should be hidden
	 * <p>
	 * Only enabled outside of production, as those messages are pure spam while developing.
	 */
	public static boolean suppressTransmutationSyncLogs() {
		return !FMLEnvironment.isProduction();
	}

	/**
	 * Installs the expansion's EMC formatter.
	 * <p>
	 * The formatter always falls back to ProjectE's default formatting while {@link #abbreviateEmc()} returns
	 * false, so installing it does not change how EMC values are displayed otherwise.
	 */
	public static void onCommonSetup() {
		EMCHelper.setEmcFormatter(ExpansionEmcFormat.INSTANCE);
	}
}
