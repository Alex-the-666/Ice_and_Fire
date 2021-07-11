package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemDragonScales extends Item {
    EnumDragonEgg type;

    public ItemDragonScales(String name, EnumDragonEgg type) {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS));
        this.type = type;
        this.setRegistryName(IceAndFire.MODID, name);
    }

    public String getTranslationKey() {
        return "item.iceandfire.dragonscales";
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("dragon." + type.toString().toLowerCase()).mergeStyle(type.color));
    }

}
