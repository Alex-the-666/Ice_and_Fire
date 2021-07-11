package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityDragonSkull;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemDragonSkull extends Item implements ICustomRendered {
    private int dragonType;
    public ItemDragonSkull(int dragonType) {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS).maxStackSize(1));
        this.dragonType = dragonType;
        this.setRegistryName(IceAndFire.MODID, "dragon_skull_" + getType(dragonType));
    }

    private String getType(int type){
        if(type == 2){
            return "lightning";
        }else if (type == 1){
            return "ice";
        }else{
            return "fire";
        }
    }

    @Override
    public void onCreated(ItemStack itemStack, World world, PlayerEntity player) {
        itemStack.setTag(new CompoundNBT());
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (stack.getTag() == null) {
            stack.setTag(new CompoundNBT());
            stack.getTag().putInt("Stage", 4);
            stack.getTag().putInt("DragonAge", 75);
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        String iceorfire = "dragon." + getType(dragonType);
        tooltip.add(new TranslationTextComponent(iceorfire).mergeStyle(TextFormatting.GRAY));
        if (stack.getTag() != null) {
            tooltip.add(new TranslationTextComponent("dragon.stage").mergeStyle(TextFormatting.GRAY).appendSibling(new StringTextComponent( " " + stack.getTag().getInt("Stage"))));
        }
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack stack = context.getPlayer().getHeldItem(context.getHand());
        /*
         * EntityDragonEgg egg = new EntityDragonEgg(worldIn);
         * egg.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() +
         * 0.5); if(!worldIn.isRemote){ worldIn.spawnEntityInWorld(egg); }
         */
        if (stack.getTag() != null) {
            EntityDragonSkull skull = new EntityDragonSkull(IafEntityRegistry.DRAGON_SKULL, context.getWorld());
            skull.setDragonType(dragonType);
            skull.setStage(stack.getTag().getInt("Stage"));
            skull.setDragonAge(stack.getTag().getInt("DragonAge"));
            BlockPos offset = context.getPos().offset(context.getFace(), 1);
            skull.setLocationAndAngles(offset.getX() + 0.5, offset.getY(), offset.getZ() + 0.5, 0, 0);
            float yaw = context.getPlayer().rotationYaw;
            if (context.getFace() != Direction.UP) {
                yaw = context.getPlayer().getHorizontalFacing().getHorizontalAngle();
            }
            skull.setYaw(yaw);
            if (stack.hasDisplayName()) {
                skull.setCustomName(stack.getDisplayName());
            }
            if (!context.getWorld().isRemote) {
                context.getWorld().addEntity(skull);
            }
            if (!context.getPlayer().isCreative()) {
                stack.shrink(1);
            }
        }
        return ActionResultType.SUCCESS;

    }
}
