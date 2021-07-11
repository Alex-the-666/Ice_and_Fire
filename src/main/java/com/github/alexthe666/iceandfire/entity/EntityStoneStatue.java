package com.github.alexthe666.iceandfire.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.util.IBlacklistedFromStatues;
import com.google.common.collect.ImmutableList;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class EntityStoneStatue extends LivingEntity implements IBlacklistedFromStatues {

    public boolean smallArms;
    private static final DataParameter<String> TRAPPED_ENTITY_TYPE = EntityDataManager.createKey(EntityStoneStatue.class, DataSerializers.STRING);
    private static final DataParameter<CompoundNBT> TRAPPED_ENTITY_DATA = EntityDataManager.createKey(EntityStoneStatue.class, DataSerializers.COMPOUND_NBT);
    private static final DataParameter<Float> TRAPPED_ENTITY_WIDTH = EntityDataManager.createKey(EntityStoneStatue.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> TRAPPED_ENTITY_HEIGHT = EntityDataManager.createKey(EntityStoneStatue.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> TRAPPED_ENTITY_SCALE = EntityDataManager.createKey(EntityStoneStatue.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> CRACK_AMOUNT = EntityDataManager.createKey(EntityStoneStatue.class, DataSerializers.VARINT);
    private EntitySize stoneStatueSize = EntitySize.fixed(0.5F, 0.5F);

    public EntityStoneStatue(EntityType t, World worldIn) {
        super(t, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, 20)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.0D)
                //ATTACK
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    public void applyEntityCollision(Entity entityIn) {
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(TRAPPED_ENTITY_TYPE, "minecraft:pig");
        this.dataManager.register(TRAPPED_ENTITY_DATA, new CompoundNBT());
        this.dataManager.register(TRAPPED_ENTITY_WIDTH, 0.5F);
        this.dataManager.register(TRAPPED_ENTITY_HEIGHT, 0.5F);
        this.dataManager.register(TRAPPED_ENTITY_SCALE, 1F);
        this.dataManager.register(CRACK_AMOUNT, 0);
    }

    public EntityType getTrappedEntityType() {
        String str = getTrappedEntityTypeString();
        return EntityType.byKey(str).orElse(EntityType.PIG);
    }


    public String getTrappedEntityTypeString() {
        return this.dataManager.get(TRAPPED_ENTITY_TYPE);
    }

    public void setTrappedEntityTypeString(String string) {
        this.dataManager.set(TRAPPED_ENTITY_TYPE, string);
    }

    public CompoundNBT getTrappedTag() {
        return this.dataManager.get(TRAPPED_ENTITY_DATA);
    }

    public void setTrappedTag(CompoundNBT tag) {
        this.dataManager.set(TRAPPED_ENTITY_DATA, tag);
    }

    public float getTrappedWidth() {
        return this.dataManager.get(TRAPPED_ENTITY_WIDTH);
    }

    public void setTrappedEntityWidth(float size) {
        this.dataManager.set(TRAPPED_ENTITY_WIDTH, size);
    }

    public float getTrappedHeight() {
        return this.dataManager.get(TRAPPED_ENTITY_HEIGHT);
    }

    public void setTrappedHeight(float size) {
        this.dataManager.set(TRAPPED_ENTITY_HEIGHT, size);
    }

    public float getTrappedScale() {
        return this.dataManager.get(TRAPPED_ENTITY_SCALE);
    }

    public void setTrappedScale(float size) {
        this.dataManager.set(TRAPPED_ENTITY_SCALE, size);
    }

    public static EntityStoneStatue buildStatueEntity(LivingEntity parent){
        EntityStoneStatue statue = IafEntityRegistry.STONE_STATUE.create(parent.world);
        CompoundNBT entityTag = new CompoundNBT();
        try{
            parent.writeWithoutTypeId(entityTag);
        }catch (Exception e){
            IceAndFire.LOGGER.debug("Encountered issue creating stone statue from {}", parent);
        }
        statue.setTrappedTag(entityTag);
        statue.setTrappedEntityTypeString(Registry.ENTITY_TYPE.getKey(parent.getType()).toString());
        statue.setTrappedEntityWidth(parent.getWidth());
        statue.setTrappedHeight(parent.getHeight());
        statue.setTrappedScale(parent.getRenderScale());

        return statue;
    }

    @Nullable
    public AxisAlignedBB getCollisionBox(Entity entityIn) {
        return this.getCollisionBoundingBox();
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox() {
        return this.getBoundingBox();
    }

    public boolean isAIDisabled() {
        return true;
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        tag.putInt("CrackAmount", this.getCrackAmount());
        tag.putFloat("StatueWidth", this.getTrappedWidth());
        tag.putFloat("StatueHeight", this.getTrappedHeight());
        tag.putFloat("StatueScale", this.getTrappedScale());
        tag.putString("StatueEntityType", this.getTrappedEntityTypeString());
        tag.put("StatueEntityTag", this.getTrappedTag());
    }

    @Override
    public float getRenderScale() {
        return this.getTrappedScale();
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        this.setCrackAmount(tag.getByte("CrackAmount"));
        this.setTrappedEntityWidth(tag.getFloat("StatueWidth"));
        this.setTrappedHeight(tag.getFloat("StatueHeight"));
        this.setTrappedScale(tag.getFloat("StatueScale"));
        this.setTrappedEntityTypeString(tag.getString("StatueEntityType"));
        if(tag.contains("StatueEntityTag")){
            this.setTrappedTag(tag.getCompound("StatueEntityTag"));

        }
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        return source == DamageSource.OUT_OF_WORLD;
    }

    public EntitySize getSize(Pose poseIn) {
        return stoneStatueSize;
    }

    public void tick() {
        super.tick();
        this.rotationYaw = this.renderYawOffset;
        this.rotationYawHead = this.rotationYaw;
        if(Math.abs(this.getWidth() - getTrappedWidth()) > 0.01 || Math.abs(this.getHeight() - getTrappedHeight())  > 0.01){
            double prevX = this.getPosX();
            double prevZ = this.getPosZ();
            this.stoneStatueSize = EntitySize.flexible(getTrappedWidth(), getTrappedHeight());
            recalculateSize();
            this.setPosition(prevX, this.getPosY(), prevZ);
        }
    }

    public void onKillCommand() {
        this.remove();
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return ImmutableList.of();
    }

    @Override
    public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {

    }

    @Override
    public HandSide getPrimaryHand() {
        return HandSide.RIGHT;
    }

    public int getCrackAmount() {
        return this.dataManager.get(CRACK_AMOUNT);
    }

    public void setCrackAmount(int crackAmount) {
        this.dataManager.set(CRACK_AMOUNT, crackAmount);
    }


    @Override
    public boolean canBeTurnedToStone() {
        return false;
    }
}
