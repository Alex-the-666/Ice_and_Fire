package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.util.IGroundMount;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class EntityGroundAIRide<T extends MobEntity & IGroundMount> extends Goal {

    private T dragon;
    private PlayerEntity player;

    public EntityGroundAIRide(T dragon) {
        this.dragon = dragon;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        player = dragon.getRidingPlayer();

        return player != null;
    }

    @Override
    public void startExecuting() {
        dragon.getNavigator().clearPath();
    }

    @Override
    public void tick() {
        dragon.getNavigator().clearPath();
        dragon.setAttackTarget(null);
        double x = dragon.getPosX();
        double y = dragon.getPosY();
        double z = dragon.getPosZ();
        double speed = 1.8F * dragon.getRideSpeedModifier();
        if (player.moveStrafing != 0 || player.moveForward != 0) {
            Vector3d lookVec = player.getLookVec();
            if (player.moveForward < 0) {
                lookVec = lookVec.rotateYaw((float) Math.PI);
            } else if (player.moveStrafing > 0) {
                lookVec = lookVec.rotateYaw((float) Math.PI * 0.5f);
            } else if (player.moveStrafing < 0) {
                lookVec = lookVec.rotateYaw((float) Math.PI * -0.5f);
            }
            if (Math.abs(player.moveStrafing) > 0.0) {
                speed *= 0.25D;
            }
            if (player.moveForward < 0.0) {
                speed *= 0.15D;
            }
            x += lookVec.x * 10;
            z += lookVec.z * 10;
        }
        dragon.getMoveHelper().setMoveTo(x, y, z, speed);
    }
}
