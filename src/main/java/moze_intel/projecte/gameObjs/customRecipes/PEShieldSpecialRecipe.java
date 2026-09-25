package moze_intel.projecte.gameObjs.customRecipes;

import moze_intel.projecte.gameObjs.items.tools.PEShield;
import moze_intel.projecte.gameObjs.registries.PERecipeSerializers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.jetbrains.annotations.NotNull;

public class PEShieldSpecialRecipe extends CustomRecipe {

	public PEShieldSpecialRecipe() {
	}

	@Override
	public boolean matches(CraftingInput inv, @NotNull Level world) {
		ItemStack shieldStack = ItemStack.EMPTY;
		ItemStack bannerStack = ItemStack.EMPTY;
		for (ItemStack stackInSlot : inv.items()) {
			if (!stackInSlot.isEmpty()) {
				if (stackInSlot.getItem() instanceof BannerItem) {
					if (!bannerStack.isEmpty()) {
						return false;
					}
					bannerStack = stackInSlot;
				} else {
					if (!(stackInSlot.getItem() instanceof PEShield) || !shieldStack.isEmpty()) {
						return false;
					}
					BannerPatternLayers bannerpatternlayers = stackInSlot.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
					if (!bannerpatternlayers.layers().isEmpty()) {
						return false;
					}
					shieldStack = stackInSlot;
				}
			}
		}
		return !shieldStack.isEmpty() && !bannerStack.isEmpty();
	}

	@NotNull
	@Override
	public ItemStack assemble(CraftingInput inv) {
		ItemStack bannerStack = ItemStack.EMPTY;
		ItemStack shieldStack = ItemStack.EMPTY;
		for (ItemStack stackInSlot : inv.items()) {
			if (!stackInSlot.isEmpty()) {
				if (stackInSlot.getItem() instanceof BannerItem) {
					bannerStack = stackInSlot;
				} else if (stackInSlot.getItem() instanceof PEShield) {
					shieldStack = stackInSlot.copy();
				}
			}
		}
		if (shieldStack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		shieldStack.set(DataComponents.BANNER_PATTERNS, bannerStack.get(DataComponents.BANNER_PATTERNS));
		shieldStack.set(DataComponents.BASE_COLOR, ((BannerItem) bannerStack.getItem()).getColor());
		return shieldStack;
	}

	@NotNull
	@Override
	public RecipeSerializer<PEShieldSpecialRecipe> getSerializer() {
		return PERecipeSerializers.SHIELD_DECORATION.get();
	}

	@Override
	public boolean equals(Object o) {
		return o != null && o.getClass() == getClass();
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
