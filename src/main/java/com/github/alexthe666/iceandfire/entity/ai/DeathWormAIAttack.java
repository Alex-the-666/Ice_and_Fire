package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class DeathWormAIAttack extends Goal {
    private EntityDeathWorm worm;
    private LivingEntity target;
    private Vector3d leapPos = null;
    private int jumpCooldown = 0;

    public DeathWormAIAttack(EntityDeathWorm worm) {
        this.worm = worm;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean shouldExecute() {
        if (jumpCooldown > 0) {
            jumpCooldown--;
        }
        if (this.worm.getAttackTarget() == null || !worm.isOnGround() && !worm.isInSandStrict() || jumpCooldown > 0) {
            return false;
        } else {
            BlockPos blockpos = this.worm.getPosition();
            return true;
        }
    }

    public boolean shouldContinueExecuting() {
        double d0 = this.worm.getMotion().y;
        return worm.getAttackTarget() != null && jumpCooldown > 0 && (!(d0 * d0 < (double) 0.03F) || this.worm.rotationPitch == 0.0F || !(Math.abs(this.worm.rotationPitch) < 10.0F) || !this.worm.isInWater()) && !this.worm.isOnGround();
    }

    public boolean isPreemptible() {
        return false;
    }

    public void startExecuting() {
        LivingEntity target = this.worm.getAttackTarget();
        if (target != null) {
            if(worm.isInSand()){
                BlockPos topSand = worm.getPosition();
                while(worm.world.getBlockState(topSand.up()).isIn(BlockTags.SAND)){
                    topSand = topSand.up();
                }
                worm.setPosition(worm.getPosX(), topSand.getY() + 0.5F, worm.getPosZ());
            }
            double distanceXZ = worm.getDistanceSq(target.getPosX(), worm.getPosY(), target.getPosZ());
            if (Math.sqrt(distanceXZ) < 12 && Math.sqrt(distanceXZ) > 2) {
                worm.faceEntity(target, 260, 30);
                double smoothX = MathHelper.clamp(Math.abs(target.getPosX() - worm.getPosX()), 0, 1);
                double smoothY = MathHelper.clamp(Math.abs(target.getPosY() - worm.getPosY()), 0, 1);
                double smoothZ = MathHelper.clamp(Math.abs(target.getPosZ() - worm.getPosZ()), 0, 1);
                double d0 = (target.getPosX() - this.worm.getPosX()) * 0.2 * smoothX;
                double d1 = Math.signum(target.getPosY() - this.worm.getPosY());
                double d2 = (target.getPosZ() - this.worm.getPosZ()) * 0.2 * smoothZ;
                float up = (worm.getRenderScale() > 3 ? 0.8F : 0.5F) + worm.getRNG().nextFloat() * 0.5F;
                this.worm.setMotion(this.worm.getMotion().add(d0 * 0.3D, up, d2 * 0.3D));
                this.worm.getNavigator().clearPath();
                this.worm.setWormJumping(20);
                this.jumpCooldown = worm.getRNG().nextInt(32) + 64;
            } else if(distanceXZ > 16F){
                worm.getNavigator().tryMoveToEntityLiving(target, 1.0F);
            }

        }
    }

    public void resetTask() {
        this.worm.rotationPitch = 0.0F;
    }

    public void tick() {
        if (jumpCooldown > 0) {
            jumpCooldown--;
        }
        LivingEntity target = this.worm.getAttackTarget();
        if (target != null && this.worm.canEntityBeSeen(target)) {
            if (this.worm.getDistance(target) < 3F) {
                this.worm.attackEntityAsMob(target);
            }
        }

        Vector3d vector3d = this.worm.getMotion();
        if (vector3d.y * vector3d.y < (double) 0.1F && this.worm.rotationPitch != 0.0F) {
            this.worm.rotationPitch = MathHelper.rotLerp(this.worm.rotationPitch, 0.0F, 0.2F);
        } else {
            double d0 = Math.sqrt(Entity.horizontalMag(vector3d));
            double d1 = Math.signum(-vector3d.y) * Math.acos(d0 / vector3d.length()) * (double) (180F / (float) Math.PI);
            this.worm.rotationPitch = (float) d1;
        }

    }
}
