package moze_intel.projecte.expansion.datagen;

/**
 * The contents of the tags of the content that came from the ProjectExpansion addon.
 * <p>
 * They live in here instead of in hand written side car tag files because 1.20.4 validates every reference of
 * a generated tag against the tags it knows about, and it does not read the side car files of other namespaces, so
 * the addon tags are generated in ProjectE's own namespace and the vanilla tags reference them from there.
 */
public final class ExpansionTagData {

	private ExpansionTagData() {}

	public static final String[] BLOCK_EXPANSION_CHESTS = {
		"projecte:white_advanced_alchemical_chest", "projecte:orange_advanced_alchemical_chest",
		"projecte:magenta_advanced_alchemical_chest", "projecte:light_blue_advanced_alchemical_chest",
		"projecte:yellow_advanced_alchemical_chest", "projecte:lime_advanced_alchemical_chest",
		"projecte:pink_advanced_alchemical_chest", "projecte:gray_advanced_alchemical_chest",
		"projecte:light_gray_advanced_alchemical_chest", "projecte:cyan_advanced_alchemical_chest",
		"projecte:purple_advanced_alchemical_chest", "projecte:blue_advanced_alchemical_chest",
		"projecte:brown_advanced_alchemical_chest", "projecte:green_advanced_alchemical_chest",
		"projecte:red_advanced_alchemical_chest", "projecte:black_advanced_alchemical_chest", "projecte:condenser_mk3"
	};

	public static final String[] BLOCK_EXPANSION_BEACON_BASE_BLOCKS = {
		"projecte:magenta_matter_block", "projecte:pink_matter_block", "projecte:purple_matter_block",
		"projecte:violet_matter_block", "projecte:blue_matter_block", "projecte:cyan_matter_block",
		"projecte:green_matter_block", "projecte:lime_matter_block", "projecte:yellow_matter_block",
		"projecte:orange_matter_block", "projecte:white_matter_block", "projecte:fading_matter_block"
	};

	public static final String[] BLOCK_EXPANSION_DRAGON_IMMUNE = {
		"projecte:magenta_matter_block", "projecte:pink_matter_block", "projecte:purple_matter_block",
		"projecte:violet_matter_block", "projecte:blue_matter_block", "projecte:cyan_matter_block",
		"projecte:green_matter_block", "projecte:lime_matter_block", "projecte:yellow_matter_block",
		"projecte:orange_matter_block", "projecte:white_matter_block", "projecte:fading_matter_block",
		"projecte:transmutation_interface", "projecte:compact_sun", "projecte:condenser_mk3"
	};

	public static final String[] BLOCK_EXPANSION_GUARDED_BY_PIGLINS = {
		"projecte:white_advanced_alchemical_chest", "projecte:orange_advanced_alchemical_chest",
		"projecte:magenta_advanced_alchemical_chest", "projecte:light_blue_advanced_alchemical_chest",
		"projecte:yellow_advanced_alchemical_chest", "projecte:lime_advanced_alchemical_chest",
		"projecte:pink_advanced_alchemical_chest", "projecte:gray_advanced_alchemical_chest",
		"projecte:light_gray_advanced_alchemical_chest", "projecte:cyan_advanced_alchemical_chest",
		"projecte:purple_advanced_alchemical_chest", "projecte:blue_advanced_alchemical_chest",
		"projecte:brown_advanced_alchemical_chest", "projecte:green_advanced_alchemical_chest",
		"projecte:red_advanced_alchemical_chest", "projecte:black_advanced_alchemical_chest", "projecte:condenser_mk3"
	};

	public static final String[] BLOCK_EXPANSION_INFINIBURN_OVERWORLD = {
		"projecte:magenta_fuel_block", "projecte:pink_fuel_block", "projecte:purple_fuel_block",
		"projecte:violet_fuel_block", "projecte:blue_fuel_block", "projecte:cyan_fuel_block", "projecte:green_fuel_block",
		"projecte:lime_fuel_block", "projecte:yellow_fuel_block", "projecte:orange_fuel_block",
		"projecte:white_fuel_block"
	};

	public static final String[] BLOCK_EXPANSION_WITHER_IMMUNE = {
		"projecte:magenta_matter_block", "projecte:pink_matter_block", "projecte:purple_matter_block",
		"projecte:violet_matter_block", "projecte:blue_matter_block", "projecte:cyan_matter_block",
		"projecte:green_matter_block", "projecte:lime_matter_block", "projecte:yellow_matter_block",
		"projecte:orange_matter_block", "projecte:white_matter_block", "projecte:fading_matter_block",
		"projecte:transmutation_interface", "projecte:compact_sun", "projecte:condenser_mk3"
	};

