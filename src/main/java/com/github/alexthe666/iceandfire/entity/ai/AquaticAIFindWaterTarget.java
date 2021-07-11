package com.github.alexthe666.iceandfire.entity.ai;

import java.util.Comparator;
import java.util.EnumSet;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class AquaticAIFindWaterTarget extends Goal {
    protected AquaticAIFindWaterTarget.Sorter fleePosSorter;
    private MobEntity mob;
    private int range;
    private boolean avoidAttacker;

    public AquaticAIFindWaterTarget(MobEntity mob, int range, boolean avoidAttacker) {
        this.mob = mob;
        this.range = range;
        this.avoidAttacker = avoidAttacker;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
        fleePosSorter = new Sorter(mob);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.mob.isInWater() || this.mob.isPassenger() || this.mob.isBeingRidden()) {
            return false;
        }
        Path path = this.mob.getNavigator().getPath();
        if (this.mob.getRNG().nextFloat() < 0.15F || path != null && path.getFinalPathPoint() != null && this.mob.getDistanceSq(path.getFinalPathPoint().x, path.getFinalPathPoint().y, path.getFinalPathPoint().z) < 3) {
            if (path != null && path.getFinalPathPoint() != null || !this.mob.getNavigator().noPath() && !isDirectPathBetweenPoints(this.mob, this.mob.getPositionVec(), new Vector3d(path.getFinalPathPoint().x, path.getFinalPathPoint().y, path.getFinalPathPoint().z))) {
                this.mob.getNavigator().clearPath();
            }
            if (this.mob.getNavigator().noPath()) {
                BlockPos vec3 = this.findWaterTarget();
                if (vec3 != null) {
                    this.mob.getNavigator().tryMoveToXYZ(vec3.getX(), vec3.getY(), vec3.getZ(), 1.0);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return false;
    }

    public BlockPos findWaterTarget() {
        BlockPos blockpos = new BlockPos(this.mob.getPosX(), this.mob.getBoundingBox().minY, mob.getPosZ());
        if (this.mob.getAttackTarget() == null || !this.mob.getAttackTarget().isAlive()) {
            for (int i = 0; i < 10; ++i) {
                BlockPos blockpos1 = blockpos.add(mob.getRNG().nextInt(20) - 10, mob.getRNG().nextInt(6) - 3, mob.getRNG().nextInt(20) - 10);
                if (mob.world.getBlockState(blockpos1).getMaterial() == Material.WATER) {
                    return blockpos1;
                }
            }
        } else {
            return this.mob.getAttackTarget().getPosition();
        }
        return null;
    }

    public boolean isDirectPathBetweenPoints(Entity entity, Vector3d vec1, Vector3d vec2) {
        return mob.world.rayTraceBlocks(new RayTraceContext(vec1, vec2, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity)).getType() == RayTraceResult.Type.MISS;

    }

    public class Sorter implements Comparator<BlockPos> {
        private BlockPos pos;

        public Sorter(Entity theEntityIn) {
            this.pos = theEntityIn.getPosition();
        }

        //further; more prefered.
        public int compare(BlockPos p_compare_1_, BlockPos p_compare_2_) {
            this.pos = AquaticAIFindWaterTarget.this.mob.getPosition();
            double d0 = this.pos.distanceSq(p_compare_1_);
            double d1 = this.pos.distanceSq(p_compare_2_);
            return d0 < d1 ? 1 : (d0 > d1 ? -1 : 0);
        }
    }
}