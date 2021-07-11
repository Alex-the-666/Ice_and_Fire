package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.particle.LightningBoltData;
import com.github.alexthe666.iceandfire.client.particle.LightningRender;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityLightningDragon;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

public class RenderLightningDragon extends RenderDragonBase {

    private LightningRender lightningRender = new LightningRender();

    public RenderLightningDragon(EntityRendererManager manager, SegmentedModel model, int dragonType) {
        super(manager, model, dragonType);
    }

    public boolean shouldRender(EntityDragonBase livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else {
            EntityLightningDragon lightningDragon = (EntityLightningDragon)livingEntityIn;
            if (lightningDragon.hasLightningTarget()) {
                Vector3d Vector3d1 = lightningDragon.getHeadPosition();
                Vector3d Vector3d = new Vector3d(lightningDragon.getLightningTargetX(), lightningDragon.getLightningTargetY(), lightningDragon.getLightningTargetZ());
                return camera.isBoundingBoxInFrustum(new AxisAlignedBB(Vector3d1.x, Vector3d1.y, Vector3d1.z, Vector3d.x, Vector3d.y, Vector3d.z));
            }
            return false;
        }
    }

    public void render(EntityDragonBase entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        EntityLightningDragon lightningDragon = (EntityLightningDragon)entityIn;
        matrixStackIn.push();
        if (lightningDragon.hasLightningTarget()) {
            double dist = Minecraft.getInstance().player.getDistance(lightningDragon);
            if(dist <= Math.max(256, Minecraft.getInstance().gameSettings.renderDistanceChunks * 16F)){
                Vector3d Vector3d1 = lightningDragon.getHeadPosition();
                Vector3d Vector3d = new Vector3d(lightningDragon.getLightningTargetX(), lightningDragon.getLightningTargetY(), lightningDragon.getLightningTargetZ());
                float energyScale = 0.4F * lightningDragon.getRenderScale();
                LightningBoltData bolt = new LightningBoltData(LightningBoltData.BoltRenderInfo.ELECTRICITY, Vector3d1, Vector3d, 15)
                        .size(0.05F * getBoundedScale(energyScale, 0.5F, 2))
                        .lifespan(4)
                        .spawn(LightningBoltData.SpawnFunction.NO_DELAY);
                lightningRender.update(null, bolt, partialTicks);
                matrixStackIn.translate(-lightningDragon.getPosX(), -lightningDragon.getPosY(), -lightningDragon.getPosZ());
                lightningRender.render(partialTicks, matrixStackIn, bufferIn);
            }
        }
        matrixStackIn.pop();

    }

    private static float getBoundedScale(float scale, float min, float max) {
        return min + scale * (max - min);
    }
}
