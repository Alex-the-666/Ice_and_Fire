package com.github.alexthe666.iceandfire.entity.ai;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class DeathWormAIFindSandTarget extends Goal {
    private EntityDeathWorm mob;
    private int range;
    private boolean avoidAttacker;

    public DeathWormAIFindSandTarget(EntityDeathWorm mob, int range) {
        this.mob = mob;
        this.range = range;
    }

    @Override
    public boolean shouldExecute() {
        if (!this.mob.isInSand() || this.mob.isPassenger() || this.mob.isBeingRidden()) {
            return false;
        }
        if (this.mob.getRNG().nextFloat() < 0.5F) {
            Path path = this.mob.getNavigator().getPath();
            if (path != null || !this.mob.getNavigator().noPath() && !isDirectPathBetweenPoints(this.mob, this.mob.getPositionVec(), new Vector3d(path.getFinalPathPoint().x, path.getFinalPathPoint().y, path.getFinalPathPoint().z))) {
                this.mob.getNavigator().clearPath();
            }
            if (this.mob.getNavigator().noPath() && !this.mob.getMoveHelper().isUpdating()) {
                BlockPos vec3 = this.findSandTarget();
                if (vec3 != null) {
                    this.mob.getMoveHelper().setMoveTo(vec3.getX(), vec3.getY(), vec3.getZ(), 1.0);
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

    public BlockPos findSandTarget() {
        if (this.mob.getAttackTarget() == null || !this.mob.getAttackTarget().isAlive()) {
            List<BlockPos> sand = new ArrayList<>();
            if (this.mob.isTamed() && this.mob.getWormHome() != null) {
                range = 25;
                for (int x = this.mob.getWormHome().getX() - range; x < this.mob.getWormHome().getX() + range; x++) {
                    for (int y = this.mob.getWormHome().getY() - range; y < this.mob.getWormHome().getY() + range; y++) {
                        for (int z = this.mob.getWormHome().getZ() - range; z < this.mob.getWormHome().getZ() + range; z++) {
                            if (this.mob.world.getBlockState(new BlockPos(x, y, z)).getMaterial() == Material.SAND && isDirectPathBetweenPoints(this.mob, this.mob.getPositionVec(), new Vector3d(x, y, z))) {
                                sand.add(new BlockPos(x, y, z));
                            }
                        }
                    }
                }
            } else {
                for (int x = (int) this.mob.getPosX() - range; x < (int) this.mob.getPosX() + range; x++) {
                    for (int y = (int) this.mob.getPosY() - range; y < (int) this.mob.getPosY() + range; y++) {
                        for (int z = (int) this.mob.getPosZ() - range; z < (int) this.mob.getPosZ() + range; z++) {
                            if (this.mob.world.getBlockState(new BlockPos(x, y, z)).getMaterial() == Material.SAND && isDirectPathBetweenPoints(this.mob, this.mob.getPositionVec(), new Vector3d(x, y, z))) {
                                sand.add(new BlockPos(x, y, z));
                            }

                        }
                    }
                }
            }

            if (!sand.isEmpty()) {
                return sand.get(this.mob.getRNG().nextInt(sand.size()));
            }
        } else {
            BlockPos blockpos1 = this.mob.getAttackTarget().getPosition();
            return new BlockPos(blockpos1.getX(), (double) blockpos1.getY() - 1, blockpos1.getZ());
        }
        return null;
    }

    public boolean isDirectPathBetweenPoints(Entity entity, Vector3d vec1, Vector3d vec2) {
        return true;
    }
}