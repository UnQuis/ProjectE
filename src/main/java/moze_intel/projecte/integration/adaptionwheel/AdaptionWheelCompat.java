package moze_intel.projecte.integration.adaptionwheel;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import moze_intel.projecte.PECore;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

/**
 * Soft, reflection based bridge to the {@code adaptionwheel} mod (Adaption Wheel).
 * <p>
 * The integration is purely optional: ProjectE never references the other mod's classes directly, so it compiles
 * and runs without it. Everything is resolved reflectively on first use and if anything at all goes wrong the
 * integration disables itself instead of breaking either mod.
 *
 * @apiNote Only the small set of entry points documented by that mod's {@code mod-api.md} is used.
 */
public final class AdaptionWheelCompat {

	public static final String MOD_ID = "adaptionwheel";

	private static final String API_CLASS = "ru.adaptionwheel.api.AdaptionWheelAPI";
	private static final String EVENTS_CLASS = "ru.adaptionwheel.server.AdaptionEvents";

	private static Boolean modLoaded;
	private static boolean resolved;
	private static boolean available;
	private static boolean warned;

	private static Method isWearingWheel;
	private static Method getLevel;
	private static Method isAdapted;
	private static Method getAdaptCount;
	private static Method getActiveTasks;
	private static Method dataOf;
	private static Method startTask;
	private static Method startOrAccelerate;
	private static Method grant;
	private static Method registerDefinition;
	private static Class<?> definitionClass;
	private static Method definitionOneTime;
	private static Method definitionLeveled;

	private AdaptionWheelCompat() {
	}

	/**
	 * @return true if the Adaption Wheel mod is present and its API could be resolved
	 */
	public static boolean isAvailable() {
		if (!isModLoaded()) {
			return false;
		}
		if (!resolved) {
			resolve();
		}
		return available;
	}

	private static boolean isModLoaded() {
		if (modLoaded == null) {
			modLoaded = ModList.get() != null && ModList.get().isLoaded(MOD_ID);
			if (Boolean.TRUE.equals(modLoaded)) {
				PECore.LOGGER.info("Adaption Wheel detected, enabling the alchemical adaptation integration");
			}
		}
		return modLoaded;
	}

	private static synchronized void resolve() {
		if (resolved) {
			return;
		}
		resolved = true;
		try {
			Class<?> api = Class.forName(API_CLASS);
			Class<?> events = Class.forName(EVENTS_CLASS);
			isWearingWheel = api.getMethod("isWearingWheel", Player.class);
			getLevel = api.getMethod("getLevel", Player.class, String.class);
			isAdapted = api.getMethod("isAdapted", Player.class, String.class);
			getAdaptCount = api.getMethod("getAdaptCount", Player.class);
			getActiveTasks = api.getMethod("getActiveTasks", Player.class);
			registerDefinition = api.getMethod("registerDefinition", Class.forName("ru.adaptionwheel.adapt.AdaptationDefinition"));
			definitionClass = Class.forName("ru.adaptionwheel.adapt.AdaptationDefinition");
			definitionOneTime = definitionClass.getMethod("oneTime", String.class, Class.forName("ru.adaptionwheel.adapt.AdaptationDomain"));
			definitionLeveled = definitionClass.getMethod("leveled", String.class, Class.forName("ru.adaptionwheel.adapt.AdaptationDomain"));
			dataOf = events.getMethod("dataOf", ServerPlayer.class);
			startTask = events.getMethod("startTask", ServerPlayer.class, dataOf.getReturnType(), String.class, int.class);
			startOrAccelerate = events.getMethod("startOrAccelerate", ServerPlayer.class, dataOf.getReturnType(), String.class, int.class, boolean.class);
			grant = events.getMethod("debugGrant", ServerPlayer.class, String.class, int.class);
			available = true;
		} catch (ReflectiveOperationException | RuntimeException e) {
			available = false;
			warn("Could not resolve the Adaption Wheel API (" + e + "), the integration stays disabled");
		}
	}

	public static boolean isWearingWheel(Player player) {
		if (!isAvailable()) {
			return false;
		}
		try {
			return Boolean.TRUE.equals(isWearingWheel.invoke(null, player));
		} catch (ReflectiveOperationException | RuntimeException e) {
			fail("isWearingWheel", e);
			return false;
		}
	}

