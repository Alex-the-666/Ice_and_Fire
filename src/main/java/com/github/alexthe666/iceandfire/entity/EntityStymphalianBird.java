package com.github.alexthe666.iceandfire.entity;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.ai.StymphalianBirdAIAirTarget;
import com.github.alexthe666.iceandfire.entity.ai.StymphalianBirdAIFlee;
import com.github.alexthe666.iceandfire.entity.ai.StymphalianBirdAITarget;
import com.github.alexthe666.iceandfire.entity.util.IAnimalFear;
import com.github.alexthe666.iceandfire.entity.util.IVillagerFear;
import com.github.alexthe666.iceandfire.entity.util.StymphalianBirdFlock;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.google.common.base.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
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
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class EntityStymphalianBird extends MonsterEntity implements IAnimatedEntity, IMob, IVillagerFear, IAnimalFear {

    public static final Predicate<Entity> STYMPHALIAN_PREDICATE = new Predicate<Entity>() {
        public boolean apply(@Nullable Entity entity) {
            return entity instanceof EntityStymphalianBird;
        }
    };
    private static final int FLIGHT_CHANCE_PER_TICK = 100;
    private static final DataParameter<Optional<UUID>> VICTOR_ENTITY = EntityDataManager.createKey(EntityStymphalianBird.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityStymphalianBird.class, DataSerializers.BOOLEAN);
    public static Animation ANIMATION_PECK = Animation.create(20);
    public static Animation ANIMATION_SHOOT_ARROWS = Animation.create(30);
    public static Animation ANIMATION_SPEAK = Animation.create(10);
    public float flyProgress;
    public BlockPos airTarget;
    public StymphalianBirdFlock flock;
    private int animationTick;
    private Animation currentAnimation;
    private LivingEntity victorEntity;
    private boolean isFlying;
    private int flyTicks;
    private int launchTicks;
    private boolean aiFlightLaunch = false;
    private int airBorneCounter;

    public EntityStymphalianBird(EntityType t, World worldIn) {
        super(t, worldIn);
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new StymphalianBirdAIFlee(this, 10));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.5D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new StymphalianBirdAIAirTarget(this));
        this.goalSelector.addGoal(7, new LookAtGoal(this, LivingEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new StymphalianBirdAITarget(this, LivingEntity.class, true));
    }


    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D)
                //ATTACK
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, IafConfig.myrmexBaseAttackStrength * 2D)
                //FOLLOW RANGE
                .createMutableAttribute(Attributes.FOLLOW_RANGE, Math.min(2048, IafConfig.stymphalianBirdTargetSearchLength))
                //ARMOR
                .createMutableAttribute(Attributes.ARMOR, 4.0D);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(VICTOR_ENTITY, Optional.empty());
        this.dataManager.register(FLYING, Boolean.valueOf(false));
    }

    protected int getExperiencePoints(PlayerEntity player) {
        return 10;
    }

    public void tick() {
        super.tick();
        if (!this.world.isRemote && this.world.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
        }
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        if (this.getVictorId() != null) {
            tag.putUniqueId("VictorUUID", this.getVictorId());
        }
        tag.putBoolean("Flying", this.isFlying());
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        UUID s;

        if (tag.hasUniqueId("VictorUUID")) {
            s = tag.getUniqueId("VictorUUID");
        } else {
            String s1 = tag.getString("VictorUUID");
            s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
        }

        if (s != null) {
            try {
                this.setVictorId(s);
            } catch (Throwable var4) {
            }
        }
        this.setFlying(tag.getBoolean("Flying"));
    }

    public boolean isFlying() {
        if (world.isRemote) {
            return this.isFlying = this.dataManager.get(FLYING).booleanValue();
        }
        return isFlying;
    }

    public void setFlying(boolean flying) {
        this.dataManager.set(FLYING, flying);
        if (!world.isRemote) {
            this.isFlying = flying;
        }
    }

    public void onDeath(DamageSource cause) {
        if (cause.getTrueSource() != null && cause.getTrueSource() instanceof LivingEntity && !world.isRemote) {
            this.setVictorId(cause.getTrueSource().getUniqueID());
            if (this.flock != null) {
                this.flock.setFearTarget((LivingEntity) cause.getTrueSource());
            }
        }
        super.onDeath(cause);
    }

    protected void onDeathUpdate() {
        super.onDeathUpdate();
    }

    @Nullable
    public UUID getVictorId() {
        return (UUID) ((Optional) this.dataManager.get(VICTOR_ENTITY)).orElse(null);
    }

    public void setVictorId(@Nullable UUID uuid) {
        this.dataManager.set(VICTOR_ENTITY, Optional.ofNullable(uuid));
    }

    @Nullable
    public LivingEntity getVictor() {
        try {
            UUID uuid = this.getVictorId();
            return uuid == null ? null : this.world.getPlayerByUuid(uuid);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    public void setVictor(LivingEntity player) {
        this.setVictorId(player.getUniqueID());
    }

    public boolean isVictor(LivingEntity entityIn) {
        return entityIn == this.getVictor();
    }

    public boolean isTargetBlocked(Vector3d target) {
        return world.rayTraceBlocks(new RayTraceContext(target, this.getEyePosition(1.0F), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() == RayTraceResult.Type.MISS;
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.getAnimation() == NO_ANIMATION) {
            this.setAnimation(ANIMATION_PECK);
        }
        return true;
    }


    @Override
    public void livingTick() {
        super.livingTick();
        if (world.getDifficulty() == Difficulty.PEACEFUL && this.getAttackTarget() instanceof PlayerEntity) {
            this.setAttackTarget(null);
        }
        if (this.getAttackTarget() != null && (this.getAttackTarget() instanceof PlayerEntity && ((PlayerEntity) this.getAttackTarget()).isCreative() || this.getVictor() != null && this.isVictor(this.getAttackTarget()))) {
            this.setAttackTarget(null);
        }
        if (this.flock == null) {
            StymphalianBirdFlock otherFlock = StymphalianBirdFlock.getNearbyFlock(this);
            if (otherFlock == null) {
                this.flock = StymphalianBirdFlock.createFlock(this);
            } else {
                this.flock = otherFlock;
                this.flock.addToFlock(this);
            }
        } else {
            if (!this.flock.isLeader(this)) {
                double dist = this.getDistanceSq(this.flock.getLeader());
                if (dist > 360) {
                    this.setFlying(true);
                    this.navigator.clearPath();
                    this.airTarget = StymphalianBirdAIAirTarget.getNearbyAirTarget(this.flock.getLeader());
                    this.aiFlightLaunch = false;
                } else if (!this.flock.getLeader().isFlying()) {
                    this.setFlying(false);
                    this.airTarget = null;
                    this.aiFlightLaunch = false;
                }
                if (this.isOnGround() && dist < 40 && this.getAnimation() != ANIMATION_SHOOT_ARROWS) {
                    this.setFlying(false);
                }
            }
            this.flock.update();
        }
        if (!world.isRemote && this.getAttackTarget() != null && this.getAttackTarget().isAlive()) {
            double dist = this.getDistanceSq(this.getAttackTarget());
            if (this.getAnimation() == ANIMATION_PECK && this.getAnimationTick() == 7) {
                if (dist < 1.5F) {
                    this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
                }
                if (onGround) {
                    this.setFlying(false);
                }
            }
            if (this.getAnimation() != ANIMATION_PECK && this.getAnimation() != ANIMATION_SHOOT_ARROWS && dist > 3 && dist < 225) {
                this.setAnimation(ANIMATION_SHOOT_ARROWS);
            }
            if (this.getAnimation() == ANIMATION_SHOOT_ARROWS) {
                LivingEntity target = this.getAttackTarget();
                this.faceEntity(target, 360, 360);
                if (this.isFlying()) {
                    rotationYaw = renderYawOffset;
                    if ((this.getAnimationTick() == 7 || this.getAnimationTick() == 14) && isDirectPathBetweenPoints(this, this.getPositionVec(), target.getPositionVec())) {
                        this.playSound(IafSoundRegistry.STYMPHALIAN_BIRD_ATTACK, 1, 1);
                        for (int i = 0; i < 4; i++) {
                            float wingX = (float) (getPosX() + 1.8F * 0.5F * Math.cos((rotationYaw + 180 * (i % 2)) * Math.PI / 180));
                            float wingZ = (float) (getPosZ() + 1.8F * 0.5F * Math.sin((rotationYaw + 180 * (i % 2)) * Math.PI / 180));
                            float wingY = (float) (getPosY() + 1F);
                            double d0 = target.getPosX() - wingX;
                            double d1 = target.getBoundingBox().minY - wingY;
                            double d2 = target.getPosZ() - wingZ;
                            double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
                            EntityStymphalianFeather entityarrow = new EntityStymphalianFeather(IafEntityRegistry.STYMPHALIAN_FEATHER, world, this);
                            entityarrow.setPosition(wingX, wingY, wingZ);
                            entityarrow.shoot(d0, d1 + d3 * 0.10000000298023224D, d2, 1.6F, (float) (14 - this.world.getDifficulty().getId() * 4));
                            this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
                            this.world.addEntity(entityarrow);
                        }
                    }
                } else {
                    this.setFlying(true);
                }
            }
        }
        boolean flying = this.isFlying() && !this.isOnGround() || airBorneCounter > 10 || this.getAnimation() == ANIMATION_SHOOT_ARROWS;
        if (flying && flyProgress < 20.0F) {
            flyProgress += 1F;
        } else if (!flying && flyProgress > 0.0F) {
            flyProgress -= 1F;
        }
        if (!this.isFlying() && this.airTarget != null && this.isOnGround() && !world.isRemote) {
            this.airTarget = null;
        }
        if (this.isFlying() && getAttackTarget() == null) {
            flyAround();
        } else if (getAttackTarget() != null) {
            flyTowardsTarget();
        }
        if (!world.isRemote && this.doesWantToLand() && !aiFlightLaunch && this.getAnimation() != ANIMATION_SHOOT_ARROWS) {
            this.setFlying(false);
            this.airTarget = null;
        }
        if (!world.isRemote && this.isOffsetPositionInLiquid(0, 0, 0) && !this.isFlying()) {
            this.setFlying(true);
            this.launchTicks = 0;
            this.flyTicks = 0;
            this.aiFlightLaunch = true;
        }
        if (!world.isRemote && this.isOnGround() && this.isFlying() && !aiFlightLaunch && this.getAnimation() != ANIMATION_SHOOT_ARROWS) {
            this.setFlying(false);
            this.airTarget = null;
        }
        if (!world.isRemote && (this.flock == null || this.flock != null && this.flock.isLeader(this)) && this.getRNG().nextInt(FLIGHT_CHANCE_PER_TICK) == 0 && !this.isFlying() && this.getPassengers().isEmpty() && !this.isChild() && this.onGround) {
            this.setFlying(true);
            this.launchTicks = 0;
            this.flyTicks = 0;
            this.aiFlightLaunch = true;
        }
        if (!world.isRemote) {
            if (aiFlightLaunch && this.launchTicks < 40) {
                this.launchTicks++;
            } else {
                this.launchTicks = 0;
                aiFlightLaunch = false;
            }
            if (this.isFlying()) {
                this.flyTicks++;
            } else {
                this.flyTicks = 0;
            }
        }
        if (!this.onGround) {
            airBorneCounter++;
        } else {
            airBorneCounter = 0;
        }
        if (this.getAnimation() == ANIMATION_SHOOT_ARROWS && !this.isFlying() && !world.isRemote) {
            this.setFlying(true);
            aiFlightLaunch = true;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean isDirectPathBetweenPoints(Entity entity, Vector3d vec1, Vector3d vec2) {
        return world.rayTraceBlocks(new RayTraceContext(vec1, vec2, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() == RayTraceResult.Type.MISS;
    }

    private boolean isLeaderNotFlying() {
        return this.flock != null && this.flock.getLeader() != null && !this.flock.getLeader().isFlying();
    }

    public void flyAround() {
        if (airTarget != null && this.isFlying()) {
            if (!isTargetInAir() || flyTicks > 6000 || !this.isFlying()) {
                airTarget = null;
            }
            flyTowardsTarget();
        }
    }

    public void flyTowardsTarget() {
        if (airTarget != null && isTargetInAir() && this.isFlying() && this.getDistanceSquared(new Vector3d(airTarget.getX(), this.getPosY(), airTarget.getZ())) > 3) {
            double targetX = airTarget.getX() + 0.5D - getPosX();
            double targetY = Math.min(airTarget.getY(), 256) + 1D - getPosY();
            double targetZ = airTarget.getZ() + 0.5D - getPosZ();
            double motionX = (Math.signum(targetX) * 0.5D - this.getMotion().x) * 0.100000000372529 * getFlySpeed(false);
            double motionY = (Math.signum(targetY) * 0.5D - this.getMotion().y) * 0.100000000372529 * getFlySpeed(true);
            double motionZ = (Math.signum(targetZ) * 0.5D - this.getMotion().z) * 0.100000000372529 * getFlySpeed(false);
            this.setMotion(this.getMotion().add(motionX, motionY, motionZ));
            float angle = (float) (Math.atan2(this.getMotion().z, this.getMotion().x) * 180.0D / Math.PI) - 90.0F;
            float rotation = MathHelper.wrapDegrees(angle - rotationYaw);
            moveForward = 0.5F;
            prevRotationYaw = rotationYaw;
            rotationYaw += rotation;
            if (!this.isFlying()) {
                this.setFlying(true);
            }
        } else {
            this.airTarget = null;
        }
        if (airTarget != null && isTargetInAir() && this.isFlying() && this.getDistanceSquared(new Vector3d(airTarget.getX(), this.getPosY(), airTarget.getZ())) < 3 && this.doesWantToLand()) {
            this.setFlying(false);
        }
    }

    private float getFlySpeed(boolean y) {
        float speed = 2;
        if (this.flock != null && !this.flock.isLeader(this) && this.getDistanceSq(this.flock.getLeader()) > 10) {
            speed = 4;
        }
        if (this.getAnimation() == ANIMATION_SHOOT_ARROWS && !y) {
            speed *= 0.05;
        }
        return speed;
    }

    public void fall(float distance, float damageMultiplier) {
    }

    public void playAmbientSound() {
        if (this.getAnimation() == this.NO_ANIMATION) {
            this.setAnimation(ANIMATION_SPEAK);
        }
        super.playAmbientSound();
    }

    protected void playHurtSound(DamageSource source) {
        if (this.getAnimation() == this.NO_ANIMATION) {
            this.setAnimation(ANIMATION_SPEAK);
        }
        super.playHurtSound(source);
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return IafSoundRegistry.STYMPHALIAN_BIRD_IDLE;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return IafSoundRegistry.STYMPHALIAN_BIRD_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return IafSoundRegistry.STYMPHALIAN_BIRD_DIE;
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(IafConfig.stymphalianBirdTargetSearchLength);
        return spawnDataIn;
    }

    @Override
    public void setAttackTarget(LivingEntity entity) {
        if (this.isVictor(entity) && entity != null) {
            return;
        }
        super.setAttackTarget(entity);
        if (this.flock != null && this.flock.isLeader(this) && entity != null) {
            this.flock.onLeaderAttack(entity);
        }
    }

    public float getDistanceSquared(Vector3d Vector3d) {
        float f = (float) (this.getPosX() - Vector3d.x);
        float f1 = (float) (this.getPosY() - Vector3d.y);
        float f2 = (float) (this.getPosZ() - Vector3d.z);
        return f * f + f1 * f1 + f2 * f2;
    }

    protected boolean isTargetInAir() {
        return airTarget != null && ((world.getBlockState(airTarget).getMaterial() == Material.AIR) || world.getBlockState(airTarget).getMaterial() == Material.AIR);
    }

    public boolean doesWantToLand() {
        if (this.flock != null) {
            if (!this.flock.isLeader(this) && this.flock.getLeader() != null) {
                return this.flock.getLeader().doesWantToLand();
            }
        }
        return this.flyTicks > 500 || flyTicks > 40 && this.flyProgress == 0;
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
        return new Animation[]{NO_ANIMATION, ANIMATION_PECK, ANIMATION_SHOOT_ARROWS, ANIMATION_SPEAK};
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return IafConfig.stympahlianBirdAttackAnimals;
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
