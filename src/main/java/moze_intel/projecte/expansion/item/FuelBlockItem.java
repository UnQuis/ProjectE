package moze_intel.projecte.expansion.item;

import moze_intel.projecte.expansion.util.Fuel;
import moze_intel.projecte.expansion.util.FuelKeys;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.Objects;

public class FuelBlockItem extends BlockItem {
	private final Fuel level;
	public FuelBlockItem(Fuel level) {
		this(level, new Item.Properties().rarity(level.getRarity()));
	}

	//26.3 hands the (id carrying) Item.Properties in from the BlockDeferredRegister
	public FuelBlockItem(Fuel level, Properties properties) {
		//The block holds 9 of the single items, which is exactly what ProjectE's own fuel blocks burn for
		super(Objects.requireNonNull(level.getBlock()), properties.cookingFuel(FuelKeys.AETERNALIS_FUEL_BLOCK));
		this.level = level;
	}

	public Fuel getFuel() {
		return level;
	}
}
