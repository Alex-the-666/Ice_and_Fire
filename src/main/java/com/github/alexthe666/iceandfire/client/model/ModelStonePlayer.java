package com.github.alexthe666.iceandfire.client.model;

import com.github.alexthe666.iceandfire.entity.EntityStoneStatue;

import net.minecraft.client.renderer.entity.model.BipedModel;

public class ModelStonePlayer extends BipedModel<EntityStoneStatue> {

    public ModelStonePlayer(float modelSize, boolean smallArmsIn) {
        super(modelSize);
    }

    public void setRotationAngles(EntityStoneStatue entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
}
