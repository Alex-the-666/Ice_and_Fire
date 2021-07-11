package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.iceandfire.client.model.util.EntityModelPartBuilder;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexSentinel;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;

public class ModelMyrmexSentinel extends ModelMyrmexBase {
    public AdvancedModelBox Body2;
    public AdvancedModelBox Body3;
    public AdvancedModelBox Body1;
    public AdvancedModelBox legTopR2;
    public AdvancedModelBox legTopR2_1;
    public AdvancedModelBox legTopR3;
    public AdvancedModelBox legTopR3_1;
    public AdvancedModelBox Tail1;
    public AdvancedModelBox legMidR3;
    public AdvancedModelBox legBottomR3;
    public AdvancedModelBox legMidR3_1;
    public AdvancedModelBox legBottomR3_1;
    public AdvancedModelBox Tail2;
    public AdvancedModelBox Tail3;
    public AdvancedModelBox Tail4;
    public AdvancedModelBox Tail5;
    public AdvancedModelBox Tail6;
    public AdvancedModelBox Tail7;
    public AdvancedModelBox Tail8;
    public AdvancedModelBox Tail9;
    public AdvancedModelBox Stinger;
    public AdvancedModelBox Neck1;
    public AdvancedModelBox legTopR1;
    public AdvancedModelBox legTopR1_1;
    public AdvancedModelBox HeadBase;
    public AdvancedModelBox EyeR;
    public AdvancedModelBox MandibleL;
    public AdvancedModelBox MandibleR;
    public AdvancedModelBox EyeL;
    public AdvancedModelBox legMidR1;
    public AdvancedModelBox legBottomR1;
    public AdvancedModelBox legMidR1_1;
    public AdvancedModelBox legBottomR1_1;
    public AdvancedModelBox legMidR2;
    public AdvancedModelBox legBottomR2;
    public AdvancedModelBox legMidR2_1;
    public AdvancedModelBox legBottomR2_1;
    private ModelAnimator animator;

