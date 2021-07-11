package com.github.alexthe666.iceandfire.entity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.message.MessageMultipartInteract;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public abstract class EntityMutlipartPart extends Entity {

    private static final DataParameter<Optional<UUID>> PARENT_UUID = EntityDataManager.createKey(EntityMutlipartPart.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private static final DataParameter<Float> SCALE_WIDTH = EntityDataManager.createKey(EntityMutlipartPart.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> SCALE_HEIGHT = EntityDataManager.createKey(EntityMutlipartPart.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> PART_YAW = EntityDataManager.createKey(EntityMutlipartPart.class, DataSerializers.FLOAT);
    public EntitySize multipartSize;
    protected float radius;
    protected float angleYaw;
    protected float offsetY;
    protected float damageMultiplier;

    public EntityMutlipartPart(EntityType t, World world) {
        super(t, world);
        multipartSize = t.getSize();
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {

    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {

    }

    @Override
    public Entity getEntity() {
        return this;
    }

    @Override
    protected void doWaterSplashEffect() {

    }

    public EntityMutlipartPart(EntityType t, Entity parent, float radius, float angleYaw, float offsetY, float sizeX, float sizeY, float damageMultiplier) {
        super(t, parent.world);
        this.setParent(parent);
        this.setScaleX(sizeX);
        this.setScaleY(sizeY);
        this.radius = radius;
        this.angleYaw = (angleYaw + 90.0F) * ((float) Math.PI / 180.0F);
        this.offsetY = offsetY;

        this.damageMultiplier = damageMultiplier;
    }


    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, 2D)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.1D);
    }

    @Override
    public EntitySize getSize(Pose poseIn) {
        return new EntitySize(getScaleX(), getScaleY(), false);
    }

    @Override
    protected void registerData() {
        this.dataManager.register(PARENT_UUID, Optional.empty());
        this.dataManager.register(SCALE_WIDTH, 0.5F);
        this.dataManager.register(SCALE_HEIGHT, 0.5F);
        this.dataManager.register(PART_YAW, 0F);
    }

    @Nullable
    public UUID getParentId() {
        return this.dataManager.get(PARENT_UUID).orElse(null);
    }

    public void setParentId(@Nullable UUID uniqueId) {
        this.dataManager.set(PARENT_UUID, Optional.ofNullable(uniqueId));
    }

    private float getScaleX() {
        return this.dataManager.get(SCALE_WIDTH).floatValue();
    }

    private void setScaleX(float scale) {
        this.dataManager.set(SCALE_WIDTH, scale);
    }

    private float getScaleY() {
        return this.dataManager.get(SCALE_HEIGHT).floatValue();
    }

    private void setScaleY(float scale) {
        this.dataManager.set(SCALE_HEIGHT, scale);
    }

    public float getPartYaw() {
        return this.dataManager.get(PART_YAW).floatValue();
    }

    private void setPartYaw(float yaw) {
        this.dataManager.set(PART_YAW, yaw % 360);
    }

    @Override
    public void tick() {
        inWater = false;
        if(this.ticksExisted > 10){
            Entity parent = getParent();
            recalculateSize();
            if (parent != null && !world.isRemote) {
                float renderYawOffset = parent.rotationYaw;
                if(parent instanceof LivingEntity) {
                    renderYawOffset = ((LivingEntity) parent).renderYawOffset;
                }
                if(isSlowFollow()){
                    this.setPosition(parent.prevPosX + this.radius * Math.cos(renderYawOffset * (Math.PI / 180.0F) + this.angleYaw), parent.prevPosY + this.offsetY, parent.prevPosZ + this.radius * Math.sin(renderYawOffset * (Math.PI / 180.0F) + this.angleYaw));
                    double d0 = parent.getPosX() - this.getPosX();
                    double d1 = parent.getPosY() - this.getPosY();
                    double d2 = parent.getPosZ() - this.getPosZ();
                    double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                    float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                    float f2 = -((float) (MathHelper.atan2(d1, MathHelper.sqrt(d0 * d0 + d2 * d2)) * (double) (180F / (float) Math.PI)));
                    this.rotationPitch = this.limitAngle(this.rotationPitch, f2, 5.0F);
                    this.markVelocityChanged();
                    this.rotationYaw = renderYawOffset;
                    this.setPartYaw(rotationYaw);
                    if (!this.world.isRemote) {
                        this.collideWithNearbyEntities();
                    }
                }else{
                    this.setPosition(parent.getPosX() + this.radius * Math.cos(renderYawOffset * (Math.PI / 180.0F) + this.angleYaw), parent.getPosY() + this.offsetY, parent.getPosZ() + this.radius * Math.sin(renderYawOffset * (Math.PI / 180.0F) + this.angleYaw));
                    this.markVelocityChanged();
                }
                if (!this.world.isRemote) {
                    this.collideWithNearbyEntities();
                }
                if (parent.removed && !world.isRemote) {
                    this.remove();
                }
            } else if (ticksExisted > 20 && !world.isRemote) {
                remove();
            }
        }
        super.tick();
    }

    protected boolean isSlowFollow(){
        return false;
    }

    protected float limitAngle(float sourceAngle, float targetAngle, float maximumChange) {
        float f = MathHelper.wrapDegrees(targetAngle - sourceAngle);
        if (f > maximumChange) {
            f = maximumChange;
        }

        if (f < -maximumChange) {
            f = -maximumChange;
        }

        float f1 = sourceAngle + f;
        if (f1 < 0.0F) {
            f1 += 360.0F;
        } else if (f1 > 360.0F) {
            f1 -= 360.0F;
        }

        return f1;
    }


    public void remove() {
        this.remove(false);
    }

    public Entity getParent() {
        UUID id = getParentId();
        if (id != null && !world.isRemote) {
            Entity e = ((ServerWorld) world).getEntityByUuid(id);
            return e;
        }
        return null;
    }

    public void setParent(Entity entity) {
        this.setParentId(entity.getUniqueID());
    }

    @Override
    public boolean isEntityEqual(Entity entity) {
        return this == entity || this.getParent() == entity;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public void collideWithNearbyEntities() {
        List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
        Entity parent = this.getParent();
        if(parent != null){
            entities.stream().filter(entity -> entity != parent && !(entity instanceof EntityMutlipartPart) && entity.canBePushed()).forEach(entity -> entity.applyEntityCollision(parent));

        }
    }

    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        Entity parent = getParent();
        if (world.isRemote && parent != null) {
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageMultipartInteract(parent.getEntityId(), 0));
        }
        return parent != null ? parent.processInitialInteract(player, hand) : ActionResultType.PASS;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        Entity parent = getParent();
        if (world.isRemote && source.getTrueSource() instanceof PlayerEntity && parent != null) {
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageMultipartInteract(parent.getEntityId(), damage * damageMultiplier));
        }
        return parent != null && parent.attackEntityFrom(source, damage * this.damageMultiplier);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.FALL || source == DamageSource.DROWN || source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || source == DamageSource.LAVA || source.isFireDamage() || super.isInvulnerableTo(source);
    }

    public boolean shouldNotExist() {
        Entity parent = getParent();
        return !parent.isAlive();
    }

    public boolean shouldContinuePersisting() {
        return isAddedToWorld() || this.removed;
    }
}