	public static final String[] BLOCK_MINEABLE_EXPANSION_PICKAXE = {
		"projecte:transmutation_interface", "projecte:magenta_fuel_block", "projecte:pink_fuel_block",
		"projecte:purple_fuel_block", "projecte:violet_fuel_block", "projecte:blue_fuel_block", "projecte:cyan_fuel_block",
		"projecte:green_fuel_block", "projecte:lime_fuel_block", "projecte:yellow_fuel_block",
		"projecte:orange_fuel_block", "projecte:white_fuel_block", "projecte:basic_collector", "projecte:dark_collector",
		"projecte:red_collector", "projecte:magenta_collector", "projecte:pink_collector", "projecte:purple_collector",
		"projecte:violet_collector", "projecte:blue_collector", "projecte:cyan_collector", "projecte:green_collector",
		"projecte:lime_collector", "projecte:yellow_collector", "projecte:orange_collector", "projecte:white_collector",
		"projecte:fading_collector", "projecte:final_collector", "projecte:basic_power_flower",
		"projecte:dark_power_flower", "projecte:red_power_flower", "projecte:magenta_power_flower",
		"projecte:pink_power_flower", "projecte:purple_power_flower", "projecte:violet_power_flower",
		"projecte:blue_power_flower", "projecte:cyan_power_flower", "projecte:green_power_flower",
		"projecte:lime_power_flower", "projecte:yellow_power_flower", "projecte:orange_power_flower",
		"projecte:white_power_flower", "projecte:fading_power_flower", "projecte:final_power_flower",
		"projecte:basic_relay", "projecte:dark_relay", "projecte:red_relay", "projecte:magenta_relay",
		"projecte:pink_relay", "projecte:purple_relay", "projecte:violet_relay", "projecte:blue_relay",
		"projecte:cyan_relay", "projecte:green_relay", "projecte:lime_relay", "projecte:yellow_relay",
		"projecte:orange_relay", "projecte:white_relay", "projecte:fading_relay", "projecte:final_relay",
		"projecte:basic_emc_link", "projecte:dark_emc_link", "projecte:red_emc_link", "projecte:magenta_emc_link",
		"projecte:pink_emc_link", "projecte:purple_emc_link", "projecte:violet_emc_link", "projecte:blue_emc_link",
		"projecte:cyan_emc_link", "projecte:green_emc_link", "projecte:lime_emc_link", "projecte:yellow_emc_link",
		"projecte:orange_emc_link", "projecte:white_emc_link", "projecte:fading_emc_link", "projecte:final_emc_link",
		"projecte:white_advanced_alchemical_chest", "projecte:orange_advanced_alchemical_chest",
		"projecte:magenta_advanced_alchemical_chest", "projecte:light_blue_advanced_alchemical_chest",
		"projecte:yellow_advanced_alchemical_chest", "projecte:lime_advanced_alchemical_chest",
		"projecte:pink_advanced_alchemical_chest", "projecte:gray_advanced_alchemical_chest",
		"projecte:light_gray_advanced_alchemical_chest", "projecte:cyan_advanced_alchemical_chest",
		"projecte:purple_advanced_alchemical_chest", "projecte:blue_advanced_alchemical_chest",
		"projecte:brown_advanced_alchemical_chest", "projecte:green_advanced_alchemical_chest",
		"projecte:red_advanced_alchemical_chest", "projecte:black_advanced_alchemical_chest",
		"projecte:magenta_matter_block", "projecte:pink_matter_block", "projecte:purple_matter_block",
		"projecte:violet_matter_block", "projecte:blue_matter_block", "projecte:cyan_matter_block",
		"projecte:green_matter_block", "projecte:lime_matter_block", "projecte:yellow_matter_block",
		"projecte:orange_matter_block", "projecte:white_matter_block", "projecte:fading_matter_block",
		"projecte:compact_sun", "projecte:condenser_mk3"
	};

	public static final String[] BLOCK_EXPANSION_NEEDS_DARK_MATTER_TOOL = {
		"projecte:dark_collector", "projecte:dark_power_flower", "projecte:dark_relay", "projecte:dark_emc_link"
	};

