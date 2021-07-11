package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityGhost;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.world.World;

public class GhostPathNavigator extends FlyingPathNavigator {

    public EntityGhost ghost;

    public GhostPathNavigator(EntityGhost entityIn, World worldIn) {
        super(entityIn, worldIn);
        ghost = entityIn;

    }

    public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn) {
        ghost.getMoveHelper().setMoveTo(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ(), speedIn);
        return true;
    }

    public boolean tryMoveToXYZ(double x, double y, double z, double speedIn) {
        ghost.getMoveHelper().setMoveTo(x, y, z, speedIn);
        return true;
    }
}
