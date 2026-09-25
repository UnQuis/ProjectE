package moze_intel.projecte.network.packets.to_client.knowledge;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.impl.capability.KnowledgeImpl.KnowledgeAttachment;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record KnowledgeSyncPKT(KnowledgeAttachment data) implements IPEPacket {

	public static final CustomPacketPayload.Type<KnowledgeSyncPKT> TYPE = new CustomPacketPayload.Type<>(PECore.rl("sync_knowledge"));
	public static final StreamCodec<RegistryFriendlyByteBuf, KnowledgeSyncPKT> STREAM_CODEC = KnowledgeAttachment.STREAM_CODEC.map(
			KnowledgeSyncPKT::new, KnowledgeSyncPKT::data
	);

	/**
	 * Data that arrived before the client had a player to apply it to. It is applied as soon as the client player exists.
	 */
	@Nullable
	private static KnowledgeAttachment pendingData;

	@NotNull
	@Override
	public CustomPacketPayload.Type<KnowledgeSyncPKT> type() {
		return TYPE;
	}

	@Override
	public void handle(IPayloadContext context) {
		//We have to use the client's player instance rather than context#player as the first usage of this packet is sent during player login
		// which is before the player exists on the client so the context does not contain it.
		//Note: This must stay LocalPlayer to not cause classloading issues
		if (applyToPlayer(Minecraft.getInstance().player, data)) {
			PECore.debugLog("** RECEIVED TRANSMUTATION DATA CLIENTSIDE **");
		} else {
			//26.3: the packet can now be processed before the client player exists, in which case we hold onto the data until it does
			pendingData = data;
			PECore.debugLog("** RECEIVED TRANSMUTATION DATA CLIENTSIDE ** (waiting for the client player)");
		}
	}

	/**
	 * Applies knowledge data that was received before the client player existed.
	 *
	 * @return true if there was pending data that got applied
	 */
	public static boolean applyPendingData(@Nullable LocalPlayer player) {
		KnowledgeAttachment pending = pendingData;
		if (player != null && pending != null) {
			pendingData = null;
			applyToPlayer(player, pending);
			PECore.debugLog("** APPLIED PENDING TRANSMUTATION DATA CLIENTSIDE **");
			return true;
		}
		return false;
	}

	private static boolean applyToPlayer(@Nullable LocalPlayer player, @NotNull KnowledgeAttachment data) {
		if (player == null) {
			return false;
		}
		player.setData(PEAttachmentTypes.KNOWLEDGE, data);
		if (player.containerMenu instanceof TransmutationContainer container) {
			container.transmutationInventory.updateClientTargets(false);
		}
		return true;
	}
}