    public ModelMyrmexSentinel() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.MandibleR = new AdvancedModelBox(this, 0, 25);
        this.MandibleR.mirror = true;
        this.MandibleR.setRotationPoint(-3.4F, 3.7F, -7.7F);
        this.MandibleR.addBox(-2.0F, -2.51F, -5.1F, 4, 2, 6, 0.0F);
        this.setRotateAngle(MandibleR, 0.17453292519943295F, -0.18203784098300857F, 0.0F);
        this.legTopR2_1 = new AdvancedModelBox(this, 0, 54);
        this.legTopR2_1.mirror = true;
        this.legTopR2_1.setRotationPoint(-3.3F, 1.0F, 1.6F);
        this.legTopR2_1.addBox(-1.0F, 0.0F, -1.5F, 2, 10, 3, 0.0F);
        this.setRotateAngle(legTopR2_1, -0.5235987755982988F, -0.5235987755982988F, 0.6981317007977318F);
        this.MandibleL = new AdvancedModelBox(this, 0, 25);
        this.MandibleL.setRotationPoint(3.4F, 3.7F, -7.7F);
        this.MandibleL.addBox(-2.0F, -2.51F, -5.1F, 4, 2, 6, 0.0F);
        this.setRotateAngle(MandibleL, 0.17453292519943295F, 0.18203784098300857F, 0.0F);
        this.legTopR1 = new AdvancedModelBox(this, 0, 75);
        this.legTopR1.setRotationPoint(-3.3F, 0.0F, -8.4F);
        this.legTopR1.addBox(-1.5F, 0.0F, -1.5F, 3, 17, 3, 0.0F);
        this.setRotateAngle(legTopR1, 1.2217304763960306F, 0.0F, 0.18203784098300857F);
        this.legBottomR1_1 = new AdvancedModelBox(this, 0, 98);
        this.legBottomR1_1.setRotationPoint(0.0F, 17.4F, 0.0F);
        this.legBottomR1_1.addBox(-1.5F, 0.0F, -2.9F, 3, 20, 6, 0.0F);
        this.setRotateAngle(legBottomR1_1, 2.96705972839036F, 0.0F, 0.0F);
        this.legMidR1_1 = new AdvancedModelBox(this, 12, 75);
        this.legMidR1_1.setRotationPoint(0.0F, 15.4F, 0.1F);
        this.legMidR1_1.addBox(-1.0F, 0.0F, -1.0F, 2, 17, 4, 0.0F);
        this.setRotateAngle(legMidR1_1, -2.792526803190927F, 0.0F, 0.0F);
        this.legTopR1_1 = new AdvancedModelBox(this, 0, 75);
        this.legTopR1_1.mirror = true;
        this.legTopR1_1.setRotationPoint(3.3F, 0.0F, -8.4F);
        this.legTopR1_1.addBox(-1.5F, 0.0F, -1.5F, 3, 17, 3, 0.0F);
        this.setRotateAngle(legTopR1_1, 1.2217304763960306F, 0.0F, -0.18203784098300857F);
        this.EyeL = new AdvancedModelBox(this, 39, 0);
        this.EyeL.setRotationPoint(4.0F, -0.3F, -3.5F);
        this.EyeL.addBox(-1.5F, -1.0F, -3.5F, 3, 2, 7, 0.0F);
        this.setRotateAngle(EyeL, 0.22689280275926282F, -0.08726646259971647F, 1.5707963267948966F);
        this.legBottomR3 = new AdvancedModelBox(this, 22, 51);
        this.legBottomR3.setRotationPoint(0.0F, 10.4F, 0.0F);
        this.legBottomR3.addBox(-1.01F, 0.0F, -0.9F, 2, 16, 2, 0.0F);
        this.setRotateAngle(legBottomR3, 0.0F, 0.0F, -1.3203415791337103F);
        this.legTopR3_1 = new AdvancedModelBox(this, 0, 54);
        this.legTopR3_1.setRotationPoint(3.3F, 1.0F, 1.6F);
        this.legTopR3_1.addBox(-1.0F, 0.0F, -1.5F, 2, 10, 3, 0.0F);
        this.setRotateAngle(legTopR3_1, 0.5009094953223726F, -0.22759093446006054F, -0.6457718232379019F);
        this.Body2 = new AdvancedModelBox(this, 91, 57);
        this.Body2.setRotationPoint(0.0F, 4.0F, -6.0F);
        this.Body2.addBox(-3.0F, -2.7F, -1.1F, 6, 7, 4, 0.0F);
        this.setRotateAngle(Body2, -0.045553093477052F, 0.0F, 0.0F);
        this.legBottomR3_1 = new AdvancedModelBox(this, 22, 51);
        this.legBottomR3_1.mirror = true;
        this.legBottomR3_1.setRotationPoint(0.0F, 10.4F, 0.0F);
        this.legBottomR3_1.addBox(-1.01F, 0.0F, -0.9F, 2, 16, 2, 0.0F);
        this.setRotateAngle(legBottomR3_1, 0.0F, 0.0F, 1.3203415791337103F);
        this.Neck1 = new AdvancedModelBox(this, 32, 22);
        this.Neck1.setRotationPoint(0.0F, -2.0F, -13.0F);
        this.Neck1.addBox(-2.5F, -2.0F, -5.5F, 5, 5, 7, 0.0F);
        this.setRotateAngle(Neck1, 0.36425021489121656F, 0.0F, 0.0F);
        this.Tail6 = new AdvancedModelBox(this, 100, 20);
        this.Tail6.setRotationPoint(0.0F, 0.7F, 3.2F);
        this.Tail6.addBox(-2.5F, -3.5F, -0.1F, 5, 6, 7, 0.0F);
        this.setRotateAngle(Tail6, 0.18203784098300857F, 0.0F, 0.0F);
        this.legMidR1 = new AdvancedModelBox(this, 12, 75);
        this.legMidR1.setRotationPoint(0.0F, 15.4F, 0.1F);
        this.legMidR1.addBox(-1.0F, 0.0F, -1.0F, 2, 17, 4, 0.0F);
        this.setRotateAngle(legMidR1, -2.792526803190927F, 0.0F, 0.0F);
        this.legBottomR2_1 = new AdvancedModelBox(this, 22, 51);
        this.legBottomR2_1.setRotationPoint(0.0F, 10.4F, 0.0F);
        this.legBottomR2_1.addBox(-1.01F, 0.0F, -0.9F, 2, 16, 2, 0.0F);
        this.setRotateAngle(legBottomR2_1, 0.0F, 0.0F, -1.3203415791337103F);
        this.Body1 = new AdvancedModelBox(this, 34, 47);
        this.Body1.setRotationPoint(0.0F, -3.7F, -4.0F);
        this.Body1.addBox(-3.5F, -4.1F, -12.8F, 7, 9, 20, 0.0F);
        this.setRotateAngle(Body1, -0.8196066167365371F, 0.0F, 0.0F);
        this.Tail7 = new AdvancedModelBox(this, 65, 35);
        this.Tail7.setRotationPoint(0.0F, -1.0F, 6.4F);
        this.Tail7.addBox(-2.0F, -2.2F, -1.3F, 4, 5, 5, 0.0F);
        this.setRotateAngle(Tail7, 0.40980330836826856F, 0.0F, 0.0F);
        this.legBottomR1 = new AdvancedModelBox(this, 0, 98);
        this.legBottomR1.setRotationPoint(0.0F, 17.4F, 0.0F);
        this.legBottomR1.addBox(-1.02F, 0.0F, -2.9F, 3, 20, 6, 0.0F);
        this.setRotateAngle(legBottomR1, 2.96705972839036F, 0.0F, 0.0F);
        this.Tail2 = new AdvancedModelBox(this, 100, 20);
        this.Tail2.setRotationPoint(0.0F, 0.7F, 3.2F);
        this.Tail2.addBox(-2.5F, -3.5F, -0.1F, 5, 6, 7, 0.0F);
        this.setRotateAngle(Tail2, 0.4553564018453205F, 0.0F, 0.0F);
        this.EyeR = new AdvancedModelBox(this, 39, 0);
        this.EyeR.mirror = true;
        this.EyeR.setRotationPoint(-4.0F, -0.3F, -3.5F);
        this.EyeR.addBox(-1.5F, -1.0F, -3.5F, 3, 2, 7, 0.0F);
        this.setRotateAngle(EyeR, 0.22689280275926282F, 0.08726646259971647F, -1.5707963267948966F);
        this.legTopR3 = new AdvancedModelBox(this, 0, 54);
        this.legTopR3.mirror = true;
        this.legTopR3.setRotationPoint(-3.3F, 1.0F, 1.6F);
        this.legTopR3.addBox(-1.0F, 0.0F, -1.5F, 2, 10, 3, 0.0F);
        this.setRotateAngle(legTopR3, 0.5009094953223726F, 0.22759093446006054F, 0.6457718232379019F);
        this.Stinger = new AdvancedModelBox(this, 60, 0);
        this.Stinger.setRotationPoint(0.0F, -0.4F, 6.0F);
        this.Stinger.addBox(-1.0F, -2.7F, -1.7F, 2, 10, 2, 0.0F);
        this.setRotateAngle(Stinger, 2.86844862565268F, 0.0F, 0.0F);
        this.legMidR2_1 = new AdvancedModelBox(this, 11, 50);
        this.legMidR2_1.setRotationPoint(0.0F, 8.4F, 0.1F);
        this.legMidR2_1.addBox(-1.5F, 0.0F, -1.0F, 3, 12, 2, 0.0F);
        this.setRotateAngle(legMidR2_1, 0.0F, 0.0F, 1.1383037381507017F);
        this.legMidR3 = new AdvancedModelBox(this, 11, 50);
        this.legMidR3.setRotationPoint(0.0F, 8.4F, 0.1F);
        this.legMidR3.addBox(-1.5F, 0.0F, -1.0F, 3, 12, 2, 0.0F);
        this.setRotateAngle(legMidR3, 0.0F, 0.0F, 1.1383037381507017F);
        this.Tail8 = new AdvancedModelBox(this, 80, 38);
        this.Tail8.setRotationPoint(0.0F, 0.6F, 1.2F);
        this.Tail8.addBox(-5.5F, -3.2F, 0.9F, 11, 7, 11, 0.0F);
        this.setRotateAngle(Tail8, 0.6829473363053812F, 0.0F, 0.0F);
        this.legTopR2 = new AdvancedModelBox(this, 0, 54);
        this.legTopR2.setRotationPoint(3.3F, 1.0F, 1.6F);
        this.legTopR2.addBox(-1.0F, 0.0F, -1.5F, 2, 10, 3, 0.0F);
        this.setRotateAngle(legTopR2, -0.5235987755982988F, 0.5235987755982988F, -0.6981317007977318F);
        this.Tail3 = new AdvancedModelBox(this, 65, 35);
        this.Tail3.setRotationPoint(0.0F, -1.0F, 6.4F);
        this.Tail3.addBox(-2.0F, -2.2F, -1.3F, 4, 5, 5, 0.0F);
        this.setRotateAngle(Tail3, 0.6829473363053812F, 0.0F, 0.0F);
        this.legBottomR2 = new AdvancedModelBox(this, 22, 51);
        this.legBottomR2.mirror = true;
        this.legBottomR2.setRotationPoint(0.0F, 10.4F, 0.0F);
        this.legBottomR2.addBox(-1.01F, 0.0F, -0.9F, 2, 16, 2, 0.0F);
        this.setRotateAngle(legBottomR2, 0.0F, 0.0F, 1.3203415791337103F);
        this.Tail1 = new AdvancedModelBox(this, 65, 35);
        this.Tail1.setRotationPoint(0.0F, -0.4F, 6.4F);
        this.Tail1.addBox(-2.0F, -2.2F, -0.1F, 4, 5, 5, 0.0F);
        this.setRotateAngle(Tail1, -0.045553093477052F, 0.0F, 0.0F);
        this.HeadBase = new AdvancedModelBox(this, 0, 0);
        this.HeadBase.setRotationPoint(0.0F, -1.1F, -4.4F);
        this.HeadBase.addBox(-4.0F, -2.51F, -10.1F, 8, 6, 10, 0.0F);
        this.setRotateAngle(HeadBase, 1.0927506446736497F, 0.0F, 0.0F);
        this.Tail5 = new AdvancedModelBox(this, 65, 35);
        this.Tail5.setRotationPoint(0.0F, -1.0F, 6.4F);
        this.Tail5.addBox(-2.0F, -2.2F, -1.3F, 4, 5, 5, 0.0F);
        this.setRotateAngle(Tail5, 0.40980330836826856F, 0.0F, 0.0F);
        this.legMidR2 = new AdvancedModelBox(this, 11, 50);
        this.legMidR2.mirror = true;
        this.legMidR2.setRotationPoint(0.0F, 8.4F, 0.1F);
        this.legMidR2.addBox(-1.5F, 0.0F, -1.0F, 3, 12, 2, 0.0F);
        this.setRotateAngle(legMidR2, 0.0F, 0.0F, -1.1383037381507017F);
        this.Tail4 = new AdvancedModelBox(this, 100, 20);
        this.Tail4.setRotationPoint(0.0F, 0.7F, 3.2F);
        this.Tail4.addBox(-2.5F, -3.5F, -0.1F, 5, 6, 7, 0.0F);
        this.setRotateAngle(Tail4, 0.27314402793711257F, 0.0F, 0.0F);
        this.legMidR3_1 = new AdvancedModelBox(this, 11, 50);
        this.legMidR3_1.mirror = true;
        this.legMidR3_1.setRotationPoint(0.0F, 8.4F, 0.1F);
        this.legMidR3_1.addBox(-1.5F, 0.0F, -1.0F, 3, 12, 2, 0.0F);
        this.setRotateAngle(legMidR3_1, 0.0F, 0.0F, -1.1383037381507017F);
        this.Tail9 = new AdvancedModelBox(this, 60, 17);
        this.Tail9.setRotationPoint(0.0F, -0.4F, 9.0F);
        this.Tail9.addBox(-4.0F, -2.7F, -0.1F, 8, 6, 7, 0.0F);
        this.setRotateAngle(Tail9, 0.18203784098300857F, 0.0F, 0.0F);
        this.Body3 = new AdvancedModelBox(this, 36, 76);
        this.Body3.setRotationPoint(0.0F, 0.2F, 4.1F);
        this.Body3.addBox(-4.5F, -3.4F, -1.4F, 9, 9, 9, 0.0F);
        this.HeadBase.addChild(this.MandibleR);
        this.Body2.addChild(this.legTopR2_1);
        this.HeadBase.addChild(this.MandibleL);
        this.Body1.addChild(this.legTopR1);
        this.legMidR1_1.addChild(this.legBottomR1_1);
        this.legTopR1_1.addChild(this.legMidR1_1);
        this.Body1.addChild(this.legTopR1_1);
        this.HeadBase.addChild(this.EyeL);
        this.legMidR3.addChild(this.legBottomR3);
        this.Body3.addChild(this.legTopR3_1);
        this.legMidR3_1.addChild(this.legBottomR3_1);
        this.Body1.addChild(this.Neck1);
        this.Tail5.addChild(this.Tail6);
        this.legTopR1.addChild(this.legMidR1);
        this.legMidR2_1.addChild(this.legBottomR2_1);
        this.Body2.addChild(this.Body1);
        this.Tail6.addChild(this.Tail7);
        this.legMidR1.addChild(this.legBottomR1);
        this.Tail1.addChild(this.Tail2);
        this.HeadBase.addChild(this.EyeR);
        this.Body3.addChild(this.legTopR3);
        this.Tail9.addChild(this.Stinger);
        this.legTopR2_1.addChild(this.legMidR2_1);
        this.legTopR3.addChild(this.legMidR3);
        this.Tail7.addChild(this.Tail8);
        this.Body2.addChild(this.legTopR2);
        this.Tail2.addChild(this.Tail3);
        this.legMidR2.addChild(this.legBottomR2);
        this.Body3.addChild(this.Tail1);
        this.Neck1.addChild(this.HeadBase);
        this.Tail4.addChild(this.Tail5);
        this.legTopR2.addChild(this.legMidR2);
        this.Tail3.addChild(this.Tail4);
        this.legTopR3_1.addChild(this.legMidR3_1);
        this.Tail8.addChild(this.Tail9);
        this.Body2.addChild(this.Body3);
        animator = ModelAnimator.create();
        this.updateDefaultPose();
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(Body2);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return EntityModelPartBuilder.getAllPartsFromClass(this.getClass(), this.getClass().getName());
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        this.resetToDefaultPose();
        animator.update(entity);
        animator.setAnimation(EntityMyrmexSentinel.ANIMATION_GRAB);
        animator.startKeyframe(5);
        ModelUtils.rotateFrom(animator, Body1, -65, 0, 0);
        ModelUtils.rotateFrom(animator, Neck1, 0, 0, 0);
        ModelUtils.rotateFrom(animator, legTopR1, -14, 5, 10);
        ModelUtils.rotateFrom(animator, legTopR1_1, -14, -5, -10);
        ModelUtils.rotateFrom(animator, legMidR1, -41, 0, 0);
        ModelUtils.rotateFrom(animator, legMidR1_1, -41, 0, 0);
        ModelUtils.rotateFrom(animator, legBottomR1, 67, 20, 0);
        ModelUtils.rotateFrom(animator, legBottomR1_1, 67, -20, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);

