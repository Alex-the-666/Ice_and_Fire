package com.github.alexthe666.iceandfire.pathfinding.raycoms;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface IPassabilityNavigator {

    int maxSearchNodes();

    boolean isBlockPassable(BlockState state, BlockPos pos, BlockPos entityPos);
}
