package moze_intel.projecte.expansion.item;

import moze_intel.projecte.expansion.config.Config;
import moze_intel.projecte.expansion.util.ColorStyle;
import moze_intel.projecte.expansion.util.EMCFormat;
import moze_intel.projecte.expansion.util.Lang;
import moze_intel.projecte.expansion.util.Util;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.function.Consumer;

public class ItemInfiniteSteak extends Item {
	@SuppressWarnings("unused")
	public ItemInfiniteSteak(Properties properties) {
		super(properties
				.food(Foods.COOKED_BEEF)
				.stacksTo(1)
				.rarity(Rarity.RARE));
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, tooltip, flag);
		tooltip.accept(Lang.Items.INFINITE_STEAK_TOOLTIP.translateColored(ChatFormatting.GRAY));
		tooltip.accept(Lang.COST.translateColored(ChatFormatting.RED, EMCFormat.getComponent(Config.server.infiniteSteakCost.get()).setStyle(ColorStyle.GRAY)));
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return Items.COOKED_BEEF.getUseDuration(stack, entity);
	}

	//26.3 Item#use returns an InteractionResult instead of an InteractionResultHolder<ItemStack>
	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		@Nullable IKnowledgeProvider provider = Util.getKnowledgeProvider(player);
		if (!player.canEat(false) || Config.server.infiniteSteakCost.get() == 0 || provider == null || provider.getEmc().compareTo(BigInteger.valueOf(Config.server.infiniteSteakCost.get())) < 0) return InteractionResult.FAIL;
		player.startUsingItem(hand);
		return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
	}

	//26.3 replaced LivingEntity#eat with the minecraft:consumable data component, super#finishUsingItem runs it
	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (!(entity instanceof ServerPlayer player) || level.isClientSide()) return stack;
		@Nullable IKnowledgeProvider provider = Util.getKnowledgeProvider(player);
		if (provider == null) {
			player.sendOverlayMessage(Lang.FAILED_TO_GET_KNOWLEDGE_PROVIDER.translateColored(ChatFormatting.RED, player.getDisplayName()));
			return stack;
		}
		BigInteger emc = provider.getEmc().subtract(BigInteger.valueOf(Config.server.infiniteSteakCost.get()));
		if (emc.compareTo(BigInteger.ZERO) < 0) {
			player.sendOverlayMessage(Lang.Items.INFINITE_STEAK_NOT_ENOUGH_EMC.translateColored(ChatFormatting.RED, Component.literal(Integer.toString(Config.server.infiniteSteakCost.get()))));
			return stack;
		}
		provider.setEmc(emc);
		provider.syncEmc(player);
		return super.finishUsingItem(stack, level, entity);
	}
}
