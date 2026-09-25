package moze_intel.projecte.common.tag;

import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

public class PEBlockTagsProvider extends BlockTagsProvider {

	public PEBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, PECore.MODID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(PETags.Blocks.FARMING_OVERRIDE).add(Blocks.PINK_PETALS);
		TagAppender<Block, Block> blacklistHarvest = tag(PETags.Blocks.BLACKLIST_HARVEST);
		//Add blocks that sometimes return false from isValidBonemealTarget, but that we don't actually want to be broken
		blacklistHarvest.add(
				//If there is no neighboring nylium we don't want to cause the netherrack to be broken
				Blocks.NETHERRACK,
				//If it doesn't have air above it
				Blocks.BAMBOO_SAPLING,
				//If it doesn't have air below it
				Blocks.ROOTED_DIRT,
				//If it has a fluid above it
				Blocks.AZALEA,
				Blocks.FLOWERING_AZALEA,
				//If it doesn't have air
				Blocks.BIG_DRIPLEAF,
				Blocks.BIG_DRIPLEAF_STEM
		);
		TagAppender<Block, Block> overridePlantable = tag(PETags.Blocks.OVERRIDE_PLANTABLE);
		addTags(overridePlantable,
				BlockTags.LEAVES,
				//Note: All vanilla tall flowers are bonemealable, so will get handled before being used by this tag
				// but if a mod adds a tall flower that doesn't inherit the class hierarchy, having this could be useful
				BlockTags.FLOWERS,
				Tags.Blocks.PUMPKINS_NORMAL);
		overridePlantable.add(Blocks.MELON);
		for (Block block : BuiltInRegistries.BLOCK) {
			if (WorldHelper.isPlantableImplementation(block)) {
				overridePlantable.add(block);
			}
			if (WorldHelper.isUnharvestableImplementation(block)) {
				blacklistHarvest.add(block);
			}
		}
		tag(PETags.Blocks.BLACKLIST_TIME_WATCH);
		tag(PETags.Blocks.VEIN_SHOVEL).add(Blocks.CLAY).addTag(Tags.Blocks.GRAVELS);
		//Vanilla/Forge Tags
		tag(Tags.Blocks.BARRELS).add(projecteBlock(PEBlocks.ALCHEMICAL_BARREL));
		tag(Tags.Blocks.CHESTS).add(projecteBlock(PEBlocks.ALCHEMICAL_CHEST));
		tag(Tags.Blocks.PLAYER_WORKSTATIONS_FURNACES).add(
				projecteBlock(PEBlocks.DARK_MATTER_FURNACE), projecteBlock(PEBlocks.RED_MATTER_FURNACE));
		tag(BlockTags.BEACON_BASE_BLOCKS).add(projecteBlock(PEBlocks.DARK_MATTER), projecteBlock(PEBlocks.RED_MATTER));
		tag(BlockTags.GUARDED_BY_PIGLINS).add(
				projecteBlock(PEBlocks.ALCHEMICAL_CHEST), projecteBlock(PEBlocks.ALCHEMICAL_BARREL),
				projecteBlock(PEBlocks.CONDENSER), projecteBlock(PEBlocks.CONDENSER_MK2));
		tag(BlockTags.PIGLIN_REPELLENTS).add(projecteBlock(PEBlocks.INTERDICTION_LANTERN));
		tag(BlockTags.INFINIBURN_OVERWORLD).add(
				projecteBlock(PEBlocks.ALCHEMICAL_COAL), projecteBlock(PEBlocks.MOBIUS_FUEL),
				projecteBlock(PEBlocks.AETERNALIS_FUEL));
		addImmuneBlocks(BlockTags.DRAGON_IMMUNE);
		addImmuneBlocks(BlockTags.WITHER_IMMUNE);

		tag(PETags.Blocks.MINEABLE_WITH_HAMMER);
		tag(PETags.Blocks.MINEABLE_WITH_KATAR);
		tag(PETags.Blocks.MINEABLE_WITH_MORNING_STAR);

