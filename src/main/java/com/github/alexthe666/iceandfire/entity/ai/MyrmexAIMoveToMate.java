package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexRoyal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class MyrmexAIMoveToMate extends Goal {
    private final EntityMyrmexRoyal myrmex;
    private final double movementSpeed;
    private Path path;

    public MyrmexAIMoveToMate(EntityMyrmexRoyal entityIn, double movementSpeedIn) {
        this.myrmex = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        return this.myrmex.canMove() && this.myrmex.getAttackTarget() == null && this.myrmex.mate != null && this.myrmex.canSeeSky();
    }

    public void tick() {
        if (this.myrmex.mate != null && (this.myrmex.getDistance(this.myrmex.mate) > 30 || this.myrmex.getNavigator().noPath())) {
            this.myrmex.getMoveHelper().setMoveTo(this.myrmex.mate.getPosX(), this.myrmex.getPosY(), this.myrmex.mate.getPosZ(), movementSpeed);
        }
    }

    public boolean shouldContinueExecuting() {
        return this.myrmex.canMove() && this.myrmex.getAttackTarget() == null && this.myrmex.mate != null && this.myrmex.mate.isAlive() && (this.myrmex.getDistance(this.myrmex.mate) < 15 || !this.myrmex.getNavigator().noPath());
    }

}