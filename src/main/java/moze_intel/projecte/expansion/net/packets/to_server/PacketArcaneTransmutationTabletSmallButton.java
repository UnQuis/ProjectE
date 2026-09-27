package moze_intel.projecte.expansion.net.packets.to_server;

import moze_intel.projecte.expansion.gui.container.ContainerArcaneTransmutationTablet;
import moze_intel.projecte.expansion.net.ExpansionPacketHandler;
import moze_intel.projecte.expansion.net.packets.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record PacketArcaneTransmutationTabletSmallButton(Action action) implements IPacket {
	public static final ResourceLocation ID = ExpansionPacketHandler.rl("arcane_transmutation_tablet_small_button");

	public PacketArcaneTransmutationTabletSmallButton(FriendlyByteBuf buffer) {
		this(buffer.readEnum(Action.class));
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeEnum(action);
	}

	@Override
	public void handle(IPayloadContext context) {
		Optional<Player> player = context.player();
		if (player.isPresent() && player.get().containerMenu instanceof ContainerArcaneTransmutationTablet container) {
			switch (action) {
				case ROTATE -> container.rotateCrafting(true);
				case ROTATE_CC -> container.rotateCrafting(false);
				case BALANCE -> container.balanceCrafting();
				case SPREAD -> container.spreadCrafting();
				case CLEAR -> container.clearCrafting(false);
				case CLEAR_FORCE -> container.clearCrafting(true);
			}
		}
	}

	public enum Action {
		ROTATE, ROTATE_CC, BALANCE, SPREAD, CLEAR, CLEAR_FORCE
	}
}
