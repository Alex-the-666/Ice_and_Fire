package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemHippocampusSlapper extends SwordItem {

    public ItemHippocampusSlapper() {
        super(IafItemRegistry.HIPPOCAMPUS_SWORD_TOOL_MATERIAL, 3, -2.4F, new Item.Properties().group(IceAndFire.TAB_ITEMS));
        this.setRegistryName(IceAndFire.MODID, "hippocampus_slapper");
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity targetEntity, LivingEntity attacker) {
        targetEntity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 100, 2));
        targetEntity.addPotionEffect(new EffectInstance(Effects.NAUSEA, 100, 2));
        targetEntity.playSound(SoundEvents.ENTITY_GUARDIAN_FLOP, 3, 1);

        return super.hitEntity(stack, targetEntity, attacker);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.iceandfire.legendary_weapon.desc").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.iceandfire.hippocampus_slapper.desc_0").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.iceandfire.hippocampus_slapper.desc_1").mergeStyle(TextFormatting.GRAY));
    }
}