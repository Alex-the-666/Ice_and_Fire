package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.model.ModelDragonEgg;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityEggInIce;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class RenderEggInIce<T extends TileEntityEggInIce> extends TileEntityRenderer<T> {

    public RenderEggInIce(TileEntityRendererDispatcher p_i226016_1_) {
        super(p_i226016_1_);
    }

    @Override
    public void render(T egg, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ModelDragonEgg model = new ModelDragonEgg();
        if (egg.type != null) {
            matrixStackIn.push();
            matrixStackIn.translate(0.5, -0.8F, 0.5F);
            matrixStackIn.push();
            model.renderFrozen(egg);
            model.render(matrixStackIn, bufferIn.getBuffer(RenderPodium.getEggTexture(egg.type)), combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.pop();
            matrixStackIn.pop();
        }
    }

}
