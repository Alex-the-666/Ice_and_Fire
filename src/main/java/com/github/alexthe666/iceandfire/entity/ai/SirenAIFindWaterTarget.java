package com.github.alexthe666.iceandfire.entity.ai;

import java.util.ArrayList;
import java.util.List;

import com.github.alexthe666.iceandfire.entity.EntitySiren;

import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class SirenAIFindWaterTarget extends Goal {
    private EntitySiren mob;

    public SirenAIFindWaterTarget(EntitySiren mob) {
        this.mob = mob;
    }

    @Override
    public boolean shouldExecute() {
        if (!this.mob.isInWater()) {
            return false;
        }
        if (this.mob.getRNG().nextFloat() < 0.5F) {
            Path path = this.mob.getNavigator().getPath();
            if (path != null && path.getFinalPathPoint() != null || !this.mob.getNavigator().noPath() && !this.mob.isDirectPathBetweenPoints(this.mob.getPositionVec(), new Vector3d(path.getFinalPathPoint().x, path.getFinalPathPoint().y, path.getFinalPathPoint().z))) {
                this.mob.getNavigator().clearPath();
            }
            if (this.mob.getNavigator().noPath()) {
                Vector3d vec3 = this.findWaterTarget();
                if (vec3 != null) {
                    this.mob.getNavigator().tryMoveToXYZ(vec3.x, vec3.y, vec3.z, 1.0);
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

    public Vector3d findWaterTarget() {
        if (this.mob.getAttackTarget() == null || !this.mob.getAttackTarget().isAlive()) {
            List<Vector3d> water = new ArrayList<>();
            List<Vector3d> singTargets = new ArrayList<>();
            for (int x = (int) this.mob.getPosX() - 5; x < (int) this.mob.getPosX() + 5; x++) {
                for (int y = (int) this.mob.getPosY() - 5; y < (int) this.mob.getPosY() + 5; y++) {
                    for (int z = (int) this.mob.getPosZ() - 5; z < (int) this.mob.getPosZ() + 5; z++) {
                        if (mob.wantsToSing()) {
                            if (this.mob.world.getBlockState(new BlockPos(x, y, z)).getMaterial().isSolid() && this.mob.world.isAirBlock(new BlockPos(x, y + 1, z)) && this.mob.isDirectPathBetweenPoints(this.mob.getPositionVec(), new Vector3d(x, y + 1, z))) {
                                singTargets.add(new Vector3d(x, y + 1, z));
                            }
                        }
                        if (this.mob.world.getBlockState(new BlockPos(x, y, z)).getMaterial() == Material.WATER && this.mob.isDirectPathBetweenPoints(this.mob.getPositionVec(), new Vector3d(x, y, z))) {
                            water.add(new Vector3d(x, y, z));
                        }

                    }
                }
            }
            if (!singTargets.isEmpty()) {
                return singTargets.get(this.mob.getRNG().nextInt(singTargets.size()));

            }
            if (!water.isEmpty()) {
                return water.get(this.mob.getRNG().nextInt(water.size()));
            }
        } else {
            BlockPos blockpos1 = this.mob.getAttackTarget().getPosition();
            return new Vector3d(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
        }
        return null;
    }
}