package com.github.alexthe666.iceandfire.pathfinding.raycoms.pathjobs;
/*
    All of this code is used with permission from Raycoms, one of the developers of the minecolonies project.
 */
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.Node;

import com.github.alexthe666.iceandfire.pathfinding.raycoms.Pathfinding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Job that handles moving to a location.
 */
public class PathJobMoveToLocation extends AbstractPathJob {
    private static final float DESTINATION_SLACK_NONE = 0.1F;
    // 1^2 + 1^2 + 1^2 + (epsilon of 0.1F)
    private static final float DESTINATION_SLACK_ADJACENT = (float) Math.sqrt(2f);

    private final BlockPos destination;
    // 0 = exact match
    private float destinationSlack = DESTINATION_SLACK_NONE;

    /**
     * Prepares the PathJob for the path finding system.
     *
     * @param world  world the entity is in.
     * @param start  starting location.
     * @param end    target location.
     * @param range  max search range.
     * @param entity the entity.
     */
    public PathJobMoveToLocation(final World world, final BlockPos start, final BlockPos end, final int range, final LivingEntity entity) {
        super(world, start, end, range, entity);

        this.destination = new BlockPos(end);
    }

    /**
     * Perform the search.
     *
     * @return Path of a path to the given location, a best-effort, or null.
     */
    @Nullable
    @Override
    protected Path search() {
        if (Pathfinding.isDebug()) {
            IceAndFire.LOGGER.info(String.format("Pathfinding from [%d,%d,%d] to [%d,%d,%d]",
                    start.getX(), start.getY(), start.getZ(), destination.getX(), destination.getY(), destination.getZ()));
        }

        //  Compute destination slack - if the destination point cannot be stood in
        if (getGroundHeight(null, destination) != destination.getY()) {
            destinationSlack = DESTINATION_SLACK_ADJACENT;
        }

        return super.search();
    }

    @Override
    protected BlockPos getPathTargetPos(final Node finalNode) {
        return destination;
    }

    @Override
    protected double computeHeuristic(final BlockPos pos) {
        return Math.sqrt(destination.distanceSq(pos));
    }

    /**
     * Checks if the target has been reached.
     *
     * @param n Node to test.
     * @return true if has been reached.
     */
    @Override
    protected boolean isAtDestination(final Node n) {
        if (destinationSlack <= DESTINATION_SLACK_NONE) {
            return n.pos.getX() == destination.getX()
                    && n.pos.getY() == destination.getY()
                    && n.pos.getZ() == destination.getZ();
        }

        if (n.pos.getY() == destination.getY() - 1) {
            return destination.withinDistance(new Vector3i(n.pos.getX(), destination.getY(), n.pos.getZ()), DESTINATION_SLACK_ADJACENT);
        }
        return destination.withinDistance(n.pos, DESTINATION_SLACK_ADJACENT);
    }

    /**
     * Calculate the distance to the target.
     *
     * @param n Node to test.
     * @return double of the distance.
     */
    @Override
    protected double getNodeResultScore(final Node n) {
        //  For Result Score lower is better
        return destination.distanceSq(n.pos);
    }
}
