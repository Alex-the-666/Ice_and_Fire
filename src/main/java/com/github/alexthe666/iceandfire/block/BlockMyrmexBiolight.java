package com.github.alexthe666.iceandfire.block;

import java.util.Random;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockMyrmexBiolight extends BushBlock {

    public static final BooleanProperty CONNECTED_DOWN = BooleanProperty.create("down");

    public BlockMyrmexBiolight(boolean jungle) {
        super(
			Properties
				.create(Material.PLANTS)
				.notSolid()
				.doesNotBlockMovement()
				.variableOpacity()
				.hardnessAndResistance(0)
				.setLightLevel((state) -> { return 7; })
				.sound(SoundType.PLANT).tickRandomly()
		);

        this.setRegistryName(IceAndFire.MODID, jungle ? "myrmex_jungle_biolight" : "myrmex_desert_biolight");
        this.setDefaultState(this.getStateContainer().getBaseState().with(CONNECTED_DOWN, Boolean.valueOf(false)));
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos blockpos = pos.up();
        return worldIn.getBlockState(blockpos).getBlock() == this || worldIn.getBlockState(blockpos).isSolid();
    }


    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        boolean flag3 = worldIn.getBlockState(currentPos.down()).getBlock() == this;
        return stateIn.with(CONNECTED_DOWN, flag3);
    }

    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (!worldIn.isRemote) {
            this.updateState(state, worldIn, pos, state.getBlock());
        }
        if (!worldIn.getBlockState(pos.up()).isSolid() && worldIn.getBlockState(pos.up()).getBlock() != this) {
            worldIn.destroyBlock(pos, true);
        }
    }

    public void updateState(BlockState state, World worldIn, BlockPos pos, Block blockIn) {
        boolean flag2 = state.get(CONNECTED_DOWN);
        boolean flag3 = worldIn.getBlockState(pos.down()).getBlock() == this;
        if (flag2 != flag3) {
            worldIn.setBlockState(pos, state.with(CONNECTED_DOWN, Boolean.valueOf(flag3)), 3);
        }

    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(CONNECTED_DOWN);
    }
}