package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.google.common.base.Predicate;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class DeathWormAITarget<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    private EntityDeathWorm deathworm;

    public DeathWormAITarget(EntityDeathWorm entityIn, Class<T> classTarget, boolean checkSight, Predicate<LivingEntity> targetPredicate) {
        super(entityIn, classTarget, 20, checkSight, false, targetPredicate);
        this.deathworm = entityIn;
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean shouldExecute() {
        if (super.shouldExecute() && nearestTarget != null && !nearestTarget.getClass().equals(this.deathworm.getClass())) {
            if (nearestTarget instanceof PlayerEntity && !deathworm.isOwner(nearestTarget)) {
                return !deathworm.isTamed();
            } else {
                if (!deathworm.isOwner(nearestTarget)) {
                    return true;
                }
                if (nearestTarget instanceof MonsterEntity && deathworm.getWormAge() > 2) {
                    if (nearestTarget instanceof CreatureEntity) {
                        return deathworm.getWormAge() > 3;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    protected AxisAlignedBB getTargetableArea(double targetDistance) {
        return this.deathworm.getBoundingBox().grow(targetDistance, targetDistance, targetDistance);
    }
}