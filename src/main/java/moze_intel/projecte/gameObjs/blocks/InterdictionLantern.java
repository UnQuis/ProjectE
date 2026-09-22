package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.block_entities.InterdictionLanternBlockEntity;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InterdictionLantern extends LanternBlock implements PEEntityBlock<InterdictionLanternBlockEntity> {

	public InterdictionLantern(Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public BlockEntityTypeRegistryObject<InterdictionLanternBlockEntity> getType() {
		return PEBlockEntityTypes.INTERDICTION_LANTERN;
	}

	@Override
	@Deprecated
	public boolean triggerEvent(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, int id, int param) {
		super.triggerEvent(state, level, pos, id, param);
		return triggerBlockEntityEvent(state, level, pos, id, param);
	}
}
