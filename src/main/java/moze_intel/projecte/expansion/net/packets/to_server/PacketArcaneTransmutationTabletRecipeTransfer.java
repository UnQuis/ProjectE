package moze_intel.projecte.expansion.net.packets.to_server;

import moze_intel.projecte.expansion.gui.container.ContainerArcaneTransmutationTablet;
import moze_intel.projecte.expansion.net.ExpansionPacketHandler;
import moze_intel.projecte.expansion.net.packets.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.Optional;

public record PacketArcaneTransmutationTabletRecipeTransfer(List<List<ItemStack>> recipe, boolean transferAll) implements IPacket {
	public static final ResourceLocation ID = ExpansionPacketHandler.rl("arcane_transmutation_recipe_transfer");

	public PacketArcaneTransmutationTabletRecipeTransfer(FriendlyByteBuf buffer) {
		this(buffer.readList(inner -> inner.readList(FriendlyByteBuf::readItem)), buffer.readBoolean());
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeCollection(recipe, (buf, stacks) -> stacks.forEach(buf::writeItem));
		buffer.writeBoolean(transferAll);
	}

	@Override
	public void handle(IPayloadContext context) {
		Optional<Player> player = context.player();
		if (player.isPresent() && player.get().containerMenu instanceof ContainerArcaneTransmutationTablet container) {
			container.onRecipeTransfer(recipe, transferAll);
		}
	}
}
