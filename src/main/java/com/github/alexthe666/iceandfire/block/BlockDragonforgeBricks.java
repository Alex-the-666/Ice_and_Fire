package com.github.alexthe666.iceandfire.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDragonforge;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDragonforgeBrick;

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

public class BlockDragonforgeBricks extends ContainerBlock implements IDragonProof {

    public static final BooleanProperty GRILL = BooleanProperty.create("grill");
    private final int isFire;

    public BlockDragonforgeBricks(int isFire) {
        super(
    		Properties
    			.create(Material.ROCK)
    			.variableOpacity()
    			.hardnessAndResistance(40, 500)
    			.sound(SoundType.METAL)
		);

        this.setRegistryName(IceAndFire.MODID, "dragonforge_" + DragonType.getNameFromInt(isFire) + "_brick");
        this.isFire = isFire;
        this.setDefaultState(this.getStateContainer().getBaseState().with(GRILL, Boolean.valueOf(false)));
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult resultIn) {
        if (this.getConnectedTileEntity(worldIn, resultIn.getPos()) != null) {
            TileEntityDragonforge forge = this.getConnectedTileEntity(worldIn, resultIn.getPos());
            if (forge != null && forge.isFire == isFire) {
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
        return ActionResultType.FAIL;
    }

    public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
        if (!worldIn.isRemote) {
            this.checkGrill(worldIn, pos);
        }
    }

    public int tickRate(World worldIn) {
        return 3;
    }

    private void checkGrill(World worldIn, BlockPos pos) {
        BlockState state = worldIn.getBlockState(pos);
        boolean missingFurnace = getConnectedTileEntity(worldIn, pos) == null;
        worldIn.setBlockState(pos, state.with(GRILL, !missingFurnace));

    }

    private TileEntityDragonforge getConnectedTileEntity(World worldIn, BlockPos pos) {
        for (Direction facing : Direction.values()) {
            if (worldIn.getTileEntity(pos.offset(facing)) != null && worldIn.getTileEntity(pos.offset(facing)) instanceof TileEntityDragonforge) {
                TileEntityDragonforge forge = (TileEntityDragonforge) worldIn.getTileEntity(pos.offset(facing));
                if (forge != null && forge.assembled()) {
                    return forge;
                }
            }
        }
        return null;
    }

    public BlockState getStateFromMeta(int meta) {
        return this.getDefaultState().with(GRILL, Boolean.valueOf(meta > 0));
    }

    public int getMetaFromState(BlockState state) {
        return state.get(GRILL).booleanValue() ? 1 : 0;
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(GRILL);
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }


    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityDragonforgeBrick();
    }
}
