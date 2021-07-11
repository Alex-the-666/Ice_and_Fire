package com.github.alexthe666.iceandfire.entity.ai;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityDragonEgg;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class DragonAIMate extends Goal {
    private static final BlockState NEST = IafBlockRegistry.NEST.getDefaultState();
    private final EntityDragonBase dragon;
    World theWorld;
    int spawnBabyDelay;
    double moveSpeed;
    private EntityDragonBase targetMate;

    public DragonAIMate(EntityDragonBase dragon, double speedIn) {
        this.dragon = dragon;
        this.theWorld = dragon.world;
        this.moveSpeed = speedIn;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        if (!this.dragon.isInLove() || !this.dragon.canMove()) {
            return false;
        } else {
            this.targetMate = this.getNearbyMate();
            return this.targetMate != null;
        }
    }

    /**
     * Returns whether an in-progress Goal should continue executing
     */
    public boolean continueExecuting() {
        return this.targetMate.isAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
    }

    /**
     * Resets the task
     */
    public void resetTask() {
        this.targetMate = null;
        this.spawnBabyDelay = 0;
    }

    /**
     * Updates the task
     */
    public void tick() {
        this.dragon.getLookController().setLookPositionWithEntity(this.targetMate, 10.0F, (float) this.dragon.getVerticalFaceSpeed());
        this.dragon.getNavigator().tryMoveToXYZ(targetMate.getPosX(), targetMate.getPosY(), targetMate.getPosZ(), this.moveSpeed);
        this.dragon.setFlying(false);
        this.dragon.setHovering(false);
        ++this.spawnBabyDelay;
        if (this.spawnBabyDelay >= 60 && this.dragon.getDistance(this.targetMate) < 35) {
            this.spawnBaby();
        }
    }

    /**
     * Loops through nearby animals and finds another animal of the same type that can be mated with. Returns the first
     * valid mate found.
     */
    private EntityDragonBase getNearbyMate() {
        List<EntityDragonBase> list = this.theWorld.getEntitiesWithinAABB(this.dragon.getClass(), this.dragon.getBoundingBox().grow(180.0D, 180.0D, 180.0D));
        double d0 = Double.MAX_VALUE;
        EntityDragonBase mate = null;
        for (EntityDragonBase partner : list) {
            if (this.dragon.canMateWith(partner)) {
                double d1 = this.dragon.getDistanceSq(partner);
                if (d1 < d0) { // find min distance
                    mate = partner;
                    d0 = d1;
                }
            }
        }
        return mate;
    }

    /**
     * Spawns a baby animal of the same type.
     */
    private void spawnBaby() {

        EntityDragonEgg egg = this.dragon.createEgg(this.targetMate);

        if (egg != null) {
            PlayerEntity PlayerEntity = this.dragon.getLoveCause();

            if (PlayerEntity == null && this.targetMate.getLoveCause() != null) {
                PlayerEntity = this.targetMate.getLoveCause();
            }

            this.dragon.setGrowingAge(6000);
            this.targetMate.setGrowingAge(6000);
            this.dragon.resetInLove();
            this.targetMate.resetInLove();
            int nestX = (int) (this.dragon.isMale() ? this.targetMate.getPosX() : this.dragon.getPosX());
            int nestY = (int) (this.dragon.isMale() ? this.targetMate.getPosY() : this.dragon.getPosY()) - 1;
            int nestZ = (int) (this.dragon.isMale() ? this.targetMate.getPosZ() : this.dragon.getPosZ());

            egg.setLocationAndAngles(nestX - 0.5F, nestY + 1F, nestZ - 0.5F, 0.0F, 0.0F);
            this.theWorld.addEntity(egg);
            Random random = this.dragon.getRNG();

            for (int i = 0; i < 17; ++i) {
                double d0 = random.nextGaussian() * 0.02D;
                double d1 = random.nextGaussian() * 0.02D;
                double d2 = random.nextGaussian() * 0.02D;
                double d3 = random.nextDouble() * (double) this.dragon.getWidth() * 2.0D - (double) this.dragon.getWidth();
                double d4 = 0.5D + random.nextDouble() * (double) this.dragon.getHeight();
                double d5 = random.nextDouble() * (double) this.dragon.getWidth() * 2.0D - (double) this.dragon.getWidth();
                this.theWorld.addParticle(ParticleTypes.HEART, this.dragon.getPosX() + d3, this.dragon.getPosY() + d4, this.dragon.getPosZ() + d5, d0, d1, d2);
            }
            BlockPos eggPos = new BlockPos(nestX - 2, nestY, nestZ - 2);
            BlockPos dirtPos = eggPos.add(1, 0, 1);

            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    BlockPos add = eggPos.add(x, 0, z);
                    BlockState prevState = theWorld.getBlockState(add);
                    if (prevState.getMaterial().isReplaceable() || theWorld.getBlockState(add).getMaterial() == Material.EARTH || theWorld.getBlockState(add).getBlockHardness(theWorld, add) < 5F || theWorld.getBlockState(add).getBlockHardness(theWorld, add) >= 0F) {
                        theWorld.setBlockState(add, NEST);
                    }
                }
            }
            if (theWorld.getBlockState(dirtPos).getMaterial().isReplaceable() || theWorld.getBlockState(dirtPos) == NEST) {
                theWorld.setBlockState(dirtPos, Blocks.GRASS_PATH.getDefaultState());
            }
            if (this.theWorld.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                this.theWorld.addEntity(new ExperienceOrbEntity(this.theWorld, this.dragon.getPosX(), this.dragon.getPosY(), this.dragon.getPosZ(), random.nextInt(15) + 10));
            }
        }
    }
}