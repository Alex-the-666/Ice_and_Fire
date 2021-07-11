package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.AdvancedPathNavigate;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.PathResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

import java.util.EnumSet;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class MyrmexAIAttackMelee extends Goal {
    protected EntityMyrmexBase myrmex;
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

    public MyrmexAIAttackMelee(EntityMyrmexBase dragon, double speedIn, boolean useLongMemory) {
        this.myrmex = dragon;
        this.speedTowardsTarget = speedIn;
        this.longMemory = useLongMemory;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        LivingEntity LivingEntity = this.myrmex.getAttackTarget();
        if (this.myrmex.getNavigator() instanceof AdvancedPathNavigate) {
            pathNavigate = (AdvancedPathNavigate) this.myrmex.getNavigator();
        } else {
            return false;
        }
        if (LivingEntity instanceof PlayerEntity&& this.myrmex.getHive()!=null){
            if(!this.myrmex.getHive().isPlayerReputationLowEnoughToFight(LivingEntity.getUniqueID())){
                return false;
            }
        }
        if (LivingEntity == null) {
            return false;
        } else if (!LivingEntity.isAlive()) {
            return false;
        } else if (!myrmex.canMove()) {
            return false;
        } else {

            attackPath = ((AdvancedPathNavigate) this.myrmex.getNavigator()).moveToLivingEntity(LivingEntity, speedTowardsTarget);
            if (this.attackPath != null) {
                return true;
            } else {
                return this.getAttackReachSqr(LivingEntity) >= this.myrmex.getDistanceSq(LivingEntity.getPosX(), LivingEntity.getBoundingBox().minY, LivingEntity.getPosZ());
            }
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        LivingEntity LivingEntity = this.myrmex.getAttackTarget();
        if (this.myrmex.getLastAttackedEntity() != null && this.myrmex.getLastAttackedEntity().isAlive()){
            LivingEntity = this.myrmex.getLastAttackedEntity();
        }
        if (LivingEntity != null && !LivingEntity.isAlive() || !(this.myrmex.getNavigator() instanceof AdvancedPathNavigate)) {
            this.resetTask();
            return false;
        }

        return LivingEntity != null && (LivingEntity.isAlive() && (!(LivingEntity instanceof PlayerEntity) || !LivingEntity.isSpectator() && !((PlayerEntity) LivingEntity).isCreative()));
    }

    @Override
    public void startExecuting() {
        this.delayCounter = 0;
    }

    @Override
    public void resetTask() {
        LivingEntity LivingEntity = this.myrmex.getAttackTarget();
        if (LivingEntity instanceof PlayerEntity && (LivingEntity.isSpectator() || ((PlayerEntity) LivingEntity).isCreative())) {
            this.myrmex.setAttackTarget(null);
            this.myrmex.setLastAttackedEntity(null);
        }
    }

    @Override
    public void tick() {
        LivingEntity entity = this.myrmex.getAttackTarget();
        if (entity != null) {
            ((AdvancedPathNavigate) this.myrmex.getNavigator()).tryMoveToEntityLiving(entity, speedTowardsTarget);
            double d0 = this.myrmex.getDistanceSq(entity.getPosX(), entity.getBoundingBox().minY, entity.getPosZ());
            double d1 = this.getAttackReachSqr(entity);
            --this.delayCounter;
            if ((this.longMemory || this.myrmex.getEntitySenses().canSee(entity)) && this.delayCounter <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || entity.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.myrmex.getRNG().nextFloat() < 0.05F)) {
                this.targetX = entity.getPosX();
                this.targetY = entity.getBoundingBox().minY;
                this.targetZ = entity.getPosZ();
                this.delayCounter = 4 + this.myrmex.getRNG().nextInt(7);

                if (this.canPenalize) {
                    this.delayCounter += failedPathFindingPenalty;
                    if (this.myrmex.getNavigator().getPath() != null) {
                        net.minecraft.pathfinding.PathPoint finalPathPoint = this.myrmex.getNavigator().getPath().getFinalPathPoint();
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
                if (this.myrmex.canMove()) {
                    this.delayCounter += 15;
                }
            }

            this.attackTick = Math.max(this.attackTick - 1, 0);

            if (d0 <= d1 && this.attackTick <= 0) {
                this.attackTick = 20;
                this.myrmex.swingArm(Hand.MAIN_HAND);
                this.myrmex.attackEntityAsMob(entity);
            }
        }
    }

    protected double getAttackReachSqr(LivingEntity attackTarget) {
        return this.myrmex.getWidth() * 2.0F * this.myrmex.getWidth() * 2.0F + attackTarget.getWidth();
    }
}