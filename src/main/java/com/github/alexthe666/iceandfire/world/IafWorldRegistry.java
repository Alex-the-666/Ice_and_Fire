package com.github.alexthe666.iceandfire.world;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.config.BiomeConfig;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.world.feature.*;
import com.github.alexthe666.iceandfire.world.gen.*;
import com.github.alexthe666.iceandfire.world.structure.DreadMausoleumStructure;
import com.github.alexthe666.iceandfire.world.structure.DummyPiece;
import com.github.alexthe666.iceandfire.world.structure.GorgonTempleStructure;
import com.github.alexthe666.iceandfire.world.structure.GraveyardStructure;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class IafWorldRegistry {

    public static Feature<NoFeatureConfig> FIRE_DRAGON_ROOST;
    public static Feature<NoFeatureConfig> ICE_DRAGON_ROOST;
    public static Feature<NoFeatureConfig> LIGHTNING_DRAGON_ROOST;
    public static Feature<NoFeatureConfig> FIRE_DRAGON_CAVE;
    public static Feature<NoFeatureConfig> ICE_DRAGON_CAVE;
    public static Feature<NoFeatureConfig> LIGHTNING_DRAGON_CAVE;
    public static Feature<NoFeatureConfig> CYCLOPS_CAVE;
    public static Feature<NoFeatureConfig> PIXIE_VILLAGE;
    public static Feature<NoFeatureConfig> SIREN_ISLAND;
    public static Feature<NoFeatureConfig> HYDRA_CAVE;
    public static Feature<NoFeatureConfig> MYRMEX_HIVE_DESERT;
    public static Feature<NoFeatureConfig> MYRMEX_HIVE_JUNGLE;
    public static Feature<NoFeatureConfig> SPAWN_DEATH_WORM;
    public static Feature<NoFeatureConfig> SPAWN_DRAGON_SKELETON_L;
    public static Feature<NoFeatureConfig> SPAWN_DRAGON_SKELETON_F;
    public static Feature<NoFeatureConfig> SPAWN_DRAGON_SKELETON_I;
    public static Feature<NoFeatureConfig> SPAWN_HIPPOCAMPUS;
    public static Feature<NoFeatureConfig> SPAWN_SEA_SERPENT;
    public static Feature<NoFeatureConfig> SPAWN_STYMPHALIAN_BIRD;
    public static Feature<NoFeatureConfig> SPAWN_WANDERING_CYCLOPS;
    public static IStructurePieceType DUMMY_PIECE;
    public static Structure<NoFeatureConfig> MAUSOLEUM = new DreadMausoleumStructure(NoFeatureConfig.CODEC);
    public static Structure<NoFeatureConfig> GORGON_TEMPLE = new GorgonTempleStructure(NoFeatureConfig.CODEC);
    public static Structure<NoFeatureConfig> GRAVEYARD = new GraveyardStructure(NoFeatureConfig.CODEC);
    public static ConfiguredFeature FIRE_LILY_CF;
    public static ConfiguredFeature FROST_LILY_CF;
    public static ConfiguredFeature LIGHTNING_LILY_CF;
    public static ConfiguredFeature COPPER_ORE_CF;
    public static ConfiguredFeature SILVER_ORE_CF;
    public static ConfiguredFeature SAPPHIRE_ORE_CF;
    public static ConfiguredFeature AMETHYST_ORE_CF;
    public static ConfiguredFeature FIRE_DRAGON_ROOST_CF;
    public static ConfiguredFeature ICE_DRAGON_ROOST_CF;
    public static ConfiguredFeature LIGHTNING_DRAGON_ROOST_CF;
    public static ConfiguredFeature FIRE_DRAGON_CAVE_CF;
    public static ConfiguredFeature ICE_DRAGON_CAVE_CF;
    public static ConfiguredFeature LIGHTNING_DRAGON_CAVE_CF;
    public static ConfiguredFeature CYCLOPS_CAVE_CF;
    public static ConfiguredFeature PIXIE_VILLAGE_CF;
    public static ConfiguredFeature SIREN_ISLAND_CF;
    public static ConfiguredFeature HYDRA_CAVE_CF;
    public static ConfiguredFeature MYRMEX_HIVE_DESERT_CF;
    public static ConfiguredFeature MYRMEX_HIVE_JUNGLE_CF;
    public static ConfiguredFeature SPAWN_DEATH_WORM_CF;
    public static ConfiguredFeature SPAWN_DRAGON_SKELETON_L_CF;
    public static ConfiguredFeature SPAWN_DRAGON_SKELETON_F_CF;
    public static ConfiguredFeature SPAWN_DRAGON_SKELETON_I_CF;
    public static ConfiguredFeature SPAWN_HIPPOCAMPUS_CF;
    public static ConfiguredFeature SPAWN_SEA_SERPENT_CF;
    public static ConfiguredFeature SPAWN_STYMPHALIAN_BIRD_CF;
    public static ConfiguredFeature SPAWN_WANDERING_CYCLOPS_CF;
    public static StructureFeature GORGON_TEMPLE_CF;
    public static StructureFeature MAUSOLEUM_CF;
    public static StructureFeature GRAVEYARD_CF;
    public static List<Feature<?>> featureList = new ArrayList<>();
    public static List<Structure<?>> structureFeatureList = new ArrayList<>();


    public static void register() {
        FIRE_DRAGON_ROOST = registerFeature("iceandfire:fire_dragon_roost", new WorldGenFireDragonRoosts(NoFeatureConfig.CODEC));
        ICE_DRAGON_ROOST = registerFeature("iceandfire:ice_dragon_roost", new WorldGenIceDragonRoosts(NoFeatureConfig.CODEC));
        LIGHTNING_DRAGON_ROOST = registerFeature("iceandfire:lightning_dragon_roost", new WorldGenLightningDragonRoosts(NoFeatureConfig.CODEC));
        FIRE_DRAGON_CAVE = registerFeature("iceandfire:fire_dragon_cave", new WorldGenFireDragonCave(NoFeatureConfig.CODEC));
        ICE_DRAGON_CAVE = registerFeature("iceandfire:ice_dragon_cave", new WorldGenIceDragonCave(NoFeatureConfig.CODEC));
        LIGHTNING_DRAGON_CAVE = registerFeature("iceandfire:lightning_dragon_cave", new WorldGenLightningDragonCave(NoFeatureConfig.CODEC));
        CYCLOPS_CAVE = registerFeature("iceandfire:cyclops_cave", new WorldGenCyclopsCave(NoFeatureConfig.CODEC));
        PIXIE_VILLAGE = registerFeature("iceandfire:pixie_village", new WorldGenPixieVillage(NoFeatureConfig.CODEC));
        SIREN_ISLAND = registerFeature("iceandfire:siren_island", new WorldGenSirenIsland(NoFeatureConfig.CODEC));
        HYDRA_CAVE = registerFeature("iceandfire:hydra_cave", new WorldGenHydraCave(NoFeatureConfig.CODEC));
        MYRMEX_HIVE_DESERT = registerFeature("iceandfire:myrmex_hive_desert", new WorldGenMyrmexHive(false, false, NoFeatureConfig.CODEC));
        MYRMEX_HIVE_JUNGLE = registerFeature("iceandfire:myrmex_hive_jungle", new WorldGenMyrmexHive(false, true, NoFeatureConfig.CODEC));

        SPAWN_DEATH_WORM = registerFeature("iceandfire:spawn_death_worm", new SpawnDeathWorm(NoFeatureConfig.CODEC));
        SPAWN_DRAGON_SKELETON_L = registerFeature("iceandfire:spawn_dragon_skeleton_l", new SpawnDragonSkeleton(IafEntityRegistry.LIGHTNING_DRAGON, NoFeatureConfig.CODEC));
        SPAWN_DRAGON_SKELETON_F = registerFeature("iceandfire:spawn_dragon_skeleton_f", new SpawnDragonSkeleton(IafEntityRegistry.FIRE_DRAGON, NoFeatureConfig.CODEC));
        SPAWN_DRAGON_SKELETON_I = registerFeature("iceandfire:spawn_dragon_skeleton_i", new SpawnDragonSkeleton(IafEntityRegistry.ICE_DRAGON, NoFeatureConfig.CODEC));
        SPAWN_HIPPOCAMPUS = registerFeature("iceandfire:spawn_hippocampus", new SpawnHippocampus(NoFeatureConfig.CODEC));
        SPAWN_SEA_SERPENT = registerFeature("iceandfire:spawn_sea_serpent", new SpawnSeaSerpent(NoFeatureConfig.CODEC));
        SPAWN_STYMPHALIAN_BIRD = registerFeature("iceandfire:spawn_stymphalian_bird", new SpawnStymphalianBird(NoFeatureConfig.CODEC));
        SPAWN_WANDERING_CYCLOPS = registerFeature("iceandfire:spawn_wandering_cyclops", new SpawnWanderingCyclops(NoFeatureConfig.CODEC));

        // Technically we don't need the piece classes anymore but we should register dummy pieces
        // under the same registry name or else player's will get logspammed by Minecraft in existing worlds.
        DUMMY_PIECE = Registry.register(Registry.STRUCTURE_PIECE, "iceandfire:gorgon_piece", DummyPiece::new);
        Registry.register(Registry.STRUCTURE_PIECE, "iceandfire:mausoleum_piece", DummyPiece::new);
        Registry.register(Registry.STRUCTURE_PIECE, "iceandfire:gorgon_piece_empty", DummyPiece::new);
        Registry.register(Registry.STRUCTURE_PIECE, "iceandfire:graveyard_piece", DummyPiece::new);

        MAUSOLEUM = registerStructureFeature( "iceandfire:mausoleum", MAUSOLEUM);
        putStructureOnAList("iceandfire:mausoleum", MAUSOLEUM);

        GORGON_TEMPLE = registerStructureFeature( "iceandfire:gorgon_temple", GORGON_TEMPLE);
        putStructureOnAList("iceandfire:gorgon_temple", GORGON_TEMPLE);

        GRAVEYARD = registerStructureFeature( "iceandfire:graveyard", GRAVEYARD);
        putStructureOnAList("iceandfire:graveyard", GRAVEYARD);

        addStructureSeperation(DimensionSettings.OVERWORLD, GORGON_TEMPLE, new StructureSeparationSettings(Math.max(IafConfig.spawnGorgonsChance, 2), Math.max(IafConfig.spawnGorgonsChance / 2, 1), 342226450));
        addStructureSeperation(DimensionSettings.OVERWORLD, MAUSOLEUM, new StructureSeparationSettings(Math.max(IafConfig.generateMausoleumChance, 2), Math.max(IafConfig.generateMausoleumChance / 2, 1), 342226451));
        addStructureSeperation(DimensionSettings.OVERWORLD, GRAVEYARD, new StructureSeparationSettings(Math.max(IafConfig.generateGraveyardChance * 3, 2), Math.max(IafConfig.generateGraveyardChance * 3 / 2, 1), 342226440));

        GORGON_TEMPLE_CF = Registry.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, "iceandfire:gorgon_temple", GORGON_TEMPLE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        MAUSOLEUM_CF = Registry.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, "iceandfire:mausoleum", MAUSOLEUM.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        GRAVEYARD_CF = Registry.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, "iceandfire:graveyard", GRAVEYARD.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));

        Structure.field_236384_t_= ImmutableList.<Structure<?>>builder().addAll(Structure.field_236384_t_).add(GORGON_TEMPLE, MAUSOLEUM, GRAVEYARD).build();

        COPPER_ORE_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:copper_ore", Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, IafBlockRegistry.COPPER_ORE.getDefaultState(), 5)).range(128).square().count(5));
        SILVER_ORE_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:silver_ore", Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, IafBlockRegistry.SILVER_ORE.getDefaultState(), 8)).range(32).square().count(2));
        SAPPHIRE_ORE_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:sapphire_ore", Feature.EMERALD_ORE.withConfiguration(new ReplaceBlockConfig(Blocks.STONE.getDefaultState(), IafBlockRegistry.SAPPHIRE_ORE.getDefaultState())).withPlacement(Placement.EMERALD_ORE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
        AMETHYST_ORE_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:amythest_ore", Feature.EMERALD_ORE.withConfiguration(new ReplaceBlockConfig(Blocks.STONE.getDefaultState(), IafBlockRegistry.AMYTHEST_ORE.getDefaultState())).withPlacement(Placement.EMERALD_ORE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
        FIRE_LILY_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:fire_lily", Feature.FLOWER.withConfiguration(new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(IafBlockRegistry.FIRE_LILY.getDefaultState()), new SimpleBlockPlacer()).tries(1).build()).withPlacement(Features.Placements.VEGETATION_PLACEMENT).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT));
        FROST_LILY_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:frost_lily", Feature.FLOWER.withConfiguration(new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(IafBlockRegistry.FROST_LILY.getDefaultState()), new SimpleBlockPlacer()).tries(1).build()).withPlacement(Features.Placements.VEGETATION_PLACEMENT).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT));
        LIGHTNING_LILY_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:lightning_lily", Feature.FLOWER.withConfiguration(new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(IafBlockRegistry.LIGHTNING_LILY.getDefaultState()), new SimpleBlockPlacer()).tries(1).build()).withPlacement(Features.Placements.VEGETATION_PLACEMENT).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT));
        FIRE_DRAGON_ROOST_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:fire_dragon_roost", FIRE_DRAGON_ROOST.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        ICE_DRAGON_ROOST_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:ice_dragon_roost", ICE_DRAGON_ROOST.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        LIGHTNING_DRAGON_ROOST_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:lightning_dragon_roost", LIGHTNING_DRAGON_ROOST.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        FIRE_DRAGON_CAVE_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:fire_dragon_cave", FIRE_DRAGON_CAVE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        ICE_DRAGON_CAVE_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:ice_dragon_cave", ICE_DRAGON_CAVE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        LIGHTNING_DRAGON_CAVE_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:lightning_dragon_cave", LIGHTNING_DRAGON_CAVE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        CYCLOPS_CAVE_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:cyclops_cave", CYCLOPS_CAVE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        PIXIE_VILLAGE_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:pixie_village", PIXIE_VILLAGE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        SIREN_ISLAND_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:siren_island", SIREN_ISLAND.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        HYDRA_CAVE_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:hydra_cave", HYDRA_CAVE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        MYRMEX_HIVE_DESERT_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:myrmex_hive_desert", MYRMEX_HIVE_DESERT.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        MYRMEX_HIVE_JUNGLE_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:myrmex_hive_jungle", MYRMEX_HIVE_JUNGLE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        
        SPAWN_DEATH_WORM_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:spawn_death_worm_misc", SPAWN_DEATH_WORM.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        SPAWN_DRAGON_SKELETON_L_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:spawn_dragon_skeleton_l_misc", SPAWN_DRAGON_SKELETON_L.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        SPAWN_DRAGON_SKELETON_F_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:spawn_dragon_skeleton_f_misc", SPAWN_DRAGON_SKELETON_F.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        SPAWN_DRAGON_SKELETON_I_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:spawn_dragon_skeleton_i_misc", SPAWN_DRAGON_SKELETON_I.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        SPAWN_HIPPOCAMPUS_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:spawn_hippocampus_misc", SPAWN_HIPPOCAMPUS.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        SPAWN_SEA_SERPENT_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:spawn_sea_serpent_misc", SPAWN_SEA_SERPENT.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        SPAWN_STYMPHALIAN_BIRD_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:spawn_stymphalian_bird_misc", SPAWN_STYMPHALIAN_BIRD.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        SPAWN_WANDERING_CYCLOPS_CF = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "iceandfire:spawn_wandering_cyclops_misc", SPAWN_WANDERING_CYCLOPS.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
    }

    public static void addStructureSeperation(RegistryKey<DimensionSettings> preset, Structure structure, StructureSeparationSettings settings) {
        WorldGenRegistries.NOISE_SETTINGS.getValueForKey(preset).getStructures().func_236195_a_().put(structure, settings);
    }

    public static <F extends Structure<?>> void putStructureOnAList(String nameForList, F structure) {
        Structure.NAME_STRUCTURE_BIMAP.put(nameForList.toLowerCase(Locale.ROOT), structure);
    }

    private static Feature<NoFeatureConfig> registerFeature(String registryName, Feature<NoFeatureConfig> feature) {
        featureList.add(feature.setRegistryName(registryName));
        return feature;
    }


    private static Structure<NoFeatureConfig> registerStructureFeature(String registryName, Structure<NoFeatureConfig> feature) {
        structureFeatureList.add(feature);
        return feature;
    }

    public static void setup() {
    }

    public static boolean isFarEnoughFromSpawn(IWorld world, BlockPos pos) {
        BlockPos spawnRelative = new BlockPos(0, pos.getY(), 0);
        boolean spawnCheck = !spawnRelative.withinDistance(pos, IafConfig.dangerousWorldGenDistanceLimit);
        return spawnCheck;
    }

    public static boolean isDimensionListedForFeatures(IServerWorld world) {
        ResourceLocation name = world.getWorld().getDimensionKey().getLocation();
        if (name == null) {
            return false;
        }
        if (IafConfig.useDimensionBlackList) {
            for (String blacklisted : IafConfig.featureBlacklistedDimensions) {
                if (name.toString().equals(blacklisted)) {
                    return false;
                }
            }
            return true;
        } else {
            for (String whitelist : IafConfig.featureWhitelistedDimensions) {
                if (name.toString().equals(whitelist)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean isDimensionListedForDragons(IServerWorld world) {
        ResourceLocation name = world.getWorld().getDimensionKey().getLocation();
        if (name == null) {
            return false;
        }
        if (IafConfig.useDimensionBlackList) {
            for (String blacklisted : IafConfig.dragonBlacklistedDimensions) {
                if (name.toString().equals(blacklisted)) {
                    return false;
                }
            }
            return true;
        } else {
            for (String whitelist : IafConfig.dragonWhitelistedDimensions) {
                if (name.toString().equals(whitelist)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean isDimensionListedForMobs(IServerWorld world) {
        ResourceLocation name = world.getWorld().getDimensionKey().getLocation();
        if (name == null) {
            return false;
        }
        if (IafConfig.useDimensionBlackList) {
            for (String blacklisted : IafConfig.mobBlacklistedDimensions) {
                if (name.toString().equals(blacklisted)) {
                    return false;
                }
            }
            return true;
        } else {
            for (String whitelist : IafConfig.mobWhitelistedDimensions) {
                if (name.toString().equals(whitelist)) {
                    return true;
                }
            }
            return false;
        }
    }
    public static boolean isFarEnoughFromDangerousGen(IServerWorld world, BlockPos pos) {
        boolean canGen = true;
        IafWorldData data = IafWorldData.get(world.getWorld());
        if (data != null) {
            BlockPos last = data.lastGeneratedDangerousStructure;
            canGen = last.distanceSq(pos) > IafConfig.dangerousWorldGenSeparationLimit * IafConfig.dangerousWorldGenSeparationLimit;
            if (canGen) {
                data.setLastGeneratedDangerousStructure(pos);
            }

        }
        return canGen;
    }

    public static HashMap<String, Boolean> LOADED_FEATURES;
    static {
    	LOADED_FEATURES = new HashMap<String, Boolean>();
    	LOADED_FEATURES.put("FIRE_LILY_CF", false);
    	LOADED_FEATURES.put("FROST_LILY_CF", false);
    	LOADED_FEATURES.put("LIGHTNING_LILY_CF", false);
    	LOADED_FEATURES.put("COPPER_ORE_CF", false);
    	LOADED_FEATURES.put("SILVER_ORE_CF", false);
    	LOADED_FEATURES.put("SAPPHIRE_ORE_CF", false);
    	LOADED_FEATURES.put("AMETHYST_ORE_CF", false);
    	LOADED_FEATURES.put("FIRE_DRAGON_ROOST_CF", false);
    	LOADED_FEATURES.put("ICE_DRAGON_ROOST_CF", false);
    	LOADED_FEATURES.put("LIGHTNING_DRAGON_ROOST_CF", false);
    	LOADED_FEATURES.put("FIRE_DRAGON_CAVE_CF", false);
    	LOADED_FEATURES.put("ICE_DRAGON_CAVE_CF", false);
    	LOADED_FEATURES.put("LIGHTNING_DRAGON_CAVE_CF", false);
    	LOADED_FEATURES.put("CYCLOPS_CAVE_CF", false);
    	LOADED_FEATURES.put("PIXIE_VILLAGE_CF", false);
    	LOADED_FEATURES.put("SIREN_ISLAND_CF", false);
    	LOADED_FEATURES.put("HYDRA_CAVE_CF", false);
    	LOADED_FEATURES.put("MYRMEX_HIVE_DESERT_CF", false);
    	LOADED_FEATURES.put("MYRMEX_HIVE_JUNGLE_CF", false);
    	LOADED_FEATURES.put("SPAWN_DEATH_WORM_CF", false);
    	LOADED_FEATURES.put("SPAWN_DRAGON_SKELETON_L_CF", false);
    	LOADED_FEATURES.put("SPAWN_DRAGON_SKELETON_F_CF", false);
    	LOADED_FEATURES.put("SPAWN_DRAGON_SKELETON_I_CF", false);
    	LOADED_FEATURES.put("SPAWN_HIPPOCAMPUS_CF", false);
    	LOADED_FEATURES.put("SPAWN_SEA_SERPENT_CF", false);
    	LOADED_FEATURES.put("SPAWN_STYMPHALIAN_BIRD_CF", false);
    	LOADED_FEATURES.put("SPAWN_WANDERING_CYCLOPS_CF", false);
    	LOADED_FEATURES.put("GORGON_TEMPLE_CF", false);
    	LOADED_FEATURES.put("MAUSOLEUM_CF", false);
    	LOADED_FEATURES.put("GRAVEYARD_CF", false);
    }
    public static void onBiomesLoad(BiomeLoadingEvent event) {
    	Biome biome = ForgeRegistries.BIOMES.getValue(event.getName());

    	if (BiomeConfig.test(BiomeConfig.fireLilyBiomes, biome)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, FIRE_LILY_CF);
        	LOADED_FEATURES.put("FIRE_LILY_CF", true);
        }
    	if (BiomeConfig.test(BiomeConfig.lightningLilyBiomes, biome)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, LIGHTNING_LILY_CF);
        	LOADED_FEATURES.put("LIGHTNING_LILY_CF", true);
        }
    	if (BiomeConfig.test(BiomeConfig.iceLilyBiomes, biome)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, FROST_LILY_CF);
        	LOADED_FEATURES.put("FROST_LILY_CF", true);
        }
    	if (BiomeConfig.test(BiomeConfig.oreGenBiomes, biome)) {
            if (IafConfig.generateSilverOre) {
                event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, SILVER_ORE_CF);
            	LOADED_FEATURES.put("SILVER_ORE_CF", true);
            }
            if (IafConfig.generateCopperOre) {
                event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, COPPER_ORE_CF);
            	LOADED_FEATURES.put("COPPER_ORE_CF", true);
            }
        }
        if (IafConfig.generateSapphireOre && BiomeConfig.test(BiomeConfig.sapphireBiomes, biome)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, SAPPHIRE_ORE_CF);
        	LOADED_FEATURES.put("SAPPHIRE_ORE_CF", true);
        }
        if (IafConfig.generateAmythestOre && BiomeConfig.test(BiomeConfig.amethystBiomes, biome)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, AMETHYST_ORE_CF);
        	LOADED_FEATURES.put("AMETHYST_ORE_CF", true);
        }
        if (IafConfig.generateDragonRoosts) {
            if (BiomeConfig.test(BiomeConfig.fireDragonBiomes, biome)) {
                event.getGeneration().withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, FIRE_DRAGON_ROOST_CF);
                event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, FIRE_DRAGON_CAVE_CF);
            	LOADED_FEATURES.put("FIRE_DRAGON_ROOST_CF", true);
            	LOADED_FEATURES.put("FIRE_DRAGON_CAVE_CF", true);
            }
            if (BiomeConfig.test(BiomeConfig.lightningDragonBiomes, biome)) {
                event.getGeneration().withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, LIGHTNING_DRAGON_ROOST_CF);
                event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, LIGHTNING_DRAGON_CAVE_CF);
            	LOADED_FEATURES.put("LIGHTNING_DRAGON_ROOST_CF", true);
            	LOADED_FEATURES.put("LIGHTNING_DRAGON_CAVE_CF", true);
            }
            if (BiomeConfig.test(BiomeConfig.iceDragonBiomes, biome)) {
                event.getGeneration().withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, ICE_DRAGON_ROOST_CF);
                event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, ICE_DRAGON_CAVE_CF);
            	LOADED_FEATURES.put("ICE_DRAGON_ROOST_CF", true);
            	LOADED_FEATURES.put("ICE_DRAGON_CAVE_CF", true);
            }
        }
        if (IafConfig.generateCyclopsCaves && BiomeConfig.test(BiomeConfig.cyclopsCaveBiomes, biome)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, CYCLOPS_CAVE_CF);
        	LOADED_FEATURES.put("CYCLOPS_CAVE_CF", true);
        }
        if (IafConfig.spawnGorgons && BiomeConfig.test(BiomeConfig.gorgonTempleBiomes, biome)) {
            event.getGeneration().withStructure(GORGON_TEMPLE_CF);
        	LOADED_FEATURES.put("GORGON_TEMPLE_CF", true);
        }
        if (IafConfig.spawnPixies && BiomeConfig.test(BiomeConfig.pixieBiomes, biome)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, PIXIE_VILLAGE_CF);
        }
        if (IafConfig.generateHydraCaves && BiomeConfig.test(BiomeConfig.hydraBiomes, biome)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, HYDRA_CAVE_CF);
        	LOADED_FEATURES.put("HYDRA_CAVE_CF", true);
        }
        if (IafConfig.generateMausoleums && BiomeConfig.test(BiomeConfig.mausoleumBiomes, biome)) {
            event.getGeneration().withStructure(MAUSOLEUM_CF);
        	LOADED_FEATURES.put("MAUSOLEUM_CF", true);
        }
        if (IafConfig.generateGraveyards && BiomeConfig.test(BiomeConfig.graveyardBiomes, biome)) {
            event.getGeneration().withStructure(GRAVEYARD_CF);
        	LOADED_FEATURES.put("GRAVEYARD_CF", true);
        }
        if (IafConfig.generateMyrmexColonies && BiomeConfig.test(BiomeConfig.desertMyrmexBiomes, biome)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, MYRMEX_HIVE_DESERT_CF);
        	LOADED_FEATURES.put("MYRMEX_HIVE_DESERT_CF", true);
        }
        if (IafConfig.generateMyrmexColonies && BiomeConfig.test(BiomeConfig.jungleMyrmexBiomes, biome)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, MYRMEX_HIVE_JUNGLE_CF);
        	LOADED_FEATURES.put("MYRMEX_HIVE_JUNGLE_CF", true);
        }
        if (IafConfig.generateSirenIslands && BiomeConfig.test(BiomeConfig.sirenBiomes, biome)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, SIREN_ISLAND_CF);
        	LOADED_FEATURES.put("SIREN_ISLAND_CF", true);
        }
    	if (IafConfig.spawnDeathWorm && BiomeConfig.test(BiomeConfig.deathwormBiomes, biome)) {
    		event.getGeneration().withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, SPAWN_DEATH_WORM_CF);
        	LOADED_FEATURES.put("SPAWN_DEATH_WORM_CF", true);
    	}
        if (IafConfig.generateWanderingCyclops && BiomeConfig.test(BiomeConfig.wanderingCyclopsBiomes, biome)) {
        	event.getGeneration().withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, SPAWN_WANDERING_CYCLOPS_CF);
        	LOADED_FEATURES.put("SPAWN_WANDERING_CYCLOPS_CF", true);
        }
        if (IafConfig.generateDragonSkeletons) {
            if (BiomeConfig.test(BiomeConfig.lightningDragonSkeletonBiomes, biome)) {
        		event.getGeneration().withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, SPAWN_DRAGON_SKELETON_L_CF);
            	LOADED_FEATURES.put("SPAWN_DRAGON_SKELETON_L_CF", true);
            }
            if (BiomeConfig.test(BiomeConfig.fireDragonSkeletonBiomes, biome)) {
        		event.getGeneration().withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, SPAWN_DRAGON_SKELETON_F_CF);
            	LOADED_FEATURES.put("SPAWN_DRAGON_SKELETON_F_CF", true);
            }
            if (BiomeConfig.test(BiomeConfig.iceDragonSkeletonBiomes, biome)) {
            	event.getGeneration().withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, SPAWN_DRAGON_SKELETON_I_CF);
            	LOADED_FEATURES.put("SPAWN_DRAGON_SKELETON_I_CF", true);
            }
        }
    	if (IafConfig.spawnHippocampus && BiomeConfig.test(BiomeConfig.hippocampusBiomes, biome)) {
    		event.getGeneration().withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, SPAWN_HIPPOCAMPUS_CF);
        	LOADED_FEATURES.put("SPAWN_HIPPOCAMPUS_CF", true);
    	}
        if (IafConfig.spawnSeaSerpents && BiomeConfig.test(BiomeConfig.seaSerpentBiomes, biome)) {
        	event.getGeneration().withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, SPAWN_SEA_SERPENT_CF);
        	LOADED_FEATURES.put("SPAWN_SEA_SERPENT_CF", true);
        }
        if (IafConfig.spawnStymphalianBirds && BiomeConfig.test(BiomeConfig.stymphalianBiomes, biome)) {
        	event.getGeneration().withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, SPAWN_STYMPHALIAN_BIRD_CF);
        	LOADED_FEATURES.put("SPAWN_STYMPHALIAN_BIRD_CF", true);
        }
    }
}
