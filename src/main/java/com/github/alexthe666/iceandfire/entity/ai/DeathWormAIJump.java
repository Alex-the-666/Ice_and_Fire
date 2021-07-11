package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.JumpGoal;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class DeathWormAIJump extends JumpGoal {
    private static final int[] JUMP_DISTANCES = new int[]{0, 1, 4, 5, 6, 7};
    private final EntityDeathWorm dolphin;
    private final int field_220712_c;
    private boolean inWater;
    private int jumpCooldown;

    public DeathWormAIJump(EntityDeathWorm dolphin, int p_i50329_2_) {
        this.dolphin = dolphin;
        this.field_220712_c = p_i50329_2_;
    }

    public boolean shouldExecute() {
        if(jumpCooldown > 0){
            jumpCooldown--;
        }
        if (this.dolphin.getRNG().nextInt(this.field_220712_c) != 0 || dolphin.getAttackTarget() != null) {
            return false;
        } else {
            Direction direction = this.dolphin.getAdjustedHorizontalFacing();
            int i = direction.getXOffset();
            int j = direction.getZOffset();
            BlockPos blockpos = this.dolphin.getPosition();
            for (int k : JUMP_DISTANCES) {
                if (!this.canJumpTo(blockpos, i, j, k) || !this.isAirAbove(blockpos, i, j, k)) {
                    return false;
                }
            }
            return true;
        }
    }

    private boolean canJumpTo(BlockPos pos, int dx, int dz, int scale) {
        BlockPos blockpos = pos.add(dx * scale, 0, dz * scale);
        return this.dolphin.world.getBlockState(blockpos).isIn(BlockTags.SAND);
    }

    private boolean isAirAbove(BlockPos pos, int dx, int dz, int scale) {
        return this.dolphin.world.getBlockState(pos.add(dx * scale, 1, dz * scale)).isAir() && this.dolphin.world.getBlockState(pos.add(dx * scale, 2, dz * scale)).isAir();
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting() {
        double d0 = this.dolphin.getMotion().y;
        return jumpCooldown > 0 && (!(d0 * d0 < (double) 0.03F) || this.dolphin.rotationPitch == 0.0F || !(Math.abs(this.dolphin.rotationPitch) < 10.0F) || !this.dolphin.isInSand()) && !this.dolphin.isOnGround();
    }

    public boolean isPreemptible() {
        return false;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        Direction direction = this.dolphin.getAdjustedHorizontalFacing();
        float up = (dolphin.getRenderScale() > 3 ? 0.7F : 0.4F) + dolphin.getRNG().nextFloat() * 0.4F;
        this.dolphin.setMotion(this.dolphin.getMotion().add((double) direction.getXOffset() * 0.6D, up, (double) direction.getZOffset() * 0.6D));
        this.dolphin.getNavigator().clearPath();
        this.dolphin.setWormJumping(30);
        this.jumpCooldown = dolphin.getRNG().nextInt(65) + 32;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {
        this.dolphin.rotationPitch = 0.0F;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        boolean flag = this.inWater;
        if (!flag) {
            this.inWater = this.dolphin.world.getBlockState(this.dolphin.getPosition()).isIn(BlockTags.SAND);
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
