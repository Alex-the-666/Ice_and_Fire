package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.core.ModItems;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemModShovel extends ItemSpade {

	public ItemModShovel(ToolMaterial toolmaterial, String gameName, String name) {
		super(toolmaterial);
		this.setUnlocalizedName(name);
		this.setCreativeTab(IceAndFire.TAB);
		this.setRegistryName(IceAndFire.MODID, gameName);
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		if (this == ModItems.silver_shovel) {
			if (target.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
				target.attackEntityFrom(DamageSource.MAGIC, 2);
			}
		}
		return super.hitEntity(stack, target, attacker);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (this == ModItems.silver_shovel)
			tooltip.add(TextFormatting.GREEN + StatCollector.translateToLocal("silvertools.hurt"));
	}
}
