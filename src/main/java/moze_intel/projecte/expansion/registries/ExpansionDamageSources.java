package moze_intel.projecte.expansion.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

public class ExpansionDamageSources {

	private final Registry<DamageType> damageTypes;
	private final DamageSource walkOnSun;
	private final DamageSource stareAtSun;

	public ExpansionDamageSources(RegistryAccess registry) {
		this.damageTypes = registry.registryOrThrow(Registries.DAMAGE_TYPE);
		this.walkOnSun = this.source(ExpansionDamageTypes.WALK_ON_SUN);
		this.stareAtSun = this.source(ExpansionDamageTypes.STARE_AT_SUN);
	}

	private DamageSource source(ResourceKey<DamageType> damageType) {
		return new DamageSource(this.damageTypes.getHolderOrThrow(damageType));
	}

	@SuppressWarnings("unused")
	private DamageSource source(ResourceKey<DamageType> damageType, @Nullable Entity causingEntity) {
		return new DamageSource(this.damageTypes.getHolderOrThrow(damageType), causingEntity);
	}

	@SuppressWarnings("unused")
	private DamageSource source(ResourceKey<DamageType> damageType, @Nullable Entity causingEntity, @Nullable Entity directEntity) {
		return new DamageSource(this.damageTypes.getHolderOrThrow(damageType), causingEntity, directEntity);
	}

	public DamageSource walkOnSun() {
		return this.walkOnSun;
	}

	public DamageSource stareAtSun() {
		return this.stareAtSun;
	}

	public static ExpansionDamageSources fromServer(MinecraftServer server) {
		return new ExpansionDamageSources(server.registryAccess());
	}

	public static ExpansionDamageSources fromLevel(Level level) {
		return new ExpansionDamageSources(level.registryAccess());
	}
}
