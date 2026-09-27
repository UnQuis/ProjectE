package moze_intel.projecte.expansion.net.packets.to_client;

import moze_intel.projecte.expansion.capability.CapabilityAlchemicalBookLocations;
import moze_intel.projecte.expansion.item.ItemAlchemicalBook;
import moze_intel.projecte.expansion.net.ExpansionPacketHandler;
import moze_intel.projecte.expansion.net.packets.IPacket;
import moze_intel.projecte.expansion.util.ClientSideHandler;
import moze_intel.projecte.network.PEStreamCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record PacketOpenAlchemicalBookGUI(InteractionHand hand, List<CapabilityAlchemicalBookLocations.TeleportLocation> locations, ItemAlchemicalBook.Mode mode, boolean canEdit) implements IPacket {
	public static final CustomPacketPayload.Type<PacketOpenAlchemicalBookGUI> TYPE = new CustomPacketPayload.Type<>(ExpansionPacketHandler.rl("open_alchemical_book_gui"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PacketOpenAlchemicalBookGUI> STREAM_CODEC = StreamCodec.composite(
			PEStreamCodecs.INTERACTION_HAND, PacketOpenAlchemicalBookGUI::hand,
			CapabilityAlchemicalBookLocations.TeleportLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), PacketOpenAlchemicalBookGUI::locations,
			ItemAlchemicalBook.Mode.STREAM_CODEC, PacketOpenAlchemicalBookGUI::mode,
			ByteBufCodecs.BOOL, PacketOpenAlchemicalBookGUI::canEdit,
			PacketOpenAlchemicalBookGUI::new
	);

	@Override
	public CustomPacketPayload.Type<PacketOpenAlchemicalBookGUI> type() {
		return TYPE;
	}

	@Override
	public void handle(IPayloadContext context) {
		ClientSideHandler.handleAlchemicalBookOpen(this);
	}
}
