package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockDreadWoodLock extends Block implements IDragonProof, IDreadBlock {
    public static final BooleanProperty PLAYER_PLACED = BooleanProperty.create("player_placed");

    public BlockDreadWoodLock() {
        super(
    		Properties
    			.create(Material.WOOD)
    			.hardnessAndResistance(-1.0F, 1000000F)
    			.sound(SoundType.WOOD)
		);

        this.setRegistryName(IceAndFire.MODID, "dreadwood_planks_lock");
        this.setDefaultState(this.getStateContainer().getBaseState().with(PLAYER_PLACED, Boolean.valueOf(false)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
        if (state.get(PLAYER_PLACED)) {
            float f = 8f;
            //Code from super method
            return player.getDigSpeed(state, pos) / f / (float) 30;
        }
        return super.getPlayerRelativeBlockHardness(state,player,worldIn,pos);
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult resultIn) {
        ItemStack stack = player.getHeldItem(handIn);
        if (stack.getItem() == IafItemRegistry.DREAD_KEY) {
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            deleteNearbyWood(worldIn, pos, pos);
            worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.BLOCKS, 1, 1, false);
            worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 1, 2, false);
        }
        return ActionResultType.SUCCESS;
    }

    private void deleteNearbyWood(World worldIn, BlockPos pos, BlockPos startPos) {
        if (pos.distanceSq(startPos) < 32) {
            if (worldIn.getBlockState(pos).getBlock() == IafBlockRegistry.DREADWOOD_PLANKS || worldIn.getBlockState(pos).getBlock() == IafBlockRegistry.DREADWOOD_PLANKS_LOCK) {
                worldIn.destroyBlock(pos, false);
                for (Direction facing : Direction.values()) {
                    deleteNearbyWood(worldIn, pos.offset(facing), startPos);
                }
            }
        }
    }


    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PLAYER_PLACED);
    }

    public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        return this.getDefaultState().with(PLAYER_PLACED, true);
    }
}
