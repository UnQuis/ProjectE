package moze_intel.projecte.expansion.net;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.net.packets.IPacket;
import moze_intel.projecte.expansion.net.packets.to_client.ClearKnowledgePacket;
import moze_intel.projecte.expansion.net.packets.to_client.PacketOpenAlchemicalBookGUI;
import moze_intel.projecte.expansion.net.packets.to_client.PacketSyncAlchemicalBookLocations;
import moze_intel.projecte.expansion.net.packets.to_client.PacketUpdateCondenserLock;
import moze_intel.projecte.expansion.net.packets.to_client.PacketUpdateWindowBigInteger;
import moze_intel.projecte.expansion.net.packets.to_client.PacketUpdateWindowInt;
import moze_intel.projecte.expansion.net.packets.to_client.PacketUpdateWindowLong;
import moze_intel.projecte.expansion.net.packets.to_client.UpdateTransmutationTargetsPacket;
import moze_intel.projecte.expansion.net.packets.to_server.PacketArcaneTransmutationTabletRecipeTransfer;
import moze_intel.projecte.expansion.net.packets.to_server.PacketArcaneTransmutationTabletSmallButton;
import moze_intel.projecte.expansion.net.packets.to_server.PacketCreateTeleportLocation;
import moze_intel.projecte.expansion.net.packets.to_server.PacketDeleteTeleportLocation;
import moze_intel.projecte.expansion.net.packets.to_server.PacketTeleportBack;
import moze_intel.projecte.expansion.net.packets.to_server.PacketTeleportToLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Payload registration for everything that came in with ProjectExpansion.
 * <p>
 * Based off of {@link moze_intel.projecte.network.PacketHandler}. Every channel id built by {@link #rl(String)} is
 * prefixed with {@link #CHANNEL_PREFIX} because ProjectE already occupies a number of the plain names in the
 * {@code projecte} namespace ({@code clear_knowledge}, {@code update_transmutation_targets},
 * {@code update_condenser_lock} and {@code update_window_long}), and NeoForge refuses to register two payloads under
 * the same id.
 */
public final class ExpansionPacketHandler {

	/**
	 * Prepended to every channel id created by {@link #rl(String)} to keep it disjoint from ProjectE's own payloads.
	 */
	public static final String CHANNEL_PREFIX = "expansion_";

	/**
	 * Fallback protocol version, only used if the mod container has not been populated yet.
	 */
	private static final String FALLBACK_VERSION = "1.0.0";

	private ExpansionPacketHandler() {}

	/**
	 * Builds an {@code projecte:expansion_*} channel id.
	 */
	public static ResourceLocation rl(String name) {
		return PECore.rl(CHANNEL_PREFIX + name);
	}

	/**
	 * Called from {@link moze_intel.projecte.PECore} (or {@code ExpansionCore}) during mod construction.
	 */
	public static void register(IEventBus modEventBus) {
		modEventBus.addListener(RegisterPayloadHandlersEvent.class, event -> register(event.registrar(version())));
	}

	public static void register(PayloadRegistrar registrar) {
		//Client to server
		registrar.playToServer(PacketArcaneTransmutationTabletRecipeTransfer.TYPE, PacketArcaneTransmutationTabletRecipeTransfer.STREAM_CODEC, IPacket::handle);
		registrar.playToServer(PacketArcaneTransmutationTabletSmallButton.TYPE, PacketArcaneTransmutationTabletSmallButton.STREAM_CODEC, IPacket::handle);
		registrar.playToServer(PacketCreateTeleportLocation.TYPE, PacketCreateTeleportLocation.STREAM_CODEC, IPacket::handle);
		registrar.playToServer(PacketDeleteTeleportLocation.TYPE, PacketDeleteTeleportLocation.STREAM_CODEC, IPacket::handle);
		registrar.playToServer(PacketTeleportBack.TYPE, PacketTeleportBack.STREAM_CODEC, IPacket::handle);
		registrar.playToServer(PacketTeleportToLocation.TYPE, PacketTeleportToLocation.STREAM_CODEC, IPacket::handle);
		//Server to client
		registrar.playToClient(ClearKnowledgePacket.TYPE, ClearKnowledgePacket.STREAM_CODEC, IPacket::handle);
		registrar.playToClient(PacketOpenAlchemicalBookGUI.TYPE, PacketOpenAlchemicalBookGUI.STREAM_CODEC, IPacket::handle);
		registrar.playToClient(PacketSyncAlchemicalBookLocations.TYPE, PacketSyncAlchemicalBookLocations.STREAM_CODEC, IPacket::handle);
		registrar.playToClient(PacketUpdateCondenserLock.TYPE, PacketUpdateCondenserLock.STREAM_CODEC, IPacket::handle);
		registrar.playToClient(PacketUpdateWindowBigInteger.TYPE, PacketUpdateWindowBigInteger.STREAM_CODEC, IPacket::handle);
		registrar.playToClient(PacketUpdateWindowInt.TYPE, PacketUpdateWindowInt.STREAM_CODEC, IPacket::handle);
		registrar.playToClient(PacketUpdateWindowLong.TYPE, PacketUpdateWindowLong.STREAM_CODEC, IPacket::handle);
		registrar.playToClient(UpdateTransmutationTargetsPacket.TYPE, UpdateTransmutationTargetsPacket.STREAM_CODEC, IPacket::handle);
	}

	private static String version() {
		ModContainer container = PECore.MOD_CONTAINER;
		return container == null ? FALLBACK_VERSION : container.getModInfo().getVersion().toString();
	}
}
