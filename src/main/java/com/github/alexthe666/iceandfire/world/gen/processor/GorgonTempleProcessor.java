package com.github.alexthe666.iceandfire.world.gen.processor;

import com.github.alexthe666.iceandfire.block.BlockGhostChest;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.world.IafProcessors;
import com.mojang.serialization.Codec;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;

import javax.annotation.Nullable;
import java.util.Random;

public class GorgonTempleProcessor extends StructureProcessor {

    private float integrity = 1.0F;
    public static final GorgonTempleProcessor INSTANCE = new GorgonTempleProcessor();
    public static final Codec<GorgonTempleProcessor> CODEC = Codec.unit(() -> INSTANCE);
    public GorgonTempleProcessor() {
    }


    public Template.BlockInfo process(IWorldReader worldReader, BlockPos pos, BlockPos pos2, Template.BlockInfo infoIn1, Template.BlockInfo infoIn2, PlacementSettings settings, @Nullable Template template) {

        // Workaround for https://bugs.mojang.com/browse/MC-130584
        // Due to a hardcoded field in Templates, any waterloggable blocks in structures replacing water in the world will become waterlogged.
        // Idea of workaround is detect if we are placing a waterloggable block and if so, remove the water in the world instead.
        ChunkPos currentChunk = new ChunkPos(infoIn2.pos);
        if(infoIn2.state.getBlock() instanceof IWaterLoggable){
            if(worldReader.getFluidState(infoIn2.pos).isTagged(FluidTags.WATER)){
               worldReader.getChunk(currentChunk.x, currentChunk.z).setBlockState(infoIn2.pos, Blocks.AIR.getDefaultState(), false);
            }
        }

        // Needed as waterloggable blocks will get waterlogged from neighboring chunk's water too.
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for(Direction direction : Direction.Plane.HORIZONTAL){
            mutable.setPos(infoIn2.pos).move(direction);
            if(currentChunk.x != mutable.getX() >> 4 || currentChunk.z != mutable.getZ() >> 4){
                IChunk sideChunk = worldReader.getChunk(mutable);
                if(sideChunk.getFluidState(mutable).isTagged(FluidTags.WATER)) {
                    sideChunk.setBlockState(mutable, Blocks.STONE_BRICKS.getDefaultState(), false);
                }
            }
        }

        return infoIn2;
    }


    @Override
    protected IStructureProcessorType getType() {
        return IafProcessors.GORGONTEMPLEPROCESSOR;
    }
}
