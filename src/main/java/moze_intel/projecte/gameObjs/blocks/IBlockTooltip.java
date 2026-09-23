package moze_intel.projecte.gameObjs.blocks;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * Vanilla 26.x removed the block-level tooltip hook, so blocks that need custom tooltip lines
 * implement this interface and {@link moze_intel.projecte.gameObjs.items.PEBlockItem} delegates to it.
 */
public interface IBlockTooltip {

	void appendBlockTooltip(ItemStack stack, Item.TooltipContext context, Consumer<Component> tooltip, TooltipFlag flags);
}
