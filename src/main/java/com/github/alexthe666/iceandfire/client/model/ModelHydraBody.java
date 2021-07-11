package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.iceandfire.client.model.util.EntityModelPartBuilder;
import com.github.alexthe666.iceandfire.entity.EntityHydra;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;

public class ModelHydraBody extends ModelDragonBase<EntityHydra> {
    public AdvancedModelBox BodyUpper;
    public AdvancedModelBox BodyLower;
    public AdvancedModelBox BodySpike1;
    public AdvancedModelBox BodySpike2;
    public AdvancedModelBox Tail1;
    public AdvancedModelBox BodySpike3;
    public AdvancedModelBox Tail2;
    public AdvancedModelBox Tail3;
    public AdvancedModelBox Tail4;
    public AdvancedModelBox Tail5;
    public AdvancedModelBox TailSpike1;
    public AdvancedModelBox TailSpike2;
    public AdvancedModelBox TailSpike3;
    private ModelAnimator animator;

    public ModelHydraBody() {
        this.textureWidth = 256;
        this.textureHeight = 128;
        this.BodySpike1 = new AdvancedModelBox(this, 0, 0);
        this.BodySpike1.setRotationPoint(0.0F, -1.2F, 3.0F);
        this.BodySpike1.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(BodySpike1, 0.6433283622851098F, -0.0F, 0.0F);
        this.Tail3 = new AdvancedModelBox(this, 70, 15);
        this.Tail3.setRotationPoint(0.0F, -0.1F, 7.7F);
        this.Tail3.addBox(-1.5F, -1.3F, 0.7F, 3, 4, 9, 0.0F);
        this.setRotateAngle(Tail3, 0.091106186954104F, 0.0F, 0.0F);
        this.Tail1 = new AdvancedModelBox(this, 69, 34);
        this.Tail1.setRotationPoint(0.0F, -1.2F, 7.3F);
        this.Tail1.addBox(-2.5F, -2.0F, 1.0F, 5, 5, 8, 0.0F);
        this.setRotateAngle(Tail1, 0.045553093477052F, 0.0F, 0.0F);
        this.TailSpike2 = new AdvancedModelBox(this, 0, 0);
        this.TailSpike2.setRotationPoint(0.0F, 0.0F, 3.0F);
        this.TailSpike2.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(TailSpike2, 0.6433283622851098F, -0.0F, 0.0F);
        this.Tail4 = new AdvancedModelBox(this, 97, 16);
        this.Tail4.setRotationPoint(0.0F, 0.3F, 8.0F);
        this.Tail4.addBox(-1.52F, -1.3F, 0.8F, 3, 3, 9, 0.0F);
        this.Tail5 = new AdvancedModelBox(this, 42, 17);
        this.Tail5.setRotationPoint(0.0F, -0.4F, 7.5F);
        this.Tail5.addBox(-1.0F, -0.4F, 1.0F, 2, 2, 8, 0.0F);
        this.setRotateAngle(Tail5, 0.091106186954104F, 0.0F, 0.0F);
        this.TailSpike3 = new AdvancedModelBox(this, 40, 0);
        this.TailSpike3.setRotationPoint(0.0F, 0.0F, 7.0F);
        this.TailSpike3.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(TailSpike3, 0.6433283622851098F, -0.0F, 0.0F);
        this.BodySpike3 = new AdvancedModelBox(this, 0, 0);
        this.BodySpike3.setRotationPoint(0.0F, -3.1F, 2.5F);
        this.BodySpike3.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(BodySpike3, 0.6433283622851098F, -0.0F, 0.0F);
        this.BodySpike2 = new AdvancedModelBox(this, 40, 0);
        this.BodySpike2.setRotationPoint(0.0F, -1.2F, 7.0F);
        this.BodySpike2.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(BodySpike2, 0.6433283622851098F, -0.0F, 0.0F);
        this.BodyLower = new AdvancedModelBox(this, 103, 47);
        this.BodyLower.setRotationPoint(0.0F, 2.2F, 8.1F);
        this.BodyLower.addBox(-3.5F, -3.5F, 0.0F, 7, 6, 9, 0.0F);
        this.setRotateAngle(BodyLower, -0.091106186954104F, -0.0F, 0.0F);
        this.TailSpike1 = new AdvancedModelBox(this, 40, 0);
        this.TailSpike1.setRotationPoint(0.0F, -0.6F, 4.2F);
        this.TailSpike1.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(TailSpike1, 0.6433283622851098F, -0.0F, 0.0F);
        this.Tail2 = new AdvancedModelBox(this, 95, 34);
        this.Tail2.setRotationPoint(0.0F, 0.5F, 7.4F);
        this.Tail2.addBox(-2.01F, -1.6F, 0.9F, 4, 4, 8, 0.0F);
        this.BodyUpper = new AdvancedModelBox(this, 67, 47);
        this.BodyUpper.setRotationPoint(0.0F, 19.1F, -9.7F);
        this.BodyUpper.addBox(-4.5F, -1.8F, 0.0F, 9, 7, 9, 0.0F);
        this.BodyUpper.addChild(this.BodySpike1);
        this.Tail2.addChild(this.Tail3);
        this.BodyLower.addChild(this.Tail1);
        this.Tail5.addChild(this.TailSpike2);
        this.Tail3.addChild(this.Tail4);
        this.Tail4.addChild(this.Tail5);
        this.Tail5.addChild(this.TailSpike3);
        this.BodyLower.addChild(this.BodySpike3);
        this.BodyUpper.addChild(this.BodySpike2);
        this.BodyUpper.addChild(this.BodyLower);
        this.Tail4.addChild(this.TailSpike1);
        this.Tail1.addChild(this.Tail2);
        animator = ModelAnimator.create();
        this.updateDefaultPose();
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.resetToDefaultPose();
        animator.update(entity);
    }

