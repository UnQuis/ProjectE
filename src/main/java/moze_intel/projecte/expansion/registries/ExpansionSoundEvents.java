package moze_intel.projecte.expansion.registries;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registration.impl.SoundEventDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.SoundEventRegistryObject;
import net.minecraft.sounds.SoundEvent;

public class ExpansionSoundEvents {

	public static final SoundEventDeferredRegister SOUND_EVENTS = new SoundEventDeferredRegister(PECore.MODID);

	public static final SoundEventRegistryObject<SoundEvent> KNOWLEDGE_SHARING_BOOK_STORE = SOUND_EVENTS.register("knowledge_sharing_book.store");
	public static final SoundEventRegistryObject<SoundEvent> KNOWLEDGE_SHARING_BOOK_USE = SOUND_EVENTS.register("knowledge_sharing_book.use");
	public static final SoundEventRegistryObject<SoundEvent> KNOWLEDGE_SHARING_BOOK_USE_NONE = SOUND_EVENTS.register("knowledge_sharing_book.use_none");
	public static final SoundEventRegistryObject<SoundEvent> ALCHEMICAL_COLLECTION_COLLECT = SOUND_EVENTS.register("alchemical_collection.collect");
}
