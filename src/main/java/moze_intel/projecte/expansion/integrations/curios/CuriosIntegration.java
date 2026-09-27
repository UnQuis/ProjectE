package moze_intel.projecte.expansion.integrations.curios;

import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;

/**
 * Access to the Curios backed {@code transmutation_tablet} slot.
 * <p>
 * 1.20.4 runs Curios 7.3.2, where the inventory is reached through {@link CuriosApi#getCuriosInventory} and the slot
 * handler through {@link ICuriosItemHandler#getStacksHandler(String)}.
 */
public class CuriosIntegration {
	public static final String MOD_ID = "curios";
	public static final String TABLET_SLOT_ID = "transmutation_tablet";

	public static boolean modLoaded() {
		return ModList.get().isLoaded(MOD_ID);
	}

	public static Optional<IItemHandlerModifiable> getCuriosInventory(Player player) {
		if (!modLoaded()) {
			return Optional.empty();
		}
		return CuriosApi.getCuriosInventory(player)
				.flatMap(handler -> handler.getStacksHandler(TABLET_SLOT_ID))
				.map(ICurioStacksHandler::getStacks);
	}
}
