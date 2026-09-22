package moze_intel.projecte.gameObjs.blacklist;

import java.util.function.Predicate;
import net.neoforged.fml.ModList;

/**
 * Runtime-safe helper for the optional Game Stages integration.
 *
 * <p>ProjectE only compiles against the GameStages API ({@code compileOnly}), so all actual
 * usages must be guarded by {@link #gameStagesLoaded} which is first checked in
 * {@link #checkModsLoaded()}.
 */
public class GameStagesHelper {

	public static final String GAMESTAGES_ID = "gamestages";

	public static boolean gameStagesLoaded;

	private GameStagesHelper() {
	}

	public static void checkModsLoaded() {
		ModList modList = ModList.get();
		//Note: The modlist is null when running tests
		Predicate<String> loadedCheck = modList == null ? modid -> false : modList::isLoaded;
		gameStagesLoaded = loadedCheck.test(GAMESTAGES_ID);
	}
}