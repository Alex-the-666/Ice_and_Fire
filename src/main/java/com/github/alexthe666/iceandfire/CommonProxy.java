package com.github.alexthe666.iceandfire;

import java.lang.reflect.Field;

import com.github.alexthe666.iceandfire.config.BiomeConfig;
import com.github.alexthe666.iceandfire.config.ConfigHolder;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.entity.IafVillagerRegistry;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.event.ServerEvents;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;

import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = IceAndFire.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy {


    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent event) {
        final ModConfig config = event.getConfig();
        // Rebake the configs when they change
        if (config.getSpec() == ConfigHolder.CLIENT_SPEC) {
            IafConfig.bakeClient(config);
        } else if (config.getSpec() == ConfigHolder.SERVER_SPEC) {
            IafConfig.bakeServer(config);
        }
        BiomeConfig.init();

    }

    @SubscribeEvent
    public static void registerFeatures(final RegistryEvent.Register<Feature<?>> event) {
        for (Feature<?> feature : IafWorldRegistry.featureList) {
            event.getRegistry().register(feature);
        }
    }

    @SubscribeEvent
    public static void registerStructures(final RegistryEvent.Register<Structure<?>> event) {
        for (Structure<?> feature : IafWorldRegistry.structureFeatureList) {
            event.getRegistry().register(feature);
        }
    }

    @SubscribeEvent
    public static void registerSoundEvents(final RegistryEvent.Register<SoundEvent> event) {
        try {
            for (Field f : IafSoundRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof SoundEvent) {
                    event.getRegistry().register((SoundEvent) obj);
                } else if (obj instanceof SoundEvent[]) {
                    for (SoundEvent soundEvent : (SoundEvent[]) obj) {
                        event.getRegistry().register(soundEvent);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setReferencedHive(MyrmexHive hive) {

    }

    public void preInit() {

    }

    public void init() {

    }

    public void postInit() {
    }

    public void spawnParticle(String name, double x, double y, double z, double motX, double motY, double motZ) {
        spawnParticle(name, x, y, z, motX, motY, motZ, 1.0F);
    }

    public void spawnDragonParticle(String name, double x, double y, double z, double motX, double motY, double motZ, EntityDragonBase entityDragonBase) {
    }

    public void spawnParticle(String name, double x, double y, double z, double motX, double motY, double motZ, float size) {
    }

    public void openBestiaryGui(ItemStack book) {
    }

    public void openMyrmexStaffGui(ItemStack staff) {
    }

    public Object getArmorModel(int armorId) {
        return null;
    }

    public Object getFontRenderer() {
        return null;
    }

    public int getDragon3rdPersonView() {
        return 0;
    }

    public void setDragon3rdPersonView(int view) {
    }

    public void openMyrmexAddRoomGui(ItemStack staff, BlockPos pos, Direction facing) {
    }


    public Object getDreadlandsRender(int i) {
        return null;
    }

    public int getPreviousViewType() {
        return 0;
    }

    public void setPreviousViewType(int view) {
    }

    public void updateDragonArmorRender(String clear) {
    }

    public boolean shouldSeeBestiaryContents() {
        return true;
    }

    public Entity getReferencedMob() {
        return null;
    }

    public void setReferencedMob(Entity dragonBase) {
    }

    public TileEntity getRefrencedTE() {
        return null;
    }

    public void setRefrencedTE(TileEntity tileEntity) {
    }

    public Item.Properties setupISTER(Item.Properties group) {
        return group;
    }

    public void setupClient() {
    }

    public PlayerEntity getClientSidePlayer(){
        return null;
    }

    public void setup() {
        MinecraftForge.EVENT_BUS.register(new ServerEvents());
        IafEntityRegistry.setup();
    }
}