		tag(PETags.Blocks.NEEDS_DARK_MATTER_TOOL).add(
				projecteBlock(PEBlocks.DARK_MATTER), projecteBlock(PEBlocks.DARK_MATTER_FURNACE),
				projecteBlock(PEBlocks.DARK_MATTER_PEDESTAL));
		tag(PETags.Blocks.NEEDS_RED_MATTER_TOOL).add(
				projecteBlock(PEBlocks.RED_MATTER), projecteBlock(PEBlocks.RED_MATTER_FURNACE));
		tag(PETags.Blocks.INCORRECT_FOR_RED_MATTER_TOOL);
		addTags(tag(PETags.Blocks.INCORRECT_FOR_DARK_MATTER_TOOL), PETags.Blocks.NEEDS_RED_MATTER_TOOL);
		addTags(tag(BlockTags.INCORRECT_FOR_NETHERITE_TOOL), PETags.Blocks.NEEDS_DARK_MATTER_TOOL,
				PETags.Blocks.NEEDS_RED_MATTER_TOOL);
		addTags(tag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL), PETags.Blocks.NEEDS_DARK_MATTER_TOOL,
				PETags.Blocks.NEEDS_RED_MATTER_TOOL);
		addTags(tag(BlockTags.INCORRECT_FOR_IRON_TOOL), PETags.Blocks.NEEDS_DARK_MATTER_TOOL,
				PETags.Blocks.NEEDS_RED_MATTER_TOOL);
		addTags(tag(BlockTags.INCORRECT_FOR_STONE_TOOL), PETags.Blocks.NEEDS_DARK_MATTER_TOOL,
				PETags.Blocks.NEEDS_RED_MATTER_TOOL);
		addTags(tag(BlockTags.INCORRECT_FOR_GOLD_TOOL), PETags.Blocks.NEEDS_DARK_MATTER_TOOL,
				PETags.Blocks.NEEDS_RED_MATTER_TOOL);
		addTags(tag(BlockTags.INCORRECT_FOR_WOODEN_TOOL), PETags.Blocks.NEEDS_DARK_MATTER_TOOL,
				PETags.Blocks.NEEDS_RED_MATTER_TOOL);

		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
				projecteBlock(PEBlocks.ALCHEMICAL_CHEST), projecteBlock(PEBlocks.ALCHEMICAL_COAL),
				projecteBlock(PEBlocks.ALCHEMICAL_BARREL), projecteBlock(PEBlocks.MOBIUS_FUEL),
				projecteBlock(PEBlocks.AETERNALIS_FUEL), projecteBlock(PEBlocks.COLLECTOR),
				projecteBlock(PEBlocks.COLLECTOR_MK2), projecteBlock(PEBlocks.COLLECTOR_MK3),
				projecteBlock(PEBlocks.CONDENSER), projecteBlock(PEBlocks.CONDENSER_MK2),
				projecteBlock(PEBlocks.DARK_MATTER_PEDESTAL), projecteBlock(PEBlocks.DARK_MATTER_FURNACE),
				projecteBlock(PEBlocks.RED_MATTER_FURNACE), projecteBlock(PEBlocks.DARK_MATTER),
				projecteBlock(PEBlocks.RED_MATTER), projecteBlock(PEBlocks.TRANSMUTATION_TABLE),
				projecteBlock(PEBlocks.RELAY), projecteBlock(PEBlocks.RELAY_MK2),
				projecteBlock(PEBlocks.RELAY_MK3), projecteBlock(PEBlocks.INTERDICTION_LANTERN));

		//MINEABLE_WITH_PE_SHEARS
		addTags(tag(PETags.Blocks.MINEABLE_WITH_PE_HAMMER), PETags.Blocks.MINEABLE_WITH_HAMMER,
				BlockTags.MINEABLE_WITH_PICKAXE);
		tag(PETags.Blocks.MINEABLE_WITH_PE_SHEARS).add(Blocks.COBWEB);
		tag(PETags.Blocks.MINEABLE_WITH_PE_SWORD).add(Blocks.COBWEB);
		addTags(tag(PETags.Blocks.MINEABLE_WITH_PE_KATAR), PETags.Blocks.MINEABLE_WITH_KATAR,
				BlockTags.MINEABLE_WITH_AXE, BlockTags.MINEABLE_WITH_HOE, PETags.Blocks.MINEABLE_WITH_PE_SHEARS,
				PETags.Blocks.MINEABLE_WITH_PE_SWORD).add(Blocks.COBWEB);
		addTags(tag(PETags.Blocks.MINEABLE_WITH_PE_MORNING_STAR), PETags.Blocks.MINEABLE_WITH_MORNING_STAR,
				PETags.Blocks.MINEABLE_WITH_PE_HAMMER, BlockTags.MINEABLE_WITH_SHOVEL);

		tag(BlockTags.WALL_POST_OVERRIDE).add(projecteBlock(PEBlocks.INTERDICTION_TORCH));
	}

	private void addImmuneBlocks(TagKey<Block> tag) {
		tag(tag).add(
				projecteBlock(PEBlocks.DARK_MATTER), projecteBlock(PEBlocks.DARK_MATTER_FURNACE),
				projecteBlock(PEBlocks.DARK_MATTER_PEDESTAL), projecteBlock(PEBlocks.RED_MATTER),
				projecteBlock(PEBlocks.RED_MATTER_FURNACE), projecteBlock(PEBlocks.CONDENSER_MK2));
	}

	private static Block projecteBlock(BlockRegistryObject<?, ?> block) {
		return block.getBlock();
	}

	private static TagAppender<Block, Block> addTags(TagAppender<Block, Block> appender, TagKey<Block>... tags) {
		for (TagKey<Block> tag : tags) {
			appender.addTag(tag);
		}
		return appender;
	}
}
