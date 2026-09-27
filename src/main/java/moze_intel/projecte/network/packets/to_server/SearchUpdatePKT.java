package moze_intel.projecte.network.packets.to_server;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.network.PacketUtils;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import moze_intel.projecte.expansion.ExpansionTransmutationSync;

public record SearchUpdatePKT(int slot, ItemStack itemStack) implements IPEPacket<PlayPayloadContext> {

	public static final ResourceLocation ID = PECore.rl("search_update");

	public SearchUpdatePKT(FriendlyByteBuf buffer) {
		this(buffer.readVarInt(), buffer.readItem());
	}

	@NotNull
	@Override
	public ResourceLocation id() {
		return ID;
	}

	public SearchUpdatePKT {
		itemStack = itemStack.copy();
	}

	@Override
	public void handle(PlayPayloadContext context) {
		Optional<TransmutationContainer> container = PacketUtils.container(context, TransmutationContainer.class);
		if (container.isPresent()) {
			container.get().transmutationInventory.writeIntoOutputSlot(slot, itemStack);
		} else {
			//Let any transmutation gui that is not one of ours know which of its outputs to display
			ExpansionTransmutationSync.onSearchUpdate(context.player().orElse(null), slot, itemStack);
		}
	}

	@Override
	public void write(@NotNull FriendlyByteBuf buffer) {
		buffer.writeVarInt(slot);
		buffer.writeItem(itemStack);
	}
}