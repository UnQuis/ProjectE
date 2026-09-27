package moze_intel.projecte.expansion.registries;

import moze_intel.projecte.PECore;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

@SuppressWarnings("unused")
public class ExpansionEnchantments {

	public static final ResourceKey<Enchantment> ALCHEMICAL_COLLECTION = ResourceKey.create(Registries.ENCHANTMENT, PECore.rl("alchemical_collection"));
}
