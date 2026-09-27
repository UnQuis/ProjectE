package moze_intel.projecte.expansion.registries;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.block.BlockAdvancedAlchemicalChest;
import moze_intel.projecte.expansion.block.BlockCollector;
import moze_intel.projecte.expansion.block.BlockCondenserMK3;
import moze_intel.projecte.expansion.block.BlockEMCLink;
import moze_intel.projecte.expansion.block.BlockMatter;
import moze_intel.projecte.expansion.block.BlockPowerFlower;
import moze_intel.projecte.expansion.block.BlockRelay;
import moze_intel.projecte.expansion.util.IHasColor;
import moze_intel.projecte.expansion.util.IHasMatter;
import moze_intel.projecte.expansion.util.Matter;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.impl.BlockTypeDeferredRegister;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.BiFunction;

public class ExpansionBlockTypes {

	public static final BlockTypeDeferredRegister BLOCK_TYPES = new BlockTypeDeferredRegister(PECore.MODID);

	public static final PEDeferredHolder<MapCodec<? extends Block>, MapCodec<BlockAdvancedAlchemicalChest>> ADVANCED_ALCHEMICAL_CHEST = registerColor("advanced_alchemical_chest", BlockAdvancedAlchemicalChest::new);
	public static final PEDeferredHolder<MapCodec<? extends Block>, MapCodec<BlockCollector>> COLLECTOR = registerMatter("collector", BlockCollector::new);
	public static final PEDeferredHolder<MapCodec<? extends Block>, MapCodec<BlockEMCLink>> EMC_LINK = registerMatter("emc_link", BlockEMCLink::new);
	public static final PEDeferredHolder<MapCodec<? extends Block>, MapCodec<BlockPowerFlower>> POWER_FLOWER = registerMatter("power_flower", BlockPowerFlower::new);
	public static final PEDeferredHolder<MapCodec<? extends Block>, MapCodec<BlockRelay>> RELAY = registerMatter("relay", BlockRelay::new);
	public static final PEDeferredHolder<MapCodec<? extends Block>, MapCodec<BlockMatter>> MATTER_BLOCK = registerMatter("matter_block", BlockMatter::new);
	public static final PEDeferredHolder<MapCodec<? extends Block>, MapCodec<BlockCondenserMK3>> CONDENSER_MK3 = BLOCK_TYPES.registerSimple("condenser_mk3", BlockCondenserMK3::new);

	public static <T extends Block & IHasMatter> PEDeferredHolder<MapCodec<? extends Block>, MapCodec<T>> registerMatter(String name, BiFunction<BlockBehaviour.Properties, Matter, T> block) {
		return BLOCK_TYPES.register(name, () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
				BlockBehaviour.propertiesCodec(),
				Matter.CODEC.fieldOf("matter").forGetter(IHasMatter::getMatter)
		).apply(instance, block)));
	}

	public static <T extends Block & IHasColor> PEDeferredHolder<MapCodec<? extends Block>, MapCodec<T>> registerColor(String name, BiFunction<BlockBehaviour.Properties, DyeColor, T> block) {
		return BLOCK_TYPES.register(name, () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
				BlockBehaviour.propertiesCodec(),
				DyeColor.CODEC.fieldOf("color").forGetter(IHasColor::getColor)
		).apply(instance, block)));
	}
}
