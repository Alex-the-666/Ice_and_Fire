package com.github.alexthe666.iceandfire.block;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.tile.TileEntityJar;
import com.github.alexthe666.iceandfire.item.ICustomRendered;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockJar extends ContainerBlock implements ICustomRendered {
    protected static final VoxelShape AABB = Block.makeCuboidShape(3, 0, 3, 13, 16, 13);
    public Item itemBlock;
    private boolean empty;
    private int pixieType;

    public BlockJar(int pixieType) {
        super(
    		pixieType != -1 ? 
				Properties
					.create(Material.GLASS)
					.notSolid()
					.variableOpacity()
					.hardnessAndResistance(1, 2)
					.sound(SoundType.GLASS)
					.setLightLevel((state) -> { return pixieType == -1 ? 0 : 10; })
					.lootFrom(IafBlockRegistry.JAR_EMPTY)
				: Properties
					.create(Material.GLASS)
					.notSolid()
					.variableOpacity()
					.hardnessAndResistance(1, 2)
					.sound(SoundType.GLASS)
		);

        this.empty = pixieType == -1;
        this.pixieType = pixieType;
        if (empty) {
            this.setRegistryName("iceandfire:pixie_jar_empty");
        } else {
            this.setRegistryName("iceandfire:pixie_jar_" + pixieType);
        }
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return AABB;
    }

    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return AABB;
    }


    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        dropPixie(worldIn, pos);
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    public void dropPixie(World world, BlockPos pos) {
        if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityJar && ((TileEntityJar) world.getTileEntity(pos)).hasPixie) {
            ((TileEntityJar) world.getTileEntity(pos)).releasePixie();
        }
    }

    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult resultIn) {
        if (!empty && world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityJar && ((TileEntityJar) world.getTileEntity(pos)).hasPixie && ((TileEntityJar) world.getTileEntity(pos)).hasProduced) {
            ((TileEntityJar) world.getTileEntity(pos)).hasProduced = false;
            ItemEntity item = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, new ItemStack(IafItemRegistry.PIXIE_DUST));
            if (!world.isRemote) {
                world.addEntity(item);
            }
            world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5, IafSoundRegistry.PIXIE_HURT, SoundCategory.NEUTRAL, 1, 1, false);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }


    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityJar) {
            if (!empty) {
                ((TileEntityJar) world.getTileEntity(pos)).hasPixie = true;
                ((TileEntityJar) world.getTileEntity(pos)).pixieType = pixieType;
            } else {
                ((TileEntityJar) world.getTileEntity(pos)).hasPixie = false;
            }
            world.getTileEntity(pos).markDirty();
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityJar(empty);
    }
}
