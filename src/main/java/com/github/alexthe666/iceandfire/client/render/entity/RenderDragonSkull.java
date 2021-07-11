package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.iceandfire.entity.EntityDragonSkull;
import com.github.alexthe666.iceandfire.enums.EnumDragonTextures;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderDragonSkull extends EntityRenderer<EntityDragonSkull> {

    public static final float[] growth_stage_1 = new float[]{1F, 3F};
    public static final float[] growth_stage_2 = new float[]{3F, 7F};
    public static final float[] growth_stage_3 = new float[]{7F, 12.5F};
    public static final float[] growth_stage_4 = new float[]{12.5F, 20F};
    public static final float[] growth_stage_5 = new float[]{20F, 30F};
    public float[][] growth_stages;
    private TabulaModel fireDragonModel;
    private TabulaModel lightningDragonModel;
    private TabulaModel iceDragonModel;

    public RenderDragonSkull(EntityRendererManager renderManager, SegmentedModel fireDragonModel, SegmentedModel iceDragonModel, SegmentedModel lightningDragonModel) {
        super(renderManager);
        growth_stages = new float[][]{growth_stage_1, growth_stage_2, growth_stage_3, growth_stage_4, growth_stage_5};
        this.fireDragonModel = (TabulaModel) fireDragonModel;
        this.iceDragonModel = (TabulaModel) iceDragonModel;
        this.lightningDragonModel = (TabulaModel) lightningDragonModel;
    }

    private static void setRotationAngles(ModelRenderer cube, float rotX, float rotY, float rotZ) {
        cube.rotateAngleX = rotX;
        cube.rotateAngleY = rotY;
        cube.rotateAngleZ = rotZ;
    }

    public void render(EntityDragonSkull entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        TabulaModel model;
        if (entity.getDragonType() == 2) {
            model = lightningDragonModel;
        } else if (entity.getDragonType() == 1) {
            model = iceDragonModel;
        } else {
            model = fireDragonModel;
        }
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityTranslucent(getEntityTexture(entity)));
        matrixStackIn.push();
        matrixStackIn.rotate(new Quaternion(Vector3f.XP, -180, true));
        matrixStackIn.rotate(new Quaternion(Vector3f.YN, 180 - entity.getYaw(), true));
        float f = 0.0625F;
        matrixStackIn.scale(1.0F,  1.0F, 1.0F);
        float size = getRenderSize(entity) / 3;
        matrixStackIn.scale(size, size, size);
        matrixStackIn.translate(0, entity.isOnWall() ? -0.24F : -0.12F, entity.isOnWall() ? 0.4F : 0.5F);
        model.resetToDefaultPose();
        setRotationAngles(model.getCube("Head"), entity.isOnWall() ? (float) Math.toRadians(50F) : 0F, 0, 0);
        model.getCube("Head").render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.pop();
    }
    public ResourceLocation getEntityTexture(EntityDragonSkull entity) {
        if (entity.getDragonType() == 2) {
            return EnumDragonTextures.getLightningDragonSkullTextures(entity);
        }
        if (entity.getDragonType() == 1) {
            return EnumDragonTextures.getIceDragonSkullTextures(entity);
        }
        return EnumDragonTextures.getFireDragonSkullTextures(entity);
    }


    public float getRenderSize(EntityDragonSkull skull) {
        float step = (growth_stages[skull.getDragonStage() - 1][1] - growth_stages[skull.getDragonStage() - 1][0]) / 25;
        if (skull.getDragonAge() > 125) {
            return growth_stages[skull.getDragonStage() - 1][0] + ((step * 25));
        }
        return growth_stages[skull.getDragonStage() - 1][0] + ((step * this.getAgeFactor(skull)));
    }

    private int getAgeFactor(EntityDragonSkull skull) {
        return (skull.getDragonStage() > 1 ? skull.getDragonAge() - (25 * (skull.getDragonStage() - 1)) : skull.getDragonAge());
    }

}
