package moze_intel.projecte.expansion.item;

import moze_intel.projecte.expansion.config.Config;
import moze_intel.projecte.expansion.registries.ExpansionDataComponentTypes;
import moze_intel.projecte.expansion.util.ColorStyle;
import moze_intel.projecte.expansion.util.EMCFormat;
import moze_intel.projecte.expansion.util.Lang;
import moze_intel.projecte.expansion.util.Util;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.function.Consumer;
import java.util.UUID;

public class ItemInfiniteFuel extends Item {

	public ItemInfiniteFuel(Properties properties) {
		super(properties.stacksTo(1).rarity(Rarity.RARE));
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, display, tooltip, tooltipFlag);
		tooltip.accept(Lang.Items.INFINITE_FUEL_TOOLTIP.translateColored(ChatFormatting.GRAY));
		tooltip.accept(Lang.COST.translateColored(ChatFormatting.RED, EMCFormat.getComponent(Config.server.infiniteFuelCost.get()).setStyle(ColorStyle.GRAY)));
	}

	/**
	 * Gives the stack the {@code minecraft:cooking_fuel} component with the configured burn time, or a burn time of
	 * {@code 0} (which makes furnaces refuse it) if its owner cannot afford the EMC cost.
	 * <p>
	 * Since 26.3 the burn time is a data component, and it can only be a constant or a reference to a loot context int
	 * provider, neither of which can look at the config or at the EMC of the owner. So the value is stamped onto the
	 * stacks instead: when the owner is set, and every time the furnace hands the item back after using it up.
	 */
	public static void stampBurnTime(ItemStack stack) {
		stack.set(DataComponents.COOKING_FUEL, new CookingFuel(new ResolvableInt.Constant(burnTime(stack)),
				new ResolvableFloat.Constant(1)));
	}

	/**
	 * How long a single unit of this fuel burns for, {@code 0} if the owner cannot afford it.
	 * <p>
	 * Note: 26.3 has no per item dynamic {@code Item#getBurnTime} hook anymore, the burn time is the static
	 * {@link net.minecraft.core.component.DataComponents#COOKING_FUEL} component, so this is only used to decide
	 * whether the item is usable as fuel at all.
	 */
	private static int burnTime(ItemStack stack) {
		@Nullable ExpansionDataComponentTypes.OwnerData owner = stack.get(ExpansionDataComponentTypes.OWNER);
		@Nullable IKnowledgeProvider provider = owner == null ? null : Util.getKnowledgeProvider(owner.uuid());
		if (owner == null || provider == null) return 0;
		return Config.server.infiniteFuelCost.get() == 0 || Config.server.infiniteFuelBurnTime.get() == 0 ? 0
				: provider.getEmc().compareTo(BigInteger.valueOf(Config.server.infiniteFuelCost.get())) < 0 ? 0
				: Config.server.infiniteFuelBurnTime.get();
	}

	//26.3 replaced hasCraftingRemainingItem/getCraftingRemainingItem with the ItemStackTemplate based override
	@Override
	public @Nullable ItemStackTemplate getCraftingRemainder(ItemInstance instance) {
		ExpansionDataComponentTypes.OwnerData ownerData = instance.get(ExpansionDataComponentTypes.OWNER);
		@Nullable UUID owner = ownerData == null ? null : ownerData.uuid();
		if (owner != null) {
			ServerPlayer player = Util.getPlayer(owner);
			@Nullable IKnowledgeProvider provider = Util.getKnowledgeProvider(owner);
			if (provider != null) {
				provider.setEmc(provider.getEmc().subtract(BigInteger.valueOf(Config.server.infiniteFuelCost.get())));
				if (player != null) provider.syncEmc(player);
			}
		}
		//The vanilla call sites always hand us the real stack, the fallback is only there for completeness
		ItemStack stack = instance instanceof ItemStack itemStack ? itemStack : new ItemStack(instance.typeHolder(), instance.count());
		//The remainder is a brand new stack, so it has to be told the current burn time again
		stampBurnTime(stack);
		return ItemStackTemplate.fromNonEmptyStack(stack);
	}
}
