package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.enums.EnumSeaSerpent;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemSeaSerpentArmor extends ArmorItem {

    public EnumSeaSerpent armor_type;

    public ItemSeaSerpentArmor(EnumSeaSerpent armorType, CustomArmorMaterial material, EquipmentSlotType slot) {
        super(material, slot, new Item.Properties().group(IceAndFire.TAB_ITEMS));
        this.armor_type = armorType;
    }

    public String getTranslationKey() {
        switch (this.slot){
            case HEAD:
                return "item.iceandfire.sea_serpent_helmet";
            case CHEST:
                return "item.iceandfire.sea_serpent_chestplate";
            case LEGS:
                return "item.iceandfire.sea_serpent_leggings";
            case FEET:
                return "item.iceandfire.sea_serpent_boots";
        }
        return "item.iceandfire.sea_serpent_helmet";
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity LivingEntity, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
        return (A) IceAndFire.PROXY.getArmorModel(slot == EquipmentSlotType.LEGS ? 9 : 8);
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return "iceandfire:textures/models/armor/armor_tide_" + armor_type.resourceName + (slot == EquipmentSlotType.LEGS ? "_legs.png" : ".png");
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

        tooltip.add(new TranslationTextComponent("sea_serpent." + armor_type.resourceName).mergeStyle(armor_type.color));
        tooltip.add(new TranslationTextComponent("item.iceandfire.sea_serpent_armor.desc_0").mergeStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.iceandfire.sea_serpent_armor.desc_1").mergeStyle(TextFormatting.GRAY));
    }
}
