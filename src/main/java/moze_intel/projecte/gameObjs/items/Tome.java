package moze_intel.projecte.gameObjs.items;

import java.util.List;
import moze_intel.projecte.expansion.ExpansionSettings;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

public class Tome extends ItemPE {

	public Tome(Properties props) {
		super(props);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		//Note: The tome is our most important item, so it gets the enchantment glint if the expansion has it enabled
		return ExpansionSettings.tomeGlint || super.isFoil(stack);
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, context, tooltip, flags);
		tooltip.add(PELang.TOOLTIP_TOME.translate());
	}
}