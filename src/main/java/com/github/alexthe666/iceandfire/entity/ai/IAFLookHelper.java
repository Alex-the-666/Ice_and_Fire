package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.LookController;

public class IAFLookHelper extends LookController {

    public IAFLookHelper(MobEntity LivingEntityIn) {
        super(LivingEntityIn);
    }

    @Override
    public void setLookPositionWithEntity(Entity entityIn, float deltaYaw, float deltaPitch) {
        try {
            super.setLookPositionWithEntity(entityIn, deltaYaw, deltaPitch);//rarely causes crash with vanilla
        } catch (Exception e) {
            IceAndFire.LOGGER.warn(" Stopped a crash from happening relating to faulty looking AI.");
        }
    }
}
