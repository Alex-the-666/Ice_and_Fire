package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;

import com.github.alexthe666.iceandfire.api.FoodUtils;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.google.common.base.Predicate;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class DragonAITarget<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    private EntityDragonBase dragon;

    public DragonAITarget(EntityDragonBase entityIn, Class<T> classTarget, boolean checkSight, Predicate<LivingEntity> targetSelector) {
        super(entityIn, classTarget, 3, checkSight, false, targetSelector);
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
        this.dragon = entityIn;
    }

    @Override
    public boolean shouldExecute() {
        if (dragon.getCommand() == 1 || dragon.getCommand() == 2 || dragon.isSleeping()) {
            return false;
        }
        if(!dragon.isTamed() && dragon.lookingForRoostAIFlag){
            return false;
        }
        if (nearestTarget != null && !nearestTarget.getClass().equals(this.dragon.getClass())) {
            float dragonSize = Math.max(this.dragon.getWidth(), this.dragon.getWidth() * dragon.getRenderSize());
            if (dragonSize >= nearestTarget.getWidth() && super.shouldExecute()) {
                if (nearestTarget instanceof PlayerEntity && !dragon.isTamed()) {
                    return true;
                }
                if (nearestTarget instanceof EntityDragonBase) {
                    EntityDragonBase dragon = (EntityDragonBase) nearestTarget;
                    if (dragon.getOwner() != null && this.dragon.getOwner() != null && this.dragon.isOwner(dragon.getOwner())) {
                        return false;
                    }
                    return !dragon.isModelDead();
                }
                if (nearestTarget instanceof PlayerEntity && dragon.isTamed()) {
                    return false;
                } else {
                    if (!dragon.isOwner(nearestTarget) && FoodUtils.getFoodPoints(nearestTarget) > 0 && dragon.canMove() && (dragon.getHunger() < 90 || !dragon.isTamed() && nearestTarget instanceof PlayerEntity)) {
                        if (dragon.isTamed()) {
                            return DragonUtils.canTameDragonAttack(dragon, nearestTarget);
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    protected AxisAlignedBB getTargetableArea(double targetDistance) {
        return this.dragon.getBoundingBox().grow(targetDistance, targetDistance, targetDistance);
    }

    protected double getTargetDistance() {
        ModifiableAttributeInstance iattributeinstance = this.goalOwner.getAttribute(Attributes.FOLLOW_RANGE);
        return iattributeinstance == null ? 64.0D : iattributeinstance.getValue();
    }
}