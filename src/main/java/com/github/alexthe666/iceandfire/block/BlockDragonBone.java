package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraftforge.common.ToolType;

import net.minecraft.block.AbstractBlock;

public class BlockDragonBone extends RotatedPillarBlock implements IDragonProof {

    public BlockDragonBone() {
        super(
    		AbstractBlock.Properties
	    		.create(Material.ROCK)
	    		.sound(SoundType.WOOD)
	    		.hardnessAndResistance(30F, 500F)
	    		.harvestTool(ToolType.PICKAXE)
	    		.harvestLevel(1)
	    		.setRequiresTool()
		);

        this.setRegistryName(IceAndFire.MODID, "dragon_bone_block");
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }
}
