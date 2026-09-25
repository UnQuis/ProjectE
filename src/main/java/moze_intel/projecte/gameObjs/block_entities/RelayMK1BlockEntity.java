package moze_intel.projecte.gameObjs.block_entities;

import moze_intel.projecte.api.block_entity.IRelay;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.gameObjs.EnumRelayTier;
import moze_intel.projecte.gameObjs.container.RelayMK1Container;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RelayMK1BlockEntity extends EmcBlockEntity implements MenuProvider, IRelay {

	public static final ICapabilityProvider<RelayMK1BlockEntity, @Nullable Direction, ResourceHandler<ItemResource>> INVENTORY_PROVIDER = (relay, side) -> {
		if (side == null) {
			return relay.joined;
		} else if (side.getAxis().isVertical()) {
			return relay.automationOutput;
		}
		return relay.automationInput;
	};

	private final CompactableStackHandler input;
	private final StackHandler output = new StackHandler(1);

	private final ResourceHandler<ItemResource> automationOutput;
	private final ResourceHandler<ItemResource> automationInput;
	private final ResourceHandler<ItemResource> joined;

	private final long chargeRate;

	private double bonusEMC;

	public RelayMK1BlockEntity(BlockPos pos, BlockState state) {
		this(PEBlockEntityTypes.RELAY, pos, state, 7, EnumRelayTier.MK1);
	}

	RelayMK1BlockEntity(BlockEntityTypeRegistryObject<? extends RelayMK1BlockEntity> type, BlockPos pos, BlockState state, int sizeInv, EnumRelayTier tier) {
		super(type, pos, state, tier.getStorage());
		this.chargeRate = tier.getChargeRate();
		input = new CompactableStackHandler(sizeInv) {
			@Override
			public boolean isValid(int index, ItemResource resource) {
				return super.isValid(index, resource) && SlotPredicates.RELAY_INV.test(resource.toStack(1));
			}
		};

		this.automationInput = new WrappedItemHandler(input, WrappedItemHandler.WriteMode.IN);
		this.automationOutput = new WrappedItemHandler(output, WrappedItemHandler.WriteMode.IN_OUT) {
			@Override
			public boolean isValid(int index, ItemResource resource) {
				return super.isValid(index, resource) && SlotPredicates.EMC_HOLDER.test(resource.toStack(1));
			}

			@Override
			public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
				ItemStack stack = ItemUtil.getStack(output, 0);
				IItemEmcHolder emcHolder = stack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
				if (emcHolder != null && emcHolder.getNeededEmc(stack) > 0) {
					return 0;
				}
				return super.extract(index, resource, amount, transaction);
			}
		};
		this.joined = new CombinedResourceHandler<>(automationInput, automationOutput);
	}

	@Override
	public boolean isRelay() {
		return true;
	}

	private ItemStack getCharging() {
		return output.getStackInSlot(0);
	}

	private ItemStack getBurn() {
		return input.getStackInSlot(0);
	}

	public ResourceHandler<ItemResource> getInput() {
		return input;
	}

	public ResourceHandler<ItemResource> getOutput() {
		return output;
	}

	@Override
	protected boolean emcAffectsComparators() {
		return true;
	}

	public static void tickServer(Level level, BlockPos pos, BlockState state, RelayMK1BlockEntity relay) {
		relay.sendToAllAcceptors(level, pos, relay.getAvailableCharge());
		relay.input.compact();
		ItemStack stack = relay.getBurn();
		if (!stack.isEmpty()) {
			IItemEmcHolder emcHolder = stack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
			if (emcHolder != null) {
				//Try to take emc from the stack in the burn slot and put it in the relay
				long simulatedVal = relay.forceInsertEmc(emcHolder.extractEmc(stack, relay.chargeRate, EmcAction.SIMULATE), EmcAction.SIMULATE);
				if (simulatedVal > 0) {
					relay.forceInsertEmc(emcHolder.extractEmc(stack, simulatedVal, EmcAction.EXECUTE), EmcAction.EXECUTE);
				}
			} else {
				long emcVal = IEMCProxy.INSTANCE.getSellValue(stack);
				if (emcVal > 0 && emcVal <= relay.getNeededEmc()) {
					relay.forceInsertEmc(emcVal, EmcAction.EXECUTE);
					relay.getBurn().shrink(1);
					relay.input.onContentsChanged(0);
				}
			}
		}
		if (relay.getStoredEmc() > 0) {
			ItemStack chargeable = relay.getCharging();
			IItemEmcHolder emcHolder = chargeable.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
			if (emcHolder != null) {
				long actualSent = emcHolder.insertEmc(chargeable, relay.getAvailableCharge(), EmcAction.EXECUTE);
				relay.forceExtractEmc(actualSent, EmcAction.EXECUTE);
			}
		}
		relay.updateComparators(level, pos);
	}

	private long getAvailableCharge() {
		return Math.min(chargeRate, getStoredEmc());
	}

	public double getItemChargeProportion() {
		ItemStack charging = getCharging();
		IItemEmcHolder emcHolder = charging.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
		if (emcHolder != null) {
			return (double) emcHolder.getStoredEmc(charging) / emcHolder.getMaximumEmc(charging);
		}
		return 0;
	}

	public double getInputBurnProportion() {
		ItemStack burn = getBurn();
		if (burn.isEmpty()) {
			return 0;
		}
		IItemEmcHolder emcHolder = burn.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
		if (emcHolder != null) {
			return (double) emcHolder.getStoredEmc(burn) / emcHolder.getMaximumEmc(burn);
		}
		return burn.getCount() / (double) burn.getMaxStackSize();
	}

	@Override
	public void loadAdditional(@NotNull ValueInput input) {
		super.loadAdditional(input);
		input.readChild("input", this.input);
		input.readChild("output", this.output);
		bonusEMC = input.getDoubleOr("bonus_emc", 0.0D);
	}

	@Override
	protected void saveAdditional(@NotNull ValueOutput output) {
		super.saveAdditional(output);
		output.putChild("input", this.input);
		output.putChild("output", this.output);
		output.putDouble("bonus_emc", bonusEMC);
	}

	@Override
	public double getBonusToAdd() {
		return 0.05;
	}

	@Override
	public void addBonus(@NotNull Level level, @NotNull BlockPos pos) {
		bonusEMC += getBonusToAdd();
		if (bonusEMC >= 1) {
			long emcToInsert = (long) bonusEMC;
			forceInsertEmc(emcToInsert, EmcAction.EXECUTE);
			//Don't subtract the actual amount we managed to insert so that we do not continue to grow to
			// an infinite amount of "bonus" emc if our buffer is full.
			bonusEMC -= emcToInsert;
		}
		markDirty(level, pos, false);
	}

	@NotNull
	@Override
	public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player) {
		return new RelayMK1Container(windowId, playerInventory, this);
	}

	@NotNull
	@Override
	public Component getDisplayName() {
		return PELang.GUI_RELAY_MK1.translate();
	}
}