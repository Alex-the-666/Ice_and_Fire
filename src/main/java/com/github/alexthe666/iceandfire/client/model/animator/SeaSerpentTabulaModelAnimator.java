package com.github.alexthe666.iceandfire.client.model.animator;

import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ITabulaModelAnimator;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.client.model.util.EnumSeaSerpentAnimations;
import com.github.alexthe666.iceandfire.entity.EntitySeaSerpent;

import net.minecraft.client.Minecraft;

public class SeaSerpentTabulaModelAnimator extends IceAndFireTabulaModelAnimator implements ITabulaModelAnimator<EntitySeaSerpent> {

    public TabulaModel[] swimPose = {EnumSeaSerpentAnimations.SWIM1.seaserpent_model, EnumSeaSerpentAnimations.SWIM3.seaserpent_model, EnumSeaSerpentAnimations.SWIM4.seaserpent_model, EnumSeaSerpentAnimations.SWIM6.seaserpent_model};
    public SeaSerpentTabulaModelAnimator() {
        super(EnumSeaSerpentAnimations.T_POSE.seaserpent_model);
    }

    @Override
    public void setRotationAngles(TabulaModel model, EntitySeaSerpent entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        model.resetToDefaultPose();
        model.getCube("BodyUpper").rotationPointY += 9;//model was made too high
        model.llibAnimator.update(entity);
        animate(model, entity, limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale);
        int currentIndex = entity.swimCycle / 10;
        int prevIndex = currentIndex - 1;
        if (prevIndex < 0) {
            prevIndex = 3;
        }
        TabulaModel prevPosition = swimPose[prevIndex];
        TabulaModel currentPosition = swimPose[currentIndex];
        float partialTicks = Minecraft.getInstance().getRenderPartialTicks();
        float delta = ((entity.swimCycle) / 10.0F) % 1.0F + (partialTicks / 10.0F);
        AdvancedModelBox[] tailParts = {model.getCube("Tail1"), model.getCube("Tail2"), model.getCube("Tail3"), model.getCube("Tail4"), model.getCube("Tail5"), model.getCube("Tail6")};
        AdvancedModelBox[] neckParts = {model.getCube("Neck1"), model.getCube("Neck2"), model.getCube("Neck3"), model.getCube("Head")};

        for (AdvancedModelBox cube : model.getCubes().values()) {
            if (entity.jumpProgress > 0.0F) {
                if (!isPartEqual(cube, EnumSeaSerpentAnimations.JUMPING2.seaserpent_model.getCube(cube.boxName))) {
                    transitionTo(cube, EnumSeaSerpentAnimations.JUMPING2.seaserpent_model.getCube(cube.boxName), entity.jumpProgress, 5, false);
                }
            }
            if (entity.wantJumpProgress > 0.0F) {
                if (!isPartEqual(cube, EnumSeaSerpentAnimations.JUMPING1.seaserpent_model.getCube(cube.boxName))) {
                    transitionTo(cube, EnumSeaSerpentAnimations.JUMPING1.seaserpent_model.getCube(cube.boxName), entity.wantJumpProgress, 10, false);
                }
            }
            AdvancedModelBox prevPositionCube =  prevPosition.getCube(cube.boxName);
            AdvancedModelBox currPositionCube =  currentPosition.getCube(cube.boxName);
            float prevX = prevPositionCube.rotateAngleX;
            float prevY = prevPositionCube.rotateAngleY;
            float prevZ = prevPositionCube.rotateAngleZ;
            float x = currPositionCube.rotateAngleX;
            float y = currPositionCube.rotateAngleY;
            float z = currPositionCube.rotateAngleZ;
            this.addToRotateAngle(cube, limbSwingAmount, prevX + delta * distance(prevX, x), prevY + delta * distance(prevY, y), prevZ + delta * distance(prevZ, z));

        }
        if (entity.breathProgress > 0.0F) {
            progressRotation(model.getCube("Head"), entity.breathProgress, (float) Math.toRadians(-15F), 0, 0);
            progressRotation(model.getCube("HeadFront"), entity.breathProgress, (float) Math.toRadians(-20F), 0, 0);
            progressRotation(model.getCube("Jaw"), entity.breathProgress, (float) Math.toRadians(60F), 0, 0);
        }
        if (entity.jumpRot > 0.0F) {
            float jumpRot = entity.prevJumpRot + (entity.jumpRot - entity.prevJumpRot) * partialTicks;
            float turn = (float) entity.getMotion().y * -4F;
            model.getCube("BodyUpper").rotateAngleX += (float) Math.toRadians(22.5F * turn) * jumpRot;
            model.getCube("Tail1").rotateAngleX -= (float) Math.toRadians(turn) * jumpRot;
            model.getCube("Tail2").rotateAngleX -= (float) Math.toRadians(turn) * jumpRot;
            model.getCube("Tail3").rotateAngleX -= (float) Math.toRadians(turn) * jumpRot;
            model.getCube("Tail4").rotateAngleX -= (float) Math.toRadians(turn) * jumpRot;
        }
        float prevRenderOffset = entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * partialTicks;

        model.getCube("Tail1").rotateAngleY += (entity.getPieceYaw(1, partialTicks) - prevRenderOffset) * ((float)Math.PI / 180F);
        model.getCube("Tail2").rotateAngleY += (entity.getPieceYaw(2, partialTicks) - prevRenderOffset) * ((float)Math.PI / 180F);
        model.getCube("Tail3").rotateAngleY += (entity.getPieceYaw(3, partialTicks) - prevRenderOffset) * ((float)Math.PI / 180F);
        model.getCube("Tail4").rotateAngleY += (entity.getPieceYaw(4, partialTicks) - prevRenderOffset) * ((float)Math.PI / 180F);
        model.getCube("BodyUpper").rotateAngleX -= rotationPitch * ((float)Math.PI / 180F);
        if(!entity.isJumpingOutOfWater() || entity.isInWater()){
            model.getCube("Tail1").rotateAngleX -= (entity.getPiecePitch(1, partialTicks) - 0) * ((float)Math.PI / 180F);
            model.getCube("Tail2").rotateAngleX -= (entity.getPiecePitch(2, partialTicks) - 0) * ((float)Math.PI / 180F);
            model.getCube("Tail3").rotateAngleX -= (entity.getPiecePitch(3, partialTicks) - 0) * ((float)Math.PI / 180F);
            model.getCube("Tail4").rotateAngleX -= (entity.getPiecePitch(4, partialTicks) - 0) * ((float)Math.PI / 180F);
        }
    }

