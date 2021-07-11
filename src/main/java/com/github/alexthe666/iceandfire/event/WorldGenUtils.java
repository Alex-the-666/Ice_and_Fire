package com.github.alexthe666.iceandfire.event;

import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class WorldGenUtils {

    private static boolean canHeightSkipBlock(BlockPos pos, IWorld world) {
        BlockState state = world.getBlockState(pos);
        return BlockTags.LOGS.contains(state.getBlock()) || !state.getFluidState().isEmpty();
    }

    public static BlockPos degradeSurface(IWorld world, BlockPos surface) {
        while ((!world.getBlockState(surface).isSolid() || canHeightSkipBlock(surface, world)) && surface.getY() > 1) {
            surface = surface.down();
        }
        return surface;
    }
}
