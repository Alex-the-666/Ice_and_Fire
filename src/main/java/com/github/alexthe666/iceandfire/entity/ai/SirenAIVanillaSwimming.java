package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntitySiren;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.GroundPathNavigator;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class SirenAIVanillaSwimming extends Goal {
    private final EntitySiren entity;

    public SirenAIVanillaSwimming(EntitySiren entityIn) {
        this.entity = entityIn;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
        if (entityIn.getNavigator() instanceof GroundPathNavigator) {
            entityIn.getNavigator().setCanSwim(true);
        }
    }

    public boolean shouldExecute() {
        return (this.entity.isInWater() || this.entity.isInLava()) && this.entity.wantsToSing();
    }

    public void tick() {
        if (this.entity.getRNG().nextFloat() < 0.8F) {
            this.entity.getJumpController().setJumping();
        }
    }
}