package com.github.alexthe666.iceandfire.world.gen.processor;

import java.util.Random;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;

public class DreadPortalProcessor extends StructureProcessor {

    private float integrity = 1.0F;

    public DreadPortalProcessor(BlockPos position, PlacementSettings settings, Biome biome) {
    }

    public static BlockState getRandomCrackedBlock(@Nullable BlockState prev, Random random) {
        float rand = random.nextFloat();
        if (rand < 0.3) {
            return IafBlockRegistry.DREAD_STONE_BRICKS.getDefaultState();
        } else if (rand < 0.6) {
            return IafBlockRegistry.DREAD_STONE_BRICKS_CRACKED.getDefaultState();
        } else {
            return IafBlockRegistry.DREAD_STONE_BRICKS_MOSSY.getDefaultState();
        }
    }

    @Nullable
    @Override
    public Template.BlockInfo process(IWorldReader world, BlockPos pos, BlockPos p_230386_3_, Template.BlockInfo blockInfoIn, Template.BlockInfo p_230386_5_, PlacementSettings settings, @Nullable Template template) {
        Random random = settings.getRandom(pos);
        if (random.nextFloat() <= integrity) {
            if (blockInfoIn.state.getBlock() == Blocks.DIAMOND_BLOCK) {
                return new Template.BlockInfo(pos, IafBlockRegistry.DREAD_PORTAL.getDefaultState(), null);
            }
            if (blockInfoIn.state.getBlock() == IafBlockRegistry.DREAD_STONE_BRICKS) {
                BlockState state = getRandomCrackedBlock(null, random);
                return new Template.BlockInfo(pos, state, null);
            }
            return blockInfoIn;
        }
        return blockInfoIn;

    }

    @Override
    protected IStructureProcessorType getType() {
        return IStructureProcessorType.BLOCK_ROT;
    }

}
