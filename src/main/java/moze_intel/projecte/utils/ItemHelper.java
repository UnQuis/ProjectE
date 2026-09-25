package moze_intel.projecte.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

/**
 * Helpers for Inventories, ItemStacks, Items, and the Ore Dictionary Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class ItemHelper {

	private ItemHelper() {
	}

	/**
	 * Gets an ActionResult based on a type
	 */
	public static InteractionResult actionResultFromType(InteractionResult type, ItemStack stack) {
		return type;
	}

	/**
	 * Compacts an inventory and returns if the inventory is/was empty.
	 *
	 * @return True if the inventory was empty.
	 */
	public static boolean compactInventory(ResourceHandler<ItemResource> inventory) {
		List<ItemStack> temp = new ArrayList<>();
		try (Transaction transaction = Transaction.openRoot()) {
			for (int i = 0, slots = inventory.size(); i < slots; i++) {
				ItemStack stack = ItemUtil.getStack(inventory, i);
				if (!stack.isEmpty()) {
					temp.add(stack);
					int extracted = inventory.extract(i, ItemResource.of(stack), stack.getCount(), transaction);
					if (extracted != stack.getCount()) {
						throw new IllegalStateException("Could not extract the full stack while compacting an inventory");
					}
				}
			}
			for (ItemStack stack : temp) {
				int inserted = ResourceHandlerUtil.insertStacking(inventory, ItemResource.of(stack), stack.getCount(), transaction);
				if (inserted != stack.getCount()) {
					throw new IllegalStateException("Could not reinsert the full stack while compacting an inventory");
				}
			}
			transaction.commit();
		}
		return temp.isEmpty();
	}

	public static ResourceHandler<ItemResource> immutableCopy(ResourceHandler<ItemResource> toCopy) {
		return new DelegatingResourceHandler<>(toCopy) {
			@Override
			public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
				Objects.checkIndex(index, size());
				TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
				return 0;
			}

			@Override
			public int insert(ItemResource resource, int amount, TransactionContext transaction) {
				TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
				return 0;
			}

			@Override
			public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
				Objects.checkIndex(index, size());
				TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
				return 0;
			}

			@Override
			public int extract(ItemResource resource, int amount, TransactionContext transaction) {
				TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
				return 0;
			}
		};
	}

	public static ItemStack insertItemStacked(@Nullable ResourceHandler<ItemResource> handler, ItemStack stack) {
		if (stack.isEmpty() || handler == null) {
			return stack;
		}
		int inserted = ResourceHandlerUtil.insertStacking(handler, ItemResource.of(stack), stack.getCount(), null);
		int remaining = stack.getCount() - inserted;
		return remaining == 0 ? ItemStack.EMPTY : stack.copyWithCount(remaining);
	}

	public static ItemStack extractItem(ResourceHandler<ItemResource> handler, int index, int amount, boolean simulate) {
		ItemStack current = ItemUtil.getStack(handler, index);
		if (current.isEmpty() || amount <= 0) {
			return ItemStack.EMPTY;
		}
		try (Transaction transaction = Transaction.openRoot()) {
			int extracted = handler.extract(index, ItemResource.of(current), Math.min(amount, current.getCount()), transaction);
			if (!simulate && extracted > 0) {
				transaction.commit();
			}
			return extracted == 0 ? ItemStack.EMPTY : current.copyWithCount(extracted);
		}
	}

	/**
	 * Транзакционно заменяет содержимое одного слота.
	 *
	 * @return {@code true}, если новое содержимое было полностью записано
	 */
	public static boolean setStack(ResourceHandler<ItemResource> handler, int index, ItemStack stack) {
		return setStack(handler, index, stack, null);
	}

	public static boolean setStack(ResourceHandler<ItemResource> handler, int index, ItemStack stack, @Nullable TransactionContext parent) {
		Objects.checkIndex(index, handler.size());
		ItemStack current = ItemUtil.getStack(handler, index);
		if (ItemStack.matches(current, stack)) {
			return true;
		}
		ItemResource currentResource = handler.getResource(index);
		int currentAmount = handler.getAmountAsInt(index);
		ItemResource newResource = ItemResource.of(stack);
		int newAmount = stack.getCount();
		try (Transaction transaction = Transaction.open(parent)) {
			if (handler.extract(index, currentResource, currentAmount, transaction) != currentAmount) {
				return false;
			}
			if (newAmount > 0 && handler.insert(index, newResource, newAmount, transaction) != newAmount) {
				return false;
			}
			transaction.commit();
			return true;
		}
	}

	public static boolean isRepairableDamagedItem(ItemStack stack) {
		//MC 26.1 removed ItemStack#isRepairable; repairability is now expressed via the REPAIRABLE
		// data component (repair with a matching material in an anvil) or combine-repairing with a copy
		return stack.isDamageableItem() && stack.getDamageValue() > 0 &&
				(stack.has(DataComponents.REPAIRABLE) || stack.getItem().isCombineRepairable(stack));
	}

	/**
	 * @return The amount of the given stack that could not fit. If it all fit, zero is returned
	 */
	public static int simulateFit(NonNullList<ItemStack> inv, ItemStack stack) {
		int remainder = stack.getCount();
		for (ItemStack invStack : inv) {
			if (invStack.isEmpty()) {
				//Slot is empty, just put it all there
				return 0;
			}
			if (ItemStack.isSameItemSameComponents(stack, invStack)) {
				int amountSlotNeeds = invStack.getMaxStackSize() - invStack.getCount();
				//Double check we don't have an over sized stack
				if (amountSlotNeeds > 0) {
					if (remainder <= amountSlotNeeds) {
						//If the slot can accept it all, return it all fit
						return 0;
					}
					//Otherwise take that many items out and
					remainder -= amountSlotNeeds;
				}
			}
		}
		return remainder;
	}

	public static ItemStack size(ItemStack stack, int size) {
		if (size <= 0 || stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		return stack.copyWithCount(size);
	}

	public static BlockState stackToState(ItemStack stack, @Nullable BlockPlaceContext context) {
		if (stack.getItem() instanceof BlockItem blockItem) {
			if (context == null) {
				return blockItem.getBlock().defaultBlockState();
			}
			return blockItem.getBlock().getStateForPlacement(context);
		}
		return null;
	}
}
