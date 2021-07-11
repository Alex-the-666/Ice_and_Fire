package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.iceandfire.client.model.util.HideableModelRenderer;
import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelCube extends AdvancedEntityModel {
    public HideableModelRenderer bipedHead;

    public ModelCube() {
        this(0.0F);
    }

    public ModelCube(float modelSize) {
        this.textureHeight = 32;
        this.textureWidth = 64;
        this.bipedHead = new HideableModelRenderer(this, 0, 0);
        this.bipedHead.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16, modelSize - 0.5F);
        this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.updateDefaultPose();
    }

    @Override
    public void setRotationAngles(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
    }

    @Override
    public Iterable<ModelRenderer> getParts() {
        return ImmutableList.of(bipedHead);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(bipedHead);
    }

}