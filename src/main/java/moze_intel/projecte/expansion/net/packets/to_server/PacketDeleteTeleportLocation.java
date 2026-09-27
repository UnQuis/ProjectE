package moze_intel.projecte.expansion.net.packets.to_server;

import moze_intel.projecte.expansion.capability.CapabilityAlchemicalBookLocations;
import moze_intel.projecte.expansion.capability.IAlchemicalBookLocationsProvider;
import moze_intel.projecte.expansion.item.ItemAlchemicalBook;
import moze_intel.projecte.expansion.net.ExpansionPacketHandler;
import moze_intel.projecte.expansion.net.packets.IPacket;
import moze_intel.projecte.expansion.util.Lang;
import moze_intel.projecte.expansion.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;

public record PacketDeleteTeleportLocation(String name, Player player, InteractionHand hand) implements IPacket {
	public static final ResourceLocation ID = ExpansionPacketHandler.rl("delete_teleport_location");

	public PacketDeleteTeleportLocation(FriendlyByteBuf buffer) {
		this(buffer.readUtf(), Objects.requireNonNull(Util.getPlayer(buffer.readUUID())), buffer.readEnum(InteractionHand.class));
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(name);
		buffer.writeUUID(player.getUUID());
		buffer.writeEnum(hand);
	}

	@Override
	public void handle(IPayloadContext context) {
		ItemStack stack = player.getItemInHand(hand);
		if (stack.getItem() instanceof ItemAlchemicalBook) {
			try {
				IAlchemicalBookLocationsProvider provider = CapabilityAlchemicalBookLocations.from(stack);
				provider.ensureEditable((ServerPlayer) player);
				provider.removeLocation(name);
				provider.sync((ServerPlayer) player);
				provider.syncToOtherPlayers();
			} catch (CapabilityAlchemicalBookLocations.BookError error) {
				player.sendSystemMessage(Lang.Items.ALCHEMICAL_BOOK_DELETE_FAILED.translateColored(ChatFormatting.RED, error.getComponent()));
			}
		}
	}
}
