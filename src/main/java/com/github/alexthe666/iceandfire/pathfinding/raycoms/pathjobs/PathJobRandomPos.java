package com.github.alexthe666.iceandfire.pathfinding.raycoms.pathjobs;
/*
    All of this code is used with permission from Raycoms, one of the developers of the minecolonies project.
 */
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.Node;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.RandomPathResult;

import net.minecraft.entity.LivingEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

import com.github.alexthe666.iceandfire.pathfinding.raycoms.pathjobs.AbstractPathJob.SurfaceType;

/**
 * Job that handles random pathing.
 */
public class PathJobRandomPos extends AbstractPathJob
{
    /**
     * Direction to walk to.
     */

    protected final BlockPos destination;

    /**
     * Required avoidDistance.
     */
    protected final int distance;

    /**
     * Random pathing rand.
     */
    private static Random random = new Random();

    /**
     * Prepares the PathJob for the path finding system.
     *
     * @param world         world the entity is in.
     * @param start         starting location.
     * @param distance how far to move away.
     * @param range         max range to search.
     * @param entity        the entity.
     */
    public PathJobRandomPos(
      final World world,
      final BlockPos start,
      final int distance,
      final int range,
      final LivingEntity entity)
    {
        super(world, start, start, range, new RandomPathResult(), entity);
        this.distance = distance;

        final Tuple<Direction, Direction> dir = getRandomDirectionTuple(random);
        this.destination = start.offset(dir.getA(), distance).offset(dir.getB(), distance);
    }

    private Tuple<Direction, Direction> getRandomDirectionTuple(Random random) {
        return new Tuple<Direction, Direction>(Direction.getRandomDirection(random), Direction.getRandomDirection(random));
    }

    @Nullable
    @Override
    protected Path search()
    {
        if (true)
        {
            IceAndFire.LOGGER.info(String.format("Pathfinding from [%d,%d,%d] in the direction of [%d,%d,%d]",
              start.getX(), start.getY(), start.getZ(), destination.getX(), destination.getY(), destination.getZ()));
        }

        return super.search();
    }


    @Override
    public RandomPathResult getResult()
    {
        return (RandomPathResult) super.getResult();
    }

    @Override
    protected double computeHeuristic(final BlockPos pos)
    {
        return Math.sqrt(destination.distanceSq(new BlockPos(pos.getX(), destination.getY(), pos.getZ())));
    }

    @Override
    protected boolean isAtDestination(final Node n)
    {
        if (Math.sqrt(start.distanceSq(n.pos)) > distance && isWalkableSurface(world.getBlockState(n.pos.down()), n.pos.down()) == SurfaceType.WALKABLE) //&& isWalkableSurface(world.getBlockState(n.pos.down()), n.pos.down()) == SurfaceType.WALKABLE)
        {
            getResult().randomPos = n.pos;
            return true;
        }
        return false;
    }

    @Override
    protected double getNodeResultScore(final Node n)
    {
        //  For Result Score lower is better
        return destination.distanceSq(n.pos);
    }
}
