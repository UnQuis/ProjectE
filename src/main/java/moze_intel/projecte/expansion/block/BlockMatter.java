package moze_intel.projecte.expansion.block;

import moze_intel.projecte.expansion.util.IHasMatter;
import moze_intel.projecte.expansion.util.Matter;
import moze_intel.projecte.expansion.util.Util;
import moze_intel.projecte.gameObjs.IMatterType;
import moze_intel.projecte.gameObjs.blocks.IMatterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class BlockMatter extends Block implements IHasMatter, IMatterBlock {
	private final Matter matter;

	public BlockMatter(BlockBehaviour.Properties properties, Matter matter) {
		super(properties);
		this.matter = matter;
	}

	public static BlockBehaviour.Properties getProperties(BlockBehaviour.Properties properties, Matter matter) {
		return properties.strength(2_000_000, 6_000_000).requiresCorrectToolForDrops().lightLevel((state) -> Math.min(matter.ordinal(), 15));
	}

	@Override
	public Matter getMatter() {
		return matter;
	}

	@Override
	public IMatterType getMatterType() {
		return Util.getMatterForProjectE(getMatter());
	}

	@Override
	public MapColor getMapColor(BlockState state, BlockGetter level, BlockPos pos, MapColor defaultColor) {
		return matter.mapColor == null ? super.getMapColor(state, level, pos, defaultColor) : matter.mapColor.get();
	}
}
