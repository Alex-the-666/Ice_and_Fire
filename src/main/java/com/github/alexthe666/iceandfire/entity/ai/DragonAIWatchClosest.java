package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtGoal;

public class DragonAIWatchClosest extends LookAtGoal {

    public DragonAIWatchClosest(CreatureEntity LivingEntityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance) {
        super(LivingEntityIn, watchTargetClass, maxDistance);
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity instanceof EntityDragonBase && (!((EntityDragonBase) this.entity).canMove() || ((EntityDragonBase) this.entity).getAnimation() == EntityDragonBase.ANIMATION_SHAKEPREY)) {
            return false;
        }
        return super.shouldExecute();
    }
}
