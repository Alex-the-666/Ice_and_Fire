package com.github.alexthe666.iceandfire.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IAnimalFear;
import com.github.alexthe666.iceandfire.entity.util.IBlacklistedFromStatues;
import com.github.alexthe666.iceandfire.entity.util.IMultipartEntity;
import com.github.alexthe666.iceandfire.entity.util.IVillagerFear;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityHydra extends MonsterEntity implements IAnimatedEntity, IMultipartEntity, IVillagerFear, IAnimalFear {

    public static final int HEADS = 9;
    public static final double HEAD_HEALTH_THRESHOLD = 20;
    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityHydra.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HEAD_COUNT = EntityDataManager.createKey(EntityHydra.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> SEVERED_HEAD = EntityDataManager.createKey(EntityHydra.class, DataSerializers.VARINT);
    private static final float[][] ROTATE = new float[][]{
            {0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F},// 1 total heads
            {10F, -10F, 0F, 0F, 0F, 0F, 0F, 0F, 0F},// 2 total heads
            {10F, 0F, -10F, 0F, 0F, 0F, 0F, 0F, 0F},// 3 total heads
            {25F, 10F, -10F, -25F, 0F, 0F, 0F, 0F, 0F},//etc...
            {30F, 15F, 0F, -15F, -30F, 0F, 0F, 0F, 0F},
            {40F, 25F, 5F, -5F, -25F, -40F, 0F, 0F, 0F},
            {40F, 30F, 15F, 0F, -15F, -30F, -40F, 0F, 0F},
            {45F, 30F, 20F, 5F, -5F, -20F, -30F, -45F, 0F},
            {50F, 37F, 25F, 15F, 0, -15F, -25F, -37F, -50F},
    };
    public boolean[] isStriking = new boolean[HEADS];
    public float[] strikingProgress = new float[HEADS];
    public float[] prevStrikeProgress = new float[HEADS];
    public boolean[] isBreathing = new boolean[HEADS];
    public float[] speakingProgress = new float[HEADS];
    public float[] prevSpeakingProgress = new float[HEADS];
    public float[] breathProgress = new float[HEADS];
    public float[] prevBreathProgress = new float[HEADS];
    public int[] breathTicks = new int[HEADS];
    public float[] headDamageTracker = new float[HEADS];
    private int animationTick;
    private Animation currentAnimation;
    private EntityHydraHead[] headBoxes = new EntityHydraHead[HEADS * 9];
    private int strikeCooldown = 0;
    private int breathCooldown = 0;
    private int lastHitHead = 0;
    private int prevHeadCount = -1;
    private int regrowHeadCooldown = 0;
    private boolean onlyRegrowOneHeadNotTwo = false;
    private float headDamageThreshold = 20;

    public EntityHydra(EntityType type, World worldIn) {
        super(type, worldIn);
        resetParts();
        headDamageThreshold = Math.max(5, (float) IafConfig.hydraMaxHealth * 0.08F);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, PlayerEntity.class, 0, true, false, new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity entity) {
                return entity.isAlive();
            }
        }));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, LivingEntity.class, 0, true, false, new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity entity) {
                return entity instanceof LivingEntity && DragonUtils.isAlive((LivingEntity) entity) && !(entity instanceof EntityMutlipartPart) && !(entity instanceof IMob) || (entity instanceof IBlacklistedFromStatues && ((IBlacklistedFromStatues) entity).canBeTurnedToStone());
            }
        }));
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        return false;
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (this.getAttackTarget() != null && this.canEntityBeSeen(this.getAttackTarget())) {
            int index = rand.nextInt(getHeadCount());
            if (!isBreathing[index] && !isStriking[index]) {
                if (this.getDistance(this.getAttackTarget()) < 6) {
                    if (strikeCooldown == 0 && strikingProgress[index] == 0) {
                        isBreathing[index] = false;
                        isStriking[index] = true;
                        this.world.setEntityState(this, (byte) (40 + index));
                        strikeCooldown = 3;
                    }
                } else if (rand.nextBoolean() && breathCooldown == 0) {
                    isBreathing[index] = true;
                    isStriking[index] = false;
                    this.world.setEntityState(this, (byte) (50 + index));
                    breathCooldown = 15;
                }

            }

        }
        for (int i = 0; i < HEADS; i++) {
            boolean striking = isStriking[i];
            boolean breathing = isBreathing[i];
            prevStrikeProgress[i] = strikingProgress[i];
            if (striking && strikingProgress[i] > 9) {
                isStriking[i] = false;
                if (this.getAttackTarget() != null && this.getDistance(this.getAttackTarget()) < 6) {
                    this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                    this.getAttackTarget().addPotionEffect(new EffectInstance(Effects.POISON, 100, 3, false, false));
                    this.getAttackTarget().applyKnockback(0.25F, this.getPosX() - this.getAttackTarget().getPosX(), this.getPosZ() - this.getAttackTarget().getPosZ());
                }
            }
            if (breathing) {
                LivingEntity entity = this.getAttackTarget();
                if (ticksExisted % 7 == 0 && entity != null && i < this.getHeadCount()) {
                    Vector3d Vector3d = this.getLook(1.0F);
                    if (rand.nextFloat() < 0.2F) {
                        this.playSound(IafSoundRegistry.HYDRA_SPIT, this.getSoundVolume(), this.getSoundPitch());
                    }
                    double headPosX = this.headBoxes[i].getPosX() + Vector3d.x * 1.0D;
                    double headPosY = this.headBoxes[i].getPosY() + 1.3F;
                    double headPosZ = this.headBoxes[i].getPosZ() + Vector3d.z * 1.0D;
                    double d2 = entity.getPosX() - headPosX + this.rand.nextGaussian() * 0.4D;
                    double d3 = entity.getPosY() + entity.getEyeHeight() - headPosY + this.rand.nextGaussian() * 0.4D;
                    double d4 = entity.getPosZ() - headPosZ + this.rand.nextGaussian() * 0.4D;
                    EntityHydraBreath entitylargefireball = new EntityHydraBreath(IafEntityRegistry.HYDRA_BREATH, world, this, d2, d3, d4);
                    entitylargefireball.setPosition(headPosX, headPosY, headPosZ);
                    if (!world.isRemote) {
                        world.addEntity(entitylargefireball);
                    }
                }
                if (isBreathing[i] && (entity == null || !entity.isAlive() || breathTicks[i] > 60) && !world.isRemote) {
                    isBreathing[i] = false;
                    breathTicks[i] = 0;
                    breathCooldown = 15;
                    this.world.setEntityState(this, (byte) (60 + i));
                }
                breathTicks[i]++;
            } else {
                breathTicks[i] = 0;
            }
            if (striking && strikingProgress[i] < 10.0F) {
                strikingProgress[i] += 2.5F;
            } else if (!striking && strikingProgress[i] > 0.0F) {
                strikingProgress[i] -= 2.5F;
            }
            prevSpeakingProgress[i] = speakingProgress[i];
            if (speakingProgress[i] > 0.0F) {
                speakingProgress[i] -= 0.1F;
            }
            prevBreathProgress[i] = breathProgress[i];
            if (breathing && breathProgress[i] < 10.0F) {
                breathProgress[i] += 1.0F;
            } else if (!breathing && breathProgress[i] > 0.0F) {
                breathProgress[i] -= 1.0F;
            }

        }
        if (strikeCooldown > 0) {
            strikeCooldown--;
        }
        if (breathCooldown > 0) {
            breathCooldown--;
        }
        if (this.getHeadCount() == 1 && this.getSeveredHead() != -1) {
            this.setSeveredHead(-1);
        }
        if (this.getHeadCount() == 1 && !this.isBurning()) {
            this.setHeadCount(2);
            this.setSeveredHead(1);
            onlyRegrowOneHeadNotTwo = true;
        }

        if (this.getSeveredHead() != -1 && this.getSeveredHead() < this.getHeadCount()) {
            this.setSeveredHead(MathHelper.clamp(this.getSeveredHead(), 0, this.getHeadCount() - 1));
            regrowHeadCooldown++;
            if (regrowHeadCooldown >= 100) {
                headDamageTracker[this.getSeveredHead()] = 0;
                this.setSeveredHead(-1);
                if (this.isBurning()) {
                    this.setHeadCount(this.getHeadCount() - 1);
                } else {
                    this.playSound(IafSoundRegistry.HYDRA_REGEN_HEAD, this.getSoundVolume(), this.getSoundPitch());
                    if (!onlyRegrowOneHeadNotTwo) {
                        this.setHeadCount(this.getHeadCount() + 1);
                    }
                }
                onlyRegrowOneHeadNotTwo = false;
                regrowHeadCooldown = 0;
            }
        } else {
            regrowHeadCooldown = 0;
        }
    }

    public void resetParts() {
        clearParts();
        headBoxes = new EntityHydraHead[HEADS * 2];
        for (int i = 0; i < getHeadCount(); i++) {
            float maxAngle = 5;
            headBoxes[i] = new EntityHydraHead(this, 3.2F, ROTATE[getHeadCount() - 1][i] * 1.1F, 1.0F, 0.75F, 1.75F, 1, i, false);
            headBoxes[HEADS + i] = new EntityHydraHead(this, 2.1F, ROTATE[getHeadCount() - 1][i] * 1.1F, 1.0F, 0.75F, 0.75F, 1, i, true);
            headBoxes[i].copyLocationAndAnglesFrom(this);
            headBoxes[HEADS + i].copyLocationAndAnglesFrom(this);
            headBoxes[i].setParent(this);
            headBoxes[HEADS + i].setParent(this);
        }
    }


    @Override
    public void tick() {
        super.tick();
        if (prevHeadCount != this.getHeadCount()) {
            resetParts();
        }
        onUpdateParts();
        float partY = 1.0F - limbSwingAmount * 0.5F;
        for (int i = 0; i < getHeadCount(); i++) {
            headBoxes[i].setPosition(headBoxes[i].getPosX(), this.getPosY() + partY, headBoxes[i].getPosZ());
            headBoxes[i].setParent(this);
            if(!headBoxes[i].shouldContinuePersisting()){
                world.addEntity(headBoxes[i]);
            }
            headBoxes[HEADS + i].setPosition(headBoxes[HEADS + i].getPosX(), this.getPosY() + partY, headBoxes[HEADS + i].getPosZ());
            headBoxes[HEADS + i].setParent(this);
            if(!headBoxes[HEADS + i].shouldContinuePersisting()){
                world.addEntity(headBoxes[HEADS + i]);
            }
        }
        if (getHeadCount() > 1 && !isBurning()) {
            if (this.getHealth() < this.getMaxHealth() && this.ticksExisted % 30 == 0) {
                int level = getHeadCount() - 1;
                if (this.getSeveredHead() != -1) {
                    level--;
                }
                this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 30, level, false, false));
            }
        }
        if (isBurning()) {
            this.removePotionEffect(Effects.REGENERATION);
        }

        prevHeadCount = this.getHeadCount();
    }

    public void onUpdateParts() {
    }

    private void clearParts() {
        for (Entity entity : headBoxes) {
            if (entity != null) {
                entity.remove();
            }
        }
    }

    public void remove() {
        clearParts();
        super.remove();
    }

    protected void playHurtSound(DamageSource source) {
        speakingProgress[rand.nextInt(getHeadCount())] = 1F;
        super.playHurtSound(source);
    }

    public void playAmbientSound() {
        speakingProgress[rand.nextInt(getHeadCount())] = 1F;
        super.playAmbientSound();
    }

    public int getTalkInterval() {
        return 100 / getHeadCount();
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putInt("HeadCount", this.getHeadCount());
        compound.putInt("SeveredHead", this.getSeveredHead());
        for (int i = 0; i < HEADS; i++) {
            compound.putFloat("HeadDamage" + i, headDamageTracker[i]);
        }
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setVariant(compound.getInt("Variant"));
        this.setHeadCount(compound.getInt("HeadCount"));
        this.setSeveredHead(compound.getInt("SeveredHead"));
        for (int i = 0; i < HEADS; i++) {
            headDamageTracker[i] = compound.getFloat("HeadDamage" + i);
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(VARIANT, Integer.valueOf(0));
        this.dataManager.register(HEAD_COUNT, Integer.valueOf(3));
        this.dataManager.register(SEVERED_HEAD, Integer.valueOf(-1));
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, IafConfig.hydraMaxHealth)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D)
                //ATTACK
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D)
                //ARMOR
                .createMutableAttribute(Attributes.ARMOR, 1.0D);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (lastHitHead > this.getHeadCount()) {
            lastHitHead = this.getHeadCount() - 1;
        }
        int headIndex = lastHitHead;
        headDamageTracker[headIndex] += amount;

        if (headDamageTracker[headIndex] > headDamageThreshold && (this.getSeveredHead() == -1 || this.getSeveredHead() >= this.getHeadCount())) {
            headDamageTracker[headIndex] = 0;
            this.regrowHeadCooldown = 0;
            this.setSeveredHead(headIndex);
            this.playSound(SoundEvents.ENTITY_GUARDIAN_FLOP, this.getSoundVolume(), this.getSoundPitch());
        }
        if (this.getHealth() <= amount + 5 && this.getHeadCount() > 1 && !source.canHarmInCreative()) {
            amount = 0;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        ILivingEntityData data = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setVariant(rand.nextInt(3));
        return data;
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{};
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return true;
    }

    @Override
    public boolean isNoDespawnRequired() {
        return true;
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    public int getVariant() {
        return this.dataManager.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.dataManager.set(VARIANT, variant);
    }

    public int getHeadCount() {
        return MathHelper.clamp(this.dataManager.get(HEAD_COUNT).intValue(), 1, HEADS);
    }

    public void setHeadCount(int count) {
        this.dataManager.set(HEAD_COUNT, MathHelper.clamp(count, 1, HEADS));
    }

    public int getSeveredHead() {
        return MathHelper.clamp(this.dataManager.get(SEVERED_HEAD).intValue(), -1, HEADS);
    }

    public void setSeveredHead(int count) {
        this.dataManager.set(SEVERED_HEAD, MathHelper.clamp(count, -1, HEADS));
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id >= 40 && id <= 48) {
            int index = id - 40;
            isStriking[MathHelper.clamp(index, 0, 8)] = true;
        } else if (id >= 50 && id <= 58) {
            int index = id - 50;
            isBreathing[MathHelper.clamp(index, 0, 8)] = true;
        } else if (id >= 60 && id <= 68) {
            int index = id - 60;
            isBreathing[MathHelper.clamp(index, 0, 8)] = false;
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public boolean isPotionApplicable(EffectInstance potioneffectIn) {
        return potioneffectIn.getPotion() != Effects.POISON && super.isPotionApplicable(potioneffectIn);
    }

    public void onHitHead(float damage, int headIndex) {
        lastHitHead = headIndex;
    }

    public void triggerHeadFlags(int index) {
        lastHitHead = index;
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return IafSoundRegistry.HYDRA_IDLE;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return IafSoundRegistry.HYDRA_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return IafSoundRegistry.HYDRA_DIE;
    }

}
