package moze_intel.projecte.emc.components.processor;

import it.unimi.dsi.fastutil.objects.Reference2LongMap;
import it.unimi.dsi.fastutil.objects.Reference2LongMaps;
import it.unimi.dsi.fastutil.objects.Reference2LongOpenHashMap;
import java.util.function.ToLongFunction;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.components.DataComponentProcessor;
import moze_intel.projecte.config.PEConfigTranslations;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

@DataComponentProcessor
public class ArmorTrimProcessor extends PersistentComponentProcessor<ArmorTrim> {

	/**
	 * Trim materials no longer reference an item directly (items reference the material instead, through the
	 * {@code minecraft:provides_trim_material} component), and the trim pattern to template item mapping is gone as well.
	 * We instead resolve the EMC of the material/template from the items that provide them, using the item tags that
	 * vanilla defines for them.
	 */
	private static final TagKey<Item> TRIM_TEMPLATES = TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("trim_templates"));

	@NotNull
	private Reference2LongMap<Holder<TrimMaterial>> materialEmcLookup = Reference2LongMaps.emptyMap();
	private long templateEmc;

	@Override
	public String getName() {
		return PEConfigTranslations.DCP_ARMOR_TRIM.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.DCP_ARMOR_TRIM.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.DCP_ARMOR_TRIM.tooltip();
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long recalculateEMC(@NotNull ItemInfo info, @Range(from = 1, to = Long.MAX_VALUE) long currentEMC, @NotNull ArmorTrim trim) throws ArithmeticException {
		long materialEmc = materialEmcLookup.getLong(trim.material());
		if (materialEmc == 0) {
			//The material for the trim doesn't have an EMC value, so there is no valid EMC value for the applied trim as a whole
			return 0;
		}
		if (templateEmc == 0) {
			//The template for the trim doesn't have an EMC value, and given the template is consumed: there is no valid EMC value for the applied trim as a whole
			return 0;
		}
		return Math.addExact(
				Math.addExact(currentEMC, materialEmc),
				templateEmc
		);
	}

	@Override
	public void updateCachedValues(@Nullable ToLongFunction<ItemInfo> emcLookup) {
		if (emcLookup == null) {
			materialEmcLookup = Reference2LongMaps.emptyMap();
			templateEmc = 0;
			return;
		}
		Reference2LongMap<Holder<TrimMaterial>> materialEmcLookup = new Reference2LongOpenHashMap<>();
		for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(ItemTags.TRIM_MATERIALS)) {
			Item item = holder.value();
			Holder<TrimMaterial> material = item.getDefaultInstance().get(DataComponents.PROVIDES_TRIM_MATERIAL);
			if (material != null) {
				long emc = emcLookup.applyAsLong(ItemInfo.fromItem(item));
				if (emc > 0) {
					materialEmcLookup.mergeLong(material, emc, Math::min);
				}
			}
		}
		this.materialEmcLookup = materialEmcLookup;
		templateEmc = 0;
		for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(TRIM_TEMPLATES)) {
			long emc = emcLookup.applyAsLong(ItemInfo.fromItem(holder.value()));
			if (emc > 0 && (templateEmc == 0 || emc < templateEmc)) {
				templateEmc = emc;
			}
		}
	}

	@Override
	protected boolean validItem(@NotNull ItemInfo info) {
		return info.getItem().is(ItemTags.TRIMMABLE_ARMOR);
	}

	@Override
	protected boolean shouldPersist(@NotNull ItemInfo info, @NotNull ArmorTrim component) {
		return true;
	}

	@Override
	protected DataComponentType<ArmorTrim> getComponentType(@NotNull ItemInfo info) {
		return DataComponents.TRIM;
	}
}