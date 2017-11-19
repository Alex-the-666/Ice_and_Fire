package com.github.alexthe666.iceandfire.client.gui.bestiary;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class IndexPageButton extends GuiButton {

	public IndexPageButton(int id, int x, int y, String buttonText) {
		super(id, x, y, 160, 32, buttonText);
		this.width = 160;
		this.height = 32;
	}

	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
			FontRenderer fontrenderer = (FontRenderer) IceAndFire.PROXY.getFontRenderer();
			mc.getTextureManager().bindTexture(new ResourceLocation("iceandfire:textures/gui/bestiary/widgets.png"));
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			int i = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			this.drawTexturedModalRect(this.x, this.y, 0, this.hovered ? 32 : 0, this.width, this.height);
			this.mouseDragged(mc, mouseX, mouseY);
			int j = 0X303030;
			fontrenderer.drawString(this.displayString, (float) (this.x + this.width / 2 - fontrenderer.getStringWidth(this.displayString) / 2), (float) this.y + (this.height - 8) / 2, this.hovered ? 0XFAE67D : 0X303030, false);
		}
	}
}
