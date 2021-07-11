package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemSeaSerpentScales extends ItemGeneric {

    private TextFormatting color;
    private String colorName;

    public ItemSeaSerpentScales(String colorName, TextFormatting color) {
        super("sea_serpent_scales_" + colorName);
        this.color = color;
        this.colorName = colorName;
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("sea_serpent." + colorName).mergeStyle(color));
    }
}
