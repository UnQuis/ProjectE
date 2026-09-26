package moze_intel.projecte.expansion.util;

import moze_intel.projecte.PECore;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;

/**
 * The loot context int providers the fuels of the addon use as their {@code minecraft:cooking_fuel} burn time.
 * <p>
 * Since 26.3 the burn time of a fuel is a reference to a loot context int provider instead of a number the item can
 * return, so it has to live in a data file. ProjectE already ships providers for its own fuels, and since all the
 * fuels of the addon burn for exactly as long as ProjectE's aeternalis fuel (and their blocks for as long as an
 * aeternalis fuel block) those are reused instead of writing a near identical file per color.
 */
public final class FuelKeys {
	/**
	 * Burn time of a single fuel item, {@code data/projecte/context_int_provider/cooking/time_aeternalis_fuel.json}
	 */
	public static final ResourceKey<ContextIntProvider> AETERNALIS_FUEL = cookingKey("time_aeternalis_fuel");

	/**
	 * Burn time of a fuel block, which holds nine of the single items,
	 * {@code data/projecte/context_int_provider/cooking/time_aeternalis_fuel_block.json}
	 */
	public static final ResourceKey<ContextIntProvider> AETERNALIS_FUEL_BLOCK = cookingKey("time_aeternalis_fuel_block");

	private FuelKeys() {
	}

	private static ResourceKey<ContextIntProvider> cookingKey(String name) {
		return ResourceKey.create(Registries.CONTEXT_INT_PROVIDER, PECore.rl("cooking/" + name));
	}
}
