package com.github.alexthe666.iceandfire.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.util.IBlacklistedFromStatues;
import com.github.alexthe666.iceandfire.entity.util.IDeadMob;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class EntityDragonSkull extends AnimalEntity implements IBlacklistedFromStatues, IDeadMob {

    private static final DataParameter<Integer> DRAGON_TYPE = EntityDataManager.createKey(EntityDragonSkull.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DRAGON_AGE = EntityDataManager.createKey(EntityDragonSkull.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DRAGON_STAGE = EntityDataManager.createKey(EntityDragonSkull.class, DataSerializers.VARINT);
    private static final DataParameter<Float> DRAGON_DIRECTION = EntityDataManager.createKey(EntityDragonSkull.class, DataSerializers.FLOAT);

    public final float minSize = 0.3F;
    public final float maxSize = 8.58F;

    public EntityDragonSkull(EntityType type, World worldIn) {
        super(type, worldIn);
        this.ignoreFrustumCheck = true;
        // setScale(this.getDragonAge());
    }

    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, 10)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0D);
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource i) {
        return i.getTrueSource() != null && super.isInvulnerableTo(i);
    }

    @Override
    public boolean isAIDisabled() {
        return true;
    }

    public boolean isOnWall() {
        return this.world.isAirBlock(this.getPosition().down());
    }

    public void onUpdate() {
        this.prevRenderYawOffset = 0;
        this.prevRotationYawHead = 0;
        this.renderYawOffset = 0;
        this.rotationYawHead = 0;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.getDataManager().register(DRAGON_TYPE, Integer.valueOf(0));
        this.getDataManager().register(DRAGON_AGE, Integer.valueOf(0));
        this.getDataManager().register(DRAGON_STAGE, Integer.valueOf(0));
        this.getDataManager().register(DRAGON_DIRECTION, Float.valueOf(0F));
    }

    public float getYaw() {
        return this.getDataManager().get(DRAGON_DIRECTION).floatValue();
    }

    public void setYaw(float var1) {
        this.getDataManager().set(DRAGON_DIRECTION, var1);
    }

    public int getDragonType() {
        return this.getDataManager().get(DRAGON_TYPE).intValue();
    }

    public void setDragonType(int var1) {
        this.getDataManager().set(DRAGON_TYPE, var1);
    }

    public int getStage() {
        return this.getDataManager().get(DRAGON_STAGE).intValue();
    }

    public void setStage(int var1) {
        this.getDataManager().set(DRAGON_STAGE, var1);
    }

    public int getDragonAge() {
        return this.getDataManager().get(DRAGON_AGE).intValue();
    }

    public void setDragonAge(int var1) {
        this.getDataManager().set(DRAGON_AGE, var1);
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return null;
    }

    @Override
    public boolean attackEntityFrom(DamageSource var1, float var2) {
        this.turnIntoItem();
        return super.attackEntityFrom(var1, var2);
    }

    public void turnIntoItem() {
        if (removed)
            return;
        this.remove();
        ItemStack stack = new ItemStack(getDragonSkullItem());
        stack.setTag(new CompoundNBT());
        stack.getTag().putInt("Stage", this.getStage());
        stack.getTag().putInt("DragonAge", this.getDragonAge());
        if (!this.world.isRemote)
            this.entityDropItem(stack, 0.0F);

    }

    public Item getDragonSkullItem() {
        switch (getDragonType()) {
            case 0:
                return IafItemRegistry.DRAGON_SKULL_FIRE;
            case 1:
                return IafItemRegistry.DRAGON_SKULL_ICE;
            case 2:
                return IafItemRegistry.DRAGON_SKULL_LIGHTNING;
            default:
                return IafItemRegistry.DRAGON_SKULL_FIRE;
        }
    }

    @Nullable
    @Override
    public AgeableEntity createChild(ServerWorld serverWorld, AgeableEntity ageable) {
        return null;
    }

    @Override
    public ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
        if (player.isSneaking()) {
            this.setYaw(player.rotationYaw);
        }
        return super.getEntityInteractionResult(player, hand);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        this.setDragonType(compound.getInt("Type"));
        this.setStage(compound.getInt("Stage"));
        this.setDragonAge(compound.getInt("DragonAge"));
        this.setYaw(compound.getFloat("DragonYaw"));
        super.readAdditional(compound);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        compound.putInt("Type", this.getDragonType());
        compound.putInt("Stage", this.getStage());
        compound.putInt("DragonAge", this.getDragonAge());
        compound.putFloat("DragonYaw", this.getYaw());
        super.writeAdditional(compound);
    }

    public float getDragonSize() {
        float step;
        step = (minSize - maxSize) / (125);

        if (this.getDragonAge() > 125) {
            return this.minSize + (step * 125);
        }

        return this.minSize + (step * this.getDragonAge());
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    protected void collideWithEntity(Entity entity) {
    }

    @Override
    public boolean canBeTurnedToStone() {
        return false;
    }

    @Override
    public boolean isMobDead() {
        return true;
    }

    public int getDragonStage() {
        return Math.max(getStage(), 1);
    }

    @Override
    public boolean isNoDespawnRequired() {
        return true;
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }
}
