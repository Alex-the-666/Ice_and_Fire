package com.github.alexthe666.iceandfire.world.gen;

import java.util.Random;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class WorldGenRoostPile {
    private Block block;

    public WorldGenRoostPile(Block block) {
        this.block = block;
    }

    public boolean generate(IWorld worldIn, Random rand, BlockPos position) {
        int radius = rand.nextInt(4);
        int layers = radius;
        for (int i = 0; i < layers; i++) {
            int j = radius - i;
            int l = radius - i;
            float f = (float) (j + l) * 0.333F + 0.5F;
            BlockPos up = position.up(i);
            for (BlockPos blockpos : BlockPos.getAllInBox(up.add(-j, 0, -l), up.add(j, 0, l)).map(BlockPos::toImmutable).collect(Collectors.toSet())) {
                if (blockpos.distanceSq(position) <= (double) (f * f)) {
                    worldIn.setBlockState(blockpos, block.getDefaultState(), 2);
                }
            }
        }
        return true;
    }
}
