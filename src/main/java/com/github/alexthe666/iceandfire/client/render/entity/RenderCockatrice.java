package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelCockatrice;
import com.github.alexthe666.iceandfire.client.model.ModelCockatriceChick;
import com.github.alexthe666.iceandfire.entity.EntityCockatrice;
import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderCockatrice extends MobRenderer<EntityCockatrice, SegmentedModel<EntityCockatrice>> {

    public static final ResourceLocation TEXTURE_ROOSTER = new ResourceLocation("iceandfire:textures/models/cockatrice/cockatrice_0.png");
    public static final ResourceLocation TEXTURE_HEN = new ResourceLocation("iceandfire:textures/models/cockatrice/cockatrice_1.png");
    public static final RenderType TEXTURE_BEAM = RenderType.getEntityCutoutNoCull(new ResourceLocation("iceandfire:textures/models/cockatrice/beam.png"));
    public static final ResourceLocation TEXTURE_ROOSTER_CHICK = new ResourceLocation("iceandfire:textures/models/cockatrice/cockatrice_0_chick.png");
    public static final ResourceLocation TEXTURE_HEN_CHICK = new ResourceLocation("iceandfire:textures/models/cockatrice/cockatrice_1_chick.png");
    public static final ModelCockatrice ADULT_MODEL = new ModelCockatrice();
    public static final ModelCockatriceChick BABY_MODEL = new ModelCockatriceChick();

    public RenderCockatrice(EntityRendererManager renderManager) {
        super(renderManager, new ModelCockatrice(), 0.6F);
    }

    private static void func_229108_a_(IVertexBuilder p_229108_0_, Matrix4f p_229108_1_, Matrix3f p_229108_2_, float p_229108_3_, float p_229108_4_, float p_229108_5_, int p_229108_6_, int p_229108_7_, int p_229108_8_, float p_229108_9_, float p_229108_10_) {
        p_229108_0_.pos(p_229108_1_, p_229108_3_, p_229108_4_, p_229108_5_).color(p_229108_6_, p_229108_7_, p_229108_8_, 255).tex(p_229108_9_, p_229108_10_).overlay(OverlayTexture.NO_OVERLAY).lightmap(15728880).normal(p_229108_2_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private Vector3d getPosition(LivingEntity LivingEntityIn, double p_177110_2_, float p_177110_4_) {
        double d0 = LivingEntityIn.lastTickPosX + (LivingEntityIn.getPosX() - LivingEntityIn.lastTickPosX) * (double) p_177110_4_;
        double d1 = p_177110_2_ + LivingEntityIn.lastTickPosY + (LivingEntityIn.getPosY() - LivingEntityIn.lastTickPosY) * (double) p_177110_4_;
        double d2 = LivingEntityIn.lastTickPosZ + (LivingEntityIn.getPosZ() - LivingEntityIn.lastTickPosZ) * (double) p_177110_4_;
        return new Vector3d(d0, d1, d2);
    }

    public boolean shouldRender(EntityCockatrice livingEntityIn, ClippingHelper camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) {
            return true;
        } else {
            if (livingEntityIn.hasTargetedEntity()) {
                LivingEntity livingentity = livingEntityIn.getTargetedEntity();
                if (livingentity != null) {
                    Vector3d Vector3d = this.getPosition(livingentity, (double) livingentity.getHeight() * 0.5D, 1.0F);
                    Vector3d Vector3d1 = this.getPosition(livingEntityIn, livingEntityIn.getEyeHeight(), 1.0F);
                    return camera.isBoundingBoxInFrustum(new AxisAlignedBB(Vector3d1.x, Vector3d1.y, Vector3d1.z, Vector3d.x, Vector3d.y, Vector3d.z));
                }
            }

            return false;
        }
    }

    public void render(EntityCockatrice entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (entityIn.isChild()) {
            entityModel = BABY_MODEL;
        } else {
            entityModel = ADULT_MODEL;
        }
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        LivingEntity livingentity = entityIn.getTargetedEntity();
        boolean blindness = entityIn.isPotionActive(Effects.BLINDNESS) || livingentity != null && livingentity.isPotionActive(Effects.BLINDNESS);
        if (!blindness && livingentity != null && EntityGorgon.isEntityLookingAt(entityIn, livingentity, EntityCockatrice.VIEW_RADIUS) && EntityGorgon.isEntityLookingAt(livingentity, entityIn, EntityCockatrice.VIEW_RADIUS)) {
            if (livingentity != null) {
                float f = entityIn.getAttackAnimationScale(partialTicks);
                float f1 = (float) entityIn.world.getGameTime() + partialTicks;
                float f2 = f1 * 0.5F % 1.0F;
                float f3 = entityIn.getEyeHeight();
                matrixStackIn.push();
                matrixStackIn.translate(0.0D, f3, 0.0D);
                Vector3d Vector3d = this.getPosition(livingentity, (double) livingentity.getHeight() * 0.5D, partialTicks);
                Vector3d Vector3d1 = this.getPosition(entityIn, f3, partialTicks);
                Vector3d Vector3d2 = Vector3d.subtract(Vector3d1);
                float f4 = (float) (Vector3d2.length() + 1.0D);
                Vector3d2 = Vector3d2.normalize();
                float f5 = (float) Math.acos(Vector3d2.y);
                float f6 = (float) Math.atan2(Vector3d2.z, Vector3d2.x);
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees((((float) Math.PI / 2F) - f6) * (180F / (float) Math.PI)));
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f5 * (180F / (float) Math.PI)));
                int i = 1;
                float f7 = f1 * 0.05F * -1.5F;
                float f8 = f * f;
                int j = 64 + (int) (f8 * 191.0F);
                int k = 32 + (int) (f8 * 191.0F);
                int l = 128 - (int) (f8 * 64.0F);
                float f9 = 0.2F;
                float f10 = 0.282F;
                float f11 = MathHelper.cos(f7 + 2.3561945F) * 0.282F;
                float f12 = MathHelper.sin(f7 + 2.3561945F) * 0.282F;
                float f13 = MathHelper.cos(f7 + ((float) Math.PI / 4F)) * 0.282F;
                float f14 = MathHelper.sin(f7 + ((float) Math.PI / 4F)) * 0.282F;
                float f15 = MathHelper.cos(f7 + 3.926991F) * 0.282F;
                float f16 = MathHelper.sin(f7 + 3.926991F) * 0.282F;
                float f17 = MathHelper.cos(f7 + 5.4977875F) * 0.282F;
                float f18 = MathHelper.sin(f7 + 5.4977875F) * 0.282F;
                float f19 = MathHelper.cos(f7 + (float) Math.PI) * 0.2F;
                float f20 = MathHelper.sin(f7 + (float) Math.PI) * 0.2F;
                float f21 = MathHelper.cos(f7 + 0.0F) * 0.2F;
                float f22 = MathHelper.sin(f7 + 0.0F) * 0.2F;
                float f23 = MathHelper.cos(f7 + ((float) Math.PI / 2F)) * 0.2F;
                float f24 = MathHelper.sin(f7 + ((float) Math.PI / 2F)) * 0.2F;
                float f25 = MathHelper.cos(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
                float f26 = MathHelper.sin(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
                float f27 = 0.0F;
                float f28 = 0.4999F;
                float f29 = -1.0F + f2;
                float f30 = f4 * 2.5F + f29;
                IVertexBuilder ivertexbuilder = bufferIn.getBuffer(TEXTURE_BEAM);
                MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
                Matrix4f matrix4f = matrixstack$entry.getMatrix();
                Matrix3f matrix3f = matrixstack$entry.getNormal();
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f19, f4, f20, j, k, l, 0.4999F, f30);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f19, 0.0F, f20, j, k, l, 0.4999F, f29);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f21, 0.0F, f22, j, k, l, 0.0F, f29);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f21, f4, f22, j, k, l, 0.0F, f30);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f23, f4, f24, j, k, l, 0.4999F, f30);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f23, 0.0F, f24, j, k, l, 0.4999F, f29);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f25, 0.0F, f26, j, k, l, 0.0F, f29);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f25, f4, f26, j, k, l, 0.0F, f30);
                float f31 = 0.0F;
                if (entityIn.ticksExisted % 2 == 0) {
                    f31 = 0.5F;
                }

                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f17, f4, f18, j, k, l, 1.0F, f31);
                func_229108_a_(ivertexbuilder, matrix4f, matrix3f, f15, f4, f16, j, k, l, 0.5F, f31);
                matrixStackIn.pop();
            }
        }

    }

    @Override
    protected void preRenderCallback(EntityCockatrice entity, MatrixStack matrixStackIn, float partialTickTime) {
        if (entity.isChild()) {
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
        }
    }

    @Override
    public ResourceLocation getEntityTexture(EntityCockatrice cockatrice) {
        if (cockatrice.isChild()) {
            return cockatrice.isHen() ? TEXTURE_HEN_CHICK : TEXTURE_ROOSTER_CHICK;
        } else {
            return cockatrice.isHen() ? TEXTURE_HEN : TEXTURE_ROOSTER;
        }
    }

}
