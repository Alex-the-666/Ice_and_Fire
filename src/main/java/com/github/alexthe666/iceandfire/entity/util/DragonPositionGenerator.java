package com.github.alexthe666.iceandfire.entity.util;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class DragonPositionGenerator {

    public static Vector3d findRandomTargetBlock(MobEntity MobEntityIn, int xz, int y, @Nullable Vector3d targetVec3) {
        Vector3d vec = generateRandomPos(MobEntityIn, xz, y, targetVec3, false);
        return vec == null ? MobEntityIn.getPositionVec() : vec;
    }

    @Nullable
    public static Vector3d generateRandomPos(MobEntity mob, int xz, int y, @Nullable Vector3d vec, boolean skipWater) {
        PathNavigator pathnavigate = mob.getNavigator();
        Random random = mob.getRNG();
        boolean flag;

        if (mob.detachHome()) {
            double d0 = mob.getHomePosition().distanceSq(MathHelper.floor(mob.getPosX()), MathHelper.floor(mob.getPosY()), MathHelper.floor(mob.getPosZ()), true) + 4.0D;
            double d1 = mob.getMaximumHomeDistance() + (float) xz;
            flag = d0 < d1 * d1;
        } else {
            flag = false;
        }

        boolean flag1 = false;
        float f = -99999.0F;
        int k1 = 0;
        int i = 0;
        int j = 0;

        for (int k = 0; k < 10; ++k) {
            int l = random.nextInt(2 * xz + 1) - xz;
            int i1 = random.nextInt(2 * y + 1) - y;
            int j1 = random.nextInt(2 * xz + 1) - xz;

            if (vec == null || (double) l * vec.x + (double) j1 * vec.z >= 0.0D) {
                if (mob.detachHome() && xz > 1) {
                    BlockPos blockpos = mob.getHomePosition();

                    if (mob.getPosX() > (double) blockpos.getX()) {
                        l -= random.nextInt(xz / 2);
                    } else {
                        l += random.nextInt(xz / 2);
                    }

                    if (mob.getPosZ() > (double) blockpos.getZ()) {
                        j1 -= random.nextInt(xz / 2);
                    } else {
                        j1 += random.nextInt(xz / 2);
                    }
                }

                BlockPos blockpos1 = new BlockPos((double) l + mob.getPosX(), (double) i1 + mob.getPosY(), (double) j1 + mob.getPosZ());

                if ((!flag || mob.isWithinHomeDistanceFromPosition(blockpos1)) && pathnavigate.canEntityStandOnPos(blockpos1)) {
                    if (skipWater) {
                        blockpos1 = moveAboveSolid(blockpos1, mob);
                        if (isWaterDestination(blockpos1, mob)) {
                            continue;
                        }
                    }

                    float f1 = 0.0F;

                    if (f1 > f) {
                        f = f1;
                        k1 = l;
                        i = i1;
                        j = j1;
                        flag1 = true;
                    }
                }
            }
        }

        if (flag1) {
            return new Vector3d((double) k1 + mob.getPosX(), (double) i + mob.getPosY(), (double) j + mob.getPosZ());
        } else {
            return null;
        }
    }

    private static BlockPos moveAboveSolid(BlockPos pos, MobEntity mob) {
        if (!mob.world.getBlockState(pos).getMaterial().isSolid()) {
            return pos;
        } else {
            BlockPos blockpos;

            for (blockpos = pos.up(); blockpos.getY() < mob.world.getHeight() && mob.world.getBlockState(blockpos).getMaterial().isSolid(); blockpos = blockpos.up()) {
            }

            return blockpos;
        }
    }

    private static boolean isWaterDestination(BlockPos pos, MobEntity mob) {
        return mob.world.getBlockState(pos).getMaterial() == Material.WATER;
    }
}
