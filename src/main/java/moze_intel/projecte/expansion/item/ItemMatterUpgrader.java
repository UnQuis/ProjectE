package moze_intel.projecte.expansion.item;

import moze_intel.projecte.expansion.block.entity.*;
import moze_intel.projecte.expansion.util.*;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.function.Consumer;
import java.util.Objects;

public class ItemMatterUpgrader extends Item {
	@SuppressWarnings("unused")
	public ItemMatterUpgrader(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, tooltip, flag);
		tooltip.accept(Lang.Items.MATTER_UPGRADER_TOOLTIP.translateColored(ChatFormatting.GRAY));
		tooltip.accept(Lang.Items.MATTER_UPGRADER_TOOLTIP2.translateColored(ChatFormatting.GREEN));
		tooltip.accept(Lang.Items.MATTER_UPGRADER_TOOLTIP_CREATIVE.translateColored(ChatFormatting.RED));
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		@Nullable Player player = context.getPlayer();
		BlockPos pos = context.getClickedPos();
		Level level = context.getLevel();

		if (level.isClientSide() || player == null) return InteractionResult.PASS;

		BlockEntity blockEntity = level.getBlockEntity(pos);
		Block block = level.getBlockState(pos).getBlock();

		Matter matter;
		Matter upgradeTo;
		if (block instanceof IHasMatter) {
			matter = ((IHasMatter) block).getMatter();
			upgradeTo = matter.next();
		} else return InteractionResult.PASS;

		if (matter == Matter.FINAL) {
			player.sendOverlayMessage(Lang.Items.MATTER_UPGRADER_MAX_UPGRADE.translateColored(ChatFormatting.RED));
			return InteractionResult.FAIL;
		}


		@Nullable IKnowledgeProvider provider = Util.getKnowledgeProvider(player);
		if(provider == null) {
			player.sendOverlayMessage(Lang.FAILED_TO_GET_KNOWLEDGE_PROVIDER.translateColored(ChatFormatting.RED, player.getDisplayName()));
			return InteractionResult.FAIL;
		}
		IEMCProxy proxy = IEMCProxy.INSTANCE;

		@Nullable BlockItem upgrade = null;
		@Nullable Block upgradeBlock = null;
		@Nullable BlockEntity newBlockEntity = null;
		@Nullable BlockState newBlockState = null;

		if (blockEntity instanceof BlockEntityCollector) {
			upgrade = Objects.requireNonNull(upgradeTo.getCollectorItem());
			upgradeBlock = Objects.requireNonNull(upgradeTo.getCollector());
		}

		if (blockEntity instanceof BlockEntityPowerFlower be) {
			upgrade = Objects.requireNonNull(upgradeTo.getPowerFlowerItem());
			upgradeBlock = Objects.requireNonNull(upgradeTo.getPowerFlower());
			if (be.owner == null) return InteractionResult.FAIL;
			if (!be.owner.equals(player.getUUID())) {
				player.sendOverlayMessage(Lang.Items.MATTER_UPGRADER_NOT_OWNER.translateColored(ChatFormatting.RED));
				return InteractionResult.FAIL;
			}

			BlockEntityPowerFlower intBlockEntity = new BlockEntityPowerFlower(pos, newBlockState = upgradeBlock.defaultBlockState());
			intBlockEntity.owner = be.owner;
			intBlockEntity.ownerName = be.ownerName;
			intBlockEntity.emc = be.emc;
			newBlockEntity = intBlockEntity;
		}

		if (blockEntity instanceof BlockEntityEMCLink be) {
			upgrade = Objects.requireNonNull(upgradeTo.getEMCLinkItem());
			upgradeBlock = Objects.requireNonNull(upgradeTo.getEMCLink());
			if (be.owner == null) return InteractionResult.FAIL;
			if (!be.owner.equals(player.getUUID())) {
				player.sendOverlayMessage(Lang.Items.MATTER_UPGRADER_NOT_OWNER.translateColored(ChatFormatting.RED));
				return InteractionResult.FAIL;
			}

			BlockEntityEMCLink intBlockEntity = new BlockEntityEMCLink(pos, newBlockState = upgradeBlock.defaultBlockState().setValue(BlockEntityNBTFilterable.FILTER, be.getBlockState().getValue(BlockEntityNBTFilterable.FILTER)));
			intBlockEntity.owner = be.owner;
			intBlockEntity.ownerName = be.ownerName;
			intBlockEntity.emc = be.emc;
			intBlockEntity.itemStack = be.itemStack;
			intBlockEntity.remainingEMC = be.remainingEMC;
			intBlockEntity.remainingImport = be.remainingImport;
			intBlockEntity.remainingExport = be.remainingExport;
			intBlockEntity.remainingFluid = be.remainingFluid;
			newBlockEntity = intBlockEntity;
		}

		if (blockEntity instanceof BlockEntityRelay) {
			upgrade = Objects.requireNonNull(upgradeTo.getRelayItem());
			upgradeBlock = Objects.requireNonNull(upgradeTo.getRelay());
		}

		if (upgrade == null || !provider.hasKnowledge(ItemInfo.fromItem(upgrade)) && !player.isCreative()) {
			player.sendOverlayMessage(Lang.Items.MATTER_UPGRADER_NOT_LEARNED.translateColored(ChatFormatting.RED, Component.translatable(Objects.requireNonNull(upgrade).toString())));
			return InteractionResult.FAIL;
		}

		long prevValue = proxy.getValue(block);
		long emcValue = proxy.getValue(Objects.requireNonNull(upgrade));
		long diff = emcValue - prevValue;
		if (player.isCreative()) diff = 0;
		BigInteger newEmc = provider.getEmc().subtract(BigInteger.valueOf(diff));
		if (newEmc.compareTo(BigInteger.ZERO) < 0) {
			player.sendOverlayMessage(Lang.Items.MATTER_UPGRADER_NOT_ENOUGH_EMC.translateColored(ChatFormatting.RED, EMCFormat.format(BigInteger.valueOf(diff))));
			return InteractionResult.FAIL;
		}

		if(newBlockState == null) {
			newBlockState = upgradeBlock.defaultBlockState();
		}

		level.removeBlock(pos, false);
		level.setBlockAndUpdate(pos, newBlockState);

		if(newBlockEntity != null) {
			level.removeBlockEntity(pos);
			level.setBlockEntity(newBlockEntity);
			Util.markDirty(level, pos);
		}

		provider.setEmc(newEmc);
		player.sendOverlayMessage(Lang.Items.MATTER_UPGRADER_DONE.translateColored(ChatFormatting.WHITE, EMCFormat.format(BigInteger.valueOf(diff))));
		return InteractionResult.SUCCESS;
	}
}
