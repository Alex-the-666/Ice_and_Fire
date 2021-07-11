package com.github.alexthe666.iceandfire.pathfinding.raycoms;
/*
    All of this code is used with permission from Raycoms, one of the developers of the minecolonies project.
 */
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.pathfinding.*;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.pathjobs.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.pathfinding.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Region;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Minecolonies async PathNavigate.
 */
public class AdvancedPathNavigate extends AbstractAdvancedPathNavigate {
    public static final double MIN_Y_DISTANCE = 0.001;
    public static final int MAX_SPEED_ALLOWED = 2;
    public static final double MIN_SPEED_ALLOWED = 0.1;
    private static final double ON_PATH_SPEED_MULTIPLIER = 1.3D;
    private static final double PIRATE_SWIM_BONUS = 1.5;
    private static final double BARBARIAN_SWIM_BONUS = 1.2;
    private static final double CITIZEN_SWIM_BONUS = 1.1;
    @Nullable
    private PathResult pathResult;

    /**
     * The world time when a path was added.
     */
    private long pathStartTime = 0;

    /**
     * Spawn pos of minecart.
     */
    private BlockPos spawnedPos = BlockPos.ZERO;

    /**
     * Desired position to reach
     */
    private BlockPos desiredPos;

    /**
     * Timeout for the desired pos, resets when its no longer wanted
     */
    private int desiredPosTimeout = 0;

    /**
     * The stuck handler to use
     */
    private IStuckHandler stuckHandler;

    /**
     * Whether we did set sneaking
     */
    private boolean isSneaking = true;

    private float width = 1;

    private float height = 1;

    public enum MovementType {
        WALKING,
        FLYING,
        CLIMBING
    }
    /**
     * Instantiates the navigation of an ourEntity.
     *
     * @param entity the ourEntity.
     * @param world  the world it is in.
     */
    public AdvancedPathNavigate(final MobEntity entity, final World world) {
        this(entity,world,MovementType.WALKING);
    }
    public AdvancedPathNavigate(final MobEntity entity, final World world, MovementType type) {
       this(entity, world, type,1,1);
    }
    public AdvancedPathNavigate(final MobEntity entity, final World world, MovementType type, float width, float height) {
        super(entity, world);
        switch (type){
            case FLYING:
                this.nodeProcessor = new NodeProcessorFly();
                getPathingOptions().setIsFlying(true);
                break;
            case WALKING:
                this.nodeProcessor = new NodeProcessorWalk();
                break;
            case CLIMBING:
                this.nodeProcessor = new NodeProcessorWalk();
                getPathingOptions().setCanClimb(true);
                break;
        }
        this.nodeProcessor.setCanEnterDoors(true);
        getPathingOptions().setEnterDoors(true);
        this.nodeProcessor.setCanOpenDoors(true);
        getPathingOptions().setCanOpenDoors(true);
        this.nodeProcessor.setCanSwim(true);
        getPathingOptions().setCanSwim(true);
        this.width = width;
        this.height = height;
        stuckHandler = PathingStuckHandler.createStuckHandler().withTakeDamageOnStuck(0.2f).withTeleportSteps(6).withTeleportOnFullStuck();
    }

    public static boolean isEqual(final BlockPos coords, final int x, final int y, final int z) {
        return coords.getX() == x && coords.getY() == y && coords.getZ() == z;
    }

    @Override
    public BlockPos getDestination() {
        return destination;
    }

    @Nullable
    public PathResult moveAwayFromXYZ(final BlockPos avoid, final double range, final double speedFactor) {
        final BlockPos start = AbstractPathJob.prepareStart(ourEntity);

        return setPathJob(new PathJobMoveAwayFromLocation(ourEntity.world,
                start,
                avoid,
                (int) range,
                (int) ourEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue(),
                ourEntity), null, speedFactor);
    }

    @Nullable
    public RandomPathResult moveToRandomPos(final double range, final double speedFactor) {
        if (pathResult instanceof RandomPathResult && pathResult.isComputing()) {
            return (RandomPathResult) pathResult;
        }

        final int theRange = (int) (entity.getRNG().nextInt((int) range) + range / 2);
        final BlockPos start = AbstractPathJob.prepareStart(ourEntity);

        return (RandomPathResult) setPathJob(new PathJobRandomPos(ourEntity.world,
                start,
                theRange,
                (int) ourEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue(),
                ourEntity), null, speedFactor);
    }

