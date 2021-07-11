package com.github.alexthe666.iceandfire.entity.ai;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.EntitySeaSerpent;
import com.google.common.base.Predicate;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.util.math.AxisAlignedBB;

public class FlyingAITarget extends NearestAttackableTargetGoal {

    public FlyingAITarget(MobEntity creature, Class classTarget, boolean checkSight) {
        super(creature, classTarget, checkSight);
    }

    public FlyingAITarget(MobEntity creature, Class classTarget, boolean checkSight, boolean onlyNearby) {
        super(creature, classTarget, checkSight, onlyNearby);
    }

    public FlyingAITarget(MobEntity creature, Class classTarget, int chance, boolean checkSight, boolean onlyNearby, @Nullable final Predicate targetSelector) {
        super(creature, classTarget, chance, checkSight, onlyNearby, targetSelector);
    }

    @Override
    protected AxisAlignedBB getTargetableArea(double targetDistance) {
        return this.goalOwner.getBoundingBox().grow(targetDistance, targetDistance, targetDistance);
    }

    public boolean shouldExecute() {
        if (goalOwner instanceof EntitySeaSerpent && (((EntitySeaSerpent) goalOwner).isJumpingOutOfWater() || !goalOwner.isInWater())) {
            return false;
        }
        return super.shouldExecute();
    }

}
