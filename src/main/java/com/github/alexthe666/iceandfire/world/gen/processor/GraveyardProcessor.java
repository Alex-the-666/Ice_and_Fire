package com.github.alexthe666.iceandfire.world.gen.processor;

import java.util.Random;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.block.BlockGhostChest;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;

import com.github.alexthe666.iceandfire.world.IafProcessors;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;

public class GraveyardProcessor extends StructureProcessor {

    private float integrity = 1.0F;
    public static final GraveyardProcessor INSTANCE = new GraveyardProcessor();
    public static final Codec<GraveyardProcessor> CODEC = Codec.unit(() -> INSTANCE);
    public GraveyardProcessor() {
    }

    public static BlockState getRandomCobblestone(@Nullable BlockState prev, Random random) {
        float rand = random.nextFloat();
        if (rand < 0.5) {
            return Blocks.COBBLESTONE.getDefaultState();
        } else if (rand < 0.9) {
            return Blocks.MOSSY_COBBLESTONE.getDefaultState();
        } else {
            return Blocks.INFESTED_COBBLESTONE.getDefaultState();
        }
    }

    public static BlockState getRandomCrackedBlock(@Nullable BlockState prev, Random random) {
        float rand = random.nextFloat();
        if (rand < 0.5) {
            return Blocks.STONE_BRICKS.getDefaultState();
        } else if (rand < 0.9) {
            return Blocks.CRACKED_STONE_BRICKS.getDefaultState();
        } else {
            return Blocks.MOSSY_STONE_BRICKS.getDefaultState();
        }
    }

    public Template.BlockInfo process(IWorldReader worldReader, BlockPos pos, BlockPos pos2, Template.BlockInfo infoIn1, Template.BlockInfo infoIn2, PlacementSettings settings,@Nullable Template template) {
        Random random = settings.getRandom(infoIn2.pos);
        if (infoIn2.state.getBlock() == Blocks.STONE_BRICKS) {
            BlockState state = getRandomCrackedBlock(null, random);
            return new Template.BlockInfo(infoIn2.pos, state, null);
        }
        if (infoIn2.state.getBlock() == Blocks.COBBLESTONE) {
            BlockState state = getRandomCobblestone(null, random);
            return new Template.BlockInfo(infoIn2.pos, state, null);
        }
        return infoIn2;
    }


    @Override
    protected IStructureProcessorType getType() {
        return IafProcessors.GRAVEYARDPROCESSOR;
    }

}
