package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexEgg;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemMyrmexEgg extends Item implements ICustomRendered {

    boolean isJungle;

    public ItemMyrmexEgg(boolean isJungle) {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS).maxStackSize(1));
        this.isJungle = isJungle;
        this.setRegistryName(IceAndFire.MODID, isJungle ? "myrmex_jungle_egg" : "myrmex_desert_egg");
    }

    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            for (int i = 0; i < 5; i++) {
                ItemStack stack = new ItemStack(this);
                CompoundNBT tag = new CompoundNBT();
                tag.putInt("EggOrdinal", i);
                stack.setTag(tag);
                items.add(stack);
            }
        }

    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        String caste;
        CompoundNBT tag = stack.getTag();
        int eggOrdinal = 0;
        if (tag != null) {
            eggOrdinal = tag.getInt("EggOrdinal");
        }
        switch (eggOrdinal) {
            default:
                caste = "worker";
                break;
            case 1:
                caste = "soldier";
                break;
            case 2:
                caste = "royal";
                break;
            case 3:
                caste = "sentinel";
                break;
            case 4:
                caste = "queen";
        }
        if (eggOrdinal == 4) {
            tooltip.add(new TranslationTextComponent("myrmex.caste_" + caste + ".name").mergeStyle(TextFormatting.LIGHT_PURPLE));
        } else {
            tooltip.add(new TranslationTextComponent("myrmex.caste_" + caste + ".name").mergeStyle(TextFormatting.GRAY));
        }
    }

    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack itemstack = context.getPlayer().getHeldItem(context.getHand());
        BlockPos offset = context.getPos().offset(context.getFace());
        EntityMyrmexEgg egg = new EntityMyrmexEgg(IafEntityRegistry.MYRMEX_EGG, context.getWorld());
        CompoundNBT tag = itemstack.getTag();
        int eggOrdinal = 0;
        if (tag != null) {
            eggOrdinal = tag.getInt("EggOrdinal");
        }
        egg.setJungle(isJungle);
        egg.setMyrmexCaste(eggOrdinal);
        egg.setLocationAndAngles(offset.getX() + 0.5, offset.getY(), offset.getZ() + 0.5, 0, 0);
        egg.onPlayerPlace(context.getPlayer());
        if (itemstack.hasDisplayName()) {
            egg.setCustomName(itemstack.getDisplayName());
        }
        if (!context.getWorld().isRemote) {
            context.getWorld().addEntity(egg);
        }
        itemstack.shrink(1);
        return ActionResultType.SUCCESS;
    }

    public boolean hasEffect(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        int eggOrdinal = 0;
        if (tag != null) {
            eggOrdinal = tag.getInt("EggOrdinal");
        }
        return super.hasEffect(stack) || eggOrdinal == 4;
    }
}
