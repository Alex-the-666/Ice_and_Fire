package com.github.alexthe666.iceandfire.item;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.enums.EnumTroll;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemTrollWeapon extends SwordItem implements ICustomRendered {

    public EnumTroll.Weapon weapon = EnumTroll.Weapon.AXE;

    public ItemTrollWeapon(EnumTroll.Weapon weapon) {
        super(IafItemRegistry.TROLL_WEAPON_TOOL_MATERIAL, 15, -3.5F, IceAndFire.PROXY.setupISTER(new Item.Properties().group(IceAndFire.TAB_ITEMS)));
        this.setRegistryName(IceAndFire.MODID, "troll_weapon_" + weapon.name().toLowerCase(Locale.ROOT));
        this.weapon = weapon;
    }

    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        return player.getCooledAttackStrength(0) < 0.95 || player.swingProgress != 0;
    }

    public boolean onEntitySwing(LivingEntity LivingEntity, ItemStack stack) {
        if (LivingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) LivingEntity;
            if (player.getCooledAttackStrength(0) < 1 && player.swingProgress > 0) {
                return true;
            } else {
                player.swingProgressInt = -1;
            }
        }
        return false;
    }

    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (entityIn instanceof PlayerEntity && isSelected) {
            PlayerEntity player = (PlayerEntity) entityIn;
            if (player.getCooledAttackStrength(0) < 0.95 && player.swingProgress > 0) {
                player.swingProgressInt--;
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.iceandfire.legendary_weapon.desc").mergeStyle(TextFormatting.GRAY));
    }

}
