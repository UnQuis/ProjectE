package moze_intel.projecte.gameObjs.block_entities;

import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class InterdictionLanternBlockEntity extends InterdictionBlockEntity {

	public InterdictionLanternBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@NotNull
	@Override
	public BlockEntityType<?> getType() {
		return PEBlockEntityTypes.INTERDICTION_LANTERN.get();
	}
}
