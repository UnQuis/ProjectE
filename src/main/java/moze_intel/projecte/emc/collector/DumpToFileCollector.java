package moze_intel.projecte.emc.collector;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import moze_intel.projecte.api.mapper.arithmetic.IValueArithmetic;
import moze_intel.projecte.api.mapper.collector.IExtendedMappingCollector;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.api.conversion.CustomConversion;
import moze_intel.projecte.api.conversion.CustomConversionFile;
import moze_intel.projecte.impl.codec.PECodecHelper;

public class DumpToFileCollector<A extends IValueArithmetic<?>> extends AbstractMappingCollector<NormalizedSimpleStack, Long, A> {

	public static String currentGroupName = "default";
	private final CustomConversionFile out = new CustomConversionFile();
	private final IExtendedMappingCollector<NormalizedSimpleStack, Long, A> inner;
	private final Path path;

	public DumpToFileCollector(Path path, IExtendedMappingCollector<NormalizedSimpleStack, Long, A> inner) {
		super(inner.getArithmetic());
		this.path = path;
		this.inner = inner;
	}

	@Override
	public void setValueFromConversion(int outnumber, NormalizedSimpleStack something, Map<NormalizedSimpleStack, Integer> ingredientsWithAmount) {
		inner.setValueFromConversion(outnumber, something, ingredientsWithAmount);
		if (something != null && !ingredientsWithAmount.containsKey(null)) {
			Map<NormalizedSimpleStack, Integer> dumpable = withoutRefundedIngredients(ingredientsWithAmount);
			if (dumpable != null) {
				out.values().addConversion(CustomConversion.getFor(outnumber, something, dumpable));
			}
		}
	}

	@Override
	public void addConversion(int outnumber, NormalizedSimpleStack output, Map<NormalizedSimpleStack, Integer> ingredientsWithAmount, A arithmeticForConversion) {
		inner.addConversion(outnumber, output, ingredientsWithAmount, arithmeticForConversion);
		if (output != null && !ingredientsWithAmount.containsKey(null)) {
			Map<NormalizedSimpleStack, Integer> dumpable = withoutRefundedIngredients(ingredientsWithAmount);
			if (dumpable != null) {
				out.getOrAddGroup(currentGroupName).addConversion(CustomConversion.getFor(outnumber, output, dumpable));
			}
		}
	}

	@Override
	public void setValueBefore(NormalizedSimpleStack something, Long value) {
		inner.setValueBefore(something, value);
		if (something != null && isDumpable(value)) {
			out.values().setValueBefore().put(something, value);
		}
	}

	@Override
	public void setValueAfter(NormalizedSimpleStack something, Long value) {
		inner.setValueAfter(something, value);
		if (something != null && isDumpable(value)) {
			out.values().setValueAfter().put(something, value);
		}
	}

	/**
	 * The dump format only accepts a positive value or the {@code free} sentinel, so the explicit zeroes the ore and
	 * raw ore blacklists set for every blacklisted item cannot be represented. Dumping them would make the whole file
	 * fail to encode, so they are left out instead.
	 */
	private static boolean isDumpable(Long value) {
		return value != null && value > 0;
	}

	/**
	 * Recipes whose ingredient has a crafting remainder record that ingredient with an amount of zero, meaning the item
	 * is involved in the recipe but its cost is fully refunded. The conversion format rejects a zero amount, so those
	 * entries are dropped before dumping. A conversion left without any ingredients cannot be represented either, and
	 * would describe a free output, so it is skipped.
	 *
	 * @return the ingredients that can be dumped, or {@code null} if nothing is left to dump
	 */
	private static Map<NormalizedSimpleStack, Integer> withoutRefundedIngredients(Map<NormalizedSimpleStack, Integer> ingredientsWithAmount) {
		Map<NormalizedSimpleStack, Integer> dumpable = new LinkedHashMap<>(ingredientsWithAmount.size());
		for (Map.Entry<NormalizedSimpleStack, Integer> entry : ingredientsWithAmount.entrySet()) {
			if (entry.getValue() > 0) {
				dumpable.put(entry.getKey(), entry.getValue());
			}
		}
		return dumpable.isEmpty() ? null : dumpable;
	}

	@Override
	public void finishCollection() {
		PECodecHelper.writeToFile(path, CustomConversionFile.CODEC, out, "custom conversion");
		inner.finishCollection();
	}
}
