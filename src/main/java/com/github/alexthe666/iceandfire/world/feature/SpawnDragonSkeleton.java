package com.github.alexthe666.iceandfire.world.feature;

import java.util.Random;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.mojang.serialization.Codec;

import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class SpawnDragonSkeleton extends Feature<NoFeatureConfig> {
	protected EntityType<EntityDragonBase> dragonType;

	public SpawnDragonSkeleton(EntityType<EntityDragonBase> dt, Codec<NoFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
        dragonType = dt;
    }
	
    @Override
    public boolean generate(ISeedReader worldIn, ChunkGenerator p_230362_3_, Random rand, BlockPos position, NoFeatureConfig p_230362_6_) {
        if(!IafWorldRegistry.isDimensionListedForMobs(worldIn)){
            return false;
        }
        position = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, position.add(8, 0, 8));

        if (IafConfig.generateDragonSkeletons) {
            if (rand.nextInt(IafConfig.generateDragonSkeletonChance + 1) == 0) {
		        EntityDragonBase dragon = dragonType.create(worldIn.getWorld());
		        dragon.setPosition(position.getX() + 0.5F, position.getY() + 1, position.getZ() + 0.5F);
		        int dragonage = 10 + rand.nextInt(100);
		        dragon.growDragon(dragonage);
		        dragon.modelDeadProgress = 20;
		        dragon.setModelDead(true);
		        dragon.setDeathStage((dragonage / 5) / 2);
		        dragon.rotationYaw = rand.nextInt(360);
		        worldIn.addEntity(dragon);
            }
        }

        return false;
    }
}
