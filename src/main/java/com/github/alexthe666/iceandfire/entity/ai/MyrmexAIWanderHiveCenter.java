package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.world.MyrmexWorldData;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class MyrmexAIWanderHiveCenter extends Goal {
    private final EntityMyrmexBase myrmex;
    private final double movementSpeed;
    private Path path;
    private BlockPos target = BlockPos.ZERO;

    public MyrmexAIWanderHiveCenter(EntityMyrmexBase entityIn, double movementSpeedIn) {
        this.myrmex = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        if (!this.myrmex.canMove() || !this.myrmex.shouldEnterHive() && !this.myrmex.getNavigator().noPath() || this.myrmex.canSeeSky()) {
            return false;
        }
        MyrmexHive village = MyrmexWorldData.get(this.myrmex.world).getNearestHive(this.myrmex.getPosition(), 300);
        if (village == null) {
            village = this.myrmex.getHive();
        }
        if (village == null) {
            return false;
        } else {
            target = getNearPos(MyrmexHive.getGroundedPos(this.myrmex.world, village.getCenter()));

            this.path = this.myrmex.getNavigator().getPathToPos(target, 0);
            return this.path != null;
        }
    }

    public boolean shouldContinueExecuting() {
        return !this.myrmex.getNavigator().noPath() && this.myrmex.getDistanceSq(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D) > 3 && this.myrmex.shouldEnterHive();
    }

    public void startExecuting() {
        this.myrmex.getNavigator().setPath(this.path, this.movementSpeed);
    }

    public void resetTask() {
        target = BlockPos.ZERO;
        this.myrmex.getNavigator().setPath(null, this.movementSpeed);

    }

    public BlockPos getNearPos(BlockPos pos) {
        return pos.add(this.myrmex.getRNG().nextInt(8) - 4, 0, this.myrmex.getRNG().nextInt(8) - 4);
    }
}