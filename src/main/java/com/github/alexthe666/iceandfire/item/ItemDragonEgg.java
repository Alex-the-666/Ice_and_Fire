package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDragonEgg;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemDragonEgg extends Item {
    public EnumDragonEgg type;

    public ItemDragonEgg(String name, EnumDragonEgg type) {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS).maxStackSize(1));
        this.type = type;
        this.setRegistryName(IceAndFire.MODID, name);
    }

    public String getTranslationKey() {
        return "item.iceandfire.dragonegg";
    }

    @Override
    public void onCreated(ItemStack itemStack, World world, PlayerEntity player) {
        itemStack.setTag(new CompoundNBT());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("dragon." + type.toString().toLowerCase()).mergeStyle(type.color));
    }

    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack itemstack = context.getPlayer().getHeldItem(context.getHand());
        BlockPos offset = context.getPos().offset(context.getFace());
        EntityDragonEgg egg = new EntityDragonEgg(IafEntityRegistry.DRAGON_EGG, context.getWorld());
        egg.setEggType(type);
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
}
