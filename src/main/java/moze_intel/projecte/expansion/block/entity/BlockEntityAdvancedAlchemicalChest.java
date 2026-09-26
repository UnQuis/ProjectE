package moze_intel.projecte.expansion.block.entity;

import moze_intel.projecte.expansion.block.BlockAdvancedAlchemicalChest;
import moze_intel.projecte.expansion.gui.container.ContainerAdvancedAlchemicalChest;
import moze_intel.projecte.expansion.registries.ExpansionBlockEntityTypes;
import moze_intel.projecte.expansion.util.*;
import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.gameObjs.items.AlchemicalBag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

public class BlockEntityAdvancedAlchemicalChest extends BlockEntityOwnable implements IChestLike, IHasColor {
	public static final ICapabilityProvider<BlockEntityAdvancedAlchemicalChest, @Nullable Direction, ResourceHandler<ItemResource>> ITEM_HANDLER_CAPABILITY = (be, side) -> be.getBagItemHandler();
	private final ChestLidController lidController = new ChestLidController();

	private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
		@Override
		protected void onOpen(Level level, BlockPos pos, BlockState state) {
			level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.5F,
				level.getRandom().nextFloat() * 0.1F + 0.9F);
		}

		@Override
		protected void onClose(Level level, BlockPos pos, BlockState state) {
			level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.5F,
				level.getRandom().nextFloat() * 0.1F + 0.9F);
		}

		@Override
		protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int oldCount, int openCount) {
			level.blockEvent(pos, state.getBlock(), 1, openCount);
		}

		@Override
		public boolean isOwnContainer(Player player) {
			return player.containerMenu instanceof ContainerAdvancedAlchemicalChest chest && chest.blockEntityMatches(BlockEntityAdvancedAlchemicalChest.this);
		}
	};

	//26.3 replaced IItemHandler with ResourceHandler<ItemResource>
	private class BagItemHandler implements ResourceHandler<ItemResource> {
		@Override
		public int size() {
			ResourceHandler<ItemResource> bag = getBag();
			return bag == null ? 0 : bag.size();
		}

		@Override
		public ItemResource getResource(int index) {
			ResourceHandler<ItemResource> bag = getBag();
			return bag == null ? ItemResource.EMPTY : bag.getResource(index);
		}

		@Override
		public long getAmountAsLong(int index) {
			ResourceHandler<ItemResource> bag = getBag();
			return bag == null ? 0 : bag.getAmountAsLong(index);
		}

		@Override
		public long getCapacityAsLong(int index, ItemResource resource) {
			ResourceHandler<ItemResource> bag = getBag();
			return bag == null ? 0 : bag.getCapacityAsLong(index, resource);
		}

		@Override
		public boolean isValid(int index, ItemResource resource) {
			ResourceHandler<ItemResource> bag = getBag();
			return bag != null && bag.isValid(index, resource);
		}

		@Override
		public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
			ResourceHandler<ItemResource> bag = getBag();
			return bag == null ? 0 : bag.insert(index, resource, amount, transaction);
		}

		@Override
		public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
			ResourceHandler<ItemResource> bag = getBag();
			return bag == null ? 0 : bag.extract(index, resource, amount, transaction);
		}
	}

	private DyeColor color;
	public BlockEntityAdvancedAlchemicalChest(BlockPos pos, BlockState state) {
		super(ExpansionBlockEntityTypes.ADVANCED_ALCHEMICAL_CHEST.get(), pos, state);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.Item.BLOCK, ExpansionBlockEntityTypes.ADVANCED_ALCHEMICAL_CHEST.get(), ITEM_HANDLER_CAPABILITY);
	}

	BagItemHandler getBagItemHandler() {
		return new BagItemHandler();
	}

	@Override
	public DyeColor getColor() {
		BlockAdvancedAlchemicalChest block = (BlockAdvancedAlchemicalChest) getBlockState().getBlock();
		if (block.getColor() != color) {
			this.color = block.getColor();
		}
		return color;
	}

	public @Nullable ResourceHandler<ItemResource> getBag() {
		@Nullable ServerPlayer player = Util.getPlayer(level, owner);
		if(player == null) {
			return null;
		}

		@Nullable IAlchBagProvider provider = player.getCapability(PECapabilities.ALCH_BAG_CAPABILITY);
		if (provider == null) return null;
		return provider.getBag(getColor());
	}

	//26.3 has no ItemInteractionResult any more, useItemOn returns a plain InteractionResult
	public InteractionResult handleItemActivation(Player player, ItemStack stack) {
		if(!super.handleActivation(player, BlockEntityOwnable.ActivationType.CHECK_OWNERSHIP)) {
			return InteractionResult.FAIL;
		}

		if(stack.isEmpty()) {
			player.sendOverlayMessage(Lang.Blocks.ADVANCED_ALCHEMICAL_CHEST_INVALID_ITEM.translate());
			return InteractionResult.FAIL;
		}

		if(stack.getItem() instanceof AlchemicalBag bag) {
			if(level != null) {
				BlockAdvancedAlchemicalChest block = AdvancedAlchemicalChest.getBlock(bag.color);

				BlockEntityAdvancedAlchemicalChest newBlockEntity = new BlockEntityAdvancedAlchemicalChest(worldPosition, getBlockState());
				newBlockEntity.owner = owner;
				newBlockEntity.ownerName = ownerName;
				level.setBlockAndUpdate(worldPosition, block.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)).setValue(BlockStateProperties.WATERLOGGED, getBlockState().getValue(BlockStateProperties.WATERLOGGED)));
				level.removeBlockEntity(worldPosition);
				level.setBlockEntity(newBlockEntity);
				Util.markDirty(level, worldPosition);
			}
			player.sendOverlayMessage(Lang.Blocks.ADVANCED_ALCHEMICAL_CHEST_COLOR_SET.translate(bag.color.getName()));
		} else {
			player.sendOverlayMessage(Lang.Blocks.ADVANCED_ALCHEMICAL_CHEST_INVALID_ITEM.translate());
		}

		return InteractionResult.CONSUME;
	}

	@SuppressWarnings("unused")
	public static void tickClient(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if(blockEntity instanceof BlockEntityAdvancedAlchemicalChest be) {
			be.lidController.tickLid();
		}
	}

	@Override
	public boolean triggerEvent(int id, int type) {
		if (id == 1) {
			lidController.shouldBeOpen(type > 0);
			return true;
		}
		return super.triggerEvent(id, type);
	}

	public void startOpen(Player player) {
		if (!isRemoved() && !player.isSpectator() && level != null) {
			//26.3 incrementOpeners additionally takes the maximum interaction range
			openersCounter.incrementOpeners(player, level, getBlockPos(), getBlockState(), player.getContainerInteractionRange());
		}
	}

	public void stopOpen(Player player) {
		if (!isRemoved() && !player.isSpectator() && level != null) {
			openersCounter.decrementOpeners(player, level, getBlockPos(), getBlockState());
		}
	}

	public void recheckOpen() {
		if (!isRemoved() && level != null) {
			openersCounter.recheckOpeners(level, getBlockPos(), getBlockState());
		}
	}

	@Override
	public float getOpenNess(float partialTicks) {
		return lidController.getOpenness(partialTicks);
	}
}
