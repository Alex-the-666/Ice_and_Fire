package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadSpawner;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import net.minecraft.block.AbstractBlock;

public class BlockDreadSpawner extends SpawnerBlock implements IDreadBlock {

    public BlockDreadSpawner() {
        super(
    		AbstractBlock.Properties
    			.create(Material.ROCK)
    			.hardnessAndResistance(10.0F, 10000F)
    			.sound(SoundType.METAL)
    			.notSolid()
    			.variableOpacity()
		);

        this.setRegistryName(IceAndFire.MODID, "dread_spawner");
    }

    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityDreadSpawner();
    }

}
