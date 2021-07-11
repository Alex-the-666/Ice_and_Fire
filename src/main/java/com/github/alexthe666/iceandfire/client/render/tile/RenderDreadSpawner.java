package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadSpawner;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.spawner.AbstractSpawner;

public class RenderDreadSpawner<T extends TileEntityDreadSpawner> extends TileEntityRenderer<T> {

    public RenderDreadSpawner(TileEntityRendererDispatcher p_i226016_1_) {
        super(p_i226016_1_);
    }

    public void render(TileEntityDreadSpawner tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.push();
        matrixStackIn.translate(0.5D, 0.0D, 0.5D);
        AbstractSpawner abstractspawner = tileEntityIn.getSpawnerBaseLogic();
        Entity entity = abstractspawner.getCachedEntity();
        if (entity != null) {
            float f = 0.53125F;
            float f1 = Math.max(entity.getWidth(), entity.getHeight());
            if ((double) f1 > 1.0D) {
                f /= f1;
            }

            matrixStackIn.translate(0.0D, 0.4F, 0.0D);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees((float) MathHelper.lerp(partialTicks, abstractspawner.getPrevMobRotation(), abstractspawner.getMobRotation()) * 10.0F));
            matrixStackIn.translate(0.0D, -0.2F, 0.0D);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(-30.0F));
            matrixStackIn.scale(f, f, f);
            Minecraft.getInstance().getRenderManager().renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
        }

        matrixStackIn.pop();
    }
}