package moze_intel.projecte.gameObjs.customRecipes;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

/**
 * 26.1: {@link RecipeSerializer} is now a record and can no longer be implemented directly.
 * This builds a plain serializer instance that wraps/delegates to {@link ShapelessRecipe#SERIALIZER}.
 */
public final class PERecipeSerializer {

	private PERecipeSerializer() {
	}

	public static <RECIPE extends WrappedShapelessRecipe> RecipeSerializer<RECIPE> wrapped(Function<ShapelessRecipe, RECIPE> wrapper) {
		RecipeSerializer<ShapelessRecipe> base = ShapelessRecipe.SERIALIZER;
		MapCodec<RECIPE> codec = base.codec().xmap(wrapper, WrappedShapelessRecipe::getInternal);
		return new RecipeSerializer<>(codec, base.streamCodec().map(wrapper, WrappedShapelessRecipe::getInternal));
	}
}
