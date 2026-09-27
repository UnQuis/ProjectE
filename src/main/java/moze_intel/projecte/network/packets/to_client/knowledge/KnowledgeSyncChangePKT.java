package moze_intel.projecte.network.packets.to_client.knowledge;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import moze_intel.projecte.expansion.ExpansionSettings;
import moze_intel.projecte.expansion.ExpansionTransmutationSync;

public record KnowledgeSyncChangePKT(ItemInfo change, boolean learned) implements IPEPacket<PlayPayloadContext> {

	public static final ResourceLocation ID = PECore.rl("knowledge_sync_change");

	public KnowledgeSyncChangePKT(FriendlyByteBuf buffer) {
		this(ItemInfo.read(buffer), buffer.readBoolean());
	}

	@NotNull
	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void handle(PlayPayloadContext context) {
		context.player().ifPresent(player -> {
			IKnowledgeProvider knowledge = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
			if (knowledge != null) {
				if (learned) {
					if (!knowledge.hasKnowledge(change) && knowledge.addKnowledge(change)) {
						if (player.containerMenu instanceof TransmutationContainer container) {
							container.transmutationInventory.itemLearned();
						} else {
							//Let any transmutation gui that is not one of ours know that it learned an item
							ExpansionTransmutationSync.onKnowledgeChangeSynced(player, change, true);
						}
					}
				} else if (knowledge.hasKnowledge(change) && knowledge.removeKnowledge(change)) {
					if (player.containerMenu instanceof TransmutationContainer container) {
						container.transmutationInventory.itemUnlearned();
					} else {
						//Let any transmutation gui that is not one of ours know that it unlearned an item
						ExpansionTransmutationSync.onKnowledgeChangeSynced(player, change, false);
					}
				}
			}
		});
		if (!ExpansionSettings.suppressTransmutationSyncLogs()) {
			PECore.debugLog("** RECEIVED TRANSMUTATION KNOWLEDGE CHANGE DATA CLIENTSIDE **");
		}

	}

	@Override
	public void write(@NotNull FriendlyByteBuf buffer) {
		change.write(buffer);
		buffer.writeBoolean(learned);
	}
}