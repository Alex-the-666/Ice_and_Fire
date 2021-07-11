package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntitySeaSerpent;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.Random;

public class SeaSerpentAIRandomSwimming  extends RandomWalkingGoal {
    public SeaSerpentAIRandomSwimming(CreatureEntity creature, double speed, int chance) {
        super(creature, speed, chance, false);
    }

    public boolean shouldExecute() {
        if (this.creature.isBeingRidden() || this.creature.getAttackTarget() != null) {
            return false;
        } else {
            if (!this.mustUpdate) {
                if (this.creature.getRNG().nextInt(this.executionChance) != 0) {
                    return false;
                }
            }
            Vector3d vector3d = this.getPosition();
            if (vector3d == null) {
                return false;
            } else {
                this.x = vector3d.x;
                this.y = vector3d.y;
                this.z = vector3d.z;
                this.mustUpdate = false;
                return true;
            }
        }
    }

    @Nullable
    protected Vector3d getPosition() {
        if(((EntitySeaSerpent)this.creature).jumpCooldown <= 0){
            Vector3d vector3d = findSurfaceTarget(this.creature, 32, 16);
            if(vector3d != null){
                return vector3d.add(0, 1, 0);
            }
        }else{
            BlockPos blockpos = null;
            Random random = new Random();
            int range = 16;
            for(int i = 0; i < 15; i++){
                BlockPos blockpos1 = this.creature.getPosition().add(random.nextInt(range) - range/2, random.nextInt(range) - range/2, random.nextInt(range) - range/2);
                while(this.creature.world.isAirBlock(blockpos1) && this.creature.world.getFluidState(blockpos1).isEmpty() && blockpos1.getY() > 1){
                    blockpos1 = blockpos1.down();
                }
                if(this.creature.world.getFluidState(blockpos1).isTagged(FluidTags.WATER)){
                    blockpos = blockpos1;
                }
            }
            return blockpos == null ? null : new Vector3d(blockpos.getX() + 0.5D, blockpos.getY() + 0.5D, blockpos.getZ() + 0.5D);
        }
        return null;
    }

    private boolean canJumpTo(BlockPos pos, int dx, int dz, int scale) {
        BlockPos blockpos = pos.add(dx * scale, 0, dz * scale);
        return this.creature.world.getFluidState(blockpos).isTagged(FluidTags.WATER) && !this.creature.world.getBlockState(blockpos).getMaterial().blocksMovement();
    }

    private boolean isAirAbove(BlockPos pos, int dx, int dz, int scale) {
        return this.creature.world.getBlockState(pos.add(dx * scale, 1, dz * scale)).isAir() && this.creature.world.getBlockState(pos.add(dx * scale, 2, dz * scale)).isAir();
    }

    private Vector3d findSurfaceTarget(CreatureEntity creature, int i, int i1) {
        BlockPos upPos = creature.getPosition();
        while(creature.world.getFluidState(upPos).isTagged(FluidTags.WATER)){
            upPos = upPos.up();
        }
        if(isAirAbove(upPos.down(), 0, 0, 0) && canJumpTo(upPos.down(), 0, 0, 0)){
            return new Vector3d(upPos.getX() + 0.5F, upPos.getY() + 3.5F, upPos.getZ() + 0.5F);
        }
        return null;
    }
}
