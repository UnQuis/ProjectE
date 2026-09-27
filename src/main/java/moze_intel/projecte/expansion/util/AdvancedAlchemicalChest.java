package moze_intel.projecte.expansion.util;

import moze_intel.projecte.expansion.block.BlockAdvancedAlchemicalChest;
import moze_intel.projecte.expansion.registries.ExpansionBlocks;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AdvancedAlchemicalChest {

	private static final Map<DyeColor, BlockRegistryObject<BlockAdvancedAlchemicalChest, BlockItem>> blocks = new HashMap<>();

	public static void register() {
		for (DyeColor color : DyeColor.values()) {
			blocks.put(color, ExpansionBlocks.BLOCKS.register(String.format("%s_advanced_alchemical_chest", color.getName()), () -> new BlockAdvancedAlchemicalChest(BlockAdvancedAlchemicalChest.getProperties(), color), block -> new BlockItem(block, new Item.Properties())));
		}
	}

	public static void setAllCreativeTab(CreativeModeTab.Output output) {
		Arrays.stream(DyeColor.values()).forEach(color -> output.accept(blocks.get(color).asItem()));
	}

	public static BlockRegistryObject<BlockAdvancedAlchemicalChest, BlockItem> getRegistryBlock(DyeColor color) {
		return blocks.get(color);
	}

	public static BlockAdvancedAlchemicalChest getBlock(DyeColor color) {
		return getRegistryBlock(color).getBlock();
	}

	public static BlockAdvancedAlchemicalChest[] getBlocks() {
		return blocks.values().stream().map(BlockRegistryObject::getBlock).toArray(BlockAdvancedAlchemicalChest[]::new);
	}
}
