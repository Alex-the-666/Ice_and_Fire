package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;

import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;

public class DeathWormAIWander extends WaterAvoidingRandomWalkingGoal {

    private EntityDeathWorm worm;

    public DeathWormAIWander(EntityDeathWorm creatureIn, double speedIn) {
        super(creatureIn, speedIn);
        this.worm = creatureIn;
    }

    public boolean shouldExecute() {
        return !worm.isInSand() && super.shouldExecute();
    }

    public boolean shouldContinueExecuting() {
        return !worm.isInSand() && super.shouldContinueExecuting();
    }
}