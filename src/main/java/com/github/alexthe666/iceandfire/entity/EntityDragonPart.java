package com.github.alexthe666.iceandfire.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;

public class EntityDragonPart extends EntityMutlipartPart {
    private EntityDragonBase dragon;
    public EntityDragonPart(EntityType t, World world) {
        super(t, world);
    }

    public EntityDragonPart(FMLPlayMessages.SpawnEntity spawnEntity, World worldIn) {
        this(IafEntityRegistry.DRAGON_MULTIPART, worldIn);
    }

    public EntityDragonPart(EntityType type, EntityDragonBase dragon, float radius, float angleYaw, float offsetY, float sizeX, float sizeY, float damageMultiplier) {
        super(type, dragon, radius, angleYaw, offsetY, sizeX, sizeY, damageMultiplier);
        this.dragon = dragon;
    }

    public EntityDragonPart(EntityDragonBase parent, float radius, float angleYaw, float offsetY, float sizeX, float sizeY, float damageMultiplier) {
        super(IafEntityRegistry.DRAGON_MULTIPART, parent, radius, angleYaw, offsetY, sizeX, sizeY, damageMultiplier);
        this.dragon = parent;
    }

    public void collideWithNearbyEntities() {
    }

    @Override
    public boolean shouldNotExist() {
        return this.dragon != null && !this.dragon.isAlive() && !this.dragon.isModelDead();
    }
}
