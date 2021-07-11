package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityPixie;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;


import java.util.EnumSet;
import java.util.Random;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class PixieAIMoveRandom extends Goal {
    BlockPos target;
    EntityPixie pixie;
    Random random;

    public PixieAIMoveRandom(EntityPixie entityPixieIn) {
        this.pixie = entityPixieIn;
        this.random = entityPixieIn.getRNG();
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        target = EntityPixie.getPositionRelativetoGround(this.pixie, this.pixie.world, this.pixie.getPosX() + this.random.nextInt(15) - 7, this.pixie.getPosZ() + this.random.nextInt(15) - 7, this.random);
        return !this.pixie.isOwnerClose() && !this.pixie.isPixieSitting() && isDirectPathBetweenPoints(this.pixie.getPosition(), target) && !this.pixie.getMoveHelper().isUpdating() && this.random.nextInt(4) == 0 && this.pixie.getHousePos() == null;
    }

    protected boolean isDirectPathBetweenPoints(BlockPos posVec31, BlockPos posVec32) {
        return this.pixie.world.rayTraceBlocks(new RayTraceContext(new Vector3d(posVec31.getX() + 0.5D, posVec31.getY() + 0.5D, posVec31.getZ() + 0.5D), new Vector3d(posVec32.getX() + 0.5D, posVec32.getY() + (double) this.pixie.getHeight() * 0.5D, posVec32.getZ() + 0.5D), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this.pixie)).getType() == RayTraceResult.Type.MISS;
    }

    public boolean shouldContinueExecuting() {
        return false;
    }

    public void tick() {
        if (!isDirectPathBetweenPoints(this.pixie.getPosition(), target)) {
            target = EntityPixie.getPositionRelativetoGround(this.pixie, this.pixie.world, this.pixie.getPosX() + this.random.nextInt(15) - 7, this.pixie.getPosZ() + this.random.nextInt(15) - 7, this.random);
        }
        if (this.pixie.world.isAirBlock(target)) {

            this.pixie.getMoveHelper().setMoveTo((double) target.getX() + 0.5D, (double) target.getY() + 0.5D, (double) target.getZ() + 0.5D, 0.25D);
            if (this.pixie.getAttackTarget() == null) {
                this.pixie.getLookController().setLookPosition((double) target.getX() + 0.5D, (double) target.getY() + 0.5D, (double) target.getZ() + 0.5D, 180.0F, 20.0F);
            }
        }
    }
}
