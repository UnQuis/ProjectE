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

public record PacketUpdateWindowInt(short windowId, short propId, int propVal) implements IPacket {
	public static final CustomPacketPayload.Type<PacketUpdateWindowInt> TYPE = new CustomPacketPayload.Type<>(ExpansionPacketHandler.rl("update_window_int"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateWindowInt> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.SHORT, PacketUpdateWindowInt::windowId,
			ByteBufCodecs.SHORT, PacketUpdateWindowInt::propId,
			ByteBufCodecs.INT, PacketUpdateWindowInt::propVal,
			PacketUpdateWindowInt::new
	);

	@Override
	public void handle(IPayloadContext context) {
		//Note: the same pattern ProjectE's own window update packets use. It must not touch any client only class:
		//the payload class is loaded on the dedicated server too while the packets are being registered
		Player player = context.player();
		if (player != null && player.containerMenu instanceof ContainerBase container && player.containerMenu.containerId == windowId) {
			container.updateProgressBarInt(propId, propVal);
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