    public void setRotationAngles(EntityHydra entity, float f, float f1, float f2, float f3, float f4) {
        animate(entity, f, f1, f2, f3, f4, 1);
        float speed_walk = 0.6F;
        float speed_idle = 0.05F;
        float degree_walk = 1F;
        float degree_idle = 0.25F;
        AdvancedModelBox[] TAIL = new AdvancedModelBox[]{BodyLower, Tail1, Tail2, Tail3, Tail4, Tail5};
        this.chainSwing(TAIL, speed_walk, degree_walk * 0.75F, -3, f, f1);
        this.swing(BodyUpper, speed_walk * 1.5F, degree_walk * 0.12F, true, 3, 0F, f, f1);
        this.swing(Tail5, speed_idle * 1.5F, degree_idle * 0.2F, false, 3, 0F, f2, 1);
        this.swing(Tail4, speed_idle * 1.5F, degree_idle * 0.2F, false, 2, 0F, f2, 1);
        this.walk(BodySpike1, speed_idle * 1.5F, degree_idle * 0.4F, false, 2, -0.2F, f2, 1);
        this.walk(BodySpike2, speed_idle * 1.5F, degree_idle * 0.4F, false, 3, -0.2F, f2, 1);
        this.walk(BodySpike3, speed_idle * 1.5F, degree_idle * 0.4F, false, 4, -0.2F, f2, 1);

        this.walk(TailSpike1, speed_idle * 1.5F, degree_idle * 0.4F, false, 2, -0.2F, f2, 1);
        this.walk(TailSpike2, speed_idle * 1.5F, degree_idle * 0.4F, false, 3, -0.2F, f2, 1);
        this.walk(TailSpike3, speed_idle * 1.5F, degree_idle * 0.4F, false, 4, -0.2F, f2, 1);
    }

    @Override
    public void renderStatue(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, Entity living) {
        this.resetToDefaultPose();
        this.render(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        this.resetToDefaultPose();
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(BodyUpper);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return EntityModelPartBuilder.getAllPartsFromClass(this.getClass(), this.getClass().getName());
    }
}
