package moze_intel.projecte.gameObjs.items.armor;

import java.util.List;
import java.util.function.Consumer;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.gameObjs.registries.PEArmorMaterials;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GemFeet extends GemArmorBase {

	private static final Vec3 VERTICAL_MOVEMENT = new Vec3(0, 0.1, 0);
	private static final boolean STEP_ASSIST_DEFAULT = false;

	/**
	 * Since 26.3 the FOV modifier is derived from the movement speed attribute, so the hurricane boots' speed bonus
	 * has to be compensated for on the client, see {@link moze_intel.projecte.events.PlayerRender}
	 */
	public static final AttributeModifier SPEED = new AttributeModifier(PECore.rl("armor"), 1.0, Operation.ADD_MULTIPLIED_TOTAL);
	public static final AttributeModifier STEP_ASSIST_MODIFIER = new AttributeModifier(PECore.rl("gem_step_assist"), 0.4, Operation.ADD_VALUE);

	public GemFeet(Properties props) {
		super(PEArmorMaterials.GEM_ARMOR, ArmorType.BOOTS, GemFeet::addBootsAttributes, props.component(PEDataComponentTypes.STEP_ASSIST, STEP_ASSIST_DEFAULT));
	}

	private static Properties addBootsAttributes(Properties props) {
		//The armor material set up the base armor attributes, we add the hurricane boots' speed bonus on top of those
		ItemAttributeModifiers attributes = PEArmorMaterials.GEM_ARMOR.createAttributes(ArmorType.BOOTS)
				.withModifierAdded(Attributes.MOVEMENT_SPEED, SPEED, EquipmentSlotGroup.FEET);
		return props.attributes(attributes);
	}

	public static void toggleStepAssist(ItemStack boots, Player player) {
		boolean oldValue = isStepAssist(boots);
		boots.set(PEDataComponentTypes.STEP_ASSIST, !oldValue);
		//26.3: armor modifiers come from the stack's component, so the step assist modifier has to be added to/removed from it
		ItemAttributeModifiers attributes = boots.get(DataComponents.ATTRIBUTE_MODIFIERS);
		if (attributes != null) {
			boots.set(DataComponents.ATTRIBUTE_MODIFIERS, oldValue ? removeModifier(attributes, STEP_ASSIST_MODIFIER.id())
					: attributes.withModifierAdded(Attributes.STEP_HEIGHT, STEP_ASSIST_MODIFIER, EquipmentSlotGroup.FEET));
		}
		player.sendSystemMessage(getComponent(!oldValue));
	}

	private static ItemAttributeModifiers removeModifier(ItemAttributeModifiers attributes, Identifier id) {
		List<ItemAttributeModifiers.Entry> remaining = attributes.modifiers().stream()
				.filter(entry -> !entry.modifier().id().equals(id))
				.toList();
		return new ItemAttributeModifiers(remaining);
	}

	private static boolean isJumpPressed(Player player) {
		if (FMLEnvironment.getDist().isClient() && player instanceof LocalPlayer clientPlayer) {
			return clientPlayer.input.keyPresses.jump();
		}
		return false;
	}

	/**
	 * Client side movement of the hurricane boots, as since 26.3 item ticks are only run on the server.
	 */
	public static void clientTick(Player player) {
		if (player.level().isClientSide() && player.getItemBySlot(EquipmentSlot.FEET).is(PEItems.GEM_BOOTS)) {
			//TODO: Do we want to try and make use of just applying Attributes.GRAVITY to the player instead? Default gravity is 0.08
			// A modifier of -0.75, Operation.ADD_MULTIPLIED_TOTAL makes it so that we fall at about the same rate as what we do below
			boolean flying = player.getAbilities().flying;
			if (!flying && isJumpPressed(player)) {
				player.addDeltaMovement(VERTICAL_MOVEMENT);
			}
			if (!player.onGround()) {
				Vec3 deltaMovement = player.getDeltaMovement();
				if (deltaMovement.y() <= 0) {
					player.setDeltaMovement(deltaMovement = deltaMovement.multiply(1, 0.9, 1));
				}
				if (!flying) {
					if (player.zza < 0) {//Moving backwards
						player.setDeltaMovement(deltaMovement.multiply(0.9, 1, 0.9));
					} else if (player.zza > 0 && deltaMovement.lengthSqr() < 3) {//Moving forwards
						player.setDeltaMovement(deltaMovement.multiply(1.1, 1, 1.1));
					}
				}
			}
		}
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel level, @NotNull Entity entity, @Nullable EquipmentSlot slot) {
		super.inventoryTick(stack, level, entity, slot);
		if (isArmorSlot(slot) && entity instanceof Player player) {
			player.resetFallDistance();
		}
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, context, display, tooltip, flags);
		tooltip.accept(PELang.GEM_LORE_FEET.translate());
		tooltip.accept(PELang.STEP_ASSIST_PROMPT.translate(ClientKeyHelper.getKeyName(PEKeybind.BOOTS_TOGGLE)));
		tooltip.accept(getComponent(isStepAssist(stack)));
	}

	private static boolean isStepAssist(ItemStack stack) {
		return stack.getOrDefault(PEDataComponentTypes.STEP_ASSIST, STEP_ASSIST_DEFAULT);
	}

	private static Component getComponent(boolean enabled) {
		if (enabled) {
			return PELang.STEP_ASSIST.translate(ChatFormatting.GREEN, PELang.GEM_ENABLED);
		}
		return PELang.STEP_ASSIST.translate(ChatFormatting.RED, PELang.GEM_DISABLED);
	}
}
