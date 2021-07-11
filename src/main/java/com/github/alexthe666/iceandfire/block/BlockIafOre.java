package com.github.alexthe666.iceandfire.block;

import java.util.Random;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ToolType;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockIafOre extends Block {
    public Item itemBlock;

    public BlockIafOre(int toollevel, float hardness, float resistance, String name, String gameName) {
        super(
    		Properties
    			.create(Material.ROCK)
    			.harvestTool(ToolType.PICKAXE)
    			.harvestLevel(toollevel)
    			.hardnessAndResistance(hardness, resistance)
    			.setRequiresTool()
		);

        setRegistryName(IceAndFire.MODID, gameName);
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.IWorldReader reader, BlockPos pos, int fortune, int silktouch) {
        return silktouch == 0 ? this.getExperience(RANDOM) : 0;
    }

    protected int getExperience(Random rand) {
        if (this == IafBlockRegistry.SAPPHIRE_ORE || this == IafBlockRegistry.AMYTHEST_ORE) {
            return MathHelper.nextInt(rand, 3, 7);
        }
        return 0;
    }
}
