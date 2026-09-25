package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.config.PEConfigTranslations;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.BrewingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

@EMCMapper
public class BrewingMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server == null) {
			PECore.LOGGER.error("Failed to get server and potion data when trying to map potions");
			return;
		}

		//Add conversion for empty bottle + water to water bottle
		mapper.addConversion(1, NSSItem.createItem(PotionContents.createItemStack(Items.POTION, Potions.WATER)), EMCHelper.intMapOf(
				NSSItem.createItem(Items.GLASS_BOTTLE), 1,
				NSSFluid.createTag(FluidTags.WATER), FluidType.BUCKET_VOLUME / 3
		));

		int recipeCount = 0;
		for (RecipeHolder<?> recipeHolder : server.getRecipeManager().getRecipes()) {
			if (!(recipeHolder.value() instanceof BrewingRecipe recipe)) {
				continue;
			}
			ItemStack[] validInputs = getMatchingStacks(recipe.getInput().ingredient());
			ItemStack[] validReagents = getMatchingStacks(recipe.getReagent().ingredient());
			if (validInputs == null || validReagents == null) {
				//Skip recipes that use an ingredient which cannot be resolved to a set of item stacks.
				continue;
			}
			ItemStack output = recipe.getOutput().create();
			NormalizedSimpleStack nssOut = NSSItem.createItem(output);
			for (ItemStack validInput : validInputs) {
				NormalizedSimpleStack nssInput = NSSItem.createItem(validInput);
				for (ItemStack validReagent : validReagents) {
					//Add the conversion, 3 input + x reagent = 3 y output as strictly speaking the only one of the three parts
					// in the recipe that are required to be one in stack size is the input
					mapper.addConversion(3 * output.getCount(), nssOut, EMCHelper.intMapOf(
							nssInput, 3,
							NSSItem.createItem(validReagent), validReagent.getCount()
					));
					recipeCount++;
				}
			}
		}

		PECore.debugLog("{} Statistics:", getName());
		PECore.debugLog("Found {} Brewing Recipes", recipeCount);
	}

	@Override
	public String getName() {
		return PEConfigTranslations.MAPPING_BREWING_MAPPER.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.MAPPING_BREWING_MAPPER.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.MAPPING_BREWING_MAPPER.tooltip();
	}

	@Nullable
	private static ItemStack[] getMatchingStacks(Ingredient ingredient) {
		try {
			//26.1: Ingredient#getItems was replaced by a Stream of Item holders
			return ingredient.items().map(ItemStack::new).toArray(ItemStack[]::new);
		} catch (Exception e) {
			return null;
		}
	}
}
