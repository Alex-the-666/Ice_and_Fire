package com.github.alexthe666.iceandfire.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.entity.ai.DreadAITargetNonDread;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IAnimalFear;
import com.github.alexthe666.iceandfire.entity.util.IDreadMob;
import com.github.alexthe666.iceandfire.entity.util.IVillagerFear;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class EntityDreadThrall extends EntityDreadMob implements IAnimatedEntity, IVillagerFear, IAnimalFear {

    private static final DataParameter<Boolean> CUSTOM_ARMOR_HEAD = EntityDataManager.createKey(EntityDreadThrall.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CUSTOM_ARMOR_CHEST = EntityDataManager.createKey(EntityDreadThrall.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CUSTOM_ARMOR_LEGS = EntityDataManager.createKey(EntityDreadThrall.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CUSTOM_ARMOR_FEET = EntityDataManager.createKey(EntityDreadThrall.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> CUSTOM_ARMOR_INDEX = EntityDataManager.createKey(EntityDreadThrall.class, DataSerializers.VARINT);
    public static Animation ANIMATION_SPAWN = Animation.create(40);
    private int animationTick;
    private Animation currentAnimation;

    public EntityDreadThrall(EntityType type, World worldIn) {
        super(type, worldIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, IDreadMob.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10,true,false,new Predicate<LivingEntity>() {
            @Override
            public boolean apply(@Nullable LivingEntity entity) {
                return DragonUtils.canHostilesTarget(entity);
            }
        }));
        this.targetSelector.addGoal(3, new DreadAITargetNonDread(this, LivingEntity.class, false, new Predicate<LivingEntity>() {
            @Override
            public boolean apply(LivingEntity entity) {
                return entity instanceof LivingEntity && DragonUtils.canHostilesTarget(entity);
            }
        }));
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, 20.0D)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2D)
                //ATTACK
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D)
                //FOLLOW RANGE
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 128.0D)
                //ARMOR
                .createMutableAttribute(Attributes.ARMOR, 2.0D);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(CUSTOM_ARMOR_INDEX, Integer.valueOf(0));
        this.dataManager.register(CUSTOM_ARMOR_HEAD, Boolean.valueOf(false));
        this.dataManager.register(CUSTOM_ARMOR_CHEST, Boolean.valueOf(false));
        this.dataManager.register(CUSTOM_ARMOR_LEGS, Boolean.valueOf(false));
        this.dataManager.register(CUSTOM_ARMOR_FEET, Boolean.valueOf(false));
    }

    public void livingTick() {
        super.livingTick();
        if (this.getAnimation() == ANIMATION_SPAWN && this.getAnimationTick() < 30) {
            BlockState belowBlock = world.getBlockState(this.getPosition().down());
            if (belowBlock.getBlock() != Blocks.AIR) {
                for (int i = 0; i < 5; i++) {
                    this.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, belowBlock), this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getBoundingBox().minY, this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D);
                }
            }
            this.setMotion(0, this.getMotion().y, 0);
        }
        if (this.getHeldItemMainhand().getItem() == Items.BOW) {
            this.setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.BONE));
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        super.setEquipmentBasedOnDifficulty(difficulty);
        if (rand.nextFloat() < 0.75F) {
            double chance = rand.nextFloat();
            if (chance < 0.0025F) {
                this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(IafItemRegistry.DRAGONSTEEL_ICE_SWORD));
            }
            if (chance < 0.01F) {
                this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
            }
            if (chance < 0.1F) {
                this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_SWORD));
            }
            if (chance < 0.75F) {
                this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(IafItemRegistry.DREAD_SWORD));
            }
        }
        if (rand.nextFloat() < 0.75F) {
            this.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
            setCustomArmorHead(rand.nextInt(8) != 0);
        }
        if (rand.nextFloat() < 0.75F) {
            this.setItemStackToSlot(EquipmentSlotType.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
            setCustomArmorChest(rand.nextInt(8) != 0);
        }
        if (rand.nextFloat() < 0.75F) {
            this.setItemStackToSlot(EquipmentSlotType.LEGS, new ItemStack(Items.CHAINMAIL_LEGGINGS));
            setCustomArmorLegs(rand.nextInt(8) != 0);
        }
        if (rand.nextFloat() < 0.75F) {
            this.setItemStackToSlot(EquipmentSlotType.FEET, new ItemStack(Items.CHAINMAIL_BOOTS));
            setCustomArmorFeet(rand.nextInt(8) != 0);
        }
        setArmorVariant(rand.nextInt(8));
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        ILivingEntityData data = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setAnimation(ANIMATION_SPAWN);
        this.setEquipmentBasedOnDifficulty(difficultyIn);
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

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("ArmorVariant", getArmorVariant());
        compound.putBoolean("HasCustomHelmet", hasCustomArmorHead());
        compound.putBoolean("HasCustomChestplate", hasCustomArmorChest());
        compound.putBoolean("HasCustomLeggings", hasCustomArmorLegs());
        compound.putBoolean("HasCustomBoots", hasCustomArmorFeet());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        setArmorVariant(compound.getInt("ArmorVariant"));
        setCustomArmorHead(compound.getBoolean("HasCustomHelmet"));
        setCustomArmorChest(compound.getBoolean("HasCustomChestplate"));
        setCustomArmorLegs(compound.getBoolean("HasCustomLeggings"));
        setCustomArmorFeet(compound.getBoolean("HasCustomBoots"));
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    public boolean hasCustomArmorHead() {
        return this.dataManager.get(CUSTOM_ARMOR_HEAD).booleanValue();
    }

    public void setCustomArmorHead(boolean head) {
        this.dataManager.set(CUSTOM_ARMOR_HEAD, head);
    }

    public boolean hasCustomArmorChest() {
        return this.dataManager.get(CUSTOM_ARMOR_CHEST).booleanValue();
    }

    public void setCustomArmorChest(boolean head) {
        this.dataManager.set(CUSTOM_ARMOR_CHEST, head);
    }

    public boolean hasCustomArmorLegs() {
        return this.dataManager.get(CUSTOM_ARMOR_LEGS).booleanValue();
    }

    public void setCustomArmorLegs(boolean head) {
        this.dataManager.set(CUSTOM_ARMOR_LEGS, head);
    }

    public boolean hasCustomArmorFeet() {
        return this.dataManager.get(CUSTOM_ARMOR_FEET).booleanValue();
    }

    public void setCustomArmorFeet(boolean head) {
        this.dataManager.set(CUSTOM_ARMOR_FEET, head);
    }

    public int getArmorVariant() {
        return this.dataManager.get(CUSTOM_ARMOR_INDEX).intValue();
    }

    public void setArmorVariant(int variant) {
        this.dataManager.set(CUSTOM_ARMOR_INDEX, variant);
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_SPAWN};
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return true;
    }

    @Override
    public boolean shouldFear() {
        return true;
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_STRAY_AMBIENT;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_STRAY_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_STRAY_DEATH;
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_STRAY_STEP, 0.15F, 1.0F);
    }

}