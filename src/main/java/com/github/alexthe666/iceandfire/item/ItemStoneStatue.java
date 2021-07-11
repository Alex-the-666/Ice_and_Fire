package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityStoneStatue;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemStoneStatue extends Item {

    public ItemStoneStatue() {
        super(new Item.Properties().maxStackSize(1));
        this.setRegistryName(IceAndFire.MODID, "stone_statue");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.getTag() != null) {
            boolean isPlayer = stack.getTag().getBoolean("IAFStoneStatuePlayerEntity");
            String id = stack.getTag().getString("IAFStoneStatueEntityID");
            if (EntityType.byKey(id).orElse(null) != null) {
                EntityType type = EntityType.byKey(id).orElse(null);
                TranslationTextComponent untranslated = isPlayer ? new TranslationTextComponent("entity.player.name") : new TranslationTextComponent(type.getTranslationKey());
                tooltip.add(untranslated.mergeStyle(TextFormatting.GRAY));
            }
        }
    }

    @Override
    public void onCreated(ItemStack itemStack, World world, PlayerEntity player) {
        itemStack.setTag(new CompoundNBT());
        itemStack.getTag().putBoolean("IAFStoneStatuePlayerEntity", true);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (context.getFace() != Direction.UP) {
            return ActionResultType.FAIL;
        } else {
            ItemStack stack = context.getPlayer().getHeldItem(context.getHand());
            if (stack.getTag() != null) {
                String id = stack.getTag().getString("IAFStoneStatueEntityID");
                CompoundNBT statueNBT = stack.getTag().getCompound("IAFStoneStatueNBT");
                EntityStoneStatue statue = new EntityStoneStatue(IafEntityRegistry.STONE_STATUE, context.getWorld());
                statue.readAdditional(statueNBT);
                statue.setTrappedEntityTypeString(id);
                double d1 = context.getPlayer().getPosX() - (context.getPos().getX() + 0.5);
                double d2 = context.getPlayer().getPosZ() - (context.getPos().getZ() + 0.5);
                float yaw = (float)(MathHelper.atan2(d2, d1) * (double)(180F / (float)Math.PI)) - 90;
                statue.prevRotationYaw = yaw;
                statue.rotationYaw = yaw;
                statue.rotationYawHead = yaw;
                statue.renderYawOffset = yaw;
                statue.prevRenderYawOffset = yaw;
                statue.setPositionAndRotation(context.getPos().getX() + 0.5, context.getPos().getY() + 1, context.getPos().getZ() + 0.5, yaw, 0);
                if (!context.getWorld().isRemote) {
                    context.getWorld().addEntity(statue);
                    statue.readAdditional(stack.getTag());
                }
                statue.setCrackAmount(0);

                if (!context.getPlayer().isCreative()) {
                    stack.shrink(1);
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.SUCCESS;
    }
}
