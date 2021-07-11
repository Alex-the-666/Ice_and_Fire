package com.github.alexthe666.iceandfire;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.client.model.TabulaModelHandler;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.client.IafKeybindRegistry;
import com.github.alexthe666.iceandfire.client.gui.GuiMyrmexAddRoom;
import com.github.alexthe666.iceandfire.client.gui.GuiMyrmexStaff;
import com.github.alexthe666.iceandfire.client.gui.IafGuiRegistry;
import com.github.alexthe666.iceandfire.client.gui.bestiary.GuiBestiary;
import com.github.alexthe666.iceandfire.client.model.ModelCopperArmor;
import com.github.alexthe666.iceandfire.client.model.ModelDeathWormArmor;
import com.github.alexthe666.iceandfire.client.model.ModelDragonsteelFireArmor;
import com.github.alexthe666.iceandfire.client.model.ModelDragonsteelIceArmor;
import com.github.alexthe666.iceandfire.client.model.ModelDragonsteelLightningArmor;
import com.github.alexthe666.iceandfire.client.model.ModelFireDragonArmor;
import com.github.alexthe666.iceandfire.client.model.ModelIceDragonArmor;
import com.github.alexthe666.iceandfire.client.model.ModelLightningDragonArmor;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexQueen;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexRoyal;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexSentinel;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexSoldier;
import com.github.alexthe666.iceandfire.client.model.ModelMyrmexWorker;
import com.github.alexthe666.iceandfire.client.model.ModelSeaSerpentArmor;
import com.github.alexthe666.iceandfire.client.model.ModelSilverArmor;
import com.github.alexthe666.iceandfire.client.model.ModelTrollArmor;
import com.github.alexthe666.iceandfire.client.model.animator.FireDragonTabulaModelAnimator;
import com.github.alexthe666.iceandfire.client.model.animator.IceDragonTabulaModelAnimator;
import com.github.alexthe666.iceandfire.client.model.animator.LightningTabulaDragonAnimator;
import com.github.alexthe666.iceandfire.client.model.animator.SeaSerpentTabulaModelAnimator;
import com.github.alexthe666.iceandfire.client.model.util.EnumDragonAnimations;
import com.github.alexthe666.iceandfire.client.model.util.EnumSeaSerpentAnimations;
import com.github.alexthe666.iceandfire.client.particle.ParticleBlood;
import com.github.alexthe666.iceandfire.client.particle.ParticleDragonFlame;
import com.github.alexthe666.iceandfire.client.particle.ParticleDragonFrost;
import com.github.alexthe666.iceandfire.client.particle.ParticleDreadPortal;
import com.github.alexthe666.iceandfire.client.particle.ParticleDreadTorch;
import com.github.alexthe666.iceandfire.client.particle.ParticleGhostAppearance;
import com.github.alexthe666.iceandfire.client.particle.ParticleHydraBreath;
import com.github.alexthe666.iceandfire.client.particle.ParticlePixieDust;
import com.github.alexthe666.iceandfire.client.particle.ParticleSerpentBubble;
import com.github.alexthe666.iceandfire.client.particle.ParticleSirenAppearance;
import com.github.alexthe666.iceandfire.client.particle.ParticleSirenMusic;
import com.github.alexthe666.iceandfire.client.render.entity.RenderAmphithere;
import com.github.alexthe666.iceandfire.client.render.entity.RenderAmphithereArrow;
import com.github.alexthe666.iceandfire.client.render.entity.RenderChainTie;
import com.github.alexthe666.iceandfire.client.render.entity.RenderCockatrice;
import com.github.alexthe666.iceandfire.client.render.entity.RenderCyclops;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDeathWorm;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDragonArrow;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDragonBase;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDragonEgg;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDragonFireCharge;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDragonLightningCharge;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDragonSkull;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDreadBeast;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDreadGhoul;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDreadHorse;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDreadKnight;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDreadLich;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDreadLichSkull;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDreadScuttler;
import com.github.alexthe666.iceandfire.client.render.entity.RenderDreadThrall;
import com.github.alexthe666.iceandfire.client.render.entity.RenderGhost;
import com.github.alexthe666.iceandfire.client.render.entity.RenderGhostSword;
import com.github.alexthe666.iceandfire.client.render.entity.RenderGorgon;
import com.github.alexthe666.iceandfire.client.render.entity.RenderHippocampus;
import com.github.alexthe666.iceandfire.client.render.entity.RenderHippogryph;
import com.github.alexthe666.iceandfire.client.render.entity.RenderHydra;
import com.github.alexthe666.iceandfire.client.render.entity.RenderHydraArrow;
import com.github.alexthe666.iceandfire.client.render.entity.RenderLightningDragon;
import com.github.alexthe666.iceandfire.client.render.entity.RenderMobSkull;
import com.github.alexthe666.iceandfire.client.render.entity.RenderMyrmexBase;
import com.github.alexthe666.iceandfire.client.render.entity.RenderMyrmexEgg;
import com.github.alexthe666.iceandfire.client.render.entity.RenderNothing;
import com.github.alexthe666.iceandfire.client.render.entity.RenderPixie;
import com.github.alexthe666.iceandfire.client.render.entity.RenderSeaSerpent;
import com.github.alexthe666.iceandfire.client.render.entity.RenderSeaSerpentArrow;
import com.github.alexthe666.iceandfire.client.render.entity.RenderSiren;
import com.github.alexthe666.iceandfire.client.render.entity.RenderStoneStatue;
import com.github.alexthe666.iceandfire.client.render.entity.RenderStymphalianArrow;
import com.github.alexthe666.iceandfire.client.render.entity.RenderStymphalianBird;
import com.github.alexthe666.iceandfire.client.render.entity.RenderStymphalianFeather;
import com.github.alexthe666.iceandfire.client.render.entity.RenderTideTrident;
import com.github.alexthe666.iceandfire.client.render.entity.RenderTroll;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerDragonArmor;
import com.github.alexthe666.iceandfire.client.render.tile.IceAndFireTEISR;
import com.github.alexthe666.iceandfire.client.render.tile.RenderDreadPortal;
import com.github.alexthe666.iceandfire.client.render.tile.RenderDreadSpawner;
import com.github.alexthe666.iceandfire.client.render.tile.RenderEggInIce;
import com.github.alexthe666.iceandfire.client.render.tile.RenderGhostChest;
import com.github.alexthe666.iceandfire.client.render.tile.RenderJar;
import com.github.alexthe666.iceandfire.client.render.tile.RenderLectern;
import com.github.alexthe666.iceandfire.client.render.tile.RenderPixieHouse;
import com.github.alexthe666.iceandfire.client.render.tile.RenderPodium;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.entity.tile.IafTileEntityRegistry;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.event.ClientEvents;
import com.github.alexthe666.iceandfire.event.PlayerRenderEvents;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.item.ItemDragonBow;
import com.github.alexthe666.iceandfire.item.ItemDragonHorn;
import com.github.alexthe666.iceandfire.item.ItemSummoningCrystal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = IceAndFire.MODID, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    private static final ModelFireDragonArmor FIRE_DRAGON_SCALE_ARMOR_MODEL = new ModelFireDragonArmor(0.5F, false);
    private static final ModelFireDragonArmor FIRE_DRAGON_SCALE_ARMOR_MODEL_LEGS = new ModelFireDragonArmor(0.2F, true);
    private static final ModelIceDragonArmor ICE_DRAGON_SCALE_ARMOR_MODEL = new ModelIceDragonArmor(0.5F, false);
    private static final ModelIceDragonArmor ICE_DRAGON_SCALE_ARMOR_MODEL_LEGS = new ModelIceDragonArmor(0.2F, true);
    private static final ModelLightningDragonArmor LIGHTNING_DRAGON_SCALE_ARMOR_MODEL = new ModelLightningDragonArmor(0.5F, false);
    private static final ModelLightningDragonArmor LIGHTNING_DRAGON_SCALE_ARMOR_MODEL_LEGS = new ModelLightningDragonArmor(0.2F, true);
    private static final ModelDeathWormArmor DEATHWORM_ARMOR_MODEL = new ModelDeathWormArmor(0.5F);
    private static final ModelDeathWormArmor DEATHWORM_ARMOR_MODEL_LEGS = new ModelDeathWormArmor(0.2F);
    private static final ModelTrollArmor TROLL_ARMOR_MODEL = new ModelTrollArmor(0.75F);
    private static final ModelTrollArmor TROLL_ARMOR_MODEL_LEGS = new ModelTrollArmor(0.35F);
    private static final ModelSeaSerpentArmor TIDE_ARMOR_MODEL = new ModelSeaSerpentArmor(0.4F);
    private static final ModelSeaSerpentArmor TIDE_ARMOR_MODEL_LEGS = new ModelSeaSerpentArmor(0.2F);
    private static final ModelDragonsteelFireArmor DRAGONSTEEL_FIRE_ARMOR_MODEL = new ModelDragonsteelFireArmor(0.4F);
    private static final ModelDragonsteelFireArmor DRAGONSTEEL_FIRE_ARMOR_MODEL_LEGS = new ModelDragonsteelFireArmor(0.2F);
    private static final ModelDragonsteelIceArmor DRAGONSTEEL_ICE_ARMOR_MODEL = new ModelDragonsteelIceArmor(0.4F);
    private static final ModelDragonsteelIceArmor DRAGONSTEEL_ICE_ARMOR_MODEL_LEGS = new ModelDragonsteelIceArmor(0.2F);
    private static final ModelDragonsteelLightningArmor DRAGONSTEEL_LIGHTNING_ARMOR_MODEL = new ModelDragonsteelLightningArmor(0.4F);
    private static final ModelDragonsteelLightningArmor DRAGONSTEEL_LIGHTNING_ARMOR_MODEL_LEGS = new ModelDragonsteelLightningArmor(0.2F);
    private static final ModelSilverArmor SILVER_ARMOR_MODEL = new ModelSilverArmor(0.5F);
    private static final ModelSilverArmor SILVER_ARMOR_MODEL_LEGS = new ModelSilverArmor(0.2F);
    private static final ModelCopperArmor COPPER_ARMOR_MODEL = new ModelCopperArmor(0.5F);
    private static final ModelCopperArmor COPPER_ARMOR_MODEL_LEGS = new ModelCopperArmor(0.2F);

    public static List<UUID> currentDragonRiders = new ArrayList<UUID>();
    public static TabulaModel FIRE_DRAGON_BASE_MODEL;
    public static TabulaModel ICE_DRAGON_BASE_MODEL;
    public static TabulaModel SEA_SERPENT_BASE_MODEL;
    public static TabulaModel LIGHTNING_DRAGON_BASE_MODEL;
    private static MyrmexHive referedClientHive = null;
    private FontRenderer bestiaryFontRenderer;
    private int previousViewType = 0;
    private int thirdPersonViewDragon = 0;
    private Entity referencedMob = null;
    private TileEntity referencedTE = null;

    public static MyrmexHive getReferedClientHive() {
        return referedClientHive;
    }

    @OnlyIn(Dist.CLIENT)
    private static Callable<ItemStackTileEntityRenderer> getTEISR() {
        return IceAndFireTEISR::new;
    }

    public void setReferencedHive(MyrmexHive hive) {
        referedClientHive = hive;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    @SuppressWarnings("deprecation")
    public void init() {
        IafGuiRegistry.register();
        this.bestiaryFontRenderer = Minecraft.getInstance().fontRenderer;
        IafKeybindRegistry.init();
        MinecraftForge.EVENT_BUS.register(new PlayerRenderEvents());
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        renderEntities();


    }

    @OnlyIn(Dist.CLIENT)
    public void postInit() {

    }

    @SuppressWarnings("deprecation")
    @OnlyIn(Dist.CLIENT)
    private void renderEntities() {


    }

    public void setupClient() {
        EnumDragonAnimations.initializeDragonModels();
        EnumSeaSerpentAnimations.initializeSerpentModels();
        try {
            SEA_SERPENT_BASE_MODEL = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/iceandfire/models/tabula/seaserpent/seaserpent"), new SeaSerpentTabulaModelAnimator());
            FIRE_DRAGON_BASE_MODEL = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/iceandfire/models/tabula/firedragon/dragonFireGround"), new FireDragonTabulaModelAnimator());
            ICE_DRAGON_BASE_MODEL = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/iceandfire/models/tabula/icedragon/dragonIceGround"), new IceDragonTabulaModelAnimator());
            LIGHTNING_DRAGON_BASE_MODEL = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/iceandfire/models/tabula/lightningdragon/dragonLightningGround"), new LightningTabulaDragonAnimator());
        } catch (IOException e) {
            e.printStackTrace();
        }
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.FIRE_DRAGON, manager -> new RenderDragonBase(manager, FIRE_DRAGON_BASE_MODEL, 0));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.ICE_DRAGON, manager -> new RenderDragonBase(manager, ICE_DRAGON_BASE_MODEL, 1));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.LIGHTNING_DRAGON, manager -> new RenderLightningDragon(manager, LIGHTNING_DRAGON_BASE_MODEL, 2));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.DRAGON_EGG, manager -> new RenderDragonEgg(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.DRAGON_ARROW, manager -> new RenderDragonArrow(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.DRAGON_SKULL, manager -> new RenderDragonSkull(manager, FIRE_DRAGON_BASE_MODEL, ICE_DRAGON_BASE_MODEL, LIGHTNING_DRAGON_BASE_MODEL));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.FIRE_DRAGON_CHARGE, manager -> new RenderDragonFireCharge(manager, true));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.ICE_DRAGON_CHARGE, manager -> new RenderDragonFireCharge(manager, false));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.LIGHTNING_DRAGON_CHARGE, manager -> new RenderDragonLightningCharge());
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.HIPPOGRYPH_EGG, manager -> new SpriteRenderer(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.HIPPOGRYPH, manager -> new RenderHippogryph(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.STONE_STATUE, manager -> new RenderStoneStatue(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.GORGON, manager -> new RenderGorgon(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.PIXIE, manager -> new RenderPixie(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.CYCLOPS, manager -> new RenderCyclops(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.SIREN, manager -> new RenderSiren(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.HIPPOCAMPUS, manager -> new RenderHippocampus(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.DEATH_WORM, manager -> new RenderDeathWorm(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.DEATH_WORM_EGG, manager -> new SpriteRenderer(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.COCKATRICE, manager -> new RenderCockatrice(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.COCKATRICE_EGG, manager -> new SpriteRenderer(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.STYMPHALIAN_BIRD, manager -> new RenderStymphalianBird(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.STYMPHALIAN_FEATHER, manager -> new RenderStymphalianFeather(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.STYMPHALIAN_ARROW, manager -> new RenderStymphalianArrow(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.TROLL, manager -> new RenderTroll(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.MYRMEX_WORKER, manager -> new RenderMyrmexBase(manager, new ModelMyrmexWorker(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.MYRMEX_SOLDIER, manager -> new RenderMyrmexBase(manager, new ModelMyrmexSoldier(), 0.75F));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.MYRMEX_QUEEN, manager -> new RenderMyrmexBase(manager, new ModelMyrmexQueen(), 1.25F));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.MYRMEX_EGG, manager -> new RenderMyrmexEgg(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.MYRMEX_SENTINEL, manager -> new RenderMyrmexBase(manager, new ModelMyrmexSentinel(), 0.85F));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.MYRMEX_ROYAL, manager -> new RenderMyrmexBase(manager, new ModelMyrmexRoyal(), 0.75F));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.MYRMEX_SWARMER, manager -> new RenderMyrmexBase(manager, new ModelMyrmexRoyal(), 0.25F));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.AMPHITHERE, manager -> new RenderAmphithere(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.AMPHITHERE_ARROW, manager -> new RenderAmphithereArrow(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.SEA_SERPENT, manager -> new RenderSeaSerpent(manager, SEA_SERPENT_BASE_MODEL));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.SEA_SERPENT_BUBBLES, manager -> new RenderNothing(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.SEA_SERPENT_ARROW, manager -> new RenderSeaSerpentArrow(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.CHAIN_TIE, manager -> new RenderChainTie(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.PIXIE_CHARGE, manager -> new RenderNothing(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.TIDE_TRIDENT, manager -> new RenderTideTrident(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.MOB_SKULL, manager -> new RenderMobSkull(manager, SEA_SERPENT_BASE_MODEL));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.DREAD_SCUTTLER, manager -> new RenderDreadScuttler(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.DREAD_GHOUL, manager -> new RenderDreadGhoul(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.DREAD_BEAST, manager -> new RenderDreadBeast(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.DREAD_SCUTTLER, manager -> new RenderDreadScuttler(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.DREAD_THRALL, manager -> new RenderDreadThrall(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.DREAD_LICH, manager -> new RenderDreadLich(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.DREAD_LICH_SKULL, manager -> new RenderDreadLichSkull());
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.DREAD_KNIGHT, manager -> new RenderDreadKnight(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.DREAD_HORSE, manager -> new RenderDreadHorse(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.HYDRA, manager -> new RenderHydra(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.HYDRA_BREATH, manager -> new RenderNothing(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.HYDRA_ARROW, manager -> new RenderHydraArrow(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.SLOW_MULTIPART, manager -> new RenderNothing(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.DRAGON_MULTIPART, manager -> new RenderNothing(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.CYCLOPS_MULTIPART, manager -> new RenderNothing(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.HYDRA_MULTIPART, manager -> new RenderNothing(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.GHOST, manager -> new RenderGhost(manager));
        RenderingRegistry.registerEntityRenderingHandler(IafEntityRegistry.GHOST_SWORD, manager -> new RenderGhostSword(manager));
        ClientRegistry.bindTileEntityRenderer(IafTileEntityRegistry.PODIUM, manager -> new RenderPodium(manager));
        ClientRegistry.bindTileEntityRenderer(IafTileEntityRegistry.IAF_LECTERN, manager -> new RenderLectern(manager));
        ClientRegistry.bindTileEntityRenderer(IafTileEntityRegistry.EGG_IN_ICE, manager -> new RenderEggInIce(manager));
        ClientRegistry.bindTileEntityRenderer(IafTileEntityRegistry.PIXIE_HOUSE, manager -> new RenderPixieHouse(manager));
        ClientRegistry.bindTileEntityRenderer(IafTileEntityRegistry.PIXIE_JAR, manager -> new RenderJar(manager));
        ClientRegistry.bindTileEntityRenderer(IafTileEntityRegistry.DREAD_PORTAL, manager -> new RenderDreadPortal(manager));
        ClientRegistry.bindTileEntityRenderer(IafTileEntityRegistry.DREAD_SPAWNER, manager -> new RenderDreadSpawner(manager));
        ClientRegistry.bindTileEntityRenderer(IafTileEntityRegistry.GHOST_CHEST, manager -> new RenderGhostChest(manager));
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.GOLD_PILE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.SILVER_PILE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.LECTERN, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.PODIUM_OAK, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.PODIUM_BIRCH, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.PODIUM_SPRUCE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.PODIUM_JUNGLE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.PODIUM_ACACIA, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.PODIUM_DARK_OAK, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.FIRE_LILY, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.FROST_LILY, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.LIGHTNING_LILY, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.DRAGON_ICE_SPIKES, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.MYRMEX_DESERT_RESIN_BLOCK, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.MYRMEX_DESERT_RESIN_GLASS, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.MYRMEX_JUNGLE_RESIN_BLOCK, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.MYRMEX_JUNGLE_RESIN_GLASS, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.MYRMEX_DESERT_BIOLIGHT, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.MYRMEX_JUNGLE_BIOLIGHT, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.DREAD_STONE_FACE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.DREAD_TORCH, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.BURNT_TORCH, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.EGG_IN_ICE, RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.JAR_EMPTY, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.JAR_PIXIE_0, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.JAR_PIXIE_1, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.JAR_PIXIE_2, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.JAR_PIXIE_3, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.JAR_PIXIE_4, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.PIXIE_HOUSE_MUSHROOM_BROWN, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.PIXIE_HOUSE_MUSHROOM_RED, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.PIXIE_HOUSE_OAK, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.PIXIE_HOUSE_BIRCH, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.PIXIE_HOUSE_SPRUCE, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.PIXIE_HOUSE_DARK_OAK, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.DREAD_SPAWNER, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.DREAD_TORCH_WALL, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(IafBlockRegistry.BURNT_TORCH_WALL, RenderType.getCutout());
        IItemPropertyGetter pulling = ItemModelsProperties.func_239417_a_(Items.BOW, new ResourceLocation("pulling"));
        IItemPropertyGetter pull = (stack, worldIn, entity) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                ItemDragonBow item = ((ItemDragonBow) stack.getItem());
                return entity.getActiveItemStack() != stack ? 0.0F : (stack.getUseDuration() - entity.getItemInUseCount()) / 20.0F;
            }
        };
        ItemModelsProperties.registerProperty(IafItemRegistry.DRAGON_BOW.asItem(), new ResourceLocation("pulling"), pulling);
        ItemModelsProperties.registerProperty(IafItemRegistry.DRAGON_BOW.asItem(), new ResourceLocation("pull"), pull);
        ItemModelsProperties.registerProperty(IafItemRegistry.DRAGON_HORN, new ResourceLocation("iceorfire"), (p_239428_0_, p_239428_1_, p_239428_2_) -> {
            return ItemDragonHorn.getDragonType(p_239428_0_) * 0.25F;
        });
        ItemModelsProperties.registerProperty(IafItemRegistry.SUMMONING_CRYSTAL_FIRE, new ResourceLocation("has_dragon"), (stack, p_239428_1_, p_239428_2_) -> {
            return ItemSummoningCrystal.hasDragon(stack) ? 1.0F : 0.0F;
        });
        ItemModelsProperties.registerProperty(IafItemRegistry.SUMMONING_CRYSTAL_ICE, new ResourceLocation("has_dragon"), (stack, p_239428_1_, p_239428_2_) -> {
            return ItemSummoningCrystal.hasDragon(stack) ? 1.0F : 0.0F;
        });
        ItemModelsProperties.registerProperty(IafItemRegistry.SUMMONING_CRYSTAL_LIGHTNING, new ResourceLocation("has_dragon"), (stack, p_239428_1_, p_239428_2_) -> {
            return ItemSummoningCrystal.hasDragon(stack) ? 1.0F : 0.0F;
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void spawnDragonParticle(String name, double x, double y, double z, double motX, double motY, double motZ, EntityDragonBase entityDragonBase) {
        ClientWorld world = Minecraft.getInstance().world;
        if (world == null) {
            return;
        }
        net.minecraft.client.particle.Particle particle = null;
        if (name.equals("dragonfire")) {
            particle = new ParticleDragonFlame(world, x, y, z, motX, motY, motZ, entityDragonBase, 0);
        }
        if (name.equals("dragonice")) {
            particle = new ParticleDragonFrost(world, x, y, z, motX, motY, motZ, entityDragonBase, 0);
        }
        if (particle != null) {
            Minecraft.getInstance().particles.addEffect(particle);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void spawnParticle(String name, double x, double y, double z, double motX, double motY, double motZ, float size) {
        ClientWorld world = Minecraft.getInstance().world;
        if (world == null) {
            return;
        }
        net.minecraft.client.particle.Particle particle = null;
        if (name.equals("dragonfire")) {
            particle = new ParticleDragonFlame(world, x, y, z, motX, motY, motZ, size);
        }
        if (name.equals("dragonice")) {
            particle = new ParticleDragonFrost(world, x, y, z, motX, motY, motZ, size);
        }
        if (name.equals("dread_torch")) {
            particle = new ParticleDreadTorch(world, x, y, z, motX, motY, motZ, size);
        }
        if (name.equals("dread_portal")) {
            particle = new ParticleDreadPortal(world, x, y, z, motX, motY, motZ, size);
        }
        if (name.equals("blood")) {
            particle = new ParticleBlood(world, x, y, z);
        }
        if (name.equals("if_pixie")) {
            particle = new ParticlePixieDust(world, x, y, z, (float) motX, (float) motY, (float) motZ);
        }
        if (name.equals("siren_appearance")) {
            particle = new ParticleSirenAppearance(world, x, y, z, (int) motX);
        }
        if (name.equals("ghost_appearance")) {
            particle = new ParticleGhostAppearance(world, x, y, z, (int) motX);
        }
        if (name.equals("siren_music")) {
            particle = new ParticleSirenMusic(world, x, y, z, motX, motY, motZ, 1);
        }
        if (name.equals("serpent_bubble")) {
            particle = new ParticleSerpentBubble(world, x, y, z, motX, motY, motZ, 1);
        }
        if (name.equals("hydra")) {
            particle = new ParticleHydraBreath(world, x, y, z, (float) motX, (float) motY, (float) motZ);
        }
        if (particle != null) {
            Minecraft.getInstance().particles.addEffect(particle);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void openBestiaryGui(ItemStack book) {
        Minecraft.getInstance().displayGuiScreen(new GuiBestiary(book));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void openMyrmexStaffGui(ItemStack staff) {
        Minecraft.getInstance().displayGuiScreen(new GuiMyrmexStaff(staff));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void openMyrmexAddRoomGui(ItemStack staff, BlockPos pos, Direction facing) {
        Minecraft.getInstance().displayGuiScreen(new GuiMyrmexAddRoom(staff, pos, facing));
    }

    @OnlyIn(Dist.CLIENT)
    public Object getArmorModel(int armorId) {
        switch (armorId) {
            case 0:
                return FIRE_DRAGON_SCALE_ARMOR_MODEL;
            case 1:
                return FIRE_DRAGON_SCALE_ARMOR_MODEL_LEGS;
            case 2:
                return ICE_DRAGON_SCALE_ARMOR_MODEL;
            case 3:
                return ICE_DRAGON_SCALE_ARMOR_MODEL_LEGS;
            case 4:
                return DEATHWORM_ARMOR_MODEL;
            case 5:
                return DEATHWORM_ARMOR_MODEL_LEGS;
            case 6:
                return TROLL_ARMOR_MODEL;
            case 7:
                return TROLL_ARMOR_MODEL_LEGS;
            case 8:
                return TIDE_ARMOR_MODEL;
            case 9:
                return TIDE_ARMOR_MODEL_LEGS;
            case 10:
                return DRAGONSTEEL_FIRE_ARMOR_MODEL;
            case 11:
                return DRAGONSTEEL_FIRE_ARMOR_MODEL_LEGS;
            case 12:
                return DRAGONSTEEL_ICE_ARMOR_MODEL;
            case 13:
                return DRAGONSTEEL_ICE_ARMOR_MODEL_LEGS;
            case 14:
                return SILVER_ARMOR_MODEL;
            case 15:
                return SILVER_ARMOR_MODEL_LEGS;
            case 16:
                return COPPER_ARMOR_MODEL;
            case 17:
                return COPPER_ARMOR_MODEL_LEGS;
            case 18:
                return LIGHTNING_DRAGON_SCALE_ARMOR_MODEL;
            case 19:
                return LIGHTNING_DRAGON_SCALE_ARMOR_MODEL_LEGS;
            case 20:
                return DRAGONSTEEL_LIGHTNING_ARMOR_MODEL;
            case 21:
                return DRAGONSTEEL_LIGHTNING_ARMOR_MODEL_LEGS;
        }
        return null;
    }

    public Object getFontRenderer() {
        return Minecraft.getInstance().fontRenderer;
    }

    public int getDragon3rdPersonView() {
        return thirdPersonViewDragon;
    }

    public void setDragon3rdPersonView(int view) {
        thirdPersonViewDragon = view;
    }

    public Object getDreadlandsRender(int i) {
        return null;
    }

    public int getPreviousViewType() {
        return previousViewType;
    }

    public void setPreviousViewType(int view) {
        previousViewType = view;
    }

    public void updateDragonArmorRender(String clear) {
        LayerDragonArmor.clearCache(clear);
    }

    public boolean shouldSeeBestiaryContents() {
        return InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), 344);
    }

    public Entity getReferencedMob() {
        return referencedMob;
    }

    public void setReferencedMob(Entity dragonBase) {
        referencedMob = dragonBase;
    }

    public TileEntity getRefrencedTE() {
        return referencedTE;
    }

    public void setRefrencedTE(TileEntity tileEntity) {
        referencedTE = tileEntity;
    }

    public Item.Properties setupISTER(Item.Properties group) {
        return group.setISTER(ClientProxy::getTEISR);
    }

    public PlayerEntity getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }
}
