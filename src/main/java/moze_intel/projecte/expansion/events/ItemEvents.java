package moze_intel.projecte.expansion.events;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.registries.ExpansionAttributes;
import moze_intel.projecte.expansion.util.SunExposureHelper;
import moze_intel.projecte.expansion.util.Util;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

@Mod.EventBusSubscriber(modid = PECore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemEvents {
	private ItemEvents() {}

	@SubscribeEvent
	public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
		ItemStack stack = event.getItemStack();
		if (stack.is(SunExposureHelper.PROTECTIVE_ITEMS) && stack.getItem() instanceof ArmorItem armor) {
			//Note: 1.20.4 attributes are plain Attribute instances and are not bound to an equipment slot group
			EquipmentSlot slot = armor.getEquipmentSlot();
			event.addModifier(ExpansionAttributes.SUN_EXPOSURE_PROTECTION.get(), new AttributeModifier(Util.SUN_EXPOSURE_PROTECTION.apply(slot.getSerializedName()).toString(), 0.25D, AttributeModifier.Operation.ADDITION));
		}
	}
}