        animator.setAnimation(EntityMyrmexSentinel.ANIMATION_STING);
        animator.startKeyframe(5);
        ModelUtils.rotateFrom(animator, Body1, -36, -15, 0);
        ModelUtils.rotateFrom(animator, Body2, 0, -31, 0);
        ModelUtils.rotateFrom(animator, Body3, 0, -13, 0);
        ModelUtils.rotateFrom(animator, Tail1, 20, 31, 0);
        ModelUtils.rotateFrom(animator, Tail2, 52, 15, 0);
        ModelUtils.rotateFrom(animator, Tail3, 57, 2, 0);
        ModelUtils.rotateFrom(animator, Tail4, 18, 0, 0);
        ModelUtils.rotateFrom(animator, Tail5, 23, 0, 0);
        ModelUtils.rotateFrom(animator, Tail6, 5, 0, 0);
        ModelUtils.rotateFrom(animator, Tail7, 23, 0, 0);
        ModelUtils.rotateFrom(animator, Tail8, 0, 0, 0);
        ModelUtils.rotateFrom(animator, Tail9, 10, 0, 0);
        ModelUtils.rotateFrom(animator, Stinger, 107, 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        ModelUtils.rotateFrom(animator, Body1, -36, 0, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        ModelUtils.rotateFrom(animator, Body1, -36, 15, 0);
        ModelUtils.rotateFrom(animator, Body2, 0, 31, 0);
        ModelUtils.rotateFrom(animator, Body3, 0, 13, 0);
        ModelUtils.rotateFrom(animator, Tail1, 20, -31, 0);
        ModelUtils.rotateFrom(animator, Tail2, 52, -15, 0);
        ModelUtils.rotateFrom(animator, Tail3, 57, -2, 0);
        ModelUtils.rotateFrom(animator, Tail4, 18, 0, 0);
        ModelUtils.rotateFrom(animator, Tail5, 23, 0, 0);
        ModelUtils.rotateFrom(animator, Tail6, 5, 0, 0);
        ModelUtils.rotateFrom(animator, Tail7, 23, 0, 0);
        ModelUtils.rotateFrom(animator, Tail8, 0, 0, 0);
        ModelUtils.rotateFrom(animator, Tail9, 10, 0, 0);
        ModelUtils.rotateFrom(animator, Stinger, 107, 0, 0);
        animator.endKeyframe();
        animator.resetKeyframe(10);
        animator.setAnimation(EntityMyrmexSentinel.ANIMATION_NIBBLE);
        animator.startKeyframe(5);
        ModelUtils.rotate(animator, Neck1, -50, 0, 0);
        ModelUtils.rotate(animator, HeadBase, 50, 0, 0);
        ModelUtils.rotate(animator, MandibleR, 0, 35, 0);
        ModelUtils.rotate(animator, MandibleL, 0, -35, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        ModelUtils.rotate(animator, Neck1, 30, 0, 0);
        ModelUtils.rotate(animator, HeadBase, -30, 0, 0);
        ModelUtils.rotate(animator, MandibleR, 0, -50, 0);
        ModelUtils.rotate(animator, MandibleL, 0, 50, 0);
        animator.endKeyframe();
        animator.setAnimation(EntityMyrmexSentinel.ANIMATION_SLASH);
        animator.startKeyframe(5);
        ModelUtils.rotateFrom(animator, Body1, -65, 0, 0);
        ModelUtils.rotateFrom(animator, Neck1, 0, 0, 0);
        ModelUtils.rotateFrom(animator, legTopR1, 24, 20, 30);
        ModelUtils.rotateFrom(animator, legMidR1, -98, 0, 0);
        ModelUtils.rotateFrom(animator, legBottomR1, 70, 20, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        ModelUtils.rotateFrom(animator, Body1, -65, 0, 0);
        ModelUtils.rotateFrom(animator, Neck1, 0, 0, 0);
        ModelUtils.rotateFrom(animator, legTopR1_1, 24, -20, -30);
        ModelUtils.rotateFrom(animator, legMidR1_1, -98, 0, 0);
        ModelUtils.rotateFrom(animator, legBottomR1_1, 70, -20, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        ModelUtils.rotateFrom(animator, Body1, -65, 0, 0);
        ModelUtils.rotateFrom(animator, Neck1, 0, 0, 0);
        ModelUtils.rotateFrom(animator, legTopR1, 24, 20, 30);
        ModelUtils.rotateFrom(animator, legMidR1, -98, 0, 0);
        ModelUtils.rotateFrom(animator, legBottomR1, 70, 20, 0);
        animator.endKeyframe();
        animator.startKeyframe(5);
        ModelUtils.rotateFrom(animator, Body1, -65, 0, 0);
        ModelUtils.rotateFrom(animator, Neck1, 0, 0, 0);
        ModelUtils.rotateFrom(animator, legTopR1_1, 24, -20, -30);
        ModelUtils.rotateFrom(animator, legMidR1_1, -98, 0, 0);
        ModelUtils.rotateFrom(animator, legBottomR1_1, 70, -20, 0);
        animator.endKeyframe();
        animator.resetKeyframe(5);


    }

    @Override
    public void setRotationAngles(Entity entity, float f, float f1, float f2, float f3, float f4) {
        animate((IAnimatedEntity) entity, f, f1, f2, f3, f4, 1);
        EntityMyrmexSentinel myrmex = (EntityMyrmexSentinel) entity;
        AdvancedModelBox[] TAIL = new AdvancedModelBox[]{Tail1, Tail2, Tail3, Tail4, Tail5, Tail6, Tail7, Tail8, Tail9, Stinger};
        AdvancedModelBox[] NECK = new AdvancedModelBox[]{Neck1, HeadBase};
        AdvancedModelBox[] ARML1 = new AdvancedModelBox[]{legTopR1, legMidR1, legBottomR1};
        AdvancedModelBox[] LEGR2 = new AdvancedModelBox[]{legTopR2, legMidR2, legBottomR2};
        AdvancedModelBox[] LEGR3 = new AdvancedModelBox[]{legTopR3, legMidR3, legBottomR3};
        AdvancedModelBox[] ARMR1 = new AdvancedModelBox[]{legTopR1_1, legMidR1_1, legBottomR1_1};
        AdvancedModelBox[] LEGL2 = new AdvancedModelBox[]{legTopR2_1, legMidR2_1, legBottomR2_1};
        AdvancedModelBox[] LEGL3 = new AdvancedModelBox[]{legTopR3_1, legMidR3_1, legBottomR3_1};
        float speed_walk = 0.9F;
        float speed_idle = myrmex.isHiding() ? 0.015F : 0.035F;
        float degree_walk = 0.5F;
        float degree_idle = myrmex.isHiding() ? 0.1F : 0.25F;

        if (entity.getPassengers().isEmpty()) {
            this.faceTarget(f3, f4, 2, NECK);
        }
        this.chainWave(TAIL, speed_idle, degree_idle * 0.15F, 0, f2, 1);
        this.chainWave(NECK, speed_idle, degree_idle * -0.15F, 2, f2, 1);
        this.swing(MandibleR, speed_idle * 2F, degree_idle * -0.75F, false, 1, 0.2F, f2, 1);
        this.swing(MandibleL, speed_idle * 2F, degree_idle * -0.75F, true, 1, 0.2F, f2, 1);
        this.animateLeg(LEGR3, speed_walk, degree_walk, false, 0, 1, f, f1);
        this.animateLeg(LEGR2, speed_walk, degree_walk, true, 0, 1, f, f1);
        this.animateLeg(LEGL3, speed_walk, degree_walk, false, 1, -1, f, f1);
        this.animateLeg(LEGL2, speed_walk, degree_walk, true, 1, -1, f, f1);
        this.bob(Body2, speed_walk, degree_walk, false, f, f1);
        this.chainWave(ARML1, speed_idle, degree_idle * -0.25F, 0, f2, 1);
        this.chainWave(ARMR1, speed_idle, degree_idle * -0.25F, 0, f2, 1);
        this.progressRotation(legTopR1, myrmex.holdingProgress, (float) Math.toRadians(35F), (float) Math.toRadians(30F), (float) Math.toRadians(10F));
        this.progressRotation(legTopR1_1, myrmex.holdingProgress, (float) Math.toRadians(35F), (float) Math.toRadians(-30F), (float) Math.toRadians(-10F));
        this.progressRotation(legMidR1, myrmex.holdingProgress, (float) Math.toRadians(-133F), 0, 0);
        this.progressRotation(legMidR1_1, myrmex.holdingProgress, (float) Math.toRadians(-133F), 0, 0);
        this.progressRotation(legBottomR1, myrmex.holdingProgress, (float) Math.toRadians(140F), (float) Math.toRadians(20F), 0);
        this.progressRotation(legBottomR1_1, myrmex.holdingProgress, (float) Math.toRadians(140F), (float) Math.toRadians(-20F), 0);


        this.progressRotation(legTopR1, myrmex.hidingProgress, (float) Math.toRadians(70F), 0, (float) Math.toRadians(70F));
        this.progressRotation(legTopR1_1, myrmex.hidingProgress, (float) Math.toRadians(70F), 0, (float) Math.toRadians(-70F));
        this.progressRotation(Body1, myrmex.hidingProgress, (float) Math.toRadians(-2F), 0, 0);
        this.progressPosition(Body1, myrmex.hidingProgress, 0, 1.7F, -4.0F);
        this.progressPosition(Body2, myrmex.hidingProgress, 0, 17F, 0);
        this.progressRotation(Body2, myrmex.hidingProgress, (float) Math.toRadians(5F), 0, 0);

        this.progressRotation(legTopR2, myrmex.hidingProgress, (float) Math.toRadians(55), (float) Math.toRadians(30), (float) Math.toRadians(-7F));
        this.progressRotation(legTopR2_1, myrmex.hidingProgress, (float) Math.toRadians(55), (float) Math.toRadians(-30), (float) Math.toRadians(7F));
        this.progressRotation(legTopR3, myrmex.hidingProgress, (float) Math.toRadians(45), (float) Math.toRadians(10), (float) Math.toRadians(40F));
        this.progressRotation(legTopR3_1, myrmex.hidingProgress, (float) Math.toRadians(45), (float) Math.toRadians(-10), (float) Math.toRadians(-40F));
        this.progressRotation(legMidR2, myrmex.hidingProgress, 0, 0, (float) Math.toRadians(-140F));
        this.progressRotation(legMidR2_1, myrmex.hidingProgress, 0, 0, (float) Math.toRadians(140F));
        this.progressRotation(legMidR3, myrmex.hidingProgress, 0, 0, (float) Math.toRadians(146F));
        this.progressRotation(legMidR3_1, myrmex.hidingProgress, 0, 0, (float) Math.toRadians(-146F));
        this.progressRotation(legBottomR2, myrmex.hidingProgress, 0, 0, (float) Math.toRadians(146F));
        this.progressRotation(legBottomR2_1, myrmex.hidingProgress, 0, 0, (float) Math.toRadians(-146F));
        this.progressRotation(legBottomR3, myrmex.hidingProgress, 0, 0, (float) Math.toRadians(-156F));
        this.progressRotation(legBottomR3_1, myrmex.hidingProgress, 0, 0, (float) Math.toRadians(156F));
        this.progressRotation(HeadBase, myrmex.hidingProgress, (float) Math.toRadians(-15F), 0, 0);
        this.progressPosition(HeadBase, myrmex.hidingProgress, 0.0F, 0F, -4.4F);
        this.progressRotation(Tail1, myrmex.hidingProgress, (float) Math.toRadians(-20F), 0, (float) Math.toRadians(-46F));
        this.progressRotation(Tail2, myrmex.hidingProgress, (float) Math.toRadians(26F), 0, 0);
        this.progressRotation(Tail3, myrmex.hidingProgress, (float) Math.toRadians(40F), 0, (float) Math.toRadians(-30F));
        this.progressRotation(Tail4, myrmex.hidingProgress, (float) Math.toRadians(25F), 0, (float) Math.toRadians(-18F));
        this.progressRotation(Tail5, myrmex.hidingProgress, (float) Math.toRadians(23F), 0, 0);
        this.progressRotation(Tail6, myrmex.hidingProgress, (float) Math.toRadians(10), (float) Math.toRadians(-15), (float) Math.toRadians(33F));
        this.progressRotation(Tail7, myrmex.hidingProgress, (float) Math.toRadians(23F), 0, 0);
        this.progressRotation(Tail8, myrmex.hidingProgress, (float) Math.toRadians(-20), (float) Math.toRadians(-45), (float) Math.toRadians(85F));

    }

    private void animateLeg(AdvancedModelBox[] models, float speed, float degree, boolean reverse, float offset, float weight, float f, float f1) {
        this.flap(models[0], speed, degree * 0.4F, reverse, offset, weight * 0.2F, f, f1);
        this.flap(models[1], speed, degree * 2, reverse, offset, weight * -0.4F, f, f1);
        this.flap(models[1], speed, -degree * 1.2F, reverse, offset, weight * 0.5F, f, f1);
        this.walk(models[0], speed, degree, reverse, offset, 0F, f, f1);

    }

    @Override
    public ModelRenderer[] getHeadParts() {
        return new ModelRenderer[]{Neck1, HeadBase};
    }

    @Override
    public void renderStatue(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, Entity living) {
        this.render(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
