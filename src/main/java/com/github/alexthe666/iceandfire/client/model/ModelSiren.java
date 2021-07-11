package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.iceandfire.client.model.util.EntityModelPartBuilder;
import com.github.alexthe666.iceandfire.entity.EntitySiren;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;

public class ModelSiren extends ModelDragonBase<EntitySiren> {
    public AdvancedModelBox Tail_1;
    public AdvancedModelBox Tail_2;
    public AdvancedModelBox Body;
    public AdvancedModelBox Fin1;
    public AdvancedModelBox Tail_3;
    public AdvancedModelBox Fin2;
    public AdvancedModelBox FlukeL;
    public AdvancedModelBox FlukeR;
    public AdvancedModelBox Fin3;
    public AdvancedModelBox Left_Arm;
    public AdvancedModelBox Head;
    public AdvancedModelBox Right_Arm;
    public AdvancedModelBox Neck;
    public AdvancedModelBox Hair1;
    public AdvancedModelBox HairR;
    public AdvancedModelBox HairL;
    public AdvancedModelBox Mouth;
    public AdvancedModelBox Jaw;
    public AdvancedModelBox Hair2;
    private ModelAnimator animator;

    public ModelSiren() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.Left_Arm = new AdvancedModelBox(this, 40, 16);
        this.Left_Arm.mirror = true;
        this.Left_Arm.setRotationPoint(5.0F, -10.0F, 0.0F);
        this.Left_Arm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
        this.setRotateAngle(Left_Arm, -0.6981317007977318F, 0.0F, 0.0F);
        this.HairR = new AdvancedModelBox(this, 80, 16);
        this.HairR.setRotationPoint(-1.8F, -7.8F, 3.2F);
        this.HairR.addBox(-1.9F, -10.7F, -0.3F, 2, 11, 4, 0.0F);
        this.setRotateAngle(HairR, -2.5830872929516078F, 0.0F, 0.08726646259971647F);
        this.Mouth = new AdvancedModelBox(this, 38, 0);
        this.Mouth.setRotationPoint(0.5F, -1.3F, 0.0F);
        this.Mouth.addBox(-2.5F, -0.6F, -4.6F, 4, 3, 2, 0.0F);
        this.setRotateAngle(Mouth, -0.36425021489121656F, 0.0F, 0.0F);
        this.Fin2 = new AdvancedModelBox(this, 72, 34);
        this.Fin2.setRotationPoint(0.0F, 5.8F, 1.9F);
        this.Fin2.addBox(-1.0F, -5.5F, 0.8F, 1, 11, 4, 0.0F);
        this.Tail_3 = new AdvancedModelBox(this, 52, 34);
        this.Tail_3.setRotationPoint(0.0F, 10.4F, 0.1F);
        this.Tail_3.addBox(-3.0F, 0.0F, -1.9F, 6, 13, 4, 0.0F);
        this.Neck = new AdvancedModelBox(this, 40, 8);
        this.Neck.setRotationPoint(0.0F, -12.0F, 0.0F);
        this.Neck.addBox(-3.0F, -3.7F, -1.0F, 6, 4, 1, 0.0F);
        this.Hair2 = new AdvancedModelBox(this, 81, 16);
        this.Hair2.setRotationPoint(0.0F, -1.5F, 2.9F);
        this.Hair2.addBox(-3.5F, -11.9F, 0.2F, 7, 11, 3, 0.0F);
        this.setRotateAngle(Hair2, -0.22759093446006054F, 0.0F, 0.0F);
        this.Fin3 = new AdvancedModelBox(this, 72, 15);
        this.Fin3.setRotationPoint(0.0F, 6.1F, 1.9F);
        this.Fin3.addBox(-0.9F, -5.5F, 0.3F, 1, 13, 3, 0.0F);
        this.Fin1 = new AdvancedModelBox(this, 84, 34);
        this.Fin1.setRotationPoint(0.0F, 6.1F, 1.9F);
        this.Fin1.addBox(-1.0F, -5.4F, 0.8F, 1, 11, 3, 0.0F);
        this.Tail_1 = new AdvancedModelBox(this, 0, 35);
        this.Tail_1.setRotationPoint(0.0F, 22.2F, -0.2F);
        this.Tail_1.addBox(-4.0F, -0.1F, -1.8F, 8, 11, 5, 0.1F);
        this.setRotateAngle(Tail_1, 1.5707963267948966F, 0.0F, 0.0F);
        this.Head = new AdvancedModelBox(this, 0, 0);
        this.Head.setRotationPoint(0.0F, -12.0F, 0.0F);
        this.Head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        this.setRotateAngle(Head, -0.4553564018453205F, 0.0F, 0.0F);
        this.FlukeL = new AdvancedModelBox(this, 106, 34);
        this.FlukeL.setRotationPoint(0.0F, 12.3F, 0.1F);
        this.FlukeL.addBox(-3.5F, -0.1F, -0.5F, 7, 11, 1, 0.0F);
        this.setRotateAngle(FlukeL, -0.03490658503988659F, -0.08726646259971647F, -0.5235987755982988F);
        this.Tail_2 = new AdvancedModelBox(this, 27, 34);
        this.Tail_2.setRotationPoint(0.0F, 10.4F, 0.1F);
        this.Tail_2.addBox(-3.5F, 0.0F, -1.9F, 7, 11, 5, 0.0F);
        this.FlukeR = new AdvancedModelBox(this, 106, 34);
        this.FlukeR.mirror = true;
        this.FlukeR.setRotationPoint(0.0F, 12.3F, 0.1F);
        this.FlukeR.addBox(-3.5F, -0.1F, -0.5F, 7, 11, 1, 0.0F);
        this.setRotateAngle(FlukeR, -0.03490658503988659F, 0.08726646259971647F, 0.5235987755982988F);
        this.Right_Arm = new AdvancedModelBox(this, 40, 16);
        this.Right_Arm.setRotationPoint(-5.0F, -10.0F, 0.0F);
        this.Right_Arm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, 0.0F);
        this.setRotateAngle(Right_Arm, -0.6981317007977318F, 0.045553093477052F, 0.0F);
        this.Hair1 = new AdvancedModelBox(this, 80, 16);
        this.Hair1.setRotationPoint(0.0F, -7.8F, 3.2F);
        this.Hair1.addBox(-3.5F, -10.7F, -0.3F, 7, 11, 4, 0.0F);
        this.setRotateAngle(Hair1, -2.1855012893472994F, 0.0F, 0.0F);
        this.Jaw = new AdvancedModelBox(this, 24, 0);
        this.Jaw.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Jaw.addBox(-2.0F, -0.6F, -4.6F, 4, 1, 3, 0.0F);
        this.setRotateAngle(Jaw, 0.045553093477052F, 0.0F, 0.0F);
        this.Body = new AdvancedModelBox(this, 16, 16);
        this.Body.setRotationPoint(0.0F, 0.9F, 1.0F);
        this.Body.addBox(-4.0F, -12.0F, -2.0F, 8, 12, 4, 0.0F);
        this.setRotateAngle(Body, -0.8196066167365371F, 0.0F, 0.0F);
        this.HairL = new AdvancedModelBox(this, 80, 16);
        this.HairL.mirror = true;
        this.HairL.setRotationPoint(1.8F, -7.3F, 3.2F);
        this.HairL.addBox(0.1F, -10.7F, -0.3F, 2, 11, 4, 0.0F);
        this.setRotateAngle(HairL, -2.5830872929516078F, 0.0F, -0.08726646259971647F);
        this.Body.addChild(this.Left_Arm);
        this.Head.addChild(this.HairR);
        this.Head.addChild(this.Mouth);
        this.Tail_2.addChild(this.Fin2);
        this.Tail_2.addChild(this.Tail_3);
        this.Body.addChild(this.Neck);
        this.Hair1.addChild(this.Hair2);
        this.Tail_3.addChild(this.Fin3);
        this.Tail_1.addChild(this.Fin1);
        this.Body.addChild(this.Head);
        this.Tail_3.addChild(this.FlukeL);
        this.Tail_1.addChild(this.Tail_2);
        this.Tail_3.addChild(this.FlukeR);
        this.Body.addChild(this.Right_Arm);
        this.Head.addChild(this.Hair1);
        this.Head.addChild(this.Jaw);
        this.Tail_1.addChild(this.Body);
        this.Head.addChild(this.HairL);
        animator = ModelAnimator.create();
        this.updateDefaultPose();
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(Tail_1);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return EntityModelPartBuilder.getAllPartsFromClass(this.getClass(), this.getClass().getName());
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntitySiren.ANIMATION_BITE);
        animator.startKeyframe(5);
        this.rotate(animator, Mouth, -28, 0, 0);
        this.rotate(animator, Jaw, 7, 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.endKeyframe();
        animator.setAnimation(EntitySiren.ANIMATION_PULL);
        animator.startKeyframe(5);
        this.rotate(animator, Left_Arm, -103, 5, 0);
        this.rotate(animator, Right_Arm, -103, -5, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        this.rotate(animator, Left_Arm, 103, 5, 0);
        this.rotate(animator, Right_Arm, 103, -5, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.endKeyframe();
    }

    public void setRotationAngles(EntitySiren entity, float f, float f1, float f2, float f3, float f4) {
        animate(entity, f, f1, f2, f3, f4, 1);
        float speed_walk = 0.6F;
        float speed_idle = 0.05F;
        float degree_walk = 1F;
        float degree_idle = 0.5F;
        AdvancedModelBox[] TAIL_NO_BASE = {Tail_2, Tail_3};
        this.walk(Hair1, speed_idle * 1F, degree_idle * 0.3F, false, 2, 0F, f2, 1);
        this.walk(Hair2, speed_idle * 1F, degree_idle * 0.2F, false, 2, 0F, f2, 1);
        this.swing(HairL, speed_idle, degree_idle * 0.4F, true, 0F, -0.4F, f2, 1);
        this.swing(HairR, speed_idle, degree_idle * 0.4F, false, 0F, -0.4F, f2, 1);
        this.walk(Body, speed_idle * 1F, degree_idle * 0.3F, false, 2, 0F, f2, 1);
        this.walk(Right_Arm, speed_idle * 1F, degree_idle * 0.2F, true, 0, 0.1F, f2, 1);
        this.walk(Left_Arm, speed_idle * 1F, degree_idle * 0.2F, true, 0, 0.1F, f2, 1);
        this.walk(Body, speed_idle * 1F, degree_idle * 0.2F, false, 0, -0.1F, f2, 1);
        this.progressRotation(Body, entity.swimProgress, (float) Math.toRadians(-2F), 0.0F, 0.0F);
        this.progressRotation(Head, entity.swimProgress, (float) Math.toRadians(-70), 0.0F, 0.0F);
        this.progressRotation(Left_Arm, entity.swimProgress, (float) Math.toRadians(-15), 0.0F, 0.0F);
        this.progressRotation(Right_Arm, entity.swimProgress, (float) Math.toRadians(-15), 0.0F, 0.0F);
        if (entity.isSwimming()) {
            this.flap(Right_Arm, speed_walk * 1F, degree_walk * 1.2F, false, 0, 1.2F, f, f1);
            this.flap(Left_Arm, speed_walk * 1F, degree_walk * 1.2F, true, 0, 1.2F, f, f1);
            this.chainWave(TAIL_NO_BASE, speed_walk, degree_walk * 0.4F, 0, f, f1);
            this.walk(Tail_1, speed_walk * 1F, degree_walk * 0.2F, true, 0, 0F, f, f1);
        } else {
            this.walk(Right_Arm, speed_walk * 1F, degree_walk * 0.4F, false, 0, 0F, f, f1);
            this.walk(Left_Arm, speed_walk * 1F, degree_walk * 0.4F, true, 0, 0F, f, f1);
            this.chainFlap(TAIL_NO_BASE, speed_walk, degree_walk * 0.6F, 1, f, f1);
            this.swing(Tail_1, speed_walk * 1F, degree_walk * 0.2F, true, 0, 0F, f, f1);
        }
        if (entity.isSinging()) {
            switch (entity.getSingingPose()) {
                case 2:
                    this.progressRotation(Body, entity.singProgress, (float) Math.toRadians(-46F), 0.0F, 0.0F);
                    this.progressRotation(Tail_1, entity.singProgress, (float) Math.toRadians(90F), 0.0F, (float) Math.toRadians(20F));
                    this.progressRotation(Tail_2, entity.singProgress, 0.0F, (float) Math.toRadians(-13F), 0.0F);
                    this.progressRotation(Tail_3, entity.singProgress, 0.0F, (float) Math.toRadians(-7F), 0.0F);
                    this.progressRotation(Head, entity.singProgress, (float) Math.toRadians(-52F), (float) Math.toRadians(2F), (float) Math.toRadians(-26F));
                    this.progressRotation(Left_Arm, entity.singProgress, (float) Math.toRadians(-40F), (float) Math.toRadians(-28F), (float) Math.toRadians(-26F));
                    this.progressRotation(Right_Arm, entity.singProgress, (float) Math.toRadians(13F), (float) Math.toRadians(73F), (float) Math.toRadians(130F));
                    this.progressPosition(Head, entity.singProgress, 0, -12.0F, -0.5F);
                    this.walk(Right_Arm, speed_idle * 1.5F, degree_idle * 0.6F, false, 2, 0F, f2, 1);
                    this.flap(Right_Arm, speed_idle * 1.5F, degree_idle * 0.6F, false, 2, 0F, f2, 1);
                    if (entity.isOnGround()) {
                        this.chainFlap(TAIL_NO_BASE, speed_idle, degree_idle, 0, f2, 1);
                        this.swing(Tail_2, speed_idle, degree_idle * 0.4F, false, 0F, -0.4F, f2, 1);
                        this.swing(Tail_3, speed_idle, degree_idle * 0.4F, false, 0F, 0.6F, f2, 1);
                    }
                    break;
                case 1:
                    this.progressRotation(Body, entity.singProgress, (float) Math.toRadians(-57F), 0.0F, 0.0F);
                    this.progressRotation(Head, entity.singProgress, (float) Math.toRadians(-13F), 0.0F, 0.0F);
                    this.progressRotation(Left_Arm, entity.singProgress, (float) Math.toRadians(-200F), (float) Math.toRadians(-60F), (float) Math.toRadians(70F));
                    this.progressRotation(Right_Arm, entity.singProgress, (float) Math.toRadians(-200F), (float) Math.toRadians(60F), (float) Math.toRadians(-70F));
                    this.progressRotation(Tail_1, entity.singProgress, (float) Math.toRadians(70F), 0.0F, 0.0F);
                    this.progressRotation(Tail_2, entity.singProgress, (float) Math.toRadians(20F), 0.0F, (float) Math.toRadians(25F));
                    this.progressRotation(Tail_3, entity.singProgress, 0.0F, 0.0F, (float) Math.toRadians(18F));
                    this.progressPosition(Tail_1, entity.singProgress, 0.0F, 18.9F, -0.2F);
                    this.walk(Right_Arm, speed_idle * 1.5F, degree_idle * 0.6F, false, 2, 0F, f2, 1);
                    this.walk(Left_Arm, speed_idle * 1.5F, degree_idle * 0.6F, true, 2, 0F, f2, 1);
                    if (entity.isOnGround()) {
                        this.chainFlap(TAIL_NO_BASE, speed_idle, degree_idle, 0, f2, 1);
                    }
                    break;
                default:
                    this.progressRotation(Body, entity.singProgress, (float) Math.toRadians(-46F), 0.0F, (float) Math.toRadians(20.87F));
                    this.progressPosition(Head, entity.singProgress, 0, -12.0F, -0.5F);
                    this.progressRotation(Head, entity.singProgress, (float) Math.toRadians(-54F), 0.0F, (float) Math.toRadians(20.87F));
                    this.progressRotation(Tail_1, entity.singProgress, (float) Math.toRadians(90F), (float) Math.toRadians(20.87F), 0.0F);
                    this.progressRotation(Tail_2, entity.singProgress, 0.0F, 0.0F, (float) Math.toRadians(-33));
                    this.progressRotation(Tail_2, entity.singProgress, 0.0F, 0.0F, (float) Math.toRadians(-15));
                    this.progressRotation(Right_Arm, entity.singProgress, (float) Math.toRadians(-40F), (float) Math.toRadians(2F), (float) Math.toRadians(53F));
                    this.progressRotation(Left_Arm, entity.singProgress, (float) Math.toRadians(-80F), (float) Math.toRadians(-70F), 0.0F);
                    this.walk(Right_Arm, speed_idle * 1.5F, degree_idle * 0.6F, false, 2, 0F, f2, 1);
                    this.walk(Left_Arm, speed_idle * 1.5F, degree_idle * 0.6F, true, 2, 0F, f2, 1);
                    this.flap(Right_Arm, speed_idle * 1.5F, degree_idle * 0.6F, false, 2, 0F, f2, 1);
                    this.flap(Left_Arm, speed_idle * 1.5F, degree_idle * 0.6F, true, 2, 0F, f2, 1);
                    if (entity.isOnGround()) {
                        this.chainFlap(TAIL_NO_BASE, speed_idle, degree_idle * 0.5F, -1, f2, 1);
                    }
                    break;
            }
        }else{
            this.faceTarget(f3, f4, 2, Neck, Head);
        }
        if(entity.tail_buffer != null){
            entity.tail_buffer.applyChainSwingBuffer(TAIL_NO_BASE);
        }
    }

    @Override
    public void renderStatue(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, Entity living) {
        this.render(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
