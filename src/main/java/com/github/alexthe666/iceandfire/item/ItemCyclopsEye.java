package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemCyclopsEye extends Item {

    public ItemCyclopsEye() {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS).maxDamage(500));
        this.setRegistryName(IceAndFire.MODID, "cyclops_eye");
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.isItemEqual(newStack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (stack.getTag() == null) {
            stack.setTag(new CompoundNBT());
        } else {
            if (entity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) entity;
                if (living.getHeldItemMainhand() == stack || living.getHeldItemOffhand() == stack) {
                    double range = 15;
                    boolean inflictedDamage = false;
                    for (MobEntity LivingEntity : world.getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB(living.getPosX() - range, living.getPosY() - range, living.getPosZ() - range, living.getPosX() + range, living.getPosY() + range, living.getPosZ() + range))) {
                        if (!LivingEntity.isEntityEqual(living) && !LivingEntity.isOnSameTeam(living) && (LivingEntity.getAttackTarget() == living || LivingEntity.getRevengeTarget() == living || LivingEntity instanceof IMob)) {
                            LivingEntity.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 10, 1));
                            inflictedDamage = true;
                        }
                    }
                    if (inflictedDamage) {
                        stack.getTag().putInt("HurtingTicks", stack.getTag().getInt("HurtingTicks") + 1);
                    }
                }
                if (stack.getTag().getInt("HurtingTicks") > 120) {
                    stack.damageItem(1, (LivingEntity) entity, (p_220017_1_) -> {
                    });
                    stack.getTag().putInt("HurtingTicks", 0);
                }
            }

        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.iceandfire.legendary_weapon.desc").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.iceandfire.cyclops_eye.desc_0").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.iceandfire.cyclops_eye.desc_1").mergeStyle(TextFormatting.GRAY));
    }
}
