package moze_intel.projecte.expansion.registries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.capability.CapabilityAlchemicalBookLocations.AlchemicalBookLocationData;
import moze_intel.projecte.expansion.util.TagNames;
import moze_intel.projecte.expansion.util.Util;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.impl.AttachmentTypeDeferredRegister;
import net.minecraft.core.UUIDUtil;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.UUID;

/**
 * Holds the data the expansion attaches to items and players.
 * <p>
 * Note: Minecraft 1.20.4 has no data components, so everything that was a component on 1.21.1 is a NeoForge data
 * attachment here, which is what ProjectE itself uses on this version (see
 * {@link moze_intel.projecte.gameObjs.registries.PEAttachmentTypes}).
 */
public class ExpansionAttachmentTypes {

	public static final AttachmentTypeDeferredRegister ATTACHMENT_TYPES = new AttachmentTypeDeferredRegister(PECore.MODID);

	/**
	 * The player a book/fuel item is bound to. Replaces {@code ExpansionDataComponentTypes.OWNER}.
	 */
	public record OwnerData(UUID uuid, String name) {
		public static final Codec<OwnerData> CODEC = RecordCodecBuilder.create(instance ->
				instance.group(UUIDUtil.CODEC.fieldOf(TagNames.OWNER).forGetter(OwnerData::uuid), Codec.STRING.fieldOf(TagNames.OWNER_NAME).forGetter(OwnerData::name)).apply(instance, OwnerData::new)
		);

		public boolean isNone() {
			return uuid == Util.DUMMY_UUID;
		}
	}

	/**
	 * The value {@link #OWNER} falls back to while no owner is set, matching what {@code Util#getOwner} has
	 * always returned for an unbound item.
	 */
	public static OwnerData createUnboundOwner() {
		return new OwnerData(Util.DUMMY_UUID, "None");
	}

	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<OwnerData>> OWNER = ATTACHMENT_TYPES.register("uuid",
			() -> AttachmentType.builder(ExpansionAttachmentTypes::createUnboundOwner)
					.serialize(OwnerData.CODEC)
					//OwnerData is an immutable record, so it can be shared between the source and the copy
					.copyHandler((holder, data) -> data)
					.comparator(OwnerData::equals)
					.copyOnDeath()
					.build()
	);

	/**
	 * The last time a knowledge sharing book was used, in game time. Replaces
	 * {@code ExpansionDataComponentTypes.LAST_USED}.
	 */
	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<Long>> LAST_USED = ATTACHMENT_TYPES.registerNonNegativeLong("last_used", 0L);

	/**
	 * How many items the last use of a knowledge sharing book learned. Replaces
	 * {@code ExpansionDataComponentTypes.KNOWLEDGE_GAINED}.
	 */
	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<Long>> KNOWLEDGE_GAINED = ATTACHMENT_TYPES.registerNonNegativeLong("knowledge_gained", 0L);

	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<AlchemicalBookLocationData>> ALCHEMICAL_BOOK_LOCATIONS = ATTACHMENT_TYPES.register("alchemical_book_locations",
			() -> AttachmentType.builder(AlchemicalBookLocationData::new)
					.serialize(AlchemicalBookLocationData.CODEC)
					.copyHandler((holder, data) -> data.copy(holder))
					.copyOnDeath()
					.build()
	);
}
