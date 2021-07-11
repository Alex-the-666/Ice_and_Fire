package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityCockatrice;

import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class CockatriceAIWander extends Goal {
    private EntityCockatrice cockatrice;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private double speed;
    private int executionChance;
    private boolean mustUpdate;

    public CockatriceAIWander(EntityCockatrice creatureIn, double speedIn) {
        this(creatureIn, speedIn, 20);
    }

    public CockatriceAIWander(EntityCockatrice creatureIn, double speedIn, int chance) {
        this.cockatrice = creatureIn;
        this.speed = speedIn;
        this.executionChance = chance;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        if (!cockatrice.canMove() || cockatrice.getCommand() != 0) {
            return false;
        }
        if (!this.mustUpdate) {
            if (this.cockatrice.getRNG().nextInt(executionChance) != 0) {
                return false;
            }
        }
        Vector3d Vector3d = RandomPositionGenerator.findRandomTarget(this.cockatrice, 10, 7);
        if (Vector3d == null) {
            return false;
        } else {
            this.xPosition = Vector3d.x;
            this.yPosition = Vector3d.y;
            this.zPosition = Vector3d.z;
            this.mustUpdate = false;

            return true;
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !this.cockatrice.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        this.cockatrice.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }

    public void makeUpdate() {
        this.mustUpdate = true;
    }

    public void setExecutionChance(int newchance) {
        this.executionChance = newchance;
    }
}