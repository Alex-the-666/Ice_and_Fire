package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemGeneric extends Item {
    int description = 0;

    public ItemGeneric(String name) {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS));
        this.setRegistryName(IceAndFire.MODID, name);
    }

    public ItemGeneric(String name, int textLength) {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS));
        this.setRegistryName(IceAndFire.MODID, name);
        this.description = textLength;
    }

    public ItemGeneric(String name, int textLength, boolean hide) {
        super(new Item.Properties());
        this.setRegistryName(IceAndFire.MODID, name);
        this.description = textLength;
    }

    public ItemGeneric(String name, int textLength, int stacksize) {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS).maxStackSize(1));
        this.setRegistryName(IceAndFire.MODID, name);
        this.description = textLength;
    }

    public boolean hasEffect(ItemStack stack) {
        if (this == IafItemRegistry.CREATIVE_DRAGON_MEAL) {
            return true;
        } else {
            return super.hasEffect(stack);
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (description > 0) {
            for (int i = 0; i < description; i++) {
                tooltip.add(new TranslationTextComponent(this.getTranslationKey() + ".desc_" + i).mergeStyle(TextFormatting.GRAY));
            }
        }
    }
}
