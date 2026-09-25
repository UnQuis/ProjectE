package moze_intel.projecte.common.tag;

import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import org.jetbrains.annotations.NotNull;

public class PEEntityTypeTagsProvider extends EntityTypeTagsProvider {

	public PEEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider provider) {
		//Note: Intentionally does not include Axolotls, Allays, or Sniffers
		tag(PETags.Entities.RANDOMIZER_PEACEFUL).add(
				key(EntityTypes.ARMADILLO),
				key(EntityTypes.BAT),
				key(EntityTypes.BEE),
				key(EntityTypes.CAMEL),
				key(EntityTypes.CAT),
				key(EntityTypes.CHICKEN),
				key(EntityTypes.COD),
				key(EntityTypes.COW),
				key(EntityTypes.DOLPHIN),
				key(EntityTypes.DONKEY),
				key(EntityTypes.FOX),
				key(EntityTypes.FROG),
				key(EntityTypes.GLOW_SQUID),
				key(EntityTypes.GOAT),
				key(EntityTypes.HORSE),
				key(EntityTypes.LLAMA),
				key(EntityTypes.MOOSHROOM),
				key(EntityTypes.MULE),
				key(EntityTypes.OCELOT),
				key(EntityTypes.PANDA),
				key(EntityTypes.PARROT),
				key(EntityTypes.PIG),
				key(EntityTypes.POLAR_BEAR),
				key(EntityTypes.PUFFERFISH),
				key(EntityTypes.RABBIT),
				key(EntityTypes.SALMON),
				key(EntityTypes.SHEEP),
				key(EntityTypes.SQUID),
				key(EntityTypes.STRIDER),
				key(EntityTypes.TADPOLE),
				key(EntityTypes.TRADER_LLAMA),
				key(EntityTypes.TROPICAL_FISH),
				key(EntityTypes.TURTLE),
				key(EntityTypes.VILLAGER),
				key(EntityTypes.WANDERING_TRADER),
				key(EntityTypes.WOLF)
		);
		tag(PETags.Entities.RANDOMIZER_HOSTILE).add(
				key(EntityTypes.BLAZE),
				key(EntityTypes.BOGGED),
				key(EntityTypes.BREEZE),
				key(EntityTypes.CREEPER),
				key(EntityTypes.DROWNED),
				key(EntityTypes.ENDERMAN),
				key(EntityTypes.ENDERMITE),
				key(EntityTypes.EVOKER),
				key(EntityTypes.GHAST),
				key(EntityTypes.GUARDIAN),
				key(EntityTypes.HOGLIN),
				key(EntityTypes.HUSK),
				key(EntityTypes.PHANTOM),
				key(EntityTypes.PIGLIN),
				key(EntityTypes.PIGLIN_BRUTE),
				key(EntityTypes.PILLAGER),
				key(EntityTypes.RABBIT),
				key(EntityTypes.SHULKER),
				key(EntityTypes.SILVERFISH),
				key(EntityTypes.SKELETON),
				key(EntityTypes.SKELETON_HORSE),
				key(EntityTypes.SLIME),
				key(EntityTypes.SPIDER),
				key(EntityTypes.STRAY),
				key(EntityTypes.VEX),
				key(EntityTypes.VINDICATOR),
				key(EntityTypes.WITCH),
				key(EntityTypes.WITHER_SKELETON),
				key(EntityTypes.ZOGLIN),
				key(EntityTypes.ZOMBIE),
				key(EntityTypes.ZOMBIE_HORSE),
				key(EntityTypes.ZOMBIE_VILLAGER),
				key(EntityTypes.ZOMBIFIED_PIGLIN)
		);
		tag(PETags.Entities.BLACKLIST_SWRG);
		tag(PETags.Entities.BLACKLIST_INTERDICTION);
		//Vanilla tags
		tag(EntityTypeTags.ARROWS).add(key(PEEntityTypes.HOMING_ARROW.get()));
		tag(EntityTypeTags.IMPACT_PROJECTILES).add(
				key(PEEntityTypes.FIRE_PROJECTILE.get()),
				key(PEEntityTypes.LAVA_PROJECTILE.get()),
				key(PEEntityTypes.LENS_PROJECTILE.get()),
				key(PEEntityTypes.SWRG_PROJECTILE.get()),
				key(PEEntityTypes.WATER_PROJECTILE.get()),
				key(PEEntityTypes.PE_TRIDENT.get())
		);
	}

	private static ResourceKey<EntityType<?>> key(EntityType<?> type) {
		return BuiltInRegistries.ENTITY_TYPE.getResourceKey(type).orElseThrow();
	}
}
