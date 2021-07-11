package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.iceandfire.client.model.util.EntityModelPartBuilder;
import com.github.alexthe666.iceandfire.entity.EntityCockatrice;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;

public class ModelCockatriceChick extends ModelDragonBase<EntityCockatrice> {
    public AdvancedModelBox lowerBody;
    public AdvancedModelBox leftThigh;
    public AdvancedModelBox rightThigh;
    public AdvancedModelBox upperBody;
    public AdvancedModelBox tail1;
    public AdvancedModelBox neck;
    public AdvancedModelBox leftUpperArm;
    public AdvancedModelBox RightUpperArm;
    public AdvancedModelBox neck2;
    public AdvancedModelBox head;
    public AdvancedModelBox upperJaw;
    public AdvancedModelBox lowerJaw;
    public AdvancedModelBox leftUpperArmWing;
    public AdvancedModelBox RightUpperArmWing;
    public AdvancedModelBox tail2;
    public AdvancedModelBox tail3;
    public AdvancedModelBox rightToeClaw2;
    public AdvancedModelBox leftLeg;
    public AdvancedModelBox leftFoot;
    public AdvancedModelBox rightLeg;
    public AdvancedModelBox rightFoot;
    private ModelAnimator animator;

    public ModelCockatriceChick() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.RightUpperArm = new AdvancedModelBox(this, 0, 20);
        this.RightUpperArm.mirror = true;
        this.RightUpperArm.setRotationPoint(-2.5F, 1.3F, -3.3F);
        this.RightUpperArm.addBox(-2.0F, 0.0F, -1.0F, 2, 5, 3, 0.0F);
        this.setRotateAngle(RightUpperArm, 0.0F, -0.22689280275926282F, 0.8726646259971648F);
        this.leftUpperArmWing = new AdvancedModelBox(this, 22, 10);
        this.leftUpperArmWing.setRotationPoint(1.4F, 2.7F, 1.1F);
        this.leftUpperArmWing.addBox(-0.5F, 0.0F, -2.5F, 1, 4, 7, 0.0F);
        this.setRotateAngle(leftUpperArmWing, 1.5184364492350666F, -0.0F, 0.0F);
        this.tail1 = new AdvancedModelBox(this, 91, 0);
        this.tail1.setRotationPoint(0.0F, -0.3F, 6.5F);
        this.tail1.addBox(-2.0F, 0.0F, 0.0F, 4, 4, 7, 0.0F);
        this.setRotateAngle(tail1, 0.18203784098300857F, -0.0F, 0.0F);
        this.rightThigh = new AdvancedModelBox(this, 14, 35);
        this.rightThigh.setRotationPoint(-3.0F, 15.2F, 3.0F);
        this.rightThigh.addBox(-3.0F, -2.5F, -2.0F, 3, 6, 5, 0.0F);
        this.lowerBody = new AdvancedModelBox(this, 65, 32);
        this.lowerBody.setRotationPoint(0.0F, 9.0F, -0.5F);
        this.lowerBody.addBox(-4.5F, -1.0F, 0.0F, 9, 9, 9, 0.0F);
        this.setRotateAngle(lowerBody, -0.31869712141416456F, 0.0F, 0.0F);
        this.head = new AdvancedModelBox(this, 0, 0);
        this.head.setRotationPoint(0.0F, 0.8F, -6.03F);
        this.head.addBox(-2.5F, -4.0F, -5.0F, 5, 5, 6, 0.0F);
        this.setRotateAngle(head, 1.1383037381507017F, 0.0F, 0.0F);
        this.rightLeg = new AdvancedModelBox(this, 2, 25);
        this.rightLeg.setRotationPoint(-1.2F, 0.9F, 0.1F);
        this.rightLeg.addBox(-1.0F, 0.4F, -6.7F, 2, 2, 8, 0.0F);
        this.setRotateAngle(rightLeg, 1.3089969389957472F, -0.0F, 0.0F);
        this.neck2 = new AdvancedModelBox(this, 0, 47);
        this.neck2.setRotationPoint(0.0F, 1.0F, -2.6F);
        this.neck2.addBox(-2.02F, -2.0F, -8.0F, 4, 4, 8, 0.0F);
        this.setRotateAngle(neck2, -0.31869712141416456F, -0.0F, 0.0F);
        this.tail3 = new AdvancedModelBox(this, 49, 16);
        this.tail3.setRotationPoint(0.0F, 0.3F, 5.2F);
        this.tail3.addBox(-1.0F, 0.0F, 0.0F, 2, 2, 12, 0.0F);
        this.setRotateAngle(tail3, -0.05253441048502932F, -0.0F, 0.0F);
        this.upperJaw = new AdvancedModelBox(this, 28, 1);
        this.upperJaw.setRotationPoint(0.0F, 0.0F, -3.93F);
        this.upperJaw.addBox(-1.5F, -2.4F, -4.0F, 3, 2, 4, 0.0F);
        this.setRotateAngle(upperJaw, -0.0017453292519943296F, -0.0F, 0.0F);
        this.leftFoot = new AdvancedModelBox(this, 0, 36);
        this.leftFoot.setRotationPoint(0.0F, 0.9F, -5.7F);
        this.leftFoot.addBox(-1.5F, 0.0F, -3.1F, 3, 2, 4, 0.0F);
        this.setRotateAngle(leftFoot, -1.3089969389957472F, -0.0F, 0.0F);
        this.rightToeClaw2 = new AdvancedModelBox(this, 0, 40);
        this.rightToeClaw2.setRotationPoint(0.0F, 0.2F, -2.5F);
        this.rightToeClaw2.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 1, 0.0F);
        this.setRotateAngle(rightToeClaw2, -1.7627825445142729F, -0.0F, 0.0F);
        this.leftUpperArm = new AdvancedModelBox(this, 0, 20);
        this.leftUpperArm.mirror = true;
        this.leftUpperArm.setRotationPoint(2.5F, 1.3F, -3.3F);
        this.leftUpperArm.addBox(0.0F, 0.0F, -1.0F, 2, 5, 3, 0.0F);
        this.setRotateAngle(leftUpperArm, 0.0F, 0.22689280275926282F, -0.8726646259971648F);
        this.upperBody = new AdvancedModelBox(this, 67, 5);
        this.upperBody.setRotationPoint(0.0F, 1.1F, -2.0F);
        this.upperBody.addBox(-3.5F, -2.0F, -5.0F, 7, 8, 8, 0.0F);
        this.setRotateAngle(upperBody, 0.091106186954104F, -0.0F, 0.0F);
        this.rightFoot = new AdvancedModelBox(this, 0, 36);
        this.rightFoot.setRotationPoint(0.0F, 0.9F, -5.7F);
        this.rightFoot.addBox(-1.5F, 0.0F, -3.0F, 3, 2, 4, 0.0F);
        this.setRotateAngle(rightFoot, -1.3089969389957472F, -0.0F, 0.0F);
        this.leftThigh = new AdvancedModelBox(this, 14, 35);
        this.leftThigh.mirror = true;
        this.leftThigh.setRotationPoint(3.0F, 15.2F, 3.0F);
        this.leftThigh.addBox(0.0F, -2.5F, -2.0F, 3, 6, 5, 0.0F);
        this.tail2 = new AdvancedModelBox(this, 90, 13);
        this.tail2.setRotationPoint(0.0F, 0.1F, 4.7F);
        this.tail2.addBox(-1.5F, 0.0F, 0.0F, 3, 3, 8, 0.0F);
        this.setRotateAngle(tail2, 0.18203784098300857F, 0.0F, 0.0F);
        this.neck = new AdvancedModelBox(this, 29, 51);
        this.neck.setRotationPoint(0.0F, 1.8F, -1.5F);
        this.neck.addBox(-2.5F, -2.0F, -6.5F, 5, 5, 7, 0.0F);
        this.setRotateAngle(neck, -0.4553564018453205F, -0.0F, 0.0F);
        this.RightUpperArmWing = new AdvancedModelBox(this, 22, 10);
        this.RightUpperArmWing.mirror = true;
        this.RightUpperArmWing.setRotationPoint(-1.0F, 2.7F, 1.1F);
        this.RightUpperArmWing.addBox(-0.5F, 0.0F, -2.5F, 1, 4, 7, 0.0F);
        this.setRotateAngle(RightUpperArmWing, 1.5184364492350666F, -0.0F, 0.0F);
        this.lowerJaw = new AdvancedModelBox(this, 50, 0);
        this.lowerJaw.setRotationPoint(0.0F, -0.1F, -3.53F);
        this.lowerJaw.addBox(-1.0F, -0.5F, -4.3F, 2, 1, 4, 0.0F);
        this.setRotateAngle(lowerJaw, -0.091106186954104F, 0.0F, 0.0F);
        this.leftLeg = new AdvancedModelBox(this, 2, 25);
        this.leftLeg.mirror = true;
        this.leftLeg.setRotationPoint(1.2F, 0.9F, 0.1F);
        this.leftLeg.addBox(-1.0F, 0.4F, -6.7F, 2, 2, 8, 0.0F);
        this.setRotateAngle(leftLeg, 1.3089969389957472F, -0.0F, 0.0F);
        this.upperBody.addChild(this.RightUpperArm);
        this.leftUpperArm.addChild(this.leftUpperArmWing);
        this.lowerBody.addChild(this.tail1);
        this.neck2.addChild(this.head);
        this.rightThigh.addChild(this.rightLeg);
        this.neck.addChild(this.neck2);
        this.tail2.addChild(this.tail3);
        this.head.addChild(this.upperJaw);
        this.leftLeg.addChild(this.leftFoot);
        this.tail2.addChild(this.rightToeClaw2);
        this.upperBody.addChild(this.leftUpperArm);
        this.lowerBody.addChild(this.upperBody);
        this.rightLeg.addChild(this.rightFoot);
        this.tail1.addChild(this.tail2);
        this.upperBody.addChild(this.neck);
        this.RightUpperArm.addChild(this.RightUpperArmWing);
        this.head.addChild(this.lowerJaw);
        this.leftThigh.addChild(this.leftLeg);
        animator = ModelAnimator.create();
        this.updateDefaultPose();
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(lowerBody, leftThigh, rightThigh);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return EntityModelPartBuilder.getAllPartsFromClass(this.getClass(), this.getClass().getName());
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntityCockatrice.ANIMATION_EAT);
        animator.startKeyframe(5);
        this.rotate(animator, neck2, 7, 0, 0);
        this.rotate(animator, head, 45, 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        this.rotate(animator, upperBody, 15, 0, 0);
        this.rotate(animator, neck, 31, 0, 0);
        this.rotate(animator, neck2, 7, 0, 0);
        this.rotate(animator, head, 45, 0, 0);
        this.rotate(animator, lowerJaw, 15, 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        this.rotate(animator, upperBody, 5, 0, 0);
        this.rotate(animator, neck, -10, 0, 0);
        this.rotate(animator, neck2, 1, 0, 0);
        this.rotate(animator, head, 45, 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(EntityCockatrice.ANIMATION_JUMPAT);
        animator.startKeyframe(10);
        this.rotate(animator, lowerBody, 18, 0, 0);
        this.rotate(animator, upperBody, 5, 0, 0);
        this.rotate(animator, neck, -23, 0, 0);
        this.rotate(animator, neck2, 18, 0, 0);
        this.rotate(animator, head, -27, 0, 0);
        this.rotate(animator, leftUpperArm, 0, 30, -50);
        this.rotate(animator, RightUpperArm, 0, -30, 50);
        animator.move(lowerBody, 0, 1.7F, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        jumpPos();
        animator.endKeyframe();
        animator.startKeyframe(2);
        jumpPos();
        this.rotate(animator, leftUpperArm, 0, 30, -75);
        this.rotate(animator, RightUpperArm, 0, -30, 75);
        animator.endKeyframe();
        animator.startKeyframe(2);
        jumpPos();
        this.rotate(animator, leftUpperArm, 0, 30, -19);
        this.rotate(animator, RightUpperArm, 0, -30, 19);
        animator.endKeyframe();
        animator.startKeyframe(2);
        jumpPos();
        this.rotate(animator, leftUpperArm, 0, 30, -75);
        this.rotate(animator, RightUpperArm, 0, -30, 75);
        animator.endKeyframe();
        animator.startKeyframe(2);
        jumpPos();
        this.rotate(animator, leftUpperArm, 0, 30, -19);
        this.rotate(animator, RightUpperArm, 0, -30, 19);
        animator.endKeyframe();
        animator.startKeyframe(2);
        jumpPos();
        this.rotate(animator, leftUpperArm, 0, 30, -75);
        this.rotate(animator, RightUpperArm, 0, -30, 75);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(EntityCockatrice.ANIMATION_WATTLESHAKE);
        animator.startKeyframe(3);
        this.rotate(animator, neck, 0, 0, -23);
        this.rotate(animator, neck2, 0, 0, -13);
        this.rotate(animator, head, 5, 0, -15);
        animator.endKeyframe();
        animator.startKeyframe(2);
        this.rotate(animator, neck, 0, 0, -23);
        this.rotate(animator, neck2, 0, 0, -13);
        this.rotate(animator, head, 5, 0, -15);
        animator.endKeyframe();
        animator.startKeyframe(3);
        this.rotate(animator, neck, 0, 0, 23);
        this.rotate(animator, neck2, 0, 0, 13);
        this.rotate(animator, head, 5, 0, 15);
        animator.endKeyframe();
        animator.startKeyframe(2);
        this.rotate(animator, neck, 0, 0, 23);
        this.rotate(animator, neck2, 0, 0, 13);
        this.rotate(animator, head, 5, 0, 15);
        animator.endKeyframe();
        animator.startKeyframe(3);
        this.rotate(animator, neck, 0, 0, -23);
        this.rotate(animator, neck2, 0, 0, -13);
        this.rotate(animator, head, 5, 0, -15);
        animator.endKeyframe();
        animator.startKeyframe(2);
        this.rotate(animator, neck, 0, 0, -23);
        this.rotate(animator, neck2, 0, 0, -13);
        this.rotate(animator, head, 5, 0, -15);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(EntityCockatrice.ANIMATION_BITE);
        animator.startKeyframe(5);
        this.rotate(animator, neck, -47, 0, 0);
        this.rotate(animator, neck2, 17, 0, 0);
        this.rotate(animator, head, 46, 0, 0);
        this.rotate(animator, lowerJaw, 10, 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        this.rotate(animator, neck, 26, 0, 0);
        this.rotate(animator, neck2, -18, 0, 0);
        this.rotate(animator, head, 2, 0, 0);
        this.rotate(animator, lowerJaw, 33, 0, 0);
        this.rotate(animator, upperJaw, -20, 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(EntityCockatrice.ANIMATION_SPEAK);
        animator.startKeyframe(5);
        this.rotate(animator, lowerJaw, 25, 0, 0);
        animator.resetKeyframe(5);

    }

    private void jumpPos() {
        this.rotate(animator, lowerBody, -29, 0, 0);
        this.rotate(animator, upperBody, 10, 0, 0);
        this.rotate(animator, neck, 7, 0, 0);
        this.rotate(animator, neck2, 32, 0, 0);
        this.rotate(animator, head, 36, 0, 0);
        this.rotate(animator, lowerJaw, 28, 0, 0);
        this.rotate(animator, tail1, -18, 0, 0);
        animator.move(lowerBody, 0, -1.9F, 2.5F);
        this.rotate(animator, rightThigh, -74, 0, 10);
        this.rotate(animator, rightLeg, 0, 39, 0);
        this.rotate(animator, rightFoot, 50, -10, 0);
        this.rotate(animator, leftThigh, -74, 0, -10);
        this.rotate(animator, leftLeg, 0, -39, 0);
        this.rotate(animator, leftFoot, 50, 10, 0);
    }

    public void setRotationAngles(EntityCockatrice entity, float f, float f1, float f2, float f3, float f4) {
        animate(entity, f, f1, f2, f3, f4);
        float speed_walk = 0.6F;
        float speed_idle = 0.05F;
        float degree_walk = 0.5F;
        float degree_idle = 0.5F;
        AdvancedModelBox[] NECK = new AdvancedModelBox[]{neck, neck2, head};
        AdvancedModelBox[] TAIL = new AdvancedModelBox[]{tail1, tail2, tail3};
        this.chainWave(NECK, speed_idle, degree_idle * 0.1F, 4, f2, 1);
        this.chainSwing(TAIL, speed_idle, degree_idle * 0.5F, 0, f2, 1);
        this.walk(lowerBody, speed_idle, degree_idle * 0.1F, false, 0, 0.1F, f2, 1);
        this.walk(upperBody, speed_idle, degree_idle * 0.05F, true, 1, 0F, f2, 1);

        this.walk(leftUpperArm, speed_idle, degree_idle * 0.2F, false, 1, 0.1F, f2, 1);
        this.walk(RightUpperArm, speed_idle, degree_idle * 0.2F, false, 1, 0.1F, f2, 1);

        this.flap(leftUpperArm, speed_idle, degree_idle * 0.2F, true, 2, -0.3F, f2, 1);
        this.flap(RightUpperArm, speed_idle, degree_idle * 0.2F, false, 2, -0.3F, f2, 1);
        this.faceTarget(f3, f4, 2, head);
        this.faceTarget(f3, f4, 2, neck);


        this.chainWave(NECK, speed_walk, degree_walk * 0.5F, 4, f, f1);
        this.walk(lowerBody, speed_walk, degree_walk * 0.1F, false, 0, 0F, f, f1);
        this.walk(upperBody, speed_walk, degree_walk * 0.05F, true, 1, 0F, f, f1);
        this.walk(leftThigh, speed_walk, degree_walk, true, 1, 0.1F, f, f1);
        this.walk(rightThigh, speed_walk, degree_walk, false, 1, 0.1F, f, f1);
        this.walk(leftLeg, speed_walk, degree_walk, true, 1, 0.1F, f, f1);
        this.walk(rightLeg, speed_walk, degree_walk, false, 1, 0.1F, f, f1);
        this.walk(leftFoot, speed_walk, degree_walk * -1.75F, true, 1, -0.1F, f, f1);
        this.walk(rightFoot, speed_walk, degree_walk * -1.75F, false, 1, -0.1F, f, f1);
        this.progressRotation(neck, entity.stareProgress, (float) Math.toRadians(10), 0.0F, 0.0F);
        this.progressRotation(neck2, entity.stareProgress, (float) Math.toRadians(-18), 0.0F, 0.0F);
        this.progressRotation(head, entity.stareProgress, (float) Math.toRadians(18), 0.0F, 0.0F);

        this.progressRotation(rightThigh, entity.sitProgress, (float) Math.toRadians(-15), 0.0F, 0.0F);
        this.progressRotation(leftThigh, entity.sitProgress, (float) Math.toRadians(-15), 0.0F, 0.0F);
        this.progressRotation(rightLeg, entity.sitProgress, (float) Math.toRadians(13), 0.0F, 0.0F);
        this.progressRotation(leftLeg, entity.sitProgress, (float) Math.toRadians(13), 0.0F, 0.0F);
        this.progressRotation(rightFoot, entity.sitProgress, 0.0F, 0.0F, 0.0F);
        this.progressRotation(leftFoot, entity.sitProgress, 0.0F, 0.0F, 0.0F);
        this.progressPosition(rightThigh, entity.sitProgress, -3.0F, 19F, 3.0F);
        this.progressPosition(leftThigh, entity.sitProgress, 3.0F, 19F, 3.0F);
        this.progressPosition(lowerBody, entity.sitProgress, 0.0F, 12.9F, -2.5F);
    }

    @Override
    public void renderStatue(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, Entity living) {
        this.render(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
