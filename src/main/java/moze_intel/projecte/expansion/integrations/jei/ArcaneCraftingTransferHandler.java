package moze_intel.projecte.expansion.integrations.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.types.IRecipeType;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.expansion.gui.container.ContainerArcaneTransmutationTablet;
import moze_intel.projecte.expansion.net.packets.to_server.PacketArcaneTransmutationTabletRecipeTransfer;
import moze_intel.projecte.expansion.registries.ExpansionMenus;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

public class ArcaneCraftingTransferHandler implements IRecipeTransferHandler<ContainerArcaneTransmutationTablet, RecipeHolder<CraftingRecipe>> {
	public static final int BLUE_SLOT_HIGHLIGHT_COLOR = 1073742079;
	public static final int RED_SLOT_HIGHLIGHT_COLOR = 1727987712;
	public static final int BLUE_PLUS_BUTTON_COLOR = -2142943745;
	public static final int ORANGE_PLUS_BUTTON_COLOR = -2130729728;

	@Override
	public Class<ContainerArcaneTransmutationTablet> getContainerClass() {
		return ContainerArcaneTransmutationTablet.class;
	}

	@Override
	public Optional<MenuType<ContainerArcaneTransmutationTablet>> getMenuType() {
		return ExpansionMenus.ARCANE_TRANSMUTATION_TABLET.asOptional();
	}

	@Override
	public IRecipeType<RecipeHolder<CraftingRecipe>> getRecipeType() {
		return RecipeTypes.CRAFTING;
	}

	private List<Integer> findMissingSlots(IRecipeSlotsView recipe, ContainerArcaneTransmutationTablet container, Player player) {
		List<IRecipeSlotView> ingredients = recipe.getSlotViews();
		List<Integer> slots = new ArrayList<>();

		for(int i = 1; i < ingredients.size(); ++i) {
			//26.3: JEI 31 hands back a Stream of stacks rather than a List
			List<ItemStack> items = ingredients.get(i).getItemStacks().toList();
			boolean found = false;
			if (ingredients.get(i).isEmpty()) {
				found = true;
			} else {
				for (ItemStack stack : items) {
					if (player.getInventory().contains(stack) || container.getProvider().hasKnowledge(stack) && IEMCProxy.INSTANCE.getValue(stack) <= container.transmutationInventory.getAvailableEmcAsLong()) {
						found = true;
						break;
					}
				}
			}

			if (!found) {
				slots.add(i - 1);
			}
		}

		return slots;
	}

	@Override
	public @Nullable IRecipeTransferError transferRecipe(ContainerArcaneTransmutationTablet container, RecipeHolder<CraftingRecipe> recipe, IRecipeSlotsView iRecipeSlotsView, Player player, boolean transferAll, boolean doTransfer) {
		if (doTransfer) {
			List<List<ItemStack>> itemStack = new ArrayList<>();
			List<ItemStack> emptyStack = new ArrayList<>();
			emptyStack.add(ItemStack.EMPTY);

			for(int i = 1; i < iRecipeSlotsView.getSlotViews().size(); ++i) {
				List<ItemStack> stacks = iRecipeSlotsView.getSlotViews().get(i).getItemStacks().toList();
				if (stacks.isEmpty()) {
					itemStack.add(emptyStack);
				} else {
					itemStack.add(stacks);
				}
			}

			//26.3: serverbound payloads go through ClientPacketDistributor
			ClientPacketDistributor.sendToServer(new PacketArcaneTransmutationTabletRecipeTransfer(itemStack, transferAll));
			return null;
		} else {
			List<Integer> missing = this.findMissingSlots(iRecipeSlotsView, container, player);
			boolean plus = !missing.isEmpty();
			int color = plus ? ORANGE_PLUS_BUTTON_COLOR : BLUE_PLUS_BUTTON_COLOR;
			return new ErrorRenderer(iRecipeSlotsView, missing, color);
		}
	}

	private record ErrorRenderer(IRecipeSlotsView iRecipeSlotsView, List<Integer> missing, int color) implements IRecipeTransferError {
		@Override
		public Type getType() {
			return Type.COSMETIC;
		}

		@Override
		public int getButtonHighlightColor() {
			return 0;
		}

		@Override
		public void showError(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, IRecipeSlotsView slots, int recipeX, int recipeY) {
			//26.3: the GUI transform is a 2D Matrix3x2fStack, translate takes (x, y) only
			Matrix3x2fStack poseStack = guiGraphics.pose();
			poseStack.pushMatrix();
			poseStack.translate(recipeX, recipeY);

			for(int i = 0; i < this.iRecipeSlotsView.getSlotViews().size(); ++i) {
				if (this.missing.contains(i)) {
					this.iRecipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT).get(i).drawHighlight(guiGraphics, RED_SLOT_HIGHLIGHT_COLOR);
				}
			}

			poseStack.popMatrix();
		}
	}
}
