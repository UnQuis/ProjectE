package moze_intel.projecte.expansion.item;

import moze_intel.projecte.expansion.util.Fuel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class ItemFuel extends Item  {
	private final Fuel level;
	public ItemFuel(Fuel level) {
		super(new Properties().rarity(level.getRarity()));
		this.level = level;
	}


	@Override
	public int getBurnTime(ItemStack stack, RecipeType<?> recipeType) {
		return level.getBurnTime();
	}
}