    @Nullable
    public PathResult setPathJob(
            final AbstractPathJob job,
            final BlockPos dest,
            final double speedFactor) {
        if (dest != null && dest.equals(desiredPos) && calculationFuture != null && pathResult != null) {
            return pathResult;
        }

        clearPath();

        this.destination = dest;
        this.originalDestination = dest;
        if (dest != null) {
            desiredPos = dest;
            desiredPosTimeout = 50 * 20;
        }
        this.walkSpeedFactor = speedFactor;

        if (speedFactor > MAX_SPEED_ALLOWED || speedFactor < MIN_SPEED_ALLOWED) {
            IceAndFire.LOGGER.error("Tried to set a bad speed:" + speedFactor + " for entity:" + ourEntity, new Exception());
            return null;
        }

        job.setPathingOptions(getPathingOptions());
        calculationFuture = Pathfinding.enqueue(job);
        pathResult = job.getResult();
        return pathResult;
    }

    @Override
    public boolean noPath() {
        return calculationFuture == null && super.noPath();
    }

    @Override
    public void tick() {
        if (nodeProcessor instanceof  NodeProcessorWalk){
            ((NodeProcessorWalk)nodeProcessor).setEntitySize(width, height);
        }
        else{
            ((NodeProcessorFly)nodeProcessor).setEntitySize(width, height);
        }
        if (desiredPosTimeout > 0) {
            if (desiredPosTimeout-- <= 0) {
                desiredPos = null;
            }
        }

        if (calculationFuture != null) {
            if (!calculationFuture.isDone()) {
                return;
            }

            try {
                if (processCompletedCalculationResult()) {
                    return;
                }
            } catch (InterruptedException | ExecutionException e) {
                IceAndFire.LOGGER.catching(e);
            }

            calculationFuture = null;
        }

        int oldIndex = this.noPath() ? 0 : this.getPath().getCurrentPathIndex();

        if (isSneaking) {
            isSneaking = false;
            entity.setSneaking(false);
        }
        //this.ourEntity.setMoveVertical(0);
        if (handleLadders(oldIndex)) {
            pathFollow();
            return;
        }
        if (handleRails()) {
            return;
        }
        super.tick();

        if (pathResult != null && noPath()) {
            pathResult.setStatus(PathFindingStatus.COMPLETE);
            pathResult = null;
        }
        //Make sure the entity isn't sleeping or chained when checking if it's stuck
        if (this.entity instanceof TameableEntity){
            if (!((TameableEntity)this.entity).isTamed() &&
                (this.entity instanceof EntityDragonBase &&
                !((EntityDragonBase) this.entity).isChained() &&!((EntityDragonBase) this.entity).isEntitySleeping())) {
                stuckHandler.checkStuck(this);
            }
        }
        else{
            stuckHandler.checkStuck(this);
        }

    }

    @Nullable
    public PathResult moveToXYZ(final double x, final double y, final double z, final double speedFactor) {
        final int newX = MathHelper.floor(x);
        final int newY = (int) y;
        final int newZ = MathHelper.floor(z);

        if (pathResult != null &&
                (
                        pathResult.isComputing()
                                || (destination != null && isEqual(destination, newX, newY, newZ))
                                || (originalDestination != null && isEqual(originalDestination, newX, newY, newZ))
                )
        ) {
            return pathResult;
        }

        final BlockPos start = AbstractPathJob.prepareStart(ourEntity);
        desiredPos = new BlockPos(newX, newY, newZ);

        return setPathJob(
                new PathJobMoveToLocation(ourEntity.world,
                        start,
                        desiredPos,
                        (int) ourEntity.getAttribute(Attributes.FOLLOW_RANGE).getValue(),
                        ourEntity),
                desiredPos, speedFactor);
    }

    @Override
    public boolean tryMoveToBlockPos(final BlockPos pos, final double speedFactor) {
        moveToXYZ(pos.getX(), pos.getY(), pos.getZ(), speedFactor);
        return true;
    }
    //Return a new WalkNodeProcessor for safety reasons eg if the entity
    //has a passenger this method get's called and returning null is not a great idea
    @Override
    protected PathFinder getPathFinder(final int p_179679_1_) {
        return new PathFinder(new WalkNodeProcessor(),p_179679_1_);
    }

