package com.github.alexthe666.iceandfire.config;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.alexthe666.citadel.config.biome.SpawnBiomeConfig;
import com.github.alexthe666.citadel.config.biome.SpawnBiomeData;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.google.common.collect.Lists;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class BiomeConfig {
    public static Pair<String, SpawnBiomeData> oreGenBiomes = Pair.of("iceandfire:ore_gen_biomes", DefaultBiomes.OVERWORLD);
    public static Pair<String, SpawnBiomeData> sapphireBiomes = Pair.of("iceandfire:sapphire_gen_biomes", DefaultBiomes.VERY_SNOWY);
    public static Pair<String, SpawnBiomeData> amethystBiomes = Pair.of("iceandfire:amethyst_gen_biomes", DefaultBiomes.SAVANNAS);
    public static Pair<String, SpawnBiomeData> fireLilyBiomes = Pair.of("iceandfire:fire_lily_biomes", DefaultBiomes.VERY_HOT);
    public static Pair<String, SpawnBiomeData> iceLilyBiomes = Pair.of("iceandfire:ice_lily_biomes", DefaultBiomes.VERY_SNOWY);
    public static Pair<String, SpawnBiomeData> lightningLilyBiomes = Pair.of("iceandfire:lightning_lily_biomes", DefaultBiomes.SAVANNAS);
    public static Pair<String, SpawnBiomeData> fireDragonBiomes = Pair.of("iceandfire:fire_dragon_biomes", DefaultBiomes.FIREDRAGON_ROOST);
    public static Pair<String, SpawnBiomeData> iceDragonBiomes = Pair.of("iceandfire:ice_dragon_biomes", DefaultBiomes.ICEDRAGON_ROOST);
    public static Pair<String, SpawnBiomeData> lightningDragonBiomes = Pair.of("iceandfire:lightning_dragon_biomes", DefaultBiomes.LIGHTNING_ROOST);
    public static Pair<String, SpawnBiomeData> gorgonTempleBiomes = Pair.of("iceandfire:gorgon_temple_biomes", DefaultBiomes.BEACHES);
    public static Pair<String, SpawnBiomeData> cyclopsCaveBiomes = Pair.of("iceandfire:cyclops_cave_biomes", DefaultBiomes.BEACHES);
    public static Pair<String, SpawnBiomeData> hippogryphBiomes = Pair.of("iceandfire:hippogryph_biomes", DefaultBiomes.HILLS);
    public static Pair<String, SpawnBiomeData> pixieBiomes = Pair.of("iceandfire:pixie_village_biomes", DefaultBiomes.PIXIES);
    public static Pair<String, SpawnBiomeData> hippocampusBiomes = Pair.of("iceandfire:hippocampus_biomes", DefaultBiomes.OCEANS);
    public static Pair<String, SpawnBiomeData> seaSerpentBiomes = Pair.of("iceandfire:sea_serpent_biomes", DefaultBiomes.OCEANS);
    public static Pair<String, SpawnBiomeData> sirenBiomes = Pair.of("iceandfire:siren_biomes", DefaultBiomes.OCEANS);
    public static Pair<String, SpawnBiomeData> amphithereBiomes = Pair.of("iceandfire:amphithere_biomes", DefaultBiomes.JUNGLE);
    public static Pair<String, SpawnBiomeData> desertMyrmexBiomes = Pair.of("iceandfire:desert_myrmex_biomes", DefaultBiomes.DESERT);
    public static Pair<String, SpawnBiomeData> jungleMyrmexBiomes = Pair.of("iceandfire:jungle_myrmex_biomes", DefaultBiomes.JUNGLE);
    public static Pair<String, SpawnBiomeData> snowyTrollBiomes = Pair.of("iceandfire:snowy_troll_biomes", DefaultBiomes.SNOWY);
    public static Pair<String, SpawnBiomeData> forestTrollBiomes = Pair.of("iceandfire:forest_troll_biomes", DefaultBiomes.WOODLAND);
    public static Pair<String, SpawnBiomeData> mountainTrollBiomes = Pair.of("iceandfire:mountain_troll_biomes", DefaultBiomes.VERY_HILLY);

    public static Pair<String, SpawnBiomeData> stymphalianBiomes = Pair.of("iceandfire:stymphalian_bird_biomes", DefaultBiomes.SWAMPS);
    public static Pair<String, SpawnBiomeData> hydraBiomes = Pair.of("iceandfire:hydra_cave_biomes", DefaultBiomes.SWAMPS);
    public static Pair<String, SpawnBiomeData> mausoleumBiomes = Pair.of("iceandfire:mausoleum_biomes", DefaultBiomes.VERY_SNOWY);
    public static Pair<String, SpawnBiomeData> graveyardBiomes = Pair.of("iceandfire:graveyard_biomes", DefaultBiomes.GRAVEYARD);
    public static Pair<String, SpawnBiomeData> cockatriceBiomes = Pair.of("iceandfire:cockatrice_biomes", DefaultBiomes.SAVANNAS);
    public static Pair<String, SpawnBiomeData> deathwormBiomes = Pair.of("iceandfire:deathworm_biomes", DefaultBiomes.DESERT);
    public static Pair<String, SpawnBiomeData> wanderingCyclopsBiomes = Pair.of("iceandfire:wandering_cyclops_biomes", DefaultBiomes.PLAINS);
    public static Pair<String, SpawnBiomeData> lightningDragonSkeletonBiomes = Pair.of("iceandfire:lightning_dragon_skeleton_biomes", DefaultBiomes.SAVANNAS);
    public static Pair<String, SpawnBiomeData> fireDragonSkeletonBiomes = Pair.of("iceandfire:fire_dragon_skeleton_biomes", DefaultBiomes.DESERT);
    public static Pair<String, SpawnBiomeData> iceDragonSkeletonBiomes = Pair.of("iceandfire:ice_dragon_skeleton_biomes", DefaultBiomes.VERY_SNOWY);

    public static Pair<String, SpawnBiomeData> blackHippogryphBiomes = Pair.of("iceandfire:hippogryph_black_biomes",DefaultBiomes.HIPPOGRYPH_BLACK);
    public static Pair<String, SpawnBiomeData> brownHippogryphBiomes = Pair.of("iceandfire:hippogryph_brown_biomes", DefaultBiomes.VERY_HILLY);
    public static Pair<String, SpawnBiomeData> grayHippogryphBiomes = Pair.of("iceandfire:hippogryph_gray_biomes", DefaultBiomes.HIPPOGRYPH_GRAY);
    public static Pair<String, SpawnBiomeData> chestnutHippogryphBiomes = Pair.of("iceandfire:hippogryph_chestnut_biomes", DefaultBiomes.HIPPOGRYPH_CHESTNUT);
    public static Pair<String, SpawnBiomeData> creamyHippogryphBiomes = Pair.of("iceandfire:hippogryph_creamy_biomes", DefaultBiomes.HIPPOGRYPH_CREAMY);
    public static Pair<String, SpawnBiomeData> darkBrownHippogryphBiomes = Pair.of("iceandfire:hippogryph_dark_brown_biomes", DefaultBiomes.HIPPOGRYPH_DBROWN);
    public static Pair<String, SpawnBiomeData> whiteHippogryphBiomes = Pair.of("iceandfire:hippogryph_white_biomes", DefaultBiomes.HIPPOGRYPH_WHITE);

    private static boolean init = false;
    private static Map<String, SpawnBiomeData> biomeConfigValues = new HashMap<>();

    public static void init() {
        try {
            for (Field f : BiomeConfig.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if(obj instanceof Pair){
                    String id = (String)((Pair) obj).getLeft();
                    SpawnBiomeData data = (SpawnBiomeData)((Pair) obj).getRight();
                    biomeConfigValues.put(id, SpawnBiomeConfig.create(new ResourceLocation(id), data));
                }
            }
        }catch (Exception e){
            IceAndFire.LOGGER.warn("Encountered error building iceandfire biome config .json files");
            e.printStackTrace();
        }
        init = true;
    }

    public static boolean test(Pair<String, SpawnBiomeData> entry, Biome biome){
        if(!init){
            return false;
        }
        return biomeConfigValues.get(entry.getKey()).matches(biome);
    }
}
