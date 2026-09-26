package moze_intel.projecte.expansion.registries;

import moze_intel.projecte.PECore;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class ExpansionDamageTypes {

	public static final ResourceKey<DamageType> WALK_ON_SUN = ResourceKey.create(Registries.DAMAGE_TYPE, PECore.rl("walk_on_sun"));
	public static final ResourceKey<DamageType> STARE_AT_SUN = ResourceKey.create(Registries.DAMAGE_TYPE, PECore.rl("stare_at_sun"));
}
