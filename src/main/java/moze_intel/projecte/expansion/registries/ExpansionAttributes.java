package moze_intel.projecte.expansion.registries;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class ExpansionAttributes {

	public static final PEDeferredRegister<Attribute> ATTRIBUTES = new PEDeferredRegister<>(Registries.ATTRIBUTE, PECore.MODID);

	public static final PEDeferredHolder<Attribute, Attribute> SUN_EXPOSURE_PROTECTION = ATTRIBUTES.register("sun_exposure_protection", () -> new RangedAttribute(String.format("attribute.%s.sun_exposure_protection", PECore.MODID), 0, 0, 1).setSyncable(true));
}
