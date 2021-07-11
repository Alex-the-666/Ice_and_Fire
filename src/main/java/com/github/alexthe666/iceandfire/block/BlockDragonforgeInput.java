package com.github.alexthe666.iceandfire.block;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDragonforge;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDragonforgeInput;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockDragonforgeInput extends ContainerBlock implements IDragonProof {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    private int dragonType;

    public BlockDragonforgeInput(int dragonType) {
        super(
    		Properties
    			.create(Material.ROCK)
    			.variableOpacity()
    			.hardnessAndResistance(40, 500)
    			.sound(SoundType.METAL)
		);

        this.setRegistryName(IceAndFire.MODID, "dragonforge_" + DragonType.getNameFromInt(dragonType) + "_input");
        this.dragonType = dragonType;
        this.setDefaultState(this.getStateContainer().getBaseState().with(ACTIVE, Boolean.valueOf(false)));
    }


    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult resultIn) {
        if (this.getConnectedTileEntity(worldIn, resultIn.getPos()) != null) {
            TileEntityDragonforge forge = this.getConnectedTileEntity(worldIn, resultIn.getPos());
            if (forge != null && forge.isFire == dragonType) {
                if (worldIn.isRemote) {
                    IceAndFire.PROXY.setRefrencedTE(worldIn.getTileEntity(forge.getPos()));
                } else {
                    INamedContainerProvider inamedcontainerprovider = this.getContainer(forge.getBlockState(), worldIn, forge.getPos());
                    if (inamedcontainerprovider != null) {
                        player.openContainer(inamedcontainerprovider);
                    }
                }
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.SUCCESS;
    }

    private TileEntityDragonforge getConnectedTileEntity(World worldIn, BlockPos pos) {
        for (Direction facing : Direction.values()) {
            if (worldIn.getTileEntity(pos.offset(facing)) != null && worldIn.getTileEntity(pos.offset(facing)) instanceof TileEntityDragonforge) {
                return (TileEntityDragonforge) worldIn.getTileEntity(pos.offset(facing));
            }
        }
        return null;
    }

    public BlockState getStateFromMeta(int meta) {
        return this.getDefaultState().with(ACTIVE, Boolean.valueOf(meta > 0));
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public int getMetaFromState(BlockState state) {
        return state.get(ACTIVE).booleanValue() ? 1 : 0;
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (worldIn.getTileEntity(pos) instanceof TileEntityDragonforgeInput) {
            ((TileEntityDragonforgeInput) worldIn.getTileEntity(pos)).resetCore();
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityDragonforgeInput();
    }
}
