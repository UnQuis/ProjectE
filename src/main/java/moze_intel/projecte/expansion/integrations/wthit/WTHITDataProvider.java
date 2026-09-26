package moze_intel.projecte.expansion.integrations.wthit;

import java.util.function.Consumer;
import moze_intel.projecte.expansion.integrations.Common;
import moze_intel.projecte.expansion.integrations.IDataProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 26.3 note: WTHIT has not been ported to 26.3 (the newest {@code wthit-api} on the WTHIT maven is still the 1.21.1
 * one) and {@code build.gradle} no longer has the {@code mcp.mobius.waila} dependency, so this class can no longer
 * implement {@code mcp.mobius.waila.api.IBlockComponentProvider} nor read an {@code IBlockAccessor}. The tooltip
 * logic is unchanged and still goes through {@link Common}; only the WTHIT facing glue is gone, so the file also
 * keeps a {@link DataProvider} that takes the plain values instead.
 *
 * @see WTHITPlugin
 */
@SuppressWarnings("unused")
public class WTHITDataProvider {

	/**
	 * The body tooltip, which is exactly what the old {@code appendBody} fed to WTHIT.
	 */
	public void appendBody(Consumer<Component> sink, DataProvider accessor) {
		Common.registerCommonTooltips(sink, accessor);
	}

	@SuppressWarnings("unused")
	public record DataProvider(BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Level level, Player player, Direction side) implements IDataProvider {
		@Override
		public BlockPos getBlockPos() {
			return pos;
		}

		@Override
		public Block getBlock() {
			return state.getBlock();
		}

		@Override
		public BlockState getBlockState() {
			return state;
		}

		@Override
		public @Nullable BlockEntity getBlockEntity() {
			return blockEntity;
		}

		@Override
		public Level getLevel() {
			return level;
		}

		@Override
		public Player getPlayer() {
			return player;
		}

		@Override
		public Direction getSide() {
			return side;
		}
	}
}