    public void progressRotation(AdvancedModelBox model, float progress, float rotX, float rotY, float rotZ) {
        model.rotateAngleX += progress * (rotX - model.defaultRotationX) / 20.0F;
        model.rotateAngleY += progress * (rotY - model.defaultRotationY) / 20.0F;
        model.rotateAngleZ += progress * (rotZ - model.defaultRotationZ) / 20.0F;
    }

    private void animate(TabulaModel model, EntitySeaSerpent entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
        model.llibAnimator.setAnimation(EntitySeaSerpent.ANIMATION_SPEAK);
        model.llibAnimator.startKeyframe(5);
        this.rotate(model.llibAnimator, model.getCube("Jaw"), 25, 0, 0);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.setStaticKeyframe(5);
        model.llibAnimator.resetKeyframe(5);
        model.llibAnimator.setAnimation(EntitySeaSerpent.ANIMATION_BITE);
        model.llibAnimator.startKeyframe(5);
        moveToPose(model, EnumSeaSerpentAnimations.BITE1.seaserpent_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(5);
        moveToPose(model, EnumSeaSerpentAnimations.BITE2.seaserpent_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.setStaticKeyframe(2);
        model.llibAnimator.resetKeyframe(3);

        model.llibAnimator.setAnimation(EntitySeaSerpent.ANIMATION_ROAR);
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumSeaSerpentAnimations.ROAR1.seaserpent_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumSeaSerpentAnimations.ROAR2.seaserpent_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.startKeyframe(10);
        moveToPose(model, EnumSeaSerpentAnimations.ROAR3.seaserpent_model);
        model.llibAnimator.endKeyframe();
        model.llibAnimator.resetKeyframe(10);

    }
}
