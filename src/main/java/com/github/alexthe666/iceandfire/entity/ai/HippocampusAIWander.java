package com.github.alexthe666.iceandfire.entity.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.passive.TameableEntity;

public class HippocampusAIWander extends RandomWalkingGoal {

    public HippocampusAIWander(CreatureEntity creatureIn, double speedIn) {
        super(creatureIn, speedIn);
    }

    public boolean shouldExecute() {
        return !(creature instanceof TameableEntity && ((TameableEntity) creature).isQueuedToSit()) && !this.creature.isInWater() && super.shouldExecute();
    }

    public boolean shouldContinueExecuting() {
        return !(creature instanceof TameableEntity && ((TameableEntity) creature).isQueuedToSit()) && !this.creature.isInWater() && super.shouldContinueExecuting();
    }
}