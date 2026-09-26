package moze_intel.projecte.expansion.net.packets.to_client;

import moze_intel.projecte.expansion.gui.container.ContainerBase;
import moze_intel.projecte.expansion.net.ExpansionPacketHandler;
import moze_intel.projecte.expansion.net.packets.IPacket;
import moze_intel.projecte.expansion.util.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.math.BigInteger;

public record PacketUpdateWindowBigInteger(short windowId, short propId, BigInteger propVal) implements IPacket {
	public static final CustomPacketPayload.Type<PacketUpdateWindowBigInteger> TYPE = new CustomPacketPayload.Type<>(ExpansionPacketHandler.rl("update_window_big_integer"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateWindowBigInteger> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.SHORT, PacketUpdateWindowBigInteger::windowId,
			ByteBufCodecs.SHORT, PacketUpdateWindowBigInteger::propId,
			Util.BIG_INTEGER_STREAM_CODEC, PacketUpdateWindowBigInteger::propVal,
			PacketUpdateWindowBigInteger::new
	);

	@Override
	public void handle(IPayloadContext context) {
		//Note: the same pattern ProjectE's own window update packets use. It must not touch any client only class:
		//the payload class is loaded on the dedicated server too while the packets are being registered
		Player player = context.player();
		if (player != null && player.containerMenu instanceof ContainerBase container && player.containerMenu.containerId == windowId) {
			container.updateProgressBarBigInteger(propId, propVal);
		}
	}

	@Override
	public CustomPacketPayload.Type<PacketUpdateWindowBigInteger> type() {
		return TYPE;
	}
}
