package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityAmphithere;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.player.PlayerEntity;

public class AmphithereAIHurtByTarget extends HurtByTargetGoal {

    public AmphithereAIHurtByTarget(EntityAmphithere amphithere, boolean help, Class[] classes) {
        super(amphithere, classes);
    }

    protected void setEntityAttackTarget(MobEntity creatureIn, LivingEntity LivingEntityIn) {
        EntityAmphithere amphithere = (EntityAmphithere) creatureIn;
        if (amphithere.isTamed() || !(LivingEntityIn instanceof PlayerEntity)) {
            amphithere.setAttackTarget(LivingEntityIn);
        }
    }
}
