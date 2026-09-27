package moze_intel.projecte.expansion.net.packets.to_client;

import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.expansion.gui.container.ContainerCondenserMK3Input;
import moze_intel.projecte.expansion.net.ExpansionPacketHandler;
import moze_intel.projecte.expansion.net.packets.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record PacketUpdateCondenserLock(short windowId, @Nullable ItemInfo lockInfo) implements IPacket {
	public static final ResourceLocation ID = ExpansionPacketHandler.rl("update_condenser_lock");

	public PacketUpdateCondenserLock(FriendlyByteBuf buffer) {
		this(buffer.readShort(), buffer.readBoolean() ? ItemInfo.read(buffer) : null);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeShort(windowId);
		buffer.writeBoolean(lockInfo != null);
		if (lockInfo != null) {
			lockInfo.write(buffer);
		}
	}

	@Override
	public void handle(IPayloadContext context) {
		Optional<Player> player = context.player();
		if (player.isPresent() && player.get().containerMenu instanceof ContainerCondenserMK3Input container && container.containerId == windowId) {
			container.updateLockInfo(lockInfo);
		}
	}
}
