package com.github.alexthe666.iceandfire.enums;

import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;
import com.github.alexthe666.iceandfire.block.BlockSeaSerpentScales;
import com.github.alexthe666.iceandfire.item.IafArmorMaterial;
import com.github.alexthe666.iceandfire.item.ItemSeaSerpentArmor;
import com.github.alexthe666.iceandfire.item.ItemSeaSerpentScales;

import net.minecraft.block.Block;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;

import java.util.Locale;

public enum EnumSeaSerpent {
    BLUE(TextFormatting.BLUE),
    BRONZE(TextFormatting.GOLD),
    DEEPBLUE(TextFormatting.DARK_BLUE),
    GREEN(TextFormatting.DARK_GREEN),
    PURPLE(TextFormatting.DARK_PURPLE),
    RED(TextFormatting.DARK_RED),
    TEAL(TextFormatting.AQUA);

    public String resourceName;
    public TextFormatting color;
    public CustomArmorMaterial armorMaterial;
    public Item scale;
    public Item helmet;
    public Item chestplate;
    public Item leggings;
    public Item boots;
    public Block scaleBlock;

    EnumSeaSerpent(TextFormatting color) {
        this.resourceName = this.name().toLowerCase(Locale.ROOT);
        this.color = color;
        this.scaleBlock = new BlockSeaSerpentScales(this.resourceName, this.color);
    }


    public static void initArmors() {
        for (EnumSeaSerpent color : EnumSeaSerpent.values()) {
            color.armorMaterial = new IafArmorMaterial("iceandfire:sea_serpent_scales_" + color.resourceName, 30, new int[]{4, 8, 7, 4}, 25, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 2.5F);
            color.scale = new ItemSeaSerpentScales(color.resourceName, color.color);
            color.helmet = new ItemSeaSerpentArmor(color, color.armorMaterial, EquipmentSlotType.HEAD);
            color.chestplate = new ItemSeaSerpentArmor(color, color.armorMaterial, EquipmentSlotType.CHEST);
            color.leggings = new ItemSeaSerpentArmor(color, color.armorMaterial, EquipmentSlotType.LEGS);
            color.boots = new ItemSeaSerpentArmor(color, color.armorMaterial, EquipmentSlotType.FEET);
            color.helmet.setRegistryName("iceandfire:tide_" + color.resourceName + "_helmet");
            color.chestplate.setRegistryName("iceandfire:tide_" + color.resourceName + "_chestplate");
            color.leggings.setRegistryName("iceandfire:tide_" + color.resourceName + "_leggings");
            color.boots.setRegistryName("iceandfire:tide_" + color.resourceName + "_boots");
        }
    }
}
