package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;
import java.util.Random;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.EntitySiren;

import net.minecraft.block.material.Material;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class AquaticAIGetInWater extends Goal {
    private final MobEntity creature;
    private final double movementSpeed;
    private final World world;
    private double shelterX;
    private double shelterY;
    private double shelterZ;

    public AquaticAIGetInWater(MobEntity theCreatureIn, double movementSpeedIn) {
        this.creature = theCreatureIn;
        this.movementSpeed = movementSpeedIn;
        this.world = theCreatureIn.world;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    protected boolean isAttackerInWater() {
        return creature.getAttackTarget() != null && !creature.getAttackTarget().isInWater();
    }

    public boolean shouldExecute() {
        if (creature.isBeingRidden() || creature instanceof TameableEntity && ((TameableEntity) creature).isTamed() || creature.isInWater() || isAttackerInWater() || creature instanceof EntitySiren && (((EntitySiren) creature).isSinging() || ((EntitySiren) creature).wantsToSing())) {
            return false;
        } else {
            Vector3d Vector3d = this.findPossibleShelter();

            if (Vector3d == null) {
                return false;
            } else {
                this.shelterX = Vector3d.x;
                this.shelterY = Vector3d.y;
                this.shelterZ = Vector3d.z;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress Goal should continue executing
     */
    public boolean shouldContinueExecuting() {
        return !this.creature.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        this.creature.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
    }

    @Nullable
    public Vector3d findPossibleShelter() {
        return findPossibleShelter(10, 3);
    }

    @Nullable
    protected Vector3d findPossibleShelter(int xz, int y) {
        Random random = this.creature.getRNG();
        BlockPos blockpos = new BlockPos(this.creature.getPosX(), this.creature.getBoundingBox().minY, this.creature.getPosZ());

        for (int i = 0; i < 10; ++i) {
            BlockPos blockpos1 = blockpos.add(random.nextInt(xz * 2) - xz, random.nextInt(y * 2) - y, random.nextInt(xz * 2) - xz);

            if (this.world.getBlockState(blockpos1).getMaterial() == Material.WATER) {
                return new Vector3d(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
            }
        }

        return null;
    }
}