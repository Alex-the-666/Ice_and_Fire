package com.github.alexthe666.iceandfire.world.gen.processor;

import com.github.alexthe666.iceandfire.world.IafProcessors;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;

import javax.annotation.Nullable;
import java.util.Random;

public class VillageHouseProcessor extends StructureProcessor {

    public static final ResourceLocation LOOT = new ResourceLocation("iceandfire", "chest/village_scribe");
    private float integrity = 1.0F;
    public static final VillageHouseProcessor INSTANCE = new VillageHouseProcessor();
    public static final Codec<VillageHouseProcessor> CODEC = Codec.unit(() -> INSTANCE);
    public VillageHouseProcessor() {
    }

    public Template.BlockInfo process(IWorldReader worldReader, BlockPos pos, BlockPos pos2, Template.BlockInfo infoIn1, Template.BlockInfo infoIn2, PlacementSettings settings,@Nullable Template template) {
        Random random = settings.getRandom(infoIn2.pos);
        if (infoIn2.state.getBlock() == Blocks.CHEST) {
            CompoundNBT tag = new CompoundNBT();
            tag.putString("LootTable", LOOT.toString());
            tag.putLong("LootTableSeed", random.nextLong());
            return new Template.BlockInfo(infoIn2.pos, infoIn2.state, tag);
        }
        return infoIn2;
    }


    @Override
    protected IStructureProcessorType getType() {
        return IafProcessors.VILLAGEHOUSEPROCESSOR;
    }

}
