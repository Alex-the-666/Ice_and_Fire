package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityMobSkull;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.enums.EnumSkullType;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class ItemMobSkull extends Item implements ICustomRendered {

    private EnumSkullType skull;

    public ItemMobSkull(EnumSkullType skull) {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS).maxStackSize(1));
        this.setRegistryName(IceAndFire.MODID, skull.itemResourceName);
        this.skull = skull;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        EntityMobSkull skull = new EntityMobSkull(IafEntityRegistry.MOB_SKULL, context.getWorld());
        ItemStack stack = player.getHeldItem(context.getHand());
        BlockPos offset = context.getPos().offset(context.getFace(), 1);
        skull.setLocationAndAngles(offset.getX() + 0.5, offset.getY(), offset.getZ() + 0.5, 0, 0);
        float yaw = player.rotationYaw;
        if (context.getFace() != Direction.UP) {
            yaw = player.getHorizontalFacing().getHorizontalAngle();
        }
        skull.setYaw(yaw);
        skull.setSkullType(this.skull);
        if (!context.getWorld().isRemote) {
            context.getWorld().addEntity(skull);
        }
        if (stack.hasDisplayName()) {
            skull.setCustomName(stack.getDisplayName());
        }
        if (!player.isCreative()) {
            stack.shrink(1);
        }
        return ActionResultType.SUCCESS;
    }
}
