package com.github.alexthe666.iceandfire.world.feature;

import java.util.Random;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.EntityHippocampus;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.mojang.serialization.Codec;

import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class SpawnHippocampus extends Feature<NoFeatureConfig> {

    public SpawnHippocampus(Codec<NoFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public boolean generate(ISeedReader worldIn, ChunkGenerator p_230362_3_, Random rand, BlockPos position, NoFeatureConfig p_230362_6_) {
        if(!IafWorldRegistry.isDimensionListedForMobs(worldIn)){
            return false;
        }
        position = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, position.add(8, 0, 8));
        BlockPos oceanPos = worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, position.add(8, 0, 8));

        if (IafConfig.spawnHippocampus && rand.nextInt(IafConfig.hippocampusSpawnChance + 1) == 0) {
            for (int i = 0; i < rand.nextInt(5); i++) {
                BlockPos pos = oceanPos.add(rand.nextInt(10) - 5, rand.nextInt(30), rand.nextInt(10) - 5);
                if (worldIn.getFluidState(pos).getFluid() == Fluids.WATER) {
                    EntityHippocampus campus = IafEntityRegistry.HIPPOCAMPUS.create(worldIn.getWorld());
                    campus.setVariant(rand.nextInt(6));
                    campus.setLocationAndAngles(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0, 0);
                    worldIn.addEntity(campus);
                }
            }
        }

        return false;
    }
}
