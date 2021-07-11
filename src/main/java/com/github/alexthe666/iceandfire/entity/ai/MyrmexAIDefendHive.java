package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class MyrmexAIDefendHive extends TargetGoal {
    EntityMyrmexBase myrmex;
    LivingEntity villageAgressorTarget;

    public MyrmexAIDefendHive(EntityMyrmexBase myrmex) {
        super(myrmex, false, true);
        this.myrmex = myrmex;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        MyrmexHive village = this.myrmex.getHive();

        if (!this.myrmex.canMove() || village == null) {
            return false;
        } else {
            this.villageAgressorTarget = village.findNearestVillageAggressor(this.myrmex);
            if (this.isSuitableTarget(this.villageAgressorTarget, EntityPredicate.DEFAULT)) {
                return true;
            } else if (this.goalOwner.getRNG().nextInt(20) == 0) {
                this.villageAgressorTarget = village.getNearestTargetPlayer(this.myrmex, this.myrmex.world);
                return this.isSuitableTarget(this.villageAgressorTarget, EntityPredicate.DEFAULT);
            } else {
                return false;
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        this.myrmex.setAttackTarget(this.villageAgressorTarget);
        super.startExecuting();
    }
}