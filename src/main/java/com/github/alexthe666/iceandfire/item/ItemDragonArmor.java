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

public class ItemDragonArmor extends Item implements ICustomRendered {

    public int type;
    public int dragonSlot;
    public String name;

    public ItemDragonArmor(int type, int dragonSlot, String name) {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS).maxStackSize(1));
        this.type = type;
        this.dragonSlot = dragonSlot;
        this.name = name;
        this.setRegistryName(IceAndFire.MODID, name + "_" + getNameForSlot(dragonSlot));

    }

    public String getTranslationKey() {
        return "item.iceandfire." + name;
    }

    private String getNameForSlot(int slot){
        switch (slot){
            case 0:
                return "head";
            case 1:
                return "neck";
            case 2:
                return "body";
            case 3:
                return "tail";
        }
        return "";
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        String words;
        switch (dragonSlot) {
            default:
                words = "dragon.armor_head";
                break;
            case 1:
                words = "dragon.armor_neck";
                break;
            case 2:
                words = "dragon.armor_body";
                break;
            case 3:
                words = "dragon.armor_tail";
                break;
        }
        tooltip.add(new TranslationTextComponent(words).mergeStyle(TextFormatting.GRAY));
    }
}
