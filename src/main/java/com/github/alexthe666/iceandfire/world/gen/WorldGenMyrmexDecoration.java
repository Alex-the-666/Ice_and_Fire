package com.github.alexthe666.iceandfire.world.gen;

import java.util.Random;
import java.util.stream.Collectors;

import com.github.alexthe666.iceandfire.block.BlockGoldPile;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class WorldGenMyrmexDecoration {

    public static final ResourceLocation MYRMEX_GOLD_CHEST = new ResourceLocation("iceandfire", "chest/myrmex_loot_chest");
    public static final ResourceLocation DESERT_MYRMEX_FOOD_CHEST = new ResourceLocation("iceandfire", "chest/myrmex_desert_food_chest");
    public static final ResourceLocation JUNGLE_MYRMEX_FOOD_CHEST = new ResourceLocation("iceandfire", "chest/myrmex_jungle_food_chest");
    public static final ResourceLocation MYRMEX_TRASH_CHEST = new ResourceLocation("iceandfire", "chest/myrmex_trash_chest");
    private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    public static void generateSkeleton(IWorld worldIn, BlockPos blockpos, BlockPos origin, int radius, Random rand) {
        if (worldIn.getBlockState(blockpos.down()).isSolidSide(worldIn, blockpos.down(), Direction.UP)) {
            Direction direction = Direction.byHorizontalIndex(rand.nextInt(3));
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
    }

    public static void generateLeaves(IWorld worldIn, BlockPos blockpos, BlockPos origin, int radius, Random rand, boolean jungle) {
        if (worldIn.getBlockState(blockpos.down()).isSolidSide(worldIn, blockpos.down(), Direction.UP)) {
            BlockState leaf = Blocks.OAK_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, Boolean.valueOf(true));
            if (jungle) {
                leaf = Blocks.JUNGLE_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, Boolean.valueOf(true));
            }
            int i1 = 0;
            for (int i = 0; i1 >= 0 && i < 3; ++i) {
                int j = i1 + rand.nextInt(2);
                int k = i1 + rand.nextInt(2);
                int l = i1 + rand.nextInt(2);
                float f = (float) (j + k + l) * 0.333F + 0.5F;
                for (BlockPos pos : BlockPos.getAllInBox(blockpos.add(-j, -k, -l), blockpos.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet())) {
                    if (pos.distanceSq(blockpos) <= (double) (f * f) && worldIn.isAirBlock(pos)) {
                        worldIn.setBlockState(pos, leaf, 4);
                    }
                }
                blockpos = blockpos.add(-(i1 + 1) + rand.nextInt(2 + i1 * 2), 0 - rand.nextInt(2), -(i1 + 1) + rand.nextInt(2 + i1 * 2));
            }
        }
    }

    public static void generatePumpkins(IWorld worldIn, BlockPos blockpos, BlockPos origin, int radius, Random rand, boolean jungle) {
        if (worldIn.getBlockState(blockpos.down()).isSolidSide(worldIn, blockpos.down(), Direction.UP)) {
            worldIn.setBlockState(blockpos, jungle ? Blocks.MELON.getDefaultState() : Blocks.PUMPKIN.getDefaultState(), 2);
        }
    }

    public static void generateCocoon(IWorld worldIn, BlockPos blockpos, Random rand, boolean jungle, ResourceLocation lootTable) {
        if (worldIn.getBlockState(blockpos.down()).isSolidSide(worldIn, blockpos.down(), Direction.UP)) {
            worldIn.setBlockState(blockpos, jungle ? IafBlockRegistry.JUNGLE_MYRMEX_COCOON.getDefaultState() : IafBlockRegistry.DESERT_MYRMEX_COCOON.getDefaultState(), 3);

            if (worldIn.getTileEntity(blockpos) != null && worldIn.getTileEntity(blockpos) instanceof LockableLootTileEntity) {
                TileEntity tileentity1 = worldIn.getTileEntity(blockpos);
                ((LockableLootTileEntity) tileentity1).setLootTable(lootTable, rand.nextLong());

            }
        }
    }

    public static void generateMushrooms(IWorld worldIn, BlockPos blockpos, BlockPos origin, int radius, Random rand) {
        if (worldIn.getBlockState(blockpos.down()).isSolidSide(worldIn, blockpos.down(), Direction.UP)) {
            worldIn.setBlockState(blockpos, rand.nextBoolean() ? Blocks.BROWN_MUSHROOM.getDefaultState() : Blocks.RED_MUSHROOM.getDefaultState(), 2);
        }
    }

    public static void generateGold(IWorld worldIn, BlockPos blockpos, BlockPos origin, int radius, Random rand) {
        BlockState gold = IafBlockRegistry.GOLD_PILE.getDefaultState();
        int choice = rand.nextInt(2);
        if(choice == 1){
            gold = IafBlockRegistry.SILVER_PILE.getDefaultState();
        }else if(choice == 2){
            gold = IafBlockRegistry.COPPER_PILE.getDefaultState();
        }
        if (worldIn.getBlockState(blockpos.down()).isSolidSide(worldIn, blockpos.down(), Direction.UP)) {
            worldIn.setBlockState(blockpos, gold.with(BlockGoldPile.LAYERS, 8), 3);
            worldIn.setBlockState(MyrmexHive.getGroundedPos(worldIn, blockpos.north()), gold.with(BlockGoldPile.LAYERS, 1 + new Random().nextInt(7)), 3);
            worldIn.setBlockState(MyrmexHive.getGroundedPos(worldIn, blockpos.south()), gold.with(BlockGoldPile.LAYERS, 1 + new Random().nextInt(7)), 3);
            worldIn.setBlockState(MyrmexHive.getGroundedPos(worldIn, blockpos.west()), gold.with(BlockGoldPile.LAYERS, 1 + new Random().nextInt(7)), 3);
            worldIn.setBlockState(MyrmexHive.getGroundedPos(worldIn, blockpos.east()), gold.with(BlockGoldPile.LAYERS, 1 + new Random().nextInt(7)), 3);
            if (rand.nextInt(3) == 0) {
                worldIn.setBlockState(blockpos.up(), Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, HORIZONTALS[new Random().nextInt(3)]), 2);
                if (worldIn.getBlockState(blockpos.up()).getBlock() instanceof ChestBlock) {
                    TileEntity tileentity1 = worldIn.getTileEntity(blockpos.up());
                    if (tileentity1 instanceof ChestTileEntity) {
                        ((ChestTileEntity) tileentity1).setLootTable(MYRMEX_GOLD_CHEST, rand.nextLong());
                    }
                }
            }
        }
    }

    public static void generateTrashHeap(IWorld worldIn, BlockPos blockpos, BlockPos origin, int radius, Random rand) {
        if (worldIn.getBlockState(blockpos.down()).isSolidSide(worldIn, blockpos.down(), Direction.UP)) {
            Block blob = Blocks.DIRT;
            switch (rand.nextInt(3)) {
                case 0:
                    blob = Blocks.DIRT;
                    break;
                case 1:
                    blob = Blocks.SAND;
                    break;
                case 2:
                    blob = Blocks.COBBLESTONE;
                    break;
                case 3:
                    blob = Blocks.GRAVEL;
                    break;
            }
            int i1 = 0;
            for (int i = 0; i1 >= 0 && i < 3; ++i) {
                int j = i1 + rand.nextInt(2);
                int k = i1 + rand.nextInt(2);
                int l = i1 + rand.nextInt(2);
                float f = (float) (j + k + l) * 0.333F + 0.5F;
                for (BlockPos pos : BlockPos.getAllInBox(blockpos.add(-j, -k, -l), blockpos.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet())) {
                    if (pos.distanceSq(blockpos) <= (double) (f * f)) {
                        worldIn.setBlockState(pos, blob.getDefaultState(), 4);
                    }
                }
                blockpos = blockpos.add(-(i1 + 1) + rand.nextInt(2 + i1 * 2), 0 - rand.nextInt(2), -(i1 + 1) + rand.nextInt(2 + i1 * 2));
            }

        }
    }

    public static void generateTrashOre(IWorld worldIn, BlockPos blockpos, BlockPos origin, int radius, Random rand) {
        Block current = worldIn.getBlockState(blockpos).getBlock();
        if (origin.distanceSq(blockpos) <= (double) (radius * radius)) {
            if (current == Blocks.DIRT || current == Blocks.SAND || current == Blocks.COBBLESTONE || current == Blocks.GRAVEL) {
                Block ore = Blocks.REDSTONE_ORE;
                if (rand.nextInt(3) == 0) {
                    ore = rand.nextBoolean() ? Blocks.GOLD_ORE : IafBlockRegistry.SILVER_ORE;
                    if(rand.nextInt(2) == 0){
                        ore = IafBlockRegistry.COPPER_ORE;
                    }
                } else if (rand.nextInt(3) == 0) {
                    ore = Blocks.DIAMOND_ORE;
                } else if (rand.nextInt(2) == 0) {
                    ore = rand.nextBoolean() ? Blocks.EMERALD_ORE : IafBlockRegistry.SAPPHIRE_ORE;
                    if(rand.nextInt(2) == 0){
                        ore = IafBlockRegistry.AMYTHEST_ORE;
                    }
                }
                worldIn.setBlockState(blockpos, ore.getDefaultState(), 2);
            }
        }
    }
}