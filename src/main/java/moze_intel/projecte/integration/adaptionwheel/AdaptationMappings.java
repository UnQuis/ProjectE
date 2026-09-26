package moze_intel.projecte.integration.adaptionwheel;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import moze_intel.projecte.PECore;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Data driven mapping of items to Adaption Wheel concepts: learning an item in the transmutation table teaches the
 * matching adaptation. Mappings live in {@code data/<namespace>/adaptation_mappings/<item path>.json} (the item id can
 * also be given explicitly with an {@code id} field), so any data pack can extend or override the mapping.
 */
public class AdaptationMappings extends SimpleJsonResourceReloadListener<AdaptationMappings.AdaptationMapping> {

	public static final AdaptationMappings INSTANCE = new AdaptationMappings();

	private static final String DIRECTORY = "adaptation_mappings";

	private volatile Map<Identifier, AdaptationMapping> mappings = Map.of();

	private AdaptationMappings() {
		super(AdaptationMapping.CODEC, FileToIdConverter.json(DIRECTORY));
	}

	public record AdaptationMapping(Optional<String> id, String concept, int levels, boolean instant, int ticks) {

		public static final Codec<AdaptationMapping> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.optionalFieldOf("id").forGetter(AdaptationMapping::id),
				Codec.STRING.fieldOf("concept").forGetter(AdaptationMapping::concept),
				Codec.INT.optionalFieldOf("levels", 1).forGetter(AdaptationMapping::levels),
				Codec.BOOL.optionalFieldOf("instant", false).forGetter(AdaptationMapping::instant),
				Codec.INT.optionalFieldOf("ticks", 0).forGetter(AdaptationMapping::ticks)
		).apply(instance, AdaptationMapping::new));

		/**
		 * @param fileId Id of the json file this mapping was read from
		 * @return The item this mapping applies to
		 */
		public Identifier itemId(Identifier fileId) {
			return id.map(Identifier::tryParse).orElse(fileId);
		}

		public int effectiveTicks() {
			return ticks > 0 ? ticks : 100;
		}
	}

	/**
	 * @return The mapping registered for the given item, or null if learning that item teaches nothing
	 */
	@Nullable
	public AdaptationMapping get(@NotNull ItemStack stack) {
		if (stack.isEmpty()) {
			return null;
		}
		Map<Identifier, AdaptationMapping> current = mappings;
		if (current.isEmpty()) {
			return null;
		}
		return current.get(BuiltInRegistries.ITEM.getKey(stack.getItem()));
	}

	@Override
	protected void apply(@NotNull Map<Identifier, AdaptationMapping> preparations, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
		Map<Identifier, AdaptationMapping> loaded = new HashMap<>();
		preparations.forEach((fileId, mapping) -> {
			Identifier item = mapping.itemId(fileId);
			if (item != null) {
				loaded.put(item, mapping);
			}
		});
		mappings = Map.copyOf(loaded);
		PECore.debugLog("Loaded {} adaptation mappings", loaded.size());
	}
}
