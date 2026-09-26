package moze_intel.projecte;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.client.PEBlockStateProvider;
import moze_intel.projecte.client.PEItemModelProvider;
import moze_intel.projecte.client.PESpriteSourceProvider;
import moze_intel.projecte.client.lang.PELangProvider;
import moze_intel.projecte.client.sound.PESoundProvider;
import moze_intel.projecte.common.PEAdvancementsGenerator;
import moze_intel.projecte.common.PECustomConversionProvider;
import moze_intel.projecte.common.PEPackMetadataGenerator;
import moze_intel.projecte.common.PEWorldTransmutationProvider;
import moze_intel.projecte.common.loot.PEBlockLootTable;
import moze_intel.projecte.common.recipe.PERecipeProvider;
import moze_intel.projecte.common.tag.PEBlockEntityTypeTagsProvider;
import moze_intel.projecte.common.tag.PEBlockTagsProvider;
import moze_intel.projecte.common.tag.PEDamageTypeTagsProvider;
import moze_intel.projecte.common.tag.PEEntityTypeTagsProvider;
import moze_intel.projecte.common.tag.PEItemTagsProvider;
import moze_intel.projecte.common.tag.PEPotionsTagsProvider;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.gameObjs.registries.PEDamageTypes;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = PECore.MODID)
public class ProjectEDataGenerator {

	@SubscribeEvent
	public static void gatherServerData(GatherDataEvent.Server event) {
		//Ensure that we register the configs for data component processors and the like
		EMCMappingHandler.loadMappers();

		DataGenerator gen = event.getGenerator();
		PackOutput output = gen.getPackOutput();
		RegistrySetBuilder registryEntries = new RegistrySetBuilder()
				//Note: Damage types are defined as data (src/datagen/generated/data/projecte/damage_type/player_attack.json) since 26.3 already
				// provides the minecraft:damage_type registry through its own bootstrap, so registering them here would conflict
				.add(RecipeProvider.asBootstrap(PERecipeProvider::new))
				.add(Registries.ADVANCEMENT, new AdvancementProvider(List.of(PEAdvancementsGenerator::new)))
				.add(Registries.LOOT_TABLE, new LootTableProvider(Set.of(), List.of(
						new SubProviderEntry(PEBlockLootTable::new, LootContextParamSets.BLOCK))));
		event.createReloadableRegistryObjects(registryEntries);
		CompletableFuture<HolderLookup.Provider> registries = event.getReloadableLookupProvider();

		event.addProvider(new PEPackMetadataGenerator(output, PELang.PACK_DESCRIPTION));
		//Tag data generators
		event.addProvider(new PEBlockTagsProvider(output, registries));
		event.addProvider(new PEItemTagsProvider(output, registries));
		event.addProvider(new PEEntityTypeTagsProvider(output, registries));
		event.addProvider(new PEBlockEntityTypeTagsProvider(output, registries));
		event.addProvider(new PEDamageTypeTagsProvider(output, registries));
		event.addProvider(new PEPotionsTagsProvider(output, registries));
		//Other generators (after tags in case we need them to exist)
		event.addProvider(new PECustomConversionProvider(output, registries));
		event.addProvider(new PEWorldTransmutationProvider(output, registries));
	}

	@SubscribeEvent
	public static void gatherClientData(GatherDataEvent.Client event) {
		DataGenerator gen = event.getGenerator();
		PackOutput output = gen.getPackOutput();
		event.addProvider(new moze_intel.projecte.expansion.lang.ExpansionLangProvider(output));
		event.addProvider(new PESoundProvider(output));
		event.addProvider(new PEBlockStateProvider(output));
		event.addProvider(new PEItemModelProvider(output));
		event.addProvider(new PESpriteSourceProvider(output, event.getReloadableLookupProvider()));
	}
}
