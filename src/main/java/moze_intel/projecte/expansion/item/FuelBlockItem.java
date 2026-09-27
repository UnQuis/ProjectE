package moze_intel.projecte.expansion.item;

import moze_intel.projecte.expansion.util.Fuel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.FuelValues;

import javax.annotation.Nullable;
import java.util.Objects;

public class FuelBlockItem extends BlockItem {
	private final Fuel level;
	public FuelBlockItem(Fuel level, Properties properties) {
		//Since 26.1 the registry id has to be on the properties before the item is created, and BlockDeferredRegister
		//hands them in already carrying it
		super(Objects.requireNonNull(Objects.requireNonNull(level).getBlock()), properties.rarity(level.getRarity()));
		this.level = level;
	}


	@Override
	public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType, FuelValues fuelValues) {
		return level.getBurnTime(recipeType, fuelValues) * 9;
	}
}
