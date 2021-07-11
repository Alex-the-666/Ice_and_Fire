package com.github.alexthe666.iceandfire.client.gui;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDragonforge;
import com.github.alexthe666.iceandfire.inventory.ContainerDragonForge;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiDragonForge extends ContainerScreen<ContainerDragonForge> {
    private static final ResourceLocation TEXTURE_FIRE = new ResourceLocation("iceandfire:textures/gui/dragonforge_fire.png");
    private static final ResourceLocation TEXTURE_ICE = new ResourceLocation("iceandfire:textures/gui/dragonforge_ice.png");
    private static final ResourceLocation TEXTURE_LIGHTNING = new ResourceLocation("iceandfire:textures/gui/dragonforge_lightning.png");
    private final PlayerInventory playerInventory;
    private ContainerDragonForge tileFurnace;
    private int dragonType;

    public GuiDragonForge(ContainerDragonForge container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.playerInventory = inv;
        this.tileFurnace = container;
        if (tileFurnace instanceof ContainerDragonForge) {
            this.dragonType = tileFurnace.isFire;
        }
    }

    protected void drawGuiContainerForegroundLayer(MatrixStack stack, int mouseX, int mouseY) {
        FontRenderer font = this.getMinecraft().fontRenderer;
        if (tileFurnace != null) {
            String s = I18n.format("block.iceandfire.dragonforge_" + DragonType.getNameFromInt(dragonType) + "_core");
            font.drawString(stack, s, this.xSize / 2 - font.getStringWidth(s) / 2, 6, 4210752);
        }
        font.drawString(stack, this.playerInventory.getDisplayName().getString(), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (dragonType == 0) {
            this.getMinecraft().getTextureManager().bindTexture(TEXTURE_FIRE);
        } else if (dragonType == 1) {
            this.getMinecraft().getTextureManager().bindTexture(TEXTURE_ICE);
        } else{
            this.getMinecraft().getTextureManager().bindTexture(TEXTURE_LIGHTNING);
        }
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.blit(matrixStack, k, l, 0, 0, this.xSize, this.ySize);
        int i1 = this.func_175381_h(126);
        this.blit(matrixStack, k + 12, l + 23, 0, 166, i1, 38);
    }

    private int func_175381_h(int p_175381_1_) {
        TileEntity te = IceAndFire.PROXY.getRefrencedTE();
        int j = 0;
        int maxCookTime = 1000;
        if (te instanceof TileEntityDragonforge) {
            maxCookTime = ((TileEntityDragonforge) te).getMaxCookTime(tileFurnace.getSlot(0).getStack(), tileFurnace.getSlot(1).getStack());
            j = Math.min(((TileEntityDragonforge) te).cookTime, maxCookTime);
        }
        return j != 0 ? j * p_175381_1_ / maxCookTime : 0;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

}