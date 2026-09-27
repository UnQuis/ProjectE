package moze_intel.projecte.expansion.util;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;

import java.util.Optional;

public record ContainerData(boolean inHand, Optional<InteractionHand> hand, int selected) {
	//Note: 1.20.4 has no payload stream codecs, so the data is written to and read from the menu buffer by hand,
	// and the enum is written as its ordinal as there is no enum codec available either
	public void encode(FriendlyByteBuf buf) {
		buf.writeBoolean(inHand);
		buf.writeBoolean(hand.isPresent());
		hand.ifPresent(value -> buf.writeVarInt(value.ordinal()));
		buf.writeVarInt(selected);
	}

	public static ContainerData inHand(InteractionHand hand, int selected) {
		return new ContainerData(true, Optional.of(hand), selected);
	}

	public static void inHand(FriendlyByteBuf buf, InteractionHand hand, int selected) {
		new ContainerData(true, Optional.of(hand), selected).encode(buf);
	}

	public static ContainerData noHand() {
		return new ContainerData(false, Optional.empty(), 0);
	}

	public static void noHand(FriendlyByteBuf buf) {
		new ContainerData(false, Optional.empty(), 0).encode(buf);
	}

	public static ContainerData decode(FriendlyByteBuf buf) {
		boolean inHand = buf.readBoolean();
		Optional<InteractionHand> hand = buf.readBoolean() ? Optional.of(InteractionHand.values()[buf.readVarInt()]) : Optional.empty();
		return new ContainerData(inHand, hand, buf.readVarInt());
	}
}
