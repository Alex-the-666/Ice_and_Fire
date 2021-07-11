package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityAmphithere;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class AmphithereAIFollowOwner extends Goal {
    private final EntityAmphithere ampithere;
    private final double followSpeed;
    World world;
    float maxDist;
    float minDist;
    private LivingEntity owner;
    private int timeToRecalcPath;
    private float oldWaterCost;

    public AmphithereAIFollowOwner(EntityAmphithere ampithereIn, double followSpeedIn, float minDistIn, float maxDistIn) {
        this.ampithere = ampithereIn;
        this.world = ampithereIn.world;
        this.followSpeed = followSpeedIn;
        this.minDist = minDistIn;
        this.maxDist = maxDistIn;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        LivingEntity LivingEntity = this.ampithere.getOwner();
        if (ampithere.getCommand() != 2) {
            return false;
        }
        if (LivingEntity == null) {
            return false;
        } else if (LivingEntity instanceof PlayerEntity && LivingEntity.isSpectator()) {
            return false;
        } else if (this.ampithere.isQueuedToSit()) {
            return false;
        } else if (this.ampithere.getDistanceSq(LivingEntity) < (double) (this.minDist * this.minDist)) {
            return false;
        } else {
            this.owner = LivingEntity;
            return true;
        }
    }

    public boolean shouldContinueExecuting() {
        return !noPath() && this.ampithere.getDistanceSq(this.owner) > (double) (this.maxDist * this.maxDist) && !this.ampithere.isQueuedToSit();
    }

    private boolean noPath() {
        if (!ampithere.isFlying()) {
            return this.ampithere.getNavigator().noPath();
        } else {
            return false;
        }
    }

    public void startExecuting() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.ampithere.getPathPriority(PathNodeType.WATER);
        this.ampithere.setPathPriority(PathNodeType.WATER, 0.0F);
    }

    public void resetTask() {
        this.owner = null;
        this.ampithere.getNavigator().clearPath();
        this.ampithere.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
    }

    public void tick() {
        this.ampithere.getLookController().setLookPositionWithEntity(this.owner, 10.0F, (float) this.ampithere.getVerticalFaceSpeed());

        if (!this.ampithere.isQueuedToSit()) {
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                tryMoveTo();
                if (!this.ampithere.getLeashed() && !this.ampithere.isPassenger()) {
                    if (this.ampithere.getDistanceSq(this.owner) >= 144.0D) {
                        int i = MathHelper.floor(this.owner.getPosX()) - 2;
                        int j = MathHelper.floor(this.owner.getPosZ()) - 2;
                        int k = MathHelper.floor(this.owner.getBoundingBox().minY);

                        for (int l = 0; l <= 4; ++l) {
                            for (int i1 = 0; i1 <= 4; ++i1) {
                                if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.canTeleportToBlock(new BlockPos(i, j, k))) {
                                    this.ampithere.setLocationAndAngles((float) (i + l) + 0.5F, k, (float) (j + i1) + 0.5F, this.ampithere.rotationYaw, this.ampithere.rotationPitch);
                                    ampithere.getNavigator().clearPath();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected boolean canTeleportToBlock(BlockPos pos) {
        BlockState blockstate = this.world.getBlockState(pos);
        return blockstate.canEntitySpawn(this.world, pos, this.ampithere.getType()) && this.world.isAirBlock(pos.up()) && this.world.isAirBlock(pos.up(2));
    }

    private boolean tryMoveTo() {
        if (!ampithere.isFlying()) {
            return ampithere.getNavigator().tryMoveToEntityLiving(this.owner, this.followSpeed);
        } else {
            this.ampithere.getMoveHelper().setMoveTo(this.owner.getPosX(), this.owner.getPosY() + this.owner.getEyeHeight() + 5 + this.ampithere.getRNG().nextInt(8), this.owner.getPosZ(), 0.25D);
            return true;
        }
    }
}