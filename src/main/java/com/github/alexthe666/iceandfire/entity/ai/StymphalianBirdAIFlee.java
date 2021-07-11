package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.EntityStymphalianBird;
import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class StymphalianBirdAIFlee extends Goal {
    private final Predicate<Entity> canBeSeenSelector;
    private final float avoidDistance;
    protected EntityStymphalianBird stymphalianBird;
    protected LivingEntity closestLivingEntity;
    private Vector3d hidePlace;

    public StymphalianBirdAIFlee(EntityStymphalianBird stymphalianBird, float avoidDistanceIn) {
        this.stymphalianBird = stymphalianBird;
        this.canBeSeenSelector = new Predicate<Entity>() {
            public boolean apply(@Nullable Entity entity) {
                return entity instanceof PlayerEntity && entity.isAlive() && StymphalianBirdAIFlee.this.stymphalianBird.getEntitySenses().canSee(entity) && !StymphalianBirdAIFlee.this.stymphalianBird.isOnSameTeam(entity);
            }
        };
        this.avoidDistance = avoidDistanceIn;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }


    public boolean shouldExecute() {
        if (this.stymphalianBird.getVictor() == null) {
            return false;
        }
        List<LivingEntity> list = this.stymphalianBird.world.getEntitiesWithinAABB(LivingEntity.class, this.stymphalianBird.getBoundingBox().grow(this.avoidDistance, 3.0D, this.avoidDistance),
                this.canBeSeenSelector);

        if (list.isEmpty()) {
            return false;
        } else {
            this.closestLivingEntity = list.get(0);
            if (closestLivingEntity != null && this.stymphalianBird.getVictor() != null && this.closestLivingEntity.equals(this.stymphalianBird.getVictor())) {
                Vector3d Vector3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.stymphalianBird, 32, 7, new Vector3d(this.closestLivingEntity.getPosX(), this.closestLivingEntity.getPosY(), this.closestLivingEntity.getPosZ()));

                if (Vector3d == null) {
                    return false;
                } else {
                    Vector3d = Vector3d.add(0, 3, 0);
                    this.stymphalianBird.getMoveHelper().setMoveTo(Vector3d.x, Vector3d.y, Vector3d.z, 3D);
                    this.stymphalianBird.getLookController().setLookPosition(Vector3d.x, Vector3d.y, Vector3d.z, 180.0F, 20.0F);
                    hidePlace = Vector3d;
                    return true;
                }
            }
            return false;
        }
    }

    public boolean shouldContinueExecuting() {
        return hidePlace != null && this.stymphalianBird.getDistanceSq(hidePlace.add(0.5, 0.5, 0.5)) < 2;
    }

    public void startExecuting() {
        this.stymphalianBird.getMoveHelper().setMoveTo(hidePlace.x, hidePlace.y, hidePlace.z, 3D);
        this.stymphalianBird.getLookController().setLookPosition(hidePlace.x, hidePlace.y, hidePlace.z, 180.0F, 20.0F);
    }

    public void resetTask() {
        this.closestLivingEntity = null;
    }
}