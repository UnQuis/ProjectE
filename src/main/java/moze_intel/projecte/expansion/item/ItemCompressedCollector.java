package moze_intel.projecte.expansion.item;

import moze_intel.projecte.expansion.util.IHasMatter;
import moze_intel.projecte.expansion.util.Lang;
import moze_intel.projecte.expansion.util.Matter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class ItemCompressedCollector extends Item implements IHasMatter {
	public final Matter matter;
	public ItemCompressedCollector(Properties properties,Matter matter) {
		super(properties.rarity(matter.getRarity()));
		this.matter = matter;
	}

	@Override
	public Matter getMatter() {
		return matter;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, tooltip, flag);
		tooltip.accept(Lang.Items.COMRESSED_COLLECTOR_TOOLTIP.translateColored(ChatFormatting.GRAY));
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}
}
