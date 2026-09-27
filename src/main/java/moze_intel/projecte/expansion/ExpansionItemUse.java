package moze_intel.projecte.expansion;

import moze_intel.projecte.expansion.block.entity.BlockEntityAdvancedAlchemicalChest;
import moze_intel.projecte.expansion.block.entity.BlockEntityNBTFilterable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Extra right click interactions that the expansion adds to ProjectE's items.
 * <p>
 * These are only ever called from ProjectE's items, and do nothing if the block that is being used does not
 * support the interaction, so ProjectE's items behave exactly like they normally do without the expansion.
 */
public final class ExpansionItemUse {

	private ExpansionItemUse() {}

	/**
	 * Lets an alchemical bag set the color of the advanced alchemical chest it is being used on.
	 *
	 * @return If the interaction was handled, in which case the bag should not open its own gui
	 */
	public static boolean trySetAdvancedChestColor(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide) {
			return false;
		}
		BlockEntity blockEntity = getTargetedBlockEntity(player);
		if (blockEntity instanceof BlockEntityAdvancedAlchemicalChest chest) {
			ItemStack stack = player.getItemInHand(hand);
			chest.handleItemActivation(player, stack);
			return true;
		}
		return false;
	}

	/**
	 * Lets the philosopher's stone toggle the NBT filter of the block it is being used on.
	 *
	 * @return If the interaction was handled, in which case the stone should not transmute anything
	 */
	public static boolean tryToggleNbtFilter(Level level, @Nullable Player player, BlockPos pos) {
		if (level.isClientSide || player == null) {
			return false;
		}
		if (level.getBlockEntity(pos) instanceof BlockEntityNBTFilterable filterable) {
			filterable.toggleFilter(player);
			return true;
		}
		return false;
	}

	@Nullable
	private static BlockEntity getTargetedBlockEntity(Player player) {
		HitResult hit = player.pick(20.0D, 0.0F, false);
		if (hit instanceof BlockHitResult blockHit) {
			BlockPos pos = blockHit.getBlockPos();
			return player.level().getBlockEntity(pos);
		}
		return null;
	}
}
