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
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.utils.PEKeybind;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import org.jetbrains.annotations.Nullable;

/**
 * The Alchemical Collection enchantment, which converts the EMC value of mined blocks into EMC and knowledge
 * instead of dropping them.
 * <p>
 * The conversion is done with ProjectE's EMC values, so only things that have an EMC value can be collected,
 * and the amount of EMC given is the sell value of the block that would have dropped.
 */
@EventBusSubscriber(modid = PECore.MODID)
public final class ExpansionAlchemicalCollection {

	private ExpansionAlchemicalCollection() {}

	/**
	 * Converts the drops of a block that was mined with an active Alchemical Collection enchanted tool.
	 * <p>
	 * Note: This is only called for blocks that are actually broken by a player, and the drops that have an
	 * EMC value are removed from the list of drops before they are added to the world.
	 */
	@SubscribeEvent
	public static void onBlockDrops(BlockDropsEvent event) {
		if (!(event.getBreaker() instanceof ServerPlayer player)) {
			return;
		}
		ItemStack tool = event.getTool();
		if (tool.isEmpty() || !tool.getOrDefault(PEDataComponentTypes.ACTIVE, false)) {
			return;
		}
		Holder<Enchantment> collection = getAlchemicalCollection(player);
		if (collection == null || EnchantmentHelper.getTagEnchantmentLevel(collection, tool) <= 0) {
			return;
		}
		IKnowledgeProvider provider = Util.getKnowledgeProvider(player);
		if (provider == null || !event.getState().canHarvestBlock(event.getLevel(), event.getPos(), player)) {
			return;
		}
		AtomicBigInteger emc = new AtomicBigInteger();
		List<ItemInfo> knowledge = new ArrayList<>();
		for (Iterator<ItemEntity> iterator = event.getDrops().iterator(); iterator.hasNext(); ) {
			ItemStack drop = iterator.next().getItem();
			ItemInfo info = IEMCProxy.INSTANCE.getPersistentInfo(ItemInfo.fromStack(drop));
			if (IEMCProxy.INSTANCE.hasValue(info)) {
				emc.addAndGet(BigInteger.valueOf(IEMCProxy.INSTANCE.getSellValue(info)).multiply(BigInteger.valueOf(drop.getCount())));
				if (!provider.hasKnowledge(info) && !knowledge.contains(info)) {
					knowledge.add(info);
				}
				iterator.remove();
			}
		}
		if (emc.get().signum() > 0 || !knowledge.isEmpty()) {
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
		Holder<Enchantment> collection = getAlchemicalCollection(player);
		if (collection == null) {
			return;
		}
		for (InteractionHand hand : InteractionHand.values()) {
			ItemStack stack = player.getItemInHand(hand);
			if (EnchantmentHelper.getTagEnchantmentLevel(collection, stack) > 0) {
				stack.set(PEDataComponentTypes.ACTIVE, !stack.getOrDefault(PEDataComponentTypes.ACTIVE, false));
			}
		}
	}

	@Nullable
	private static Holder<Enchantment> getAlchemicalCollection(Player player) {
		return player.registryAccess().holderOrThrow(ExpansionEnchantments.ALCHEMICAL_COLLECTION);
	}
}
