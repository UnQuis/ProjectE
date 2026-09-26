package moze_intel.projecte.expansion.registries;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.capability.IAlchemicalBookLocationsProvider;
import moze_intel.projecte.expansion.util.IEmcStorageBigInteger;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

public class ExpansionCapabilities {

	//Note: BIG_EMC_STORAGE_CAPABILITY kept the "alchemical_book_locations" id from ProjectExpansion on purpose, do not "fix" it.
	public static final EntityCapability<IAlchemicalBookLocationsProvider, Void> ALCHEMICAL_BOOK_LOCATIONS_ENTITY = EntityCapability.createVoid(PECore.rl("alchemical_book_locations"), IAlchemicalBookLocationsProvider.class);
	public static final ItemCapability<IAlchemicalBookLocationsProvider, Void> ALCHEMICAL_BOOK_LOCATIONS_ITEM = ItemCapability.createVoid(PECore.rl("alchemical_book_locations"), IAlchemicalBookLocationsProvider.class);
	public static final BlockCapability<IEmcStorageBigInteger, @Nullable Direction> BIG_EMC_STORAGE_CAPABILITY = BlockCapability.createSided(PECore.rl("alchemical_book_locations"), IEmcStorageBigInteger.class);
}
