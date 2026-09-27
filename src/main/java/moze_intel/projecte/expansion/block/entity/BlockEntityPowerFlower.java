package moze_intel.projecte.expansion.block.entity;

import moze_intel.projecte.expansion.block.BlockCompactSun;
import moze_intel.projecte.expansion.block.BlockPowerFlower;
import moze_intel.projecte.expansion.config.Config;
import moze_intel.projecte.expansion.registries.ExpansionBlockEntityTypes;
import moze_intel.projecte.expansion.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.math.BigInteger;
import java.util.Objects;

@SuppressWarnings("unused")
public class BlockEntityPowerFlower extends BlockEntityOwnable implements IHasMatter, IHasSunBonus, IEmcStorageBigInteger, IGeneratesEMC {
	public BigInteger emc = BigInteger.ZERO;
	public BlockEntityPowerFlower(BlockPos pos, BlockState state) {
		super(ExpansionBlockEntityTypes.POWER_FLOWER.get(), pos, state);
	}

	@Override
	@SuppressWarnings("unused")
	public void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		emc = new BigInteger(input.getStringOr(TagNames.STORED_EMC, "0"));
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putString(TagNames.STORED_EMC, emc.toString());
	}

	public static void tickServer(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (blockEntity instanceof BlockEntityPowerFlower be) be.tickServer(level, pos, state, be);
	}

	public void tickServer(Level level, BlockPos pos, BlockState state, BlockEntityPowerFlower blockEntity) {
		if (level.isClientSide() || (level.getGameTime() % Config.server.tickDelay.get()) != Util.mod(hashCode(), Config.server.tickDelay.get())) return;
		BigInteger res = getMatter().getPowerFlowerOutputForTicks(Config.server.tickDelay.get());
		if(hasSunBonus() && getSunBonus() != null) {
			res = res.multiply(BigInteger.valueOf(getSunBonus()));
		}
		ServerPlayer player = Util.getPlayer(level, owner);

		if (player != null) {
			PowerFlowerCollector.add(player, emc.add(res));
			emc = BigInteger.ZERO;
			markDirty();
		} else {
			emc = emc.add(res);
			markDirty();
		}
	}

	@Override
	public Matter getMatter() {
		return ((BlockPowerFlower) getBlockState().getBlock()).getMatter();
	}

	@Override
	public boolean hasSunBonus() {
		return BlockCompactSun.adjacent(level, worldPosition, Direction.DOWN);
	}

	@Override
	public BigInteger getStoredEmcBigInteger() {
		return emc;
	}

	@Override
	public BigInteger getMaximumEmcBigInteger() {
		BigInteger max = BigInteger.valueOf(Long.MAX_VALUE);
		if(hasSunBonus()) {
			max = max.multiply(BigInteger.valueOf(Objects.requireNonNull(getSunBonus())));
		}
		return max;
	}

	@Override
	public BigInteger extractEmcBigInteger(BigInteger toExtract, EmcAction action) {
		if (toExtract.compareTo(BigInteger.ZERO) < 0) {
			return insertEmcBigInteger(toExtract.negate(), action);
		}
		return BigInteger.ZERO;
	}

	@Override
	public BigInteger insertEmcBigInteger(BigInteger toAccept, EmcAction action) {
		if (toAccept.compareTo(BigInteger.ZERO) < 0) {
			return extractEmcBigInteger(toAccept.negate(), action);
		}
		return BigInteger.ZERO;
	}

	@Override
	public BigInteger getGeneratedEMC() {
		BigInteger generated = getMatter().getPowerFlowerOutput();
		if(hasSunBonus() && getSunBonus() != null) {
			generated = generated.multiply(BigInteger.valueOf(getSunBonus()));
		}
		return generated;
	}
}
