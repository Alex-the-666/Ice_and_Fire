package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityCyclops;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.util.Hand;

public class CyclopsAIAttackMelee extends MeleeAttackGoal {

    public CyclopsAIAttackMelee(EntityCyclops creature, double speedIn, boolean useLongMemory) {
        super(creature, speedIn, useLongMemory);
    }

    protected void checkAndPerformAttack(LivingEntity entity, double distance) {
        double d0 = this.getAttackReachSqr(entity);
        if (isCyclopsBlinded() && distance >= 36D) {
            this.resetTask();
            return;
        }
        if (distance <= d0 ) {
            this.attacker.swingArm(Hand.MAIN_HAND);
            this.attacker.attackEntityAsMob(entity);
        }
    }

    private boolean isCyclopsBlinded() {
        return this.attacker instanceof EntityCyclops && ((EntityCyclops) this.attacker).isBlinded();
    }
}
