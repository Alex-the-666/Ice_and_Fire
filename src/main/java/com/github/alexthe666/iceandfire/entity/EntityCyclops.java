package com.github.alexthe666.iceandfire.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.api.event.GenericGriefEvent;
import com.github.alexthe666.iceandfire.entity.ai.CyclopsAIAttackMelee;
import com.github.alexthe666.iceandfire.entity.ai.CyclopsAITargetSheepPlayers;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IBlacklistedFromStatues;
import com.github.alexthe666.iceandfire.entity.util.IHumanoid;
import com.github.alexthe666.iceandfire.entity.util.IVillagerFear;
import com.github.alexthe666.iceandfire.event.ServerEvents;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.github.alexthe666.iceandfire.misc.IafTagRegistry;
import com.github.alexthe666.iceandfire.pathfinding.PathNavigateCyclops;
import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.FleeSunGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RestrictSunGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class EntityCyclops extends MonsterEntity implements IAnimatedEntity, IBlacklistedFromStatues, IVillagerFear, IHumanoid {

    private static final DataParameter<Boolean> BLINDED = EntityDataManager.createKey(EntityCyclops.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityCyclops.class, DataSerializers.VARINT);
    public static Animation ANIMATION_STOMP;
    public static Animation ANIMATION_EATPLAYER;
    public static Animation ANIMATION_KICK;
    public static Animation ANIMATION_ROAR;
    public EntityCyclopsEye eyeEntity;
    private int animationTick;
    private Animation currentAnimation;

    public EntityCyclops(EntityType type, World worldIn) {
        super(type, worldIn);
        this.stepHeight = 2.5F;
        this.setPathPriority(PathNodeType.WATER, -1.0F);
        this.setPathPriority(PathNodeType.FENCE, 0.0F);
        ANIMATION_STOMP = Animation.create(27);
        ANIMATION_EATPLAYER = Animation.create(40);
        ANIMATION_KICK = Animation.create(20);
        ANIMATION_ROAR = Animation.create(30);

    }

    protected PathNavigator createNavigator(World worldIn) {
        return new PathNavigateCyclops(this, world);
    }


    protected int getExperiencePoints(PlayerEntity player) {
        return 40;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new CyclopsAIAttackMelee(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F, 1.0F));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, true, true, new Predicate<LivingEntity>() {
            @Override
            public boolean apply(@Nullable LivingEntity entity) {
                return !EntityGorgon.isStoneMob(entity) && DragonUtils.isAlive(entity) && !(entity instanceof WaterMobEntity) && (!(entity instanceof PlayerEntity) || ((PlayerEntity)entity).isCreative()) && !(entity instanceof EntityCyclops) && !ServerEvents.isAnimaniaSheep(entity) && !(entity instanceof AnimalEntity && !(entity instanceof WolfEntity || entity instanceof PolarBearEntity || entity instanceof EntityDragonBase)) || entity instanceof EntityGorgon || entity instanceof AbstractVillagerEntity;
            }
        }));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, PlayerEntity.class, 0, true, true, new Predicate<PlayerEntity>() {

            @Override
            public boolean apply(@Nullable PlayerEntity entity) {
                return !entity.isCreative();
            }
        }));
        this.targetSelector.addGoal(3, new CyclopsAITargetSheepPlayers(this, PlayerEntity.class, true));
    }

    protected void collideWithEntity(Entity entityIn) {
        if (!ServerEvents.isAnimaniaSheep(entityIn)) {
            entityIn.applyEntityCollision(this);
        }
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        int attackDescision = this.getRNG().nextInt(3);
        if (attackDescision == 0) {
            this.setAnimation(ANIMATION_STOMP);
            return true;
        } else if (attackDescision == 1) {
            if (!entityIn.isPassenger(this) && entityIn.getWidth() < 1.95F && !(entityIn instanceof EntityDragonBase) && !EntityTypeTags.getCollection().get(IafTagRegistry.CYCLOPS_UNLIFTABLES).contains(entityIn.getType())) {
                this.setAnimation(ANIMATION_EATPLAYER);
                entityIn.stopRiding();
                entityIn.startRiding(this, true);
            } else {
                this.setAnimation(ANIMATION_STOMP);
            }
            return true;
        } else {
            this.setAnimation(ANIMATION_KICK);
            return true;
        }
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, IafConfig.cyclopsMaxHealth)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.35D)
                //ATTACK
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, IafConfig.cyclopsAttackStrength)
                //FOLLOW RANGE
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 32D)
                //ARMOR
                .createMutableAttribute(Attributes.ARMOR, 20.0D);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(BLINDED, Boolean.valueOf(false));
        this.dataManager.register(VARIANT, Integer.valueOf(0));
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("Blind", this.isBlinded());
        compound.putInt("Variant", this.getVariant());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setBlinded(compound.getBoolean("Blind"));
        this.setVariant(compound.getInt("Variant"));
    }

    public int getVariant() {
        return Integer.valueOf(this.dataManager.get(VARIANT).intValue());
    }

    public void setVariant(int variant) {
        this.dataManager.set(VARIANT, Integer.valueOf(variant));
    }

    public boolean isBlinded() {
        return Boolean.valueOf(this.dataManager.get(BLINDED).booleanValue());
    }

    public void setBlinded(boolean blind) {
        this.dataManager.set(BLINDED, Boolean.valueOf(blind));
    }

    public void updatePassenger(Entity passenger) {
        super.updatePassenger(passenger);
        if (this.isPassenger(passenger)) {
            passenger.setMotion(0, passenger.getMotion().y, 0);
            this.setAnimation(ANIMATION_EATPLAYER);
            double raiseUp = this.getAnimationTick() < 10 ? 0 : Math.min((this.getAnimationTick() * 3 - 30) * 0.2, 5.2F);
            float pullIn = this.getAnimationTick() < 15 ? 0 : Math.min((this.getAnimationTick() - 15) * 0.15F, 0.75F);
            renderYawOffset = rotationYaw;
            this.rotationYaw *= 0;
            float radius = -2.75F + pullIn;
            float angle = (0.01745329251F * this.renderYawOffset) + 3.15F;
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            double extraY = raiseUp;
            passenger.setPosition(this.getPosX() + extraX, this.getPosY() + extraY, this.getPosZ() + extraZ);
            if (this.getAnimationTick() == 32) {
                passenger.attackEntityFrom(DamageSource.causeMobDamage(this), (float) IafConfig.cyclopsBiteStrength);
                passenger.stopRiding();
            }
        }
    }

    @Override
    public void travel(Vector3d vec) {
        if (this.getAnimation() == ANIMATION_EATPLAYER) {
            super.travel(vec.mul(0, 0, 0));
            return;
        }
        super.travel(vec);
    }

    public boolean canPassengerSteer() {
        return false;
    }

    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }


    public void livingTick() {
        super.livingTick();
        if (eyeEntity == null){
            eyeEntity = new EntityCyclopsEye(this, 0.2F, 0, 7.4F, 1.2F, 0.6F, 1);
            eyeEntity.copyLocationAndAnglesFrom(this);
        }
        if (world.getDifficulty() == Difficulty.PEACEFUL && this.getAttackTarget() instanceof PlayerEntity) {
            this.setAttackTarget(null);
        }
        if (this.isBlinded() && this.getAttackTarget() != null && this.getDistanceSq(this.getAttackTarget()) > 6) {
            this.setAttackTarget(null);
        }
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() == 5) {
            this.playSound(IafSoundRegistry.CYCLOPS_BLINDED, 1, 1);
        }
        if (this.getAnimation() == ANIMATION_EATPLAYER && this.getAnimationTick() == 25) {
            this.playSound(IafSoundRegistry.CYCLOPS_BITE, 1, 1);
        }
        if (this.getAnimation() == ANIMATION_STOMP && this.getAttackTarget() != null && this.getDistanceSq(this.getAttackTarget()) < 12D && this.getAnimationTick() == 14) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
        }
        if (this.getAnimation() == ANIMATION_KICK && this.getAttackTarget() != null && this.getDistanceSq(this.getAttackTarget()) < 14D && this.getAnimationTick() == 12) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
            this.getAttackTarget().applyKnockback(2, this.getPosX() - this.getAttackTarget().getPosX(), this.getPosZ() - this.getAttackTarget().getPosZ());

        }
        if (this.getAnimation() != ANIMATION_EATPLAYER && this.getAttackTarget() != null && !this.getPassengers().isEmpty() && this.getPassengers().contains(this.getAttackTarget())) {
            this.setAnimation(ANIMATION_EATPLAYER);
        }
        if (this.getAnimation() == NO_ANIMATION && this.getAttackTarget() != null && this.getRNG().nextInt(100) == 0) {
            this.setAnimation(ANIMATION_ROAR);
        }
        if (this.getAnimation() == ANIMATION_STOMP && this.getAnimationTick() == 14) {
            for (int i1 = 0; i1 < 20; i1++) {
                double motionX = getRNG().nextGaussian() * 0.07D;
                double motionY = getRNG().nextGaussian() * 0.07D;
                double motionZ = getRNG().nextGaussian() * 0.07D;
                float radius = 0.75F * -2F;
                float angle = (0.01745329251F * this.renderYawOffset) + i1 * 1F;
                double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                double extraY = 0.8F;
                double extraZ = radius * MathHelper.cos(angle);

                BlockState BlockState = this.world.getBlockState(new BlockPos(MathHelper.floor(this.getPosX() + extraX), MathHelper.floor(this.getPosY() + extraY) - 1, MathHelper.floor(this.getPosZ() + extraZ)));
                if (BlockState.getMaterial() != Material.AIR) {
                    if (world.isRemote) {
                        world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, BlockState), this.getPosX() + extraX, this.getPosY() + extraY, this.getPosZ() + extraZ, motionX, motionY, motionZ);
                    }
                }
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
        if(eyeEntity == null){
            eyeEntity = new EntityCyclopsEye(this, 0.2F, 0, 7.4F, 1.2F, 0.5F, 1);
            eyeEntity.copyLocationAndAnglesFrom(this);
            eyeEntity.setParent(this);

        }
        if(!eyeEntity.shouldContinuePersisting()){
            world.addEntity(eyeEntity);
        }
        eyeEntity.setParent(this);
        breakBlock();
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setVariant(this.getRNG().nextInt(4));
        return spawnDataIn;
    }

    public void breakBlock() {
        if (IafConfig.cyclopsGriefing) {
            for (int a = (int) Math.round(this.getBoundingBox().minX) - 1; a <= (int) Math.round(this.getBoundingBox().maxX) + 1; a++) {
                for (int b = (int) Math.round(this.getBoundingBox().minY) + 1; (b <= (int) Math.round(this.getBoundingBox().maxY) + 2) && (b <= 127); b++) {
                    for (int c = (int) Math.round(this.getBoundingBox().minZ) - 1; c <= (int) Math.round(this.getBoundingBox().maxZ) + 1; c++) {
                        BlockPos pos = new BlockPos(a, b, c);
                        BlockState state = world.getBlockState(pos);
                        Block block = state.getBlock();
                        if (!state.isAir() && !state.getShape(world, pos).isEmpty() && !(block instanceof BushBlock) && block != Blocks.BEDROCK && (state.getBlock() instanceof LeavesBlock || BlockTags.LOGS.contains(state.getBlock()))) {
                            this.getMotion().scale(0.6D);
                            if (MinecraftForge.EVENT_BUS.post(new GenericGriefEvent(this, a, b, c))) continue;
                            if (block != Blocks.AIR) {
                                if (!world.isRemote) {
                                    world.destroyBlock(pos, true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    public void remove() {
        if (eyeEntity != null) {
            eyeEntity.remove();
        }
        super.remove();
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
        return new Animation[]{NO_ANIMATION, ANIMATION_STOMP, ANIMATION_EATPLAYER, ANIMATION_KICK, ANIMATION_ROAR};
    }

    public boolean isBlinking() {
        return this.ticksExisted % 50 > 40 && !this.isBlinded();
    }


    public void onHitEye(DamageSource source, float damage) {
        if (!this.isBlinded()) {
            this.setBlinded(true);
            this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(6F);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35D);
            this.setAnimation(ANIMATION_ROAR);
            this.attackEntityFrom(source, damage * 3);
        }
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return IafSoundRegistry.CYCLOPS_IDLE;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return IafSoundRegistry.CYCLOPS_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return IafSoundRegistry.CYCLOPS_DIE;
    }

    @Override
    public boolean canBeTurnedToStone() {
        return !this.isBlinded();
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
