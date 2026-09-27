package moze_intel.projecte.expansion.gui.container.slots;

import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.container.slots.transmutation.SlotOutput;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PXOutputSlot extends SlotOutput {
	protected final TransmutationInventory inv;
	public PXOutputSlot(TransmutationInventory inv, int index, int x, int y) {
		super(inv, index, x, y);
		this.inv = inv;
	}

	/**
	 * The arcane tablet shows how many of the item the player can currently transmute, so the amount is calculated
	 * from the emc on hand and capped by the max stack size of the item. The calculation is client side only: the server
	 * keeps a single item per slot and re-checks how much the player is actually allowed to take.
	 */
	@NotNull
	@Override
	public ItemStack getItem() {
		ItemStack stack = super.getItem();
		if (stack.isEmpty() || inv.isServer()) {
			return stack;
		}
		long value = IEMCProxy.INSTANCE.getValue(stack);
		if (value <= 0) {
			return stack;
		}
		long affordable = inv.getAvailableEmcAsLong() / value;
		if (affordable <= 1) {
			return stack;
		}
		return stack.copyWithCount((int) Math.min(affordable, stack.getMaxStackSize()));
	}
}
