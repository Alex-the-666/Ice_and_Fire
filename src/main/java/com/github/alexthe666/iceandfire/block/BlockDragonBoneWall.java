package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;

import net.minecraft.block.AbstractBlock;

public class BlockDragonBoneWall extends WallBlock implements IDragonProof {

    public BlockDragonBoneWall(AbstractBlock.Properties props) {
        super(props);
        this.setRegistryName(IceAndFire.MODID, "dragon_bone_wall");
    }
}
