package com.github.alexthe666.iceandfire.block;

import java.util.Random;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockBurntTorchWall extends WallTorchBlock implements IDreadBlock {

    public BlockBurntTorchWall() {
        super(
    		Properties.create(Material.WOOD)
        		.setLightLevel((state) -> { return 0; })
        		.sound(SoundType.WOOD).notSolid().variableOpacity()
        		.lootFrom(IafBlockRegistry.BURNT_TORCH)
                .doesNotBlockMovement(),
    		RedstoneParticleData.REDSTONE_DUST
		);

        setRegistryName(IceAndFire.MODID, "burnt_torch_wall");
    }


    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {

    }
}