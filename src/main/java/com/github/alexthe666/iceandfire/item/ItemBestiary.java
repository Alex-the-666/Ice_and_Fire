package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.enums.EnumBestiaryPages;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemBestiary extends Item {

    public ItemBestiary() {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS).maxStackSize(1));
        this.setRegistryName(IceAndFire.MODID, "bestiary");
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        stack.setTag(new CompoundNBT());
        stack.getTag().putIntArray("Pages", new int[]{0});

    }

    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            items.add(new ItemStack(this));
            ItemStack stack = new ItemStack(IafItemRegistry.BESTIARY);
            stack.setTag(new CompoundNBT());
            int[] pages = new int[EnumBestiaryPages.values().length];
            for (int i = 0; i < EnumBestiaryPages.values().length; i++) {
                pages[i] = i;
            }
            stack.getTag().putIntArray("Pages", pages);
            items.add(stack);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemStackIn = playerIn.getHeldItem(handIn);
        if (worldIn.isRemote) {
            IceAndFire.PROXY.openBestiaryGui(itemStackIn);
        }
        return new ActionResult<ItemStack>(ActionResultType.PASS, itemStackIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (stack.getTag() == null) {
            stack.setTag(new CompoundNBT());
            stack.getTag().putIntArray("Pages", new int[]{EnumBestiaryPages.INTRODUCTION.ordinal()});

        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.getTag() != null) {
            if (IceAndFire.PROXY.shouldSeeBestiaryContents()) {
                tooltip.add(new TranslationTextComponent("bestiary.contains").mergeStyle(TextFormatting.GRAY));
                List<EnumBestiaryPages> pages = EnumBestiaryPages.containedPages(EnumBestiaryPages.toList(stack.getTag().getIntArray("Pages")));
                for (EnumBestiaryPages page : pages) {
                    tooltip.add(new StringTextComponent(TextFormatting.WHITE + "-").appendSibling(new TranslationTextComponent("bestiary." + EnumBestiaryPages.values()[page.ordinal()].toString().toLowerCase())).mergeStyle(TextFormatting.GRAY));
                }
            } else {
                tooltip.add(new TranslationTextComponent("bestiary.hold_shift").mergeStyle(TextFormatting.GRAY));
            }

        }
    }

}
