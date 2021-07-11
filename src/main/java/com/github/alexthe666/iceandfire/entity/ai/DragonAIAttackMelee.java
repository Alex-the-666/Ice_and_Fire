package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;

import com.github.alexthe666.iceandfire.pathfinding.raycoms.AdvancedPathNavigate;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.PathResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class DragonAIAttackMelee extends Goal {
    protected EntityDragonBase dragon;
    private int attackTick;
    private double speedTowardsTarget;
    private boolean longMemory;
    private int delayCounter;
    private double targetX;
    private double targetY;
    private double targetZ;
    private int failedPathFindingPenalty = 0;
    private boolean canPenalize = false;
    private PathResult attackPath;
    private AdvancedPathNavigate pathNavigate;
    private BlockPos lastTargetPosition;

    public DragonAIAttackMelee(EntityDragonBase dragon, double speedIn, boolean useLongMemory) {
        this.dragon = dragon;
        this.speedTowardsTarget = speedIn;
        this.longMemory = useLongMemory;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        LivingEntity livingEntity = this.dragon.getAttackTarget();
        if (!(this.dragon.getNavigator() instanceof AdvancedPathNavigate)) {
            return false;
        }
        if (livingEntity == null) {
            return false;
        } else if (!livingEntity.isAlive()) {
            return false;
        } else if (!dragon.canMove() || dragon.isHovering() || dragon.isFlying()) {
            return false;
        } else {
            attackPath = ((AdvancedPathNavigate) this.dragon.getNavigator()).moveToLivingEntity(livingEntity, speedTowardsTarget);
            return true;
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        LivingEntity LivingEntity = this.dragon.getAttackTarget();
        if (!(this.dragon.getNavigator() instanceof AdvancedPathNavigate)) {
            return false;
        }
        if (LivingEntity != null && !LivingEntity.isAlive()) {
            this.resetTask();
            return false;
        }

        return LivingEntity != null && LivingEntity.isAlive() && !dragon.isFlying() && !dragon.isHovering();
    }

    @Override
    public void startExecuting() {
        this.delayCounter = 0;
    }

    @Override
    public void resetTask() {
        LivingEntity LivingEntity = this.dragon.getAttackTarget();
        if (LivingEntity instanceof PlayerEntity && (LivingEntity.isSpectator() || ((PlayerEntity) LivingEntity).isCreative())) {
            this.dragon.setAttackTarget(null);
        }
        this.dragon.getNavigator().clearPath();
    }

    @Override
    public void tick() {
        LivingEntity entity = this.dragon.getAttackTarget();
        if(delayCounter > 0){
            delayCounter--;
        }
        if (entity != null) {
            double resetDist = 10;
            attackPath = ((AdvancedPathNavigate) this.dragon.getNavigator()).moveToLivingEntity(entity, speedTowardsTarget);

            if (dragon.getAnimation() == EntityDragonBase.ANIMATION_SHAKEPREY) {
                this.resetTask();
                return;
            }
            double d0 = this.dragon.getDistanceSq(entity.getPosX(), entity.getBoundingBox().minY, entity.getPosZ());
            double d1 = this.getAttackReachSqr(entity);
            --this.delayCounter;
            if ((this.longMemory || this.dragon.getEntitySenses().canSee(entity)) && this.delayCounter <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || entity.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.dragon.getRNG().nextFloat() < 0.05F)) {
                this.targetX = entity.getPosX();
                this.targetY = entity.getBoundingBox().minY;
                this.targetZ = entity.getPosZ();
                this.delayCounter = 4 + this.dragon.getRNG().nextInt(7);

                if (this.canPenalize) {
                    this.delayCounter += failedPathFindingPenalty;
                    if (this.dragon.getNavigator().getPath() != null) {
                        net.minecraft.pathfinding.PathPoint finalPathPoint = this.dragon.getNavigator().getPath().getFinalPathPoint();
                        if (finalPathPoint != null && entity.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
                            failedPathFindingPenalty = 0;
                        else
                            failedPathFindingPenalty += 10;
                    } else {
                        failedPathFindingPenalty += 10;
                    }
                }

                if (d0 > 1024.0D) {
                    this.delayCounter += 10;
                } else if (d0 > 256.0D) {
                    this.delayCounter += 5;
                }
                if (this.dragon.canMove()) {
                    this.delayCounter += 15;
                }
            }

            this.attackTick = Math.max(this.attackTick - 1, 0);

            if (d0 <= d1 && this.attackTick <= 0) {
                this.attackTick = 20;
                this.dragon.swingArm(Hand.MAIN_HAND);
                this.dragon.attackEntityAsMob(entity);
            }
        }
    }

    protected double getAttackReachSqr(LivingEntity attackTarget) {
        return this.dragon.getWidth() * 2.0F * this.dragon.getWidth() * 2.0F + attackTarget.getWidth();
    }
}