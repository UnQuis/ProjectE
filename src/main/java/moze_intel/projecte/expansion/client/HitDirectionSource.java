package moze_intel.projecte.expansion.client;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * Client only "what side of the block is the player looking at" source, reachable from common code.
 * <p>
 * Some of the addon's menus need the side of the block the player is looking at, but menus are common code and are
 * also constructed on a dedicated server, where no such thing exists. Reading {@code Minecraft} directly from a
 * common class would make the class fail to load on a server (the client only classes cannot be resolved there), so
 * the common side only ever sees this interface, and the client installs the implementation.
 */
public final class HitDirectionSource {

	@Nullable
	private static volatile Source source;

	private HitDirectionSource() {
	}

	/**
	 * Installs the real source. Only ever called from client code.
	 */
	public static void setSource(@Nullable Source newSource) {
		source = newSource;
	}

	/**
	 * @return The side of the block the player is looking at, or null if that is unknown (always on a server, and on
	 * the client while the player is not aiming at a block)
	 */
	@Nullable
	public static Direction getDirection() {
		Source current = source;
		return current == null ? null : current.getDirection();
	}

	@FunctionalInterface
	public interface Source {
		@Nullable
		Direction getDirection();
	}
}
