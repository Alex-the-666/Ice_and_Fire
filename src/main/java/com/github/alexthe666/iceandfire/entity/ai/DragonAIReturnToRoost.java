package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class DragonAIReturnToRoost  extends Goal {
    private final EntityDragonBase dragon;
    private final double movementSpeed;
    private Path path;

    public DragonAIReturnToRoost(EntityDragonBase entityIn, double movementSpeedIn) {
        this.dragon = entityIn;
        this.movementSpeed = movementSpeedIn;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        return this.dragon.canMove() && this.dragon.lookingForRoostAIFlag && (dragon.getAttackTarget() == null || !dragon.getAttackTarget().isAlive()) && dragon.getHomePosition() != null && dragon.getDistanceSquared(Vector3d.copyCentered(dragon.getHomePosition())) > dragon.getWidth() * dragon.getWidth();
    }

    public void tick() {
        if (this.dragon.getHomePosition() != null) {
            double dist = Math.sqrt(dragon.getDistanceSquared(Vector3d.copyCentered(dragon.getHomePosition())));
            double xDist = Math.abs(dragon.getPosX() - dragon.getHomePosition().getX() - 0.5F);
            double zDist = Math.abs(dragon.getPosZ() - dragon.getHomePosition().getZ() - 0.5F);
            double xzDist = Math.sqrt(xDist * xDist + zDist * zDist);

            if (dist < this.dragon.getWidth()) {
                this.dragon.setFlying(false);
                this.dragon.setHovering(false);
                this.dragon.getNavigator().tryMoveToXYZ(this.dragon.getHomePosition().getX(), this.dragon.getHomePosition().getY(), this.dragon.getHomePosition().getZ(), 1.0F);
            }else{
                double yAddition = 15 + dragon.getRNG().nextInt(3);
                if(xzDist < 40){
                    yAddition = 0;
                    if(this.dragon.isOnGround()){
                        this.dragon.setFlying(false);
                        this.dragon.setHovering(false);
                        this.dragon.flightManager.setFlightTarget(Vector3d.copyCenteredWithVerticalOffset(this.dragon.getHomePosition(), yAddition));
                        this.dragon.getNavigator().tryMoveToXYZ(this.dragon.getHomePosition().getX(), this.dragon.getHomePosition().getY(), this.dragon.getHomePosition().getZ(), 1.0F);
                        return;
                    }
                }
                if(!this.dragon.isFlying() && !this.dragon.isHovering() && xzDist > 40){
                    this.dragon.setHovering(true);
                }
                if(this.dragon.isFlying()){
                    this.dragon.flightManager.setFlightTarget(Vector3d.copyCenteredWithVerticalOffset(this.dragon.getHomePosition(), yAddition));
                    this.dragon.getNavigator().tryMoveToXYZ(this.dragon.getHomePosition().getX(), yAddition + this.dragon.getHomePosition().getY(), this.dragon.getHomePosition().getZ(), 1F);
                }
                this.dragon.flyTicks = 0;
            }

        }

    }

    public boolean shouldContinueExecuting() {
        return shouldExecute();
    }

}
