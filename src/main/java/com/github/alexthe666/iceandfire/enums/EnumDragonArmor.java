package com.github.alexthe666.iceandfire.enums;

import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;
import com.github.alexthe666.iceandfire.item.IafArmorMaterial;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.item.ItemScaleArmor;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvents;

public enum EnumDragonArmor {

    armor_red(12, EnumDragonEgg.RED),
    armor_bronze(13, EnumDragonEgg.BRONZE),
    armor_green(14, EnumDragonEgg.GREEN),
    armor_gray(15, EnumDragonEgg.GRAY),
    armor_blue(12, EnumDragonEgg.BLUE),
    armor_white(13, EnumDragonEgg.WHITE),
    armor_sapphire(14, EnumDragonEgg.SAPPHIRE),
    armor_silver(15, EnumDragonEgg.SILVER),
    armor_electric(12, EnumDragonEgg.ELECTRIC),
    armor_amythest(13, EnumDragonEgg.AMYTHEST),
    armor_copper(14, EnumDragonEgg.COPPER),
    armor_black(15, EnumDragonEgg.BLACK);

    public CustomArmorMaterial material;
    public int armorId;
    public EnumDragonEgg eggType;
    public Item helmet;
    public Item chestplate;
    public Item leggings;
    public Item boots;
    public CustomArmorMaterial armorMaterial;

    EnumDragonArmor(int armorId, EnumDragonEgg eggType) {
        this.armorId = armorId;
        this.eggType = eggType;
    }

    public static void initArmors() {
        for (int i = 0; i < EnumDragonArmor.values().length; i++) {
            EnumDragonArmor.values()[i].armorMaterial = new IafArmorMaterial("iceandfire:armor_dragon_scales" + (i + 1), 36, new int[]{5, 7, 9, 5}, 15, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 2);
            String sub = "iceandfire:" + EnumDragonArmor.values()[i].name();
            EnumDragonArmor.values()[i].helmet = new ItemScaleArmor(EnumDragonArmor.values()[i].eggType, EnumDragonArmor.values()[i], EnumDragonArmor.values()[i].armorMaterial, EquipmentSlotType.HEAD);
            EnumDragonArmor.values()[i].chestplate = new ItemScaleArmor(EnumDragonArmor.values()[i].eggType, EnumDragonArmor.values()[i], EnumDragonArmor.values()[i].armorMaterial, EquipmentSlotType.CHEST);
            EnumDragonArmor.values()[i].leggings = new ItemScaleArmor(EnumDragonArmor.values()[i].eggType, EnumDragonArmor.values()[i], EnumDragonArmor.values()[i].armorMaterial, EquipmentSlotType.LEGS);
            EnumDragonArmor.values()[i].boots = new ItemScaleArmor(EnumDragonArmor.values()[i].eggType, EnumDragonArmor.values()[i], EnumDragonArmor.values()[i].armorMaterial, EquipmentSlotType.FEET);
            EnumDragonArmor.values()[i].helmet.setRegistryName(sub + "_helmet");
            //GameRegistry.register(EnumDragonArmor.values()[i].helmet);
            EnumDragonArmor.values()[i].chestplate.setRegistryName(sub + "_chestplate");
            //GameRegistry.register(EnumDragonArmor.values()[i].chestplate);
            EnumDragonArmor.values()[i].leggings.setRegistryName(sub + "_leggings");
            //GameRegistry.register(EnumDragonArmor.values()[i].leggings);
            EnumDragonArmor.values()[i].boots.setRegistryName(sub + "_boots");
            //GameRegistry.register(EnumDragonArmor.values()[i].boots);
        }
    }

    public static Item getScaleItem(EnumDragonArmor armor) {
        switch (armor) {
            case armor_red:
                return IafItemRegistry.DRAGONSCALES_RED;
            case armor_bronze:
                return IafItemRegistry.DRAGONSCALES_BRONZE;
            case armor_green:
                return IafItemRegistry.DRAGONSCALES_GREEN;
            case armor_gray:
                return IafItemRegistry.DRAGONSCALES_GRAY;
            case armor_blue:
                return IafItemRegistry.DRAGONSCALES_BLUE;
            case armor_white:
                return IafItemRegistry.DRAGONSCALES_WHITE;
            case armor_sapphire:
                return IafItemRegistry.DRAGONSCALES_SAPPHIRE;
            case armor_silver:
                return IafItemRegistry.DRAGONSCALES_SILVER;
            case armor_electric:
                return IafItemRegistry.DRAGONSCALES_ELECTRIC;
            case armor_amythest:
                return IafItemRegistry.DRAGONSCALES_AMYTHEST;
            case armor_copper:
                return IafItemRegistry.DRAGONSCALES_COPPER;
            case armor_black:
                return IafItemRegistry.DRAGONSCALES_BLACK;
            default:
                return IafItemRegistry.DRAGONSCALES_RED;
        }
    }
}