    @Override
    protected boolean canNavigate() {
        return true;
    }


    @Override
    protected Vector3d getEntityPosition() {
        return this.ourEntity.getPositionVec();
    }

    @Override
    public Path getPathToPos(final BlockPos pos, final int accuracy) {
        return null;
    }

    @Override
    protected boolean isDirectPathBetweenPoints(final Vector3d start, final Vector3d end, final int sizeX, final int sizeY, final int sizeZ) {
        // TODO improve road walking. This is better in some situations, but still not great.
        return super.isDirectPathBetweenPoints(start, end, sizeX, sizeY, sizeZ);
    }

    public double getSpeedFactor() {

        speed = walkSpeedFactor;
        return walkSpeedFactor;
    }

    @Override
    public void setSpeed(final double speedFactor) {
        if (speedFactor > MAX_SPEED_ALLOWED || speedFactor < MIN_SPEED_ALLOWED) {
            IceAndFire.LOGGER.debug("Tried to set a bad speed:" + speedFactor + " for entity:" + ourEntity);
            return;
        }
        walkSpeedFactor = speedFactor;
    }

    /**
     * Deprecated - try to use BlockPos instead
     */
    @Override
    public boolean tryMoveToXYZ(final double x, final double y, final double z, final double speedFactor) {
        if (x == 0 && y == 0 && z == 0) {
            return false;
        }

        moveToXYZ(x, y, z, speedFactor);
        return true;
    }

    @Override
    public boolean tryMoveToEntityLiving(final Entity entityIn, final double speedFactor) {
        return tryMoveToBlockPos(entityIn.getPosition(), speedFactor);
    }

    // Removes stupid vanilla stuff, causing our pathpoints to occasionally be replaced by vanilla ones.
    @Override
    protected void trimPath() {
    }

    @Override
    public boolean setPath(@Nullable final Path path, final double speedFactor) {
        if (path == null) {
            clearPath();
            return false;
        }
        pathStartTime = world.getGameTime();
        return super.setPath(convertPath(path), speedFactor);
    }

    /**
     * Converts the given path to a minecolonies path if needed.
     *
     * @param path given path
     * @return resulting path
     */
    private Path convertPath(final Path path) {
        final int pathLength = path.getCurrentPathLength();
        Path tempPath = null;
        if (pathLength > 0 && !(path.getPathPointFromIndex(0) instanceof PathPointExtended)) {
            //  Fix vanilla PathPoints to be PathPointExtended
            final PathPointExtended[] newPoints = new PathPointExtended[pathLength];

            for (int i = 0; i < pathLength; ++i) {
                final PathPoint point = path.getPathPointFromIndex(i);
                if (!(point instanceof PathPointExtended)) {
                    newPoints[i] = new PathPointExtended(new BlockPos(point.x, point.y, point.z));
                } else {
                    newPoints[i] = (PathPointExtended) point;
                }
            }

            tempPath = new Path(Arrays.asList(newPoints), path.getTarget(), path.reachesTarget());

            final PathPointExtended finalPoint = newPoints[pathLength - 1];
            destination = new BlockPos(finalPoint.x, finalPoint.y, finalPoint.z);
        }

        return tempPath == null ? path : tempPath;
    }

    private boolean processCompletedCalculationResult() throws InterruptedException, ExecutionException {
        if (calculationFuture.get() == null) {
            calculationFuture = null;
            return true;
        }

        setPath(calculationFuture.get(), getSpeedFactor());

        pathResult.setPathLength(getPath().getCurrentPathLength());
        pathResult.setStatus(PathFindingStatus.IN_PROGRESS_FOLLOWING);

        final PathPoint p = getPath().getFinalPathPoint();
        if (p != null && destination == null) {
            destination = new BlockPos(p.x, p.y, p.z);

            //  AbstractPathJob with no destination, did reach it's destination
            pathResult.setPathReachesDestination(true);
        }
        return false;
    }

