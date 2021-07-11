package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.world.gen.processor.VillageHouseProcessor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.block.Blocks;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.LegacySingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.feature.template.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = IceAndFire.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IafVillagerRegistry {

    private static final String[] VILLAGE_TYPES = new String[]{"plains", "desert", "snowy", "savanna", "taiga"};
    private static final StructureProcessorList HOUSE_PROCESSOR = WorldGenRegistries.register(WorldGenRegistries.STRUCTURE_PROCESSOR_LIST, new ResourceLocation("iceandfire:village_house_processor"), genVillageHouseProcessor());
    public static PointOfInterestType LECTERN_POI;
    public static VillagerProfession SCRIBE;

    private static StructureProcessorList genVillageHouseProcessor() {
        RuleStructureProcessor mossify = new RuleStructureProcessor(ImmutableList.of(new RuleEntry(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.1F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState())));
        return new StructureProcessorList(ImmutableList.of(mossify, new VillageHouseProcessor()));
    }

    public static void setup() {
        PlainsVillagePools.init();
        SnowyVillagePools.init();
        SavannaVillagePools.init();
        DesertVillagePools.init();
        TaigaVillagePools.init();

        for (String type : VILLAGE_TYPES) {
            addStructureToPool(new ResourceLocation("village/" + type + "/houses"), new ResourceLocation("village/" + type + "/terminators"), new ResourceLocation("iceandfire", "village/" + type + "_scriber_1"), IafConfig.villagerHouseWeight);
        }

    }

    private static JigsawPiece createWorkstation(String name) {
        return new LegacySingleJigsawPiece(Either.left(new ResourceLocation("iceandfire", name)), () -> ProcessorLists.EMPTY, JigsawPattern.PlacementBehaviour.RIGID);
    }

    @SubscribeEvent
    public static void registerPointOfInterests(final RegistryEvent.Register<PointOfInterestType> event) {
        event.getRegistry().register(LECTERN_POI = new PointOfInterestType("scribe", ImmutableSet.copyOf(IafBlockRegistry.LECTERN.getStateContainer().getValidStates()), 1, 1).setRegistryName("iceandfire:scribe"));
        PointOfInterestType.registerBlockStates(LECTERN_POI);
    }

    @SubscribeEvent
    public static void registerVillagerProfessions(final RegistryEvent.Register<VillagerProfession> event) {
        event.getRegistry().register(SCRIBE = new VillagerProfession("scribe", LECTERN_POI, ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN).setRegistryName("iceandfire:scribe"));
    }

    public static void addScribeTrades(Int2ObjectMap<List<VillagerTrades.ITrade>> trades) {
        final float emeraldForItemsMultiplier = 0.05F; //Values taken from VillagerTrades.java
        final float itemForEmeraldMultiplier = 0.05F;
        final float rareItemForEmeraldMultiplier = 0.2F;
        trades.get(1).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 1), new ItemStack(IafItemRegistry.MANUSCRIPT, 4), 25, 2, emeraldForItemsMultiplier));
        trades.get(1).add((entity, random) -> new MerchantOffer(new ItemStack(Items.BOOKSHELF, 3), new ItemStack(Items.EMERALD, 1), 8, 3, itemForEmeraldMultiplier));
        trades.get(1).add((entity, random) -> new MerchantOffer(new ItemStack(Items.PAPER, 15), new ItemStack(Items.EMERALD, 2), 4, 4, itemForEmeraldMultiplier));
        trades.get(1).add((entity, random) -> new MerchantOffer(new ItemStack(IafBlockRegistry.ASH, 10), new ItemStack(Items.EMERALD, 1), 8, 4, itemForEmeraldMultiplier));
        trades.get(2).add((entity, random) -> new MerchantOffer(new ItemStack(IafItemRegistry.SILVER_INGOT, 5), new ItemStack(Items.EMERALD, 1), 3, 5, itemForEmeraldMultiplier));
        trades.get(2).add((entity, random) -> new MerchantOffer(new ItemStack(IafBlockRegistry.FIRE_LILY, 8), new ItemStack(Items.EMERALD, 1), 3, 5, itemForEmeraldMultiplier));
        trades.get(2).add((entity, random) -> new MerchantOffer(new ItemStack(IafBlockRegistry.LIGHTNING_LILY, 7), new ItemStack(Items.EMERALD, 3), 2, 5, itemForEmeraldMultiplier));
        trades.get(2).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 3), new ItemStack(IafBlockRegistry.FROST_LILY, 4), 3, 3, emeraldForItemsMultiplier));
        trades.get(2).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 2), new ItemStack(IafBlockRegistry.DRAGON_ICE_SPIKES, 7), 2, 3, emeraldForItemsMultiplier));
        trades.get(2).add((entity, random) -> new MerchantOffer(new ItemStack(IafItemRegistry.SAPPHIRE_GEM), new ItemStack(Items.EMERALD, 2), 30, 3, rareItemForEmeraldMultiplier));
        trades.get(2).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 2), new ItemStack(IafBlockRegistry.JAR_EMPTY, 1), 3, 4, emeraldForItemsMultiplier));
        trades.get(2).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 2), new ItemStack(IafItemRegistry.MYRMEX_DESERT_RESIN, 1), 40, 2, emeraldForItemsMultiplier));
        trades.get(2).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 2), new ItemStack(IafItemRegistry.MYRMEX_JUNGLE_RESIN, 1), 40, 2, emeraldForItemsMultiplier));
        trades.get(2).add((entity, random) -> new MerchantOffer(new ItemStack(IafItemRegistry.AMYTHEST_GEM), new ItemStack(Items.EMERALD, 3), 20, 3, rareItemForEmeraldMultiplier));
        trades.get(3).add((entity, random) -> new MerchantOffer(new ItemStack(IafItemRegistry.DRAGON_BONE, 6), new ItemStack(Items.EMERALD, 1), 7, 4, itemForEmeraldMultiplier));
        trades.get(3).add((entity, random) -> new MerchantOffer(new ItemStack(IafItemRegistry.CHAIN, 2), new ItemStack(Items.EMERALD, 3), 4, 2, itemForEmeraldMultiplier));
        trades.get(3).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 6), new ItemStack(IafItemRegistry.PIXIE_DUST, 2), 8, 3, emeraldForItemsMultiplier));
        trades.get(3).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 6), new ItemStack(IafItemRegistry.FIRE_DRAGON_FLESH, 2), 8, 3, emeraldForItemsMultiplier));
        trades.get(3).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 7), new ItemStack(IafItemRegistry.ICE_DRAGON_FLESH, 1), 8, 3, emeraldForItemsMultiplier));
        trades.get(3).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 8), new ItemStack(IafItemRegistry.LIGHTNING_DRAGON_FLESH, 1), 8, 3, emeraldForItemsMultiplier));
        trades.get(4).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 10), new ItemStack(IafItemRegistry.DRAGON_BONE, 2), 20, 5, emeraldForItemsMultiplier));
        trades.get(4).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 4), new ItemStack(IafItemRegistry.SHINY_SCALES, 1), 5, 2, emeraldForItemsMultiplier));
        trades.get(4).add((entity, random) -> new MerchantOffer(new ItemStack(IafItemRegistry.DREAD_SHARD, 5), new ItemStack(Items.EMERALD, 1), 10, 4, itemForEmeraldMultiplier));
        trades.get(4).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 8), new ItemStack(IafItemRegistry.STYMPHALIAN_BIRD_FEATHER, 12), 3, 6, emeraldForItemsMultiplier));
        trades.get(4).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 4), new ItemStack(IafItemRegistry.TROLL_TUSK, 12), 7, 3, emeraldForItemsMultiplier));
        trades.get(5).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 15), new ItemStack(IafItemRegistry.SERPENT_FANG, 3), 20, 3, emeraldForItemsMultiplier));
        trades.get(5).add((entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 12), new ItemStack(IafItemRegistry.HYDRA_FANG, 1), 20, 3, emeraldForItemsMultiplier));
        trades.get(5).add((entity, random) -> new MerchantOffer(new ItemStack(IafItemRegistry.ECTOPLASM, 6), new ItemStack(Items.EMERALD, 1), 7, 3, itemForEmeraldMultiplier));
    }

    private static void addStructureToPool(ResourceLocation pool, ResourceLocation terminatorPool, ResourceLocation toAdd, int weight) {
        JigsawPattern old = WorldGenRegistries.JIGSAW_POOL.getOrDefault(pool);
        List<JigsawPiece> shuffled = old != null ? old.getShuffledPieces(new Random()) : ImmutableList.of();
        List<Pair<JigsawPiece, Integer>> newPieces = shuffled.stream().map(p -> new Pair<>(p, 1)).collect(Collectors.toList());
        newPieces.add(new Pair<>(new LegacySingleJigsawPiece(Either.left(toAdd), () -> HOUSE_PROCESSOR, JigsawPattern.PlacementBehaviour.RIGID), weight));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(pool, terminatorPool, newPieces));
    }

}
