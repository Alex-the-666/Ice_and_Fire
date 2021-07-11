package com.github.alexthe666.iceandfire.world.structure;

import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.ShipwreckPieces;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;

public class DummyPiece extends TemplateStructurePiece {
    // Register to the registry name of the old structure piece removed to prevent logspam for players in existing worlds.

    public DummyPiece(TemplateManager p_i48904_1_, ResourceLocation p_i48904_2_, BlockPos p_i48904_3_, Rotation p_i48904_4_, Random random) {
        super(IafWorldRegistry.DUMMY_PIECE, 0);
        func_204754_a(p_i48904_1_);
    }

    public DummyPiece(TemplateManager p_i50445_1_, CompoundNBT p_i50445_2_) {
        super(IafWorldRegistry.DUMMY_PIECE, p_i50445_2_);
        func_204754_a(p_i50445_1_);
    }

    // Sets up various templateStructurePiece variables these aren't necessarily needed
    // but are included as a backup to avoid crashes
    // Code stems from ShipwreckPieces.class
    private void func_204754_a(TemplateManager p_204754_1_) {
        Template lvt_2_1_ = p_204754_1_.getTemplateDefaulted(new ResourceLocation("minecraft:empty"));
        PlacementSettings lvt_3_1_ = (new PlacementSettings()).setRotation(Rotation.CLOCKWISE_90).setMirror(Mirror.NONE).setCenterOffset(new BlockPos(0,0,0)).addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
        this.setup(lvt_2_1_, this.templatePosition, lvt_3_1_);
    }
    //Override post processing function since we don't have to do any for this dummy piece
    @Override
    public boolean func_230383_a_(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_) {
        return true;
    }

    protected void handleDataMarker(String function, BlockPos pos, IServerWorld worldIn, Random rand, MutableBoundingBox sbb) {
    }
}
