package moze_intel.projecte.expansion.net.packets.to_client;

import moze_intel.projecte.expansion.capability.CapabilityAlchemicalBookLocations;
import moze_intel.projecte.expansion.net.ExpansionPacketHandler;
import moze_intel.projecte.expansion.net.packets.IPacket;
import moze_intel.projecte.expansion.util.ClientSideHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record PacketSyncAlchemicalBookLocations(List<CapabilityAlchemicalBookLocations.TeleportLocation> locations, boolean canEdit) implements IPacket {
	public static final ResourceLocation ID = ExpansionPacketHandler.rl("sync_alchemical_book_locations");

	public PacketSyncAlchemicalBookLocations(FriendlyByteBuf buffer) {
		this(TeleportLocationSync.fromTag(buffer.readNbt()), buffer.readBoolean());
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeNbt(TeleportLocationSync.toTag(locations));
		buffer.writeBoolean(canEdit);
	}

	@Override
	public void handle(IPayloadContext context) {
		ClientSideHandler.handleSyncAlchemicalBookLocations(this);
	}
}
