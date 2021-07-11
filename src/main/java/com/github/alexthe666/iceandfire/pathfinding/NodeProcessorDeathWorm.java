package com.github.alexthe666.iceandfire.pathfinding;

import javax.annotation.Nullable;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.FlaggedPathPoint;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;

public class NodeProcessorDeathWorm extends NodeProcessor {
    public PathPoint getStart() {
        return this.openPoint(MathHelper.floor(this.entity.getBoundingBox().minX), MathHelper.floor(this.entity.getBoundingBox().minY + 0.5D), MathHelper.floor(this.entity.getBoundingBox().minZ));
    }

    @Override
    public FlaggedPathPoint func_224768_a(double x, double y, double z) {
        return new FlaggedPathPoint(this.openPoint(MathHelper.floor(x - 0.4), MathHelper.floor(y + 0.5D), MathHelper.floor(z - 0.4)));
    }

    @Override
    public int func_222859_a(PathPoint[] p_222859_1_, PathPoint p_222859_2_) {
        return 0;
    }

    @Override
    public PathNodeType determineNodeType(IBlockReader blockaccessIn, int x, int y, int z, MobEntity entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
        return null;
    }

    @Override
    public PathNodeType getFloorNodeType(IBlockReader blockaccessIn, int x, int y, int z) {
        return null;
    }

    public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
        int i = 0;

        for (Direction Direction : Direction.values()) {
            PathPoint pathpoint = this.getWaterNode(currentPoint.x + Direction.getXOffset(), currentPoint.y + Direction.getYOffset(), currentPoint.z + Direction.getZOffset());

            if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint;
            }
        }

        return i;
    }


    @Nullable
    private PathPoint getWaterNode(int p_186328_1_, int p_186328_2_, int p_186328_3_) {
        PathNodeType pathnodetype = this.isFree(p_186328_1_, p_186328_2_, p_186328_3_);
        return pathnodetype == PathNodeType.OPEN ? this.openPoint(p_186328_1_, p_186328_2_, p_186328_3_) : null;
    }

    private PathNodeType isFree(int p_186327_1_, int p_186327_2_, int p_186327_3_) {
        return PathNodeType.OPEN;
    }
}