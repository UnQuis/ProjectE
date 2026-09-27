package moze_intel.projecte.expansion.events;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.registries.ExpansionAttributes;
import moze_intel.projecte.expansion.util.SunExposureHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 26.3 note: {@code EventBusSubscriber} no longer has a {@code bus} attribute;
 * {@link ItemAttributeModifierEvent} is a game bus event so nothing has to be declared.
 */
@EventBusSubscriber(modid = PECore.MODID)
public class ItemEvents {
	private ItemEvents() {}

	@SubscribeEvent
	public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
		ItemStack stack = event.getItemStack();
		if (stack.is(SunExposureHelper.PROTECTIVE_ITEMS)) {
			//26.3: ArmorItem is gone, armor is now a data driven Equippable component. HEAD/CHEST/LEGS/FEET plus the
			//vanilla BODY slot are the slots ArmorItem used to cover, which is EquipmentSlot.Type.HUMANOID_ARMOR
			//and ANIMAL_ARMOR in 26.3
			Equippable equippable = getArmorEquippable(stack);
			if (equippable != null) {
				EquipmentSlotGroup slot = EquipmentSlotGroup.bySlot(equippable.slot());
				//AttributeModifier now takes an Identifier, built the same way Util#SUN_EXPOSURE_PROTECTION does
				event.addModifier(ExpansionAttributes.SUN_EXPOSURE_PROTECTION, new AttributeModifier(PECore.rl(String.format("sun_exposure_protection_%s", slot.getSerializedName())), 0.25D, AttributeModifier.Operation.ADD_VALUE), slot);
			}
		}
	}

	/**
	 * @return the {@link Equippable} of the stack if it goes into one of the armor slots, null otherwise
	 */
	private static @Nullable Equippable getArmorEquippable(ItemStack stack) {
		Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
		if (equippable == null) {
			return null;
		}
		EquipmentSlot.Type type = equippable.slot().getType();
		return type == EquipmentSlot.Type.HUMANOID_ARMOR || type == EquipmentSlot.Type.ANIMAL_ARMOR ? equippable : null;
	}
}
