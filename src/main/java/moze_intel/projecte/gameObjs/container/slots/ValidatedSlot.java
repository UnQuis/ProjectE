package moze_intel.projecte.gameObjs.container.slots;

import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;

// Partial copy of ResourceHandlerSlot with a validator
public class ValidatedSlot extends InventoryContainerSlot {

	private final Predicate<ItemStack> validator;

	public ValidatedSlot(ResourceHandler<ItemResource> itemHandler, int index, int xPosition, int yPosition, Predicate<ItemStack> validator) {
		super(itemHandler, index, xPosition, yPosition);
		this.validator = validator;
	}

	@Override
	public boolean mayPlace(@NotNull ItemStack stack) {
		return super.mayPlace(stack) && validator.test(stack);
	}
}
