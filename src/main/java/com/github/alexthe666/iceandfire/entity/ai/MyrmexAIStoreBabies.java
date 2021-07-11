package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.world.gen.WorldGenMyrmexHive;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class MyrmexAIStoreBabies extends Goal {
    private final EntityMyrmexWorker myrmex;
    private final double movementSpeed;
    private Path path;
    private BlockPos nextRoom = BlockPos.ZERO;

    public MyrmexAIStoreBabies(EntityMyrmexWorker entityIn, double movementSpeedIn) {
        this.myrmex = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        if (!this.myrmex.canMove() || !this.myrmex.holdingBaby() || !this.myrmex.shouldEnterHive() && !this.myrmex.getNavigator().noPath() || this.myrmex.canSeeSky()) {
            return false;
        }
        MyrmexHive village = this.myrmex.getHive();
        if (village == null) {
            return false;
        } else {
            nextRoom = MyrmexHive.getGroundedPos(this.myrmex.world, village.getRandomRoom(WorldGenMyrmexHive.RoomType.NURSERY, this.myrmex.getRNG(), this.myrmex.getPosition())).up();
            return true;
        }
    }

    public boolean shouldContinueExecuting() {
        return this.myrmex.holdingBaby() && !this.myrmex.getNavigator().noPath() && this.myrmex.getDistanceSq(nextRoom.getX() + 0.5D, nextRoom.getY() + 0.5D, nextRoom.getZ() + 0.5D) > 3 && this.myrmex.shouldEnterHive();
    }

    public void startExecuting() {
        this.myrmex.getNavigator().tryMoveToXYZ(this.nextRoom.getX(), this.nextRoom.getY(), this.nextRoom.getZ(), 1.5F);
    }

    @Override
    public void tick() {
        if (nextRoom != null && this.myrmex.getDistanceSq(nextRoom.getX() + 0.5D, nextRoom.getY() + 0.5D, nextRoom.getZ() + 0.5D) < 4 && this.myrmex.holdingBaby()) {
            if (!this.myrmex.getPassengers().isEmpty()) {
                for (Entity entity : this.myrmex.getPassengers()) {
                    entity.stopRiding();
                    resetTask();
                    entity.copyLocationAndAnglesFrom(this.myrmex);
                }
            }
        }
    }

    public void resetTask() {
        nextRoom = BlockPos.ZERO;
        this.myrmex.getNavigator().clearPath();
    }

}