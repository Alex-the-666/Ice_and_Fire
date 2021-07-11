package com.github.alexthe666.iceandfire.world.feature;

import java.util.Random;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.EntityCyclops;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.mojang.serialization.Codec;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class SpawnWanderingCyclops extends Feature<NoFeatureConfig> {

    public SpawnWanderingCyclops(Codec<NoFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public boolean generate(ISeedReader worldIn, ChunkGenerator p_230362_3_, Random rand, BlockPos position, NoFeatureConfig p_230362_6_) {
        if(!IafWorldRegistry.isDimensionListedForMobs(worldIn)){
            return false;
        }
        position = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, position.add(8, 0, 8));

        if (IafConfig.generateWanderingCyclops && IafWorldRegistry.isFarEnoughFromSpawn(worldIn, position)) {
            if (rand.nextInt(IafConfig.spawnWanderingCyclopsChance + 1) == 0 && rand.nextInt(12) == 0) {
                EntityCyclops cyclops = IafEntityRegistry.CYCLOPS.create(worldIn.getWorld());
                cyclops.setPosition(position.getX() + 0.5F, position.getY() + 1, position.getZ() + 0.5F);
                cyclops.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(position), SpawnReason.SPAWNER, null, null);
                cyclops.setVariant(rand.nextInt(3));
                worldIn.addEntity(cyclops);
                for (int i = 0; i < 3 + rand.nextInt(3); i++) {
                    SheepEntity sheep = EntityType.SHEEP.create(worldIn.getWorld());
                    sheep.setPosition(position.getX() + 0.5F, position.getY() + 1, position.getZ() + 0.5F);
                    sheep.setFleeceColor(SheepEntity.getRandomSheepColor(rand));
                    worldIn.addEntity(sheep);
                }
            }
        }

        return false;
    }
}
