package moze_intel.projecte.expansion.net.packets;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Based on ProjectE's {@code IPEPacket}.
 */
public interface IPacket extends CustomPacketPayload {
	void handle(IPayloadContext context);
}
