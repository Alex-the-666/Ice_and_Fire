package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDreadLichSkull;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class ItemLichStaff extends Item {

    public ItemLichStaff() {
        super(new Item.Properties().maxDamage(100).group(IceAndFire.TAB_ITEMS));
        this.setRegistryName("iceandfire:lich_staff");
    }

    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == IafItemRegistry.DREAD_SHARD || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
        ItemStack itemStackIn = playerIn.getHeldItem(hand);
        if(!worldIn.isRemote){
            playerIn.setActiveHand(hand);
            playerIn.swingArm(hand);
            double d2 = playerIn.getLookVec().x;
            double d3 = playerIn.getLookVec().y;
            double d4 = playerIn.getLookVec().z;
            float inaccuracy = 1.0F;
            d2 = d2 + playerIn.getRNG().nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
            d3 = d3 + playerIn.getRNG().nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
            d4 = d4 + playerIn.getRNG().nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
            EntityDreadLichSkull charge = new EntityDreadLichSkull(IafEntityRegistry.DREAD_LICH_SKULL, worldIn, playerIn, 6);
            charge.shoot( playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 7.0F, 1.0F);
            charge.setPosition(playerIn.getPosX(), playerIn.getPosY() + 1, playerIn.getPosZ());
            worldIn.addEntity(charge);
            charge.shoot(d2, d3, d4, 1, 1);
            playerIn.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 1F, 0.75F + 0.5F * playerIn.getRNG().nextFloat());
            itemStackIn.damageItem(1, playerIn, (player) -> {
                player.sendBreakAnimation(hand);
            });
            playerIn.getCooldownTracker().setCooldown(this, 4);
        }
        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemStackIn);
    }
}