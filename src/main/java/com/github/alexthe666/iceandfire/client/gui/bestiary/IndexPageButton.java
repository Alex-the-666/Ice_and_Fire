package com.github.alexthe666.iceandfire.client.gui.bestiary;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IndexPageButton extends Button {

    public IndexPageButton(int id, int x, int y, ITextComponent buttonText, net.minecraft.client.gui.widget.button.Button.IPressable butn) {
        super(x, y, 160, 32, buttonText, butn);
        this.width = 160;
        this.height = 32;
    }

    public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
        if (this.active) {
            FontRenderer fontrenderer = IafConfig.useVanillaFont ? Minecraft.getInstance().fontRenderer : (FontRenderer) IceAndFire.PROXY.getFontRenderer();
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation("iceandfire:textures/gui/bestiary/widgets.png"));
            boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            this.blit(matrixStack, this.x, this.y, 0, flag ? 32 : 0, this.width, this.height  );
            int j =  flag ? 0XFAE67D : 0X303030;
            fontrenderer.func_238422_b_(matrixStack, this.getMessage().func_241878_f(), (this.x + this.width / 2  - fontrenderer.getStringWidth(this.getMessage().getString()) / 2), this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }
    }
}
