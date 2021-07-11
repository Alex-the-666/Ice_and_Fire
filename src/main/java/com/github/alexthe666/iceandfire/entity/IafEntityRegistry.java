package com.github.alexthe666.iceandfire.entity;

import java.lang.reflect.Field;
import java.util.HashMap;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.config.BiomeConfig;
import com.github.alexthe666.iceandfire.entity.props.ChainEntityProperties;
import com.github.alexthe666.iceandfire.entity.props.ChickenEntityProperties;
import com.github.alexthe666.iceandfire.entity.props.FrozenEntityProperties;
import com.github.alexthe666.iceandfire.entity.props.MiscEntityProperties;
import com.github.alexthe666.iceandfire.entity.props.SirenEntityProperties;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = IceAndFire.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IafEntityRegistry {

    public static final EntityType<EntityDragonPart> DRAGON_MULTIPART = registerEntity(EntityType.Builder.create(EntityDragonPart::new, EntityClassification.MISC).size(0.5F, 0.5F).immuneToFire().setCustomClientFactory(EntityDragonPart::new), "dragon_multipart");
    public static final EntityType<EntitSlowPart> SLOW_MULTIPART = registerEntity(EntityType.Builder.create(EntitSlowPart::new, EntityClassification.MISC).size(0.5F, 0.5F).immuneToFire().setCustomClientFactory(EntitSlowPart::new), "multipart");
    public static final EntityType<EntityHydraHead> HYDRA_MULTIPART = registerEntity(EntityType.Builder.create(EntityHydraHead::new, EntityClassification.MISC).size(0.5F, 0.5F).immuneToFire().setCustomClientFactory(EntityHydraHead::new), "hydra_multipart");
    public static final EntityType<EntityCyclopsEye> CYCLOPS_MULTIPART = registerEntity(EntityType.Builder.create(EntityCyclopsEye::new, EntityClassification.MISC).size(0.5F, 0.5F).immuneToFire().setCustomClientFactory(EntityCyclopsEye::new), "cylcops_multipart");
    public static final EntityType<EntityDragonEgg> DRAGON_EGG = registerEntity(EntityType.Builder.create(EntityDragonEgg::new, EntityClassification.MISC).size(0.45F, 0.55F).immuneToFire(), "dragon_egg");
    public static final EntityType<EntityDragonArrow> DRAGON_ARROW = registerEntity(EntityType.Builder.create(EntityDragonArrow::new, EntityClassification.MISC).size(0.5F, 0.5F).setCustomClientFactory(EntityDragonArrow::new), "dragon_arrow");
    public static final EntityType<EntityDragonSkull> DRAGON_SKULL = registerEntity(EntityType.Builder.create(EntityDragonSkull::new, EntityClassification.MISC).size(0.9F, 0.65F), "dragon_skull");
    public static final EntityType<EntityDragonBase> FIRE_DRAGON = registerEntity(EntityType.Builder.create(EntityFireDragon::new, EntityClassification.CREATURE).size(0.78F, 1.2F).immuneToFire().setTrackingRange(256), "fire_dragon");
    public static final EntityType<EntityDragonBase> ICE_DRAGON = registerEntity(EntityType.Builder.create(EntityIceDragon::new, EntityClassification.CREATURE).size(0.78F, 1.2F).setTrackingRange(256), "ice_dragon");
    public static final EntityType<EntityDragonBase> LIGHTNING_DRAGON = registerEntity(EntityType.Builder.create(EntityLightningDragon::new, EntityClassification.CREATURE).size(0.78F, 1.2F).setTrackingRange(256), "lightning_dragon");
    public static final EntityType<EntityDragonFireCharge> FIRE_DRAGON_CHARGE = registerEntity(EntityType.Builder.create(EntityDragonFireCharge::new, EntityClassification.MISC).size(0.9F, 0.9F).setCustomClientFactory(EntityDragonFireCharge::new), "fire_dragon_charge");
    public static final EntityType<EntityDragonIceCharge> ICE_DRAGON_CHARGE = registerEntity(EntityType.Builder.create(EntityDragonIceCharge::new, EntityClassification.MISC).size(0.9F, 0.9F).setCustomClientFactory(EntityDragonIceCharge::new), "ice_dragon_charge");
    public static final EntityType<EntityDragonLightningCharge> LIGHTNING_DRAGON_CHARGE = registerEntity(EntityType.Builder.create(EntityDragonLightningCharge::new, EntityClassification.MISC).size(0.9F, 0.9F).setCustomClientFactory(EntityDragonLightningCharge::new), "lightning_dragon_charge");
    public static final EntityType<EntityHippogryphEgg> HIPPOGRYPH_EGG = registerEntity(EntityType.Builder.create(EntityHippogryphEgg::new, EntityClassification.MISC).size(0.5F, 0.5F), "hippogryph_egg");
    public static final EntityType<EntityHippogryph> HIPPOGRYPH = registerEntity(EntityType.Builder.create(EntityHippogryph::new, EntityClassification.CREATURE).size(1.7F, 1.6F).setTrackingRange(128), "hippogryph");
    public static final EntityType<EntityStoneStatue> STONE_STATUE = registerEntity(EntityType.Builder.create(EntityStoneStatue::new, EntityClassification.CREATURE).size(0.5F, 0.5F), "stone_statue");
    public static final EntityType<EntityGorgon> GORGON = registerEntity(EntityType.Builder.create(EntityGorgon::new, EntityClassification.CREATURE).size(0.8F, 1.99F), "gorgon");
    public static final EntityType<EntityPixie> PIXIE = registerEntity(EntityType.Builder.create(EntityPixie::new, EntityClassification.CREATURE).size(0.4F, 0.8F), "pixie");
    public static final EntityType<EntityCyclops> CYCLOPS = registerEntity(EntityType.Builder.create(EntityCyclops::new, EntityClassification.CREATURE).size(1.95F, 7.4F), "cyclops");
    public static final EntityType<EntitySiren> SIREN = registerEntity(EntityType.Builder.create(EntitySiren::new, EntityClassification.CREATURE).size(1.6F, 0.9F), "siren");
    public static final EntityType<EntityHippocampus> HIPPOCAMPUS = registerEntity(EntityType.Builder.create(EntityHippocampus::new, EntityClassification.CREATURE).size(1.95F, 0.95F), "hippocampus");
    public static final EntityType<EntityDeathWorm> DEATH_WORM = registerEntity(EntityType.Builder.create(EntityDeathWorm::new, EntityClassification.CREATURE).size(0.8F, 0.8F).setTrackingRange(128), "deathworm");
    public static final EntityType<EntityDeathWormEgg> DEATH_WORM_EGG = registerEntity(EntityType.Builder.create(EntityDeathWormEgg::new, EntityClassification.MISC).size(0.5F, 0.5F), "deathworm_egg");
    public static final EntityType<EntityCockatrice> COCKATRICE = registerEntity(EntityType.Builder.create(EntityCockatrice::new, EntityClassification.CREATURE).size(0.95F, 0.95F), "cockatrice");
    public static final EntityType<EntityCockatriceEgg> COCKATRICE_EGG = registerEntity(EntityType.Builder.create(EntityCockatriceEgg::new, EntityClassification.MISC).size(0.5F, 0.5F), "cockatrice_egg");
    public static final EntityType<EntityStymphalianBird> STYMPHALIAN_BIRD = registerEntity(EntityType.Builder.create(EntityStymphalianBird::new, EntityClassification.CREATURE).size(1.3F, 1.2F).setTrackingRange(128), "stymphalian_bird");
    public static final EntityType<EntityStymphalianFeather> STYMPHALIAN_FEATHER = registerEntity(EntityType.Builder.create(EntityStymphalianFeather::new, EntityClassification.MISC).size(0.5F, 0.5F).setCustomClientFactory(EntityStymphalianFeather::new), "stymphalian_feather");
    public static final EntityType<EntityStymphalianArrow> STYMPHALIAN_ARROW = registerEntity(EntityType.Builder.create(EntityStymphalianArrow::new, EntityClassification.MISC).size(0.5F, 0.5F).setCustomClientFactory(EntityStymphalianArrow::new), "stymphalian_arrow");
    public static final EntityType<EntityTroll> TROLL = registerEntity(EntityType.Builder.create(EntityTroll::new, EntityClassification.MONSTER).size(1.2F, 3.5F), "troll");
    public static final EntityType<EntityMyrmexWorker> MYRMEX_WORKER = registerEntity(EntityType.Builder.create(EntityMyrmexWorker::new, EntityClassification.CREATURE).size(0.9F, 0.9F), "myrmex_worker");
    public static final EntityType<EntityMyrmexSoldier> MYRMEX_SOLDIER = registerEntity(EntityType.Builder.create(EntityMyrmexSoldier::new, EntityClassification.CREATURE).size(0.9F, 0.95F), "myrmex_soldier");
    public static final EntityType<EntityMyrmexSentinel> MYRMEX_SENTINEL = registerEntity(EntityType.Builder.create(EntityMyrmexSentinel::new, EntityClassification.CREATURE).size(1.3F, 1.95F), "myrmex_sentinel");
    public static final EntityType<EntityMyrmexRoyal> MYRMEX_ROYAL = registerEntity(EntityType.Builder.create(EntityMyrmexRoyal::new, EntityClassification.CREATURE).size(1.9F, 1.86F), "myrmex_royal");
    public static final EntityType<EntityMyrmexQueen> MYRMEX_QUEEN = registerEntity(EntityType.Builder.create(EntityMyrmexQueen::new, EntityClassification.CREATURE).size(2.9F, 1.86F), "myrmex_queen");
    public static final EntityType<EntityMyrmexEgg> MYRMEX_EGG = registerEntity(EntityType.Builder.create(EntityMyrmexEgg::new, EntityClassification.MISC).size(0.45F, 0.55F), "myrmex_egg");
    public static final EntityType<EntityAmphithere> AMPHITHERE = registerEntity(EntityType.Builder.create(EntityAmphithere::new, EntityClassification.CREATURE).size(2.5F, 1.25F).setTrackingRange(128), "amphithere");
    public static final EntityType<EntityAmphithereArrow> AMPHITHERE_ARROW = registerEntity(EntityType.Builder.create(EntityAmphithereArrow::new, EntityClassification.MISC).size(0.5F, 0.5F).setCustomClientFactory(EntityAmphithereArrow::new), "amphithere_arrow");
    public static final EntityType<EntitySeaSerpent> SEA_SERPENT = registerEntity(EntityType.Builder.create(EntitySeaSerpent::new, EntityClassification.CREATURE).size(0.5F, 0.5F).setTrackingRange(256), "sea_serpent");
    public static final EntityType<EntitySeaSerpentBubbles> SEA_SERPENT_BUBBLES = registerEntity(EntityType.Builder.create(EntitySeaSerpentBubbles::new, EntityClassification.MISC).size(0.9F, 0.9F).setCustomClientFactory(EntitySeaSerpentBubbles::new), "sea_serpent_bubbles");
    public static final EntityType<EntitySeaSerpentArrow> SEA_SERPENT_ARROW = registerEntity(EntityType.Builder.create(EntitySeaSerpentArrow::new, EntityClassification.MISC).size(0.5F, 0.5F).setCustomClientFactory(EntitySeaSerpentArrow::new), "sea_serpent_arrow");
    public static final EntityType<EntityChainTie> CHAIN_TIE = registerEntity(EntityType.Builder.create(EntityChainTie::new, EntityClassification.MISC).size(0.8F, 0.9F), "chain_tie");
    public static final EntityType<EntityPixieCharge> PIXIE_CHARGE = registerEntity(EntityType.Builder.create(EntityPixieCharge::new, EntityClassification.MISC).size(0.5F, 0.5F).setCustomClientFactory(EntityPixieCharge::new), "pixie_charge");
    public static final EntityType<EntityMyrmexSwarmer> MYRMEX_SWARMER = registerEntity(EntityType.Builder.create(EntityMyrmexSwarmer::new, EntityClassification.CREATURE).size(0.5F, 0.5F), "myrmex_swarmer");
    public static final EntityType<EntityTideTrident> TIDE_TRIDENT = registerEntity(EntityType.Builder.create(EntityTideTrident::new, EntityClassification.MISC).size(0.85F, 0.5F).setCustomClientFactory(EntityTideTrident::new), "tide_trident");
    public static final EntityType<EntityMobSkull> MOB_SKULL = registerEntity(EntityType.Builder.create(EntityMobSkull::new, EntityClassification.MISC).size(0.85F, 0.85F), "mob_skull");
    public static final EntityType<EntityDreadThrall> DREAD_THRALL = registerEntity(EntityType.Builder.create(EntityDreadThrall::new, EntityClassification.CREATURE).size(0.6F, 1.8F), "dread_thrall");
    public static final EntityType<EntityDreadGhoul> DREAD_GHOUL = registerEntity(EntityType.Builder.create(EntityDreadGhoul::new, EntityClassification.CREATURE).size(0.6F, 1.8F), "dread_ghoul");
    public static final EntityType<EntityDreadBeast> DREAD_BEAST = registerEntity(EntityType.Builder.create(EntityDreadBeast::new, EntityClassification.CREATURE).size(1.2F, 0.9F), "dread_beast");
    public static final EntityType<EntityDreadScuttler> DREAD_SCUTTLER = registerEntity(EntityType.Builder.create(EntityDreadScuttler::new, EntityClassification.CREATURE).size(1.5F, 1.3F), "dread_scuttler");
    public static final EntityType<EntityDreadLich> DREAD_LICH = registerEntity(EntityType.Builder.create(EntityDreadLich::new, EntityClassification.CREATURE).size(0.6F, 1.8F), "dread_lich");
    public static final EntityType<EntityDreadLichSkull> DREAD_LICH_SKULL = registerEntity(EntityType.Builder.create(EntityDreadLichSkull::new, EntityClassification.MISC).size(0.5F, 0.5F).setCustomClientFactory(EntityDreadLichSkull::new), "dread_lich_skull");
    public static final EntityType<EntityDreadKnight> DREAD_KNIGHT = registerEntity(EntityType.Builder.create(EntityDreadKnight::new, EntityClassification.CREATURE).size(0.6F, 1.8F), "dread_knight");
    public static final EntityType<EntityDreadHorse> DREAD_HORSE = registerEntity(EntityType.Builder.create(EntityDreadHorse::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F), "dread_horse");
    public static final EntityType<EntityHydra> HYDRA = registerEntity(EntityType.Builder.create(EntityHydra::new, EntityClassification.CREATURE).size(2.8F, 1.39F), "hydra");
    public static final EntityType<EntityHydraBreath> HYDRA_BREATH = registerEntity(EntityType.Builder.create(EntityHydraBreath::new, EntityClassification.MISC).size(0.9F, 0.9F).setCustomClientFactory(EntityHydraBreath::new), "hydra_breath");
    public static final EntityType<EntityHydraArrow> HYDRA_ARROW = registerEntity(EntityType.Builder.create(EntityHydraArrow::new, EntityClassification.MISC).size(0.5F, 0.5F).setCustomClientFactory(EntityHydraArrow::new), "hydra_arrow");
    public static final EntityType<EntityGhost> GHOST = registerEntity(EntityType.Builder.create(EntityGhost::new, EntityClassification.CREATURE).size(0.8F, 1.9F).immuneToFire(), "ghost");
    public static final EntityType<EntityGhostSword> GHOST_SWORD = registerEntity(EntityType.Builder.create(EntityGhostSword::new, EntityClassification.MISC).size(0.5F, 0.5F).setCustomClientFactory(EntityGhostSword::new), "ghost_sword");


    private static final EntityType registerEntity(EntityType.Builder builder, String entityName) {
        ResourceLocation nameLoc = new ResourceLocation(IceAndFire.MODID, entityName);
        return (EntityType) builder.build(entityName).setRegistryName(nameLoc);
    }

    public static void setup() {
        EntityPropertiesHandler.INSTANCE.registerProperties(MiscEntityProperties.class);
        EntityPropertiesHandler.INSTANCE.registerProperties(FrozenEntityProperties.class);
        EntityPropertiesHandler.INSTANCE.registerProperties(SirenEntityProperties.class);
        EntityPropertiesHandler.INSTANCE.registerProperties(ChickenEntityProperties.class);
        EntityPropertiesHandler.INSTANCE.registerProperties(ChainEntityProperties.class);
    }

    @SubscribeEvent
    public static void bakeAttributes(EntityAttributeCreationEvent creationEvent){
        creationEvent.put(DRAGON_EGG, EntityDragonEgg.bakeAttributes().create());
        creationEvent.put(DRAGON_SKULL, EntityDragonSkull.bakeAttributes().create());
        creationEvent.put(FIRE_DRAGON, EntityFireDragon.bakeAttributes().create());
        creationEvent.put(ICE_DRAGON, EntityIceDragon.bakeAttributes().create());
        creationEvent.put(LIGHTNING_DRAGON, EntityLightningDragon.bakeAttributes().create());
        creationEvent.put(HIPPOGRYPH, EntityHippogryph.bakeAttributes().create());
        creationEvent.put(GORGON, EntityGorgon.bakeAttributes().create());
        creationEvent.put(STONE_STATUE, EntityStoneStatue.bakeAttributes().create());
        creationEvent.put(PIXIE, EntityPixie.bakeAttributes().create());
        creationEvent.put(CYCLOPS, EntityCyclops.bakeAttributes().create());
        creationEvent.put(SIREN, EntitySiren.bakeAttributes().create());
        creationEvent.put(HIPPOCAMPUS, EntityHippocampus.bakeAttributes().create());
        creationEvent.put(DEATH_WORM, EntityDeathWorm.bakeAttributes().create());
        creationEvent.put(COCKATRICE, EntityCockatrice.bakeAttributes().create());
        creationEvent.put(STYMPHALIAN_BIRD, EntityStymphalianBird.bakeAttributes().create());
        creationEvent.put(TROLL, EntityTroll.bakeAttributes().create());
        creationEvent.put(MYRMEX_WORKER, EntityMyrmexWorker.bakeAttributes().create());
        creationEvent.put(MYRMEX_SOLDIER, EntityMyrmexSoldier.bakeAttributes().create());
        creationEvent.put(MYRMEX_SENTINEL, EntityMyrmexSentinel.bakeAttributes().create());
        creationEvent.put(MYRMEX_ROYAL, EntityMyrmexRoyal.bakeAttributes().create());
        creationEvent.put(MYRMEX_QUEEN, EntityMyrmexQueen.bakeAttributes().create());
        creationEvent.put(MYRMEX_EGG, EntityMyrmexEgg.bakeAttributes().create());
        creationEvent.put(MYRMEX_SWARMER, EntityMyrmexSwarmer.bakeAttributes().create());
        creationEvent.put(AMPHITHERE, EntityAmphithere.bakeAttributes().create());
        creationEvent.put(SEA_SERPENT, EntitySeaSerpent.bakeAttributes().create());
        creationEvent.put(MOB_SKULL, EntityMobSkull.bakeAttributes().create());
        creationEvent.put(DREAD_THRALL, EntityDreadThrall.bakeAttributes().create());
        creationEvent.put(DREAD_LICH, EntityDreadLich.bakeAttributes().create());
        creationEvent.put(DREAD_BEAST, EntityDreadBeast.bakeAttributes().create());
        creationEvent.put(DREAD_HORSE, EntityDreadHorse.bakeAttributes().create());
        creationEvent.put(DREAD_GHOUL, EntityDreadGhoul.bakeAttributes().create());
        creationEvent.put(DREAD_KNIGHT, EntityDreadKnight.bakeAttributes().create());
        creationEvent.put(DREAD_SCUTTLER, EntityDreadScuttler.bakeAttributes().create());
        creationEvent.put(HYDRA, EntityHydra.bakeAttributes().create());
        creationEvent.put(GHOST, EntityGhost.bakeAttributes().create());
    }


    static {
        EntitySpawnPlacementRegistry.register(HIPPOGRYPH, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityHippogryph::canSpawnOn);
        EntitySpawnPlacementRegistry.register(TROLL, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityTroll::canTrollSpawnOn);
        EntitySpawnPlacementRegistry.register(DREAD_LICH, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityDreadLich::canLichSpawnOn);
        EntitySpawnPlacementRegistry.register(COCKATRICE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EntityCockatrice::canSpawnOn);
        EntitySpawnPlacementRegistry.register(AMPHITHERE, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING, EntityAmphithere::canAmphithereSpawnOn);
    }
        @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        try {
            for (Field f : IafEntityRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof EntityType) {
                    event.getRegistry().register((EntityType) obj);
                } else if (obj instanceof EntityType[]) {
                    for (EntityType type : (EntityType[]) obj) {
                        event.getRegistry().register(type);

                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        //bakeAttributes();
    }

    public static HashMap<String, Boolean> LOADED_ENTITIES;
    static {
    	LOADED_ENTITIES = new HashMap<String, Boolean>();
    	LOADED_ENTITIES.put("HIPPOGRYPH", false);
    	LOADED_ENTITIES.put("DREAD_LICH", false);
    	LOADED_ENTITIES.put("COCKATRICE", false);
    	LOADED_ENTITIES.put("AMPHITHERE", false);
    	LOADED_ENTITIES.put("TROLL_F", false);
    	LOADED_ENTITIES.put("TROLL_S", false);
    	LOADED_ENTITIES.put("TROLL_M", false);
    }
    public static void onBiomesLoad(BiomeLoadingEvent event) {
    	Biome biome = ForgeRegistries.BIOMES.getValue(event.getName());

    	if (IafConfig.spawnHippogryphs && BiomeConfig.test(BiomeConfig.hippogryphBiomes, biome)) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(IafEntityRegistry.HIPPOGRYPH, IafConfig.hippogryphSpawnRate, 1, 1));
            LOADED_ENTITIES.put("HIPPOGRYPH", true);
        }
        if (IafConfig.spawnLiches && BiomeConfig.test(BiomeConfig.mausoleumBiomes, biome)) {
            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(IafEntityRegistry.DREAD_LICH, IafConfig.lichSpawnRate, 1, 1));
            LOADED_ENTITIES.put("DREAD_LICH", true);
        }
        if (IafConfig.spawnCockatrices && BiomeConfig.test(BiomeConfig.cockatriceBiomes, biome)) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(IafEntityRegistry.COCKATRICE, IafConfig.cockatriceSpawnRate, 1, 2));
            LOADED_ENTITIES.put("COCKATRICE", true);
        }
        if (IafConfig.spawnAmphitheres && BiomeConfig.test(BiomeConfig.amphithereBiomes, biome)) {
            event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(IafEntityRegistry.AMPHITHERE, IafConfig.amphithereSpawnRate, 1, 3));
            LOADED_ENTITIES.put("AMPHITHERE", true);
        }
        if (IafConfig.spawnTrolls && (
    		BiomeConfig.test(BiomeConfig.forestTrollBiomes, biome) ||
    		BiomeConfig.test(BiomeConfig.snowyTrollBiomes, biome) ||
    		BiomeConfig.test(BiomeConfig.mountainTrollBiomes, biome)
		)) {
            event.getSpawns().getSpawner(EntityClassification.MONSTER).add(new MobSpawnInfo.Spawners(IafEntityRegistry.TROLL, IafConfig.trollSpawnRate, 1, 3));
    		if (BiomeConfig.test(BiomeConfig.forestTrollBiomes, biome)) LOADED_ENTITIES.put("TROLL_F", true);
    		if (BiomeConfig.test(BiomeConfig.snowyTrollBiomes, biome)) LOADED_ENTITIES.put("TROLL_S", true); 
    		if (BiomeConfig.test(BiomeConfig.mountainTrollBiomes, biome)) LOADED_ENTITIES.put("TROLL_M", true);
        }
    }
}
