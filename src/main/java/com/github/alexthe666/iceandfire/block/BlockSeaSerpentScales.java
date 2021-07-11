package com.github.alexthe666.iceandfire.block;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockSeaSerpentScales extends Block {
    TextFormatting color;
    String name;

    public BlockSeaSerpentScales(String name, TextFormatting color) {
        super(
    		Properties
    			.create(Material.ROCK)
    			.hardnessAndResistance(30F, 500F)
    			.sound(SoundType.STONE)
    			.harvestTool(ToolType.PICKAXE)
    			.harvestLevel(2)
    			.setRequiresTool()
		);

        this.color = color;
        this.name = name;
        this.setRegistryName(IceAndFire.MODID, "sea_serpent_scale_block_" + name);
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("sea_serpent." + name).mergeStyle(color));
    }
}
