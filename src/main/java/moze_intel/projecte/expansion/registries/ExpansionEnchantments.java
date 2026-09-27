package moze_intel.projecte.expansion.registries;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * The Alchemical Collection enchantment, which turns mined blocks into EMC instead of dropping them.
 * <p>
 * Note: 1.20.4 has no data driven enchantments for mod namespaces, so the enchantment is registered in code here
 * instead of being described by a json file as in 1.21.
 */
@SuppressWarnings("unused")
public class ExpansionEnchantments {

	public static final PEDeferredRegister<Enchantment> ENCHANTMENTS = new PEDeferredRegister<>(Registries.ENCHANTMENT, PECore.MODID);

	public static final PEDeferredHolder<Enchantment, Enchantment> ALCHEMICAL_COLLECTION = ENCHANTMENTS.register("alchemical_collection",
			AlchemicalCollectionEnchantment::new);

	public static class AlchemicalCollectionEnchantment extends Enchantment {

		public AlchemicalCollectionEnchantment() {
			super(Rarity.RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
		}

		/**
		 * @return {@code true} so the enchantment can be applied to any tool. Whether the collection is actually
		 * active is decided by the tool itself, which the users toggle with the extra function keybind.
		 */
		@Override
		public boolean canEnchant(ItemStack stack) {
			return true;
		}
	}
}
