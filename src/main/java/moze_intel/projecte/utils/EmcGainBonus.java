package moze_intel.projecte.utils;

import java.math.BigInteger;
import org.jetbrains.annotations.NotNull;

/**
 * Global hook for a percentage bonus applied to every EMC a player gains.
 * <p>
 * The bonus is set by optional integrations (currently the Adaption Wheel one, where every adaptation increases the
 * bonus). It is a plain value holder, so ProjectE itself never needs to know which mod set it.
 */
public final class EmcGainBonus {

	private static final BigInteger HUNDRED = BigInteger.valueOf(100);
	private static volatile BigInteger percent = BigInteger.ZERO;

	private EmcGainBonus() {
	}

	/**
	 * @param newPercent The bonus percent, values below zero are treated as zero
	 */
	public static void setPercent(@NotNull BigInteger newPercent) {
		percent = newPercent.signum() < 0 ? BigInteger.ZERO : newPercent;
	}

	public static BigInteger getPercent() {
		return percent;
	}

	/**
	 * Applies the current bonus to the given amount of gained EMC.
	 */
	@NotNull
	public static BigInteger apply(@NotNull BigInteger amount) {
		BigInteger current = percent;
		if (current.signum() == 0 || amount.signum() <= 0) {
			return amount;
		}
		return amount.add(amount.multiply(current).divide(HUNDRED));
	}
}
