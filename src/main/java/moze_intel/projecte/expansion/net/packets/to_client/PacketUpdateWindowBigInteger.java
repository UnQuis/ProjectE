package moze_intel.projecte.expansion.net.packets.to_client;

import moze_intel.projecte.expansion.gui.container.ContainerBase;
import moze_intel.projecte.expansion.net.ExpansionPacketHandler;
import moze_intel.projecte.expansion.net.packets.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.math.BigInteger;
import java.util.Optional;

public record PacketUpdateWindowBigInteger(short windowId, short propId, BigInteger propVal) implements IPacket {
	public static final ResourceLocation ID = ExpansionPacketHandler.rl("update_window_big_integer");

	public PacketUpdateWindowBigInteger(FriendlyByteBuf buffer) {
		this(buffer.readShort(), buffer.readShort(), new BigInteger(buffer.readUtf()));
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeShort(windowId);
		buffer.writeShort(propId);
		buffer.writeUtf(propVal.toString());
	}

	@Override
	public void handle(IPayloadContext context) {
		//Note: the same pattern ProjectE's own window update packets use. It must not touch any client only class:
		//the payload class is loaded on the dedicated server too while the packets are being registered
		Optional<Player> player = context.player();
		if (player.isPresent() && player.get().containerMenu instanceof ContainerBase container && player.get().containerMenu.containerId == windowId) {
			container.updateProgressBarBigInteger(propId, propVal);
		}
	}
}
