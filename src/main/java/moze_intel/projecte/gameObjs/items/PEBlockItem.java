package moze_intel.projecte.gameObjs.items;

import java.util.function.Consumer;
import moze_intel.projecte.gameObjs.blocks.IBlockTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

/**
 * Block item that forwards tooltip lines to blocks implementing {@link IBlockTooltip},
 * since vanilla no longer exposes a block-level tooltip hook.
 */
public class PEBlockItem extends BlockItem {

	/**
	 * Factory that erases the concrete type so existing
	 * {@code BlockRegistryObject<..., BlockItem>} declarations keep compiling.
	 */
	public static BlockItem of(Block block, Item.Properties properties) {
		return new PEBlockItem(block, properties);
	}

	private PEBlockItem(Block block, Item.Properties properties) {
		super(block, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flags) {
		super.appendHoverText(stack, context, display, tooltip, flags);
		if (getBlock() instanceof IBlockTooltip blockTooltip) {
			blockTooltip.appendBlockTooltip(stack, context, tooltip, flags);
		}
	}
}
