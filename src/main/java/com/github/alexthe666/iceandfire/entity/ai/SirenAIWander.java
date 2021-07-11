package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntitySiren;

import net.minecraft.entity.ai.goal.RandomWalkingGoal;

public class SirenAIWander extends RandomWalkingGoal {

    private EntitySiren siren;

    public SirenAIWander(EntitySiren creatureIn, double speedIn) {
        super(creatureIn, speedIn);
        this.siren = creatureIn;
    }

    public boolean shouldExecute() {
        return !this.siren.isInWater() && !siren.isSinging() && super.shouldExecute();
    }

    public boolean shouldContinueExecuting() {
        return !this.siren.isInWater() && !siren.isSinging() && super.shouldContinueExecuting();
    }
}