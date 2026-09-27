package moze_intel.projecte.expansion.integrations.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.util.SearchSync;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {
	//Note: distinct from ProjectE's own plugin uid (PECore.rl("main")), otherwise JEI rejects the duplicate
	private static final Identifier UID = PECore.rl("jei_plugin");

	@Nullable
	public static IJeiRuntime RUNTIME;

	@NotNull
	@Override
	public Identifier getPluginUid() {
		return UID;
	}

	@Override
	public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(new ArcaneCraftingTransferHandler(), RecipeTypes.CRAFTING);
	}

	@Override
	public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
		RUNTIME = jeiRuntime;
		SearchSync.register(new SearchSync("jei", text -> {
			if (RUNTIME != null) RUNTIME.getIngredientFilter().setFilterText(text);
		}));
	}

	@Override
	public void onRuntimeUnavailable() {
		RUNTIME = null;
	}
}
