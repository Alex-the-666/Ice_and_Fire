package com.github.alexthe666.iceandfire.entity.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexRoyal;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.world.MyrmexWorldData;
import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.util.math.AxisAlignedBB;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class MyrmexAIFindMate<T extends EntityMyrmexBase> extends TargetGoal {
    protected final DragonAITargetItems.Sorter theNearestAttackableTargetSorter;
    protected final Predicate<? super Entity> targetEntitySelector;
    public EntityMyrmexRoyal myrmex;
    protected EntityMyrmexBase targetEntity;

    public MyrmexAIFindMate(EntityMyrmexRoyal myrmex) {
        super(myrmex, false, false);
        this.theNearestAttackableTargetSorter = new DragonAITargetItems.Sorter(myrmex);
        this.targetEntitySelector = new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity myrmex) {
                return myrmex != null && myrmex instanceof EntityMyrmexRoyal && ((EntityMyrmexRoyal) myrmex).getGrowthStage() >= 2;
            }
        };
        this.myrmex = myrmex;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        if (!this.myrmex.shouldHaveNormalAI()) {
            return false;
        }
        if (!this.myrmex.canMove() || this.myrmex.getAttackTarget() != null || this.myrmex.releaseTicks < 400 || this.myrmex.mate != null) {
            return false;
        }
        MyrmexHive village = this.myrmex.getHive();
        if (village == null) {
            village = MyrmexWorldData.get(this.myrmex.world).getNearestHive(this.myrmex.getPosition(), 100);
        }
        if (village != null && village.getCenter().distanceSq(this.myrmex.getPosX(), village.getCenter().getY(), this.myrmex.getPosZ(), true) < 2000) {
            return false;
        }
        List<Entity> list = this.goalOwner.world.getEntitiesInAABBexcluding(myrmex, this.getTargetableArea(100), this.targetEntitySelector);
        if (list.isEmpty()) {
            return false;
        } else {
            Collections.sort(list, this.theNearestAttackableTargetSorter);
            for (Entity royal : list) {
                if (this.myrmex.canMateWith((EntityMyrmexRoyal) royal)) {
                    this.myrmex.mate = (EntityMyrmexRoyal) royal;
                    this.myrmex.world.setEntityState(this.myrmex, (byte) 76);
                    return true;
                }
            }
            return false;
        }
    }

    protected AxisAlignedBB getTargetableArea(double targetDistance) {
        return this.goalOwner.getBoundingBox().grow(targetDistance, targetDistance/2, targetDistance);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return false;
    }

    public static class Sorter implements Comparator<Entity> {
        private final Entity theEntity;

        public Sorter(EntityMyrmexBase theEntityIn) {
            this.theEntity = theEntityIn;
        }

        public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d0 = this.theEntity.getDistanceSq(p_compare_1_);
            double d1 = this.theEntity.getDistanceSq(p_compare_2_);
            return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
        }
    }
}