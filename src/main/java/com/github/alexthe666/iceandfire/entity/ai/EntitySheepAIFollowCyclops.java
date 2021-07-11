package com.github.alexthe666.iceandfire.entity.ai;

import java.util.List;

import com.github.alexthe666.iceandfire.entity.EntityCyclops;

import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class EntitySheepAIFollowCyclops extends Goal {
    AnimalEntity childAnimal;
    EntityCyclops cyclops;
    double moveSpeed;
    private int delayCounter;

    public EntitySheepAIFollowCyclops(AnimalEntity animal, double speed) {
        this.childAnimal = animal;
        this.moveSpeed = speed;
    }

    public boolean shouldExecute() {
        List<EntityCyclops> list = this.childAnimal.world.getEntitiesWithinAABB(EntityCyclops.class, this.childAnimal.getBoundingBox().grow(16.0D, 8.0D, 16.0D));
        EntityCyclops cyclops = null;
        double d0 = Double.MAX_VALUE;

        for (EntityCyclops cyclops1 : list) {
            double d1 = this.childAnimal.getDistanceSq(cyclops1);

            if (d1 <= d0) {
                d0 = d1;
                cyclops = cyclops1;
            }
        }

        if (cyclops == null) {
            return false;
        } else if (d0 < 10.0D) {
            return false;
        } else {
            this.cyclops = cyclops;
            return true;
        }
    }


    public boolean shouldContinueExecuting() {
        if (this.cyclops.isAlive()) {
            return false;
        } else {
            double d0 = this.childAnimal.getDistanceSq(this.cyclops);
            return d0 >= 9.0D && d0 <= 256.0D;
        }
    }


    public void startExecuting() {
        this.delayCounter = 0;
    }

    public void resetTask() {
        this.cyclops = null;
    }

    public void tick() {
        if (--this.delayCounter <= 0) {
            this.delayCounter = 10;
            Path path = getPathToLivingEntity(this.childAnimal, this.cyclops);
            if (path != null) {
                this.childAnimal.getNavigator().setPath(path, this.moveSpeed);

            }
        }
    }

    public Path getPathToLivingEntity(AnimalEntity entityIn, EntityCyclops cyclops) {
        PathNavigator navi = entityIn.getNavigator();
        Vector3d Vector3d = RandomPositionGenerator.findRandomTargetBlockTowards(entityIn, 2, 7, new Vector3d(cyclops.getPosX(), cyclops.getPosY(), cyclops.getPosZ()));
        if (Vector3d != null) {
            BlockPos blockpos = new BlockPos(Vector3d);
            return navi.getPathToPos(blockpos, 0);
        }
        return null;
    }
}