package com.github.alexthe666.iceandfire.core;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.*;
import com.github.alexthe666.iceandfire.enums.EnumHippogryphTypes;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.List;

public class ModEntities {

	public static void registerSpawnable(Class entityClass, String name, int id, int mainColor, int subColor) {
		EntityRegistry.registerModEntity(new ResourceLocation(IceAndFire.MODID, name), entityClass, name, id, IceAndFire.INSTANCE, 64, 3, true, mainColor, subColor);
	}

	public static void registerUnspawnable(Class entityClass, String name, int id) {
		EntityRegistry.registerModEntity(new ResourceLocation(IceAndFire.MODID, name), entityClass, name, id, IceAndFire.INSTANCE, 64, 3, true);
	}

	public static void init() {
		EntityPropertiesHandler.INSTANCE.registerProperties(StoneEntityProperties.class);
		registerUnspawnable(EntityDragonEgg.class, "dragonegg", 1);
		registerUnspawnable(EntityDragonArrow.class, "dragonarrow", 2);
		registerUnspawnable(EntityDragonSkull.class, "dragonskull", 3);
		registerUnspawnable(EntityDragonFire.class, "dragonfire", 4);
		registerSpawnable(EntityFireDragon.class, "firedragon", 5, 0X340000, 0XA52929);
		registerUnspawnable(EntityDragonIceProjectile.class, "dragonice", 6);
		registerSpawnable(EntityIceDragon.class, "icedragon", 7, 0XB5DDFB, 0X7EBAF0);
		registerUnspawnable(EntityDragonFireCharge.class, "dragonfirecharge", 8);
		registerUnspawnable(EntityDragonIceCharge.class, "dragonicecharge", 9);
		registerSpawnable(EntitySnowVillager.class, "snowvillager", 10, 0X3C2A23, 0X70B1CF);
		registerUnspawnable(EntityHippogryphEgg.class, "hippogryphegg", 11);
		registerSpawnable(EntityHippogryph.class, "hippogryph", 12, 0XD8D8D8, 0XD1B55D);
		registerUnspawnable(EntityStoneStatue.class, "stonestatue", 13);
		registerSpawnable(EntityGorgon.class, "gorgon", 14, 0XD0D99F, 0X684530);
		registerSpawnable(EntityPixie.class, "if_pixie", 15, 0XFF7F89, 0XE2CCE2);
		registerSpawnable(EntityCyclops.class, "cyclops", 17, 0XBBAA92, 0X594729);

		if (IceAndFire.CONFIG.spawnHippogryphs) {
			for (EnumHippogryphTypes type : EnumHippogryphTypes.values()) {
				if (!type.developer) {
					for (Biome biome : Biome.REGISTRY) {
						if (biome != null && BiomeDictionary.hasType(biome, BiomeDictionary.Type.HILLS)) {
							List<Biome.SpawnListEntry> spawnList = biome.getSpawnableList(EnumCreatureType.CREATURE);
							spawnList.add(new Biome.SpawnListEntry(EntityHippogryph.class, 5, 1, 1));
						}
					}
				}
			}
		}
	}
}
