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
	private static final Map<UUID, Boolean> WEARING_STATE = new ConcurrentHashMap<>();
	private static final java.util.Set<String> GUARD_WARNED = ConcurrentHashMap.newKeySet();
	private static final int SYNC_INTERVAL = 20;

	/**
	 * Transmutation teaches: learning a mapped item starts the matching analysis.
	 */
	@SubscribeEvent
	public static void onLearnedItem(PlayerLearnedItemEvent event) {
		if (!(event.getPlayer() instanceof ServerPlayer player) || !ProjectEConfig.common.adaptionIntegrationEnabled.get()) {
			return;
		}
		try {
			learnAdaptation(player, event.getLearnedInfo());
		} catch (Throwable t) {
			guard("onLearnedItem", t);
		}
	}

	private static void learnAdaptation(ServerPlayer player, moze_intel.projecte.api.ItemInfo learned) {
		AdaptationMappings.AdaptationMapping mapping = AdaptationMappings.INSTANCE.get(learned.createStack());
		if (mapping == null || mapping.concept().isBlank()) {
			PECore.debugLog("Learned {} but it teaches no adaptation", learned);
			return;
		}
		if (!AdaptionWheelCompat.isWearingWheel(player)) {
			PECore.LOGGER.info("Learned {} would teach '{}', but {} is not wearing the Adaption Wheel",
					learned, mapping.concept(), player.getName().getString());
			return;
		}
		String concept = mapping.concept();
		registerMetadata(concept);
		boolean started = mapping.instant() ? AdaptionWheelCompat.grant(player, concept, mapping.levels()) :
				AdaptionWheelCompat.startTask(player, concept, mapping.effectiveTicks());
		PECore.LOGGER.info("Learned {} -> {} adaptation '{}' for {}",
				learned, started ? (mapping.instant() ? "granted" : "started") : "FAILED to start",
				concept, player.getName().getString());
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
		try {
			tickWearingWheel(player);
		} catch (Throwable t) {
			guard("onPlayerTick", t);
		}
	}

	private static void tickWearingWheel(ServerPlayer player) {
		if (!AdaptionWheelCompat.isWearingWheel(player)) {
			LAST_ADAPT_COUNT.remove(player.getUUID());
			reportWearingState(player, false);
			return;
		}
		reportWearingState(player, true);
		accelerateAnalysis(player);
		syncAdaptCount(player);
	}

	@SubscribeEvent
	public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) {
			return;
		}
		try {
			LAST_ADAPT_COUNT.remove(player.getUUID());
			if (AdaptionWheelCompat.isAvailable()) {
				boolean wearing = AdaptionWheelCompat.isWearingWheel(player);
				reportWearingState(player, wearing);
				if (wearing) {
					syncAdaptCount(player);
				}
			} else {
				PECore.LOGGER.info("Adaption Wheel is loaded, but its API is not usable, the integration is off");
			}
		} catch (Throwable t) {
			//Never let an optional integration keep a player from joining the world
			guard("onPlayerLogin", t);
		}
	}

	/**
	 * A broken optional integration must never take the game down with it, so failures are logged once and swallowed.
	 */
	private static void guard(String where, Throwable t) {
		if (GUARD_WARNED.add(where)) {
			PECore.LOGGER.error("The Adaption Wheel integration failed in {}, it is disabled from now on", where, t);
		}
	}

	@SubscribeEvent
	public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		LAST_ADAPT_COUNT.remove(event.getEntity().getUUID());
		WEARING_STATE.remove(event.getEntity().getUUID());
		EmcGainBonus.setPercent(event.getEntity().getUUID(), BigInteger.ZERO);
	}

	/**
	 * Logs a change of the "wearing the wheel" state, which is the thing that silently breaks every mechanic of the
	 * other mod when its Curios integration cannot find the wheel.
	 */
	private static void reportWearingState(ServerPlayer player, boolean wearing) {
		//Note: Map#put returns the PREVIOUS value, so a first observation is a null and must be handled explicitly
		Boolean previous = WEARING_STATE.put(player.getUUID(), wearing);
		if (previous != null && previous == wearing) {
			return;
		}
		if (previous == null && !wearing) {
			//Nothing was ever equipped, that is not worth a log line
			return;
		}
		PECore.LOGGER.info("{} {} the Adaption Wheel ({} adaptations, {} running analyses)", player.getName().getString(),
				wearing ? "equipped" : "removed", AdaptionWheelCompat.getAdaptCount(player),
				AdaptionWheelCompat.getActiveTasks(player).size());
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
		int percentPer = Math.max(0, ProjectEConfig.common.adaptionInsightPercent.get());
		//Note: the insight bonus is deliberately uncapped, so the multiplication is done in long and only clamped to
		//what the percentage field can still hold
		long percent = Math.min((long) current * percentPer, Integer.MAX_VALUE);
		EmcGainBonus.setPercent(player.getUUID(), BigInteger.valueOf(percent));
		if (previous != null && current > previous && rewardPer > 0) {
			int gained = (current - previous) * rewardPer;
			IKnowledgeProvider knowledge = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
			if (knowledge != null) {
				BigInteger value = EmcGainBonus.apply(BigInteger.valueOf(gained), player.getUUID());
				knowledge.setEmc(knowledge.getEmc().add(value));
				knowledge.syncEmc(player);
				//The message also shows the permanent bonus, so the player can see the insight growing
				player.sendSystemMessage(PELang.ADAPTION_REWARD.translateColored(ChatFormatting.AQUA,
						moze_intel.projecte.utils.EMCHelper.formatEmc(value), percent + "%"));
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
