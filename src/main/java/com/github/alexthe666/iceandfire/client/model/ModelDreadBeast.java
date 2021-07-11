package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.iceandfire.client.model.util.EntityModelPartBuilder;
import com.github.alexthe666.iceandfire.entity.EntityDreadBeast;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;

public class ModelDreadBeast extends ModelDragonBase<EntityDreadBeast> {
    private final ModelAnimator animator;
    public AdvancedModelBox Body;
    public AdvancedModelBox LegL1;
    public AdvancedModelBox LowerBody;
    public AdvancedModelBox Neck1;
    public AdvancedModelBox LegR1;
    public AdvancedModelBox pelt;
    public AdvancedModelBox pelt_1;
    public AdvancedModelBox pelt_2;
    public AdvancedModelBox LegL2;
    public AdvancedModelBox Tail;
    public AdvancedModelBox BackLegR1;
    public AdvancedModelBox BackLegL1;
    public AdvancedModelBox pelt_3;
    public AdvancedModelBox Tail2;
    public AdvancedModelBox Tail3;
    public AdvancedModelBox BackLegR2;
    public AdvancedModelBox BackLegL2;
    public AdvancedModelBox HeadBase;
    public AdvancedModelBox HeadFront;
    public AdvancedModelBox Jaw;
    public AdvancedModelBox ChopsR;
    public AdvancedModelBox ChopsL;
    public AdvancedModelBox EarR;
    public AdvancedModelBox EarL;
    public AdvancedModelBox pelt_4;
    public AdvancedModelBox EarR2;
    public AdvancedModelBox EarL2;
    public AdvancedModelBox LegR2;

