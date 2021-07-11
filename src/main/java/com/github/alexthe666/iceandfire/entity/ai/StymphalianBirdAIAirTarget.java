package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.entity.EntityStymphalianBird;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;

import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class StymphalianBirdAIAirTarget extends Goal {
    private EntityStymphalianBird bird;
    private World theWorld;

    public StymphalianBirdAIAirTarget(EntityStymphalianBird bird) {
        this.bird = bird;
        this.theWorld = bird.world;
    }

    public static BlockPos getNearbyAirTarget(EntityStymphalianBird bird) {
        if (bird.getAttackTarget() == null) {
            BlockPos pos = DragonUtils.getBlockInViewStymphalian(bird);
            if (pos != null && bird.world.getBlockState(pos).getMaterial() == Material.AIR) {
                return pos;
            }
            if (bird.flock != null && bird.flock.isLeader(bird)) {
                bird.flock.setTarget(bird.airTarget);
            }
        } else {
            return new BlockPos((int) bird.getAttackTarget().getPosX(), (int) bird.getAttackTarget().getPosY() + bird.getAttackTarget().getEyeHeight(), (int) bird.getAttackTarget().getPosZ());
        }
        return bird.getPosition();
    }

    public boolean shouldExecute() {
        if (bird != null) {
            if (!bird.isFlying()) {
                return false;
            }
            if (bird.isChild()) {
                return false;
            }
            if (bird.doesWantToLand()) {
                return false;
            }
            if (bird.airTarget != null && (bird.isTargetBlocked(Vector3d.copyCentered(bird.airTarget)))) {
                bird.airTarget = null;
            }

            if (bird.airTarget != null) {
                return false;
            } else {
                Vector3d vec = this.findAirTarget();

                if (vec == null) {
                    return false;
                } else {
                    bird.airTarget = new BlockPos(vec.x, vec.y, vec.z);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean shouldContinueExecuting() {
        if (!bird.isFlying()) {
            return false;
        }
        if (bird.isChild()) {
            return false;
        }
        return bird.airTarget != null;
    }

    public Vector3d findAirTarget() {
        return Vector3d.copyCentered(getNearbyAirTarget(bird));
    }
}