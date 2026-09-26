package moze_intel.projecte.integration.adaptionwheel;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.event.PlayerLearnedItemEvent;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.utils.EmcGainBonus;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Optional integration with the Adaption Wheel mod. Everything in here is a no-op when that mod is not installed,
 * and all of its API is reached through {@link AdaptionWheelCompat}, so ProjectE never depends on it at build time.
 * <p>
 * The integration adds three mechanics:
 * <ol>
 *     <li><b>Alchemical analysis</b>: EMC spent while an analysis is running shortens it, so adaptations can be
 *     researched instead of suffered for.</li>
 *     <li><b>Transmutation teaches</b>: learning a mapped item in the transmutation table starts (or instantly grants)
 *     the matching adaptation, see {@link AdaptationMappings}.</li>
 *     <li><b>Alchemical insight</b>: every completed adaptation grants EMC once and permanently increases all EMC
 *     the player gains.</li>
 * </ol>
 */
@EventBusSubscriber(modid = PECore.MODID)
public class AdaptionWheelIntegration {

	private static final Map<UUID, Integer> LAST_ADAPT_COUNT = new ConcurrentHashMap<>();
	private static final java.util.Set<String> REGISTERED_CONCEPTS = ConcurrentHashMap.newKeySet();
	private static final int SYNC_INTERVAL = 20;

	/**
	 * Transmutation teaches: learning a mapped item starts the matching analysis.
	 */
	@SubscribeEvent
	public static void onLearnedItem(PlayerLearnedItemEvent event) {
		if (event.getPlayer() instanceof ServerPlayer player && ProjectEConfig.common.adaptionIntegrationEnabled.get()) {
			AdaptationMappings.AdaptationMapping mapping = AdaptationMappings.INSTANCE.get(event.getLearnedInfo().createStack());
			if (mapping != null && !mapping.concept().isBlank()) {
				String concept = mapping.concept();
				registerMetadata(concept);
				boolean started = mapping.instant() ? AdaptionWheelCompat.grant(player, concept, mapping.levels()) :
						AdaptionWheelCompat.startTask(player, concept, mapping.effectiveTicks());
				if (started) {
					PECore.debugLog("Started adaptation {} for {} after learning an item", concept, player.getName().getString());
				}
			}
		}
	}

	/**
	 * Alchemical analysis + alchemical insight, polled once a second for players wearing the wheel.
	 */
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) {
			return;
		}
		if (!ProjectEConfig.common.adaptionIntegrationEnabled.get() || player.tickCount % SYNC_INTERVAL != 0) {
			return;
		}
		if (!AdaptionWheelCompat.isWearingWheel(player)) {
			LAST_ADAPT_COUNT.remove(player.getUUID());
			return;
		}
		accelerateAnalysis(player);
		syncAdaptCount(player);
	}

	@SubscribeEvent
	public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			LAST_ADAPT_COUNT.remove(player.getUUID());
			syncAdaptCount(player);
		}
	}

	@SubscribeEvent
	public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		LAST_ADAPT_COUNT.remove(event.getEntity().getUUID());
		EmcGainBonus.setPercent(event.getEntity().getUUID(), BigInteger.ZERO);
	}

	/**
	 * Spends EMC to shorten running analyses. Each step is the acceleration step of that mod, we simply buy as many
	 * of them as the configured EMC budget allows.
	 */
	private static void accelerateAnalysis(ServerPlayer player) {
		int cost = ProjectEConfig.common.adaptionEmcPerAccelerationStep.get();
		if (cost <= 0) {
			return;
		}
		List<String> tasks = AdaptionWheelCompat.getActiveTasks(player);
		if (tasks.isEmpty()) {
			return;
		}
		IKnowledgeProvider knowledge = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
		if (knowledge == null) {
			return;
		}
		BigInteger emc = knowledge.getEmc();
		BigInteger stepCost = BigInteger.valueOf(cost);
		int affordable = emc.divide(stepCost).min(BigInteger.valueOf(ProjectEConfig.common.adaptionMaxAccelerationSteps.get())).intValue();
		if (affordable <= 0) {
			return;
		}
		int used = 0;
		for (String concept : tasks) {
			if (used >= affordable) {
				break;
			}
			if (AdaptionWheelCompat.startOrAccelerate(player, concept, 100)) {
				used++;
			}
		}
		if (used > 0) {
			knowledge.setEmc(emc.subtract(stepCost.multiply(BigInteger.valueOf(used))));
			knowledge.syncEmc(player);
		}
	}

	/**
	 * Grants the completion reward and keeps the permanent gain bonus in sync with the adaptation count.
	 */
	private static void syncAdaptCount(ServerPlayer player) {
		int current = AdaptionWheelCompat.getAdaptCount(player);
		Integer previous = LAST_ADAPT_COUNT.put(player.getUUID(), current);
		int rewardPer = ProjectEConfig.common.adaptionEmcReward.get();
		int percentPer = ProjectEConfig.common.adaptionInsightPercent.get();
		int maxPercent = ProjectEConfig.common.adaptionMaxInsightPercent.get();
		EmcGainBonus.setPercent(player.getUUID(), BigInteger.valueOf(Math.min(maxPercent, current * Math.max(0, percentPer))));
		if (previous != null && current > previous && rewardPer > 0) {
			int gained = (current - previous) * rewardPer;
			IKnowledgeProvider knowledge = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
			if (knowledge != null) {
				BigInteger value = EmcGainBonus.apply(BigInteger.valueOf(gained), player.getUUID());
				knowledge.setEmc(knowledge.getEmc().add(value));
				knowledge.syncEmc(player);
				player.sendSystemMessage(PELang.ADAPTION_REWARD.translateColored(ChatFormatting.AQUA, moze_intel.projecte.utils.EMCHelper.formatEmc(value)));
			}
		}
	}

	/**
	 * Registers display metadata for the concept so it shows up nicely in the wheel's GUI and commands. Failures are
	 * ignored, that mod falls back to prefix based domains for unknown concepts anyway.
	 */
	private static void registerMetadata(String concept) {
		if (REGISTERED_CONCEPTS.add(concept)) {
			Domain domain = Domain.of(concept);
			AdaptionWheelCompat.registerDefinition(concept, domain.name(), domain.leveled, 8);
		}
	}

	private enum Domain {
		DAMAGE("Type_", true),
		ENVIRONMENT("Env_", false),
		MOVEMENT("Move_", false),
		EFFECT("Debuff_", false),
		MINING("Mine_", true),
		COMBAT("Combat_", true),
		ENTITY("Contact_", true),
		PERCEPTION("Percep_", true),
		EXISTENCE("Existence_", false),
		SPECIAL("Mutation_", false);

		final String prefix;
		final boolean leveled;

		Domain(String prefix, boolean leveled) {
			this.prefix = prefix;
			this.leveled = leveled;
		}

		static Domain of(String concept) {
			for (Domain domain : values()) {
				if (concept.startsWith(domain.prefix)) {
					return domain;
				}
			}
			//Drop_NPC_/Offense_NPC_ also start with a D/O, check them explicitly
			if (concept.startsWith("Drop_") || concept.startsWith("Offense_")) {
				return ENTITY;
			}
			return SPECIAL;
		}
	}
}
