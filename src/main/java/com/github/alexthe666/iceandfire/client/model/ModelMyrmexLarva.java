package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;

public class ModelMyrmexLarva extends ModelDragonBase {
    public AdvancedModelBox Body2;
    public AdvancedModelBox Body3;
    public AdvancedModelBox Body1;
    public AdvancedModelBox Body4;
    public AdvancedModelBox Body5;
    public AdvancedModelBox Body4_1;
    public AdvancedModelBox Body5_1;
    public AdvancedModelBox Body4_2;
    public AdvancedModelBox Neck1;
    public AdvancedModelBox HeadBase;
    private ModelAnimator animator;

    public ModelMyrmexLarva() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.Body5 = new AdvancedModelBox(this, 82, 35);
        this.Body5.setRotationPoint(0.0F, -0.4F, 4.2F);
        this.Body5.addBox(-3.5F, -2.5F, -2.1F, 7, 8, 6, 0.0F);
        this.setRotateAngle(Body5, -0.045553093477052F, 0.0F, 0.0F);
        this.Body3 = new AdvancedModelBox(this, 36, 73);
        this.Body3.setRotationPoint(0.0F, 0.2F, 4.1F);
        this.Body3.addBox(-4.5F, -3.4F, -1.4F, 9, 9, 9, 0.0F);
        this.Body4_1 = new AdvancedModelBox(this, 58, 35);
        this.Body4_1.setRotationPoint(0.0F, 0.6F, 3.3F);
        this.Body4_1.addBox(-3.0F, -2.7F, -1.5F, 6, 7, 4, 0.0F);
        this.setRotateAngle(Body4_1, 0.045553093477052F, 0.0F, 0.0F);
        this.Neck1 = new AdvancedModelBox(this, 32, 22);
        this.Neck1.setRotationPoint(0.0F, 2.0F, -5.0F);
        this.Neck1.addBox(-2.5F, -2.0F, -3.5F, 5, 5, 4, 0.0F);
        this.setRotateAngle(Neck1, -1.6845917940249266F, 0.0F, 0.0F);
        this.HeadBase = new AdvancedModelBox(this, 2, 2);
        this.HeadBase.setRotationPoint(0.0F, 1.2F, -3.3F);
        this.HeadBase.addBox(-4.0F, -2.51F, -8.1F, 8, 6, 8, 0.0F);
        this.setRotateAngle(HeadBase, 2.5953045977155678F, 0.0F, 0.0F);
        this.Body4_2 = new AdvancedModelBox(this, 58, 35);
        this.Body4_2.setRotationPoint(0.0F, 0.8F, 4.3F);
        this.Body4_2.addBox(-3.0F, -2.7F, -1.5F, 6, 7, 4, 0.0F);
        this.setRotateAngle(Body4_2, -0.4553564018453205F, 0.0F, 0.0F);
        this.Body1 = new AdvancedModelBox(this, 34, 47);
        this.Body1.setRotationPoint(0.0F, -0.7F, -1.0F);
        this.Body1.addBox(-3.5F, -2.1F, -6.3F, 7, 8, 9, 0.0F);
        this.setRotateAngle(Body1, 0.045553093477052F, 0.0F, 0.0F);
        this.Body4 = new AdvancedModelBox(this, 58, 35);
        this.Body4.setRotationPoint(0.0F, -0.4F, 7.3F);
        this.Body4.addBox(-3.0F, -2.7F, -1.5F, 6, 7, 4, 0.0F);
        this.setRotateAngle(Body4, 0.045553093477052F, 0.0F, 0.0F);
        this.Body5_1 = new AdvancedModelBox(this, 82, 35);
        this.Body5_1.setRotationPoint(0.0F, -0.4F, 4.2F);
        this.Body5_1.addBox(-3.5F, -2.5F, -2.1F, 7, 8, 6, 0.0F);
        this.setRotateAngle(Body5_1, -0.045553093477052F, 0.0F, 0.0F);
        this.Body2 = new AdvancedModelBox(this, 70, 53);
        this.Body2.setRotationPoint(0.0F, 19.0F, -6.0F);
        this.Body2.addBox(-3.0F, -2.7F, -0.1F, 6, 7, 4, 0.0F);
        this.setRotateAngle(Body2, -0.045553093477052F, 0.0F, 0.0F);
        this.Body4.addChild(this.Body5);
        this.Body2.addChild(this.Body3);
        this.Body5.addChild(this.Body4_1);
        this.Body1.addChild(this.Neck1);
        this.Neck1.addChild(this.HeadBase);
        this.Body5_1.addChild(this.Body4_2);
        this.Body2.addChild(this.Body1);
        this.Body3.addChild(this.Body4);
        this.Body4_1.addChild(this.Body5_1);
        animator = ModelAnimator.create();
        this.updateDefaultPose();
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(Body2);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(Body2, Body3, Body1, Body4, Body5, Body4_1, Body5_1, Body4_2, Neck1, HeadBase);
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntityMyrmexBase.ANIMATION_PUPA_WIGGLE);
        animator.startKeyframe(5);
        ModelUtils.rotate(animator, Body1, 0, -15, 0);
        ModelUtils.rotate(animator, Body2, 0, -15, 0);
        ModelUtils.rotate(animator, Body3, 0, -15, 0);
        ModelUtils.rotate(animator, Body4, 0, 15, 0);
        ModelUtils.rotate(animator, Body5, 0, 15, 0);
        ModelUtils.rotate(animator, Body5_1, 0, 15, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        ModelUtils.rotate(animator, Body1, 0, 15, 0);
        ModelUtils.rotate(animator, Body2, 0, 15, 0);
        ModelUtils.rotate(animator, Body3, 0, 15, 0);
        ModelUtils.rotate(animator, Body4, 0, -15, 0);
        ModelUtils.rotate(animator, Body5, 0, -15, 0);
        ModelUtils.rotate(animator, Body5_1, 0, -15, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        ModelUtils.rotate(animator, Body1, 0, -15, 0);
        ModelUtils.rotate(animator, Body2, 0, -15, 0);
        ModelUtils.rotate(animator, Body3, 0, -15, 0);
        ModelUtils.rotate(animator, Body4, 0, 15, 0);
        ModelUtils.rotate(animator, Body5, 0, 15, 0);
        ModelUtils.rotate(animator, Body5_1, 0, 15, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
    }

    @Override
    public void setRotationAngles(Entity entity, float f, float f1, float f2, float f3, float f4) {
        animate((IAnimatedEntity) entity, f, f1, f2, f3, f4, 1);
        this.resetToDefaultPose();
        float speed_idle = 0.025F;
        float degree_idle = 0.25F;
        AdvancedModelBox[] PARTS = new AdvancedModelBox[]{Body1, Body2, Body3, Body4, Body4_1, Body4_2, Body5, Body5_1};
        this.bob(Body2, speed_idle, degree_idle * 2.5F, true, f2, 1);
        this.chainSwing(PARTS, speed_idle, degree_idle * 0.15F, 1, f2, 1);
        this.chainFlap(PARTS, speed_idle, degree_idle * 0.15F, 1, f2, 1);

    }

    @Override
    public void renderStatue(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, Entity living) {
        this.render(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
