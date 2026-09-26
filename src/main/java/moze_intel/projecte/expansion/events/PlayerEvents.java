package moze_intel.projecte.expansion.events;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.capability.IAlchemicalBookLocationsProvider;
import moze_intel.projecte.expansion.registries.ExpansionAttributes;
import moze_intel.projecte.expansion.registries.ExpansionCapabilities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = PECore.MODID)
public class PlayerEvents {
	private PlayerEvents() {}

	@SubscribeEvent
	public static void cloneEvent(PlayerEvent.Clone event) {
		IAlchemicalBookLocationsProvider provider = event.getEntity().getCapability(ExpansionCapabilities.ALCHEMICAL_BOOK_LOCATIONS_ENTITY);
		if (provider != null) {
			provider.sync((ServerPlayer) event.getEntity());
		}
	}

	@SubscribeEvent
	public static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		IAlchemicalBookLocationsProvider provider = event.getEntity().getCapability(ExpansionCapabilities.ALCHEMICAL_BOOK_LOCATIONS_ENTITY);
		if (provider != null) {
			provider.sync((ServerPlayer) event.getEntity());
		}
	}

	@SubscribeEvent
	public static void respawnEvent(PlayerEvent.PlayerRespawnEvent event) {
		IAlchemicalBookLocationsProvider provider = event.getEntity().getCapability(ExpansionCapabilities.ALCHEMICAL_BOOK_LOCATIONS_ENTITY);
		if (provider != null) {
			provider.sync((ServerPlayer) event.getEntity());
		}
	}

	@EventBusSubscriber(modid = PECore.MODID, bus = EventBusSubscriber.Bus.MOD)
	public static class ModEvents {
		@SubscribeEvent
		public static void entityAttributeModification(EntityAttributeModificationEvent event) {
			event.add(EntityType.PLAYER, ExpansionAttributes.SUN_EXPOSURE_PROTECTION);
		}
	}
}
