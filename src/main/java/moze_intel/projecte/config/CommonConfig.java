package moze_intel.projecte.config;

import moze_intel.projecte.config.value.CachedBooleanValue;
import moze_intel.projecte.config.value.CachedIntValue;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * For config options that either the server or the client may care about but do not have to agree upon.
 */
public class CommonConfig extends BasePEConfig {

	private final ModConfigSpec configSpec;

	public final CachedBooleanValue debugLogging;
	public final CachedBooleanValue craftableTome;
	public final CachedBooleanValue fullKleinStars;
	public final CachedBooleanValue adaptionIntegrationEnabled;
	public final CachedIntValue adaptionEmcPerAccelerationStep;
	public final CachedIntValue adaptionMaxAccelerationSteps;
	public final CachedIntValue adaptionEmcReward;
	public final CachedIntValue adaptionInsightPercent;
	public final CachedIntValue adaptionMaxInsightPercent;

	CommonConfig() {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		debugLogging = CachedBooleanValue.wrap(this, PEConfigTranslations.COMMON_DEBUG_LOGGING.applyToBuilder(builder).define("debugLogging", false));

		PEConfigTranslations.COMMON_CRAFTING.applyToBuilder(builder).push("crafting");
		craftableTome = CachedBooleanValue.wrap(this, PEConfigTranslations.COMMON_CRAFTING_TOME.applyToBuilder(builder).define("craftableTome", false));
		fullKleinStars = CachedBooleanValue.wrap(this, PEConfigTranslations.COMMON_CRAFTING_FULL_KLEIN.applyToBuilder(builder).define("fullKleinStars", false));
		builder.pop();

		PEConfigTranslations.COMMON_ADAPTION.applyToBuilder(builder).push("adaption");
		adaptionIntegrationEnabled = CachedBooleanValue.wrap(this, PEConfigTranslations.COMMON_ADAPTION_ENABLED.applyToBuilder(builder).define("enabled", true));
		adaptionEmcPerAccelerationStep = CachedIntValue.wrap(this, PEConfigTranslations.COMMON_ADAPTION_EMC_STEP.applyToBuilder(builder).defineInRange("emcPerAccelerationStep", 256, 0, Integer.MAX_VALUE));
		adaptionMaxAccelerationSteps = CachedIntValue.wrap(this, PEConfigTranslations.COMMON_ADAPTION_MAX_STEPS.applyToBuilder(builder).defineInRange("maxAccelerationSteps", 4, 0, 64));
		adaptionEmcReward = CachedIntValue.wrap(this, PEConfigTranslations.COMMON_ADAPTION_REWARD.applyToBuilder(builder).defineInRange("emcReward", 500, 0, Integer.MAX_VALUE));
		adaptionInsightPercent = CachedIntValue.wrap(this, PEConfigTranslations.COMMON_ADAPTION_INSIGHT.applyToBuilder(builder).defineInRange("insightPercentPerAdaptation", 1, 0, 100));
		adaptionMaxInsightPercent = CachedIntValue.wrap(this, PEConfigTranslations.COMMON_ADAPTION_MAX_INSIGHT.applyToBuilder(builder).defineInRange("maxInsightPercent", 50, 0, 1000));
		builder.pop();
		configSpec = builder.build();
	}

	@Override
	public String getFileName() {
		return "common";
	}

	@Override
	public String getTranslation() {
		return "Common Config";
	}

	@Override
	public ModConfigSpec getConfigSpec() {
		return configSpec;
	}

	@Override
	public ModConfig.Type getConfigType() {
		return ModConfig.Type.COMMON;
	}
}