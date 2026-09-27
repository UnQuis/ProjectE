package moze_intel.projecte.expansion.net.packets.to_client;

import moze_intel.projecte.expansion.gui.container.ContainerArcaneTransmutationTablet;
import moze_intel.projecte.expansion.net.ExpansionPacketHandler;
import moze_intel.projecte.expansion.net.packets.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public class UpdateTransmutationTargetsPacket implements IPacket {
	public static final UpdateTransmutationTargetsPacket INSTANCE = new UpdateTransmutationTargetsPacket();
	public static final ResourceLocation ID = ExpansionPacketHandler.rl("update_transmutation_targets");

	public UpdateTransmutationTargetsPacket() {
	}

	public UpdateTransmutationTargetsPacket(FriendlyByteBuf buffer) {
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
	}

	@Override
	public void handle(IPayloadContext context) {
		Optional<Player> player = context.player();
		if (player.isPresent() && player.get().containerMenu instanceof ContainerArcaneTransmutationTablet container) {
			container.transmutationInventory.updateClientTargets();
		}
	}
}
