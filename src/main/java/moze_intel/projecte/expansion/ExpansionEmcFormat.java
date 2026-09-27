package moze_intel.projecte.expansion;

import java.math.BigDecimal;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import moze_intel.projecte.expansion.util.EMCFormat;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

/**
 * A {@link NumberFormat} that can display EMC values in an abbreviated (1.23M) format.
 * <p>
 * While {@link ExpansionSettings#abbreviateEmc()} is disabled this formats values exactly like ProjectE does,
 * so it is safe to always install as ProjectE's EMC formatter.
 * <p>
 * Note: The abbreviated format itself is implemented by {@link EMCFormat}, which uses the expansion's config
 * to decide if it is active, and is only ever used here on the client as it checks if shift is being held.
 */
public class ExpansionEmcFormat extends NumberFormat {

	/**
	 * A copy of ProjectE's default EMC formatting, used whenever the abbreviated format is disabled
	 */
	private static final NumberFormat VANILLA = Util.make(NumberFormat.getInstance(), formatter -> formatter.setMaximumFractionDigits(1));

	public static final ExpansionEmcFormat INSTANCE = new ExpansionEmcFormat();

	private ExpansionEmcFormat() {}

	/**
	 * @return If the abbreviated format should currently be used
	 */
	private static boolean isAbbreviated() {
		return ExpansionSettings.abbreviateEmc();
	}

	/**
	 * Formats the given EMC value, using the abbreviated format if it is enabled.
	 */
	public static Component formatEmc(Number emc) {
		if (isAbbreviated()) {
			return EMCFormat.getComponent(new BigDecimal(emc.toString()));
		}
		return PELang.EMC.translate(emc);
	}

	@Override
	public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
		if (isAbbreviated()) {
			return toAppendTo.append(EMCFormat.format(new BigDecimal(Double.toString(number)), EMCFormat.FormatOptions.create()));
		}
		return VANILLA.format(number, toAppendTo, pos);
	}

	@Override
	public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
		if (isAbbreviated()) {
			return toAppendTo.append(EMCFormat.format(BigDecimal.valueOf(number), EMCFormat.FormatOptions.create()));
		}
		return VANILLA.format(number, toAppendTo, pos);
	}

	@Override
	public Number parse(String source, ParsePosition pos) {
		//Note: We never parse EMC values back out of a string, so we just use the default parsing
		return VANILLA.parse(source, pos);
	}
}
