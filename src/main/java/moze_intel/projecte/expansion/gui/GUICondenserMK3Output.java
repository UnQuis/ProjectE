package moze_intel.projecte.expansion.gui;

import moze_intel.projecte.PECore;
import moze_intel.projecte.expansion.gui.container.ContainerCondenserMK3Output;
import moze_intel.projecte.gameObjs.gui.PEContainerScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GUICondenserMK3Output extends PEContainerScreen<ContainerCondenserMK3Output> {
	public GUICondenserMK3Output(ContainerCondenserMK3Output container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title, 382, 252);
	}

	protected Identifier getTexture() {
		return PECore.rl("textures/gui/condenser_mk3_output.png");
	}

	@Override
	protected void extractLabels(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {}

	@Override
	public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		//26.3: the shader/blend state is part of the RenderType passed to blit now, the old
		//RenderSystem#setShader/setShaderColor/setShaderTexture calls have no equivalent and are gone.
		//Note: this texture is 382x252, so it has to be blitted against its own size rather than 256x256
		graphics.blit(RenderPipelines.GUI_TEXTURED, getTexture(), leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight, imageWidth, imageHeight);
	}
}
