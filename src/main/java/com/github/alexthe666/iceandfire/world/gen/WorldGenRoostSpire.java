package com.github.alexthe666.iceandfire.world.gen;

import java.util.Random;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class WorldGenRoostSpire {

    public boolean generate(IWorld worldIn, Random rand, BlockPos position) {
        int height = 5 + rand.nextInt(5);
        Direction bumpDirection = Direction.NORTH;
        for(int i = 0; i < height; i++){
            worldIn.setBlockState(position.up(i), IafBlockRegistry.CRACKLED_STONE.getDefaultState(), 2);
            if(rand.nextBoolean()){
                bumpDirection = bumpDirection.rotateY();
            }
            int offset = 1;
            if(i < 4){
                worldIn.setBlockState(position.up(i).north(), IafBlockRegistry.CRACKLED_GRAVEL.getDefaultState(), 2);
                worldIn.setBlockState(position.up(i).south(), IafBlockRegistry.CRACKLED_GRAVEL.getDefaultState(), 2);
                worldIn.setBlockState(position.up(i).east(), IafBlockRegistry.CRACKLED_GRAVEL.getDefaultState(), 2);
                worldIn.setBlockState(position.up(i).west(), IafBlockRegistry.CRACKLED_GRAVEL.getDefaultState(), 2);
                offset = 2;
            }
            if(i < height - 2){
                worldIn.setBlockState(position.up(i).offset(bumpDirection, offset), IafBlockRegistry.CRACKLED_COBBLESTONE.getDefaultState(), 2);
            }
        }
        return true;
    }
}