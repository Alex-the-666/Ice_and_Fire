package com.github.alexthe666.iceandfire.block;

import java.util.Random;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassPathBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import net.minecraft.block.AbstractBlock;

public class BlockCharedPath extends GrassPathBlock {
    public static final BooleanProperty REVERTS = BooleanProperty.create("revert");
    public Item itemBlock;
    public int dragonType;

    @SuppressWarnings("deprecation")
    public BlockCharedPath(int dragonType) {
        super(
    		AbstractBlock.Properties
    			.create(Material.PLANTS)
    			.sound(dragonType != 1 ? SoundType.GROUND : SoundType.GLASS)
    			.hardnessAndResistance(0.6F).harvestTool(ToolType.SHOVEL)
    			.harvestLevel(0)
    			.slipperiness(dragonType != 1 ? 0.6F : 0.98F)
    			.tickRandomly()
    			.setRequiresTool()
		);

        this.dragonType = dragonType;
        setRegistryName(IceAndFire.MODID, getNameFromType(dragonType));
        this.setDefaultState(stateContainer.getBaseState().with(REVERTS, Boolean.valueOf(false)));
    }

    public String getNameFromType(int dragonType){
        switch (dragonType){
            case 0:
                return "chared_grass_path";
            case 1:
                return "frozen_grass_path";
            case 2:
                return "crackled_grass_path";
        }
        return "";
    }

    public BlockState getSmushedState(int dragonType){
        switch (dragonType){
            case 0:
                return IafBlockRegistry.CHARRED_DIRT.getDefaultState();
            case 1:
                return  IafBlockRegistry.FROZEN_DIRT.getDefaultState();
            case 2:
                return IafBlockRegistry.CRACKLED_DIRT.getDefaultState();
        }
        return null;
    }

    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        super.tick(state, worldIn, pos, rand);
        if (!worldIn.isRemote) {
            if (!worldIn.isAreaLoaded(pos, 3))
                return;
            if (state.get(REVERTS) && rand.nextInt(3) == 0) {
                worldIn.setBlockState(pos, Blocks.GRASS_PATH.getDefaultState());
            }
        }
        if (worldIn.getBlockState(pos.up()).getMaterial().isSolid()) {
            worldIn.setBlockState(pos, getSmushedState(dragonType));
        }
        updateBlockState(worldIn, pos);
    }

    private void updateBlockState(World worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos.up()).getMaterial().isSolid()) {
            worldIn.setBlockState(pos, getSmushedState(dragonType));
        }
    }

    public BlockState getStateFromMeta(int meta) {
        return this.getDefaultState().with(REVERTS, meta == 1);
    }

    public int getMetaFromState(BlockState state) {
        return state.get(REVERTS) ? 1 : 0;
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(REVERTS);
    }
}
