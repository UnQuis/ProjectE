package moze_intel.projecte.expansion.net.packets.to_server;

import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.expansion.capability.CapabilityAlchemicalBookLocations;
import moze_intel.projecte.expansion.capability.IAlchemicalBookLocationsProvider;
import moze_intel.projecte.expansion.item.ItemAlchemicalBook;
import moze_intel.projecte.expansion.net.ExpansionPacketHandler;
import moze_intel.projecte.expansion.net.packets.IPacket;
import moze_intel.projecte.expansion.util.Lang;
import moze_intel.projecte.expansion.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.math.BigInteger;
import java.util.Objects;

public record PacketTeleportToLocation(String name, Player player, InteractionHand hand) implements IPacket {
	public static final ResourceLocation ID = ExpansionPacketHandler.rl("teleport_to_location");

	public PacketTeleportToLocation(FriendlyByteBuf buffer) {
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
		if (stack.getItem() instanceof ItemAlchemicalBook book) {
			try {
				IAlchemicalBookLocationsProvider provider = CapabilityAlchemicalBookLocations.from(stack);
				IKnowledgeProvider knowledgeProvider = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
				if (knowledgeProvider == null) throw new IllegalStateException("Player does not have knowledge capability");
				BigInteger emc = knowledgeProvider.getEmc();
				CapabilityAlchemicalBookLocations.TeleportLocation location = provider.getLocationOrThrow(name);
				BigInteger cost = BigInteger.valueOf(location.getCost(stack, player));
				if (!cost.equals(BigInteger.ZERO)) {
					if (emc.compareTo(cost) < 0) {
						throw new CapabilityAlchemicalBookLocations.BookError.NotEnoughEMCError(cost.toString());
					}
					knowledgeProvider.setEmc(emc.subtract(cost));
					knowledgeProvider.syncEmc((ServerPlayer) player);
				}
				GlobalPos pos = GlobalPos.of(player.level().dimension(), player.blockPosition());
				location.teleportTo((ServerPlayer) player, book.getTier().isAcrossDimensions());
				if (location.distanceFrom(pos.pos()) > 1) {
					provider.saveBackLocation(player, pos);
				}
			} catch (CapabilityAlchemicalBookLocations.BookError error) {
				player.sendSystemMessage(Lang.Items.ALCHEMICAL_BOOK_TELEPORT_FAILED.translateColored(ChatFormatting.RED, error.getComponent()));
			} catch (IllegalStateException error) {
				player.sendSystemMessage(Lang.PROVIDER_ERROR.translateColored(ChatFormatting.RED));
			}
		}
	}
}
