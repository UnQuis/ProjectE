package moze_intel.projecte.emc.mappers.recipe;

import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.config.PEConfigTranslations;
import net.minecraft.world.item.crafting.RecipeType;

@RecipeTypeMapper
public class SmithingRecipeMapper extends BaseRecipeTypeMapper {

	@Override
	public String getName() {
		return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_SMITHING.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_SMITHING.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_SMITHING.tooltip();
	}

	@Override
	public boolean canHandle(RecipeType<?> recipeType) {
		return recipeType == RecipeType.SMITHING;
	}
	//Note: Ingredients are provided by placementInfo() in the base mapper, which already includes
	// the optional template/addition ingredients of smithing recipes, so no override is needed
}