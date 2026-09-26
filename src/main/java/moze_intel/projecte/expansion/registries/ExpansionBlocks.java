package moze_intel.projecte.expansion.registries;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.block.BlockCompactSun;
import moze_intel.projecte.expansion.block.BlockCondenserMK3;
import moze_intel.projecte.expansion.block.BlockTransmutationInterface;
import moze_intel.projecte.gameObjs.registration.impl.BlockDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

@SuppressWarnings("unused")
public class ExpansionBlocks {

	public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(PECore.MODID);

	public static final BlockRegistryObject<BlockTransmutationInterface, BlockItem> TRANSMUTATION_INTERFACE = BLOCKS.register("transmutation_interface",
			() -> new BlockTransmutationInterface(BlockTransmutationInterface.getProperties()),
			block -> new BlockItem(block, new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
	public static final BlockRegistryObject<BlockCompactSun, BlockItem> COMPACT_SUN = BLOCKS.register("compact_sun",
			() -> new BlockCompactSun(BlockCompactSun.getProperties()),
			block -> new BlockItem(block, new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
	public static final BlockRegistryObject<BlockCondenserMK3, BlockItem> CONDENSER_MK3 = BLOCKS.register("condenser_mk3",
			() -> new BlockCondenserMK3(BlockCondenserMK3.getProperties()),
			block -> new BlockItem(block, new Item.Properties().fireResistant()));
}
