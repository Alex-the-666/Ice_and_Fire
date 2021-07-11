package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;
import java.util.List;

import com.github.alexthe666.iceandfire.entity.EntityAmphithere;

import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.vector.Vector3d;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class AmphithereAIFleePlayer extends Goal {
    private final double farSpeed;
    private final double nearSpeed;
    private final float avoidDistance;
    protected EntityAmphithere entity;
    protected PlayerEntity closestLivingEntity;
    private Path path;

    public AmphithereAIFleePlayer(EntityAmphithere entityIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
        this.entity = entityIn;
        this.avoidDistance = avoidDistanceIn;
        this.farSpeed = farSpeedIn;
        this.nearSpeed = nearSpeedIn;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }


    public boolean shouldExecute() {
        if (!this.entity.isFlying() && !this.entity.isTamed()) {
            List<PlayerEntity> list = this.entity.world.getEntitiesWithinAABB(PlayerEntity.class, this.entity.getBoundingBox().grow(this.avoidDistance, 6D, this.avoidDistance), EntityPredicates.CAN_AI_TARGET);
            if (list.isEmpty()) {
                return false;
            } else {
                this.closestLivingEntity = list.get(0);
                Vector3d Vector3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 20, 7, new Vector3d(this.closestLivingEntity.getPosX(), this.closestLivingEntity.getPosY(), this.closestLivingEntity.getPosZ()));

                if (Vector3d == null) {
                    return false;
                } else if (this.closestLivingEntity.getDistanceSq(Vector3d) < this.closestLivingEntity.getDistanceSq(this.entity)) {
                    return false;
                } else {
                    this.path = this.entity.getNavigator().pathfind(Vector3d.x, Vector3d.y, Vector3d.z, 0);
                    return this.path != null;
                }
            }
        } else {
            return false;
        }
    }

    public boolean shouldContinueExecuting() {
        return !this.entity.getNavigator().noPath();
    }

    public void startExecuting() {
        this.entity.getNavigator().setPath(this.path, this.farSpeed);
    }

    public void resetTask() {
        this.closestLivingEntity = null;
    }

    public void tick() {
        if (this.entity.getDistanceSq(this.closestLivingEntity) < 49.0D) {
            this.entity.getNavigator().setSpeed(this.nearSpeed);
        } else {
            this.entity.getNavigator().setSpeed(this.farSpeed);
        }
    }
}