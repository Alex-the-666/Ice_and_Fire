package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntitySeaSerpent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.JumpGoal;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class SeaSerpentAIMeleeJump  extends JumpGoal {
    private final EntitySeaSerpent dolphin;
    private int attackCooldown = 0;
    private boolean inWater;

    public SeaSerpentAIMeleeJump(EntitySeaSerpent dolphin) {
        this.dolphin = dolphin;
    }

    public boolean shouldExecute() {
        if (this.dolphin.getAttackTarget() == null || !dolphin.shouldUseJumpAttack(this.dolphin.getAttackTarget()) || this.dolphin.isOnGround() ) {
            return false;
        } else {
            BlockPos blockpos = this.dolphin.getPosition();
            return true;
        }
    }
    public boolean shouldContinueExecuting() {
        double d0 = this.dolphin.getMotion().y;
        return dolphin.getAttackTarget() != null && dolphin.jumpCooldown > 0 && (!(d0 * d0 < (double) 0.03F) || this.dolphin.rotationPitch == 0.0F || !(Math.abs(this.dolphin.rotationPitch) < 10.0F) || !this.dolphin.isInWater()) && !this.dolphin.isOnGround();
    }

    public boolean isPreemptible() {
        return false;
    }

    public void startExecuting() {
        LivingEntity target = this.dolphin.getAttackTarget();
        if(target != null){
            double distanceXZ = dolphin.getDistanceSq(target.getPosX(), dolphin.getPosY(), target.getPosZ());
            if(distanceXZ < 300){
                dolphin.faceEntity(target, 260, 30);
                double smoothX = MathHelper.clamp(Math.abs(target.getPosX() - dolphin.getPosX()), 0, 1);
                double smoothY = MathHelper.clamp(Math.abs(target.getPosY() - dolphin.getPosY()), 0, 1);
                double smoothZ = MathHelper.clamp(Math.abs(target.getPosZ() - dolphin.getPosZ()), 0, 1);
                double d0 = (target.getPosX() - this.dolphin.getPosX()) * 0.3 * smoothX;
                double d1 = Math.signum(target.getPosY() - this.dolphin.getPosY());
                double d2 = (target.getPosZ() - this.dolphin.getPosZ()) * 0.3 * smoothZ;
                float up = 1F + dolphin.getRNG().nextFloat() * 0.8F;
                this.dolphin.setMotion(this.dolphin.getMotion().add((double) d0 * 0.3D, up, (double) d2 * 0.3D));
                this.dolphin.getNavigator().clearPath();
                this.dolphin.jumpCooldown = dolphin.getRNG().nextInt(32) + 32;
            }else{
                dolphin.getNavigator().tryMoveToEntityLiving(target, 1.0F);
            }

        }
    }

    public void resetTask() {
        this.dolphin.rotationPitch = 0.0F;
        this.attackCooldown = 0;
    }

    public void tick() {
        boolean flag = this.inWater;
        if (!flag) {
            FluidState fluidstate = this.dolphin.world.getFluidState(this.dolphin.getPosition());
            this.inWater = fluidstate.isTagged(FluidTags.WATER);
        }
        if(attackCooldown > 0){
            attackCooldown--;
        }
        if (this.inWater && !flag) {
            this.dolphin.playSound(SoundEvents.ENTITY_DOLPHIN_JUMP, 1.0F, 1.0F);
        }
        LivingEntity target = this.dolphin.getAttackTarget();
        if(target != null){
            if(this.dolphin.getDistance(target) < 3F && attackCooldown <= 0){
                this.dolphin.onJumpHit(target);
                attackCooldown = 20;
            }else if(this.dolphin.getDistance(target) < 5F){
                this.dolphin.setAnimation(EntitySeaSerpent.ANIMATION_BITE);
            }
        }

        Vector3d vector3d = this.dolphin.getMotion();
        if (vector3d.y * vector3d.y < (double) 0.1F && this.dolphin.rotationPitch != 0.0F) {
            this.dolphin.rotationPitch = MathHelper.rotLerp(this.dolphin.rotationPitch, 0.0F, 0.2F);
        } else {
            double d0 = Math.sqrt(Entity.horizontalMag(vector3d));
            double d1 = Math.signum(-vector3d.y) * Math.acos(d0 / vector3d.length()) * (double) (180F / (float) Math.PI);
            this.dolphin.rotationPitch = (float) d1;
        }

    }
}
