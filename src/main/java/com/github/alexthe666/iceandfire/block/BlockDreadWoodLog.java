package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockDreadWoodLog extends RotatedPillarBlock implements IDragonProof, IDreadBlock {

    public BlockDreadWoodLog() {
        super(
    		Properties
    			.create(Material.WOOD)
    			.hardnessAndResistance(2F, 10000F)
    			.sound(SoundType.WOOD)
		);

        this.setRegistryName(IceAndFire.MODID, "dreadwood_log");
    }
}
