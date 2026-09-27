package moze_intel.projecte.expansion.config;

import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.config.value.CachedBooleanValue;
import moze_intel.projecte.config.value.CachedDoubleValue;
import moze_intel.projecte.config.value.CachedIntValue;
import moze_intel.projecte.expansion.util.Lang;
import moze_intel.projecte.utils.text.ILangEntry;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.TranslatableEnum;

/**
 * Bridge to ProjectE's own configuration for the features that came from the ProjectExpansion addon.
 * <p>
 * The addon used to register its own {@code projectexpansion-client/server.toml} files. Now that it lives inside
 * ProjectE, every option lives in the {@code expansion} section of ProjectE's client and server configs (with proper
 * translations and the standard config screen), and this class only exposes them under the names the ported addon code
 * uses. It is intentionally just an alias holder: there is no second source of truth and nothing to synchronize.
 */
public class Config {

	public static final Client client = new Client();
	public static final Server server = new Server();

	private Config() {
	}

	public static class Client {

		private Client() {
		}

		public final CachedBooleanValue formatEMC = ProjectEConfig.client.expansion.formatEMC;
		public final CachedBooleanValue fullNumberNames = ProjectEConfig.client.expansion.fullNumberNames;
		public final CachedBooleanValue emcDisplay = ProjectEConfig.client.expansion.emcDisplay;
		public final CachedBooleanValue enableLearnedTooltip = ProjectEConfig.client.expansion.enableLearnedTooltip;
		public final CachedBooleanValue alchemicalCollectionSound = ProjectEConfig.client.expansion.alchemicalCollectionSound;
		//This one is ProjectE's own option, the addon only reused it
		public final CachedBooleanValue shiftEmcToolTips = ProjectEConfig.client.shiftEmcToolTips;

		/**
		 * @return if the client config is loaded and can be read safely
		 */
		public boolean isLoaded() {
			return ProjectEConfig.client.isLoaded();
		}
	}

	public static class Server {

		private Server() {
		}

		public final CachedIntValue tickDelay = ProjectEConfig.server.expansion.tickDelay;
		public final CachedBooleanValue notifyCommandChanges = ProjectEConfig.server.expansion.notifyCommandChanges;
		public final CachedBooleanValue notifyKnowledgeBookGains = ProjectEConfig.server.expansion.notifyKnowledgeBookGains;
		public final CachedBooleanValue limitEmcLinkVendor = ProjectEConfig.server.expansion.limitEmcLinkVendor;
		public final CachedBooleanValue enableFluidEfficiency = ProjectEConfig.server.expansion.enableFluidEfficiency;
		public final CachedIntValue transmutationInterfaceItemCount = ProjectEConfig.server.expansion.transmutationInterfaceItemCount;
		public final CachedDoubleValue collectorMultiplier = ProjectEConfig.server.expansion.collectorMultiplier;
		public final CachedDoubleValue emcLinkItemLimitMultiplier = ProjectEConfig.server.expansion.emcLinkItemLimitMultiplier;
		public final CachedDoubleValue emcLinkFluidLimitMultiplier = ProjectEConfig.server.expansion.emcLinkFluidLimitMultiplier;
		public final CachedDoubleValue emcLinkEMCLimitMultiplier = ProjectEConfig.server.expansion.emcLinkEMCLimitMultiplier;
		public final CachedDoubleValue powerflowerMultiplier = ProjectEConfig.server.expansion.powerflowerMultiplier;
		public final CachedDoubleValue relayBonusMultiplier = ProjectEConfig.server.expansion.relayBonusMultiplier;
		public final CachedDoubleValue relayTransferMultiplier = ProjectEConfig.server.expansion.relayTransferMultiplier;
		public final CachedIntValue infiniteFuelCost = ProjectEConfig.server.expansion.infiniteFuelCost;
		public final CachedIntValue infiniteFuelBurnTime = ProjectEConfig.server.expansion.infiniteFuelBurnTime;
		public final CachedIntValue infiniteSteakCost = ProjectEConfig.server.expansion.infiniteSteakCost;
		public final CachedBooleanValue persistEnchantedBooksOnly = ProjectEConfig.server.expansion.persistEnchantedBooksOnly;
		public final ModConfigSpec.EnumValue<AlchemicalBookEditLevel> editOthersAlchemicalBooks = ProjectEConfig.server.expansion.editOthersAlchemicalBooks;
		public final CachedBooleanValue zeroEmcFluidsAreFree = ProjectEConfig.server.expansion.zeroEmcFluidsAreFree;
		public final CachedBooleanValue enableCollectorOptimizations = ProjectEConfig.server.expansion.enableCollectorOptimizations;
		public final CachedIntValue compactSunBonus = ProjectEConfig.server.expansion.compactSunBonus;
		public final CachedBooleanValue sunMultiplierPriceCompensation = ProjectEConfig.server.expansion.sunMultiplierPriceCompensation;
		public final CachedBooleanValue enableReloadEMCCommand = ProjectEConfig.server.expansion.enableReloadEMCCommand;
	}

	public enum AlchemicalBookEditLevel implements TranslatableEnum, ILangEntry {
		DISABLED(Lang.Configuration.EDIT_OTHERS_ALCHEMICAL_BOOKS_DISABLED),
		OP_ONLY(Lang.Configuration.EDIT_OTHERS_ALCHEMICAL_BOOKS_OP_ONLY),
		ENABLED(Lang.Configuration.EDIT_OTHERS_ALCHEMICAL_BOOKS_ENABLED);

		private final ILangEntry translation;

		AlchemicalBookEditLevel(ILangEntry translation) {
			this.translation = translation;
		}

		@Override
		public Component getTranslatedName() {
			return translation.translate();
		}

		@Override
		public String getTranslationKey() {
			return translation.getTranslationKey();
		}
	}
}
