package moze_intel.projecte.gameObjs.items.armor;

import java.util.function.UnaryOperator;
import moze_intel.projecte.gameObjs.registries.PEArmorMaterials;
import moze_intel.projecte.gameObjs.registries.PEItems;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.damagesource.DamageTypes;

public abstract class GemArmorBase extends PEArmor {

	public GemArmorBase(ArmorType armorType, Properties props) {
		this(PEArmorMaterials.GEM_ARMOR, armorType, UnaryOperator.identity(), props);
	}

	protected GemArmorBase(ArmorMaterial material, ArmorType armorType, UnaryOperator<Properties> postProcess, Properties props) {
		super(material, armorType, postProcess, props);
	}

	@Override
	public float getFullSetBaseReduction() {
		return 0.9F;
	}

	/**
	 * Environmental damage that normally ignores armor entirely, but which the gem armor still mitigates.
	 * The gem set is meant to be nearly unkillable, so falling into the void, the Warden's sonic boom or suffocating
	 * in a wall should not be an instant death for a player wearing the full set.
	 */
	private static boolean isMitigatedArmorBypassingSource(DamageSource source) {
		return source.typeHolder().is(DamageTypes.FELL_OUT_OF_WORLD) || source.typeHolder().is(DamageTypes.SONIC_BOOM) ||
				source.typeHolder().is(DamageTypes.IN_WALL);
	}

	@Override
	public float getMaxDamageAbsorb(ArmorType type, DamageSource source) {
		if (source.is(DamageTypeTags.IS_EXPLOSION)) {
			return 750;
		}
		if (type == ArmorType.BOOTS && source.is(DamageTypeTags.IS_FALL)) {
			return 15 / getPieceEffectiveness(type);
		} else if (type == ArmorType.HELMET && source.is(DamageTypeTags.IS_DROWNING)) {
			return 15 / getPieceEffectiveness(type);
		}
		if (source.is(DamageTypeTags.BYPASSES_ARMOR) && !isMitigatedArmorBypassingSource(source)) {
			return 0;
		}
		//If the source is not unblockable, allow our piece to block a certain amount of damage
		if (type == ArmorType.HELMET || type == ArmorType.BOOTS) {
			return 400;
		}
		return 500;
	}

	public static boolean hasAnyPiece(Player player) {
		return player.getItemBySlot(EquipmentSlot.HEAD).is(PEItems.GEM_HELMET) ||
			   player.getItemBySlot(EquipmentSlot.CHEST).is(PEItems.GEM_CHESTPLATE) ||
			   player.getItemBySlot(EquipmentSlot.LEGS).is(PEItems.GEM_LEGGINGS) ||
			   player.getItemBySlot(EquipmentSlot.FEET).is(PEItems.GEM_BOOTS);
	}

	public static boolean hasFullSet(Player player) {
		return player.getItemBySlot(EquipmentSlot.HEAD).is(PEItems.GEM_HELMET) &&
			   player.getItemBySlot(EquipmentSlot.CHEST).is(PEItems.GEM_CHESTPLATE) &&
			   player.getItemBySlot(EquipmentSlot.LEGS).is(PEItems.GEM_LEGGINGS) &&
			   player.getItemBySlot(EquipmentSlot.FEET).is(PEItems.GEM_BOOTS);
	}
}