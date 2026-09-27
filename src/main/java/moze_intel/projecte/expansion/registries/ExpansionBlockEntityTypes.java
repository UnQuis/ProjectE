package moze_intel.projecte.expansion.registries;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.block.entity.BlockEntityAdvancedAlchemicalChest;
import moze_intel.projecte.expansion.block.entity.BlockEntityCollector;
import moze_intel.projecte.expansion.block.entity.BlockEntityCondenserMK3;
import moze_intel.projecte.expansion.block.entity.BlockEntityEMCLink;
import moze_intel.projecte.expansion.block.entity.BlockEntityPowerFlower;
import moze_intel.projecte.expansion.block.entity.BlockEntityRelay;
import moze_intel.projecte.expansion.block.entity.BlockEntityTransmutationInterface;
import moze_intel.projecte.expansion.util.AdvancedAlchemicalChest;
import moze_intel.projecte.expansion.util.Matter;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Arrays;

/**
 * Note: Unlike {@link moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes} these block entity types are shared by all
 * 16 matter tiers, which the {@link moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeDeferredRegister#builder}
 * API cannot express. The tickers are therefore supplied by the blocks themselves and the capabilities are registered by
 * {@code events.CapabilityEvents}, exactly as they were in ProjectExpansion.
 */
@SuppressWarnings({"unused", "ConstantConditions"})
public class ExpansionBlockEntityTypes {

	public static final PEDeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = new PEDeferredRegister<>(Registries.BLOCK_ENTITY_TYPE, PECore.MODID);

	public static final PEDeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityEMCLink>> EMC_LINK = BLOCK_ENTITY_TYPES.register("emc_link", () -> BlockEntityType.Builder.of(BlockEntityEMCLink::new, Arrays.stream(Matter.VALUES).map(Matter::getEMCLink).toArray(Block[]::new)).build(null));
	public static final PEDeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityPowerFlower>> POWER_FLOWER = BLOCK_ENTITY_TYPES.register("power_flower", () -> BlockEntityType.Builder.of(BlockEntityPowerFlower::new, Arrays.stream(Matter.VALUES).map(Matter::getPowerFlower).toArray(Block[]::new)).build(null));
	public static final PEDeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityCollector>> COLLECTOR = BLOCK_ENTITY_TYPES.register("collector", () -> BlockEntityType.Builder.of(BlockEntityCollector::new, Arrays.stream(Matter.VALUES).map(Matter::getCollector).toArray(Block[]::new)).build(null));
	public static final PEDeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityRelay>> RELAY = BLOCK_ENTITY_TYPES.register("relay", () -> BlockEntityType.Builder.of(BlockEntityRelay::new, Arrays.stream(Matter.VALUES).map(Matter::getRelay).toArray(Block[]::new)).build(null));
	public static final PEDeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityTransmutationInterface>> TRANSMUTATION_INTERFACE = BLOCK_ENTITY_TYPES.register("transmutation_interface", () -> BlockEntityType.Builder.of(BlockEntityTransmutationInterface::new, ExpansionBlocks.TRANSMUTATION_INTERFACE.getBlock()).build(null));
	public static final PEDeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityAdvancedAlchemicalChest>> ADVANCED_ALCHEMICAL_CHEST = BLOCK_ENTITY_TYPES.register("advanced_alchemical_chest", () -> BlockEntityType.Builder.of(BlockEntityAdvancedAlchemicalChest::new, Arrays.stream(AdvancedAlchemicalChest.getBlocks()).toArray(Block[]::new)).build(null));
	public static final PEDeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityCondenserMK3>> CONDENSER_MK3 = BLOCK_ENTITY_TYPES.register("condenser_mk3", () -> BlockEntityType.Builder.of(BlockEntityCondenserMK3::new, ExpansionBlocks.CONDENSER_MK3.getBlock()).build(null));
}
