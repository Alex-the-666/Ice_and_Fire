package com.github.alexthe666.iceandfire.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraftforge.common.ToolType;

import net.minecraft.block.AbstractBlock;

public class BlockGenericStairs extends StairsBlock {

    public BlockGenericStairs(BlockState modelState, String name) {
        super(
    		modelState,
    		AbstractBlock.Properties
    			.create(modelState.getMaterial())
    			.harvestTool(ToolType.PICKAXE)
    			.hardnessAndResistance(20F)
		);
        
        this.setRegistryName(name);
    }
}
