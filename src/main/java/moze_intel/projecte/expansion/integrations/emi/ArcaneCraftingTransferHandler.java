package moze_intel.projecte.expansion.integrations.emi;

import moze_intel.projecte.expansion.gui.container.ContainerArcaneTransmutationTablet;
import moze_intel.projecte.expansion.net.packets.to_server.PacketArcaneTransmutationTabletRecipeTransfer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;

/**
 * 26.3 note: EMI has not been released for 26.x, so {@code dev.emi} is not on the compile classpath and this class
 * can no longer implement {@code StandardRecipeHandler}. The one piece of real logic (sending the recipe transfer
 * packet) is kept in {@link #craft(List)} so re-enabling the integration is a matter of adding the dependency back and
 * re-implementing {@code StandardRecipeHandler#craft} on top of it.
 */
@SuppressWarnings("unused")
public class ArcaneCraftingTransferHandler {

	/**
	 * @param itemStacks one entry per recipe input, each entry the alternatives for that input
	 * @param transferAll whether every input should be filled as full as possible
	 * @return always {@code true}, the transfer always went through
	 */
	public boolean craft(List<List<ItemStack>> itemStacks, boolean transferAll) {
		ClientPacketDistributor.sendToServer(new PacketArcaneTransmutationTabletRecipeTransfer(itemStacks, transferAll));
		return true;
	}

	/**
	 * Kept so the menu reference the handler used is not lost, EMI looks the handler up by the container.
	 */
	public Class<ContainerArcaneTransmutationTablet> getContainerClass() {
		return ContainerArcaneTransmutationTablet.class;
	}
}
