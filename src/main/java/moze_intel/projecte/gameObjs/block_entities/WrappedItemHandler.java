package moze_intel.projecte.gameObjs.block_entities;

import java.util.Objects;
import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Ограничивает направление перемещений ресурсов, сохраняя остальную семантику делегируемого обработчика.
 */
public class WrappedItemHandler extends DelegatingResourceHandler<ItemResource> {

	private final WriteMode mode;

	public WrappedItemHandler(ResourceHandler<ItemResource> delegate, WriteMode mode) {
		super(delegate);
		this.mode = mode;
	}

	@Override
	public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
		Objects.checkIndex(index, size());
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
		return mode == WriteMode.IN || mode == WriteMode.IN_OUT ? super.insert(index, resource, amount, transaction) : 0;
	}

	@Override
	public int insert(ItemResource resource, int amount, TransactionContext transaction) {
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
		int inserted = 0;
		for (int index = 0; index < size() && inserted < amount; index++) {
			inserted += insert(index, resource, amount - inserted, transaction);
		}
		return inserted;
	}

	@Override
	public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
		Objects.checkIndex(index, size());
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
		return mode == WriteMode.OUT || mode == WriteMode.IN_OUT ? super.extract(index, resource, amount, transaction) : 0;
	}

	@Override
	public int extract(ItemResource resource, int amount, TransactionContext transaction) {
		TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
		int extracted = 0;
		for (int index = 0; index < size() && extracted < amount; index++) {
			extracted += extract(index, resource, amount - extracted, transaction);
		}
		return extracted;
	}

	public enum WriteMode {
		IN,
		OUT,
		IN_OUT,
		NONE
	}
}
