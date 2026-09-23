package moze_intel.projecte.emc.mappers.recipe;

import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.config.PEConfigTranslations;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;

@RecipeTypeMapper(priority = Integer.MIN_VALUE)
public class FallbackRecipeTypeMapper extends BaseRecipeTypeMapper {

	@Override
	public String getName() {
		return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_FALLBACK.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_FALLBACK.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_FALLBACK.tooltip();
	}

	@Override
	public boolean canHandle(RecipeType<?> recipeType) {
		//Pretend that we can handle
		return true;
	}

	@Override
	public boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, RecipeHolder<?> recipeHolder, RegistryAccess registryAccess, INSSFakeGroupManager fakeGroupManager) {
		Recipe<?> recipe = recipeHolder.value();
		if (recipe instanceof CraftingRecipe || recipe instanceof AbstractCookingRecipe || recipe instanceof SingleItemRecipe ||
			//Note: Ingredients are retrieved via placementInfo() which already includes the optional smithing ingredients
			recipe instanceof SmithingTransformRecipe || recipe instanceof SmithingTrimRecipe) {
			return super.handleRecipe(mapper, recipeHolder, registryAccess, fakeGroupManager);
		}
		return false;
	}
}