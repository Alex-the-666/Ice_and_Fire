package com.github.alexthe666.iceandfire.item;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemModArmor extends ArmorItem {

    public ItemModArmor(IArmorMaterial material, EquipmentSlotType slot, String name) {
        super(material, slot, new Item.Properties().group(IceAndFire.TAB_ITEMS));
        this.setRegistryName(IceAndFire.MODID, name);
    }

    public String getTranslationKey(ItemStack stack) {
        if (this == IafItemRegistry.EARPLUGS) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            if (calendar.get(2) + 1 == 4 && calendar.get(5) == 1) {
                return "item.iceandfire.air_pods";
            }
        }
        return super.getTranslationKey(stack);
    }

    @Nullable
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        if(this.material == IafItemRegistry.MYRMEX_DESERT_ARMOR_MATERIAL){
            return "iceandfire:textures/models/armor/" + (slot == EquipmentSlotType.LEGS ? "myrmex_desert_layer_2" : "myrmex_desert_layer_1") + ".png";
        }
        if(this.material == IafItemRegistry.MYRMEX_JUNGLE_ARMOR_MATERIAL){
            return "iceandfire:textures/models/armor/" + (slot == EquipmentSlotType.LEGS ? "myrmex_jungle_layer_2" : "myrmex_jungle_layer_1") + ".png";
        }
        if(this.material == IafItemRegistry.SHEEP_ARMOR_MATERIAL){
            return "iceandfire:textures/models/armor/" + (slot == EquipmentSlotType.LEGS ? "sheep_disguise_layer_2" : "sheep_disguise_layer_1") + ".png";
        }
        if(this.material == IafItemRegistry.EARPLUGS_ARMOR_MATERIAL){
            return "iceandfire:textures/models/armor/earplugs_layer_1.png";
        }
        return null;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (this == IafItemRegistry.EARPLUGS) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            if (calendar.get(2) + 1 == 4 && calendar.get(5) == 1) {
                tooltip.add(new TranslationTextComponent("item.iceandfire.air_pods.desc").mergeStyle(TextFormatting.GREEN));
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