    private boolean handleLadders(int oldIndex) {
        //  Ladder Workaround
        if (!this.noPath()) {
            final PathPointExtended pEx = (PathPointExtended) this.getPath().getPathPointFromIndex(this.getPath().getCurrentPathIndex());
            final PathPointExtended pExNext = getPath().getCurrentPathLength() > this.getPath().getCurrentPathIndex() + 1
                    ? (PathPointExtended) this.getPath()
                    .getPathPointFromIndex(this.getPath()
                            .getCurrentPathIndex() + 1)
                    : null;

            for (int i = this.currentPath.getCurrentPathIndex(); i < Math.min(this.currentPath.getCurrentPathLength(), this.currentPath.getCurrentPathIndex() + 3); i++) {
                final PathPointExtended nextPoints = (PathPointExtended) this.getPath().getPathPointFromIndex(i);
                if (nextPoints.isOnLadder()) {
                    Vector3d motion = this.entity.getMotion();
                    double x = motion.x < -0.1 ? -0.1 : Math.min(motion.x, 0.1);
                    double z = motion.x < -0.1 ? -0.1 : Math.min(motion.z, 0.1);

                    this.ourEntity.setMotion(x, motion.y, z);
                    break;
                }
            }

            if (ourEntity.isInWater()) {
                return handleEntityInWater(oldIndex, pEx);
            } else if (world.rand.nextInt(10) == 0) {
                speed = getSpeedFactor();
            }
        }
        return false;
    }

    /**
     * Determine what block the entity stands on
     *
     * @param parEntity the entity that stands on the block
     * @return the Blockstate.
     */
    private BlockPos findBlockUnderEntity(final Entity parEntity) {
        int blockX = (int) Math.round(parEntity.getPosX());
        int blockY = MathHelper.floor(parEntity.getPosY() - 0.2D);
        int blockZ = (int) Math.round(parEntity.getPosZ());
        return new BlockPos(blockX, blockY, blockZ);
    }

    /**
     * Handle rails navigation.
     *
     * @return true if block.
     */
    private boolean handleRails() {
        if (!this.noPath()) {
            final PathPointExtended pEx = (PathPointExtended) this.getPath().getPathPointFromIndex(this.getPath().getCurrentPathIndex());
            final PathPointExtended pExNext = getPath().getCurrentPathLength() > this.getPath().getCurrentPathIndex() + 1
                    ? (PathPointExtended) this.getPath()
                    .getPathPointFromIndex(this.getPath()
                            .getCurrentPathIndex() + 1)
                    : null;
        }
        return false;
    }

    private boolean handleEntityInWater(int oldIndex, final PathPointExtended pEx) {
        //  Prevent shortcuts when swimming
        final int curIndex = this.getPath().getCurrentPathIndex();
        if (curIndex > 0
                && (curIndex + 1) < this.getPath().getCurrentPathLength()
                && this.getPath().getPathPointFromIndex(curIndex - 1).y != pEx.y) {
            //  Work around the initial 'spin back' when dropping into water
            oldIndex = curIndex + 1;
        }

        this.getPath().setCurrentPathIndex(oldIndex);

        Vector3d vec3d = this.getPath().getPosition(this.ourEntity);

        if (vec3d.squareDistanceTo(new Vector3d(ourEntity.getPosX(), vec3d.y, ourEntity.getPosZ())) < 0.1
                && Math.abs(ourEntity.getPosY() - vec3d.y) < 0.5) {
            this.getPath().incrementPathIndex();
            if (this.noPath()) {
                return true;
            }

            vec3d = this.getPath().getPosition(this.ourEntity);
        }

        this.ourEntity.getMoveHelper().setMoveTo(vec3d.x, vec3d.y, vec3d.z, getSpeedFactor());
        return false;
    }

