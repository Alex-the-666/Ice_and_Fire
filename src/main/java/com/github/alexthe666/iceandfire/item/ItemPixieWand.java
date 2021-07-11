package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityPixieCharge;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemPixieWand extends Item {

    public ItemPixieWand() {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS).maxStackSize(1).maxDamage(500));
        this.setRegistryName(IceAndFire.MODID, "pixie_wand");
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
        ItemStack itemStackIn = playerIn.getHeldItem(hand);
        boolean flag = playerIn.isCreative() || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, itemStackIn) > 0;
        ItemStack itemstack = this.findAmmo(playerIn);
        playerIn.setActiveHand(hand);
        playerIn.swingArm(hand);
        if (!itemstack.isEmpty() || flag) {
            boolean flag1 = playerIn.isCreative() || this.isInfinite(itemstack, itemStackIn, playerIn);
            if (!flag1) {
                itemstack.shrink(1);
                if (itemstack.isEmpty()) {
                    playerIn.inventory.deleteStack(itemstack);
                }
            }
            double d2 = playerIn.getLookVec().x;
            double d3 = playerIn.getLookVec().y;
            double d4 = playerIn.getLookVec().z;
            float inaccuracy = 1.0F;
            d2 = d2 + playerIn.getRNG().nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
            d3 = d3 + playerIn.getRNG().nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
            d4 = d4 + playerIn.getRNG().nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
            EntityPixieCharge charge = new EntityPixieCharge(IafEntityRegistry.PIXIE_CHARGE, worldIn, playerIn, d2, d3, d4);
            charge.setPosition(playerIn.getPosX(), playerIn.getPosY() + 1, playerIn.getPosZ());
            if (!worldIn.isRemote) {
                worldIn.addEntity(charge);
            }
            playerIn.playSound(IafSoundRegistry.PIXIE_WAND, 1F, 0.75F + 0.5F * playerIn.getRNG().nextFloat());
            itemstack.damageItem(1, playerIn, (player) -> {
                player.sendBreakAnimation(playerIn.getActiveHand());
            });
            playerIn.getCooldownTracker().setCooldown(this, 5);
        }
        return new ActionResult<ItemStack>(ActionResultType.PASS, itemStackIn);
    }

    public boolean isInfinite(ItemStack stack, ItemStack bow, net.minecraft.entity.player.PlayerEntity player) {
        int enchant = net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bow);
        return enchant > 0 && stack.getItem() == IafItemRegistry.PIXIE_DUST;
    }

    private ItemStack findAmmo(PlayerEntity player) {
        if (this.isAmmo(player.getHeldItem(Hand.OFF_HAND))) {
            return player.getHeldItem(Hand.OFF_HAND);
        } else if (this.isAmmo(player.getHeldItem(Hand.MAIN_HAND))) {
            return player.getHeldItem(Hand.MAIN_HAND);
        } else {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = player.inventory.getStackInSlot(i);

                if (this.isAmmo(itemstack)) {
                    return itemstack;
                }
            }

            return ItemStack.EMPTY;
        }
    }

    protected boolean isAmmo(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == IafItemRegistry.PIXIE_DUST;
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.iceandfire.legendary_weapon.desc").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.iceandfire.pixie_wand.desc_0").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.iceandfire.pixie_wand.desc_1").mergeStyle(TextFormatting.GRAY));
    }
}
