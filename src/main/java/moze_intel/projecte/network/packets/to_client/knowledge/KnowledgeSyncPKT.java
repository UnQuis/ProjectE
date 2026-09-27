package moze_intel.projecte.network.packets.to_client.knowledge;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import moze_intel.projecte.expansion.ExpansionSettings;
import moze_intel.projecte.expansion.ExpansionTransmutationSync;

public record KnowledgeSyncPKT(CompoundTag nbt) implements IPEPacket<PlayPayloadContext> {

	public static final ResourceLocation ID = PECore.rl("knowledge_sync");

	public KnowledgeSyncPKT(FriendlyByteBuf buffer) {
		this(buffer.readNbt());
	}

	@NotNull
	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void handle(PlayPayloadContext context) {
		//We have to use the client's player instance rather than context#player as the first usage of this packet is sent during player login
		// which is before the player exists on the client so the context does not contain it.
		//Note: This must stay LocalPlayer to not cause classloading issues
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			player.getData(PEAttachmentTypes.KNOWLEDGE).deserializeNBT(nbt);
			if (player.containerMenu instanceof TransmutationContainer container) {
				container.transmutationInventory.updateClientTargets();
			} else {
				//Let any transmutation gui that is not one of ours know that its targets may have changed
				//Note: pass the context's player, not the LocalPlayer above: handing a LocalPlayer to a method that takes a
				//Player makes the verifier resolve the client only class, which a dedicated server cannot load
				ExpansionTransmutationSync.onKnowledgeSynced(player);
			}
		}
		if (!ExpansionSettings.suppressTransmutationSyncLogs()) {
			PECore.debugLog("** RECEIVED TRANSMUTATION DATA CLIENTSIDE **");
		}

	}

	@Override
	public void write(@NotNull FriendlyByteBuf buffer) {
		buffer.writeNbt(nbt);
	}
}