package moze_intel.projecte.expansion.registries;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.block.entity.BlockEntityCollector;
import moze_intel.projecte.expansion.block.entity.BlockEntityCondenserMK3;
import moze_intel.projecte.expansion.gui.container.ContainerArcaneTransmutationTablet;
import moze_intel.projecte.expansion.gui.container.ContainerBase;
import moze_intel.projecte.expansion.gui.container.ContainerCollector;
import moze_intel.projecte.expansion.gui.container.ContainerCondenserMK3Input;
import moze_intel.projecte.expansion.gui.container.ContainerCondenserMK3Output;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

@SuppressWarnings("unused")
public class ExpansionMenus {

	public static final ContainerTypeDeferredRegister MENUS = new ContainerTypeDeferredRegister(PECore.MODID);

	public static final ContainerTypeRegistryObject<ContainerCollector> COLLECTOR_TIER_1 = registerBlockEntity("collector_tier_1", BlockEntityCollector.class, ContainerCollector.Tier1::new);
	public static final ContainerTypeRegistryObject<ContainerCollector> COLLECTOR_TIER_2 = registerBlockEntity("collector_tier_2", BlockEntityCollector.class, ContainerCollector.Tier2::new);
	public static final ContainerTypeRegistryObject<ContainerCollector> COLLECTOR_TIER_3 = registerBlockEntity("collector_tier_3", BlockEntityCollector.class, ContainerCollector.Tier3::new);
	public static final ContainerTypeRegistryObject<ContainerCondenserMK3Input> CONDENSER_MK3_INPUT = registerBlockEntity("condenser_mk3_input", BlockEntityCondenserMK3.class, ContainerCondenserMK3Input::new);
	public static final ContainerTypeRegistryObject<ContainerCondenserMK3Output> CONDENSER_MK3_OUTPUT = registerBlockEntity("condenser_mk3_output", BlockEntityCondenserMK3.class, ContainerCondenserMK3Output::new);
	public static final ContainerTypeRegistryObject<ContainerArcaneTransmutationTablet> ARCANE_TRANSMUTATION_TABLET = MENUS.register("arcane_transmutation_tablet", ContainerArcaneTransmutationTablet::fromNetwork);

	/**
	 * {@link ContainerTypeDeferredRegister} only offers a block entity based registration method keyed off an
	 * {@link moze_intel.projecte.gameObjs.registration.INamedEntry}, while ProjectExpansion keyed its containers off plain names
	 * ({@code collector_tier_1} rather than {@code collector}), so the entry has to be built by hand here.
	 */
	public static <CONTAINER extends ContainerBase, BE extends BlockEntity> ContainerTypeRegistryObject<CONTAINER> registerBlockEntity(String name, Class<BE> blockEntityClass, ContainerTypeDeferredRegister.IBlockEntityContainerFactory<CONTAINER, BE> factory) {
		return MENUS.registerMenu(name, () -> IMenuTypeExtension.create((id, inv, buf) -> factory.create(id, inv, getBlockEntityFromBuf(buf, blockEntityClass))));
	}

	@OnlyIn(Dist.CLIENT)
	private static <BE extends BlockEntity> BE getBlockEntityFromBuf(FriendlyByteBuf buf, Class<BE> type) {
		BlockPos pos = buf.readBlockPos();
		BE blockEntity = WorldHelper.getBlockEntity(type, Minecraft.getInstance().level, pos);
		if (blockEntity == null) {
			throw new IllegalStateException("Client could not locate block entity at " + pos + " for block entity container. "
					+ "This is likely caused by a mod breaking client side block entity lookup");
		}
		return blockEntity;
	}
}
