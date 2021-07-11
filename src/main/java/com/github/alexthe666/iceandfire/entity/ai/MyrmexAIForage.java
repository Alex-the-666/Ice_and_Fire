package com.github.alexthe666.iceandfire.entity.ai;

import java.util.*;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.api.event.GenericGriefEvent;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker;

import com.github.alexthe666.iceandfire.pathfinding.raycoms.AdvancedPathNavigate;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.PathResult;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class MyrmexAIForage extends Goal {
    private static final int RADIUS = 16;
    private final EntityMyrmexWorker myrmex;
    private final BlockSorter targetSorter;
    private BlockPos targetBlock = null;
    private int wanderRadius;
    private int chance;
    private PathResult path;
    private int failedToFindPath = 0;
    public MyrmexAIForage(EntityMyrmexWorker myrmex,int chanceIn) {
        super();
        this.myrmex = myrmex;
        this.targetSorter = new BlockSorter();
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
        this.chance = chanceIn;
    }

    @Override
    public boolean shouldExecute() {
        if (!this.myrmex.canMove() || this.myrmex.holdingSomething() || !this.myrmex.getNavigator().noPath() || this.myrmex.isInHive() || this.myrmex.shouldEnterHive()) {
            return false;
        }
        if (!(this.myrmex.getNavigator() instanceof AdvancedPathNavigate) || this.myrmex.isPassenger()) {
            return false;
        }
        if (this.myrmex.getWaitTicks()>0){
            return false;
        }

        //Get nearby edible blocks
        List<BlockPos> edibleBlocks = getEdibleBlocks();
        //If there are no edible blocks nearby
        if (edibleBlocks.isEmpty()) {
            return myrmex.getRNG().nextInt(chance) == 0 && increaseRadiusAndWander();
        }
        //Set closest block as target
        edibleBlocks.sort(this.targetSorter);
        this.targetBlock = edibleBlocks.get(0);
        this.path = ((AdvancedPathNavigate) this.myrmex.getNavigator()).moveToXYZ(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ(), 1D);
        return targetBlock != null && myrmex.getRNG().nextInt(chance) == 0;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (this.targetBlock == null) {
            return false;
        }
        if (this.myrmex.getWaitTicks()>0){
            return false;
        }
        if (myrmex.shouldEnterHive()) {
            this.myrmex.keepSearching = false;
            return false;
        }
        return !this.myrmex.holdingSomething();
    }

    @Override
    public void tick() {
        //If we haven't found an edible block as a target
        if (targetBlock != null && this.myrmex.keepSearching) {
            //If the myrmex is close enough to the target or at the end of the path
            if (this.myrmex.isCloseEnoughToTarget(targetBlock,12)
                    || !this.myrmex.pathReachesTarget(path,targetBlock,12)) {
                failedToFindPath = 0;
                List<BlockPos> edibleBlocks = getEdibleBlocks();
                //If we found an edible block nearby
                if (!edibleBlocks.isEmpty()){
                    this.myrmex.keepSearching = false;
                    edibleBlocks.sort(this.targetSorter);
                    this.targetBlock = edibleBlocks.get(0);
                    this.path = ((AdvancedPathNavigate) this.myrmex.getNavigator()).moveToXYZ(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ(), 1D);
                }
                //If there are still no edible blocks nearby
                else {
                    increaseRadiusAndWander();
                }
            }
            //if we have found an edible block
        } else if(!this.myrmex.keepSearching){
            failedToFindPath = 0;
            BlockState block = this.myrmex.world.getBlockState(this.targetBlock);
            //Test if the block is edible
            if (EntityMyrmexBase.isEdibleBlock(block)) {
                double distance = this.getDistanceSq(this.targetBlock);
                if (distance < 6) {
                    //Routine to break block and add item to myrmex
                    List<ItemStack> drops = block.getBlock().getDrops(block, (ServerWorld) this.myrmex.world, this.targetBlock, this.myrmex.world.getTileEntity(targetBlock)); // use the old method until it gets removed, for backward compatibility
                    if (!drops.isEmpty()) {
                        this.myrmex.world.destroyBlock(this.targetBlock, false);
                        ItemStack heldStack = drops.get(0).copy();
                        heldStack.setCount(1);
                        drops.get(0).shrink(1);
                        this.myrmex.setHeldItem(Hand.MAIN_HAND, heldStack);
                        for (ItemStack stack : drops) {
                            ItemEntity itemEntity = new ItemEntity(this.myrmex.world, this.targetBlock.getX() + this.myrmex.getRNG().nextDouble(), this.targetBlock.getY() + this.myrmex.getRNG().nextDouble(), this.targetBlock.getZ() + this.myrmex.getRNG().nextDouble(), stack);
                            itemEntity.setDefaultPickupDelay();
                            if (!this.myrmex.world.isRemote) {
                                this.myrmex.world.addEntity(itemEntity);
                            }
                        }
                        this.targetBlock = null;
                        this.resetTask();
                        this.myrmex.keepSearching = false;
                        this.wanderRadius = RADIUS;
                        return;
                    }
                }
                //If the myrmex reached the end of the path but is still not close enough to the target
                else if (!this.myrmex.pathReachesTarget(path,targetBlock,12)){
                    List<BlockPos> edibleBlocks = getEdibleBlocks();
                    //If we found an edible block nearby
                    if (!edibleBlocks.isEmpty()){
                        this.myrmex.keepSearching = false;
                        //This time choose a different random edible block
                        this.targetBlock = edibleBlocks.get(this.myrmex.getRNG().nextInt(edibleBlocks.size()));
                        this.path = ((AdvancedPathNavigate) this.myrmex.getNavigator()).moveToXYZ(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ(), 1D);
                    }
                    else{
                        this.myrmex.keepSearching = true;
                    }
                }
            }
        }

    }

    public void resetTask() {
        this.targetBlock = null;
        this.myrmex.keepSearching = true;
    }

    private double getDistanceSq(BlockPos pos) {
        double deltaX = this.myrmex.getPosX() - (pos.getX() + 0.5);
        double deltaY = this.myrmex.getPosY() + this.myrmex.getEyeHeight() - (pos.getY() + 0.5);
        double deltaZ = this.myrmex.getPosZ() - (pos.getZ() + 0.5);
        return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
    }

    public class BlockSorter implements Comparator<BlockPos> {
        @Override
        public int compare(BlockPos pos1, BlockPos pos2) {
            double distance1 = MyrmexAIForage.this.getDistanceSq(pos1);
            double distance2 = MyrmexAIForage.this.getDistanceSq(pos2);
            return Double.compare(distance1, distance2);
        }
    }
    private List<BlockPos> getEdibleBlocks(){
        List<BlockPos> allBlocks = new ArrayList<BlockPos>();
        BlockPos.getAllInBox(this.myrmex.getPosition().add(-RADIUS, -RADIUS/2, -RADIUS), this.myrmex.getPosition().add(RADIUS, RADIUS/2, RADIUS)).map(BlockPos::toImmutable).forEach(pos -> {
            if (!MinecraftForge.EVENT_BUS.post(new GenericGriefEvent(this.myrmex, pos.getX(), pos.getY(), pos.getZ()))) {
                if (EntityMyrmexBase.isEdibleBlock(this.myrmex.world.getBlockState(pos))) {
                    allBlocks.add(pos);
                    this.myrmex.keepSearching = false;
                }
            }
        });
        return allBlocks;
    }
    private boolean increaseRadiusAndWander(){
        this.myrmex.keepSearching = true;
        if (myrmex.getHive() != null) {
            wanderRadius = myrmex.getHive().getWanderRadius();
            myrmex.getHive().setWanderRadius(wanderRadius*2);
        }
        //Increase wander radius
        wanderRadius *= 2;
        //this.myrmex.setWaitTicks(80+new Random().nextInt(40));
        //Set target as random position inside wanderRadius
        if (wanderRadius >= IafConfig.myrmexMaximumWanderRadius){
            wanderRadius = IafConfig.myrmexMaximumWanderRadius;
            this.myrmex.setWaitTicks(80+new Random().nextInt(40));
            //Keep track of how many times the myrmex has potentially not found a path to a target
            failedToFindPath++;
            if (failedToFindPath >= 10){
                this.myrmex.setWaitTicks(800+new Random().nextInt(40));
            }
        }
        Vector3d vec = RandomPositionGenerator.findRandomTarget(this.myrmex, wanderRadius, 7);
        if (vec != null) {
            this.targetBlock = new BlockPos(vec);
        }
        if (this.targetBlock != null){
            this.path = ((AdvancedPathNavigate) this.myrmex.getNavigator()).moveToXYZ(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ(), 1D);
            return true;
        }
        return false;
    }

}
