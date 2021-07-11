package com.github.alexthe666.iceandfire.entity.ai;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.EntityCockatrice;
import com.github.alexthe666.iceandfire.entity.EntityGorgon;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.player.PlayerEntity;

public class CockatriceAIAggroLook extends NearestAttackableTargetGoal<PlayerEntity> {
    private final EntityCockatrice cockatrice;
    private PlayerEntity player;
    private int aggroTime;
    private int teleportTime;

    public CockatriceAIAggroLook(EntityCockatrice endermanIn) {
        super(endermanIn, PlayerEntity.class, false);
        this.cockatrice = endermanIn;
    }

    /**
     * Returns whether the Goal should begin execution.
     */
    public boolean shouldExecute() {
        if (cockatrice.isTamed()) {
            return false;
        }
        double d0 = this.getTargetDistance();
        this.player = this.cockatrice.world.getClosestPlayer(new EntityPredicate() {
            public boolean canTarget(@Nullable LivingEntity attacker, LivingEntity target) {
                return target != null && EntityGorgon.isEntityLookingAt(target, CockatriceAIAggroLook.this.cockatrice, EntityCockatrice.VIEW_RADIUS) && CockatriceAIAggroLook.this.cockatrice.getDistance(target) < d0;
            }
        }, this.cockatrice.getPosX(), this.cockatrice.getPosY(), this.cockatrice.getPosZ());
        return this.player != null;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        this.aggroTime = 5;
        this.teleportTime = 0;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask() {
        this.player = null;
        super.resetTask();
    }

    /**
     * Returns whether an in-progress Goal should continue executing
     */
    public boolean shouldContinueExecuting() {
        if (this.player != null && !this.player.isCreative()) {
            if (!EntityGorgon.isEntityLookingAt(this.player, this.cockatrice, 0.4F)) {
                return false;
            } else {
                this.cockatrice.faceEntity(this.player, 10.0F, 10.0F);
                if (!this.cockatrice.isTamed()) {
                    this.cockatrice.setTargetedEntity(this.player.getEntityId());
                    this.cockatrice.setAttackTarget(this.player);
                }
                return true;
            }
        } else {
            return this.target != null && this.target.isAlive() || super.shouldContinueExecuting();
        }
    }
}
