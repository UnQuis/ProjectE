package moze_intel.projecte.expansion.net.packets.to_client;

import moze_intel.projecte.expansion.capability.CapabilityAlchemicalBookLocations;
import moze_intel.projecte.expansion.item.ItemAlchemicalBook;
import moze_intel.projecte.expansion.net.ExpansionPacketHandler;
import moze_intel.projecte.expansion.net.packets.IPacket;
import moze_intel.projecte.expansion.util.ClientSideHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record PacketOpenAlchemicalBookGUI(InteractionHand hand, List<CapabilityAlchemicalBookLocations.TeleportLocation> locations, ItemAlchemicalBook.Mode mode, boolean canEdit) implements IPacket {
	public static final ResourceLocation ID = ExpansionPacketHandler.rl("open_alchemical_book_gui");

	public PacketOpenAlchemicalBookGUI(FriendlyByteBuf buffer) {
		this(buffer.readEnum(InteractionHand.class), TeleportLocationSync.fromTag(buffer.readNbt()), buffer.readEnum(ItemAlchemicalBook.Mode.class), buffer.readBoolean());
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeEnum(hand);
		buffer.writeNbt(TeleportLocationSync.toTag(locations));
		buffer.writeEnum(mode);
		buffer.writeBoolean(canEdit);
	}

	@Override
	public void handle(IPayloadContext context) {
		ClientSideHandler.handleAlchemicalBookOpen(this);
	}
}
