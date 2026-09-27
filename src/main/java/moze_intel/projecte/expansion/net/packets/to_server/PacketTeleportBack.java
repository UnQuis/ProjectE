package moze_intel.projecte.expansion.net.packets.to_server;

import moze_intel.projecte.expansion.capability.CapabilityAlchemicalBookLocations;
import moze_intel.projecte.expansion.capability.IAlchemicalBookLocationsProvider;
import moze_intel.projecte.expansion.item.ItemAlchemicalBook;
import moze_intel.projecte.expansion.net.ExpansionPacketHandler;
import moze_intel.projecte.expansion.net.packets.IPacket;
import moze_intel.projecte.expansion.util.Lang;
import moze_intel.projecte.expansion.util.Util;
import moze_intel.projecte.network.PEStreamCodecs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketTeleportBack(Player player, InteractionHand hand) implements IPacket {
	public static final CustomPacketPayload.Type<PacketTeleportBack> TYPE = new CustomPacketPayload.Type<>(ExpansionPacketHandler.rl("teleport_back"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PacketTeleportBack> STREAM_CODEC = StreamCodec.composite(
			Util.PLAYER_STREAM_CODEC, PacketTeleportBack::player,
			PEStreamCodecs.INTERACTION_HAND, PacketTeleportBack::hand,
			PacketTeleportBack::new
	);

	@Override
	public void handle(IPayloadContext context) {
		ItemStack stack = player.getItemInHand(hand);
		if (stack.getItem() instanceof ItemAlchemicalBook book) {
			try {
				IAlchemicalBookLocationsProvider provider = CapabilityAlchemicalBookLocations.from(stack);
				provider.teleportBack((ServerPlayer) player, book.getTier().isAcrossDimensions());
				provider.syncToOtherPlayers();
			} catch (CapabilityAlchemicalBookLocations.BookError error) {
				player.sendSystemMessage(Lang.Items.ALCHEMICAL_BOOK_TELEPORT_FAILED.translateColored(ChatFormatting.RED, error.getComponent()));
			}
		}
	}

	@Override
	public CustomPacketPayload.Type<PacketTeleportBack> type() {
		return TYPE;
	}
}
