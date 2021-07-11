package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;
import java.util.List;

import com.github.alexthe666.iceandfire.entity.EntityDreadKnight;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class DreadAIRideHorse extends Goal {
    private final EntityDreadKnight knight;
    private AbstractHorseEntity horse;

    public DreadAIRideHorse(EntityDreadKnight knight) {
        this.knight = knight;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        if (this.knight.isPassenger()) {
            return false;
        } else {
            List<AbstractHorseEntity> list = this.knight.world.getEntitiesWithinAABB(AbstractHorseEntity.class, this.knight.getBoundingBox().grow(16.0D, 7.0D, 16.0D));

            if (list.isEmpty()) {
                return false;
            } else {
                for (AbstractHorseEntity entityirongolem : list) {
                    if (!entityirongolem.isBeingRidden()) {
                        this.horse = entityirongolem;
                        break;
                    }
                }

                return this.horse != null;
            }
        }
    }

    public boolean shouldContinueExecuting() {
        return !this.knight.isPassenger() && this.horse != null && !this.horse.isBeingRidden();
    }

    public void startExecuting() {
        this.horse.getNavigator().clearPath();
    }

    public void resetTask() {
        this.horse = null;
        this.knight.getNavigator().clearPath();
    }

    public void tick() {
        this.knight.getLookController().setLookPositionWithEntity(this.horse, 30.0F, 30.0F);

        this.knight.getNavigator().tryMoveToEntityLiving(this.horse, 1.2D);

        if (this.knight.getDistanceSq(this.horse) < 4.0D) {
            this.horse.setHorseTamed(true);
            this.knight.getNavigator().clearPath();
            this.knight.startRiding(horse);
        }
    }
}