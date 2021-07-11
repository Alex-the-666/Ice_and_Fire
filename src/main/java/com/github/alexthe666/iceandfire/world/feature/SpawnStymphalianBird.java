package com.github.alexthe666.iceandfire.world.feature;

import java.util.Random;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.EntityStymphalianBird;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class SpawnStymphalianBird extends Feature<NoFeatureConfig> {

    public SpawnStymphalianBird(Codec<NoFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public boolean generate(ISeedReader worldIn, ChunkGenerator p_230362_3_, Random rand, BlockPos position, NoFeatureConfig p_230362_6_) {
        if(!IafWorldRegistry.isDimensionListedForMobs(worldIn)){
            return false;
        }
        position = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, position.add(8, 0, 8));

        if (IafConfig.spawnStymphalianBirds && IafWorldRegistry.isFarEnoughFromSpawn(worldIn, position) && rand.nextInt(IafConfig.stymphalianBirdSpawnChance + 1) == 0) {
            for (int i = 0; i < 4 + rand.nextInt(4); i++) {
                BlockPos pos = position.add(rand.nextInt(10) - 5, 0, rand.nextInt(10) - 5);
                pos = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, pos);
                if (worldIn.getBlockState(pos.down()).isSolid()) {
                    EntityStymphalianBird bird = IafEntityRegistry.STYMPHALIAN_BIRD.create(worldIn.getWorld());
                    bird.setLocationAndAngles(pos.getX() + 0.5F, pos.getY() + 1.5F, pos.getZ() + 0.5F, 0, 0);
                    worldIn.addEntity(bird);
                    
                }
            }
        }

        return false;
    }
}
