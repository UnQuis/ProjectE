package moze_intel.projecte.expansion.integrations.emi;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiRegistry;
import moze_intel.projecte.expansion.registries.ExpansionMenus;
import moze_intel.projecte.expansion.util.SearchSync;

@EmiEntrypoint
public class EmiPlugin implements dev.emi.emi.api.EmiPlugin {
	@Override
	public void register(EmiRegistry registry) {
		SearchSync.register(new SearchSync("emi", EmiApi::setSearchText));
		registry.addRecipeHandler(ExpansionMenus.ARCANE_TRANSMUTATION_TABLET.get(), new ArcaneCraftingTransferHandler());
	}
}
