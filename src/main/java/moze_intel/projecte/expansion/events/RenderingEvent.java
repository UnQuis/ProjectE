package moze_intel.projecte.expansion.events;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.registries.ExpansionBlockEntityTypes;
import moze_intel.projecte.expansion.rendering.ChestRenderer;
import moze_intel.projecte.expansion.util.IChestLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = PECore.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RenderingEvent {
	private RenderingEvent() {}

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		registerChest(event, ExpansionBlockEntityTypes.ADVANCED_ALCHEMICAL_CHEST.get());
		registerChest(event, ExpansionBlockEntityTypes.CONDENSER_MK3.get());
	}

	private static <BE extends BlockEntity & IChestLike> void registerChest(EntityRenderersEvent.RegisterRenderers event, BlockEntityType<BE> type) {
		event.registerBlockEntityRenderer(type, context -> new ChestRenderer<>(context, type));
	}
}
