package moze_intel.projecte.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

//In Minecraft 26.1 Explosion became an interface and ServerExplosion its (non-final) implementation,
// so we extend ServerExplosion to hook into EventHooks.onExplosionStart/onExplosionDetonate (they require ServerExplosion).
//Private members of ServerExplosion are not accessible, so calculateExplodedPositions/hurtEntities are
// [VanillaCopy] reimplemented here on top of the public accessors ServerExplosion exposes.
public class NovaExplosion extends ServerExplosion {

	private final ExplosionDamageCalculator damageCalculator;

	public NovaExplosion(ServerLevel level, @Nullable Entity entity, double x, double y, double z, float radius, Explosion.BlockInteraction blockInteraction) {
		//Nova Explosions don't cause fire
		super(level, entity, null, null, new Vec3(x, y, z), radius, false, blockInteraction);
		this.damageCalculator = makeDamageCalculator(entity);
	}

	private static ExplosionDamageCalculator makeDamageCalculator(@Nullable Entity source) {
		//Mirrors ServerExplosion#makeDamageCalculator (private there)
		return source == null ? new ExplosionDamageCalculator() : new EntityBasedExplosionDamageCalculator(source);
	}

	public Holder<SoundEvent> getExplosionSound() {
		return SoundEvents.GENERIC_EXPLODE;
	}

	// [VanillaCopy] super, but collecting all drops into one place, and no fire (so we don't have to copy that bit)
	// We also sync the sound playing and particles by the caller instead of here, so we don't bother copying those either
	// Replaces the old explode() + finalizeExplosion() pair from when Explosion was a class
	public List<BlockPos> detonate() {
		this.level().gameEvent(this.getDirectSourceEntity(), GameEvent.EXPLODE, this.center());
		List<BlockPos> toBlow = calculateExplodedPositions();
		hurtEntities(toBlow);
		List<BlockPos> particlePositions = new ArrayList<>();
		if (this.getBlockInteraction() != Explosion.BlockInteraction.KEEP) {
			ProfilerFiller profiler = Profiler.get();
			profiler.push("explosion_blocks");

			List<ItemStack> allDrops = new ArrayList<>();
			Util.shuffle(toBlow, this.level().getRandom());

			for (BlockPos pos : toBlow) {
				BlockState state = this.level().getBlockState(pos);
				if (!state.isAir()) {
					particlePositions.add(pos);
				}
				// PE: Collect the drops we can, spawn the stuff we can't
				state.onExplosionHit(this.level(), pos, this, (stack, position) -> allDrops.add(stack));
			}

			// PE: Drop all together
			LivingEntity placer = this.getIndirectSourceEntity();
			WorldHelper.createLootDrop(allDrops, this.level(), placer == null ? this.center() : placer.position());
			profiler.pop();
		}
		return particlePositions;
	}

	// [VanillaCopy] super#calculateExplodedPositions (private there)
	private List<BlockPos> calculateExplodedPositions() {
		Set<BlockPos> toBlowSet = new HashSet<>();
		Vec3 center = this.center();

		for (int xx = 0; xx < 16; xx++) {
			for (int yy = 0; yy < 16; yy++) {
				for (int zz = 0; zz < 16; zz++) {
					if (xx == 0 || xx == 15 || yy == 0 || yy == 15 || zz == 0 || zz == 15) {
						double xd = xx / 15.0F * 2.0F - 1.0F;
						double yd = yy / 15.0F * 2.0F - 1.0F;
						double zd = zz / 15.0F * 2.0F - 1.0F;
						double d = Math.sqrt(xd * xd + yd * yd + zd * zd);
						xd /= d;
						yd /= d;
						zd /= d;
						float remainingPower = this.radius() * (0.7F + this.level().getRandom().nextFloat() * 0.6F);
						double xp = center.x;
						double yp = center.y;
						double zp = center.z;

						for (float stepSize = 0.3F; remainingPower > 0.0F; remainingPower -= 0.22500001F) {
							BlockPos pos = BlockPos.containing(xp, yp, zp);
							BlockState block = this.level().getBlockState(pos);
							FluidState fluid = this.level().getFluidState(pos);
							if (!this.level().isInWorldBounds(pos)) {
								break;
							}

							Optional<Float> resistance = this.damageCalculator.getBlockExplosionResistance(this, this.level(), pos, block, fluid);
							if (resistance.isPresent()) {
								remainingPower -= (resistance.get() + 0.3F) * 0.3F;
							}

							if (remainingPower > 0.0F && this.damageCalculator.shouldBlockExplode(this, this.level(), pos, block, remainingPower)) {
								toBlowSet.add(pos);
							}

							xp += xd * 0.3F;
							yp += yd * 0.3F;
							zp += zd * 0.3F;
						}
					}
				}
			}
		}

		return new ArrayList<>(toBlowSet);
	}

	// [VanillaCopy] super#hurtEntities(List) (private there)
	private void hurtEntities(List<BlockPos> blocks) {
		if (!(this.radius() < 1.0E-5F)) {
			float doubleRadius = this.radius() * 2.0F;
			Vec3 center = this.center();
			int x0 = Mth.floor(center.x - doubleRadius - 1.0);
			int x1 = Mth.floor(center.x + doubleRadius + 1.0);
			int y0 = Mth.floor(center.y - doubleRadius - 1.0);
			int y1 = Mth.floor(center.y + doubleRadius + 1.0);
			int z0 = Mth.floor(center.z - doubleRadius - 1.0);
			int z1 = Mth.floor(center.z + doubleRadius + 1.0);

			List<Entity> list = this.level().getEntities(this.getDirectSourceEntity(), new AABB(x0, y0, z0, x1, y1, z1));
			EventHooks.onExplosionDetonate(this.level(), this, list, blocks);
			for (Entity entity : list) {
				if (!entity.ignoreExplosion(this)) {
					double dist = Math.sqrt(entity.distanceToSqr(center)) / doubleRadius;
					if (!(dist > 1.0)) {
						Vec3 entityOrigin = entity instanceof PrimedTnt ? entity.position() : entity.getEyePosition();
						Vec3 direction = entityOrigin.subtract(center).normalize();
						boolean shouldDamageEntity = this.damageCalculator.shouldDamageEntity(this, entity);
						float knockbackMultiplier = this.damageCalculator.getKnockbackMultiplier(entity);
						float exposure = !shouldDamageEntity && knockbackMultiplier == 0.0F ? 0.0F : getSeenPercent(center, entity);
						if (shouldDamageEntity) {
							entity.hurtServer(this.level(), this.getDamageSource(), this.damageCalculator.getEntityDamageAmount(this, entity, exposure));
						}

						double knockbackResistance = entity instanceof LivingEntity livingEntity
							? livingEntity.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE)
							: 0.0;
						double knockbackPower = (1.0 - dist) * exposure * knockbackMultiplier * (1.0 - knockbackResistance);
						Vec3 knockback = direction.scale(knockbackPower);
						knockback = EventHooks.getExplosionKnockback(this.level(), this, entity, knockback, blocks);
						entity.push(knockback);
						if (entity.is(EntityTypeTags.REDIRECTABLE_PROJECTILE) && entity instanceof Projectile projectile) {
							projectile.setOwner(this.getDamageSource().getEntity());
						} else if (entity instanceof Player player && !player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
							this.getHitPlayers().put(player, knockback);
						}

						entity.onExplosionHit(this.getDirectSourceEntity());
					}
				}
			}
		}
	}
}
