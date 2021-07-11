package com.github.alexthe666.iceandfire.world.gen;

import java.util.Random;
import java.util.stream.Collectors;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.EntityHydra;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.mojang.serialization.Codec;

import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class WorldGenHydraCave extends Feature<NoFeatureConfig> {

    public static final ResourceLocation HYDRA_CHEST = new ResourceLocation("iceandfire", "chest/hydra_cave");
    protected static final ConfiguredFeature SWAMP_FEATURE = Features.SWAMP_TREE;
    private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    public WorldGenHydraCave(Codec<NoFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public boolean generate(ISeedReader worldIn, ChunkGenerator p_230362_3_, Random rand, BlockPos position, NoFeatureConfig p_230362_6_) {
        if (!IafWorldRegistry.isDimensionListedForFeatures(worldIn)) {
            return false;
        }
        if (!IafConfig.generateHydraCaves || rand.nextInt(IafConfig.generateHydraChance) != 0 || !IafWorldRegistry.isFarEnoughFromSpawn(worldIn, position) || !IafWorldRegistry.isFarEnoughFromDangerousGen(worldIn, position)) {
            return false;
        }
        position = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, position);
        int i1 = 8;
        int i2 = i1 - 2;
        int dist = 6;
        if (worldIn.isAirBlock(position.add(i1 - dist, -3, -i1 + dist)) || worldIn.isAirBlock(position.add(i1 - dist, -3, i1 - dist)) || worldIn.isAirBlock(position.add(-i1 + dist, -3, -i1 + dist)) || worldIn.isAirBlock(position.add(-i1 + dist, -3, i1 - dist))) {
            return false;
        }

        {
            int ySize = rand.nextInt(2);
            int j = i1 + rand.nextInt(2);
            int k = 5 + ySize;
            int l = i1 + rand.nextInt(2);
            float f = (float) (j + k + l) * 0.333F + 0.5F;


            for (BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet())) {
                boolean doorwayX = blockpos.getX() >= position.getX() - 2 + rand.nextInt(2) && blockpos.getX() <= position.getX() + 2 + rand.nextInt(2);
                boolean doorwayZ = blockpos.getZ() >= position.getZ() - 2 + rand.nextInt(2) && blockpos.getZ() <= position.getZ() + 2 + rand.nextInt(2);
                boolean isNotInDoorway = !doorwayX && !doorwayZ && blockpos.getY() > position.getY() || blockpos.getY() > position.getY() + k - (1 + rand.nextInt(2));
                if (blockpos.distanceSq(position) <= (double) (f * f)) {
                    if (!(worldIn.getBlockState(position).getBlock() instanceof ChestBlock) && isNotInDoorway) {
                        worldIn.setBlockState(blockpos, Blocks.GRASS_BLOCK.getDefaultState(), 3);
                        if (worldIn.getBlockState(position.down()).getBlock() == Blocks.GRASS_BLOCK) {
                            worldIn.setBlockState(blockpos.down(), Blocks.DIRT.getDefaultState(), 3);
                        }
                        if (rand.nextInt(4) == 0) {
                            worldIn.setBlockState(blockpos.up(), Blocks.GRASS.getDefaultState(), 2);
                        }
                        if (rand.nextInt(9) == 0) {
                            SWAMP_FEATURE.generate(worldIn, p_230362_3_, rand, blockpos.up());
                        }

                    }
                    if (blockpos.getY() == position.getY()) {
                        worldIn.setBlockState(blockpos, Blocks.GRASS_BLOCK.getDefaultState(), 3);
                    }
                    if (blockpos.getY() <= position.getY() - 1 && !worldIn.getBlockState(blockpos).isSolid()) {
                        worldIn.setBlockState(blockpos, Blocks.STONE.getDefaultState(), 3);

                    }
                }
            }


        }
        {
            int ySize = rand.nextInt(2);
            int j = i2 + rand.nextInt(2);
            int k = 4 + ySize;
            int l = i2 + rand.nextInt(2);
            float f = (float) (j + k + l) * 0.333F + 0.5F;
            for (BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet())) {
                if (blockpos.distanceSq(position) <= (double) (f * f) && blockpos.getY() > position.getY()) {
                    if (!(worldIn.getBlockState(position).getBlock() instanceof ChestBlock)) {
                        worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 3);

                    }
                }
            }
            for (BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k + 8, l)).map(BlockPos::toImmutable).collect(Collectors.toSet())) {
                if (blockpos.distanceSq(position) <= (double) (f * f) && blockpos.getY() == position.getY()) {
                    if (rand.nextInt(30) == 0 && isTouchingAir(worldIn, blockpos.up())) {
                        worldIn.setBlockState(blockpos.up(1), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, HORIZONTALS[new Random().nextInt(3)]), 2);
                        if (worldIn.getBlockState(blockpos.up(1)).getBlock() instanceof ChestBlock) {
                            TileEntity tileentity1 = worldIn.getTileEntity(blockpos.up(1));
                            if (tileentity1 instanceof ChestTileEntity) {
                                ((ChestTileEntity) tileentity1).setLootTable(HYDRA_CHEST, rand.nextLong());
                            }
                        }
                        continue;
                    }
                    if (rand.nextInt(45) == 0 && isTouchingAir(worldIn, blockpos.up())) {
                        worldIn.setBlockState(blockpos.up(), Blocks.SKELETON_SKULL.getDefaultState().with(SkullBlock.ROTATION, rand.nextInt(15)), 2);
                        TileEntity tileentity1 = worldIn.getTileEntity(blockpos.up(1));
                        continue;
                    }
                    if (rand.nextInt(35) == 0 && isTouchingAir(worldIn, blockpos.up())) {
                        worldIn.setBlockState(blockpos.up(), Blocks.OAK_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true), 2);
                        for (Direction facing : Direction.values()) {
                            if (rand.nextFloat() < 0.3F && facing != Direction.DOWN) {
                                worldIn.setBlockState(blockpos.up().offset(facing), Blocks.OAK_LEAVES.getDefaultState(), 2);
                            }
                        }
                        continue;
                    }
                    if (rand.nextInt(15) == 0 && isTouchingAir(worldIn, blockpos.up())) {
                        worldIn.setBlockState(blockpos.up(), Blocks.TALL_GRASS.getDefaultState(), 2);
                        continue;
                    }
                    if (rand.nextInt(15) == 0 && isTouchingAir(worldIn, blockpos.up())) {
                        worldIn.setBlockState(blockpos.up(), rand.nextBoolean() ? Blocks.BROWN_MUSHROOM.getDefaultState() : Blocks.RED_MUSHROOM.getDefaultState(), 2);
                        continue;
                    }
                }
            }
        }
        EntityHydra hydra = new EntityHydra(IafEntityRegistry.HYDRA, worldIn.getWorld());
        hydra.setVariant(rand.nextInt(3));
        hydra.setHomePosAndDistance(position, 15);
        hydra.setPositionAndRotation(position.getX() + 0.5, position.getY() + 1.5, position.getZ() + 0.5, rand.nextFloat() * 360, 0);
        worldIn.addEntity(hydra);
        return true;
    }

    private boolean isTouchingAir(IWorld worldIn, BlockPos pos) {
        boolean isTouchingAir = true;
        for (Direction direction : HORIZONTALS) {
            if (!worldIn.isAirBlock(pos.offset(direction))) {
                isTouchingAir = false;
            }
        }
        return isTouchingAir;
    }
}
