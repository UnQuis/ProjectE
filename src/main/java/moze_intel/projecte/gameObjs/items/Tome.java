package moze_intel.projecte.gameObjs.items;

import java.util.List;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import moze_intel.projecte.expansion.ExpansionSettings;

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
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, level, tooltips, flags);
		tooltips.add(PELang.TOOLTIP_TOME.translate());
	}
}