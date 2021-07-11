package com.github.alexthe666.iceandfire.world.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.block.BlockGoldPile;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.EntityFireDragon;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.mojang.serialization.Codec;

import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class WorldGenFireDragonCave extends Feature<NoFeatureConfig> {
    public static final ResourceLocation FIREDRAGON_CHEST = new ResourceLocation("iceandfire", "chest/fire_dragon_female_cave");
    public static final ResourceLocation FIREDRAGON_MALE_CHEST = new ResourceLocation("iceandfire", "chest/fire_dragon_male_cave");
    private static final WorldGenCaveStalactites CEILING_DECO = new WorldGenCaveStalactites(IafBlockRegistry.CHARRED_STONE, 3);
    private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private static boolean isMale;

    public WorldGenFireDragonCave(Codec<NoFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
    }

    public static void setGoldPile(IWorld world, BlockPos pos, Random rand) {
        int chance = rand.nextInt(99) + 1;
        if (!(world.getBlockState(pos).getBlock() instanceof ContainerBlock)) {
            if (chance < 60) {
                int goldRand = Math.max(1, IafConfig.dragonDenGoldAmount) * (isMale ? 1 : 2);
                boolean generateGold = rand.nextInt(goldRand) == 0;
                world.setBlockState(pos, generateGold ? IafBlockRegistry.GOLD_PILE.getDefaultState().with(BlockGoldPile.LAYERS, 1 + rand.nextInt(7)) : Blocks.AIR.getDefaultState(), 3);
            } else if (chance == 61) {
                world.setBlockState(pos, Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, HORIZONTALS[rand.nextInt(3)]), 2);
                if (world.getBlockState(pos).getBlock() instanceof ChestBlock) {
                    TileEntity tileentity1 = world.getTileEntity(pos);
                    if (tileentity1 instanceof ChestTileEntity) {
                        ((ChestTileEntity) tileentity1).setLootTable(isMale ? FIREDRAGON_MALE_CHEST : FIREDRAGON_CHEST, rand.nextLong());
                    }
                }
            }
        }
    }

    @Override
    public boolean generate(ISeedReader worldIn, ChunkGenerator p_230362_3_, Random rand, BlockPos position, NoFeatureConfig p_230362_6_) {
        if(!IafWorldRegistry.isDimensionListedForDragons(worldIn)){
            return false;
        }
        if(!IafConfig.generateDragonDens || rand.nextInt(IafConfig.generateDragonDenChance) != 0 || !IafWorldRegistry.isFarEnoughFromSpawn(worldIn, position) || !IafWorldRegistry.isFarEnoughFromDangerousGen(worldIn, position)){
            return false;
        }
        List<SphereInfo> sphereList = new ArrayList<SphereInfo>();
        position = new BlockPos(position.getX(), 20 + rand.nextInt(20), position.getZ());
        isMale = new Random().nextBoolean();
        int dragonAge = 75 + rand.nextInt(50);
        int radius = (int) (dragonAge * 0.2F) + rand.nextInt(8);
        createShell(worldIn, rand, position, radius, sphereList);
        for (int i = 0; i < 3 + rand.nextInt(2); i++) {
            Direction direction = HORIZONTALS[rand.nextInt(HORIZONTALS.length - 1)];
            createShell(worldIn, rand, position.offset(direction, radius - 2), 2 * (int) (radius / 3F) + rand.nextInt(8), sphereList);
        }
        for (SphereInfo info : sphereList) {
            hollowOut(worldIn, rand, info.pos, info.radius - 2);
            decorateCave(worldIn, rand, info.pos, info.radius + 2);
        }
        sphereList.clear();
        EntityFireDragon dragon = new EntityFireDragon(IafEntityRegistry.FIRE_DRAGON, worldIn.getWorld());
        dragon.setGender(isMale);
        dragon.growDragon(dragonAge);
        dragon.setAgingDisabled(true);
        dragon.setHealth(dragon.getMaxHealth());
        dragon.setVariant(rand.nextInt(4));
        dragon.setPositionAndRotation(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, rand.nextFloat() * 360, 0);
        dragon.setQueuedToSit(true);
        dragon.homePos = position;
        dragon.setHunger(50);
        worldIn.addEntity(dragon);

        return false;
    }

    private void decorateCave(IWorld worldIn, Random rand, BlockPos pos, int radius) {
        for (int i = 0; i < 15 + rand.nextInt(10); i++) {
            CEILING_DECO.generate(worldIn, rand, offsetRandomlyByXZ(pos.up(radius / 2 - 1), rand, rand.nextInt(radius) - radius / 2, rand.nextInt(radius) - radius / 2));
        }
        int j = radius;
        int k = radius / 2;
        int l = radius;
        float f = (float) (j + k + l) * 0.333F + 0.5F;
        BlockPos.getAllInBox(pos.add(-j, -k, -l), pos.add(j, k / 2, l)).map(BlockPos::toImmutable).forEach(blockPos -> {
            if (blockPos.distanceSq(pos) <= (double) (f * f) && worldIn.getBlockState(blockPos.down()).getMaterial() == Material.ROCK && worldIn.getBlockState(blockPos).getMaterial() != Material.ROCK) {
                setGoldPile(worldIn, blockPos, rand);
            }
        });

    }

    private BlockPos offsetRandomlyBy(BlockPos in, Random rand, int offset1, int offset2) {
        return in.offset(Direction.values()[rand.nextInt(Direction.values().length - 1)], offset1).offset(Direction.values()[rand.nextInt(Direction.values().length - 1)], offset2);
    }

    private BlockPos offsetRandomlyByXZ(BlockPos in, Random rand, int offset1, int offset2) {
        return in.add(offset1, 0, offset2);
    }

    private void createShell(IWorld worldIn, Random rand, BlockPos position, int radius, List<SphereInfo> sphereList) {
        int j = radius;
        int k = radius / 2;
        int l = radius;
        float f = (float) (j + k + l) * 0.333F + 0.5F;
        BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l)).map(BlockPos::toImmutable).forEach(blockPos ->  {
            if (blockPos.distanceSq(position) <= (double) (f * f)) {
                if (!(worldIn.getBlockState(position).getBlock() instanceof ContainerBlock) && worldIn.getBlockState(position).getBlockHardness(worldIn, position) >= 0) {
                    boolean doOres = rand.nextInt(IafConfig.oreToStoneRatioForDragonCaves + 1) == 0;
                    if (doOres) {
                        int chance = rand.nextInt(199) + 1;
                        if (chance < 30) {
                            worldIn.setBlockState(blockPos, Blocks.IRON_ORE.getDefaultState(), 3);
                        }
                        if (chance > 30 && chance < 40) {
                            worldIn.setBlockState(blockPos, Blocks.GOLD_ORE.getDefaultState(), 3);
                        }
                        if (chance > 40 && chance < 45) {
                            worldIn.setBlockState(blockPos, IafConfig.generateCopperOre ? IafBlockRegistry.COPPER_ORE.getDefaultState() : IafBlockRegistry.CHARRED_STONE.getDefaultState(), 3);
                        }
                        if (chance > 45 && chance < 50) {
                            worldIn.setBlockState(blockPos, IafConfig.generateSilverOre ? IafBlockRegistry.SILVER_ORE.getDefaultState() : IafBlockRegistry.CHARRED_STONE.getDefaultState(), 3);
                        }
                        if (chance > 50 && chance < 60) {
                            worldIn.setBlockState(blockPos, Blocks.COAL_ORE.getDefaultState(), 3);
                        }
                        if (chance > 60 && chance < 70) {
                            worldIn.setBlockState(blockPos, Blocks.REDSTONE_ORE.getDefaultState(), 3);
                        }
                        if (chance > 70 && chance < 80) {
                            worldIn.setBlockState(blockPos, Blocks.LAPIS_ORE.getDefaultState(), 3);
                        }
                        if (chance > 80 && chance < 90) {
                            worldIn.setBlockState(blockPos, Blocks.DIAMOND_ORE.getDefaultState(), 3);
                        }
                        if (chance > 90 && chance < 1000) {
                            worldIn.setBlockState(blockPos, Blocks.EMERALD_ORE.getDefaultState(), 3);
                        }
                    } else {
                        worldIn.setBlockState(blockPos, rand.nextBoolean() ? IafBlockRegistry.CHARRED_COBBLESTONE.getDefaultState() : IafBlockRegistry.CHARRED_STONE.getDefaultState(), 2);
                    }
                }
            }
        });
        sphereList.add(new SphereInfo(radius, position));
    }

    private void hollowOut(IWorld worldIn, Random rand, BlockPos position, int radius) {
        int j = radius;
        int k = radius / 2;
        int l = radius;
        float f = (float) (j + k + l) * 0.333F + 0.5F;
        BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l)).map(BlockPos::toImmutable).forEach(blockPos ->  {
            if (blockPos.distanceSq(position) <= (double) (f * f * MathHelper.clamp(rand.nextFloat(), 0.75F, 1.0F))) {
                if (!(worldIn.getBlockState(position).getBlock() instanceof ContainerBlock)) {
                    worldIn.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
                }
            }
        });
    }


    private class SphereInfo {
        int radius;
        BlockPos pos;

        private SphereInfo(int radius, BlockPos pos) {
            this.radius = radius;
            this.pos = pos;
        }
    }
}
