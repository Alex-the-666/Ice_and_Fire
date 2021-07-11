package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.AdvancedPathNavigate;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.PathResult;
import com.github.alexthe666.iceandfire.world.MyrmexWorldData;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class MyrmexAIMoveThroughHive extends Goal {
    private final EntityMyrmexBase myrmex;
    private final double movementSpeed;
    private BlockPos nextRoom = BlockPos.ZERO;
    private PathResult path;

    public MyrmexAIMoveThroughHive(EntityMyrmexBase entityIn, double movementSpeedIn) {
        this.myrmex = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        if (!this.myrmex.canMove() || this.myrmex instanceof EntityMyrmexWorker && ((EntityMyrmexWorker) this.myrmex).holdingSomething() || !this.myrmex.shouldMoveThroughHive() || !this.myrmex.shouldEnterHive() && !this.myrmex.getNavigator().noPath() || this.myrmex.canSeeSky()) {
            return false;
        }
        MyrmexHive village = MyrmexWorldData.get(this.myrmex.world).getNearestHive(this.myrmex.getPosition(), 300);
        if (village == null) {
            village = this.myrmex.getHive();
        }
        if (!(this.myrmex.getNavigator() instanceof AdvancedPathNavigate) ||this.myrmex.isPassenger()){
            return false;
        }
        if (village == null) {
            return false;
        } else {
            nextRoom = MyrmexHive.getGroundedPos(this.myrmex.world, village.getRandomRoom(this.myrmex.getRNG(), this.myrmex.getPosition()));
            this.path = ((AdvancedPathNavigate)this.myrmex.getNavigator()).moveToXYZ(nextRoom.getX(), nextRoom.getY(),  nextRoom.getZ(), movementSpeed);
            return this.path != null;
        }
    }

    public boolean shouldContinueExecuting() {
        return  !myrmex.shouldLeaveHive() && !this.myrmex.isCloseEnoughToTarget(nextRoom,3) && this.myrmex.shouldEnterHive() && !(this.myrmex instanceof EntityMyrmexWorker && ((EntityMyrmexWorker) this.myrmex).holdingBaby());
    }

    public void startExecuting() {
    }

    public void resetTask() {
        nextRoom = BlockPos.ZERO;

    }
}