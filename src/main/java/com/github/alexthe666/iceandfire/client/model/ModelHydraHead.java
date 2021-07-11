package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.iceandfire.client.model.util.EntityModelPartBuilder;
import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import com.github.alexthe666.iceandfire.entity.EntityHydra;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;

public class ModelHydraHead extends ModelDragonBase<EntityHydra> {
    public AdvancedModelBox Neck1;
    public AdvancedModelBox Neck2;
    public AdvancedModelBox Neck3;
    public AdvancedModelBox Neck4;
    public AdvancedModelBox Head1;
    public AdvancedModelBox HeadPivot;
    public AdvancedModelBox neckSpike1;
    public AdvancedModelBox neckSpike2;
    public AdvancedModelBox UpperJaw1;
    public AdvancedModelBox LowerJaw1;
    public AdvancedModelBox TeethTR1;
    public AdvancedModelBox TeethL1;
    public AdvancedModelBox TeethR1;
    public AdvancedModelBox TeethTL1;
    private ModelAnimator animator;
    private int headIndex = 0;

    public ModelHydraHead(int headIndex) {
        this.headIndex = headIndex;
        this.textureWidth = 256;
        this.textureHeight = 128;
        this.Neck3 = new AdvancedModelBox(this, 25, 90);
        this.Neck3.setRotationPoint(0.0F, -0.5F, -6.7F);
        this.Neck3.addBox(-1.92F, -2.0F, -8.0F, 4, 4, 9, 0.0F);
        this.setRotateAngle(Neck3, -0.12217304763960307F, -0.0F, 0.0F);
        this.TeethR1 = new AdvancedModelBox(this, 6, 44);
        this.TeethR1.mirror = true;
        this.TeethR1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.TeethR1.addBox(-0.2F, 0.8F, -4.1F, 2, 2, 6, 0.0F);
        this.neckSpike2 = new AdvancedModelBox(this, 0, 0);
        this.neckSpike2.setRotationPoint(0.0F, -0.9F, -3.7F);
        this.neckSpike2.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 4, 0.0F);
        this.setRotateAngle(neckSpike2, 0.6829473363053812F, -0.0F, 0.0F);
        this.Head1 = new AdvancedModelBox(this, 6, 77);
        this.Head1.addBox(-3.0F, -1.3F, -2.5F, 6, 3, 4, 0.0F);
        this.setRotateAngle(Head1, 0.7853981633974483F, -0.0F, 0.0F);

        this.HeadPivot = new AdvancedModelBox(this, 6, 77);
        this.HeadPivot.setRotationPoint(0.0F, -0.2F, -8.3F);

        this.LowerJaw1 = new AdvancedModelBox(this, 6, 63);
        this.LowerJaw1.setRotationPoint(0.0F, 1.9F, -0.8F);
        this.LowerJaw1.addBox(-2.0F, -0.6F, -7.0F, 4, 1, 7, 0.0F);
        this.Neck2 = new AdvancedModelBox(this, 85, 72);
        this.Neck2.setRotationPoint(0.0F, 0.2F, -6.8F);
        this.Neck2.addBox(-2.0F, -2.8F, -7.0F, 4, 5, 8, 0.0F);
        this.setRotateAngle(Neck2, -0.5235987755982988F, 0.0F, 0.0F);
        this.Neck1 = new AdvancedModelBox(this, 56, 80);
        this.Neck1.setRotationPoint(-0.0F, 1.5F, -0.2F);
        this.Neck1.addBox(-2.5F, -3.0F, -7.0F, 5, 6, 9, 0.0F);
        this.setRotateAngle(Neck1, -0.3490658503988659F, 0.0F, 0.0F);
        this.TeethL1 = new AdvancedModelBox(this, 6, 44);
        this.TeethL1.setRotationPoint(0.0F, 0.9F, -2.8F);
        this.TeethL1.addBox(-1.9F, 0.8F, -4.1F, 2, 2, 6, 0.0F);
        this.setRotateAngle(TeethL1, 0.045553093477052F, -0.0F, -3.141592653589793F);
        this.Neck4 = new AdvancedModelBox(this, 35, 70);
        this.Neck4.mirror = true;
        this.Neck4.setRotationPoint(0.0F, 0.0F, -7.4F);
        this.Neck4.addBox(-2.0F, -2.0F, -8.2F, 4, 4, 9, 0.0F);
        this.setRotateAngle(Neck4, 0.47123889803846897F, 0.0F, 0.0F);
        this.neckSpike1 = new AdvancedModelBox(this, 40, 0);
        this.neckSpike1.setRotationPoint(0.0F, -1.2F, -6.0F);
        this.neckSpike1.addBox(-0.5F, -0.5F, 0.0F, 1, 1, 3, 0.0F);
        this.setRotateAngle(neckSpike1, 0.7740535232594852F, 0.0F, 0.0F);
        this.TeethTL1 = new AdvancedModelBox(this, 6, 44);
        this.TeethTL1.mirror = true;
        this.TeethTL1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.TeethTL1.addBox(-0.2F, 0.8F, -4.1F, 2, 2, 6, 0.0F);
        this.UpperJaw1 = new AdvancedModelBox(this, 6, 54);
        this.UpperJaw1.setRotationPoint(0.0F, 0.0F, -2.4F);
        this.UpperJaw1.addBox(-2.5F, -1.7F, -5.8F, 5, 3, 6, 0.0F);
        this.setRotateAngle(UpperJaw1, 0.091106186954104F, -0.0F, 0.0F);