    @Override
    protected void pathFollow() {
        getSpeedFactor();
        final int curNode = currentPath.getCurrentPathIndex();
        final int curNodeNext = curNode + 1;
        if (curNodeNext < currentPath.getCurrentPathLength()) {
            if (!(currentPath.getPathPointFromIndex(curNode) instanceof PathPointExtended)) {
                currentPath = convertPath(currentPath);
            }

            final PathPointExtended pEx = (PathPointExtended) currentPath.getPathPointFromIndex(curNode);
            final PathPointExtended pExNext = (PathPointExtended) currentPath.getPathPointFromIndex(curNodeNext);

            //  If current node is bottom of a ladder, then stay on this node until
            //  the ourEntity reaches the bottom, otherwise they will try to head out early
            if (pEx.isOnLadder() && pEx.getLadderFacing() == Direction.DOWN
                    && !pExNext.isOnLadder()) {
                final Vector3d vec3 = getEntityPosition();
                if ((vec3.y - (double) pEx.y) < MIN_Y_DISTANCE) {
                    this.currentPath.setCurrentPathIndex(curNodeNext);
                }
                return;
            }
        }

        Vector3d vec3d = this.getEntityPosition();
        this.maxDistanceToWaypoint = Math.max(this.entity.getWidth() / 2.0F, 1.3F);
        double maxYDistance = 0;
        // Look at multiple points, incase we're too fast
        for (int i = this.currentPath.getCurrentPathIndex(); i < Math.min(this.currentPath.getCurrentPathLength(), this.currentPath.getCurrentPathIndex() + 4); i++) {
            Vector3d vec3d2 = this.currentPath.getVectorFromIndex(this.entity, i);
            double yDist = Math.abs(this.entity.getPosY() - vec3d2.y);
            if (Math.abs(this.entity.getPosX() - vec3d2.x) < (double) this.maxDistanceToWaypoint
                    && Math.abs(this.entity.getPosZ() - vec3d2.z) < (double) this.maxDistanceToWaypoint &&
                    yDist <= Math.min(1.0F, Math.ceil(this.entity.getHeight() / 2.0F))) {
                this.currentPath.incrementPathIndex();
                // Mark reached nodes for debug path drawing
                if (AbstractPathJob.lastDebugNodesPath != null) {
                    final PathPoint point = currentPath.getPathPointFromIndex(i);
                    final BlockPos pos = new BlockPos(point.x, point.y, point.z);
                    for (final Node node : AbstractPathJob.lastDebugNodesPath) {
                        if (node.pos.equals(pos)) {
                            node.setReachedByWorker();
                            break;
                        }
                    }
                }
            }
        }
    }

    public void updatePath() {
    }

    /**
     * Don't let vanilla rapidly discard paths, set a timeout before its allowed to use stuck.
     */
    @Override
    protected void checkForStuck(final Vector3d positionVec3) {
        // Do nothing, unstuck is checked on tick, not just when we have a path
    }

    public boolean entityOnOrBelowPath(Entity entity, Vector3d slack){
        Path path = getPath();
        if (path == null){
            return false;
        }
        //getIndex doesn't return an 0-indexed index
        int closest = path.getCurrentPathIndex() - 1;
        if (closest < 0) {
            return true;
        }
        //Search through path from the current index outwards to improve performance
        for (int i = 0; i < path.getCurrentPathLength(); i++) {
            if (closest + i < path.getCurrentPathLength()) {
                PathPoint currentPoint = path.getPathPointFromIndex(closest + i);
                if (entityNearOrBelowPoint(currentPoint, entity, slack)) {
                    return true;
                }
            }
            if (closest - i >= 0) {
                PathPoint currentPoint = path.getPathPointFromIndex(closest - i);
                if (entityNearOrBelowPoint(currentPoint, entity, slack)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean entityNearOrBelowPoint(PathPoint currentPoint, Entity entity, Vector3d slack) {
        return Math.abs(currentPoint.x - entity.getPosX()) < slack.getX()
                && currentPoint.y - entity.getPosY() > -slack.getY()
                && Math.abs(currentPoint.z - entity.getPosZ()) < slack.getZ();
    }



    @Override
    public void clearPath() {
        if (calculationFuture != null) {
            calculationFuture.cancel(true);
            calculationFuture = null;
        }

        if (pathResult != null) {
            pathResult.setStatus(PathFindingStatus.CANCELLED);
            pathResult = null;
        }

        destination = null;
        super.clearPath();
    }

    @Nullable
    @Override
    public PathResult moveToLivingEntity(final Entity e, final double speed) {
        return moveToXYZ(e.getPosX(), e.getPosY(), e.getPosZ(), speed);
    }

    @Nullable
    @Override
    public PathResult moveAwayFromLivingEntity(final Entity e, final double distance, final double speed) {
        return moveAwayFromXYZ(e.getPosition(), distance, speed);
    }

    @Override
    public void setCanSwim(boolean canSwim) {
        super.setCanSwim(canSwim);
        getPathingOptions().setCanSwim(canSwim);
    }

    public BlockPos getDesiredPos() {
        return desiredPos;
    }

    /**
     * Sets the stuck handler
     *
     * @param stuckHandler handler to set
     */
    @Override
    public void setStuckHandler(final IStuckHandler stuckHandler) {
        this.stuckHandler = stuckHandler;
    }
}
