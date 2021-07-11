package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockMyrmexConnectedResin extends BreakableBlock {

    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");

    public BlockMyrmexConnectedResin(boolean jungle, boolean glass) {
        super(
    		Properties
    			.create(Material.ROCK)
    			.hardnessAndResistance(glass ? 1.5F : 3.5F)
    			.notSolid()
    			.variableOpacity()
    			.sound(glass ? SoundType.GLASS : SoundType.STONE)
		);

        this.setDefaultState(this.getStateContainer().getBaseState().with(UP, Boolean.valueOf(false))
                .with(DOWN, Boolean.valueOf(false))
                .with(NORTH, Boolean.valueOf(false))
                .with(EAST, Boolean.valueOf(false))
                .with(SOUTH, Boolean.valueOf(false))
                .with(WEST, Boolean.valueOf(false))
        );
        if (glass) {
            this.setRegistryName(IceAndFire.MODID, jungle ? "myrmex_jungle_resin_glass" : "myrmex_desert_resin_glass");
        } else {
            this.setRegistryName(IceAndFire.MODID, jungle ? "myrmex_jungle_resin_block" : "myrmex_desert_resin_block");
        }

    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        IBlockReader iblockreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        FluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
        BlockPos blockpos1 = blockpos.north();
        BlockPos blockpos2 = blockpos.east();
        BlockPos blockpos3 = blockpos.south();
        BlockPos blockpos4 = blockpos.west();
        BlockPos blockpos5 = blockpos.up();
        BlockPos blockpos6 = blockpos.down();
        BlockState blockstate = iblockreader.getBlockState(blockpos1);
        BlockState blockstate1 = iblockreader.getBlockState(blockpos2);
        BlockState blockstate2 = iblockreader.getBlockState(blockpos3);
        BlockState blockstate3 = iblockreader.getBlockState(blockpos4);
        BlockState blockstate4 = iblockreader.getBlockState(blockpos5);
        BlockState blockstate5 = iblockreader.getBlockState(blockpos6);
        return super.getStateForPlacement(context)
                .with(NORTH, Boolean.valueOf(this.canFenceConnectTo(blockstate, false, Direction.SOUTH)))
                .with(EAST, Boolean.valueOf(this.canFenceConnectTo(blockstate1, false, Direction.WEST)))
                .with(SOUTH, Boolean.valueOf(this.canFenceConnectTo(blockstate2, false, Direction.NORTH)))
                .with(WEST, Boolean.valueOf(this.canFenceConnectTo(blockstate3, false, Direction.EAST)))
                .with(UP, Boolean.valueOf(this.canFenceConnectTo(blockstate4, false, Direction.UP)))
                .with(DOWN, Boolean.valueOf(this.canFenceConnectTo(blockstate5, false, Direction.DOWN)));
    }

    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        BooleanProperty connect = null;
        switch (facing) {
            case NORTH:
                connect = NORTH;
                break;
            case SOUTH:
                connect = SOUTH;
                break;
            case EAST:
                connect = EAST;
                break;
            case WEST:
                connect = WEST;
                break;
            case DOWN:
                connect = DOWN;
                break;
            default:
                connect = UP;
                break;
        }
        return stateIn.with(connect, Boolean.valueOf(this.canFenceConnectTo(facingState, false, facing.getOpposite())));
    }


    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, DOWN, UP);
    }

    public boolean canFenceConnectTo(BlockState state, boolean isSideSolid, Direction direction) {
        return state.getBlock() == this;
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

}
