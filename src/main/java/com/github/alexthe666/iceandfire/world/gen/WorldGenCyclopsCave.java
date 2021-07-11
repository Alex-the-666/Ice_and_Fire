package com.github.alexthe666.iceandfire.world.gen;

import java.util.Random;
import java.util.stream.Collectors;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.block.BlockGoldPile;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.EntityCyclops;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.mojang.serialization.Codec;

import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class WorldGenCyclopsCave extends Feature<NoFeatureConfig> {

    public static final ResourceLocation CYCLOPS_CHEST = new ResourceLocation("iceandfire", "chest/cyclops_cave");
    private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    public WorldGenCyclopsCave(Codec<NoFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
    }

    private void genSheepPen(IServerWorld worldIn, BlockPos blockpos, Random rand, BlockPos origin, float radius) {

        int width = 5 + rand.nextInt(3);
        int sheeps = 2 + rand.nextInt(3);
        int sheepsSpawned = 0;
        Direction direction = Direction.NORTH;
        BlockPos end = blockpos;
        for (int sideCount = 0; sideCount < 4; sideCount++) {
            for (int side = 0; side < width; side++) {
                if (origin.distanceSq(end.offset(direction, side)) <= (double) (radius * radius)) {
                    worldIn.setBlockState(end.offset(direction, side), getFenceState(worldIn, end.offset(direction, side)), 3);
                    if (worldIn.isAirBlock(end.offset(direction, side).offset(direction.rotateY())) && sheepsSpawned < sheeps) {
                        BlockPos sheepPos = end.offset(direction, side).offset(direction.rotateY());

                        SheepEntity entitySheep = new SheepEntity(EntityType.SHEEP, worldIn.getWorld());
                        entitySheep.setPosition(sheepPos.getX() + 0.5F, sheepPos.getY() + 0.5F, sheepPos.getZ() + 0.5F);
                        entitySheep.setFleeceColor(rand.nextInt(4) == 0 ? DyeColor.YELLOW : DyeColor.WHITE);
                        worldIn.addEntity(entitySheep);

                        sheepsSpawned++;
                    }
                }
            }
            end = end.offset(direction, width);
            direction = direction.rotateY();
        }
        for (int sideCount = 0; sideCount < 4; sideCount++) {
            for (int side = 0; side < width; side++) {
                if (origin.distanceSq(end.offset(direction, side)) <= (double) (radius * radius)) {
                    worldIn.setBlockState(end.offset(direction, side), getFenceState(worldIn, end.offset(direction, side)), 3);
                }
            }
            end = end.offset(direction, width);
            direction = direction.rotateY();
        }
        for (int x = 1; x < width - 1; x++) {
            for (int z = 1; z < width - 1; z++) {
                if (origin.distanceSq(end.add(x, 0, z)) <= (double) (radius * radius)) {
                    worldIn.setBlockState(end.add(x, 0, z), Blocks.AIR.getDefaultState(), 2);
                }
            }
        }
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

    private void genSkeleton(IWorld worldIn, BlockPos blockpos, Random rand, BlockPos origin, float radius) {
        Direction direction = HORIZONTALS[new Random().nextInt(3)];
        Direction.Axis oppositeAxis = direction.getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        int maxRibHeight = rand.nextInt(2);
        for (int spine = 0; spine < 5 + rand.nextInt(2) * 2; spine++) {
            BlockPos segment = blockpos.offset(direction, spine);
            if (origin.distanceSq(segment) <= (double) (radius * radius)) {
                worldIn.setBlockState(segment, Blocks.BONE_BLOCK.getDefaultState().with(RotatedPillarBlock.AXIS, direction.getAxis()), 2);
            }
            if (spine % 2 != 0) {
                BlockPos rightRib = segment.offset(direction.rotateYCCW());
                BlockPos leftRib = segment.offset(direction.rotateY());
                if (origin.distanceSq(rightRib) <= (double) (radius * radius)) {
                    worldIn.setBlockState(rightRib, Blocks.BONE_BLOCK.getDefaultState().with(RotatedPillarBlock.AXIS, oppositeAxis), 2);
                }
                if (origin.distanceSq(leftRib) <= (double) (radius * radius)) {
                    worldIn.setBlockState(leftRib, Blocks.BONE_BLOCK.getDefaultState().with(RotatedPillarBlock.AXIS, oppositeAxis), 2);
                }
                for (int ribHeight = 1; ribHeight < maxRibHeight + 2; ribHeight++) {
                    if (origin.distanceSq(rightRib.up(ribHeight).offset(direction.rotateYCCW())) <= (double) (radius * radius)) {
                        worldIn.setBlockState(rightRib.up(ribHeight).offset(direction.rotateYCCW()), Blocks.BONE_BLOCK.getDefaultState().with(RotatedPillarBlock.AXIS, Direction.Axis.Y), 2);
                    }
                    if (origin.distanceSq(leftRib.up(ribHeight).offset(direction.rotateY())) <= (double) (radius * radius)) {
                        worldIn.setBlockState(leftRib.up(ribHeight).offset(direction.rotateY()), Blocks.BONE_BLOCK.getDefaultState().with(RotatedPillarBlock.AXIS, Direction.Axis.Y), 2);
                    }
                }
                if (origin.distanceSq(rightRib.up(maxRibHeight + 2)) <= (double) (radius * radius)) {
                    worldIn.setBlockState(rightRib.up(maxRibHeight + 2), Blocks.BONE_BLOCK.getDefaultState().with(RotatedPillarBlock.AXIS, oppositeAxis), 2);
                }
                if (origin.distanceSq(leftRib.up(maxRibHeight + 2)) <= (double) (radius * radius)) {
                    worldIn.setBlockState(leftRib.up(maxRibHeight + 2), Blocks.BONE_BLOCK.getDefaultState().with(RotatedPillarBlock.AXIS, oppositeAxis), 2);
                }
            }

        }
    }

    @Override
    public boolean generate(ISeedReader worldIn, ChunkGenerator p_230362_3_, Random rand, BlockPos position, NoFeatureConfig p_230362_6_) {
        if(!IafWorldRegistry.isDimensionListedForFeatures(worldIn)){
            return false;
        }
        if (!IafConfig.generateCyclopsCaves || rand.nextInt(IafConfig.spawnCyclopsCaveChance) != 0 || !IafWorldRegistry.isFarEnoughFromSpawn(worldIn, position)) {
            return false;
        }
        position = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, position);
        if (!worldIn.getFluidState(position.down()).isEmpty()) {
            return false;
        }
        int i1 = 16;
        int i2 = i1 - 2;
        int sheepPenCount = 0;
        int dist = 6;
        if (worldIn.isAirBlock(position.add(i1 - dist, -3, -i1 + dist)) || worldIn.isAirBlock(position.add(i1 - dist, -3, i1 - dist)) || worldIn.isAirBlock(position.add(-i1 + dist, -3, -i1 + dist)) || worldIn.isAirBlock(position.add(-i1 + dist, -3, i1 - dist))) {
            return false;
        }

        {
            int ySize = rand.nextInt(2);
            int j = i1 + rand.nextInt(2);
            int k = 12 + ySize;
            int l = i1 + rand.nextInt(2);
            float f = (float) (j + k + l) * 0.333F + 0.5F;


            for (BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet())) {
                boolean doorwayX = blockpos.getX() >= position.getX() - 2 + rand.nextInt(2) && blockpos.getX() <= position.getX() + 2 + rand.nextInt(2);
                boolean doorwayZ = blockpos.getZ() >= position.getZ() - 2 + rand.nextInt(2) && blockpos.getZ() <= position.getZ() + 2 + rand.nextInt(2);
                boolean isNotInDoorway = !doorwayX && !doorwayZ && blockpos.getY() > position.getY() || blockpos.getY() > position.getY() + k - (3 + rand.nextInt(2));
                if (blockpos.distanceSq(position) <= (double) (f * f)) {
                    if (!(worldIn.getBlockState(position).getBlock() instanceof AbstractChestBlock) && worldIn.getBlockState(position).getBlockHardness(worldIn, position) >= 0 && isNotInDoorway) {
                        worldIn.setBlockState(blockpos, Blocks.STONE.getDefaultState(), 3);
                    }
                    if (blockpos.getY() == position.getY()) {
                        worldIn.setBlockState(blockpos, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 3);
                    }
                    if (blockpos.getY() <= position.getY() - 1 && !worldIn.getBlockState(blockpos).isSolid()) {
                        worldIn.setBlockState(blockpos, Blocks.COBBLESTONE.getDefaultState(), 3);
                    }
                }
            }


        }
        {
            int ySize = rand.nextInt(2);
            int j = i2 + rand.nextInt(2);
            int k = 10 + ySize;
            int l = i2 + rand.nextInt(2);
            float f = (float) (j + k + l) * 0.333F + 0.5F;
            for (BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet())) {
                if (blockpos.distanceSq(position) <= (double) (f * f) && blockpos.getY() > position.getY()) {
                    if (!(worldIn.getBlockState(position).getBlock() instanceof AbstractChestBlock)) {
                        worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 3);

                    }
                }
            }
            for (BlockPos blockpos : BlockPos.getAllInBox(position.add(-j, -k, -l), position.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet())) {
                if (blockpos.distanceSq(position) <= (double) (f * f) && blockpos.getY() == position.getY()) {
                    if (rand.nextInt(130) == 0 && isTouchingAir(worldIn, blockpos.up())) {
                        this.genSkeleton(worldIn, blockpos.up(), rand, position, f);
                    }

                    if (rand.nextInt(130) == 0 && blockpos.distanceSq(position) <= (double) (f * f) * 0.8F && sheepPenCount < 2) {
                        this.genSheepPen(worldIn, blockpos.up(), rand, position, f);
                        sheepPenCount++;
                    }
                    if (rand.nextInt(80) == 0 && isTouchingAir(worldIn, blockpos.up())) {
                        worldIn.setBlockState(blockpos.up(), IafBlockRegistry.GOLD_PILE.getDefaultState().with(BlockGoldPile.LAYERS, 8), 3);
                        worldIn.setBlockState(blockpos.up().north(), IafBlockRegistry.GOLD_PILE.getDefaultState().with(BlockGoldPile.LAYERS, 1 + new Random().nextInt(7)), 3);
                        worldIn.setBlockState(blockpos.up().south(), IafBlockRegistry.GOLD_PILE.getDefaultState().with(BlockGoldPile.LAYERS, 1 + new Random().nextInt(7)), 3);
                        worldIn.setBlockState(blockpos.up().west(), IafBlockRegistry.GOLD_PILE.getDefaultState().with(BlockGoldPile.LAYERS, 1 + new Random().nextInt(7)), 3);
                        worldIn.setBlockState(blockpos.up().east(), IafBlockRegistry.GOLD_PILE.getDefaultState().with(BlockGoldPile.LAYERS, 1 + new Random().nextInt(7)), 3);
                        worldIn.setBlockState(blockpos.up(2), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, HORIZONTALS[new Random().nextInt(3)]), 2);
                        if (worldIn.getBlockState(blockpos.up(2)).getBlock() instanceof AbstractChestBlock) {
                            TileEntity tileentity1 = worldIn.getTileEntity(blockpos.up(2));
                            if (tileentity1 instanceof ChestTileEntity) {
                                ((ChestTileEntity) tileentity1).setLootTable(CYCLOPS_CHEST, rand.nextLong());
                            }
                        }

                    }


                    if (rand.nextInt(50) == 0 && isTouchingAir(worldIn, blockpos.up())) {
                        int torchHeight = rand.nextInt(2) + 1;
                        for (int fence = 0; fence < torchHeight; fence++) {
                            worldIn.setBlockState(blockpos.up(1 + fence), getFenceState(worldIn, blockpos.up(1 + fence)), 3);
                        }
                        worldIn.setBlockState(blockpos.up(1 + torchHeight), Blocks.TORCH.getDefaultState(), 2);
                    }
                }
            }
        }
        EntityCyclops cyclops = new EntityCyclops(IafEntityRegistry.CYCLOPS, worldIn.getWorld());
        cyclops.setVariant(rand.nextInt(3));
        cyclops.setPositionAndRotation(position.getX() + 0.5, position.getY() + 1.5, position.getZ() + 0.5, rand.nextFloat() * 360, 0);
        worldIn.addEntity(cyclops);
        return true;
    }

    public BlockState getFenceState(IWorld world, BlockPos pos) {
        boolean east = world.getBlockState(pos.east()).getBlock() == Blocks.OAK_FENCE;
        boolean west = world.getBlockState(pos.west()).getBlock() == Blocks.OAK_FENCE;
        boolean north = world.getBlockState(pos.north()).getBlock() == Blocks.OAK_FENCE;
        boolean south = world.getBlockState(pos.south()).getBlock() == Blocks.OAK_FENCE;
        return Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.EAST, east).with(FenceBlock.WEST, west).with(FenceBlock.NORTH, north).with(FenceBlock.SOUTH, south);
    }
}
