package moze_intel.projecte.integration;

import moze_intel.projecte.integration.curios.CurioItemCapability;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;

public class IntegrationHelper {

	public static final String CURIO_MODID = "curios";
	public static final String EMI_MODID = "emi";

	public static final EntityCapability<IItemHandler, Void> CURIO_ITEM_HANDLER = EntityCapability.createVoid(Identifier.fromNamespaceAndPath(CURIO_MODID, "item_handler"), IItemHandler.class);

	public static void registerCuriosCapability(RegisterCapabilitiesEvent event, Item item) {
		if (ModList.get().isLoaded(CURIO_MODID)) {
			CurioItemCapability.register(event, item);
		}
	}
}