        this.TeethTR1 = new AdvancedModelBox(this, 6, 44);
        this.TeethTR1.setRotationPoint(0.0F, -0.4F, -3.6F);
        this.setRotateAngle(TeethTR1, -0.091106186954104F, -0.0F, 0.0F);
        this.TeethTR1.addBox(-1.9F, 0.8F, -4.1F, 2, 2, 6, 0.0F);

        this.Neck2.addChild(this.Neck3);
        this.TeethL1.addChild(this.TeethR1);
        this.Neck4.addChild(this.neckSpike2);
        this.Neck4.addChild(this.HeadPivot);
        this.HeadPivot.addChild(this.Head1);
        this.Head1.addChild(this.LowerJaw1);
        this.Neck1.addChild(this.Neck2);
        this.LowerJaw1.addChild(this.TeethL1);
        this.Neck3.addChild(this.Neck4);
        this.Head1.addChild(this.TeethTR1);
        this.Neck4.addChild(this.neckSpike1);
        this.TeethTR1.addChild(this.TeethTL1);
        this.Head1.addChild(this.UpperJaw1);
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
        float degree_walk = 0.2F;
        float degree_idle = 0.5F;
        if (EntityGorgon.isStoneMob(entity)) {
            return;
        }
        float partialTicks = Minecraft.getInstance().getRenderPartialTicks();
        AdvancedModelBox[] ENTIRE_HEAD = new AdvancedModelBox[]{Neck1, Neck2, Neck3, Neck4};
        this.chainFlap(ENTIRE_HEAD, speed_idle, degree_idle * 0.15F, -3 + headIndex % 4, f2, 1);
        this.chainSwing(ENTIRE_HEAD, speed_idle, degree_idle * 0.05F, -3 + headIndex % 3, f2, 1);
        this.chainWave(ENTIRE_HEAD, speed_idle * 1.5F, degree_idle * 0.05F, -2 + headIndex % 3, f2, 1);
        this.faceTarget(f3, f4, 1, Head1);
        this.walk(neckSpike1, speed_idle * 1.5F, degree_idle * 0.4F, false, 2, -0.1F, f2, 1);
        this.walk(neckSpike2, speed_idle * 1.5F, degree_idle * 0.4F, false, 3, -0.1F, f2, 1);
        this.chainSwing(ENTIRE_HEAD, speed_walk, degree_walk * 0.75F, -3, f, f1);
        float speakProgress = entity.prevSpeakingProgress[headIndex] + partialTicks * (entity.speakingProgress[headIndex] - entity.prevSpeakingProgress[headIndex]);
        this.progressRotationInterp(LowerJaw1, (float) (Math.sin(speakProgress * Math.PI) * 10F), (float) Math.toRadians(25), 0.0F, 0.0F, 10F);
        float limbSwingProgress = f1;
        /*
        this.progressRotationInterp(Neck1, (float)limbSwingProgress, (float) Math.toRadians(5), 0.0F, 0.0F, 1F);
        this.progressRotationInterp(Neck2, (float)limbSwingProgress, (float) Math.toRadians(-5), 0.0F, 0.0F, 1F);
        this.progressRotationInterp(Neck3, (float)limbSwingProgress, (float) Math.toRadians(-5), 0.0F, 0.0F, 1F);
        this.progressRotationInterp(Neck4, (float)limbSwingProgress, (float) Math.toRadians(-5), 0.0F, 0.0F, 1F);
        this.progressRotationInterp(Head1, (float)limbSwingProgress, (float) Math.toRadians(-5), 0.0F, 0.0F, 1F);*/
        float strikeProgress = entity.prevStrikeProgress[headIndex] + partialTicks * (entity.strikingProgress[headIndex] - entity.prevStrikeProgress[headIndex]);
        this.progressRotationInterp(Neck2, strikeProgress, (float) Math.toRadians(5), 0.0F, 0.0F, 10F);
        this.progressRotationInterp(Neck3, strikeProgress, (float) Math.toRadians(5), 0.0F, 0.0F, 10F);
        this.progressRotationInterp(Neck4, strikeProgress, (float) Math.toRadians(5), 0.0F, 0.0F, 10F);
        this.progressRotationInterp(Head1, strikeProgress, (float) Math.toRadians(-15), 0.0F, 0.0F, 10F);
        this.progressRotationInterp(LowerJaw1, strikeProgress, (float) Math.toRadians(45), 0.0F, 0.0F, 10F);
        this.progresPositionInterp(TeethTR1, strikeProgress, 0.5F, 0.0F, 0.0F, 10F);
        float breathProgress = entity.prevBreathProgress[headIndex] + partialTicks * (entity.breathProgress[headIndex] - entity.prevBreathProgress[headIndex]);
        this.progressRotationInterp(Neck4, breathProgress, (float) Math.toRadians(15), 0.0F, 0.0F, 10F);
        this.progressRotationInterp(Neck3, breathProgress, (float) Math.toRadians(15), 0.0F, 0.0F, 10F);
        this.progresPositionInterp(TeethTR1, breathProgress, 0.5F, 0.0F, 0.0F, 10F);
        this.progressRotationInterp(Head1, breathProgress, (float) Math.toRadians(15), 0.0F, 0.0F, 10F);
        this.progressRotationInterp(UpperJaw1, breathProgress, (float) Math.toRadians(-10), 0.0F, 0.0F, 10F);
        this.progressRotationInterp(LowerJaw1, breathProgress, (float) Math.toRadians(50), 0.0F, 0.0F, 10F);


        this.Neck2.showModel = entity.getSeveredHead() != headIndex && entity.isAlive();
    }

    @Override
    public void renderStatue(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, Entity living) {
        this.render(matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }


    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(Neck1);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return EntityModelPartBuilder.getAllPartsFromClass(this.getClass(), this.getClass().getName());
    }
}
