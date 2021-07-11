package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityCockatrice;
import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class CockatriceAIStareAttack extends Goal {
    private final EntityCockatrice entity;
    private final double moveSpeedAmp;
    private final float maxAttackDistance;
    private int attackCooldown;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;
    private BlockPos target = null;
    private int walkingTime = -1;
    private float prevYaw;

    public CockatriceAIStareAttack(EntityCockatrice cockatrice, double speedAmplifier, int delay, float maxDistance) {
        this.entity = cockatrice;
        this.moveSpeedAmp = speedAmplifier;
        this.attackCooldown = delay;
        this.maxAttackDistance = maxDistance * maxDistance;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public static boolean isEntityLookingAt(LivingEntity looker, LivingEntity seen, double degree) {
        Vector3d Vector3d = looker.getLook(1.0F).normalize();
        Vector3d Vector3d1 = new Vector3d(seen.getPosX() - looker.getPosX(), seen.getBoundingBox().minY + (double) seen.getEyeHeight() - (looker.getPosY() + (double) looker.getEyeHeight()), seen.getPosZ() - looker.getPosZ());
        double d0 = Vector3d1.length();
        Vector3d1 = Vector3d1.normalize();
        double d1 = Vector3d.dotProduct(Vector3d1);
        return d1 > 1.0D - degree / d0 && !looker.isSpectator();
    }

    public void setAttackCooldown(int cooldown) {
        this.attackCooldown = cooldown;
    }

    public boolean shouldExecute() {
        return this.entity.getAttackTarget() != null;
    }

    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }

    public void resetTask() {
        super.resetTask();
        this.seeTime = 0;
        this.attackTime = -1;
        this.entity.resetActiveHand();
        this.entity.getNavigator().clearPath();
        target = null;
    }

    public void tick() {
        LivingEntity LivingEntity = this.entity.getAttackTarget();
        if (LivingEntity != null) {

            if (EntityGorgon.isStoneMob(LivingEntity) || !LivingEntity.isAlive()) {
                entity.setAttackTarget(null);
                this.entity.setTargetedEntity(0);
                resetTask();
                return;
            }
            if (!isEntityLookingAt(LivingEntity, entity, EntityCockatrice.VIEW_RADIUS) || (LivingEntity.prevPosX != entity.getPosX() || LivingEntity.prevPosY != entity.getPosY() || LivingEntity.prevPosZ != entity.getPosZ())) {
                this.entity.getNavigator().clearPath();
                this.prevYaw = LivingEntity.rotationYaw;
                BlockPos pos = DragonUtils.getBlockInTargetsViewCockatrice(this.entity, LivingEntity);
                if (target == null || pos.distanceSq(target) > 4) {
                    target = pos;
                }
            }
            this.entity.setTargetedEntity(LivingEntity.getEntityId());

            double d0 = this.entity.getDistanceSq(LivingEntity.getPosX(), LivingEntity.getBoundingBox().minY, LivingEntity.getPosZ());
            boolean flag = this.entity.getEntitySenses().canSee(LivingEntity);
            boolean flag1 = this.seeTime > 0;

            if (flag != flag1) {
                this.seeTime = 0;
            }

            if (flag) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }
            if (target != null) {
                if (this.entity.getDistanceSq(target.getX(), target.getY(), target.getZ()) > 16 && !isEntityLookingAt(LivingEntity, entity, EntityCockatrice.VIEW_RADIUS)) {
                    this.entity.getNavigator().tryMoveToXYZ(target.getX(), target.getY(), target.getZ(), moveSpeedAmp);
                }

            }
            this.entity.getLookController().setLookPosition(LivingEntity.getPosX(), LivingEntity.getPosY() + (double) LivingEntity.getEyeHeight(), LivingEntity.getPosZ(), (float) this.entity.getHorizontalFaceSpeed(), (float) this.entity.getVerticalFaceSpeed());
        }
    }

}