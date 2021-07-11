package com.github.alexthe666.iceandfire.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class WorldGenCaveStalactites {
    private Block block;
    private int maxHeight = 3;

    public WorldGenCaveStalactites(Block block, int maxHeight) {
        this.block = block;
        this.maxHeight = maxHeight;
    }

    public boolean generate(IWorld worldIn, Random rand, BlockPos position) {
        int height = maxHeight + rand.nextInt(3);
        for (int i = 0; i < height; i++) {
            if (i < height / 2) {
                worldIn.setBlockState(position.down(i).north(), block.getDefaultState(), 2);
                worldIn.setBlockState(position.down(i).east(), block.getDefaultState(), 2);
                worldIn.setBlockState(position.down(i).south(), block.getDefaultState(), 2);
                worldIn.setBlockState(position.down(i).west(), block.getDefaultState(), 2);
            }
            worldIn.setBlockState(position.down(i), block.getDefaultState(), 2);
        }
        return true;
    }
}
