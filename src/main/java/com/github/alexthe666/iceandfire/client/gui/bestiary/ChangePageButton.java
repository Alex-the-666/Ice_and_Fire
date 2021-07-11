package com.github.alexthe666.iceandfire.client.gui.bestiary;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.gui.widget.button.Button.IPressable;

@OnlyIn(Dist.CLIENT)
public class ChangePageButton extends Button {
    private final boolean right;
    public int lastpage = 1;
    private int page;
    private int color;

    public ChangePageButton(int x, int y, boolean right, int bookpage, int color, IPressable press) {
        super(x, y, 23, 10, new StringTextComponent(""), press);
        this.right = right;
        page = bookpage;
        this.color = color;
    }

    @Override
    public void renderWidget(MatrixStack matrixStack,  int mouseX, int mouseY, float partial) {
        if (this.active) {
            boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation("iceandfire:textures/gui/bestiary/widgets.png"));
            int i = 0;
            int j = 64;
            if (flag) {
                i += 23;
            }

            if (!this.right) {
                j += 13;
            }
            j += color * 23;

            this.blit(matrixStack, this.x, this.y, i, j, width, height);
        }
    }
}