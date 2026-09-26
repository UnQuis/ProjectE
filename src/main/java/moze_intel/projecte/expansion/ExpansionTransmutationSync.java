package moze_intel.projecte.expansion;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider.TargetUpdateType;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.event.EMCRemapEvent;
import moze_intel.projecte.api.event.PlayerKnowledgeChangeEvent;
import moze_intel.projecte.expansion.gui.container.ContainerArcaneTransmutationTablet;
import moze_intel.projecte.expansion.net.packets.to_client.ClearKnowledgePacket;
import moze_intel.projecte.expansion.net.packets.to_client.UpdateTransmutationTargetsPacket;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

/**
 * Keeps the client side targets of the expansion's arcane transmutation tablets up to date.
 * <p>
 * The tablets use ProjectE's {@link TransmutationInventory}, but they are not ProjectE's
 * {@link moze_intel.projecte.gameObjs.container.TransmutationContainer}, so ProjectE's transmutation packets
 * never update them by themselves. The client side methods of this class are called by ProjectE's packets
 * whenever ProjectE has updated its own transmutation gui with the same data.
 * <p>
 * The server side methods listen to ProjectE's own events instead, so that clients are told to update their
 * targets when the server did something that ProjectE only tells its own gui about.
 */
@EventBusSubscriber(modid = PECore.MODID)
public final class ExpansionTransmutationSync {

	private ExpansionTransmutationSync() {}

	/**
	 * Called after ProjectE replaced the client's entire transmutation knowledge
	 */
	public static void onKnowledgeSynced(@Nullable Player player) {
		TransmutationInventory inventory = getTabletInventory(player);
		if (inventory != null) {
			inventory.updateClientTargets(false);
		}
	}

	/**
	 * Called after ProjectE synced the client's EMC
	 */
	public static void onKnowledgeEmcSynced(@Nullable Player player) {
		TransmutationInventory inventory = getTabletInventory(player);
		if (inventory != null) {
			inventory.updateClientTargets(true);
		}
	}

	/**
	 * Called after ProjectE synced the client's transmutation input/lock slots
	 */
	public static void onInputsAndLocksSynced(@Nullable Player player, TargetUpdateType updateTargets) {
		TransmutationInventory inventory = getTabletInventory(player);
		if (inventory == null || updateTargets == TargetUpdateType.NONE) {
			return;
		}
		if (updateTargets == TargetUpdateType.ALL) {
			inventory.updateClientTargets(false);
		} else {
			inventory.checkForUpdates();
		}
	}

	/**
	 * Called after ProjectE synced a single knowledge change to the client
	 */
	public static void onKnowledgeChangeSynced(@Nullable Player player, ItemInfo change, boolean learned) {
		TransmutationInventory inventory = getTabletInventory(player);
		if (inventory == null) {
			return;
		}
		if (learned) {
			inventory.itemLearned(change);
		} else {
			inventory.itemUnlearned(change);
		}
	}

	/**
	 * Called after a transmutation tablet gui asked the server to write an item into one of its output slots
	 */
	public static void onSearchUpdate(@Nullable Player player, int slot, ItemStack itemStack) {
		TransmutationInventory inventory = getTabletInventory(player);
		if (inventory != null) {
			inventory.writeIntoOutputSlot(slot, itemStack);
		}
	}

	/**
	 * Tells clients that have an arcane transmutation tablet open to update their targets,
	 * as the EMC values they are sorted by may have changed.
	 */
	@SubscribeEvent
	public static void onEmcRemapped(EMCRemapEvent event) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server == null) {
			return;
		}
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (player.containerMenu instanceof ContainerArcaneTransmutationTablet) {
				PacketDistributor.sendToPlayer(player, UpdateTransmutationTargetsPacket.INSTANCE);
			}
		}
	}

	/**
	 * Tells clients that have an arcane transmutation tablet open to update their targets after their
	 * transmutation knowledge was cleared, as ProjectE does the same for its own transmutation gui.
	 */
	@SubscribeEvent
	public static void onKnowledgeChanged(PlayerKnowledgeChangeEvent event) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server == null) {
			return;
		}
		ServerPlayer player = server.getPlayerList().getPlayer(event.getPlayerUUID());
		if (player == null || !(player.containerMenu instanceof ContainerArcaneTransmutationTablet)) {
			return;
		}
		IKnowledgeProvider knowledge = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
		//Note: We only need to tell the client if all the knowledge was removed, as any other change is already
		// synced to the client by ProjectE's knowledge sync packets. We also have to check if the player has full
		// knowledge first, as getting the knowledge of such a player generates a set of every mapped item.
		if (knowledge != null && !knowledge.hasFullKnowledge() && knowledge.getKnowledge().isEmpty()) {
			PacketDistributor.sendToPlayer(player, ClearKnowledgePacket.INSTANCE);
		}
	}

	@Nullable
	private static TransmutationInventory getTabletInventory(@Nullable Player player) {
		return player != null && player.containerMenu instanceof ContainerArcaneTransmutationTablet container ? container.transmutationInventory : null;
	}
}
