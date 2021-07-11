package com.github.alexthe666.iceandfire.world.gen;

import java.util.Random;
import java.util.stream.Collectors;

import com.github.alexthe666.iceandfire.block.BlockGoldPile;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;

public class WorldGenRoostGoldPile {
    private Block block;

    public WorldGenRoostGoldPile(Block block) {
        this.block = block;
    }

    public boolean generate(IWorld worldIn, Random rand, BlockPos position) {
        int radius = rand.nextInt(3);
        int layers = radius;
        for (int i = 0; i < layers; i++) {
            int j = radius - i;
            int l = radius - i;
            float f = (float) (j + l) * 0.333F + 0.5F;
            BlockPos up = position.up(i);
            for (BlockPos blockpos : BlockPos.getAllInBox(up.add(-j, 0, -l), up.add(j, 0, l)).map(BlockPos::toImmutable).collect(Collectors.toSet())) {
                if (blockpos.distanceSq(position) <= (double) (f * f)) {
                    blockpos = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, blockpos);
                    if (block instanceof BlockGoldPile) {
                        if (worldIn.isAirBlock(blockpos)) {
                            worldIn.setBlockState(blockpos, block.getDefaultState().with(BlockGoldPile.LAYERS, 1 + rand.nextInt(7)), 2);
                            if (worldIn.getBlockState(blockpos.down()).getBlock() instanceof BlockGoldPile) {
                                worldIn.setBlockState(blockpos.down(), block.getDefaultState().with(BlockGoldPile.LAYERS, 8), 2);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
