package moze_intel.projecte.common.tag;

import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.gameObjs.registries.PEDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class PEDamageTypeTagsProvider extends DamageTypeTagsProvider {

	public PEDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider provider) {
		ResourceKey<DamageType> playerAttack = PEDamageTypes.BYPASS_ARMOR_PLAYER_ATTACK.key();
		tag(DamageTypeTags.BYPASSES_ARMOR).add(playerAttack);
		tag(DamageTypeTags.CAN_BREAK_ARMOR_STAND).add(playerAttack);
		tag(DamageTypeTags.IS_PLAYER_ATTACK).add(playerAttack);
		tag(DamageTypeTags.PANIC_CAUSES).add(playerAttack);
		tag(Tags.DamageTypes.IS_PHYSICAL).add(playerAttack);
	}

	@NotNull
	@Override
	public String getName() {
		return "Damage Type Tags";
	}
}
