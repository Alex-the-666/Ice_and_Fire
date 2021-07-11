package com.github.alexthe666.iceandfire.entity.tile;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.block.BlockDragonforgeInput;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class TileEntityDragonforgeInput extends TileEntity implements ITickableTileEntity {
    private static final int LURE_DISTANCE = 50;
    private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private int ticksSinceDragonFire;
    private TileEntityDragonforge core = null;

    public TileEntityDragonforgeInput() {
        super(IafTileEntityRegistry.DRAGONFORGE_INPUT);
    }

    public void onHitWithFlame() {
        if (core != null) {
            core.transferPower(1);
        }
    }

    @Override
    public void tick() {
        if (core == null) {
            core = getConnectedTileEntity();
        }
        if (ticksSinceDragonFire > 0) {
            ticksSinceDragonFire--;
        }
        if ((ticksSinceDragonFire == 0 || core == null) && this.isActive()) {
            TileEntity tileentity = world.getTileEntity(pos);
            world.setBlockState(pos, getDeactivatedState());
            if (tileentity != null) {
                tileentity.validate();
                world.setTileEntity(pos, tileentity);
            }
        }
        lureDragons();

    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        read(this.getBlockState(), packet.getNbtCompound());
    }

    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    protected void lureDragons() {
        Vector3d targetPosition = new Vector3d(
            this.getPos().getX() + 0.5F,
            this.getPos().getY() + 0.5F,
            this.getPos().getZ() + 0.5F
        );

        AxisAlignedBB searchArea = new AxisAlignedBB(
            (double) pos.getX() - LURE_DISTANCE,
            (double) pos.getY() - LURE_DISTANCE,
            (double) pos.getZ() - LURE_DISTANCE,
            (double) pos.getX() + LURE_DISTANCE,
            (double) pos.getY() + LURE_DISTANCE,
            (double) pos.getZ() + LURE_DISTANCE
        );

        boolean dragonSelected = false;
        for (EntityDragonBase dragon : world.getEntitiesWithinAABB(EntityDragonBase.class, searchArea)) {
            if (
                !dragonSelected &&

                // Forge Core Checks
                core != null &&
                core.assembled() &&
                core.canSmelt() &&

                // Dragon Checks
                getDragonType() == DragonType.getIntFromType(dragon.dragonType) &&
                (dragon.isChained() || dragon.isTamed()) &&
                canSeeInput(dragon, targetPosition)
            ) {
                dragon.burningTarget = this.pos;
                dragonSelected = true;

            } else if(dragon.burningTarget == this.pos) {
                dragon.burningTarget = null;
                dragon.setBreathingFire(false);
            }
        }
    }

    public void resetCore() {
        core = null;
    }

    private boolean canSeeInput(EntityDragonBase dragon, Vector3d target) {
        if (target != null) {
            RayTraceResult rayTrace = this.world.rayTraceBlocks(new RayTraceContext(dragon.getHeadPosition(), target, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, dragon));
            if (rayTrace != null && rayTrace.getHitVec() != null) {
                double distance = target.distanceTo(rayTrace.getHitVec());
                return distance < 1.0F;
            }
        }
        return false;
    }

    private BlockState getDeactivatedState() {
        switch (getDragonType()){
            case 0:
                return IafBlockRegistry.DRAGONFORGE_FIRE_INPUT.getDefaultState().with(BlockDragonforgeInput.ACTIVE, false);
            case 1:
                return IafBlockRegistry.DRAGONFORGE_ICE_INPUT.getDefaultState().with(BlockDragonforgeInput.ACTIVE, false);
            case 2:
                return IafBlockRegistry.DRAGONFORGE_LIGHTNING_INPUT.getDefaultState().with(BlockDragonforgeInput.ACTIVE, false);

        }
        return IafBlockRegistry.DRAGONFORGE_FIRE_INPUT.getDefaultState().with(BlockDragonforgeInput.ACTIVE, false);
    }

    private int getDragonType() {
        if(world.getBlockState(pos).getBlock() == IafBlockRegistry.DRAGONFORGE_FIRE_INPUT){
            return 0;
        }
        if(world.getBlockState(pos).getBlock() == IafBlockRegistry.DRAGONFORGE_ICE_INPUT){
            return 1;
        }
        if(world.getBlockState(pos).getBlock() == IafBlockRegistry.DRAGONFORGE_LIGHTNING_INPUT){
            return 2;
        }
        return 0;
    }

    private boolean isActive() {
        return world.getBlockState(pos).getBlock() instanceof BlockDragonforgeInput && world.getBlockState(pos).get(BlockDragonforgeInput.ACTIVE);
    }

    private void setActive() {
        TileEntity tileentity = world.getTileEntity(pos);
        world.setBlockState(this.pos, getDeactivatedState().with(BlockDragonforgeInput.ACTIVE, true));
        if (tileentity != null) {
            tileentity.validate();
            world.setTileEntity(pos, tileentity);
        }
    }


    private TileEntityDragonforge getConnectedTileEntity() {
        for (Direction facing : HORIZONTALS) {
            if (world.getTileEntity(pos.offset(facing)) != null && world.getTileEntity(pos.offset(facing)) instanceof TileEntityDragonforge) {
                return (TileEntityDragonforge) world.getTileEntity(pos.offset(facing));
            }
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    @Override
    @javax.annotation.Nullable
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        if (core != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return core.getCapability(capability, facing);
        }
        return super.getCapability(capability, facing);
    }

}
