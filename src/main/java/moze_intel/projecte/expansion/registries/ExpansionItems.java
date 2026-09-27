package moze_intel.projecte.expansion.registries;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.item.ItemAlchemicalBook;
import moze_intel.projecte.expansion.item.ItemArcaneTransmutationTablet;
import moze_intel.projecte.expansion.item.ItemFinalStar;
import moze_intel.projecte.expansion.item.ItemFinalStarShard;
import moze_intel.projecte.expansion.item.ItemInfiniteFuel;
import moze_intel.projecte.expansion.item.ItemInfiniteSteak;
import moze_intel.projecte.expansion.item.ItemKnowledgeSharingBook;
import moze_intel.projecte.expansion.item.ItemMatterUpgrader;
import moze_intel.projecte.gameObjs.registration.impl.ItemDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.ItemRegistryObject;

@SuppressWarnings("unused")
public class ExpansionItems {

	public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(PECore.MODID);

	public static final ItemRegistryObject<ItemFinalStarShard> FINAL_STAR_SHARD = ITEMS.register("final_star_shard", ItemFinalStarShard::new);
	public static final ItemRegistryObject<ItemFinalStar> FINAL_STAR = ITEMS.register("final_star", ItemFinalStar::new);
	public static final ItemRegistryObject<ItemMatterUpgrader> MATTER_UPGRADER = ITEMS.register("matter_upgrader", ItemMatterUpgrader::new);
	public static final ItemRegistryObject<ItemInfiniteFuel> INFINITE_FUEL = ITEMS.register("infinite_fuel", ItemInfiniteFuel::new);
	public static final ItemRegistryObject<ItemInfiniteSteak> INFINITE_STEAK = ITEMS.register("infinite_steak", ItemInfiniteSteak::new);
	public static final ItemRegistryObject<ItemKnowledgeSharingBook> KNOWLEDGE_SHARING_BOOK = ITEMS.register("knowledge_sharing_book", ItemKnowledgeSharingBook::new);
	public static final ItemRegistryObject<ItemAlchemicalBook> BASIC_ALCHEMICAL_BOOK = ITEMS.register("basic_alchemical_book", () -> new ItemAlchemicalBook(ItemAlchemicalBook.Tier.BASIC));
	public static final ItemRegistryObject<ItemAlchemicalBook> ADVANCED_ALCHEMICAL_BOOK = ITEMS.register("advanced_alchemical_book", () -> new ItemAlchemicalBook(ItemAlchemicalBook.Tier.ADVANCED));
	public static final ItemRegistryObject<ItemAlchemicalBook> MASTER_ALCHEMICAL_BOOK = ITEMS.register("master_alchemical_book", () -> new ItemAlchemicalBook(ItemAlchemicalBook.Tier.MASTER));
	public static final ItemRegistryObject<ItemAlchemicalBook> ARCANE_ALCHEMICAL_BOOK = ITEMS.register("arcane_alchemical_book", () -> new ItemAlchemicalBook(ItemAlchemicalBook.Tier.ARCANE));
	public static final ItemRegistryObject<ItemArcaneTransmutationTablet> ARCANE_TRANSMUTATION_TABLET = ITEMS.register("arcane_transmutation_tablet", ItemArcaneTransmutationTablet::new);
}
