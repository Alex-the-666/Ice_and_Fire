package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexSoldier;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class MyrmexAIEscortEntity extends Goal {
    private final EntityMyrmexSoldier myrmex;
    private final double movementSpeed;
    private Path path;

    public MyrmexAIEscortEntity(EntityMyrmexSoldier entityIn, double movementSpeedIn) {
        this.myrmex = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        return this.myrmex.canMove() && this.myrmex.getAttackTarget() == null && this.myrmex.guardingEntity != null && (this.myrmex.guardingEntity.canSeeSky() || !this.myrmex.canSeeSky()) && !this.myrmex.isEnteringHive;
    }

    public void tick() {
        if (this.myrmex.guardingEntity != null && (this.myrmex.getDistance(this.myrmex.guardingEntity) > 30 || this.myrmex.getNavigator().noPath())) {
            this.myrmex.getNavigator().tryMoveToEntityLiving(this.myrmex.guardingEntity, movementSpeed);
        }
    }

    public boolean shouldContinueExecuting() {
        return this.myrmex.canMove() && this.myrmex.getAttackTarget() == null && this.myrmex.guardingEntity != null && this.myrmex.guardingEntity.isAlive() && (this.myrmex.getDistance(this.myrmex.guardingEntity) < 15 || !this.myrmex.getNavigator().noPath()) && (this.myrmex.canSeeSky() == this.myrmex.guardingEntity.canSeeSky() && !this.myrmex.guardingEntity.canSeeSky());
    }

}