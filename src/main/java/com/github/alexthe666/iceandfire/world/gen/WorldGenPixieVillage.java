package com.github.alexthe666.iceandfire.world.gen;

import java.util.Random;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.block.BlockPixieHouse;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.EntityPixie;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class WorldGenPixieVillage extends Feature<NoFeatureConfig> {

    private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    public WorldGenPixieVillage(Codec<NoFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public boolean generate(ISeedReader worldIn, ChunkGenerator p_230362_3_, Random rand, BlockPos position, NoFeatureConfig p_230362_6_) {
        if(!IafWorldRegistry.isDimensionListedForFeatures(worldIn)){
            return false;
        }
        if(!IafConfig.spawnPixies || rand.nextInt(IafConfig.spawnPixiesChance) != 0 || !IafWorldRegistry.isFarEnoughFromSpawn(worldIn, position)){
            return false;
        }
        position = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, position);
        int maxRoads = IafConfig.pixieVillageSize + rand.nextInt(5);
        BlockPos buildPosition = position;
        int placedRoads = 0;
        while(placedRoads < maxRoads){
            int roadLength = 10 + rand.nextInt(15);
            Direction buildingDirection = Direction.byHorizontalIndex(rand.nextInt(3));
            for(int i = 0; i < roadLength; i++){
                BlockPos buildPosition2 = buildPosition.offset(buildingDirection, i);
                buildPosition2 = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, buildPosition2).down();
                if(worldIn.getBlockState(buildPosition2).getFluidState().isEmpty()){
                    worldIn.setBlockState(buildPosition2, Blocks.GRASS_PATH.getDefaultState(), 2);
                }else{
                    worldIn.setBlockState(buildPosition2, Blocks.SPRUCE_PLANKS.getDefaultState(), 2);
                }
                if(rand.nextInt(8) == 0){
                    Direction houseDir = rand.nextBoolean() ? buildingDirection.rotateY() : buildingDirection.rotateYCCW();
                    BlockState houseState = IafBlockRegistry.PIXIE_HOUSE_OAK.getDefaultState();
                    int houseColor = rand.nextInt(5);
                    switch (houseColor){
                        case 0:
                            houseState = IafBlockRegistry.PIXIE_HOUSE_MUSHROOM_RED.getDefaultState().with(BlockPixieHouse.FACING, houseDir.getOpposite());
                            break;
                        case 1:
                            houseState = IafBlockRegistry.PIXIE_HOUSE_MUSHROOM_BROWN.getDefaultState().with(BlockPixieHouse.FACING, houseDir.getOpposite());
                            break;
                        case 2:
                            houseState = IafBlockRegistry.PIXIE_HOUSE_OAK.getDefaultState().with(BlockPixieHouse.FACING, houseDir.getOpposite());
                            break;
                        case 3:
                            houseState = IafBlockRegistry.PIXIE_HOUSE_BIRCH.getDefaultState().with(BlockPixieHouse.FACING, houseDir.getOpposite());
                            break;
                        case 4:
                            houseState = IafBlockRegistry.PIXIE_HOUSE_SPRUCE.getDefaultState().with(BlockPixieHouse.FACING, houseDir.getOpposite());
                            break;
                        case 5:
                            houseState = IafBlockRegistry.PIXIE_HOUSE_DARK_OAK.getDefaultState().with(BlockPixieHouse.FACING, houseDir.getOpposite());
                            break;
                    }
                    EntityPixie pixie = IafEntityRegistry.PIXIE.create(worldIn.getWorld());
                    pixie.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(buildPosition2.up()), SpawnReason.SPAWNER, null, null);
                    pixie.setPosition(buildPosition2.getX(), buildPosition2.getY() + 2, buildPosition2.getZ());
                    pixie.enablePersistence();
                    worldIn.addEntity(pixie);

                    worldIn.setBlockState(buildPosition2.offset(houseDir).up(), houseState, 2);
                    if(!worldIn.getBlockState(buildPosition2.offset(houseDir)).isSolid()){
                        worldIn.setBlockState(buildPosition2.offset(houseDir), Blocks.COARSE_DIRT.getDefaultState(), 2);
                        worldIn.setBlockState(buildPosition2.offset(houseDir).down(), Blocks.COARSE_DIRT.getDefaultState(), 2);
                    }
                }
            }
            buildPosition = buildPosition.offset(buildingDirection, rand.nextInt(roadLength));
            placedRoads++;
        }

        return true;
    }
}
