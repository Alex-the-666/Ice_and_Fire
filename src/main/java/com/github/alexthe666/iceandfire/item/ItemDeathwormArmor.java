package com.github.alexthe666.iceandfire.item;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemDeathwormArmor extends ArmorItem {

    public ItemDeathwormArmor(IArmorMaterial material, EquipmentSlotType slot, String name) {
        super(material, slot, new Item.Properties().group(IceAndFire.TAB_ITEMS));
        this.setRegistryName(IceAndFire.MODID, name);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity LivingEntity, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
        return (A) IceAndFire.PROXY.getArmorModel(slot == EquipmentSlotType.LEGS ? 5 : 4);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        if (this.getArmorMaterial() == IafItemRegistry.DEATHWORM_2_ARMOR_MATERIAL) {
            return "iceandfire:textures/models/armor/armor_deathworm_red" + (slot == EquipmentSlotType.LEGS ? "_legs.png" : ".png");
        } else if (this.getArmorMaterial() == IafItemRegistry.DEATHWORM_1_ARMOR_MATERIAL) {
            return "iceandfire:textures/models/armor/armor_deathworm_white" + (slot == EquipmentSlotType.LEGS ? "_legs.png" : ".png");
        } else {
            return "iceandfire:textures/models/armor/armor_deathworm_yellow" + (slot == EquipmentSlotType.LEGS ? "_legs.png" : ".png");
        }
    }
}
