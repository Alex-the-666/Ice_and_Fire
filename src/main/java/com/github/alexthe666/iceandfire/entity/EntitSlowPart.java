package com.github.alexthe666.iceandfire.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;

public class EntitSlowPart extends EntityMutlipartPart{
    public EntitSlowPart(EntityType t, World world) {
        super(t, world);
    }

    public EntitSlowPart(FMLPlayMessages.SpawnEntity spawnEntity, World worldIn) {
        this(IafEntityRegistry.SLOW_MULTIPART, worldIn);
    }

    public EntitSlowPart(EntityType t, LivingEntity parent, float radius, float angleYaw, float offsetY, float sizeX, float sizeY, float damageMultiplier) {
        super(t, parent, radius, angleYaw, offsetY, sizeX, sizeY, damageMultiplier);
    }

    public EntitSlowPart(Entity parent, float radius, float angleYaw, float offsetY, float sizeX, float sizeY, float damageMultiplier) {
        super(IafEntityRegistry.SLOW_MULTIPART, parent, radius, angleYaw, offsetY, sizeX, sizeY, damageMultiplier);
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    protected boolean isSlowFollow(){
        return true;
    }
}
