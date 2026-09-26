package moze_intel.projecte.utils;

import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Per player percentage bonus applied to every EMC the player gains.
 * <p>
 * The bonus is set by optional integrations (currently the Adaption Wheel one, where every adaptation the player has
 * increases the bonus). It is a plain value holder, so ProjectE itself never needs to know which mod set it.
 */
public final class EmcGainBonus {

	private static final BigInteger HUNDRED = BigInteger.valueOf(100);
	private static final Map<UUID, BigInteger> PERCENT = new ConcurrentHashMap<>();

	private EmcGainBonus() {
	}

	/**
	 * @param player   The player the bonus belongs to, or null to clear a global default
	 * @param newPercent The bonus percent, values below zero are treated as zero
	 */
	public static void setPercent(@Nullable UUID player, @NotNull BigInteger newPercent) {
		if (player == null || newPercent.signum() <= 0) {
			PERCENT.remove(player);
		} else {
			PERCENT.put(player, newPercent);
		}
	}

	public static BigInteger getPercent(@Nullable UUID player) {
		if (player == null) {
			return BigInteger.ZERO;
		}
		return PERCENT.getOrDefault(player, BigInteger.ZERO);
	}

	/**
	 * Applies the current bonus of the given player to the given amount of gained EMC.
	 */
	@NotNull
	public static BigInteger apply(@NotNull BigInteger amount, @Nullable UUID player) {
		BigInteger current = getPercent(player);
		if (current.signum() == 0 || amount.signum() <= 0) {
			return amount;
		}
		return amount.add(amount.multiply(current).divide(HUNDRED));
	}
}
