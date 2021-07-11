package com.github.alexthe666.iceandfire.item;

import java.lang.reflect.Field;
import java.util.Locale;

import com.github.alexthe666.citadel.server.item.CustomArmorMaterial;
import com.github.alexthe666.citadel.server.item.CustomToolMaterial;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.enums.EnumDragonArmor;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import com.github.alexthe666.iceandfire.enums.EnumSeaSerpent;
import com.github.alexthe666.iceandfire.enums.EnumSkullType;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import com.github.alexthe666.iceandfire.recipe.IafRecipeRegistry;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IceAndFire.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IafItemRegistry {

    public static CustomArmorMaterial SILVER_ARMOR_MATERIAL = new IafArmorMaterial("silver", 15, new int[]{1, 4, 5, 2}, 20, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0);
    public static CustomArmorMaterial COPPER_ARMOR_MATERIAL = new IafArmorMaterial("copper", 10, new int[]{1, 3, 4, 2}, 15, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0);
    public static CustomArmorMaterial BLINDFOLD_ARMOR_MATERIAL = new IafArmorMaterial("blindfold", 5, new int[]{1, 1, 1, 1}, 10, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0);
    public static CustomArmorMaterial SHEEP_ARMOR_MATERIAL = new IafArmorMaterial("sheep", 5, new int[]{1, 3, 2, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0);
    public static CustomArmorMaterial MYRMEX_DESERT_ARMOR_MATERIAL = new IafArmorMaterial("myrmexdesert", 20, new int[]{3, 5, 8, 4}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0);
    public static CustomArmorMaterial MYRMEX_JUNGLE_ARMOR_MATERIAL = new IafArmorMaterial("myrmexjungle", 20, new int[]{3, 5, 8, 4}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0);
    public static CustomArmorMaterial EARPLUGS_ARMOR_MATERIAL = new IafArmorMaterial("earplugs", 5, new int[]{1, 1, 1, 1}, 10, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0);
    public static CustomArmorMaterial DEATHWORM_0_ARMOR_MATERIAL = new IafArmorMaterial("yellow seathworm", 15, new int[]{2, 5, 7, 3}, 5, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.5F);
    public static CustomArmorMaterial DEATHWORM_1_ARMOR_MATERIAL = new IafArmorMaterial("white seathworm", 15, new int[]{2, 5, 7, 3}, 5, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.5F);
    public static CustomArmorMaterial DEATHWORM_2_ARMOR_MATERIAL = new IafArmorMaterial("red deathworm", 15, new int[]{2, 5, 7, 3}, 5, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.5F);
    public static CustomArmorMaterial TROLL_MOUNTAIN_ARMOR_MATERIAL = new IafArmorMaterial("mountain troll", 20, new int[]{2, 5, 7, 3}, 10, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1F);
    public static CustomArmorMaterial TROLL_FOREST_ARMOR_MATERIAL = new IafArmorMaterial("forest troll", 20, new int[]{2, 5, 7, 3}, 10, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1F);
    public static CustomArmorMaterial TROLL_FROST_ARMOR_MATERIAL = new IafArmorMaterial("frost troll", 20, new int[]{2, 5, 7, 3}, 10, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1F);
    public static CustomArmorMaterial DRAGONSTEEL_FIRE_ARMOR_MATERIAL = new DragonsteelArmorMaterial("dragonsteel fire", (int) (0.02D * IafConfig.dragonsteelBaseDurabilityEquipment), new int[]{IafConfig.dragonsteelBaseArmor - 6, IafConfig.dragonsteelBaseArmor - 3, IafConfig.dragonsteelBaseArmor, IafConfig.dragonsteelBaseArmor - 5}, 30, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 6.0F);
    public static CustomArmorMaterial DRAGONSTEEL_ICE_ARMOR_MATERIAL = new DragonsteelArmorMaterial("dragonsteel_ice", (int) (0.02D * IafConfig.dragonsteelBaseDurabilityEquipment), new int[]{IafConfig.dragonsteelBaseArmor - 6, IafConfig.dragonsteelBaseArmor - 3, IafConfig.dragonsteelBaseArmor, IafConfig.dragonsteelBaseArmor - 5}, 30, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 6.0F);
    public static CustomArmorMaterial DRAGONSTEEL_LIGHTNING_ARMOR_MATERIAL = new DragonsteelArmorMaterial("dragonsteel_lightning", (int) (0.02D * IafConfig.dragonsteelBaseDurabilityEquipment), new int[]{IafConfig.dragonsteelBaseArmor - 6, IafConfig.dragonsteelBaseArmor - 3, IafConfig.dragonsteelBaseArmor, IafConfig.dragonsteelBaseArmor - 5}, 30, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 6.0F);
    public static CustomToolMaterial SILVER_TOOL_MATERIAL = new CustomToolMaterial("silver", 2, 460, 1.0F, 11.0F, 18);
    public static CustomToolMaterial COPPER_TOOL_MATERIAL = new CustomToolMaterial("copper", 2, 300, 0.0F, 0.7F, 10);
    public static CustomToolMaterial DRAGONBONE_TOOL_MATERIAL = new CustomToolMaterial("Dragonbone", 4, 1660, 4.0F, 10.0F, 22);
    public static CustomToolMaterial FIRE_DRAGONBONE_TOOL_MATERIAL = new CustomToolMaterial("FireDragonbone", 4, 2000, 5.5F, 10F, 22);
    public static CustomToolMaterial ICE_DRAGONBONE_TOOL_MATERIAL = new CustomToolMaterial("IceDragonbone", 4, 2000, 5.5F, 10F, 22);
    public static CustomToolMaterial LIGHTNING_DRAGONBONE_TOOL_MATERIAL = new CustomToolMaterial("LightningDragonbone", 4, 2000, 5.5F, 10F, 22);
    public static CustomToolMaterial TROLL_WEAPON_TOOL_MATERIAL = new CustomToolMaterial("trollWeapon", 2, 300, 1F, 10F, 1);
    public static CustomToolMaterial MYRMEX_CHITIN_TOOL_MATERIAL = new CustomToolMaterial("MyrmexChitin", 3, 600, 1.0F, 6.0F, 8);
    public static CustomToolMaterial DRAGONSTEEL_FIRE_TOOL_MATERIAL = new DragonsteelToolMaterial("DragonsteelFire", 5, IafConfig.dragonsteelBaseDurability, (float) IafConfig.dragonsteelBaseDamage - 4.0F, 10F, 10);
    public static CustomToolMaterial DRAGONSTEEL_ICE_TOOL_MATERIAL = new DragonsteelToolMaterial("DragonsteelIce", 5, IafConfig.dragonsteelBaseDurability, (float) IafConfig.dragonsteelBaseDamage - 4.0F, 10F, 10);
    public static CustomToolMaterial DRAGONSTEEL_LIGHTNING_TOOL_MATERIAL = new DragonsteelToolMaterial("DragonsteelLightning", 5, IafConfig.dragonsteelBaseDurability, (float) IafConfig.dragonsteelBaseDamage - 4.0F, 10F, 10);
    public static CustomToolMaterial HIPPOGRYPH_SWORD_TOOL_MATERIAL = new CustomToolMaterial("HippogryphSword", 2, 500, 2.5F, 10F, 10);
    public static CustomToolMaterial STYMHALIAN_SWORD_TOOL_MATERIAL = new CustomToolMaterial("StymphalianSword", 2, 500, 2, 10.0F, 10);
    public static CustomToolMaterial AMPHITHERE_SWORD_TOOL_MATERIAL = new CustomToolMaterial("AmphithereSword", 2, 500, 1F, 10F, 10);
    public static CustomToolMaterial HIPPOCAMPUS_SWORD_TOOL_MATERIAL = new CustomToolMaterial("HippocampusSword", 0, 500, -2F, 0F, 50);
    public static CustomToolMaterial DREAD_SWORD_TOOL_MATERIAL = new CustomToolMaterial("DreadSword", 0, 100, 1F, 10F, 0);
    public static CustomToolMaterial DREAD_KNIGHT_TOOL_MATERIAL = new CustomToolMaterial("DreadKnightSword", 0, 1200, 13F, 0F, 10);
    public static CustomToolMaterial DREAD_QUEEN_SWORD_TOOL_MATERIAL = new DragonsteelToolMaterial("DreadQueenSword", 0, IafConfig.dragonsteelBaseDurability, (float) IafConfig.dragonsteelBaseDamage, 10F, 10);
    public static CustomToolMaterial GHOST_SWORD_TOOL_MATERIAL = new CustomToolMaterial("GhostSword", 2, 3000, 5, 10.0F, 25);

    public static final Item BESTIARY = new ItemBestiary();
    public static final Item MANUSCRIPT = new ItemGeneric("manuscript");
    public static final Item SAPPHIRE_GEM = new ItemGeneric("sapphire_gem");
    public static final Item SILVER_INGOT = new ItemGeneric("silver_ingot");
    public static final Item SILVER_NUGGET = new ItemGeneric("silver_nugget");
    public static final Item AMYTHEST_GEM = new ItemGeneric("amythest_gem");
    public static final Item COPPER_INGOT = new ItemGeneric("copper_ingot");
    public static final Item COPPER_NUGGET = new ItemGeneric("copper_nugget");
    public static final Item SILVER_HELMET = new ItemSilverArmor(SILVER_ARMOR_MATERIAL, EquipmentSlotType.HEAD, "armor_silver_metal_helmet");
    public static final Item SILVER_CHESTPLATE = new ItemSilverArmor(SILVER_ARMOR_MATERIAL, EquipmentSlotType.CHEST, "armor_silver_metal_chestplate");
    public static final Item SILVER_LEGGINGS = new ItemSilverArmor(SILVER_ARMOR_MATERIAL, EquipmentSlotType.LEGS, "armor_silver_metal_leggings");
    public static final Item SILVER_BOOTS = new ItemSilverArmor(SILVER_ARMOR_MATERIAL, EquipmentSlotType.FEET, "armor_silver_metal_boots");
    public static final Item SILVER_SWORD = new ItemModSword(SILVER_TOOL_MATERIAL, "silver_sword");
    public static final Item SILVER_SHOVEL = new ItemModShovel(SILVER_TOOL_MATERIAL, "silver_shovel");
    public static final Item SILVER_PICKAXE = new ItemModPickaxe(SILVER_TOOL_MATERIAL, "silver_pickaxe");
    public static final Item SILVER_AXE = new ItemModAxe(SILVER_TOOL_MATERIAL, "silver_axe");
    public static final Item SILVER_HOE = new ItemModHoe(SILVER_TOOL_MATERIAL, "silver_hoe");

    public static final Item COPPER_HELMET = new ItemCopperArmor(COPPER_ARMOR_MATERIAL, EquipmentSlotType.HEAD, "armor_copper_metal_helmet");
    public static final Item COPPER_CHESTPLATE = new ItemCopperArmor(COPPER_ARMOR_MATERIAL, EquipmentSlotType.CHEST, "armor_copper_metal_chestplate");
    public static final Item COPPER_LEGGINGS = new ItemCopperArmor(COPPER_ARMOR_MATERIAL, EquipmentSlotType.LEGS, "armor_copper_metal_leggings");
    public static final Item COPPER_BOOTS = new ItemCopperArmor(COPPER_ARMOR_MATERIAL, EquipmentSlotType.FEET, "armor_copper_metal_boots");
    public static final Item COPPER_SWORD = new ItemModSword(COPPER_TOOL_MATERIAL, "copper_sword");
    public static final Item COPPER_SHOVEL = new ItemModShovel(COPPER_TOOL_MATERIAL, "copper_shovel");
    public static final Item COPPER_PICKAXE = new ItemModPickaxe(COPPER_TOOL_MATERIAL, "copper_pickaxe");
    public static final Item COPPER_AXE = new ItemModAxe(COPPER_TOOL_MATERIAL, "copper_axe");
    public static final Item COPPER_HOE = new ItemModHoe(COPPER_TOOL_MATERIAL, "copper_hoe");
    
    public static final Item FIRE_STEW = new ItemGeneric("fire_stew");
    public static final Item FROST_STEW = new ItemGeneric("frost_stew");
    public static final Item LIGHTNING_STEW = new ItemGeneric("lightning_stew");
    public static final Item DRAGONEGG_RED = new ItemDragonEgg("dragonegg_red", EnumDragonEgg.RED);
    public static final Item DRAGONEGG_GREEN = new ItemDragonEgg("dragonegg_green", EnumDragonEgg.GREEN);
    public static final Item DRAGONEGG_BRONZE = new ItemDragonEgg("dragonegg_bronze", EnumDragonEgg.BRONZE);
    public static final Item DRAGONEGG_GRAY = new ItemDragonEgg("dragonegg_gray", EnumDragonEgg.GRAY);
    public static final Item DRAGONEGG_BLUE = new ItemDragonEgg("dragonegg_blue", EnumDragonEgg.BLUE);
    public static final Item DRAGONEGG_WHITE = new ItemDragonEgg("dragonegg_white", EnumDragonEgg.WHITE);
    public static final Item DRAGONEGG_SAPPHIRE = new ItemDragonEgg("dragonegg_sapphire", EnumDragonEgg.SAPPHIRE);
    public static final Item DRAGONEGG_SILVER = new ItemDragonEgg("dragonegg_silver", EnumDragonEgg.SILVER);
    public static final Item DRAGONEGG_ELECTRIC = new ItemDragonEgg("dragonegg_electric", EnumDragonEgg.ELECTRIC);
    public static final Item DRAGONEGG_AMYTHEST = new ItemDragonEgg("dragonegg_amythest", EnumDragonEgg.AMYTHEST);
    public static final Item DRAGONEGG_COPPER = new ItemDragonEgg("dragonegg_copper", EnumDragonEgg.COPPER);
    public static final Item DRAGONEGG_BLACK = new ItemDragonEgg("dragonegg_black", EnumDragonEgg.BLACK);
    public static final Item DRAGONSCALES_RED = new ItemDragonScales("dragonscales_red", EnumDragonEgg.RED);
    public static final Item DRAGONSCALES_GREEN = new ItemDragonScales("dragonscales_green", EnumDragonEgg.GREEN);
    public static final Item DRAGONSCALES_BRONZE = new ItemDragonScales("dragonscales_bronze", EnumDragonEgg.BRONZE);
    public static final Item DRAGONSCALES_GRAY = new ItemDragonScales("dragonscales_gray", EnumDragonEgg.GRAY);
    public static final Item DRAGONSCALES_BLUE = new ItemDragonScales("dragonscales_blue", EnumDragonEgg.BLUE);
    public static final Item DRAGONSCALES_WHITE = new ItemDragonScales("dragonscales_white", EnumDragonEgg.WHITE);
    public static final Item DRAGONSCALES_SAPPHIRE = new ItemDragonScales("dragonscales_sapphire", EnumDragonEgg.SAPPHIRE);
    public static final Item DRAGONSCALES_SILVER = new ItemDragonScales("dragonscales_silver", EnumDragonEgg.SILVER);
    public static final Item DRAGONSCALES_ELECTRIC = new ItemDragonScales("dragonscales_electric", EnumDragonEgg.ELECTRIC);
    public static final Item DRAGONSCALES_AMYTHEST = new ItemDragonScales("dragonscales_amythest", EnumDragonEgg.AMYTHEST);
    public static final Item DRAGONSCALES_COPPER = new ItemDragonScales("dragonscales_copper", EnumDragonEgg.COPPER);
    public static final Item DRAGONSCALES_BLACK = new ItemDragonScales("dragonscales_black", EnumDragonEgg.BLACK);
    public static final Item DRAGON_BONE = new ItemDragonBone();
    public static final Item WITHERBONE = new ItemGeneric("witherbone");
    public static final Item FISHING_SPEAR = new ItemFishingSpear();
    public static final Item WITHER_SHARD = new ItemGeneric("wither_shard");
    public static final Item DRAGONBONE_SWORD = new ItemModSword(DRAGONBONE_TOOL_MATERIAL, "dragonbone_sword");
    public static final Item DRAGONBONE_SHOVEL = new ItemModShovel(DRAGONBONE_TOOL_MATERIAL, "dragonbone_shovel");
    public static final Item DRAGONBONE_PICKAXE = new ItemModPickaxe(DRAGONBONE_TOOL_MATERIAL, "dragonbone_pickaxe");
    public static final Item DRAGONBONE_AXE = new ItemModAxe(DRAGONBONE_TOOL_MATERIAL, "dragonbone_axe");
    public static final Item DRAGONBONE_HOE = new ItemModHoe(DRAGONBONE_TOOL_MATERIAL, "dragonbone_hoe");
    public static final Item DRAGONBONE_SWORD_FIRE = new ItemAlchemySword(FIRE_DRAGONBONE_TOOL_MATERIAL, "dragonbone_sword_fire");
    public static final Item DRAGONBONE_SWORD_ICE = new ItemAlchemySword(ICE_DRAGONBONE_TOOL_MATERIAL, "dragonbone_sword_ice");
    public static final Item DRAGONBONE_SWORD_LIGHTNING = new ItemAlchemySword(LIGHTNING_DRAGONBONE_TOOL_MATERIAL, "dragonbone_sword_lightning");
    public static final Item DRAGONBONE_ARROW = new ItemDragonArrow();
    public static final Item DRAGON_BOW = new ItemDragonBow();
    public static final Item DRAGON_SKULL_FIRE = new ItemDragonSkull(0);
    public static final Item DRAGON_SKULL_ICE = new ItemDragonSkull(1);
    public static final Item DRAGON_SKULL_LIGHTNING = new ItemDragonSkull(2);
    public static final ItemDragonArmor DRAGONARMOR_IRON_0 = new ItemDragonArmor(0, 0, "dragonarmor_iron");
    public static final ItemDragonArmor DRAGONARMOR_IRON_1 = new ItemDragonArmor(0, 1, "dragonarmor_iron");
    public static final ItemDragonArmor DRAGONARMOR_IRON_2 = new ItemDragonArmor(0, 2, "dragonarmor_iron");
    public static final ItemDragonArmor DRAGONARMOR_IRON_3 = new ItemDragonArmor(0, 3, "dragonarmor_iron");
    public static final ItemDragonArmor DRAGONARMOR_COPPER_0 = new ItemDragonArmor(6, 0, "dragonarmor_copper");
    public static final ItemDragonArmor DRAGONARMOR_COPPER_1 = new ItemDragonArmor(6, 1, "dragonarmor_copper");
    public static final ItemDragonArmor DRAGONARMOR_COPPER_2 = new ItemDragonArmor(6, 2, "dragonarmor_copper");
    public static final ItemDragonArmor DRAGONARMOR_COPPER_3 = new ItemDragonArmor(6, 3, "dragonarmor_copper");
    public static final ItemDragonArmor DRAGONARMOR_GOLD_0 = new ItemDragonArmor(1, 0, "dragonarmor_gold");
    public static final ItemDragonArmor DRAGONARMOR_GOLD_1 = new ItemDragonArmor(1, 1, "dragonarmor_gold");
    public static final ItemDragonArmor DRAGONARMOR_GOLD_2 = new ItemDragonArmor(1, 2, "dragonarmor_gold");
    public static final ItemDragonArmor DRAGONARMOR_GOLD_3 = new ItemDragonArmor(1, 3, "dragonarmor_gold");
    public static final ItemDragonArmor DRAGONARMOR_DIAMOND_0 = new ItemDragonArmor(2, 0, "dragonarmor_diamond");
    public static final ItemDragonArmor DRAGONARMOR_DIAMOND_1 = new ItemDragonArmor(2, 1, "dragonarmor_diamond");
    public static final ItemDragonArmor DRAGONARMOR_DIAMOND_2 = new ItemDragonArmor(2, 2, "dragonarmor_diamond");
    public static final ItemDragonArmor DRAGONARMOR_DIAMOND_3 = new ItemDragonArmor(2, 3, "dragonarmor_diamond");
    public static final ItemDragonArmor DRAGONARMOR_SILVER_0 = new ItemDragonArmor(3, 0, "dragonarmor_silver");
    public static final ItemDragonArmor DRAGONARMOR_SILVER_1 = new ItemDragonArmor(3, 1, "dragonarmor_silver");
    public static final ItemDragonArmor DRAGONARMOR_SILVER_2 = new ItemDragonArmor(3, 2, "dragonarmor_silver");
    public static final ItemDragonArmor DRAGONARMOR_SILVER_3 = new ItemDragonArmor(3, 3, "dragonarmor_silver");
    public static final ItemDragonArmor DRAGONARMOR_DRAGONSTEEL_FIRE_0 = new ItemDragonArmor(4, 0, "dragonarmor_dragonsteel_fire");
    public static final ItemDragonArmor DRAGONARMOR_DRAGONSTEEL_FIRE_1 = new ItemDragonArmor(4, 1, "dragonarmor_dragonsteel_fire");
    public static final ItemDragonArmor DRAGONARMOR_DRAGONSTEEL_FIRE_2 = new ItemDragonArmor(4, 2, "dragonarmor_dragonsteel_fire");
    public static final ItemDragonArmor DRAGONARMOR_DRAGONSTEEL_FIRE_3 = new ItemDragonArmor(4, 3, "dragonarmor_dragonsteel_fire");
    public static final ItemDragonArmor DRAGONARMOR_DRAGONSTEEL_ICE_0 = new ItemDragonArmor(5, 0, "dragonarmor_dragonsteel_ice");
    public static final ItemDragonArmor DRAGONARMOR_DRAGONSTEEL_ICE_1 = new ItemDragonArmor(5, 1, "dragonarmor_dragonsteel_ice");
    public static final ItemDragonArmor DRAGONARMOR_DRAGONSTEEL_ICE_2 = new ItemDragonArmor(5, 2, "dragonarmor_dragonsteel_ice");
    public static final ItemDragonArmor DRAGONARMOR_DRAGONSTEEL_ICE_3 = new ItemDragonArmor(5, 3, "dragonarmor_dragonsteel_ice");
    public static final ItemDragonArmor DRAGONARMOR_DRAGONSTEEL_LIGHTNING_0 = new ItemDragonArmor(7, 0, "dragonarmor_dragonsteel_lightning");
    public static final ItemDragonArmor DRAGONARMOR_DRAGONSTEEL_LIGHTNING_1 = new ItemDragonArmor(7, 1, "dragonarmor_dragonsteel_lightning");
    public static final ItemDragonArmor DRAGONARMOR_DRAGONSTEEL_LIGHTNING_2 = new ItemDragonArmor(7, 2, "dragonarmor_dragonsteel_lightning");
    public static final ItemDragonArmor DRAGONARMOR_DRAGONSTEEL_LIGHTNING_3 = new ItemDragonArmor(7, 3, "dragonarmor_dragonsteel_lightning");
    public static final Item DRAGON_MEAL = new ItemGeneric("dragon_meal");
    public static final Item SICKLY_DRAGON_MEAL = new ItemGeneric("sickly_dragon_meal", 1);
    public static final Item CREATIVE_DRAGON_MEAL = new ItemGeneric("creative_dragon_meal", 2);
    public static final Item FIRE_DRAGON_FLESH = new ItemDragonFlesh(0);
    public static final Item ICE_DRAGON_FLESH = new ItemDragonFlesh(1);
    public static final Item LIGHTNING_DRAGON_FLESH = new ItemDragonFlesh(2);
    public static final Item FIRE_DRAGON_HEART = new ItemGeneric("fire_dragon_heart");
    public static final Item ICE_DRAGON_HEART = new ItemGeneric("ice_dragon_heart");
    public static final Item LIGHTNING_DRAGON_HEART = new ItemGeneric("lightning_dragon_heart");
    public static final Item FIRE_DRAGON_BLOOD = new ItemGeneric("fire_dragon_blood");
    public static final Item ICE_DRAGON_BLOOD = new ItemGeneric("ice_dragon_blood");
    public static final Item LIGHTNING_DRAGON_BLOOD = new ItemGeneric("lightning_dragon_blood");
    public static final Item DRAGON_STAFF = new ItemDragonStaff();
    public static final Item DRAGON_HORN = new ItemDragonHorn();
    public static final Item DRAGON_FLUTE = new ItemDragonFlute();
    public static final Item SUMMONING_CRYSTAL_FIRE = new ItemSummoningCrystal("fire");
    public static final Item SUMMONING_CRYSTAL_ICE = new ItemSummoningCrystal("ice");
    public static final Item SUMMONING_CRYSTAL_LIGHTNING = new ItemSummoningCrystal("lightning");
    public static final Item HIPPOGRYPH_EGG = new ItemHippogryphEgg();
    public static final Item IRON_HIPPOGRYPH_ARMOR = new ItemGeneric("iron_hippogryph_armor", 0, 1);
    public static final Item GOLD_HIPPOGRYPH_ARMOR = new ItemGeneric("gold_hippogryph_armor", 0, 1);
    public static final Item DIAMOND_HIPPOGRYPH_ARMOR = new ItemGeneric("diamond_hippogryph_armor", 0, 1);
    public static final Item HIPPOGRYPH_TALON = new ItemGeneric("hippogryph_talon", 1);
    public static final Item HIPPOGRYPH_SWORD = new ItemHippogryphSword();
    public static final Item GORGON_HEAD = new ItemGorgonHead();
    public static final Item STONE_STATUE = new ItemStoneStatue();
    public static final Item BLINDFOLD = new ItemBlindfold();
    public static final Item PIXIE_DUST = new ItemPixieDust();
    public static final Item PIXIE_WINGS = new ItemGeneric("pixie_wings", 1);
    public static final Item PIXIE_WAND = new ItemPixieWand();
    public static final Item AMBROSIA = new ItemAmbrosia();
    public static final Item CYCLOPS_EYE = new ItemCyclopsEye();
    public static final Item SHEEP_HELMET = new ItemModArmor(SHEEP_ARMOR_MATERIAL, EquipmentSlotType.HEAD, "sheep_helmet");
    public static final Item SHEEP_CHESTPLATE = new ItemModArmor(SHEEP_ARMOR_MATERIAL, EquipmentSlotType.CHEST, "sheep_chestplate");
    public static final Item SHEEP_LEGGINGS = new ItemModArmor(SHEEP_ARMOR_MATERIAL, EquipmentSlotType.LEGS, "sheep_leggings");
    public static final Item SHEEP_BOOTS = new ItemModArmor(SHEEP_ARMOR_MATERIAL, EquipmentSlotType.FEET, "sheep_boots");
    public static final Item SHINY_SCALES = new ItemGeneric("shiny_scales");
    public static final Item SIREN_TEAR = new ItemGeneric("siren_tear", 1);
    public static final Item SIREN_FLUTE = new ItemSirenFlute();
    public static final Item HIPPOCAMPUS_FIN = new ItemGeneric("hippocampus_fin", 1);
    public static final Item HIPPOCAMPUS_SLAPPER = new ItemHippocampusSlapper();
    public static final Item EARPLUGS = new ItemModArmor(EARPLUGS_ARMOR_MATERIAL, EquipmentSlotType.HEAD, "earplugs");
    public static final Item DEATH_WORM_CHITIN_YELLOW = new ItemGeneric("deathworm_chitin_yellow");
    public static final Item DEATH_WORM_CHITIN_WHITE = new ItemGeneric("deathworm_chitin_white");
    public static final Item DEATH_WORM_CHITIN_RED = new ItemGeneric("deathworm_chitin_red");
    public static final Item DEATHWORM_YELLOW_HELMET = new ItemDeathwormArmor(DEATHWORM_0_ARMOR_MATERIAL, EquipmentSlotType.HEAD, "deathworm_yellow_helmet");
    public static final Item DEATHWORM_YELLOW_CHESTPLATE = new ItemDeathwormArmor(DEATHWORM_0_ARMOR_MATERIAL, EquipmentSlotType.CHEST, "deathworm_yellow_chestplate");
    public static final Item DEATHWORM_YELLOW_LEGGINGS = new ItemDeathwormArmor(DEATHWORM_0_ARMOR_MATERIAL, EquipmentSlotType.LEGS, "deathworm_yellow_leggings");
    public static final Item DEATHWORM_YELLOW_BOOTS = new ItemDeathwormArmor(DEATHWORM_0_ARMOR_MATERIAL, EquipmentSlotType.FEET, "deathworm_yellow_boots");
    public static final Item DEATHWORM_WHITE_HELMET = new ItemDeathwormArmor(DEATHWORM_1_ARMOR_MATERIAL, EquipmentSlotType.HEAD, "deathworm_white_helmet");
    public static final Item DEATHWORM_WHITE_CHESTPLATE = new ItemDeathwormArmor(DEATHWORM_1_ARMOR_MATERIAL, EquipmentSlotType.CHEST, "deathworm_white_chestplate");
    public static final Item DEATHWORM_WHITE_LEGGINGS = new ItemDeathwormArmor(DEATHWORM_1_ARMOR_MATERIAL, EquipmentSlotType.LEGS, "deathworm_white_leggings");
    public static final Item DEATHWORM_WHITE_BOOTS = new ItemDeathwormArmor(DEATHWORM_1_ARMOR_MATERIAL, EquipmentSlotType.FEET, "deathworm_white_boots");
    public static final Item DEATHWORM_RED_HELMET = new ItemDeathwormArmor(DEATHWORM_2_ARMOR_MATERIAL, EquipmentSlotType.HEAD, "deathworm_red_helmet");
    public static final Item DEATHWORM_RED_CHESTPLATE = new ItemDeathwormArmor(DEATHWORM_2_ARMOR_MATERIAL, EquipmentSlotType.CHEST, "deathworm_red_chestplate");
    public static final Item DEATHWORM_RED_LEGGINGS = new ItemDeathwormArmor(DEATHWORM_2_ARMOR_MATERIAL, EquipmentSlotType.LEGS, "deathworm_red_leggings");
    public static final Item DEATHWORM_RED_BOOTS = new ItemDeathwormArmor(DEATHWORM_2_ARMOR_MATERIAL, EquipmentSlotType.FEET, "deathworm_red_boots");
    public static final Item DEATHWORM_EGG = new ItemDeathwormEgg(false);
    public static final Item DEATHWORM_EGG_GIGANTIC = new ItemDeathwormEgg(true);
    public static final Item DEATHWORM_TOUNGE = new ItemGeneric("deathworm_tounge", 1);
    public static final Item DEATHWORM_GAUNTLET_YELLOW = new ItemDeathwormGauntlet("yellow");
    public static final Item DEATHWORM_GAUNTLET_WHITE = new ItemDeathwormGauntlet("white");
    public static final Item DEATHWORM_GAUNTLET_RED = new ItemDeathwormGauntlet("red");
    public static final Item ROTTEN_EGG = new ItemRottenEgg();
    public static final Item COCKATRICE_EYE = new ItemGeneric("cockatrice_eye", 1);
    public static final Item ITEM_COCKATRICE_SCEPTER = new ItemCockatriceScepter();
    public static final Item STYMPHALIAN_BIRD_FEATHER = new ItemGeneric("stymphalian_bird_feather");
    public static final Item STYMPHALIAN_ARROW = new ItemStymphalianArrow();
    public static final Item STYMPHALIAN_FEATHER_BUNDLE = new ItemStymphalianFeatherBundle();
    public static final Item STYMPHALIAN_DAGGER = new ItemStymphalianDagger();
    public static final Item TROLL_TUSK = new ItemGeneric("troll_tusk");
    public static final Item MYRMEX_DESERT_EGG = new ItemMyrmexEgg(false);
    public static final Item MYRMEX_JUNGLE_EGG = new ItemMyrmexEgg(true);
    public static final Item MYRMEX_DESERT_RESIN = new ItemGeneric("myrmex_desert_resin");
    public static final Item MYRMEX_JUNGLE_RESIN = new ItemGeneric("myrmex_jungle_resin");
    public static final Item MYRMEX_DESERT_CHITIN = new ItemGeneric("myrmex_desert_chitin");
    public static final Item MYRMEX_JUNGLE_CHITIN = new ItemGeneric("myrmex_jungle_chitin");
    public static final Item MYRMEX_STINGER = new ItemGeneric("myrmex_stinger");
    public static final Item MYRMEX_DESERT_SWORD = new ItemModSword(MYRMEX_CHITIN_TOOL_MATERIAL, "myrmex_desert_sword");
    public static final Item MYRMEX_DESERT_SWORD_VENOM = new ItemModSword(MYRMEX_CHITIN_TOOL_MATERIAL, "myrmex_desert_sword_venom");
    public static final Item MYRMEX_DESERT_SHOVEL = new ItemModShovel(MYRMEX_CHITIN_TOOL_MATERIAL, "myrmex_desert_shovel");
    public static final Item MYRMEX_DESERT_PICKAXE = new ItemModPickaxe(MYRMEX_CHITIN_TOOL_MATERIAL, "myrmex_desert_pickaxe");
    public static final Item MYRMEX_DESERT_AXE = new ItemModAxe(MYRMEX_CHITIN_TOOL_MATERIAL, "myrmex_desert_axe");
    public static final Item MYRMEX_DESERT_HOE = new ItemModHoe(MYRMEX_CHITIN_TOOL_MATERIAL, "myrmex_desert_hoe");
    public static final Item MYRMEX_JUNGLE_SWORD = new ItemModSword(MYRMEX_CHITIN_TOOL_MATERIAL, "myrmex_jungle_sword");
    public static final Item MYRMEX_JUNGLE_SWORD_VENOM = new ItemModSword(MYRMEX_CHITIN_TOOL_MATERIAL, "myrmex_jungle_sword_venom");
    public static final Item MYRMEX_JUNGLE_SHOVEL = new ItemModShovel(MYRMEX_CHITIN_TOOL_MATERIAL, "myrmex_jungle_shovel");
    public static final Item MYRMEX_JUNGLE_PICKAXE = new ItemModPickaxe(MYRMEX_CHITIN_TOOL_MATERIAL, "myrmex_jungle_pickaxe");
    public static final Item MYRMEX_JUNGLE_AXE = new ItemModAxe(MYRMEX_CHITIN_TOOL_MATERIAL, "myrmex_jungle_axe");
    public static final Item MYRMEX_JUNGLE_HOE = new ItemModHoe(MYRMEX_CHITIN_TOOL_MATERIAL, "myrmex_jungle_hoe");
    public static final Item MYRMEX_DESERT_STAFF = new ItemMyrmexStaff(false);
    public static final Item MYRMEX_JUNGLE_STAFF = new ItemMyrmexStaff(true);
    public static final Item MYRMEX_DESERT_HELMET = new ItemModArmor(MYRMEX_DESERT_ARMOR_MATERIAL, EquipmentSlotType.HEAD, "myrmex_desert_helmet");
    public static final Item MYRMEX_DESERT_CHESTPLATE = new ItemModArmor(MYRMEX_DESERT_ARMOR_MATERIAL, EquipmentSlotType.CHEST, "myrmex_desert_chestplate");
    public static final Item MYRMEX_DESERT_LEGGINGS = new ItemModArmor(MYRMEX_DESERT_ARMOR_MATERIAL, EquipmentSlotType.LEGS, "myrmex_desert_leggings");
    public static final Item MYRMEX_DESERT_BOOTS = new ItemModArmor(MYRMEX_DESERT_ARMOR_MATERIAL, EquipmentSlotType.FEET, "myrmex_desert_boots");
    public static final Item MYRMEX_JUNGLE_HELMET = new ItemModArmor(MYRMEX_JUNGLE_ARMOR_MATERIAL, EquipmentSlotType.HEAD, "myrmex_jungle_helmet");
    public static final Item MYRMEX_JUNGLE_CHESTPLATE = new ItemModArmor(MYRMEX_JUNGLE_ARMOR_MATERIAL, EquipmentSlotType.CHEST, "myrmex_jungle_chestplate");
    public static final Item MYRMEX_JUNGLE_LEGGINGS = new ItemModArmor(MYRMEX_JUNGLE_ARMOR_MATERIAL, EquipmentSlotType.LEGS, "myrmex_jungle_leggings");
    public static final Item MYRMEX_JUNGLE_BOOTS = new ItemModArmor(MYRMEX_JUNGLE_ARMOR_MATERIAL, EquipmentSlotType.FEET, "myrmex_jungle_boots");
    public static final Item MYMREX_DESERT_SWARM = new ItemMyrmexSwarm(false);
    public static final Item MYMREX_JUNGLE_SWARM = new ItemMyrmexSwarm(true);
    public static final Item AMPHITHERE_FEATHER = new ItemGeneric("amphithere_feather");
    public static final Item AMPHITHERE_ARROW = new ItemAmphithereArrow();
    public static final Item AMPHITHERE_MACUAHUITL = new ItemAmphithereMacuahuitl();
    public static final Item SERPENT_FANG = new ItemGeneric("sea_serpent_fang");
    public static final Item SEA_SERPENT_ARROW = new ItemSeaSerpentArrow();
    public static final Item TIDE_TRIDENT = new ItemTideTrident();
    public static final Item CHAIN = new ItemChain(false);
    public static final Item CHAIN_STICKY = new ItemChain(true);
    public static final Item DRAGONSTEEL_FIRE_INGOT = new ItemGeneric("dragonsteel_fire_ingot");
    public static final Item DRAGONSTEEL_FIRE_SWORD = new ItemModSword(DRAGONSTEEL_FIRE_TOOL_MATERIAL, "dragonsteel_fire_sword");
    public static final Item DRAGONSTEEL_FIRE_PICKAXE = new ItemModPickaxe(DRAGONSTEEL_FIRE_TOOL_MATERIAL, "dragonsteel_fire_pickaxe");
    public static final Item DRAGONSTEEL_FIRE_AXE = new ItemModAxe(DRAGONSTEEL_FIRE_TOOL_MATERIAL, "dragonsteel_fire_axe");
    public static final Item DRAGONSTEEL_FIRE_SHOVEL = new ItemModShovel(DRAGONSTEEL_FIRE_TOOL_MATERIAL, "dragonsteel_fire_shovel");
    public static final Item DRAGONSTEEL_FIRE_HOE = new ItemModHoe(DRAGONSTEEL_FIRE_TOOL_MATERIAL, "dragonsteel_fire_hoe");
    public static final Item DRAGONSTEEL_FIRE_HELMET = new ItemDragonsteelArmor(DRAGONSTEEL_FIRE_ARMOR_MATERIAL, 0, EquipmentSlotType.HEAD, "dragonsteel_fire_helmet", "iceandfire.dragonsteel_fire_helmet");
    public static final Item DRAGONSTEEL_FIRE_CHESTPLATE = new ItemDragonsteelArmor(DRAGONSTEEL_FIRE_ARMOR_MATERIAL, 1, EquipmentSlotType.CHEST, "dragonsteel_fire_chestplate", "iceandfire.dragonsteel_fire_chestplate");
    public static final Item DRAGONSTEEL_FIRE_LEGGINGS = new ItemDragonsteelArmor(DRAGONSTEEL_FIRE_ARMOR_MATERIAL, 2, EquipmentSlotType.LEGS, "dragonsteel_fire_leggings", "iceandfire.dragonsteel_fire_leggings");
    public static final Item DRAGONSTEEL_FIRE_BOOTS = new ItemDragonsteelArmor(DRAGONSTEEL_FIRE_ARMOR_MATERIAL, 3, EquipmentSlotType.FEET, "dragonsteel_fire_boots", "iceandfire.dragonsteel_fire_boots");
    public static final Item DRAGONSTEEL_ICE_INGOT = new ItemGeneric("dragonsteel_ice_ingot");
    public static final Item DRAGONSTEEL_ICE_SWORD = new ItemModSword(DRAGONSTEEL_ICE_TOOL_MATERIAL, "dragonsteel_ice_sword");
    public static final Item DRAGONSTEEL_ICE_PICKAXE = new ItemModPickaxe(DRAGONSTEEL_ICE_TOOL_MATERIAL, "dragonsteel_ice_pickaxe");
    public static final Item DRAGONSTEEL_ICE_AXE = new ItemModAxe(DRAGONSTEEL_ICE_TOOL_MATERIAL, "dragonsteel_ice_axe");
    public static final Item DRAGONSTEEL_ICE_SHOVEL = new ItemModShovel(DRAGONSTEEL_ICE_TOOL_MATERIAL, "dragonsteel_ice_shovel");
    public static final Item DRAGONSTEEL_ICE_HOE = new ItemModHoe(DRAGONSTEEL_ICE_TOOL_MATERIAL, "dragonsteel_ice_hoe");
    public static final Item DRAGONSTEEL_ICE_HELMET = new ItemDragonsteelArmor(DRAGONSTEEL_ICE_ARMOR_MATERIAL, 0, EquipmentSlotType.HEAD, "dragonsteel_ice_helmet", "iceandfire.dragonsteel_ice_helmet");
    public static final Item DRAGONSTEEL_ICE_CHESTPLATE = new ItemDragonsteelArmor(DRAGONSTEEL_ICE_ARMOR_MATERIAL, 1, EquipmentSlotType.CHEST, "dragonsteel_ice_chestplate", "iceandfire.dragonsteel_ice_chestplate");
    public static final Item DRAGONSTEEL_ICE_LEGGINGS = new ItemDragonsteelArmor(DRAGONSTEEL_ICE_ARMOR_MATERIAL, 2, EquipmentSlotType.LEGS, "dragonsteel_ice_leggings", "iceandfire.dragonsteel_ice_leggings");
    public static final Item DRAGONSTEEL_ICE_BOOTS = new ItemDragonsteelArmor(DRAGONSTEEL_ICE_ARMOR_MATERIAL, 3, EquipmentSlotType.FEET, "dragonsteel_ice_boots", "iceandfire.dragonsteel_ice_boots");

    public static final Item DRAGONSTEEL_LIGHTNING_INGOT = new ItemGeneric("dragonsteel_lightning_ingot");
    public static final Item DRAGONSTEEL_LIGHTNING_SWORD = new ItemModSword(DRAGONSTEEL_LIGHTNING_TOOL_MATERIAL, "dragonsteel_lightning_sword");
    public static final Item DRAGONSTEEL_LIGHTNING_PICKAXE = new ItemModPickaxe(DRAGONSTEEL_LIGHTNING_TOOL_MATERIAL, "dragonsteel_lightning_pickaxe");
    public static final Item DRAGONSTEEL_LIGHTNING_AXE = new ItemModAxe(DRAGONSTEEL_LIGHTNING_TOOL_MATERIAL, "dragonsteel_lightning_axe");
    public static final Item DRAGONSTEEL_LIGHTNING_SHOVEL = new ItemModShovel(DRAGONSTEEL_LIGHTNING_TOOL_MATERIAL, "dragonsteel_lightning_shovel");
    public static final Item DRAGONSTEEL_LIGHTNING_HOE = new ItemModHoe(DRAGONSTEEL_LIGHTNING_TOOL_MATERIAL, "dragonsteel_lightning_hoe");
    public static final Item DRAGONSTEEL_LIGHTNING_HELMET = new ItemDragonsteelArmor(DRAGONSTEEL_LIGHTNING_ARMOR_MATERIAL, 0, EquipmentSlotType.HEAD, "dragonsteel_lightning_helmet", "iceandfire.dragonsteel_lightning_helmet");
    public static final Item DRAGONSTEEL_LIGHTNING_CHESTPLATE = new ItemDragonsteelArmor(DRAGONSTEEL_LIGHTNING_ARMOR_MATERIAL, 1, EquipmentSlotType.CHEST, "dragonsteel_lightning_chestplate", "iceandfire.dragonsteel_lightning_chestplate");
    public static final Item DRAGONSTEEL_LIGHTNING_LEGGINGS = new ItemDragonsteelArmor(DRAGONSTEEL_LIGHTNING_ARMOR_MATERIAL, 2, EquipmentSlotType.LEGS, "dragonsteel_lightning_leggings", "iceandfire.dragonsteel_lightning_leggings");
    public static final Item DRAGONSTEEL_LIGHTNING_BOOTS = new ItemDragonsteelArmor(DRAGONSTEEL_LIGHTNING_ARMOR_MATERIAL, 3, EquipmentSlotType.FEET, "dragonsteel_lightning_boots", "iceandfire.dragonsteel_lightning_boots");


    public static final Item WEEZER_BLUE_ALBUM = new ItemGeneric("weezer_blue_album", 1, true);
    public static final Item DRAGON_DEBUG_STICK = new ItemGeneric("dragon_debug_stick", 1, true);
    public static final Item DREAD_SWORD = new ItemModSword(DREAD_SWORD_TOOL_MATERIAL, "dread_sword");
    public static final Item DREAD_KNIGHT_SWORD = new ItemModSword(DREAD_KNIGHT_TOOL_MATERIAL, "dread_knight_sword");
    public static final Item LICH_STAFF = new ItemLichStaff();
    public static final Item DREAD_QUEEN_SWORD = new ItemModSword(DREAD_QUEEN_SWORD_TOOL_MATERIAL, "dread_queen_sword");
    public static final Item DREAD_QUEEN_STAFF = new ItemDreadQueenStaff();
    public static final Item DREAD_SHARD = new ItemGeneric("dread_shard", 0);
    public static final Item DREAD_KEY = new ItemGeneric("dread_key", 0);
    public static final Item HYDRA_FANG = new ItemGeneric("hydra_fang", 0);
    public static final Item HYDRA_HEART = new ItemHydraHeart();
    public static final Item HYDRA_ARROW = new ItemHydraArrow();
    public static final Item CANNOLI = new ItemCannoli();
    public static final Item ECTOPLASM = new ItemGeneric("ectoplasm");
    public static final Item GHOST_INGOT = new ItemGeneric("ghost_ingot", 1);
    public static final Item GHOST_SWORD = new ItemGhostSword();

    static {
        EnumDragonArmor.initArmors();
        EnumSeaSerpent.initArmors();
        EnumSkullType.initItems();
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        // Items
        try {
            for (Field f : IafItemRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof Item) {
                    if(((Item) obj).getRegistryName() != null){
                        event.getRegistry().register((Item) obj);
                    }
                } else if (obj instanceof Item[]) {
                    for (Item item : (Item[]) obj) {
                        if(item.getRegistryName() != null){
                            event.getRegistry().register(item);
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        for (EnumDragonArmor armor : EnumDragonArmor.values()) {
            event.getRegistry().register(armor.helmet);
            event.getRegistry().register(armor.chestplate);
            event.getRegistry().register(armor.leggings);
            event.getRegistry().register(armor.boots);
        }
        for (EnumSeaSerpent armor : EnumSeaSerpent.values()) {
            event.getRegistry().register(armor.scale);
            event.getRegistry().register(armor.helmet);
            event.getRegistry().register(armor.chestplate);
            event.getRegistry().register(armor.leggings);
            event.getRegistry().register(armor.boots);
        }
        for (EnumTroll.Weapon weapon : EnumTroll.Weapon.values()) {
            event.getRegistry().register(weapon.item);
        }
        for (EnumTroll troll : EnumTroll.values()) {
            event.getRegistry().register(troll.leather);
            event.getRegistry().register(troll.helmet);
            event.getRegistry().register(troll.chestplate);
            event.getRegistry().register(troll.leggings);
            event.getRegistry().register(troll.boots);
        }
        for (EnumSkullType skull : EnumSkullType.values()) {
            event.getRegistry().register(skull.skull_item);
        }
        IafRecipeRegistry.preInit();
        //Banner Patterns
        try {
            for (Field f : IafRecipeRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof BannerPattern) {
                    BannerPattern pattern = (BannerPattern)obj;
                    String name = f.getName().replace("PATTERN_", "").toLowerCase(Locale.ROOT);
                    event.getRegistry().register(new BannerPatternItem(pattern, (new Item.Properties()).maxStackSize(1).group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:banner_pattern_" + name));

                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        //spawn Eggs

        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.FIRE_DRAGON, 0X340000, 0XA52929, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_fire_dragon"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.ICE_DRAGON, 0XB5DDFB, 0X7EBAF0, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_ice_dragon"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.LIGHTNING_DRAGON, 0X422367, 0X725691, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_lightning_dragon"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.HIPPOGRYPH, 0XD8D8D8, 0XD1B55D, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_hippogryph"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.GORGON, 0XD0D99F, 0X684530, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_gorgon"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.PIXIE, 0XFF7F89, 0XE2CCE2, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_pixie"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.CYCLOPS, 0XB0826E, 0X3A1F0F, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_cyclops"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.SIREN, 0X8EE6CA, 0XF2DFC8, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_siren"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.HIPPOCAMPUS, 0X4491C7, 0X4FC56B, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_hippocampus"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.DEATH_WORM, 0XD1CDA3, 0X423A3A, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_death_worm"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.COCKATRICE, 0X8F5005, 0X4F5A23, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_cockatrice"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.STYMPHALIAN_BIRD, 0X744F37, 0X9E6C4B, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_stymphalian_bird"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.TROLL, 0X3D413D, 0X58433A, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_troll"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.MYRMEX_WORKER, 0XA16026, 0X594520, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_myrmex_worker"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.MYRMEX_SOLDIER, 0XA16026, 0X7D622D, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_myrmex_soldier"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.MYRMEX_SENTINEL, 0XA16026, 0XA27F3A, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_myrmex_sentinel"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.MYRMEX_ROYAL, 0XA16026, 0XC79B48, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_myrmex_royal"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.MYRMEX_QUEEN, 0XA16026, 0XECB855, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_myrmex_queen"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.AMPHITHERE, 0X597535, 0X00AA98, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_amphithere"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.SEA_SERPENT, 0X008299, 0XC5E6E7, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_sea_serpent"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.DREAD_THRALL, 0XE0E6E6, 0X00FFFF, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_dread_thrall"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.DREAD_GHOUL, 0XE0E6E6, 0X7B838A, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_dread_ghoul"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.DREAD_BEAST, 0XE0E6E6, 0X38373C, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_dread_beast"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.DREAD_SCUTTLER, 0XE0E6E6, 0X4D5667, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_dread_scuttler"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.DREAD_LICH, 0XE0E6E6, 0X274860, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_lich"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.DREAD_KNIGHT, 0XE0E6E6, 0X4A6C6E, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_dread_knight"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.DREAD_HORSE, 0XE0E6E6, 0XACACAC, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_dread_horse"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.HYDRA, 0X8B8B78, 0X2E372B, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_hydra"));
        event.getRegistry().register(new SpawnEggItem(IafEntityRegistry.GHOST, 0XB9EDB8, 0X73B276, new Item.Properties().group(IceAndFire.TAB_ITEMS)).setRegistryName("iceandfire:spawn_egg_ghost"));
    }


}