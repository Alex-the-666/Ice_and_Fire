package com.github.alexthe666.iceandfire.pathfinding.raycoms;
/*
    All of this code is used with permission from Raycoms, one of the developers of the minecolonies project.
 */
import net.minecraft.util.math.BlockPos;

public class PathfindingConstants {
    //  Debug Output
    public static final int      DEBUG_VERBOSITY_NONE = 0;
    public static final int      DEBUG_VERBOSITY_FULL = 2;
    public static final Object   debugNodeMonitor     = new Object();
    public static final BlockPos BLOCKPOS_IDENTITY    = new BlockPos(0, 0, 0);
    public static final BlockPos BLOCKPOS_UP          = new BlockPos(0, 1, 0);
    public static final BlockPos BLOCKPOS_DOWN        = new BlockPos(0, -1, 0);
    public static final BlockPos BLOCKPOS_NORTH       = new BlockPos(0, 0, -1);
    public static final BlockPos BLOCKPOS_SOUTH       = new BlockPos(0, 0, 1);
    public static final BlockPos BLOCKPOS_EAST        = new BlockPos(1, 0, 0);
    public static final BlockPos BLOCKPOS_WEST        = new BlockPos(-1, 0, 0);
    public static final int      MAX_Y                = 256;
    public static final int      MIN_Y                = 0;

    /**
     * Distance which is considered to be on one side of the fence/glasspane/wall etc.
     */
    public static final double ONE_SIDE = 0.25D;

    /**
     * Distance which is considered to be on the other side of the fence/glasspane/wall etc.
     */
    public static final double OTHER_SIDE = 0.75D;

    /**
     * Shift x by this value to calculate the node key..
     */
    public static final int SHIFT_X_BY = 20;

    /**
     * Shift the y value by this to calculate the node key..
     */
    public static final int SHIFT_Y_BY = 12;

    /**
     * Max jump height.
     */
    public static final double MAX_JUMP_HEIGHT = 1.3;

    /**
     * Half a block.
     */
    public static final double HALF_A_BLOCK = 0.5;

    /**
     * Private constructor to hide implicit one.
     */
    private PathfindingConstants()
    {
        /*
         * Intentionally left empty.
         */
    }
}