package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexQueen;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.AdvancedPathNavigate;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.PathResult;
import com.github.alexthe666.iceandfire.world.MyrmexWorldData;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class MyrmexAILeaveHive extends Goal {
    private final EntityMyrmexBase myrmex;
    private final double movementSpeed;
    private PathResult path;
    private BlockPos nextEntrance = BlockPos.ZERO;
    private BlockPos inProgPos = BlockPos.ZERO;

    public MyrmexAILeaveHive(EntityMyrmexBase entityIn, double movementSpeedIn) {
        this.myrmex = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        if (this.myrmex instanceof EntityMyrmexQueen) {
            return false;
        }
        //If it's riding something don't execute
        if (!(this.myrmex.getNavigator() instanceof AdvancedPathNavigate) ||this.myrmex.isPassenger()){
            return false;
        }
        if (this.myrmex.isChild()) {
            return false;
        }
        if (!this.myrmex.canMove() || !this.myrmex.shouldLeaveHive() || this.myrmex.shouldEnterHive() || !this.myrmex.isInHive() || this.myrmex instanceof EntityMyrmexWorker && (((EntityMyrmexWorker) this.myrmex).holdingSomething() || !this.myrmex.getHeldItem(Hand.MAIN_HAND).isEmpty()) || this.myrmex.isEnteringHive) {
            return false;
        }
        MyrmexHive village = MyrmexWorldData.get(this.myrmex.world).getNearestHive(this.myrmex.getPosition(), 1000);
        if (village == null) {
            return false;
        } else {
            nextEntrance = MyrmexHive.getGroundedPos(this.myrmex.world, village.getClosestEntranceToEntity(this.myrmex, this.myrmex.getRNG(), true));
            this.path = ((AdvancedPathNavigate) this.myrmex.getNavigator()).moveToXYZ(nextEntrance.getX(), nextEntrance.getY(), nextEntrance.getZ(), movementSpeed);
            inProgPos = new BlockPos(this.myrmex.getPosition());
            return true;
        }
    }

    public boolean shouldContinueExecuting() {
        if (this.myrmex.isCloseEnoughToTarget(nextEntrance,12)) {
            return false;
        }
        if(this.myrmex.shouldEnterHive()){
            return false;
        }

        return this.myrmex.shouldLeaveHive();
    }

    public void tick() {
        //If the path has been created but the destination couldn't be reached
        //or if the myrmex has reached the end of the path but isn't close enough to the entrance for some reason
        if(!this.myrmex.pathReachesTarget(path,nextEntrance,12)){
            MyrmexHive village = MyrmexWorldData.get(this.myrmex.world).getNearestHive(this.myrmex.getPosition(), 1000);
            nextEntrance = MyrmexHive.getGroundedPos(this.myrmex.world, village.getClosestEntranceToEntity(this.myrmex, this.myrmex.getRNG(), true));
            path = ((AdvancedPathNavigate)this.myrmex.getNavigator()).moveToXYZ(nextEntrance.getX(), nextEntrance.getY() + 1,  nextEntrance.getZ(), movementSpeed);
        }
    }


    public void startExecuting() {
    }

    public void resetTask() {
        nextEntrance = BlockPos.ZERO;
        this.myrmex.getNavigator().clearPath();
    }
}