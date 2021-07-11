package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.AdvancedPathNavigate;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.PathResult;
import com.github.alexthe666.iceandfire.world.MyrmexWorldData;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class MyrmexAIReEnterHive extends Goal {
    private final EntityMyrmexBase myrmex;
    private final double movementSpeed;
    private PathResult path;
    private BlockPos nextEntrance = BlockPos.ZERO;
    private boolean first = true;
    private MyrmexHive hive;

    public MyrmexAIReEnterHive(EntityMyrmexBase entityIn, double movementSpeedIn) {
        this.myrmex = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        if (!this.myrmex.canMove() || this.myrmex.shouldLeaveHive() || !this.myrmex.shouldEnterHive() || !first) {
            return false;
        }
        MyrmexHive village = this.myrmex.getHive();
        if (village == null) {
            village = MyrmexWorldData.get(this.myrmex.world).getNearestHive(this.myrmex.getPosition(), 500);
        }
        if (!(this.myrmex.getNavigator() instanceof AdvancedPathNavigate) ||this.myrmex.isPassenger()){
            return false;
        }
        if (village == null) {
            return false;
        } else {
            this.hive = village;
            nextEntrance = MyrmexHive.getGroundedPos(this.myrmex.world, hive.getClosestEntranceToEntity(this.myrmex, this.myrmex.getRNG(), false));
            this.path = ((AdvancedPathNavigate)this.myrmex.getNavigator()).moveToXYZ(nextEntrance.getX(), nextEntrance.getY(),  nextEntrance.getZ(), 1);
            first = true;
            return this.path != null;
        }
    }

    public void tick() {
        //Fallback for if for some reason the myrmex can't reach the entrance try a different one (random)
        if (first && !this.myrmex.pathReachesTarget(path,nextEntrance,12)) {
            nextEntrance = MyrmexHive.getGroundedPos(this.myrmex.world, hive.getClosestEntranceToEntity(this.myrmex, this.myrmex.getRNG(), true));
            this.path = ((AdvancedPathNavigate) this.myrmex.getNavigator()).moveToXYZ(nextEntrance.getX(), nextEntrance.getY(), nextEntrance.getZ(), movementSpeed);
        }
        if (first && this.myrmex.isCloseEnoughToTarget(nextEntrance,12)) {
            if (hive != null) {
                nextEntrance = hive.getClosestEntranceBottomToEntity(this.myrmex, this.myrmex.getRNG());
                first = false;
                this.path = ((AdvancedPathNavigate)this.myrmex.getNavigator()).moveToXYZ(nextEntrance.getX(), nextEntrance.getY(),  nextEntrance.getZ(), 1);
            }
        }
        this.myrmex.isEnteringHive = !this.myrmex.isCloseEnoughToTarget(nextEntrance,14) && !first;
    }

    public boolean shouldContinueExecuting() {
        if (this.myrmex.isCloseEnoughToTarget(nextEntrance,9) && !first) {
            return false;
        }
        return true;
    }

    public void resetTask() {
        nextEntrance = BlockPos.ZERO;
        first = true;
    }
}