package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.EntityPixie;
import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class PixieAIFlee<T extends Entity> extends Goal {
    private final Predicate<Entity> canBeSeenSelector;
    private final float avoidDistance;
    private final Predicate<? super T> avoidTargetSelector;
    private final Class<T> classToAvoid;
    protected EntityPixie pixie;
    protected T closestLivingEntity;
    private Vector3d hidePlace;

    public PixieAIFlee(EntityPixie pixie, Class<T> classToAvoidIn, float avoidDistanceIn, Predicate<? super T> avoidTargetSelectorIn) {
        this.pixie = pixie;
        this.classToAvoid = classToAvoidIn;
        this.canBeSeenSelector = new Predicate<Entity>() {
            public boolean apply(@Nullable Entity entity) {
                return entity.isAlive() && PixieAIFlee.this.pixie.getEntitySenses().canSee(entity) && !PixieAIFlee.this.pixie.isOnSameTeam(entity);
            }
        };
        this.avoidTargetSelector = avoidTargetSelectorIn;
        this.avoidDistance = avoidDistanceIn;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }


    public boolean shouldExecute() {
        if (this.pixie.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
            return false;
        }
        if (this.pixie.isTamed()) {
            return false;
        }
        List<T> list = this.pixie.world.getEntitiesWithinAABB(this.classToAvoid, this.pixie.getBoundingBox().grow(this.avoidDistance, 3.0D, this.avoidDistance),
                EntityPredicates.NOT_SPECTATING);

        if (list.isEmpty()) {
            return false;
        } else {
            this.closestLivingEntity = list.get(0);
            if (closestLivingEntity != null) {
                Vector3d Vector3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.pixie, 16, 4, new Vector3d(this.closestLivingEntity.getPosX(), this.closestLivingEntity.getPosY(), this.closestLivingEntity.getPosZ()));

                if (Vector3d == null) {
                    return false;
                } else {
                    Vector3d = Vector3d.add(0, 1, 0);
                    this.pixie.getMoveHelper().setMoveTo(Vector3d.x, Vector3d.y, Vector3d.z, calculateRunSpeed());
                    this.pixie.getLookController().setLookPosition(Vector3d.x, Vector3d.y, Vector3d.z, 180.0F, 20.0F);
                    hidePlace = Vector3d;
                    pixie.slowSpeed = true;
                    return true;
                }
            }
            return false;
        }
    }

    private double calculateRunSpeed() {
        if(pixie.ticksHeldItemFor > 6000){
            return 0.1D;
        }
        if(pixie.ticksHeldItemFor > 1200){
            return 0.25D;
        }
        if(pixie.ticksHeldItemFor > 600){
            return 0.25D;
        }
        return 1D;
    }

    public boolean shouldContinueExecuting() {
        return hidePlace != null && this.pixie.getDistanceSq(hidePlace.add(0.5, 0.5, 0.5)) < 2;
    }

    public void startExecuting() {
        this.pixie.getMoveHelper().setMoveTo(hidePlace.x, hidePlace.y, hidePlace.z, calculateRunSpeed());
        this.pixie.getLookController().setLookPosition(hidePlace.x, hidePlace.y, hidePlace.z, 180.0F, 20.0F);
    }

    public void resetTask() {
        this.closestLivingEntity = null;
        pixie.slowSpeed = false;
    }
}