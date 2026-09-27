package moze_intel.projecte.api.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

/**
 * Implemented by items that open one of ProjectE's transmutation screens, so the rest of ProjectE can open the
 * screen without knowing which item it was.
 */
public interface ITransmutationTablet {

	/**
	 * Opens the transmutation screen for a held item.
	 *
	 * @param player   The player opening the screen
	 * @param hand     The hand the item is held in
	 * @param selected The selected hotbar slot, used when the item changes the screen layout
	 */
	void openContainer(Player player, InteractionHand hand, int selected);

	/**
	 * Opens the transmutation screen for a stored item.
	 *
	 * @param player The player opening the screen
	 */
	void openContainer(Player player);
}
