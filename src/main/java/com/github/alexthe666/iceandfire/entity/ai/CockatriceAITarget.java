package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;
import java.util.function.Predicate;

import com.github.alexthe666.iceandfire.entity.EntityCockatrice;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class CockatriceAITarget<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    private EntityCockatrice cockatrice;

    public CockatriceAITarget(EntityCockatrice entityIn, Class<T> classTarget, boolean checkSight, Predicate<LivingEntity> targetSelector) {
        super(entityIn, classTarget, 0, checkSight, false, targetSelector);
        this.cockatrice = entityIn;
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean shouldExecute() {
        if (this.goalOwner.getRNG().nextInt(20) != 0 || this.cockatrice.world.getDifficulty() == Difficulty.PEACEFUL) {
            return false;
        }
        if (super.shouldExecute() && nearestTarget != null && !nearestTarget.getClass().equals(this.cockatrice.getClass())) {
            if (nearestTarget instanceof PlayerEntity && !cockatrice.isOwner(nearestTarget)) {
                return !cockatrice.isTamed();
            } else {
                return !cockatrice.isOwner(nearestTarget) && cockatrice.canMove();
            }
        }
        return false;
    }
}