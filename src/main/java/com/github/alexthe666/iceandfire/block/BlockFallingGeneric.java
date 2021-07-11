package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import net.minecraft.block.AbstractBlock;

public class BlockFallingGeneric extends FallingBlock {
    public Item itemBlock;

    public BlockFallingGeneric(Material materialIn, String name, String toolUsed, int toolStrength, float hardness, float resistance, SoundType sound) {
        super(
    		AbstractBlock.Properties
    			.create(materialIn)
				.sound(sound)
				.hardnessAndResistance(hardness, resistance)
				.harvestTool(ToolType.get(toolUsed))
				.harvestLevel(toolStrength)
		);

        setRegistryName(IceAndFire.MODID, name);

    }

    @SuppressWarnings("deprecation")
    public BlockFallingGeneric(Material materialIn, String gameName, String name, String toolUsed, int toolStrength, float hardness, float resistance, SoundType sound, boolean slippery) {
        super(
    		AbstractBlock.Properties
    			.create(materialIn)
    			.sound(sound)
    			.hardnessAndResistance(hardness, resistance)
    			.harvestTool(ToolType.get(toolUsed))
    			.harvestLevel(toolStrength)
    			.slipperiness(0.98F)
		);

        setRegistryName(IceAndFire.MODID, name);
    }

    @OnlyIn(Dist.CLIENT)
    public int getDustColor(BlockState blkst) {
        return -8356741;
    }
}
