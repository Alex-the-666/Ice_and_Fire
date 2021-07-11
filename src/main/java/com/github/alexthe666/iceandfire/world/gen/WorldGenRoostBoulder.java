package com.github.alexthe666.iceandfire.world.gen;

import java.util.Random;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class WorldGenRoostBoulder {

    private final Block block;
    private final int startRadius;
    private boolean replaceAir;

    public WorldGenRoostBoulder(Block blockIn, int startRadiusIn, boolean replaceAir) {
        this.block = blockIn;
        this.startRadius = startRadiusIn;
        this.replaceAir = replaceAir;
    }

    public boolean generate(IWorld worldIn, Random rand, BlockPos position) {
        while (true) {
            label50:
            {
                if (position.getY() > 3) {
                    if (worldIn.isAirBlock(position.down())) {
                        break label50;
                    }

                    Block block = worldIn.getBlockState(position.down()).getBlock();

                    if (block != Blocks.GRASS && block != Blocks.DIRT && block != Blocks.STONE) {
                        break label50;
                    }
                }

                if (position.getY() <= 3) {
                    return false;
                }

                int i1 = this.startRadius;

                for (int i = 0; i1 >= 0 && i < 3; ++i) {
                    int j = i1 + rand.nextInt(2);
                    int k = i1 + rand.nextInt(2);
                    int l = i1 + rand.nextInt(2);
                    float f = (float) (j + k + l) * 0.333F + 0.5F;

                    for (BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet())) {
                        if (blockpos.distanceSq(position) <= (double) (f * f) && (replaceAir || worldIn.getBlockState(blockpos).isSolid())) {
                            worldIn.setBlockState(blockpos, this.block.getDefaultState(), 2);
                        }
                    }

                    position = position.add(-(i1 + 1) + rand.nextInt(2 + i1 * 2), 0 - rand.nextInt(2), -(i1 + 1) + rand.nextInt(2 + i1 * 2));
                }

                return true;
            }
            position = position.down();
        }
    }
}