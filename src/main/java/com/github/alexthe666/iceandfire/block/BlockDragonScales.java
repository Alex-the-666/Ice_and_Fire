package com.github.alexthe666.iceandfire.block;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockDragonScales extends Block implements IDragonProof {
    EnumDragonEgg type;

    public BlockDragonScales(String name, EnumDragonEgg type) {
        super(
    		Properties
    			.create(Material.ROCK)
    			.variableOpacity()
    			.hardnessAndResistance(30F, 500)
    			.harvestTool(ToolType.PICKAXE)
    			.harvestLevel(2)
    			.sound(SoundType.STONE)
    			.setRequiresTool()
		);

        this.setRegistryName("iceandfire:" + name);
        this.type = type;
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("dragon." + type.toString().toLowerCase()).mergeStyle(type.color));
    }
}
