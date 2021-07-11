package com.github.alexthe666.iceandfire.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockDreadBase extends BlockGeneric implements IDragonProof, IDreadBlock {
    public static final BooleanProperty PLAYER_PLACED = BooleanProperty.create("player_placed");

    public BlockDreadBase(Material materialIn, String gameName, String toolUsed, int toolStrength, float hardness, float resistance, SoundType sound) {
        super(materialIn, gameName, toolUsed, toolStrength, hardness, resistance, sound);
        this.setDefaultState(this.stateContainer.getBaseState().with(PLAYER_PLACED, Boolean.valueOf(false)));
    }

    public BlockDreadBase(Material materialIn, String gameName, String name, String toolUsed, int toolStrength, float hardness, float resistance, SoundType sound, boolean slippery) {
        super(materialIn, gameName, toolUsed, toolStrength, hardness, resistance, sound, slippery);
        this.setDefaultState(this.stateContainer.getBaseState().with(PLAYER_PLACED, Boolean.valueOf(false)));
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

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PLAYER_PLACED);
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(PLAYER_PLACED, true);
    }

}