	public static final String[] BLOCK_EXPANSION_NEEDS_RED_MATTER_TOOL = {
		"projecte:magenta_matter_block", "projecte:pink_matter_block", "projecte:purple_matter_block",
		"projecte:violet_matter_block", "projecte:blue_matter_block", "projecte:cyan_matter_block",
		"projecte:green_matter_block", "projecte:lime_matter_block", "projecte:yellow_matter_block",
		"projecte:orange_matter_block", "projecte:white_matter_block", "projecte:fading_matter_block",
		"projecte:compact_sun", "projecte:red_collector", "projecte:magenta_collector", "projecte:pink_collector",
		"projecte:purple_collector", "projecte:violet_collector", "projecte:blue_collector", "projecte:cyan_collector",
		"projecte:green_collector", "projecte:lime_collector", "projecte:yellow_collector", "projecte:orange_collector",
		"projecte:white_collector", "projecte:fading_collector", "projecte:final_collector", "projecte:red_power_flower",
		"projecte:magenta_power_flower", "projecte:pink_power_flower", "projecte:purple_power_flower",
		"projecte:violet_power_flower", "projecte:blue_power_flower", "projecte:cyan_power_flower",
		"projecte:green_power_flower", "projecte:lime_power_flower", "projecte:yellow_power_flower",
		"projecte:orange_power_flower", "projecte:white_power_flower", "projecte:fading_power_flower",
		"projecte:final_power_flower", "projecte:red_relay", "projecte:magenta_relay", "projecte:pink_relay",
		"projecte:purple_relay", "projecte:violet_relay", "projecte:blue_relay", "projecte:cyan_relay",
		"projecte:green_relay", "projecte:lime_relay", "projecte:yellow_relay", "projecte:orange_relay",
		"projecte:white_relay", "projecte:fading_relay", "projecte:final_relay", "projecte:red_emc_link",
		"projecte:magenta_emc_link", "projecte:pink_emc_link", "projecte:purple_emc_link", "projecte:violet_emc_link",
		"projecte:blue_emc_link", "projecte:cyan_emc_link", "projecte:green_emc_link", "projecte:lime_emc_link",
		"projecte:yellow_emc_link", "projecte:orange_emc_link", "projecte:white_emc_link", "projecte:fading_emc_link",
		"projecte:final_emc_link"
	};

	public static final String[] ITEM_EXPANSION_CHESTS = {
		"projecte:white_advanced_alchemical_chest", "projecte:orange_advanced_alchemical_chest",
		"projecte:magenta_advanced_alchemical_chest", "projecte:light_blue_advanced_alchemical_chest",
		"projecte:yellow_advanced_alchemical_chest", "projecte:lime_advanced_alchemical_chest",
		"projecte:pink_advanced_alchemical_chest", "projecte:gray_advanced_alchemical_chest",
		"projecte:light_gray_advanced_alchemical_chest", "projecte:cyan_advanced_alchemical_chest",
		"projecte:purple_advanced_alchemical_chest", "projecte:blue_advanced_alchemical_chest",
		"projecte:brown_advanced_alchemical_chest", "projecte:green_advanced_alchemical_chest",
		"projecte:red_advanced_alchemical_chest", "projecte:black_advanced_alchemical_chest", "projecte:condenser_mk3"
	};

	public static final String[] ITEM_EXPANSION_COLLECTOR_FUEL = {
		"projecte:magenta_fuel", "projecte:pink_fuel", "projecte:purple_fuel", "projecte:violet_fuel",
		"projecte:blue_fuel", "projecte:cyan_fuel", "projecte:green_fuel", "projecte:lime_fuel", "projecte:yellow_fuel",
		"projecte:orange_fuel", "projecte:white_fuel", "projecte:magenta_fuel_block", "projecte:pink_fuel_block",
		"projecte:purple_fuel_block", "projecte:violet_fuel_block", "projecte:blue_fuel_block", "projecte:cyan_fuel_block",
		"projecte:green_fuel_block", "projecte:lime_fuel_block", "projecte:yellow_fuel_block",
		"projecte:orange_fuel_block", "projecte:white_fuel_block"
	};

	public static final String[] ITEM_EXPANSION_TRANSMUTATION_TABLETS = {
		"projecte:arcane_transmutation_tablet"
	};

	public static final String[] ITEM_EXPANSION_KLEIN_STAR = {
		"projecte:magnum_star_ein", "projecte:magnum_star_zwei", "projecte:magnum_star_drei", "projecte:magnum_star_vier",
		"projecte:magnum_star_sphere", "projecte:magnum_star_omega", "projecte:colossal_star_ein",
		"projecte:colossal_star_zwei", "projecte:colossal_star_drei", "projecte:colossal_star_vier",
		"projecte:colossal_star_sphere", "projecte:colossal_star_omega", "projecte:gargantuan_star_ein",
		"projecte:gargantuan_star_zwei", "projecte:gargantuan_star_drei", "projecte:gargantuan_star_vier",
		"projecte:gargantuan_star_sphere", "projecte:gargantuan_star_omega"
	};

}
