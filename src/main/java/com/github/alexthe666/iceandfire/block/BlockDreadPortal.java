package com.github.alexthe666.iceandfire.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDreadPortal;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.item.ICustomRendered;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockDreadPortal extends ContainerBlock implements IDreadBlock, ICustomRendered {

    public BlockDreadPortal() {
        super(
    		Properties
    			.create(Material.PORTAL)
    			.variableOpacity()
    			.hardnessAndResistance(-1, 100000)
    			.harvestTool(ToolType.PICKAXE)
    			.setLightLevel((state) -> { return 1; })
    			.tickRandomly()
		);

        this.setRegistryName(IceAndFire.MODID, "dread_portal");
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entity) {
     /* if(entity.dimension != IafConfig.dreadlandsDimensionId){
            MiscEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(entity, MiscEntityProperties.class);
            if (properties != null) {
                properties.lastEnteredDreadPortalX = pos.getX();
                properties.lastEnteredDreadPortalY = pos.getY();
                properties.lastEnteredDreadPortalZ = pos.getZ();
            }
        }
        if ((!entity.isBeingRidden()) && (entity.getPassengers().isEmpty()) && (entity instanceof PlayerEntityMP)) {
            CriteriaTriggers.ENTER_BLOCK.trigger((PlayerEntityMP) entity, world.getBlockState(pos));
            PlayerEntityMP thePlayer = (PlayerEntityMP) entity;
            if (thePlayer.timeUntilPortal > 0) {
                thePlayer.timeUntilPortal = 10;
            } else if (thePlayer.dimension != IafConfig.dreadlandsDimensionId) {
                thePlayer.timeUntilPortal = 10;
                thePlayer.changeDimension(IafConfig.dreadlandsDimensionId, new TeleporterDreadLands(thePlayer.server.getWorld(IafConfig.dreadlandsDimensionId), false));
            } else {
                MiscEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(thePlayer, MiscEntityProperties.class);
                BlockPos setPos = BlockPos.ORIGIN;
                if (properties != null) {
                    setPos = new BlockPos(properties.lastEnteredDreadPortalX, properties.lastEnteredDreadPortalY, properties.lastEnteredDreadPortalZ);
                }
                thePlayer.timeUntilPortal = 10;
                thePlayer.changeDimension( 0, new TeleporterDreadLands(thePlayer.server.getWorld(0), true));
                thePlayer.setPositionAndRotation(setPos.getX(), setPos.getY() + 0.5D, setPos.getZ(), 0, 0);

            }
        }*/
    }


    public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
        if (!this.canSurviveAt(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!this.canSurviveAt(worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    public boolean canSurviveAt(World world, BlockPos pos) {
        return DragonUtils.isDreadBlock(world.getBlockState(pos.up())) && DragonUtils.isDreadBlock(world.getBlockState(pos.down()));
    }

    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDreadPortal();
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityDreadPortal) {
            int i = 3;
            for (int j = 0; j < i; ++j) {
                double d0 = (float) pos.getX() + rand.nextFloat();
                double d1 = (float) pos.getY() + rand.nextFloat();
                double d2 = (float) pos.getZ() + rand.nextFloat();
                double d3 = ((double) rand.nextFloat() - 0.5D) * 0.25D;
                double d4 = ((double) rand.nextFloat()) * -0.25D;
                double d5 = ((double) rand.nextFloat() - 0.5D) * 0.25D;
                int k = rand.nextInt(2) * 2 - 1;
                IceAndFire.PROXY.spawnParticle("dread_portal", d0, d1, d2, d3, d4, d5);
                //worldIn.spawnParticle(ParticleTypes.END_ROD, d0, d1, d2, d3, d4, d5);
            }
        }
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileEntityDreadPortal();
    }
}