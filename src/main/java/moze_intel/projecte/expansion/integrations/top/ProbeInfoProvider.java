package moze_intel.projecte.expansion.integrations.top;

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
 * 26.3 note: The One Probe has not been ported to 26.3, so this class can no longer implement
 * {@code mcjty.theoneprobe.api.IProbeInfoProvider} (nor {@code Function<ITheOneProbe, Void>}).
 * The tooltip building itself is unchanged and still lives in {@link Common}; only the The One Probe facing glue
 * is gone, and it can be restored without touching this file's logic once the API exists again.
 *
 * @see TOPIntegration
 */
@SuppressWarnings("unused")
public class ProbeInfoProvider {
	/**
	 * Feeds the addon's block tooltips into a sink, which is what a Waila/TOP style provider does.
	 */
	public void addProbeInfo(Consumer<Component> sink, IDataProvider provider) {
		Common.registerCommonTooltips(sink, provider);
	}

	public record DataProvider(Player player, Level level, BlockState state, HitData data) implements IDataProvider {
		@Override
		public BlockPos getBlockPos() {
			return data.getPos();
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
			return level.getBlockEntity(data.getPos());
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
			return data.getSideHit();
		}
	}

	/**
	 * The bits of {@code mcjty.theoneprobe.api.IProbeHitData} this class actually used, kept so the record above
	 * keeps its shape without the missing dependency.
	 */
	public interface HitData {
		BlockPos getPos();

		Direction getSideHit();
	}
}
