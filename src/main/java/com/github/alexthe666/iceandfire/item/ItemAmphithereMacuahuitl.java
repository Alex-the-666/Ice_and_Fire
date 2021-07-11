package com.github.alexthe666.iceandfire.item;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemAmphithereMacuahuitl extends SwordItem {

    public ItemAmphithereMacuahuitl() {
        super(IafItemRegistry.AMPHITHERE_SWORD_TOOL_MATERIAL, 3, -2.4F, new Item.Properties().group(IceAndFire.TAB_ITEMS));
        this.setRegistryName(IceAndFire.MODID, "amphithere_macuahuitl");
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity targetEntity, LivingEntity attacker) {
        targetEntity.playSound(IafSoundRegistry.AMPHITHERE_GUST, 1, 1);
        targetEntity.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1, 1);
        targetEntity.isAirBorne = true;
        double xRatio = -MathHelper.sin(attacker.rotationYaw * 0.017453292F);
        double zRatio = MathHelper.cos(attacker.rotationYaw * 0.017453292F);
        float strength = -0.6F;
        float f = MathHelper.sqrt(xRatio * xRatio + zRatio * zRatio);
        targetEntity.setMotion((targetEntity.getMotion().x / 2) - xRatio / (double) f * (double) strength, 0.8D, (targetEntity.getMotion().z / 2) - zRatio / (double) f * (double) strength);
        Random rand = new Random();
        for (int i = 0; i < 20; ++i) {
            double d0 = rand.nextGaussian() * 0.02D;
            double d1 = rand.nextGaussian() * 0.02D;
            double d2 = rand.nextGaussian() * 0.02D;
            double d3 = 10.0D;
            targetEntity.world.addParticle(ParticleTypes.CLOUD, targetEntity.getPosX() + (double) (rand.nextFloat() * targetEntity.getWidth() * 5.0F) - (double) targetEntity.getWidth() - d0 * 10.0D, targetEntity.getPosY() + (double) (rand.nextFloat() * targetEntity.getHeight()) - d1 * 10.0D, targetEntity.getPosZ() + (double) (rand.nextFloat() * targetEntity.getWidth() * 5.0F) - (double) targetEntity.getWidth() - d2 * 10.0D, d0, d1, d2);
        }
        return super.hitEntity(stack, targetEntity, attacker);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.iceandfire.legendary_weapon.desc").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.iceandfire.amphithere_macuahuitl.desc_0").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.iceandfire.amphithere_macuahuitl.desc_1").mergeStyle(TextFormatting.GRAY));
    }
}
