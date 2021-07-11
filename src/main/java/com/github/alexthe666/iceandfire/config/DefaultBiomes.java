package com.github.alexthe666.iceandfire.config;

import com.github.alexthe666.citadel.config.biome.BiomeEntryType;
import com.github.alexthe666.citadel.config.biome.SpawnBiomeData;

public class DefaultBiomes {

    public static final SpawnBiomeData OVERWORLD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0);

    public static final SpawnBiomeData FIREDRAGON_ROOST = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "hot", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "jungle", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "mesa", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "savanna", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "plains", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "wet", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "cold", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "jungle", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "mesa", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "savanna", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "forest", 1);
            //Not sure if firedragons should generate in mountainous terrain or not
            /*.addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 2)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "mountain", 2)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "snowy", 2);*/

    public static final SpawnBiomeData ICEDRAGON_ROOST = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "icy", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "snowy", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 2)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "cold", 2);

    public static final SpawnBiomeData LIGHTNING_ROOST = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "jungle", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "mesa", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 2)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "savanna", 2);

    public static final SpawnBiomeData VERY_HOT = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "hot", 0);

    public static final SpawnBiomeData VERY_SNOWY = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "snowy", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "icy", 1);

    public static final SpawnBiomeData SNOWY = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "snowy", 0);

    public static final SpawnBiomeData VERY_HILLY = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "mountains", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "extreme_hills", 1);

    public static final SpawnBiomeData WOODLAND = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "forest", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "forest", 1);

    public static final SpawnBiomeData SAVANNAS = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "savanna", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "savanna", 1);

    public static final SpawnBiomeData BEACHES = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "beach", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "beach", 1);

    public static final SpawnBiomeData SWAMPS = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "swamp", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "swamp", 1);

    public static final SpawnBiomeData DESERT = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "desert", 0);

    public static final SpawnBiomeData OCEANS = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "ocean", 1);

    public static final SpawnBiomeData PIXIES = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "rare", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "magical", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "dense", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "forest", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "mountain", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "hills", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "wet", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "hot", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "cold", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "dry", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, true, "taiga", 1);

    public static final SpawnBiomeData JUNGLE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "jungle", 0);

    public static final SpawnBiomeData HILLS = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "mesa", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "mountain", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 2)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "hills", 2)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 3)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "extreme_hills", 3);


    public static final SpawnBiomeData PLAINS = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "plains", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, false, "plains", 1);

    public static final SpawnBiomeData GRAVEYARD = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, true, "ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, true, "beach", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_CATEGORY, true, "beach", 0);

    public static final SpawnBiomeData HIPPOGRYPH_BLACK = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:desert_hills", 0);

    public static final SpawnBiomeData HIPPOGRYPH_DBROWN = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:taiga_mountains", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:taiga_hills", 1);

    public static final SpawnBiomeData HIPPOGRYPH_WHITE = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:snowy_mountains", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 1)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:snowy_taiga_hills", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 2)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:snowy_taiga_mountains", 2);

    public static final SpawnBiomeData HIPPOGRYPH_GRAY = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:dark_forest_hills", 0);

    public static final SpawnBiomeData HIPPOGRYPH_CHESTNUT = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:wooded_mountains", 0);

    public static final SpawnBiomeData HIPPOGRYPH_CREAMY = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_DICT, false, "overworld", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:savanna_plateau", 0);

}
