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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

/**
 * Payload registration for everything that came in with ProjectExpansion.
 * <p>
 * Based off of {@link moze_intel.projecte.network.PacketHandler}. Every channel id built by {@link #rl(String)} is
 * prefixed with {@link #CHANNEL_PREFIX} because ProjectE already occupies a number of the plain names in the
 * {@code projecte} namespace ({@code clear_knowledge}, {@code update_transmutation_targets},
 * {@code update_condenser_lock} and {@code update_window_long}), and NeoForge refuses to register two payloads under
 * the same id.
 * <p>
 * Note: 1.20.4 has no payload stream codecs, so every payload is registered with a
 * {@link FriendlyByteBuf.Reader} and its direction is declared through the direction aware handler builder.
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

	private ExpansionPacketHandler() {
	}

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
		modEventBus.addListener(RegisterPayloadHandlerEvent.class, event -> register(event.registrar(PECore.MODID).versioned(version())));
	}

	public static void register(IPayloadRegistrar registrar) {
		//Client to server
		serverbound(registrar, PacketArcaneTransmutationTabletRecipeTransfer.ID, PacketArcaneTransmutationTabletRecipeTransfer::new);
		serverbound(registrar, PacketArcaneTransmutationTabletSmallButton.ID, PacketArcaneTransmutationTabletSmallButton::new);
		serverbound(registrar, PacketCreateTeleportLocation.ID, PacketCreateTeleportLocation::new);
		serverbound(registrar, PacketDeleteTeleportLocation.ID, PacketDeleteTeleportLocation::new);
		serverbound(registrar, PacketTeleportBack.ID, PacketTeleportBack::new);
		serverbound(registrar, PacketTeleportToLocation.ID, PacketTeleportToLocation::new);
		//Server to client
		clientbound(registrar, ClearKnowledgePacket.ID, ClearKnowledgePacket::new);
		clientbound(registrar, PacketOpenAlchemicalBookGUI.ID, PacketOpenAlchemicalBookGUI::new);
		clientbound(registrar, PacketSyncAlchemicalBookLocations.ID, PacketSyncAlchemicalBookLocations::new);
		clientbound(registrar, PacketUpdateCondenserLock.ID, PacketUpdateCondenserLock::new);
		clientbound(registrar, PacketUpdateWindowBigInteger.ID, PacketUpdateWindowBigInteger::new);
		clientbound(registrar, PacketUpdateWindowInt.ID, PacketUpdateWindowInt::new);
		clientbound(registrar, PacketUpdateWindowLong.ID, PacketUpdateWindowLong::new);
		clientbound(registrar, UpdateTransmutationTargetsPacket.ID, UpdateTransmutationTargetsPacket::new);
	}

	private static <MSG extends IPacket> void serverbound(IPayloadRegistrar registrar, ResourceLocation id, FriendlyByteBuf.Reader<MSG> reader) {
		registrar.play(id, reader, builder -> builder.server(IPacket::handleMainThread));
	}

	private static <MSG extends IPacket> void clientbound(IPayloadRegistrar registrar, ResourceLocation id, FriendlyByteBuf.Reader<MSG> reader) {
		registrar.play(id, reader, builder -> builder.client(IPacket::handleMainThread));
	}

	private static String version() {
		ModContainer container = PECore.MOD_CONTAINER;
		return container == null ? FALLBACK_VERSION : container.getModInfo().getVersion().toString();
	}
}