	public static int getLevel(Player player, String concept) {
		if (!isAvailable()) {
			return 0;
		}
		try {
			return (Integer) getLevel.invoke(null, player, concept);
		} catch (ReflectiveOperationException | RuntimeException e) {
			fail("getLevel", e);
			return 0;
		}
	}

	public static boolean isAdapted(Player player, String concept) {
		if (!isAvailable()) {
			return false;
		}
		try {
			return Boolean.TRUE.equals(isAdapted.invoke(null, player, concept));
		} catch (ReflectiveOperationException | RuntimeException e) {
			fail("isAdapted", e);
			return false;
		}
	}

	public static int getAdaptCount(Player player) {
		if (!isAvailable()) {
			return 0;
		}
		try {
			return (Integer) getAdaptCount.invoke(null, player);
		} catch (ReflectiveOperationException | RuntimeException e) {
			fail("getAdaptCount", e);
			return 0;
		}
	}

	/**
	 * @return the concepts that are currently being analyzed, or an empty list if the wheel is not worn
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getActiveTasks(Player player) {
		if (!isAvailable() || !isWearingWheel(player)) {
			return Collections.emptyList();
		}
		try {
			List<String> tasks = (List<String>) getActiveTasks.invoke(null, player);
			return tasks == null ? Collections.emptyList() : tasks;
		} catch (ReflectiveOperationException | RuntimeException e) {
			fail("getActiveTasks", e);
			return Collections.emptyList();
		}
	}

	/**
	 * Starts the analysis of the given concept, so it completes after the given time.
	 */
	public static boolean startTask(ServerPlayer player, String concept, int ticks) {
		if (!isAvailable() || !isWearingWheel(player)) {
			return false;
		}
		try {
			startTask.invoke(null, player, dataOf.invoke(null, player), concept, Math.max(1, ticks));
			return true;
		} catch (ReflectiveOperationException | RuntimeException e) {
			fail("startTask", e);
			return false;
		}
	}

	/**
	 * Starts the analysis, or shortens an already running one by one acceleration step of that mod.
	 */
	public static boolean startOrAccelerate(ServerPlayer player, String concept, int ticks) {
		if (!isAvailable() || !isWearingWheel(player)) {
			return false;
		}
		try {
			startOrAccelerate.invoke(null, player, dataOf.invoke(null, player), concept, Math.max(1, ticks), true);
			return true;
		} catch (ReflectiveOperationException | RuntimeException e) {
			fail("startOrAccelerate", e);
			return false;
		}
	}

	/**
	 * Instantly grants the concept at the given level.
	 */
	public static boolean grant(ServerPlayer player, String concept, int level) {
		if (!isAvailable() || !isWearingWheel(player)) {
			return false;
		}
		try {
			grant.invoke(null, player, concept, level);
			return true;
		} catch (ReflectiveOperationException | RuntimeException e) {
			fail("grant", e);
			return false;
		}
	}

	/**
	 * Registers metadata for a concept so it shows up nicely in that mod's GUI and commands.
	 *
	 * @param domain One of that mod's {@code AdaptationDomain} enum values, by name. Unknown domains are ignored.
	 */
	public static boolean registerDefinition(String concept, String domain, boolean leveled, int maxLevel) {
		if (!isAvailable()) {
			return false;
		}
		try {
			Class<?> domainClass = Class.forName("ru.adaptionwheel.adapt.AdaptationDomain");
			Object domainValue = null;
			for (Object value : domainClass.getEnumConstants()) {
				if (((Enum<?>) value).name().equals(domain)) {
					domainValue = value;
					break;
				}
			}
			if (domainValue == null) {
				return false;
			}
			Object definition = leveled ? definitionLeveled.invoke(null, concept, domainValue) :
					definitionOneTime.invoke(null, concept, domainValue);
			registerDefinition.invoke(null, definition);
			return true;
		} catch (ReflectiveOperationException | RuntimeException e) {
			fail("registerDefinition", e);
			return false;
		}
	}

	/**
	 * Disables the integration after a runtime failure, so a broken or changed API can never break the game.
	 */
	private static void fail(String method, Throwable e) {
		available = false;
		warn("Adaption Wheel call '" + method + "' failed (" + e + "), disabling the integration");
	}

	private static void warn(String message) {
		if (!warned) {
			warned = true;
			PECore.LOGGER.warn(message);
		}
	}
}
