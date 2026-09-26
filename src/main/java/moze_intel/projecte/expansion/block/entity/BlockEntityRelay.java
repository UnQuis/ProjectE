package moze_intel.projecte.expansion.block.entity;

import moze_intel.projecte.expansion.block.BlockRelay;
import moze_intel.projecte.expansion.registries.ExpansionBlockEntityTypes;
import moze_intel.projecte.expansion.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.math.BigInteger;

@SuppressWarnings("unused")
public class BlockEntityRelay extends BlockEntityEMC implements IHasMatter, IRelayBigInteger {
	public Matter matter;
	public static final Direction[] DIRECTIONS = Direction.values();
	private BigInteger bonusEMC = BigInteger.ZERO;
	public BlockEntityRelay(BlockPos pos, BlockState state) {
		super(ExpansionBlockEntityTypes.RELAY.get(), pos, state);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		BlockEntityEMC.registerCapabilities(event, ExpansionBlockEntityTypes.RELAY.get());
	}

	public static void tickServer(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (blockEntity instanceof BlockEntityRelay be) be.tickServer(level, pos, state, be);
	}

	public void tickServer(Level level, BlockPos pos, BlockState state, BlockEntityRelay blockEntity) {
		// we can't use the user defined value due to emc duplication possibilities
		if ((level.getGameTime() % 20L) != Util.mod(hashCode(), 20)) return;

		sendToAllAcceptors(level, pos, getStoredEmcBigInteger().min(getMatter().getRelayTransfer()));
	}


	@Override
	public Matter getMatter() {
		BlockRelay block = (BlockRelay) getBlockState().getBlock();
		if (block.getMatter() != matter) {
			this.matter = block.getMatter();
		}
		return matter;
	}

	@Override
	public void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		bonusEMC = new BigInteger(input.getStringOr(TagNames.BONUS_EMC, "0"));
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putString(TagNames.BONUS_EMC, bonusEMC.toString());
	}

	@Override
	public boolean isRelay() {
		return true;
	}

	@Override
	public BigInteger getBonusToAddBigInteger() {
		return getMatter().getRelayBonus();
	}

	@Override
	public void addBonus(Level level, BlockPos pos) {
		bonusEMC = bonusEMC.add(getBonusToAddBigInteger());
		if (bonusEMC.compareTo(BigInteger.ONE) >= 0) {
			insertEmcBigInteger(bonusEMC, EmcAction.EXECUTE);
			bonusEMC = BigInteger.ZERO;
		}
		markDirty(level, pos);
	}
}
