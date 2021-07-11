package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityPixie;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;
import java.util.Random;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class PixieAIEnterHouse extends Goal {

    EntityPixie pixie;
    Random random;

    public PixieAIEnterHouse(EntityPixie entityPixieIn) {
        this.pixie = entityPixieIn;
        this.random = entityPixieIn.getRNG();
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        if (this.pixie.isOwnerClose() || this.pixie.getMoveHelper().isUpdating() || this.pixie.isPixieSitting() || this.random.nextInt(20) != 0 || this.pixie.ticksUntilHouseAI != 0) {
            return false;
        }

        BlockPos blockpos1 = EntityPixie.findAHouse(this.pixie, this.pixie.world);
        return !blockpos1.toString().equals(this.pixie.getPosition().toString());
    }

    public boolean shouldContinueExecuting() {
        return false;
    }

    public void tick() {
        BlockPos blockpos = this.pixie.getHousePos();
        if (blockpos == null) {
            blockpos = this.pixie.getPosition();
        }

        for (int i = 0; i < 3; ++i) {
            BlockPos blockpos1 = EntityPixie.findAHouse(this.pixie, this.pixie.world);
            this.pixie.getMoveHelper().setMoveTo((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 0.25D);
            this.pixie.setHousePosition(blockpos1);
            if (this.pixie.getAttackTarget() == null) {
                this.pixie.getLookController().setLookPosition((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
            }
        }
    }
}
