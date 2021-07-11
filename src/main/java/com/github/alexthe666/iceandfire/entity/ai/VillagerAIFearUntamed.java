package com.github.alexthe666.iceandfire.entity.ai;

import java.util.function.Predicate;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;

public class VillagerAIFearUntamed extends AvoidEntityGoal<LivingEntity> {

    public VillagerAIFearUntamed(CreatureEntity entityIn, Class<LivingEntity> avoidClass, float distance, double nearSpeedIn, double farSpeedIn, Predicate<LivingEntity> targetPredicate) {
        super(entityIn, avoidClass, distance, nearSpeedIn, farSpeedIn, targetPredicate);
    }
}
