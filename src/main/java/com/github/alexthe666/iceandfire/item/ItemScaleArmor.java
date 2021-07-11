package com.github.alexthe666.iceandfire.item;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.enums.EnumDragonArmor;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;

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

public class ItemScaleArmor extends ArmorItem implements IProtectAgainstDragonItem {

    public EnumDragonArmor armor_type;
    public EnumDragonEgg eggType;

    public ItemScaleArmor(EnumDragonEgg eggType, EnumDragonArmor armorType, CustomArmorMaterial material, EquipmentSlotType slot) {
        super(material, slot, new Item.Properties().group(IceAndFire.TAB_ITEMS));
        this.armor_type = armorType;
        this.eggType = eggType;
    }

    public String getTranslationKey() {
        switch (this.slot){
            case HEAD:
                return "item.iceandfire.dragon_helmet";
            case CHEST:
                return "item.iceandfire.dragon_chestplate";
            case LEGS:
                return "item.iceandfire.dragon_leggings";
            case FEET:
                return "item.iceandfire.dragon_boots";
        }
        return "item.iceandfire.dragon_helmet";
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity LivingEntity, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
        int dragonType = DragonType.getIntFromType(armor_type.eggType.dragonType);
        if(dragonType == 0){
            return (A) IceAndFire.PROXY.getArmorModel((slot == EquipmentSlotType.LEGS ? 1 : 0));
        }else if(dragonType == 1){
            return (A) IceAndFire.PROXY.getArmorModel((slot == EquipmentSlotType.LEGS ? 3 : 2));
        }else if(dragonType == 2){
            return (A) IceAndFire.PROXY.getArmorModel((slot == EquipmentSlotType.LEGS ? 19 : 18));
        }else{
            return null;
        }

    }

    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return "iceandfire:textures/models/armor/" + armor_type.name() + (slot == EquipmentSlotType.LEGS ? "_legs.png" : ".png");
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("dragon." + eggType.toString().toLowerCase()).mergeStyle(eggType.color));
        tooltip.add(new TranslationTextComponent("item.dragonscales_armor.desc").mergeStyle(TextFormatting.GRAY));
    }
}
