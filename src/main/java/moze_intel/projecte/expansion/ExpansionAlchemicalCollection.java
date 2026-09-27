package moze_intel.projecte.expansion;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.expansion.registries.ExpansionEnchantments;
import moze_intel.projecte.expansion.util.AlchemicalCollectionCollector;
import moze_intel.projecte.expansion.util.AtomicBigInteger;
import moze_intel.projecte.expansion.util.Util;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.utils.PEKeybind;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

/**
 * The Alchemical Collection enchantment, which converts the EMC value of mined blocks into EMC and knowledge
 * instead of dropping them.
 * <p>
 * The conversion is done with ProjectE's EMC values, so only things that have an EMC value can be collected,
 * and the amount of EMC given is the sell value of the block that would have dropped.
 */
@Mod.EventBusSubscriber(modid = PECore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ExpansionAlchemicalCollection {

	private ExpansionAlchemicalCollection() {}

	/**
	 * Converts the drops of a block that was mined with an active Alchemical Collection enchanted tool.
	 * <p>
	 * Note: 1.20.4 has no block drops event, so the break is cancelled and the block removed by hand, which means
	 * the drops have to be calculated here before they are converted into EMC and knowledge.
	 */
	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		if (!(event.getPlayer() instanceof ServerPlayer player)) {
			return;
		}
		ItemStack tool = player.getMainHandItem();
		if (tool.isEmpty() || !tool.getData(PEAttachmentTypes.ACTIVE)) {
			return;
		}
		if (EnchantmentHelper.getTagEnchantmentLevel(ExpansionEnchantments.ALCHEMICAL_COLLECTION.get(), tool) <= 0) {
			return;
		}
		IKnowledgeProvider provider = Util.getKnowledgeProvider(player);
		BlockState state = event.getState();
		ServerLevel level = (ServerLevel) event.getLevel();
		BlockPos pos = event.getPos();
		if (provider == null || !state.canHarvestBlock(level, pos, player)) {
			return;
		}
		AtomicBigInteger emc = new AtomicBigInteger();
		List<ItemInfo> knowledge = new ArrayList<>();
		BlockEntity blockEntity = level.getBlockEntity(pos);
		for (ItemStack drop : Block.getDrops(state, (ServerLevel) level, pos, blockEntity, player, tool)) {
			ItemInfo info = IEMCProxy.INSTANCE.getPersistentInfo(ItemInfo.fromStack(drop));
			if (IEMCProxy.INSTANCE.hasValue(info)) {
				emc.addAndGet(BigInteger.valueOf(IEMCProxy.INSTANCE.getSellValue(info)).multiply(BigInteger.valueOf(drop.getCount())));
				if (!provider.hasKnowledge(info) && !knowledge.contains(info)) {
					knowledge.add(info);
				}
			}
		}
		if (emc.get().signum() > 0 || !knowledge.isEmpty()) {
			//Only swallow the drops if there is something to collect, otherwise the block would just vanish.
			//The break itself is cancelled and the block is removed with the drop suppression flag, which is what
			//1.20.4 offers instead of the new state of the event that 1.21 has
			event.setCanceled(true);
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
			AlchemicalCollectionCollector.add(player.getUUID(), emc.get(), knowledge);
		}
	}

	/**
	 * Toggles the Alchemical Collection enchantment of the tools the player is holding when the extra function
	 * keybind is pressed.
	 */
	public static void handleExtraFunctionKey(Player player, PEKeybind key) {
		if (key != PEKeybind.EXTRA_FUNCTION) {
			return;
		}
		for (InteractionHand hand : InteractionHand.values()) {
			ItemStack stack = player.getItemInHand(hand);
			if (EnchantmentHelper.getTagEnchantmentLevel(ExpansionEnchantments.ALCHEMICAL_COLLECTION.get(), stack) > 0) {
				stack.setData(PEAttachmentTypes.ACTIVE, !stack.getData(PEAttachmentTypes.ACTIVE));
			}
		}
	}
}
