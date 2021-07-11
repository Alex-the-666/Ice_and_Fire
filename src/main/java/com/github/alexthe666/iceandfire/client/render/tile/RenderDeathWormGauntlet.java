package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.model.ModelDeathWormGauntlet;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDeathWorm;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.ItemStack;

public class RenderDeathWormGauntlet {
    private static final ModelDeathWormGauntlet MODEL = new ModelDeathWormGauntlet();

    public void renderItem(ItemStack stack, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        RenderType texture;

        if (stack.getItem() == IafItemRegistry.DEATHWORM_GAUNTLET_RED) {
            texture = RenderType.getEntityCutout(RenderDeathWorm.TEXTURE_RED);
        } else if (stack.getItem() == IafItemRegistry.DEATHWORM_GAUNTLET_WHITE) {
            texture = RenderType.getEntityCutout(RenderDeathWorm.TEXTURE_WHITE);
        } else {
            texture = RenderType.getEntityCutout(RenderDeathWorm.TEXTURE_YELLOW);
        }
        matrixStackIn.push();
        matrixStackIn.translate(0.5F, 0.5F, 0.5F);
        matrixStackIn.push();
        matrixStackIn.push();
        MODEL.animate(stack, Minecraft.getInstance().getRenderPartialTicks());
        MODEL.render(matrixStackIn, bufferIn.getBuffer(texture), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.pop();
        matrixStackIn.pop();
        matrixStackIn.pop();
    }
}
