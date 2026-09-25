package moze_intel.projecte.common.tag;

import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.gameObjs.PETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PotionTagsProvider;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import org.jetbrains.annotations.NotNull;

public class PEPotionsTagsProvider extends PotionTagsProvider {

	public PEPotionsTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider provider) {
		tag(PETags.Potions.IGNORE_MISSING_EMC).add(Potions.LUCK);
	}

	@NotNull
	@Override
	public String getName() {
		return "Potion Tags";
	}
}
