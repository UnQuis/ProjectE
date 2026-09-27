package moze_intel.projecte.expansion.net.packets;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Based on ProjectE's {@code IPEPacket}.
 * <p>
 * On 1.20.4 payloads are read through a {@link net.minecraft.network.FriendlyByteBuf.Reader} and written by
 * {@link CustomPacketPayload#write(net.minecraft.network.FriendlyByteBuf)}, so every payload also needs a
 * {@link net.minecraft.network.FriendlyByteBuf} constructor.
 */
public interface IPacket extends CustomPacketPayload {
	void handle(IPayloadContext context);

	default void handleMainThread(IPayloadContext context) {
		context.workHandler().execute(() -> handle(context));
	}
}
