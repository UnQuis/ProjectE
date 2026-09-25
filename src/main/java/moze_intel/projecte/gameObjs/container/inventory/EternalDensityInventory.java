package moze_intel.projecte.gameObjs.container.inventory;

import java.util.Set;
import moze_intel.projecte.components.GemData;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jetbrains.annotations.NotNull;

public class EternalDensityInventory extends ItemStacksResourceHandler {

	private final ItemStack invItem;
	private final boolean remote;

	public EternalDensityInventory(ItemStack stack, boolean remote) {
		super(9);
		this.invItem = stack;
		this.remote = remote;
		int slot = 0;
		for (ItemStack whitelisted : invItem.getOrDefault(PEDataComponentTypes.GEM_DATA, GemData.EMPTY).whitelist()) {
			if (!whitelisted.isEmpty()) {
				//Note: We copy it so that it doesn't mutate our gem data's stack
				stacks.set(slot++, whitelisted.copy());
				if (slot >= getSlots()) {
					break;
				}
			}
		}
	}

	public int getSlots() {
		return size();
	}

	public ItemStack getStackInSlot(int slot) {
		return getResource(slot).toStack(getAmountAsInt(slot));
	}

	@Override
	public boolean isValid(int index, ItemResource resource) {
		if (resource.isEmpty()) {
			return true;
		}
		ItemStack stack = resource.toStack(1);
		for (int i = 0, slots = size(); i < slots; i++) {
			ItemStack stored = getStackInSlot(i);
			if (!stored.isEmpty() && ItemStack.isSameItemSameComponents(stack, stored)) {
				//Only allow duplicates if it is the same slot as it is already stored in
				return i == index;
			}
		}
		return true;
	}

	public void setStackInSlot(int slot, @NotNull ItemStack stack) {
		if (isValid(slot, ItemResource.of(stack))) {
			set(slot, ItemResource.of(stack), stack.getCount());
		}
	}

	@Override
	protected int getCapacity(int index, ItemResource resource) {
		return 1;
	}

	@Override
	protected void onContentsChanged(int slot, ItemStack previousContents) {
		if (remote) {
			//Skip updating the item on teh client as we already sync the data the client cares about
			return;
		}
		Set<ItemStack> targets = ItemStackLinkedSet.createTypeAndComponentsSet();
		for (int i = 0, slots = size(); i < slots; ++i) {
			ItemStack stackInSlot = getStackInSlot(i);
			if (!stackInSlot.isEmpty()) {
				targets.add(stackInSlot.copyWithCount(1));
			}
		}
		invItem.update(PEDataComponentTypes.GEM_DATA, GemData.EMPTY, targets, GemData::withWhitelistSafe);
	}
}
