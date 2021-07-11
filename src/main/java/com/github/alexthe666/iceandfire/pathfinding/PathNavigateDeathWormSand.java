package com.github.alexthe666.iceandfire.pathfinding;

import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;

import net.minecraft.block.material.Material;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class PathNavigateDeathWormSand extends PathNavigator {
    private EntityDeathWorm worm;

    public PathNavigateDeathWormSand(EntityDeathWorm deathworm, World worldIn) {
        super(deathworm, worldIn);
        worm = deathworm;
    }

    public boolean getCanSwim() {
        return this.nodeProcessor.getCanSwim();
    }

    protected PathFinder getPathFinder(int i) {
        this.nodeProcessor = new NodeProcessorDeathWorm();
        this.nodeProcessor.setCanEnterDoors(true);
        this.nodeProcessor.setCanSwim(true);
        return new PathFinder(this.nodeProcessor, i);
    }

    /**
     * If on ground or swimming and can swim
     */
    protected boolean canNavigate() {
        return worm.isInSand();
    }

    protected Vector3d getEntityPosition() {
        return new Vector3d(this.entity.getPosX(), this.entity.getPosY() + 0.5D, this.entity.getPosZ());
    }

    protected void pathFollow() {
        Vector3d Vector3d = this.getEntityPosition();
        float f = 0.65F;
        int i = 6;

        if (Vector3d.squareDistanceTo(this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex())) < (double) f) {
            this.currentPath.incrementPathIndex();
        }

        for (int j = Math.min(this.currentPath.getCurrentPathIndex() + 6, this.currentPath.getCurrentPathLength() - 1); j > this.currentPath.getCurrentPathIndex(); --j) {
            Vector3d Vector3d1 = this.currentPath.getVectorFromIndex(this.entity, j);

            if (Vector3d1.squareDistanceTo(Vector3d) <= 36.0D && this.isDirectPathBetweenPoints(Vector3d, Vector3d1, 0, 0, 0)) {
                this.currentPath.setCurrentPathIndex(j);
                break;
            }
        }

        this.checkForStuck(Vector3d);
    }

    /**
     * Checks if the specified entity can safely walk to the specified location.
     */
    protected boolean isDirectPathBetweenPoints(Vector3d posVec31, Vector3d posVec32, int sizeX, int sizeY, int sizeZ) {
        RayTraceResult raytraceresult = this.world.rayTraceBlocks(new RayTraceContext(posVec31, posVec32, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));
        if (raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
            return entity.world.getBlockState(new BlockPos(raytraceresult.getHitVec())).getMaterial() == Material.SAND;
        }
        return false;
    }

    public boolean canEntityStandOnPos(BlockPos pos) {
        return this.world.getBlockState(pos).isSolid();
    }
}