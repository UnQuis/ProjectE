package moze_intel.projecte.gameObjs.items;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Short functional descriptions for the ProjectExpansion items that were merged into ProjectE.
 *
 * <p>Every entry produces one translation key, {@code pe.item.<id>.desc}, which is written to
 * {@code assets/projecte/lang/en_us.json} by
 * {@link moze_intel.projecte.expansion.lang.ExpansionLangProvider}. The wording follows the style of
 * the core ProjectE tooltips: short, second person, and stating what the item does and what it costs.</p>
 */
public final class ExpansionItemDescriptions {

	private ExpansionItemDescriptions() {
	}

	private static final Map<String, String> DESCRIPTIONS = build();

	/**
	 * @return an unmodifiable {@code description key -> description} map
	 */
	public static Map<String, String> descriptions() {
		return DESCRIPTIONS;
	}

	/**
	 * @param id registry name of the item, without the namespace
	 * @return the translation key holding the functional description of that item
	 */
	public static String key(String id) {
		return "pe.item." + id + ".desc";
	}

	private static Map<String, String> build() {
		Map<String, String> map = new LinkedHashMap<>();
		//Alchemical books - portable teleportation devices
		desc(map,
				"Teleport to a stored destination for a few EMC per block travelled.",
				"basic_alchemical_book"
				);
		desc(map,
				"Teleport to a stored destination for half the price of a basic alchemical book.",
				"advanced_alchemical_book"
				);
		desc(map,
				"Teleport to a stored destination for a fifth of the price of a basic alchemical book.",
				"master_alchemical_book"
				);
		desc(map,
				"Teleport to a stored destination completely free of EMC.",
				"arcane_alchemical_book"
				);

		//Handheld devices
		desc(map,
				"A transmutation tablet with a crafting grid built right in.",
				"arcane_transmutation_tablet"
				);
		desc(map,
				"Upgrades a collector, power flower or relay to its next tier in place, paying the difference in EMC.",
				"matter_upgrader"
				);
		desc(map,
				"Burns forever. The fuel is paid for with EMC and the item is never consumed.",
				"infinite_fuel"
				);
		desc(map,
				"Restores hunger forever. Every bite is paid for with EMC.",
				"infinite_steak"
				);
		desc(map,
				"Learn the transmutation knowledge of another player, and share your own in return.",
				"knowledge_sharing_book"
				);

		//Star line
		desc(map,
				"The final star. For now it mostly exists to show off.",
				"final_star"
				);
		desc(map,
				"A crafting ingredient for the final star.",
				"final_star_shard"
				);
		desc(map,
				"An EMC battery. Stores EMC until you need it.",
				"magnum_star_ein",
				"magnum_star_zwei",
				"magnum_star_drei",
				"magnum_star_vier",
				"magnum_star_sphere",
				"magnum_star_omega",
				"colossal_star_ein",
				"colossal_star_zwei",
				"colossal_star_drei",
				"colossal_star_vier",
				"colossal_star_sphere",
				"colossal_star_omega",
				"gargantuan_star_ein",
				"gargantuan_star_zwei",
				"gargantuan_star_drei",
				"gargantuan_star_vier",
				"gargantuan_star_sphere",
				"gargantuan_star_omega"
				);

		//Collector line (item form)
		desc(map,
				"A compressed collector. Crafts into a collector of the matching tier.",
				"basic_compressed_collector",
				"dark_compressed_collector",
				"red_compressed_collector",
				"magenta_compressed_collector",
				"pink_compressed_collector",
				"purple_compressed_collector",
				"violet_compressed_collector",
				"blue_compressed_collector",
				"cyan_compressed_collector",
				"green_compressed_collector",
				"lime_compressed_collector",
				"yellow_compressed_collector",
				"orange_compressed_collector",
				"white_compressed_collector",
				"fading_compressed_collector",
				"final_compressed_collector"
				);

		//Matter line (item form)
		desc(map,
				"Raw matter. Condense it into a matter block, or use a matter upgrader to raise it to the next tier.",
				"basic_matter",
				"magenta_matter",
				"pink_matter",
				"purple_matter",
				"violet_matter",
				"blue_matter",
				"cyan_matter",
				"green_matter",
				"lime_matter",
				"yellow_matter",
				"orange_matter",
				"white_matter",
				"fading_matter"
				);

		//Fuel line (item form)
		desc(map,
				"A fuel. Burns for a very long time and can be condensed into a fuel block.",
				"magenta_fuel",
				"pink_fuel",
				"purple_fuel",
				"violet_fuel",
				"blue_fuel",
				"cyan_fuel",
				"green_fuel",
				"lime_fuel",
				"yellow_fuel",
				"orange_fuel",
				"white_fuel"
				);

		return Collections.unmodifiableMap(map);
	}

	private static void desc(Map<String, String> map, String description, String... ids) {
		for (String id : ids) {
			map.put(key(id), description);
		}
	}
}
