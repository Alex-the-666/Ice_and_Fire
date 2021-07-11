package com.github.alexthe666.iceandfire.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class WorldGenRoostArch {
    private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private Block block;

    public WorldGenRoostArch(Block block) {
        this.block = block;
    }

    public boolean generate(IWorld worldIn, Random rand, BlockPos position) {
        int height = 3 + rand.nextInt(3);
        int width = Math.min(3, height - 2);
        Direction direction = HORIZONTALS[rand.nextInt(HORIZONTALS.length - 1)];
        boolean diagonal = rand.nextBoolean();
        for (int i = 0; i < height; i++) {
            worldIn.setBlockState(position.up(i), block.getDefaultState(), 2);
        }
        BlockPos offsetPos = position;
        int placedWidths = 0;
        for (int i = 0; i < width; i++) {
            offsetPos = position.up(height).offset(direction, i);
            if (diagonal) {
                offsetPos = position.up(height).offset(direction, i).offset(direction.rotateY(), i);
            }
            if (placedWidths < width - 1 || rand.nextBoolean()) {
                worldIn.setBlockState(offsetPos, block.getDefaultState(), 2);
            }
            placedWidths++;
        }
        while (worldIn.isAirBlock(offsetPos.down()) && offsetPos.getY() > 0) {
            worldIn.setBlockState(offsetPos.down(), block.getDefaultState(), 2);
            offsetPos = offsetPos.down();
        }
        return true;
    }
}
