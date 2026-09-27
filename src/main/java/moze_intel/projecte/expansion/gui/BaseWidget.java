package moze_intel.projecte.expansion.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class BaseWidget extends AbstractWidget {
	private final int uOffset, vOffset, uWidth, vHeight;
	private final Identifier texture;
	public BaseWidget(Identifier texture, int x, int y, int imageWidth, int imageHeight) {
		this(texture, x, y, imageWidth, imageHeight, x, y, imageWidth, imageHeight);
	}

	public BaseWidget(Identifier texture, int x, int y, int imageWidth, int imageHeight, int uOffset, int vOffset, int uWidth, int vHeight) {
		super(x, y, imageWidth, imageHeight, Component.empty());
		this.uOffset = uOffset;
		this.vOffset = vOffset;
		this.uWidth = uWidth;
		this.vHeight = vHeight;
		this.texture = texture;
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), uOffset, vOffset, uWidth, vHeight, 256, 256);
		handleCursor(graphics);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
