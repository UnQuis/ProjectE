package moze_intel.projecte.expansion.registries;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.capability.CapabilityAlchemicalBookLocations.AlchemicalBookLocationData;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ExpansionAttachmentTypes {

	public static final PEDeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = new PEDeferredRegister<>(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, PECore.MODID);

	public static final PEDeferredHolder<AttachmentType<?>, AttachmentType<AlchemicalBookLocationData>> ALCHEMICAL_BOOK_LOCATIONS = ATTACHMENT_TYPES.register("alchemical_book_locations",
			() -> AttachmentType.builder(AlchemicalBookLocationData::new)
					.serialize(AlchemicalBookLocationData.CODEC)
					.copyHandler(AlchemicalBookLocationData::copy)
					.copyOnDeath()
					.build()
	);
}
