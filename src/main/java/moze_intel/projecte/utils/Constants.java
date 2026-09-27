package moze_intel.projecte.utils;

import java.math.BigInteger;
import java.text.NumberFormat;
import org.jetbrains.annotations.Nullable;

public final class Constants {

	/**
	 * The formatter used to display EMC values. Addons that live in ProjectE can replace it, see
	 * {@link #setEmcFormatter(NumberFormat)}.
	 */
	public static NumberFormat EMC_FORMATTER = getFormatter();

	/**
	 * Replaces the formatter used to display EMC values.
	 *
	 * @param formatter The formatter to use, or null to restore our default formatter
	 * @apiNote The given formatter is expected to format numbers the same way our default one does, unless the addon
	 * using it intends to change how EMC values are displayed.
	 */
	public static void setEmcFormatter(@Nullable NumberFormat formatter) {
		EMC_FORMATTER = formatter == null ? getFormatter() : formatter;
	}

	private static NumberFormat getFormatter() {
		NumberFormat format = NumberFormat.getInstance();
		//Only ever use a single decimal point for our formatter,
		// because the majority of the time we are a whole number
		// except for when we are abbreviating
		format.setMaximumFractionDigits(1);
		return format;
	}

	public static final BigInteger MAX_EXACT_TRANSMUTATION_DISPLAY = BigInteger.valueOf(1_000_000_000_000L);
	public static final BigInteger MAX_INTEGER = BigInteger.valueOf(Integer.MAX_VALUE);
	public static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

	public static final float[] EXPLOSIVE_LENS_RADIUS = new float[]{4.0F, 8.0F, 12.0F, 16.0F, 16.0F, 16.0F, 16.0F, 16.0F};
	public static final long[] EXPLOSIVE_LENS_COST = new long[]{384, 768, 1536, 2304, 2304, 2304, 2304, 2304};

	public static final long BLOCK_ENTITY_MAX_EMC = Long.MAX_VALUE;

	public static final int MAX_CONDENSER_PROGRESS = 102;

	public static final int MAX_VEIN_SIZE = 250;
}