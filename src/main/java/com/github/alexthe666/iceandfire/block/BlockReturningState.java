package com.github.alexthe666.iceandfire.block;

import java.util.Random;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import net.minecraft.block.AbstractBlock;

public class BlockReturningState extends Block {
    public static final BooleanProperty REVERTS = BooleanProperty.create("revert");
    public Item itemBlock;
    private BlockState returnState;

    public BlockReturningState(Material materialIn, String name, String toolUsed, int toolStrength, float hardness, float resistance, SoundType sound, BlockState returnToState) {
        super(
    		AbstractBlock.Properties
    			.create(materialIn)
    			.sound(sound)
    			.hardnessAndResistance(hardness, resistance)
    			.harvestTool(ToolType.get(toolUsed))
    			.harvestLevel(toolStrength)
    			.tickRandomly()
		);

        setRegistryName(IceAndFire.MODID, name);
        this.returnState = returnToState;
        this.setDefaultState(this.stateContainer.getBaseState().with(REVERTS, Boolean.valueOf(false)));
    }

    @SuppressWarnings("deprecation")
    public BlockReturningState(Material materialIn, String name, String toolUsed, int toolStrength, float hardness, float resistance, SoundType sound, boolean slippery, BlockState returnToState) {
        super(AbstractBlock.Properties.create(materialIn).sound(sound).hardnessAndResistance(hardness, resistance).harvestTool(ToolType.get(toolUsed)).harvestLevel(toolStrength).slipperiness(0.98F).tickRandomly());
        setRegistryName(IceAndFire.MODID, name);
        this.returnState = returnToState;
        this.setDefaultState(this.stateContainer.getBaseState().with(REVERTS, Boolean.valueOf(false)));
    }

    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (!worldIn.isRemote) {
            if (!worldIn.isAreaLoaded(pos, 3))
                return;
            if (state.get(REVERTS) && rand.nextInt(3) == 0) {
                worldIn.setBlockState(pos, returnState);
            }
        }
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(REVERTS);
    }
}