    public ModelDreadBeast() {
        this.textureWidth = 256;
        this.textureHeight = 128;
        this.HeadBase = new AdvancedModelBox(this, 0, 15);
        this.HeadBase.setRotationPoint(0.0F, 1.4F, -8.6F);
        this.HeadBase.addBox(-3.5F, -3.51F, -4.6F, 7, 6, 6, 0.0F);
        this.setRotateAngle(HeadBase, -0.12217304763960307F, 0.0F, 0.0F);
        this.LegL1 = new AdvancedModelBox(this, 0, 54);
        this.LegL1.mirror = true;
        this.LegL1.setRotationPoint(3.3F, 2.0F, -3.5F);
        this.LegL1.addBox(-1.0F, 0.0F, -1.5F, 2, 7, 3, 0.0F);
        this.setRotateAngle(LegL1, -0.091106186954104F, 0.0F, 0.0F);
        this.EarR = new AdvancedModelBox(this, 3, 0);
        this.EarR.setRotationPoint(-3.3F, -1.5F, 0.9F);
        this.EarR.addBox(-1.5F, -1.1F, -3.1F, 3, 1, 4, 0.0F);
        this.setRotateAngle(EarR, -2.6406831582674206F, 0.5009094953223726F, -1.5025539530419183F);
        this.BackLegR1 = new AdvancedModelBox(this, 20, 52);
        this.BackLegR1.mirror = true;
        this.BackLegR1.setRotationPoint(2.5F, -0.5F, 9.3F);
        this.BackLegR1.addBox(-1.0F, 0.0F, -1.5F, 2, 8, 5, 0.0F);
        this.setRotateAngle(BackLegR1, 0.045553093477052F, 0.0F, 0.0F);
        this.LegR1 = new AdvancedModelBox(this, 0, 54);
        this.LegR1.setRotationPoint(-3.3F, 2.0F, -3.5F);
        this.LegR1.addBox(-1.0F, 0.0F, -1.5F, 2, 7, 3, 0.0F);
        this.setRotateAngle(LegR1, -0.091106186954104F, 0.0F, 0.0F);
        this.Tail = new AdvancedModelBox(this, 50, 14);
        this.Tail.setRotationPoint(0.0F, -1.6F, 13.2F);
        this.Tail.addBox(-1.5F, -1.0F, 0.0F, 3, 4, 7, 0.0F);
        this.setRotateAngle(Tail, -0.9560913642424937F, 0.0F, 0.0F);
        this.ChopsL = new AdvancedModelBox(this, 37, 0);
        this.ChopsL.mirror = true;
        this.ChopsL.setRotationPoint(2.2F, 1.6F, -1.0F);
        this.ChopsL.addBox(-1.0F, -0.5F, -3.6F, 4, 2, 5, 0.0F);
        this.setRotateAngle(ChopsL, -2.5953045977155678F, -0.8196066167365371F, 0.9560913642424937F);
        this.LegR2 = new AdvancedModelBox(this, 11, 54);
        this.LegR2.setRotationPoint(0.0F, 6.4F, 0.1F);
        this.LegR2.addBox(-1.01F, 0.0F, -1.6F, 2, 6, 2, 0.0F);
        this.pelt_2 = new AdvancedModelBox(this, 91, 49);
        this.pelt_2.setRotationPoint(0.0F, -0.8F, 3.6F);
        this.pelt_2.addBox(-4.0F, -3.4F, 0.0F, 8, 4, 9, 0.0F);
        this.setRotateAngle(pelt_2, 0.091106186954104F, 0.0F, 0.0F);
        this.EarL = new AdvancedModelBox(this, 3, 0);
        this.EarL.mirror = true;
        this.EarL.setRotationPoint(3.3F, -1.5F, 0.9F);
        this.EarL.addBox(-1.5F, -1.1F, -3.1F, 3, 1, 4, 0.0F);
        this.setRotateAngle(EarL, -2.5497515042385164F, -0.36425021489121656F, 1.5025539530419183F);
        this.BackLegR2 = new AdvancedModelBox(this, 0, 44);
        this.BackLegR2.mirror = true;
        this.BackLegR2.setRotationPoint(0.0F, 7.5F, 1.4F);
        this.BackLegR2.addBox(-1.01F, 0.0F, -0.3F, 2, 8, 2, 0.0F);
        this.pelt_3 = new AdvancedModelBox(this, 91, 49);
        this.pelt_3.setRotationPoint(0.0F, 0.4F, 5.6F);
        this.pelt_3.addBox(-4.0F, -3.4F, 0.0F, 8, 4, 9, 0.0F);
        this.setRotateAngle(pelt_3, 0.31869712141416456F, 0.0F, 0.0F);
        this.BackLegL2 = new AdvancedModelBox(this, 0, 44);
        this.BackLegL2.setRotationPoint(0.0F, 7.5F, 1.4F);
        this.BackLegL2.addBox(-1.01F, 0.0F, -0.3F, 2, 8, 2, 0.0F);
        this.HeadFront = new AdvancedModelBox(this, 14, 2);
        this.HeadFront.setRotationPoint(0.0F, -1.2F, -5.1F);
        this.HeadFront.addBox(-1.5F, -2.2F, -5.4F, 3, 4, 8, 0.0F);
        this.setRotateAngle(HeadFront, -0.27314402793711257F, 0.0F, 0.0F);
        this.Jaw = new AdvancedModelBox(this, 28, 9);
        this.Jaw.setRotationPoint(0.0F, 1.5F, -5.7F);
        this.Jaw.addBox(-2.0F, -0.7F, -5.9F, 4, 3, 7, 0.0F);
        this.setRotateAngle(Jaw, 0.27314402793711257F, 0.0F, -3.141592653589793F);
        this.Neck1 = new AdvancedModelBox(this, 0, 28);
        this.Neck1.setRotationPoint(0.0F, -0.8F, -4.5F);
        this.Neck1.addBox(-2.5F, -2.0F, -8.2F, 5, 6, 8, 0.0F);
        this.setRotateAngle(Neck1, 0.36425021489121656F, 0.0F, 0.0F);
        this.Tail2 = new AdvancedModelBox(this, 81, 22);
        this.Tail2.setRotationPoint(0.0F, 0.4F, 6.0F);
        this.Tail2.addBox(-2.01F, -1.7F, -0.8F, 4, 5, 8, 0.0F);
        this.setRotateAngle(Tail2, 0.5462880558742251F, 0.0F, -0.091106186954104F);
        this.LegL2 = new AdvancedModelBox(this, 11, 54);
        this.LegL2.mirror = true;
        this.LegL2.setRotationPoint(0.0F, 6.4F, 0.1F);
        this.LegL2.addBox(-1.01F, 0.0F, -1.6F, 2, 6, 2, 0.0F);
        this.Body = new AdvancedModelBox(this, 38, 45);
        this.Body.setRotationPoint(0.0F, 9.3F, -6.0F);
        this.Body.addBox(-4.0F, -3.9F, -6.5F, 8, 10, 10, 0.0F);
        this.setRotateAngle(Body, 0.091106186954104F, 0.0F, 0.0F);
        this.pelt = new AdvancedModelBox(this, 90, 49);
        this.pelt.setRotationPoint(0.0F, 0.2F, -5.4F);
        this.pelt.addBox(-5.0F, -3.4F, 0.0F, 10, 4, 9, 0.0F);
        this.setRotateAngle(pelt, 0.4553564018453205F, 0.0F, 0.0F);
        this.EarL2 = new AdvancedModelBox(this, 5, 2);
        this.EarL2.setRotationPoint(0.3F, 0.1F, -2.9F);
        this.EarL2.addBox(-1.0F, -1.1F, -1.5F, 2, 1, 2, 0.0F);
        this.setRotateAngle(EarL2, 0.091106186954104F, 0.0F, 0.0F);
        this.Tail3 = new AdvancedModelBox(this, 69, 10);
        this.Tail3.setRotationPoint(0.0F, 0.7F, 5.7F);
        this.Tail3.addBox(-1.01F, -2.1F, 0.0F, 2, 4, 7, 0.0F);
        this.setRotateAngle(Tail3, -0.136659280431156F, 0.0F, 0.0F);
        this.BackLegL1 = new AdvancedModelBox(this, 20, 52);
        this.BackLegL1.setRotationPoint(-2.5F, -0.5F, 9.3F);
        this.BackLegL1.addBox(-1.0F, 0.0F, -1.5F, 2, 8, 5, 0.0F);
        this.setRotateAngle(BackLegL1, 0.045553093477052F, 0.0F, 0.0F);
        this.ChopsR = new AdvancedModelBox(this, 37, 0);
        this.ChopsR.setRotationPoint(-2.2F, 1.6F, -1.0F);
        this.ChopsR.addBox(-3.0F, -0.5F, -3.6F, 4, 2, 5, 0.0F);
        this.setRotateAngle(ChopsR, -2.5953045977155678F, 0.8196066167365371F, -0.9560913642424937F);
        this.EarR2 = new AdvancedModelBox(this, 5, 2);
        this.EarR2.mirror = true;
        this.EarR2.setRotationPoint(-0.3F, 0.1F, -2.9F);
        this.EarR2.addBox(-1.0F, -1.1F, -1.5F, 2, 1, 2, 0.0F);
        this.setRotateAngle(EarR2, 0.091106186954104F, 0.0F, 0.0F);
        this.LowerBody = new AdvancedModelBox(this, 4, 69);
        this.LowerBody.setRotationPoint(0.0F, -0.5F, 3.0F);
        this.LowerBody.addBox(-3.0F, -2.7F, -0.1F, 6, 8, 14, 0.0F);
        this.setRotateAngle(LowerBody, -0.136659280431156F, 0.0F, 0.0F);
        this.pelt_4 = new AdvancedModelBox(this, 92, 49);
        this.pelt_4.setRotationPoint(0.0F, 1.4F, -3.4F);
        this.pelt_4.addBox(-3.0F, -3.4F, 0.0F, 6, 4, 9, 0.0F);
        this.setRotateAngle(pelt_4, 0.31869712141416456F, 0.0F, 0.0F);
        this.pelt_1 = new AdvancedModelBox(this, 90, 49);
        this.pelt_1.setRotationPoint(0.0F, -0.8F, 0.6F);
        this.pelt_1.addBox(-5.0F, -3.4F, 0.0F, 10, 4, 9, 0.0F);
        this.setRotateAngle(pelt_1, 0.5462880558742251F, 0.0F, 0.0F);
        this.Neck1.addChild(this.HeadBase);
        this.Body.addChild(this.LegL1);
        this.HeadBase.addChild(this.EarR);
        this.LowerBody.addChild(this.BackLegR1);
        this.Body.addChild(this.LegR1);
        this.LowerBody.addChild(this.Tail);
        this.HeadBase.addChild(this.ChopsL);
        this.LegR1.addChild(this.LegR2);
        this.Body.addChild(this.pelt_2);
        this.HeadBase.addChild(this.EarL);
        this.BackLegR1.addChild(this.BackLegR2);
        this.LowerBody.addChild(this.pelt_3);
        this.BackLegL1.addChild(this.BackLegL2);
        this.HeadBase.addChild(this.HeadFront);
        this.HeadBase.addChild(this.Jaw);
        this.Body.addChild(this.Neck1);
        this.Tail.addChild(this.Tail2);
        this.LegL1.addChild(this.LegL2);
        this.Body.addChild(this.pelt);
        this.EarL.addChild(this.EarL2);
        this.Tail2.addChild(this.Tail3);
        this.LowerBody.addChild(this.BackLegL1);
        this.HeadBase.addChild(this.ChopsR);
        this.EarR.addChild(this.EarR2);
        this.Body.addChild(this.LowerBody);
        this.HeadBase.addChild(this.pelt_4);
        this.Body.addChild(this.pelt_1);
        animator = ModelAnimator.create();
        this.updateDefaultPose();
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(Body);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return EntityModelPartBuilder.getAllPartsFromClass(this.getClass(), this.getClass().getName());
    }

