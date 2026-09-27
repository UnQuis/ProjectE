package moze_intel.projecte.expansion.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.gui.container.ContainerCondenserMK3Input;
import moze_intel.projecte.gameObjs.gui.PEContainerScreen;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.TransmutationEMCFormatter;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GUICondenserMK3Input extends PEContainerScreen<ContainerCondenserMK3Input> {
	public GUICondenserMK3Input(ContainerCondenserMK3Input container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title, 255, 233);
	}

	protected Identifier getTexture() {
		return PECore.rl("textures/gui/condenser_mk3_input.png");
	}

	@Override
	public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		//26.3: the shader/blend state is part of the RenderType passed to blit now, the old
		//RenderSystem#setShader/setShaderColor/setShaderTexture calls have no equivalent and are gone
		graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

		int progress = menu.getProgressScaled();
		graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos + 33, topPos + 10, 0, 235, progress, 10, 256, 256);
	}

	@Override
	protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int x, int y) {
		//Don't render title or inventory as we don't have space
		long toDisplay = Math.min(menu.displayEmc.get(), menu.requiredEmc.get());
		Component emc = TransmutationEMCFormatter.formatEMC(toDisplay);
		graphics.text(font, emc, 140, 10, 0xFF404040, false);
	}

	@Override
	protected void extractTooltip(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		long toDisplay = Math.min(menu.displayEmc.get(), menu.requiredEmc.get());

		if (toDisplay < 1e12) {
			super.extractTooltip(graphics, mouseX, mouseY);
			return;
		}

		int emcLeft = 140 + leftPos;
		int emcRight = emcLeft + 110;
		int emcTop = 6 + topPos;
		int emcBottom = emcTop + 15;

		if (mouseX > emcLeft && mouseX < emcRight && mouseY > emcTop && mouseY < emcBottom) {
			graphics.setTooltipForNextFrame(PELang.EMC_TOOLTIP.translate(EMCHelper.formatEmc(toDisplay)), mouseX, mouseY);
		} else {
			super.extractTooltip(graphics, mouseX, mouseY);
		}
	}
}
