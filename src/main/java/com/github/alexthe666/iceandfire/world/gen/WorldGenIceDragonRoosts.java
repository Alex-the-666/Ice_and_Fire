package com.github.alexthe666.iceandfire.world.gen;

import java.util.Random;
import java.util.stream.Collectors;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.event.WorldGenUtils;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class WorldGenIceDragonRoosts extends Feature<NoFeatureConfig> {
    private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private static boolean isMale;
    private BlockPos lastIceRoost = BlockPos.ZERO;

    public WorldGenIceDragonRoosts(Codec<NoFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
    }

    private void transformState(IWorld world, BlockPos blockpos, BlockState state) {
        float hardness = state.getBlockHardness(world, blockpos);
        if (hardness != -1.0F) {
            if (state.getBlock() instanceof ContainerBlock) {
                return;
            }
            if (state.getMaterial() == Material.ORGANIC) {
                world.setBlockState(blockpos, IafBlockRegistry.FROZEN_GRASS.getDefaultState(), 2);
            } else if (state.getMaterial() == Material.EARTH && state.getBlock() == Blocks.DIRT) {
                world.setBlockState(blockpos, IafBlockRegistry.FROZEN_DIRT.getDefaultState(), 2);
            } else if (state.getMaterial() == Material.EARTH && state.getBlock() == Blocks.GRAVEL) {
                world.setBlockState(blockpos, IafBlockRegistry.FROZEN_GRAVEL.getDefaultState(), 2);
            } else if (state.getMaterial() == Material.ROCK && (state.getBlock() == Blocks.COBBLESTONE || state.getBlock().getTranslationKey().contains("cobblestone"))) {
                world.setBlockState(blockpos, IafBlockRegistry.FROZEN_COBBLESTONE.getDefaultState(), 2);
            } else if (state.getMaterial() == Material.ROCK && state.getBlock() != IafBlockRegistry.FROZEN_COBBLESTONE) {
                world.setBlockState(blockpos, IafBlockRegistry.FROZEN_STONE.getDefaultState(), 2);
            } else if (state.getBlock() == Blocks.GRASS_PATH) {
                world.setBlockState(blockpos, IafBlockRegistry.FROZEN_GRASS_PATH.getDefaultState(), 2);
            } else if (state.getMaterial() == Material.WOOD) {
                world.setBlockState(blockpos, IafBlockRegistry.FROZEN_SPLINTERS.getDefaultState(), 2);
            } else if (state.getMaterial() == Material.LEAVES || state.getMaterial() == Material.PLANTS) {
                world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 2);
            }
        }
    }

    @Override
    public boolean generate(ISeedReader worldIn, ChunkGenerator p_230362_3_, Random rand, BlockPos position, NoFeatureConfig p_230362_6_) {
        if (!IafWorldRegistry.isDimensionListedForDragons(worldIn)) {
            return false;
        }
        if (!IafConfig.generateDragonRoosts || rand.nextInt(IafConfig.generateDragonRoostChance) != 0 || !IafWorldRegistry.isFarEnoughFromSpawn(worldIn, position)||!IafWorldRegistry.isFarEnoughFromDangerousGen(worldIn, position)) {
            return false;
        }
        if (!worldIn.getFluidState(worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, position).down()).isEmpty()) {
            return false;
        }
        position = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, position);
        isMale = new Random().nextBoolean();
        int boulders = 0;
        int radius = 12 + rand.nextInt(8);
        BlockPos finalPosition = position;
        {
            EntityDragonBase dragon = IafEntityRegistry.ICE_DRAGON.create(worldIn.getWorld());
            dragon.setGender(isMale);
            dragon.growDragon(40 + radius);
            dragon.setAgingDisabled(true);
            dragon.setHealth(dragon.getMaxHealth());
            dragon.setVariant(new Random().nextInt(4));
            dragon.setPositionAndRotation(position.getX() + 0.5, worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, position).getY() + 1.5, position.getZ() + 0.5, rand.nextFloat() * 360, 0);
            dragon.homePos = position;
            dragon.hasHomePosition = true;
            dragon.setHunger(50);
            worldIn.addEntity(dragon);
        }

        {
            int j = radius;
            int k = 2;
            int l = radius;
            float f = (float) (j + k + l) * 0.333F + 0.5F;

            BlockPos.getAllInBox(position.add(-j, k, -l), position.add(j, 0, l)).map(BlockPos::toImmutable).forEach(blockPos ->  {
                int yAdd = blockPos.getY() - finalPosition.getY();
                if (blockPos.distanceSq(finalPosition) <= (double) (f * f) && yAdd < 2 + rand.nextInt(k) && !worldIn.isAirBlock(blockPos.down())) {
                    worldIn.setBlockState(blockPos, IafBlockRegistry.FROZEN_GRASS.getDefaultState(), 2);
                }
            });
            BlockPos.getAllInBox(position.add(-j, k, -l), position.add(j, 0, l)).map(BlockPos::toImmutable).forEach(blockPos ->  {
                if (worldIn.getBlockState(blockPos).getBlock() == IafBlockRegistry.FROZEN_GRASS && !worldIn.isAirBlock(blockPos.up())) {
                    worldIn.setBlockState(blockPos, IafBlockRegistry.FROZEN_DIRT.getDefaultState(), 2);
                }
            });
        }
        {
            int j = radius;
            int k = (radius / 5);
            int l = radius;
            float f = (float) (j + k + l) * 0.333F + 0.5F;
            BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, 1, l)).map(BlockPos::toImmutable).forEach(blockPos ->  {
                if (blockPos.distanceSq(finalPosition) < (double) (f * f)) {
                    worldIn.setBlockState(blockPos, rand.nextBoolean() ? IafBlockRegistry.FROZEN_GRAVEL.getDefaultState() : IafBlockRegistry.FROZEN_DIRT.getDefaultState(), 2);
                }
                if (blockPos.distanceSq(finalPosition) == (double) (f * f)) {
                    worldIn.setBlockState(blockPos, rand.nextBoolean() ? IafBlockRegistry.FROZEN_COBBLESTONE.getDefaultState() : IafBlockRegistry.FROZEN_COBBLESTONE.getDefaultState(), 2);
                }
            });
        }
        radius -= 2;
        {
            int j = radius;
            int k = 2;
            int l = radius;
            float f = (float) (j + k + l) * 0.333F + 0.5F;
            BlockPos up = position.up(k - 1);
            BlockPos.getAllInBox(up.add(-j, -k + 2, -l), up.add(j, k, l)).map(BlockPos::toImmutable).forEach(blockPos ->  {
                if (blockPos.distanceSq(finalPosition) <= (double) (f * f)) {
                    worldIn.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
                }
            });
        }
        radius += 15;
        {
            int j = radius;
            int k = (radius / 5);
            int l = radius;
            float f = (float) (j + k + l) * 0.333F + 0.5F;
            BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l)).map(BlockPos::toImmutable).forEach(blockPos ->  {
                if (blockPos.distanceSq(finalPosition) <= (double) (f * f)) {
                    double dist = blockPos.distanceSq(finalPosition) / (double) (f * f);
                    if (!worldIn.isAirBlock(finalPosition) && rand.nextDouble() > dist * 0.5D) {
                        transformState(worldIn, blockPos, worldIn.getBlockState(blockPos));
                    }
                    if (dist > 0.5D && rand.nextInt(1000) == 0) {
                        BlockPos height = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, blockPos);
                        new WorldGenRoostBoulder(IafBlockRegistry.FROZEN_COBBLESTONE, rand.nextInt(3), true).generate(worldIn, rand, height);
                    }
                    if (rand.nextInt(1000) == 0) {
                        BlockPos height = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, blockPos);
                        new WorldGenRoostPile(IafBlockRegistry.DRAGON_ICE).generate(worldIn, rand, height);
                    }
                    if (dist < 0.3D && rand.nextInt(isMale ? 200 : 300) == 0) {
                        BlockPos height = WorldGenUtils.degradeSurface(worldIn, worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, blockPos)).up();
                        new WorldGenRoostGoldPile(IafBlockRegistry.SILVER_PILE).generate(worldIn, rand, height);
                    }
                    if (dist < 0.3D && rand.nextInt(isMale ? 500 : 700) == 0) {
                        BlockPos height = WorldGenUtils.degradeSurface(worldIn, worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, blockPos)).up();
                        worldIn.setBlockState(height, Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, HORIZONTALS[new Random().nextInt(3)]), 2);
                        if (worldIn.getBlockState(height).getBlock() instanceof ChestBlock) {
                            TileEntity tileentity1 = worldIn.getTileEntity(height);
                            if (tileentity1 instanceof ChestTileEntity) {
                                ((ChestTileEntity) tileentity1).setLootTable(WorldGenIceDragonCave.ICEDRAGON_CHEST, new Random().nextLong());
                            }
                        }
                    }
                    if (rand.nextInt(5000) == 0) {
                        BlockPos height = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, blockPos);
                        new WorldGenRoostArch(IafBlockRegistry.FROZEN_COBBLESTONE).generate(worldIn, rand, height);
                    }
                }
            });
        }
        return true;
    }
}
