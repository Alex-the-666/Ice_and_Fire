package com.github.alexthe666.iceandfire.entity.ai;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;

public class HippogryphAITarget<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    private EntityHippogryph hippogryph;

    public HippogryphAITarget(EntityHippogryph entityIn, Class<T> classTarget, boolean checkSight, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(entityIn, classTarget, 20, checkSight, false, targetPredicate);
        this.hippogryph = entityIn;
    }

    public HippogryphAITarget(EntityHippogryph entityIn, Class<T> classTarget, int i, boolean checkSight, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(entityIn, classTarget, i, checkSight, false, targetPredicate);
        this.hippogryph = entityIn;
    }


    @Override
    public boolean shouldExecute() {
        if (this.goalOwner.getRNG().nextInt(20) != 0) {
            return false;
        }
        if (super.shouldExecute() && nearestTarget != null && !nearestTarget.getClass().equals(this.hippogryph.getClass())) {
            if (this.hippogryph.getWidth() >= nearestTarget.getWidth()) {
                if (nearestTarget instanceof PlayerEntity) {
                    return !hippogryph.isTamed();
                } else {
                    if (!hippogryph.isOwner(nearestTarget) && hippogryph.canMove() && nearestTarget instanceof AnimalEntity) {
                        if (hippogryph.isTamed()) {
                            return DragonUtils.canTameDragonAttack(hippogryph, nearestTarget);
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}