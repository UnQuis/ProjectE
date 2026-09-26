package moze_intel.projecte.expansion.net.packets.to_client;

import moze_intel.projecte.expansion.gui.container.ContainerBase;
import moze_intel.projecte.expansion.net.ExpansionPacketHandler;
import moze_intel.projecte.expansion.net.packets.IPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketUpdateWindowLong(short windowId, short propId, long propVal) implements IPacket {
	public static final CustomPacketPayload.Type<PacketUpdateWindowLong> TYPE = new CustomPacketPayload.Type<>(ExpansionPacketHandler.rl("update_window_long"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateWindowLong> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.SHORT, PacketUpdateWindowLong::windowId,
			ByteBufCodecs.SHORT, PacketUpdateWindowLong::propId,
			ByteBufCodecs.VAR_LONG, PacketUpdateWindowLong::propVal,
			PacketUpdateWindowLong::new
	);

	@Override
	public void handle(IPayloadContext context) {
		//Note: the same pattern ProjectE's own window update packets use. It must not touch any client only class:
		//the payload class is loaded on the dedicated server too while the packets are being registered
		Player player = context.player();
		if (player != null && player.containerMenu instanceof ContainerBase container && player.containerMenu.containerId == windowId) {
			container.updateProgressBarLong(propId, propVal);
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
