package moze_intel.projecte.gameObjs.container.slots;

import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * Slot backed by a {@link ResourceHandler} of items. Unlike the vanilla container view, every mutation is
 * performed transactionally through the handler instead of mutating a shared {@link ItemStack} in place.
 */
public class TransactionalResourceHandlerSlot extends Slot {

	private static final SimpleContainer EMPTY_CONTAINER = new SimpleContainer(0);
	private final ResourceHandler<ItemResource> handler;

	public TransactionalResourceHandlerSlot(ResourceHandler<ItemResource> handler, int index, int x, int y) {
		super(EMPTY_CONTAINER, index, x, y);
		this.handler = handler;
	}

	@Override
	public ItemStack getItem() {
		return ItemUtil.getStack(handler, getSlotIndex());
	}

	@Override
	public void set(ItemStack stack) {
		if (!ItemHelper.setStack(handler, getSlotIndex(), stack)) {
			throw new IllegalStateException("Could not set item resource slot");
		}
	}

	@Override
	public ItemStack remove(int amount) {
		ItemStack current = getItem();
		if (current.isEmpty() || amount <= 0) {
			return ItemStack.EMPTY;
		}
		int removed = Math.min(amount, current.getCount());
		try (Transaction transaction = Transaction.openRoot()) {
			if (handler.extract(getSlotIndex(), ItemResource.of(current), removed, transaction) != removed) {
				return ItemStack.EMPTY;
			}
			transaction.commit();
		}
		return current.copyWithCount(removed);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return !stack.isEmpty() && handler.isValid(getSlotIndex(), ItemResource.of(stack));
	}

	@Override
	public boolean mayPickup(Player player) {
		ItemResource resource = handler.getResource(getSlotIndex());
		if (resource.isEmpty()) {
			return false;
		}
		try (Transaction transaction = Transaction.openRoot()) {
			return handler.extract(getSlotIndex(), resource, 1, transaction) == 1;
		}
	}

	@Override
	public int getMaxStackSize() {
		return handler.getCapacityAsInt(getSlotIndex(), ItemResource.EMPTY);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return handler.getCapacityAsInt(getSlotIndex(), ItemResource.of(stack));
	}

	@Override
	public void onQuickCraft(ItemStack oldStackIn, ItemStack newStackIn) {
		//Resource handlers perform the actual mutation through the transaction-aware insert/extract methods.
	}

	@Override
	public boolean isSameInventory(Slot other) {
		return other instanceof TransactionalResourceHandlerSlot slot && slot.handler == handler;
	}

	public ResourceHandler<ItemResource> getResourceHandler() {
		return handler;
	}
}
