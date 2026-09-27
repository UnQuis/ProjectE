package moze_intel.projecte.config;

import moze_intel.projecte.config.value.CachedBooleanValue;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * For config options that only the client cares about
 */
public class ClientConfig extends BasePEConfig {

	private final ModConfigSpec configSpec;

	public final CachedBooleanValue tagToolTips;
	public final CachedBooleanValue emcToolTips;
	public final CachedBooleanValue shiftEmcToolTips;
	public final CachedBooleanValue shiftLearnedToolTips;
	public final CachedBooleanValue statToolTips;
	public final CachedBooleanValue pedestalToolTips;
	public final CachedBooleanValue pulsatingOverlay;

	public final Expansion expansion;
	ClientConfig() {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		//We push as client in case we ever want to add an overarching comment to the client config
		builder.push("client");
		expansion = new Expansion(this, builder);
		tagToolTips = CachedBooleanValue.wrap(this, builder
				.comment("Show item tags in tooltips (useful for custom EMC registration)")
				.define("tagToolTips", false));
		emcToolTips = CachedBooleanValue.wrap(this, builder
				.comment("Show the EMC value as a tooltip on items and blocks")
				.define("emcToolTips", true));
		shiftEmcToolTips = CachedBooleanValue.wrap(this, builder
				.comment("Requires holding shift to display the EMC value as a tooltip on items and blocks. Note: this does nothing if emcToolTips is disabled.")
				.define("shiftEmcToolTips", false));
		shiftLearnedToolTips = CachedBooleanValue.wrap(this, builder
				.comment("Requires holding shift to display the learned/unlearned text as a tooltip on items and blocks. Note: this does nothing if emcToolTips is disabled.")
				.define("shiftLearnedToolTips", true));
		statToolTips = CachedBooleanValue.wrap(this, builder
				.comment("Show stats as tooltips for various ProjectE blocks")
				.define("statToolTips", true));
		pedestalToolTips = CachedBooleanValue.wrap(this, builder
				.comment("Show DM pedestal functions in item tooltips")
				.define("pedestalToolTips", true));
		pulsatingOverlay = CachedBooleanValue.wrap(this, builder
				.comment("The Philosopher's Stone overlay softly pulsates")
				.define("pulsatingOverlay", false));
		builder.pop();
		configSpec = builder.build();
	}

	@Override
	public String getFileName() {
		return "client";
	}

	@Override
	public ModConfigSpec getConfigSpec() {
		return configSpec;
	}

	@Override
	public ModConfig.Type getConfigType() {
		return ModConfig.Type.CLIENT;
	}

	public static class Expansion {

		public final CachedBooleanValue formatEMC;
		public final CachedBooleanValue fullNumberNames;
		public final CachedBooleanValue emcDisplay;
		public final ModConfigSpec.EnumValue<moze_intel.projecte.expansion.gui.EMCDisplay.EmcDisplayPosition> emcDisplayPosition;
		public final CachedBooleanValue enableLearnedTooltip;
		public final CachedBooleanValue alchemicalCollectionSound;
		public final ModConfigSpec.EnumValue<moze_intel.projecte.expansion.util.SearchType> searchType;

		private Expansion(BasePEConfig config, ModConfigSpec.Builder builder) {
			builder.push("expansion");
			formatEMC = CachedBooleanValue.wrap(config, builder.comment("If EMC should be formatted as M/B/T/etc.").define("formatEMC", true));
			fullNumberNames = CachedBooleanValue.wrap(config, builder.comment("If full number names (Million/Billion/Trillion) should be used instead of abbreviations.").define("fullNumberNames", true));
			emcDisplay = CachedBooleanValue.wrap(config, builder.comment("Displays your current emc and gained emc per second in the top left corner.").define("emcDisplay", true));
			emcDisplayPosition = builder.comment("The position of the emc display.")
					.defineEnum("emcDisplayPosition", moze_intel.projecte.expansion.gui.EMCDisplay.EmcDisplayPosition.TOP_LEFT);
			enableLearnedTooltip = CachedBooleanValue.wrap(config, builder.comment("If a tooltip should be shown on items which can be learned, denoting if the item has been learned or not. Note: ProjectE's client.shift_emc applies to this.").define("enableLearnedTooltip", true));
			alchemicalCollectionSound = CachedBooleanValue.wrap(config, builder.comment("If a sound should be played when something is collected with Alchemical Collection.").define("alchemicalCollectionSound", true));
			searchType = builder.comment("How the transmutation search should behave.")
					.defineEnum("searchType", moze_intel.projecte.expansion.util.SearchType.NORMAL);
			builder.pop();
		}
	}
}
