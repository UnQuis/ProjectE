package moze_intel.projecte.expansion.gui.container.inventory;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;

public class PXCraftingContainer extends TransientCraftingContainer implements CraftingContainer {
	public PXCraftingContainer(AbstractContainerMenu menu, int width, int height) {
		super(menu, width, height);
	}
}
