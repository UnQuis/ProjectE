package moze_intel.projecte.gameObjs.items;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Functional descriptions for the items of this mod, shown in the item tooltip.
 * <p>
 * The descriptions are stored as translation keys so they can be translated. The english text is added to the generated
 * language files by the datagen, the other languages are maintained as language files of their own.
 */
public final class ItemDescriptions {

	/**
	 * @param key     Translation key of the description
	 * @param english The english text of the description, used by the datagen to generate the english language files
	 */
	public record Description(String key, String english) {}

	private static final Map<String, List<Description>> DESCRIPTIONS = Map.ofEntries(
			Map.entry("dm_pick", List.of(new Description("pe.item.dm_pick.desc", "Vein-mines ores; charge boosts speed and vein size, with three break patterns on G."))),
			Map.entry("dm_axe", List.of(new Description("pe.item.dm_axe.desc", "Charge boosts chopping speed and widens log stripping, scraping, waxing, and clearing."))),
			Map.entry("dm_shovel", List.of(new Description("pe.item.dm_shovel.desc", "Charge boosts digging speed, flattening, vein mining, and area excavation."))),
			Map.entry("dm_sword", List.of(new Description("pe.item.dm_sword.desc", "Charge boosts damage, armor penetration, sweeping, and the C attack on nearby hostiles."))),
			Map.entry("dm_hoe", List.of(new Description("pe.item.dm_hoe.desc", "Charge boosts tilling speed and tills a widening horizontal area."))),
			Map.entry("dm_shears", List.of(new Description("pe.item.dm_shears.desc", "Charge boosts shearing speed and area; mass-clears leaves and shears entities."))),
			Map.entry("dm_hammer", List.of(new Description("pe.item.dm_hammer.desc", "Charge boosts mining speed, area excavation, and armor-piercing attack damage."))),
			Map.entry("dm_helmet", List.of(new Description("pe.item.dm_helmet.desc", "Unbreakable helmet; a full set cuts damage by 80% and absorbs drowning damage."))),
			Map.entry("dm_chestplate", List.of(new Description("pe.item.dm_chestplate.desc", "Unbreakable chestplate; a full set cuts damage by 80% and absorbs explosion damage."))),
			Map.entry("dm_leggings", List.of(new Description("pe.item.dm_leggings.desc", "Unbreakable leggings; a full set cuts damage by 80% and absorbs explosion damage."))),
			Map.entry("dm_boots", List.of(new Description("pe.item.dm_boots.desc", "Unbreakable boots; a full set cuts damage by 80% and absorbs fall damage."))),
			Map.entry("rm_pick", List.of(new Description("pe.item.rm_pick.desc", "Vein-mines ores; charge boosts speed and vein size, with three break patterns on G."))),
			Map.entry("rm_axe", List.of(new Description("pe.item.rm_axe.desc", "Charge boosts chopping speed and widens log stripping, scraping, waxing, and clearing."))),
			Map.entry("rm_shovel", List.of(new Description("pe.item.rm_shovel.desc", "Charge boosts digging speed, flattening, vein mining, and area excavation."))),
			Map.entry("rm_sword", List.of(new Description("pe.item.rm_sword.desc", "Charge boosts damage and C area attacks; G targets hostiles only or all living entities."))),
			Map.entry("rm_hoe", List.of(new Description("pe.item.rm_hoe.desc", "Charge boosts tilling speed and tills a widening horizontal area."))),
			Map.entry("rm_shears", List.of(new Description("pe.item.rm_shears.desc", "Charge boosts shearing speed and area; mass-clears leaves and shears entities."))),
			Map.entry("rm_hammer", List.of(new Description("pe.item.rm_hammer.desc", "Charge boosts mining speed, area excavation, and armor-piercing attack damage."))),
			Map.entry("rm_katar", List.of(new Description("pe.item.rm_katar.desc", "Charge boosts its C area attacks; it also shears mobs, tills, and clears wood and leaves."))),
			Map.entry("rm_morning_star", List.of(new Description("pe.item.rm_morning_star.desc", "Charge boosts this multi-tool's speed, area mining, attacks, and three break patterns."))),
			Map.entry("rm_helmet", List.of(new Description("pe.item.rm_helmet.desc", "Unbreakable helmet; a full set cuts damage by 90% and absorbs drowning damage."))),
			Map.entry("rm_chestplate", List.of(new Description("pe.item.rm_chestplate.desc", "Unbreakable chestplate; a full set cuts damage by 90% and absorbs explosion damage."))),
			Map.entry("rm_leggings", List.of(new Description("pe.item.rm_leggings.desc", "Unbreakable leggings; a full set cuts damage by 90% and absorbs explosion damage."))),
			Map.entry("rm_boots", List.of(new Description("pe.item.rm_boots.desc", "Unbreakable boots; a full set cuts damage by 90% and absorbs fall damage."))),
			Map.entry("gem_helmet", List.of(new Description("pe.item.gem_helmet.desc", "Shift+X night vision; healing, water walking, R lightning, and 90% full-set reduction."))),
			Map.entry("gem_chestplate", List.of(new Description("pe.item.gem_chestplate.desc", "C explosion; fire immunity, lava walking, auto-feeding, and 90% full-set reduction."))),
			Map.entry("gem_leggings", List.of(new Description("pe.item.gem_leggings.desc", "Hold Shift to dive and slam, repelling and damaging mobs; full set cuts damage by 90%."))),
			Map.entry("gem_boots", List.of(new Description("pe.item.gem_boots.desc", "+100% speed, fall damage immunity, X-toggled step assist, and 90% full-set reduction."))),
			Map.entry("dark_matter_trident", List.of(new Description("pe.item.dark_matter_trident.desc", "G cycles throw, lightning, Riptide, and shockwave; charge boosts each effect."))),
			Map.entry("red_matter_trident", List.of(new Description("pe.item.red_matter_trident.desc", "G cycles throw, lightning, Riptide, and shockwave; charge boosts each effect."))),
			Map.entry("dark_matter_shield", List.of(new Description("pe.item.dark_matter_shield.desc", "Blocks attacks; it is unbreakable, fireproof, and can be decorated with a banner."))),
			Map.entry("red_matter_shield", List.of(new Description("pe.item.red_matter_shield.desc", "Blocks attacks; it is unbreakable, fireproof, and can be decorated with a banner."))),
			Map.entry("klein_star_ein", List.of(new Description("pe.item.klein_star_ein.desc", "Holds up to 50,000 EMC to power your machines."))),
			Map.entry("klein_star_zwei", List.of(new Description("pe.item.klein_star_zwei.desc", "Holds up to 200,000 EMC to power your machines."))),
			Map.entry("klein_star_drei", List.of(new Description("pe.item.klein_star_drei.desc", "Holds up to 800,000 EMC to power your machines."))),
			Map.entry("klein_star_vier", List.of(new Description("pe.item.klein_star_vier.desc", "Holds up to 3,200,000 EMC to power your machines."))),
			Map.entry("klein_star_sphere", List.of(new Description("pe.item.klein_star_sphere.desc", "Holds up to 12,800,000 EMC to power your machines."))),
			Map.entry("klein_star_omega", List.of(new Description("pe.item.klein_star_omega.desc", "Holds up to 51,200,000 EMC to power your machines."))),
			Map.entry("arcana_ring", List.of(new Description("pe.item.arcana_ring.desc", "Grants flight; while active, freezes water, ignites blocks, grows/harvests crops, and repels mobs. Modes: Zero, Ignition, Harvest, SWRG"))),
			Map.entry("black_hole_band", List.of(new Description("pe.item.black_hole_band.desc", "Active: vacuums nearby drops into your inventory; right-click fluid to collect it. In a pedestal: vacuums drops into adjacent storage."))),
			Map.entry("archangel_smite", List.of(new Description("pe.item.archangel_smite.desc", "Fires homing arrows; left-click fires a volley. In a pedestal: shoots arrows at nearby mobs."))),
			Map.entry("harvest_goddess_band", List.of(new Description("pe.item.harvest_goddess_band.desc", "Active: grows and harvests crops; right-click plants seeds or applies bone meal. In a pedestal: grows and harvests crops."))),
			Map.entry("ignition_ring", List.of(new Description("pe.item.ignition_ring.desc", "Active mode creates fires; off extinguishes them; it also lights blocks and shoots fire. In a pedestal: burns nearby mobs."))),
			Map.entry("zero_ring", List.of(new Description("pe.item.zero_ring.desc", "On use or while active, turns water to ice and leaves snow. In a pedestal: freezes surroundings and extinguishes burning entities."))),
			Map.entry("swiftwolf_rending_gale", List.of(new Description("pe.item.swiftwolf_rending_gale.desc", "Grants flight with EMC, repels mobs, and throws SWRG shots. Modes: Off, Flight, Shield, Shielded Flight. In a pedestal: lightning-strikes mobs."))),
			Map.entry("void_ring", List.of(new Description("pe.item.void_ring.desc", "Condenses items, teleports you, and vacuums drops. Modes: Iron, Gold, Diamond, Dark Matter, Red Matter. In a pedestal: vacuums into adjacent storage."))),
			Map.entry("body_stone", List.of(new Description("pe.item.body_stone.desc", "While active, feeds its wearer using EMC. In a pedestal: restores nearby players' hunger."))),
			Map.entry("soul_stone", List.of(new Description("pe.item.soul_stone.desc", "While active, heals its wearer using EMC. In a pedestal: heals nearby players."))),
			Map.entry("mind_stone", List.of(new Description("pe.item.mind_stone.desc", "Active: absorbs the wearer's XP; right-click while off returns it. In a pedestal: sucks in nearby XP orbs."))),
			Map.entry("life_stone", List.of(new Description("pe.item.life_stone.desc", "While active, feeds and heals its wearer using EMC. In a pedestal: restores nearby players' hunger and health."))),
			Map.entry("evertide_amulet", List.of(new Description("pe.item.evertide_amulet.desc", "Infinite water; fills containers, shoots water, quenches fire, and lets you walk on water. In a pedestal: starts rain/snow."))),
			Map.entry("volcanite_amulet", List.of(new Description("pe.item.volcanite_amulet.desc", "Places lava or fills containers for 32 EMC; also shoots lava and lets you walk on lava. In a pedestal: stops rain/snow."))),
			Map.entry("mercurial_eye", List.of(new Description("pe.item.mercurial_eye.desc", "Places and reshapes blocks with Klein Star EMC. Modes: Creation, Extension, Extension-Classic, Transmutation, Transmutation-Classic, Pillar."))),
			Map.entry("watch_of_flowing_time", List.of(new Description("pe.item.watch_of_flowing_time.desc", "Speeds blocks, slows mobs, and fast-forwards/rewinds time. Modes: Off, Fast Forward, Rewind. In a pedestal: speeds blocks and slows mobs."))),
			Map.entry("philosophers_stone", List.of(new Description("pe.item.philosophers_stone.desc", "Transmutes targeted blocks, opens a crafting grid, and fires a mob randomizer. Modes: Cube, Panel, Line."))),
			Map.entry("repair_talisman", List.of(new Description("pe.item.repair_talisman.desc", "Periodically repairs its carrier's items; works inside alchemical bags/chests. In a pedestal: repairs nearby players' items."))),
			Map.entry("gem_of_eternal_density", List.of(new Description("pe.item.gem_of_eternal_density.desc", "While active, condenses lower-value items into a chosen material. Modes: Iron, Gold, Diamond, Dark Matter, Red Matter."))),
			Map.entry("alchemical_bag", List.of(new Description("pe.item.alchemical_bag.desc", "Opens a separate 104-slot inventory; each color has its own storage."))),
			Map.entry("alchemical_barrel", List.of(new Description("pe.item.alchemical_barrel.desc", "Stores 104 item stacks and up to 1,000 EMC."))),
			Map.entry("alchemical_chest", List.of(new Description("pe.item.alchemical_chest.desc", "Stores 104 item stacks and up to 1,000 EMC."))),
			Map.entry("collector_mk1", List.of(new Description("pe.item.collector_mk1.desc", "Generates up to 4 EMC/t from light, stores 10,000 EMC, and charges compatible items."))),
			Map.entry("collector_mk2", List.of(new Description("pe.item.collector_mk2.desc", "Generates up to 12 EMC/t from light, stores 30,000 EMC, and charges compatible items."))),
			Map.entry("collector_mk3", List.of(new Description("pe.item.collector_mk3.desc", "Generates up to 40 EMC/t from light, stores 60,000 EMC, and charges compatible items."))),
			Map.entry("condenser_mk1", List.of(new Description("pe.item.condenser_mk1.desc", "Converts items to EMC and uses it to recreate a chosen target item."))),
			Map.entry("condenser_mk2", List.of(new Description("pe.item.condenser_mk2.desc", "Faster condenser with separate I/O that creates multiple copies of its target at once."))),
			Map.entry("relay_mk1", List.of(new Description("pe.item.relay_mk1.desc", "Sends up to 64 EMC/t to adjacent devices and stores 100,000 EMC."))),
			Map.entry("relay_mk2", List.of(new Description("pe.item.relay_mk2.desc", "Sends up to 192 EMC/t to adjacent devices and stores 1,000,000 EMC."))),
			Map.entry("relay_mk3", List.of(new Description("pe.item.relay_mk3.desc", "Sends up to 640 EMC/t to adjacent devices and stores 10,000,000 EMC."))),
			Map.entry("dm_furnace", List.of(new Description("pe.item.dm_furnace.desc", "Smelts 20x faster than a vanilla furnace and has a 50% chance to double ore output."))),
			Map.entry("rm_furnace", List.of(new Description("pe.item.rm_furnace.desc", "Smelts over 66x faster than a vanilla furnace and always doubles ore output."))),
			Map.entry("dm_pedestal", List.of(new Description("pe.item.dm_pedestal.desc", "Activates a compatible item's ability; toggle it with an empty hand or redstone."))),
			Map.entry("transmutation_table", List.of(new Description("pe.item.transmutation_table.desc", "Lets players store EMC, transmute items, and learn or unlearn transmutation knowledge."))),
			Map.entry("transmutation_stone", List.of(new Description("pe.item.transmutation_stone.desc", "Lets players store EMC, transmute items, and learn or unlearn transmutation knowledge."))),
			Map.entry("interdiction_lantern", List.of(new Description("pe.item.interdiction_lantern.desc", "Pushes hostile mobs and projectiles away within 8 blocks; the blacklist is configurable."))),
			Map.entry("interdiction_torch", List.of(new Description("pe.item.interdiction_torch.desc", "Pushes hostile mobs and projectiles away within 8 blocks; the blacklist is configurable."))),
			Map.entry("divining_rod_1", List.of(new Description("pe.item.divining_rod_1.desc", "Low-tier rod: scans 3x3x3 and reports the average EMC; range is switchable."))),
			Map.entry("divining_rod_2", List.of(new Description("pe.item.divining_rod_2.desc", "Standard rod: scans 16x3x3 and reports the average and second highest EMC; range is switchable."))),
			Map.entry("divining_rod_3", List.of(new Description("pe.item.divining_rod_3.desc", "High-tier rod: scans 64x3x3 and reports the average and top three EMC values; range is switchable."))),
			Map.entry("destruction_catalyst", List.of(new Description("pe.item.destruction_catalyst.desc", "Excavates an area of blocks using EMC; charge increases its range."))),
			Map.entry("hyperkinetic_lens", List.of(new Description("pe.item.hyperkinetic_lens.desc", "Fires an EMC-powered explosive projectile; higher charge increases its blast radius."))),
			Map.entry("catalytic_lens", List.of(new Description("pe.item.catalytic_lens.desc", "Excavates a much wider area and fires charged explosive projectiles."))),
			Map.entry("nova_cataclysm", List.of(new Description("pe.item.nova_cataclysm.desc", "Explodes after a short fuse with a blast power of 48."))),
			Map.entry("nova_catalyst", List.of(new Description("pe.item.nova_catalyst.desc", "Explodes after a short fuse with a blast power of 16."))),
			Map.entry("alchemical_coal", List.of(new Description("pe.item.alchemical_coal.desc", "Long-burning furnace and collector fuel; collectors can upgrade it to Mobius Fuel."))),
			Map.entry("alchemical_coal_block", List.of(new Description("pe.item.alchemical_coal_block.desc", "Long-burning furnace and collector fuel block; collectors can upgrade it."))),
			Map.entry("mobius_fuel", List.of(new Description("pe.item.mobius_fuel.desc", "Longer-burning furnace and collector fuel; collectors can upgrade it to Aeternalis Fuel."))),
			Map.entry("mobius_fuel_block", List.of(new Description("pe.item.mobius_fuel_block.desc", "Longer-burning furnace and collector fuel block; collectors can upgrade it."))),
			Map.entry("aeternalis_fuel", List.of(new Description("pe.item.aeternalis_fuel.desc", "Extremely long-burning fuel for collectors and furnaces."))),
			Map.entry("aeternalis_fuel_block", List.of(new Description("pe.item.aeternalis_fuel_block.desc", "The longest-burning furnace and collector fuel block."))),
			Map.entry("dark_matter", List.of(new Description("pe.item.dark_matter.desc", "Primary crafting component for Dark Matter blocks, machines, and tools."))),
			Map.entry("dark_matter_block", List.of(new Description("pe.item.dark_matter_block.desc", "Ultra-hard block used to craft Dark Matter machines and components."))),
			Map.entry("red_matter", List.of(new Description("pe.item.red_matter.desc", "Advanced crafting component for Red Matter blocks, machines, and tools."))),
			Map.entry("red_matter_block", List.of(new Description("pe.item.red_matter_block.desc", "Ultra-hard block used to craft high-tier ProjectE machines."))),
			Map.entry("high_covalence_dust", List.of(new Description("pe.item.high_covalence_dust.desc", "Repairs a damaged item in crafting and restores the most durability."))),
			Map.entry("medium_covalence_dust", List.of(new Description("pe.item.medium_covalence_dust.desc", "Repairs a damaged item in crafting and restores more durability."))),
			Map.entry("low_covalence_dust", List.of(new Description("pe.item.low_covalence_dust.desc", "Repairs a damaged item in crafting and restores some durability."))),
			Map.entry("iron_band", List.of(new Description("pe.item.iron_band.desc", "A base component for crafting several ProjectE rings and bands."))),
			Map.entry("transmutation_tablet", List.of(new Description("pe.item.transmutation_tablet.desc", "Portable transmutation: transmute, learn, and unlearn items from your inventory.")))
	);

	private ItemDescriptions() {
	}

	/**
	 * Adds the description of the given item, if it has one, to the given tooltip.
	 */
	public static void addDescription(@NotNull ItemStack stack, @NotNull Consumer<Component> tooltip) {
		for (Description description : getDescriptions(stack)) {
			tooltip.accept(Component.translatable(description.key()));
		}
	}

	public static List<Description> getDescriptions(@NotNull ItemStack stack) {
		if (stack.isEmpty()) {
			return List.of();
		}
		Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
		List<Description> descriptions = DESCRIPTIONS.get(normalize(id));
		return descriptions == null ? List.of() : descriptions;
	}

	public static Map<String, List<Description>> allDescriptions() {
		return DESCRIPTIONS;
	}

	/**
	 * Colored variants of an item share the description of the item they are a variant of.
	 */
	private static String normalize(@NotNull Identifier id) {
		String path = id.getPath();
		int dot = path.indexOf('.');
		if (dot != -1) {
			//Colored shields, like dark_matter_shield.blue
			return path.substring(0, dot);
		}
		if (path.endsWith("_alchemical_bag")) {
			//Colored bags, like black_alchemical_bag
			return "alchemical_bag";
		}
		return path;
	}
}
