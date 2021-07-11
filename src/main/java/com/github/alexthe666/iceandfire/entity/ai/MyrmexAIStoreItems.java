package com.github.alexthe666.iceandfire.entity.ai;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.github.alexthe666.iceandfire.block.BlockMyrmexCocoon;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityMyrmexCocoon;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.AdvancedPathNavigate;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.PathResult;
import com.github.alexthe666.iceandfire.world.gen.WorldGenMyrmexHive;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class MyrmexAIStoreItems extends Goal {
    private final EntityMyrmexBase myrmex;
    private final double movementSpeed;
    private BlockPos nextRoom = null;
    private BlockPos nextCocoon = null;
    private BlockPos mainRoom = null;
    private boolean first = true; //first stage - enter the main hive room then storage room
    private PathResult path;
    public MyrmexAIStoreItems(EntityMyrmexBase entityIn, double movementSpeedIn) {
        this.myrmex = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        if (!this.myrmex.canMove() || this.myrmex instanceof EntityMyrmexWorker && ((EntityMyrmexWorker) this.myrmex).holdingBaby() || !this.myrmex.shouldEnterHive() && !this.myrmex.getNavigator().noPath()  || this.myrmex.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
            return false;
        }
        if (!(this.myrmex.getNavigator() instanceof AdvancedPathNavigate) || this.myrmex.isPassenger()){
            return false;
        }
        if (this.myrmex.getWaitTicks()>0){
            return false;
        }
        MyrmexHive village = this.myrmex.getHive();
        if (village == null) {
            return false;
        }
        if(!this.myrmex.isInHive()){
            if(!this.myrmex.isCloseEnoughToTarget(MyrmexHive.getGroundedPos(this.myrmex.world, village.getClosestEntranceToEntity(this.myrmex, this.myrmex.getRNG(), false)),100)) {
                return false;
            }
        }
        first = true;
        mainRoom = MyrmexHive.getGroundedPos(this.myrmex.world, village.getCenter());

        nextRoom = MyrmexHive.getGroundedPos(this.myrmex.world, village.getRandomRoom(WorldGenMyrmexHive.RoomType.FOOD, this.myrmex.getRNG(), this.myrmex.getPosition()));
        nextCocoon = getNearbyCocoon(nextRoom);
        if(nextCocoon == null){
            this.myrmex.setWaitTicks(20 + new Random().nextInt(40));
        }
        this.path = ((AdvancedPathNavigate) this.myrmex.getNavigator()).moveToXYZ(mainRoom.getX() + 0.5D, mainRoom.getY() + 0.5D, mainRoom.getZ() + 0.5D, this.movementSpeed);
        return nextCocoon != null;

    }

    public boolean shouldContinueExecuting() {
        return !this.myrmex.getHeldItem(Hand.MAIN_HAND).isEmpty() && nextCocoon != null && isUseableCocoon(nextCocoon) && !this.myrmex.isCloseEnoughToTarget(nextCocoon,3) && this.myrmex.shouldEnterHive();
    }

    @Override
    public void tick() {
        if (first && mainRoom != null) {
            if (this.myrmex.isCloseEnoughToTarget(mainRoom,10)) {
                first = false;
            }
            else if (!this.myrmex.pathReachesTarget(path,mainRoom, 9)) {
                //Simple way to stop executing this task
                nextCocoon = null;
            }
        }

        if (!first && nextCocoon != null) {
            double dist = 3 * 3;
            if (this.myrmex.isCloseEnoughToTarget(nextCocoon,dist) && !this.myrmex.getHeldItem(Hand.MAIN_HAND).isEmpty() && isUseableCocoon(nextCocoon)) {
                TileEntityMyrmexCocoon cocoon = (TileEntityMyrmexCocoon) this.myrmex.world.getTileEntity(nextCocoon);
                ItemStack itemstack = this.myrmex.getHeldItem(Hand.MAIN_HAND);
                if (!itemstack.isEmpty()) {
                    for (int i = 0; i < cocoon.getSizeInventory(); ++i) {
                        if (!itemstack.isEmpty()) {
                            ItemStack cocoonStack = cocoon.getStackInSlot(i);
                            if (cocoonStack.isEmpty()) {
                                cocoon.setInventorySlotContents(i, itemstack.copy());
                                cocoon.markDirty();

                                this.myrmex.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                                this.myrmex.isEnteringHive = false;
                                return;
                            }else if (cocoonStack.getItem() == itemstack.getItem()) {
                                int j = Math.min(cocoon.getInventoryStackLimit(), cocoonStack.getMaxStackSize());
                                int k = Math.min(itemstack.getCount(), j - cocoonStack.getCount());
                                if (k > 0) {
                                    cocoonStack.grow(k);
                                    itemstack.shrink(k);

                                    if (itemstack.isEmpty()) {
                                        cocoon.markDirty();
                                    }
                                    this.myrmex.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                                    this.myrmex.isEnteringHive = false;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            //In case the myrmex isn't close enough to the cocoon and walked to it's destination try a different one
            else if(!this.myrmex.getHeldItem(Hand.MAIN_HAND).isEmpty() && !this.myrmex.pathReachesTarget(path,nextCocoon,dist)){
                nextCocoon = getNearbyCocoon(nextRoom);
                if (nextCocoon !=null) {
                    this.path = ((AdvancedPathNavigate) this.myrmex.getNavigator()).moveToXYZ(nextCocoon.getX() + 0.5D, nextCocoon.getY() + 0.5D, nextCocoon.getZ() + 0.5D, this.movementSpeed);
                }
            }
            else if(this.myrmex.pathReachesTarget(path,nextCocoon,dist) && this.path.isCancelled()){
                resetTask();
            }
        }
    }

    public void resetTask() {
        nextRoom = null;
        nextCocoon = null;
        mainRoom = null;
        first = true;
    }

    public BlockPos getNearbyCocoon(BlockPos roomCenter) {
        int RADIUS_XZ = 15;
        int RADIUS_Y = 7;
        List<BlockPos> closeCocoons = new ArrayList<BlockPos>();
        BlockPos.getAllInBox(roomCenter.add(-RADIUS_XZ, -RADIUS_Y, -RADIUS_XZ), roomCenter.add(RADIUS_XZ, RADIUS_Y, RADIUS_XZ)).forEach(blockpos -> {
            TileEntity te = this.myrmex.world.getTileEntity(blockpos);
            if (te != null && te instanceof TileEntityMyrmexCocoon) {
                if (!((TileEntityMyrmexCocoon) te).isFull(this.myrmex.getHeldItem(Hand.MAIN_HAND))) {
                    closeCocoons.add(te.getPos());
                }
            }
        });
        if (closeCocoons.isEmpty()) {
            return null;
        }
        return closeCocoons.get(myrmex.getRNG().nextInt(Math.max(closeCocoons.size() - 1, 1)));
    }

    public boolean isUseableCocoon(BlockPos blockpos) {
        if (this.myrmex.world.getBlockState(blockpos).getBlock() instanceof BlockMyrmexCocoon && this.myrmex.world.getTileEntity(blockpos) != null && this.myrmex.world.getTileEntity(blockpos) instanceof TileEntityMyrmexCocoon) {
            return !((TileEntityMyrmexCocoon) this.myrmex.world.getTileEntity(blockpos)).isFull(this.myrmex.getHeldItem(Hand.MAIN_HAND));
        }
        return false;
    }
}