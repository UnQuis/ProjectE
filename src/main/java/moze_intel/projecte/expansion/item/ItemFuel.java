package moze_intel.projecte.expansion.item;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.util.Fuel;
import moze_intel.projecte.expansion.util.FuelKeys;
import net.minecraft.world.item.Item;

import java.util.Objects;

public class ItemFuel extends Item {
	private final Fuel level;
	public ItemFuel(Properties properties, Fuel level) {
		//Since 26.3 the burn time is the minecraft:cooking_fuel component, and it can only be a reference to a loot
		//context int provider. All the fuels of the addon burn for as long as ProjectE's own aeternalis fuel does
		//(that is what the addon did as well, its fuels all resolved to the aeternalis burn time)
		super(properties.rarity(level.getRarity()).cookingFuel(FuelKeys.AETERNALIS_FUEL));
		this.level = level;
	}

	public Fuel getFuel() {
		return Objects.requireNonNull(level);
	}
}
