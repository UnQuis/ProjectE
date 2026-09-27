package moze_intel.projecte.expansion.net.packets.to_server;

import moze_intel.projecte.expansion.gui.container.ContainerArcaneTransmutationTablet;
import moze_intel.projecte.expansion.net.ExpansionPacketHandler;
import moze_intel.projecte.expansion.net.packets.IPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record PacketArcaneTransmutationTabletRecipeTransfer(List<List<ItemStack>> recipe, boolean transferAll) implements IPacket {
	public static final CustomPacketPayload.Type<PacketArcaneTransmutationTabletRecipeTransfer> TYPE = new CustomPacketPayload.Type<>(ExpansionPacketHandler.rl("arcane_transmutation_recipe_transfer"));
	public static final StreamCodec<RegistryFriendlyByteBuf, PacketArcaneTransmutationTabletRecipeTransfer> STREAM_CODEC = StreamCodec.composite(
			ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()).apply(ByteBufCodecs.list()), PacketArcaneTransmutationTabletRecipeTransfer::recipe,
			ByteBufCodecs.BOOL, PacketArcaneTransmutationTabletRecipeTransfer::transferAll,
			PacketArcaneTransmutationTabletRecipeTransfer::new
	);

	@Override
	public void handle(IPayloadContext context) {
		Player player = context.player();
		if (player.containerMenu instanceof ContainerArcaneTransmutationTablet container) {
			container.onRecipeTransfer(recipe, transferAll);
		}
	}

	@Override
	public CustomPacketPayload.Type<PacketArcaneTransmutationTabletRecipeTransfer> type() {
		return TYPE;
	}
}
