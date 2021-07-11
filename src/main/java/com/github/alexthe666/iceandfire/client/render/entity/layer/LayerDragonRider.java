package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.iceandfire.ClientProxy;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityDreadQueen;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.client.renderer.entity.model.QuadrupedModel;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerDragonRider extends LayerRenderer<EntityDragonBase, SegmentedModel<EntityDragonBase>> {
    private final MobRenderer render;
    private final boolean excludeDreadQueenMob;

    public LayerDragonRider(MobRenderer renderIn, boolean excludeDreadQueenMob) {
        super(renderIn);
        this.render = renderIn;
        this.excludeDreadQueenMob = excludeDreadQueenMob;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, EntityDragonBase dragon, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStackIn.push();
        if (!dragon.getPassengers().isEmpty()) {
            float dragonScale = dragon.getRenderSize() / 3;
            for (Entity passenger : dragon.getPassengers()) {
                boolean prey = dragon.getControllingPassenger() == null || dragon.getControllingPassenger().getEntityId() != passenger.getEntityId();
                if (excludeDreadQueenMob && passenger instanceof EntityDreadQueen) {
                    prey = false;
                }
                ClientProxy.currentDragonRiders.remove(passenger.getUniqueID());
                float riderRot = passenger.prevRotationYaw + (passenger.rotationYaw - passenger.prevRotationYaw) * partialTicks;
                int animationTicks = 0;
                if (dragon.getAnimation() == EntityDragonBase.ANIMATION_SHAKEPREY) {
                    animationTicks = dragon.getAnimationTick();
                }
                if (animationTicks == 0 || animationTicks >= 15) {
                    translateToBody(matrixStackIn);
                }
                if (prey) {
                    if (animationTicks == 0 || animationTicks >= 15 || dragon.isFlying()) {
                        translateToHead(matrixStackIn);
                        offsetPerDragonType(dragon.dragonType, matrixStackIn);
                        EntityRenderer render = Minecraft.getInstance().getRenderManager().getRenderer(passenger);
                        EntityModel modelBase = null;
                        if (render instanceof MobRenderer) {
                            modelBase = ((MobRenderer) render).getEntityModel();
                        }
                        if ((passenger.getHeight() > passenger.getWidth() || modelBase instanceof BipedModel) && !(modelBase instanceof QuadrupedModel) && !(modelBase instanceof HorseModel)) {
                            matrixStackIn.translate(-0.15F * passenger.getHeight(), 0.1F * dragonScale - 0.1F * passenger.getHeight(), -0.1F * dragonScale - 0.1F * passenger.getWidth());
                            matrixStackIn.rotate(new Quaternion(Vector3f.ZP, 90, true));
                            matrixStackIn.rotate(new Quaternion(Vector3f.YP, 45, true));
                        } else {
                            boolean horse = modelBase instanceof HorseModel;
                            matrixStackIn.translate((horse ? -0.08F : -0.15F) * passenger.getWidth(), 0.1F * dragonScale - 0.15F * passenger.getWidth(), -0.1F * dragonScale - 0.1F * passenger.getWidth());
                            matrixStackIn.rotate(new Quaternion(Vector3f.XN, 90, true));
                        }
                    } else {
                        matrixStackIn.translate(0, 0.555F * dragonScale, -0.5F * dragonScale);
                    }

                } else {
                    matrixStackIn.translate(0, -0.01F * dragonScale, -0.035F * dragonScale);
                }
                matrixStackIn.push();
                matrixStackIn.rotate(new Quaternion(Vector3f.ZP, 180, true));
                matrixStackIn.rotate(new Quaternion(Vector3f.YP, riderRot + 180, true));
                matrixStackIn.scale(1 / dragonScale, 1 / dragonScale, 1 / dragonScale);
                matrixStackIn.translate(0, -0.25F, 0);
                renderEntity(passenger, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, matrixStackIn, bufferIn, packedLightIn);
                matrixStackIn.pop();
                ClientProxy.currentDragonRiders.add(passenger.getUniqueID());
            }
        }
        matrixStackIn.pop();
    }

    protected void translateToBody(MatrixStack stack) {
        postRender(((TabulaModel) this.render.getEntityModel()).getCube("BodyUpper"), stack, 0.0625F);
        postRender(((TabulaModel) this.render.getEntityModel()).getCube("Neck1"), stack, 00.0625F);
    }

    protected void translateToHead(MatrixStack stack) {
        postRender(((TabulaModel) this.render.getEntityModel()).getCube("Neck2"), stack, 0.0625F);
        postRender(((TabulaModel) this.render.getEntityModel()).getCube("Neck3"), stack, 0.0625F);
        postRender(((TabulaModel) this.render.getEntityModel()).getCube("Head"), stack, 0.0625F);
    }

    protected void postRender(AdvancedModelBox renderer, MatrixStack matrixStackIn, float scale) {
        if (renderer.rotateAngleX == 0.0F && renderer.rotateAngleY == 0.0F && renderer.rotateAngleZ == 0.0F) {
            if (renderer.rotationPointX != 0.0F || renderer.rotationPointY != 0.0F || renderer.rotationPointZ != 0.0F) {
                matrixStackIn.translate(renderer.rotationPointX * scale, renderer.rotationPointY * scale, renderer.rotationPointZ * scale);
            }
        } else {
            matrixStackIn.translate(renderer.rotationPointX * scale, renderer.rotationPointY * scale, renderer.rotationPointZ * scale);

            if (renderer.rotateAngleZ != 0.0F) {
                matrixStackIn.rotate(Vector3f.ZP.rotation(renderer.rotateAngleZ));
            }

            if (renderer.rotateAngleY != 0.0F) {
                matrixStackIn.rotate(Vector3f.YP.rotation(renderer.rotateAngleY));
            }

            if (renderer.rotateAngleX != 0.0F) {
                matrixStackIn.rotate(Vector3f.XP.rotation(renderer.rotateAngleX));
            }
        }
    }

    private void offsetPerDragonType(DragonType dragonType, MatrixStack stackIn){
        if(dragonType == DragonType.LIGHTNING){
            stackIn.translate(0.1F, -0.2F, -0.1F);
        }
    }


    public <E extends Entity> void renderEntity(E entityIn, double x, double y, double z, float yaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLight) {
        EntityRenderer<? super E> render = null;
        EntityRendererManager manager = Minecraft.getInstance().getRenderManager();
        try {
            render = manager.getRenderer(entityIn);

            if (render != null) {
                try {
                    render.render(entityIn, 0, partialTicks, matrixStack, bufferIn, packedLight);
                } catch (Throwable throwable1) {
                    throw new ReportedException(CrashReport.makeCrashReport(throwable1, "Rendering entity in world"));
                }
            }
        } catch (Throwable throwable3) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable3, "Rendering entity in world");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being rendered");
            entityIn.fillCrashReport(crashreportcategory);
            CrashReportCategory crashreportcategory1 = crashreport.makeCategory("Renderer details");
            crashreportcategory1.addDetail("Assigned renderer", render);
            crashreportcategory1.addDetail("Location", CrashReportCategory.getCoordinateInfo(x, y, z));
            crashreportcategory1.addDetail("Rotation", Float.valueOf(yaw));
            crashreportcategory1.addDetail("Delta", Float.valueOf(partialTicks));
            throw new ReportedException(crashreport);
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }


}