    @Override
    public void setRotationAngles(EntityDreadBeast entity, float f, float f1, float f2, float f3, float f4) {
        animate(entity, f, f1, f2, f3, f4, 0);
        float speed_walk = 0.45F;
        float speed_idle = 0.05F;
        float degree_walk = 1F;
        float degree_idle = 0.5F;
        AdvancedModelBox[] NECK = new AdvancedModelBox[]{Neck1, HeadBase};
        AdvancedModelBox[] TAIL = new AdvancedModelBox[]{Tail, Tail2, Tail3};
        this.chainWave(NECK, speed_idle, degree_idle * 0.15F, -1, f2, 1);
        this.walk(Jaw, speed_idle, degree_idle * 0.35F, true, 1, -0.1F, f2, 1);
        this.walk(pelt, speed_idle, degree_idle * 0.2F, false, 3, -0.1F, f2, 1);
        this.walk(pelt_1, speed_idle, degree_idle * 0.2F, false, 3, -0.1F, f2, 1);
        this.walk(pelt_2, speed_idle, degree_idle * 0.2F, false, 3, -0.1F, f2, 1);
        this.walk(pelt_3, speed_idle, degree_idle * 0.2F, false, 3, -0.1F, f2, 1);
        this.walk(pelt_4, speed_idle, degree_idle * 0.2F, false, 3, -0.1F, f2, 1);
        this.chainSwing(TAIL, speed_idle, degree_idle * 0.75F, -2, f2, 1);
        this.chainWave(TAIL, speed_idle, degree_idle * 0.15F, 0, f2, 1);
        this.chainWave(NECK, speed_walk, degree_walk * 0.15F, -1, f, f1);
        this.chainWave(TAIL, speed_walk, degree_walk * 0.15F, 2, f, f1);
        this.bob(Body, speed_walk, degree_walk * 1.15F, false, f, f1);
        this.walk(BackLegR1, speed_walk, degree_walk * -0.75F, true, 0, 0F, f, f1);
        this.walk(BackLegL1, speed_walk, degree_walk * -0.75F, true, 0, 0F, f, f1);
        this.walk(BackLegR2, speed_walk, degree_walk * -0.5F, true, 1, 0.3F, f, f1);
        this.walk(BackLegL2, speed_walk, degree_walk * -0.5F, true, 1, 0.3F, f, f1);
        this.walk(LegR1, speed_walk, degree_walk * -0.75F, false, 0, 0F, f, f1);
        this.walk(LegL1, speed_walk, degree_walk * -0.75F, false, 0, 0F, f, f1);
        this.walk(LegR2, speed_walk, degree_walk * -0.5F, false, -1, 0.3F, f, f1);
        this.walk(LegL2, speed_walk, degree_walk * -0.5F, false, -1, 0.3F, f, f1);
        float f12 = -0.9560913642424937F + f1;
        if (f12 > Math.toRadians(-20)) {
            f12 = (float) Math.toRadians(-20);
        }
        this.Tail.rotateAngleX = f12;
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntityDreadBeast.ANIMATION_BITE);
        animator.startKeyframe(5);
        this.rotate(animator, Neck1, -39, 0, 0);
        this.rotate(animator, HeadBase, 40, 0, -15);
        this.rotate(animator, Jaw, -50, 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        this.rotate(animator, Neck1, -19, 0, 0);
        this.rotate(animator, HeadBase, 20, 0, 10);
        this.rotate(animator, Jaw, 10, 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
        animator.setAnimation(EntityDreadBeast.ANIMATION_SPAWN);
        animator.startKeyframe(0);
        animator.move(this.Body, 0, 35, 0);
        animator.endKeyframe();
        animator.startKeyframe(30);
        animator.move(this.Body, 0, 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);
    }

    @Override
    public void renderStatue(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, Entity living) {
        this.render(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
