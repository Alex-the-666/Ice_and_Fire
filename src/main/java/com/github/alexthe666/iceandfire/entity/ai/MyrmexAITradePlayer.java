package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class MyrmexAITradePlayer extends Goal {
    private final EntityMyrmexBase myrmex;

    public MyrmexAITradePlayer(EntityMyrmexBase myrmex) {
        this.myrmex = myrmex;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    /**
     * Returns whether the Goal should begin execution.
     */
    public boolean shouldExecute() {
        if (!this.myrmex.isAlive()) {
            return false;
        } else if (this.myrmex.isInWater()) {
            return false;
        } else if (!this.myrmex.isOnGround()) {
            return false;
        } else if (this.myrmex.velocityChanged) {
            return false;
        } else {
            PlayerEntity PlayerEntity = this.myrmex.getCustomer();
            if (PlayerEntity == null) {
                return false;
            } else if (this.myrmex.getDistanceSq(PlayerEntity) > 16.0D) {
                return false;
            }
            else if (this.myrmex.getHive() != null && !this.myrmex.getHive().isPlayerReputationTooLowToTrade(PlayerEntity.getUniqueID())){
                return false;
            }
            else {
                return PlayerEntity.openContainer != null;
            }
        }
    }

    public void tick() {
        this.myrmex.getNavigator().clearPath();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {
        this.myrmex.setCustomer(null);
    }
}
