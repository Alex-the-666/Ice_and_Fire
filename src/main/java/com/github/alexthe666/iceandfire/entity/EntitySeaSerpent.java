package com.github.alexthe666.iceandfire.entity;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.ai.*;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IAnimalFear;
import com.github.alexthe666.iceandfire.entity.util.IMultipartEntity;
import com.github.alexthe666.iceandfire.entity.util.IVillagerFear;
import com.github.alexthe666.iceandfire.enums.EnumSeaSerpent;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntitySeaSerpent extends AnimalEntity implements IAnimatedEntity, IMultipartEntity, IVillagerFear, IAnimalFear {

    public static final Animation ANIMATION_BITE = Animation.create(15);
    public static final Animation ANIMATION_SPEAK = Animation.create(15);
    public static final Animation ANIMATION_ROAR = Animation.create(40);
    public static final int TIME_BETWEEN_ROARS = 300;
    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntitySeaSerpent.class, DataSerializers.VARINT);
    private static final DataParameter<Float> SCALE = EntityDataManager.createKey(EntitySeaSerpent.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> JUMPING = EntityDataManager.createKey(EntitySeaSerpent.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> BREATHING = EntityDataManager.createKey(EntitySeaSerpent.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ANCIENT = EntityDataManager.createKey(EntitySeaSerpent.class, DataSerializers.BOOLEAN);
    private static final Predicate NOT_SEA_SERPENT = new Predicate<Entity>() {
        public boolean apply(@Nullable Entity entity) {
            return entity instanceof LivingEntity && !(entity instanceof EntitySeaSerpent) && DragonUtils.isAlive((LivingEntity) entity);
        }
    };
    private static final Predicate NOT_SEA_SERPENT_IN_WATER = new Predicate<Entity>() {
        public boolean apply(@Nullable Entity entity) {
            return entity instanceof LivingEntity && !(entity instanceof EntitySeaSerpent) && DragonUtils.isAlive((LivingEntity) entity) && entity.isInWaterOrBubbleColumn();
        }
    };
    public int swimCycle;
    public float jumpProgress = 0.0F;
    public float wantJumpProgress = 0.0F;
    public float jumpRot = 0.0F;
    public float prevJumpRot = 0.0F;
    public float breathProgress = 0.0F;
    //true  = melee, false = ranged
    public boolean attackDecision = false;
    private int animationTick;
    private Animation currentAnimation;
    private EntityMutlipartPart[] segments = new EntityMutlipartPart[9];
    private float lastScale;
    private boolean isLandNavigator;
    private boolean changedSwimBehavior = false;
    public int jumpCooldown = 0;
    private int ticksSinceRoar = 0;
    private boolean isBreathing;
    private float[] tailYaw = new float[5];
    private float[] prevTailYaw = new float[5];
    private float[] tailPitch = new float[5];
    private float[] prevTailPitch = new float[5];

    public EntitySeaSerpent(EntityType t, World worldIn) {
        super(t, worldIn);
        switchNavigator(false);
        this.ignoreFrustumCheck = true;
        resetParts(1.0F);
        this.setPathPriority(PathNodeType.WATER, 0.0F);
    }

    private static BlockPos clampBlockPosToWater(Entity entity, World world, BlockPos pos) {
        BlockPos topY = new BlockPos(pos.getX(), entity.getPosY(), pos.getZ());
        BlockPos bottomY = new BlockPos(pos.getX(), entity.getPosY(), pos.getZ());
        while (isWaterBlock(world, topY) && topY.getY() < world.getHeight()) {
            topY = topY.up();
        }
        while (isWaterBlock(world, bottomY) && bottomY.getY() > 0) {
            bottomY = bottomY.down();
        }
        return new BlockPos(pos.getX(), MathHelper.clamp(pos.getY(), bottomY.getY() + 1, topY.getY() - 1), pos.getZ());
    }

    public static boolean isWaterBlock(World world, BlockPos pos) {
        return world.getFluidState(pos).isTagged(FluidTags.WATER);
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return true;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SeaSerpentAIGetInWater(this));
        this.goalSelector.addGoal(1, new SeaSerpentAIMeleeJump(this));
        this.goalSelector.addGoal(1, new SeaSerpentAIAttackMelee(this, 1.0D, true));
        this.goalSelector.addGoal(2, new SeaSerpentAIRandomSwimming(this, 1.0D, 2));
        this.goalSelector.addGoal(3, new SeaSerpentAIJump(this, 4));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, EntityMutlipartPart.class)).setCallsForHelp());
        this.targetSelector.addGoal(2, new FlyingAITarget(this, LivingEntity.class, 150, false, false, NOT_SEA_SERPENT_IN_WATER));
        this.targetSelector.addGoal(3, new FlyingAITarget(this, PlayerEntity.class, 0, false, false, NOT_SEA_SERPENT));
    }

    protected int getExperiencePoints(PlayerEntity player) {
        return this.isAncient() ? 30 : 15;
    }

    public void collideWithNearbyEntities() {
        List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
        entities.stream().filter(entity -> !(entity instanceof EntityMutlipartPart) && entity.canBePushed()).forEach(entity -> entity.applyEntityCollision(this));
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveController = new MovementController(this);
            this.navigator = new GroundPathNavigator(this, world);
            this.navigator.setCanSwim(true);
            this.isLandNavigator = true;
        } else {
            this.moveController = new EntitySeaSerpent.SwimmingMoveHelper(this);
            this.navigator = new SeaSerpentPathNavigator(this, world);
            this.isLandNavigator = false;
        }
    }

    public boolean isDirectPathBetweenPoints(BlockPos pos) {
        Vector3d vector3d = new Vector3d(this.getPosX(), this.getPosYEye(), this.getPosZ());
        Vector3d bector3d1 = new Vector3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        return this.world.rayTraceBlocks(new RayTraceContext(vector3d, bector3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() == RayTraceResult.Type.MISS;

    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.WATER;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, IafConfig.seaSerpentBaseHealth)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15D)
                //ATTACK
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D)
                //FALLOW RANGE
                .createMutableAttribute(Attributes.FOLLOW_RANGE, Math.min(2048, IafConfig.dragonTargetSearchLength))
                //ARMOR
                .createMutableAttribute(Attributes.ARMOR, 3.0D);
    }

    public void resetParts(float scale) {
        clearParts();
        segments = new EntityMutlipartPart[9];
        for (int i = 0; i < segments.length; i++) {
            if (i > 3) {
                Entity parentToSet = i <= 4 ? this : segments[i-1];
                segments[i] = new EntitSlowPart(parentToSet, 0.5F * scale, 180, 0, 0.5F * scale, 0.5F * scale, 1);
            } else {
                Entity parentToSet = i == 0 ? this : segments[i-1];
                segments[i] = new EntitSlowPart(parentToSet, -0.4F * scale, 180, 0, 0.45F * scale, 0.4F * scale, 1);
            }
            segments[i].copyLocationAndAnglesFrom(this);
        }
    }

    public void onUpdateParts() {
        for (EntityMutlipartPart entity : segments) {
            if (entity != null) {
                if (!entity.shouldContinuePersisting()) {
                    world.addEntity(entity);
                }
            }
        }
    }

    private void clearParts() {
        for (EntityMutlipartPart entity : segments) {
            if (entity != null) {
                entity.remove();
            }
        }
    }

    public void remove() {
        clearParts();
        super.remove();
    }

    @Override
    public EntitySize getSize(Pose poseIn) {
        return this.getType().getSize().scale(this.getRenderScale());
    }

    @Override
    public float getRenderScale() {
        return this.getSeaSerpentScale();
    }

    public void recalculateSize() {
        super.recalculateSize();
        float scale = this.getSeaSerpentScale();
        if (scale != lastScale) {
            resetParts(this.getSeaSerpentScale());
        }
        lastScale = scale;
    }


    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.getAnimation() != ANIMATION_BITE) {
            this.setAnimation(ANIMATION_BITE);
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if(jumpCooldown > 0){
            jumpCooldown--;
        }
        recalculateSize();
        onUpdateParts();
        if (this.isInWater()) {
            spawnParticlesAroundEntity(ParticleTypes.BUBBLE, this, (int) this.getSeaSerpentScale());

        }
        if (!this.world.isRemote && this.world.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
        }
        if(this.getAttackTarget() != null && !this.getAttackTarget().isAlive()){
            this.setAttackTarget(null);
        }
        for(int i = 0; i < tailYaw.length; i++){
            prevTailYaw[i] = tailYaw[i];
        }
        for(int i = 0; i < tailPitch.length; i++){
            prevTailPitch[i] = tailPitch[i];
        }
        this.tailYaw[0] = this.renderYawOffset;
        this.tailPitch[0] = this.rotationPitch;
        for(int i = 1; i < tailYaw.length; i++){
            tailYaw[i] = prevTailYaw[i - 1];
        }
        for(int i = 1; i < tailPitch.length; i++){
            tailPitch[i] = prevTailPitch[i - 1];
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public float getPieceYaw(int index, float partialTicks){
        if(index < segments.length && index >= 0){
            return prevTailYaw[index] + (tailYaw[index] - prevTailYaw[index]) * partialTicks;
        }
        return 0;
    }

    public float getPiecePitch(int index, float partialTicks){
        if(index < segments.length && index >= 0){
            return prevTailPitch[index] + (tailPitch[index] - prevTailPitch[index]) * partialTicks;
        }
        return 0;
    }


    private void spawnParticlesAroundEntity(IParticleData type, Entity entity, int count) {
        for (int i = 0; i < count; i++) {
            double x = entity.getPosX() + (double) (this.rand.nextFloat() * entity.getWidth() * 2.0F) - (double) entity.getWidth();
            double y = entity.getPosY() + 0.5D + (double) (this.rand.nextFloat() * entity.getHeight());
            double z = entity.getPosZ() + (double) (this.rand.nextFloat() * entity.getWidth() * 2.0F) - (double) entity.getWidth();
            if (this.world.getBlockState(new BlockPos(x, y, z)).getMaterial() == Material.WATER) {
                this.world.addParticle(type, x, y, z, 0, 0, 0);
            }
        }
    }

    private void spawnSlamParticles(IParticleData type) {
        for (int i = 0; i < this.getSeaSerpentScale() * 3; i++) {
            for (int i1 = 0; i1 < 5; i1++) {
                double motionX = getRNG().nextGaussian() * 0.07D;
                double motionY = getRNG().nextGaussian() * 0.07D;
                double motionZ = getRNG().nextGaussian() * 0.07D;
                float radius = 1.25F * getSeaSerpentScale();
                float angle = (0.01745329251F * this.renderYawOffset) + i1 * 1F;
                double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                double extraY = 0.8F;
                double extraZ = radius * MathHelper.cos(angle);
                if (world.isRemote) {
                    world.addParticle(type, true, this.getPosX() + extraX, this.getPosY() + extraY, this.getPosZ() + extraZ, motionX, motionY, motionZ);
                }
            }
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(VARIANT, Integer.valueOf(0));
        this.dataManager.register(SCALE, Float.valueOf(0F));
        this.dataManager.register(JUMPING, false);
        this.dataManager.register(BREATHING, false);
        this.dataManager.register(ANCIENT, false);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putInt("TicksSinceRoar", ticksSinceRoar);
        compound.putInt("JumpCooldown", jumpCooldown);
        compound.putFloat("Scale", this.getSeaSerpentScale());
        compound.putBoolean("JumpingOutOfWater", this.isJumpingOutOfWater());
        compound.putBoolean("AttackDecision", attackDecision);
        compound.putBoolean("Breathing", this.isBreathing());
        compound.putBoolean("Ancient", this.isAncient());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setVariant(compound.getInt("Variant"));
        ticksSinceRoar = compound.getInt("TicksSinceRoar");
        jumpCooldown = compound.getInt("JumpCooldown");
        this.setSeaSerpentScale(compound.getFloat("Scale"));
        this.setJumpingOutOfWater(compound.getBoolean("JumpingOutOfWater"));
        attackDecision = compound.getBoolean("AttackDecision");
        this.setBreathing(compound.getBoolean("Breathing"));
        this.setAncient(compound.getBoolean("Ancient"));
    }

    private void updateAttributes() {
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(Math.min(0.25D, 0.15D * this.getSeaSerpentScale() * this.getAncientModifier()));
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(Math.max(4, IafConfig.seaSerpentAttackStrength * this.getSeaSerpentScale() * this.getAncientModifier()));
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(Math.max(10, IafConfig.seaSerpentBaseHealth * this.getSeaSerpentScale() * this.getAncientModifier()));
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(Math.min(2048, IafConfig.dragonTargetSearchLength));
        this.heal(30F * this.getSeaSerpentScale());
    }

    private float getAncientModifier() {
        return this.isAncient() ? 1.5F : 1.0F;
    }

    public float getSeaSerpentScale() {
        return Float.valueOf(this.dataManager.get(SCALE).floatValue());
    }

    private void setSeaSerpentScale(float scale) {
        this.dataManager.set(SCALE, Float.valueOf(scale));
    }

    public int getVariant() {
        return Integer.valueOf(this.dataManager.get(VARIANT).intValue());
    }

    public void setVariant(int variant) {
        this.dataManager.set(VARIANT, Integer.valueOf(variant));
    }

    public boolean isJumpingOutOfWater() {
        return this.dataManager.get(JUMPING).booleanValue();
    }

    public void setJumpingOutOfWater(boolean jump) {
        this.dataManager.set(JUMPING, jump);
    }

    public boolean isAncient() {
        return this.dataManager.get(ANCIENT).booleanValue();
    }

    public void setAncient(boolean ancient) {
        this.dataManager.set(ANCIENT, ancient);
    }

    public boolean isBreathing() {
        if (world.isRemote) {
            boolean breathing = this.dataManager.get(BREATHING).booleanValue();
            this.isBreathing = breathing;
            return breathing;
        }
        return isBreathing;
    }

    public void setBreathing(boolean breathing) {
        this.dataManager.set(BREATHING, breathing);
        if (!world.isRemote) {
            this.isBreathing = breathing;
        }
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public void livingTick() {
        super.livingTick();
        if (!world.isRemote) {
            if (world.getDifficulty() == Difficulty.PEACEFUL && this.getAttackTarget() instanceof PlayerEntity) {
                this.setAttackTarget(null);
            }
        }
        boolean breathing = isBreathing() && this.getAnimation() != ANIMATION_BITE && this.getAnimation() != ANIMATION_ROAR;
        boolean jumping = !this.isInWater() && !this.isOnGround() && this.getMotion().y >= 0;
        boolean wantJumping = false; //(ticksSinceJump > TIME_BETWEEN_JUMPS) && this.isInWater();
        boolean ground = !isInWater() && this.onGround;
        boolean prevJumping = this.isJumpingOutOfWater();
        this.ticksSinceRoar++;
        this.jumpCooldown++;
        this.prevJumpRot = jumpRot;
        if (this.ticksSinceRoar > TIME_BETWEEN_ROARS && isAtSurface() && this.getAnimation() != ANIMATION_BITE && jumpProgress == 0 && !isJumpingOutOfWater()) {
            this.setAnimation(ANIMATION_ROAR);
            this.ticksSinceRoar = 0;
        }
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() == 1) {
            this.playSound(IafSoundRegistry.SEA_SERPENT_ROAR, this.getSoundVolume() + 1, 1);
        }
        if (this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 5) {
            this.playSound(IafSoundRegistry.SEA_SERPENT_BITE, this.getSoundVolume(), 1);
        }
        if (isJumpingOutOfWater() && isWaterBlock(world, this.getPosition().up(2))) {
            setJumpingOutOfWater(false);
        }
        if (this.swimCycle < 38) {
            this.swimCycle += 2;
        } else {
            this.swimCycle = 0;
        }
        if (breathing && breathProgress < 20.0F) {
            breathProgress += 0.5F;
        } else if (!breathing && breathProgress > 0.0F) {
            breathProgress -= 0.5F;
        }
        if (jumping && jumpProgress < 10.0F) {
            jumpProgress += 0.5F;
        } else if (!jumping && jumpProgress > 0.0F) {
            jumpProgress -= 0.5F;
        }
        if (wantJumping && wantJumpProgress < 10.0F) {
            wantJumpProgress += 2F;
        } else if (!wantJumping && wantJumpProgress > 0.0F) {
            wantJumpProgress -= 2F;
        }
        if (this.isJumpingOutOfWater() && jumpRot < 1.0F) {
            jumpRot += 0.1F;
        } else if (!this.isJumpingOutOfWater() && jumpRot > 0.0F) {
            jumpRot -= 0.1F;
        }
        if (prevJumping && !this.isJumpingOutOfWater()) {
            this.playSound(IafSoundRegistry.SEA_SERPENT_SPLASH, 5F, 0.75F);
            spawnSlamParticles(ParticleTypes.BUBBLE);
            this.doSplashDamage();
        }
        if (!ground && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (ground && !this.isLandNavigator) {
            switchNavigator(true);
        }
        rotationPitch = MathHelper.clamp((float) this.getMotion().y * 20F, -90, 90);
        if (changedSwimBehavior) {
            changedSwimBehavior = false;
        }
        if (!world.isRemote) {
            if (attackDecision) {
                this.setBreathing(false);
            }
            if (this.getAttackTarget() != null && this.getAnimation() != ANIMATION_ROAR) {
                if (!attackDecision) {
                    if (!this.getAttackTarget().isInWater() || !this.canEntityBeSeen(this.getAttackTarget()) || this.getDistance(this.getAttackTarget()) < 30 * this.getSeaSerpentScale()) {
                        attackDecision = true;
                    }
                    if (!attackDecision) {
                        shoot(this.getAttackTarget());
                    }
                } else {
                    if (this.getDistanceSq(this.getAttackTarget()) > 200 * this.getSeaSerpentScale()) {
                        attackDecision = false;
                    }
                }
            } else {
                this.setBreathing(false);
            }
        }
        if (this.getAnimation() == ANIMATION_BITE && this.getAttackTarget() != null && (this.isTouchingMob(this.getAttackTarget()) || this.getDistanceSq(this.getAttackTarget()) < 50)) {
            this.hurtMob(this.getAttackTarget());
        }
        breakBlock();
        if (!world.isRemote && this.isPassenger() && this.getLowestRidingEntity() instanceof BoatEntity) {
            BoatEntity boat = (BoatEntity) this.getLowestRidingEntity();
            boat.remove();
            this.stopRiding();
        }
    }

    private boolean isAtSurface() {
        BlockPos pos = this.getPosition();
        return isWaterBlock(world, pos.down()) && !isWaterBlock(world, pos.up());
    }

    private void doSplashDamage() {
        double getWidth = 2D * this.getSeaSerpentScale();
        List<Entity> list = world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow(getWidth, getWidth * 0.5D, getWidth), NOT_SEA_SERPENT);
        for (Entity entity : list) {
            if (entity instanceof LivingEntity && DragonUtils.isAlive((LivingEntity) entity)) {
                entity.attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
                destroyBoat(entity);
                double xRatio = this.getPosX() - entity.getPosX();
                double zRatio = this.getPosZ() - entity.getPosZ();
                float f = MathHelper.sqrt(xRatio * xRatio + zRatio * zRatio);
                float strength = 0.3F * this.getSeaSerpentScale();
                entity.setMotion(entity.getMotion().mul(0.5D, 1D, 0.5D));
                entity.setMotion(entity.getMotion().add(xRatio / (double) f * (double) strength, strength, zRatio / (double) f * (double) strength));
            }
        }

    }

    public void destroyBoat(Entity sailor) {
        if (sailor.getRidingEntity() != null && sailor.getRidingEntity() instanceof BoatEntity && !world.isRemote) {
            BoatEntity boat = (BoatEntity) sailor.getRidingEntity();
            boat.remove();
            if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                for (int i = 0; i < 3; ++i) {
                    boat.entityDropItem(new ItemStack(Item.getItemFromBlock(boat.getBoatType().asPlank())), 0.0F);
                }
                for (int j = 0; j < 2; ++j) {
                    boat.entityDropItem(new ItemStack(Items.STICK));
                }
            }
        }
    }

    private boolean isPreyAtSurface() {
        if (this.getAttackTarget() != null) {
            BlockPos pos = this.getAttackTarget().getPosition();
            return !isWaterBlock(world, pos.up((int) Math.ceil(this.getAttackTarget().getHeight())));
        }
        return false;
    }

    private void hurtMob(LivingEntity entity) {
        if (this.getAnimation() == ANIMATION_BITE && entity != null && this.getAnimationTick() == 6) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
            EntitySeaSerpent.this.attackDecision = this.getRNG().nextBoolean();
        }
    }

    public void moveJumping() {
        float velocity = 0.5F;
        double x = -MathHelper.sin(this.rotationYaw * 0.017453292F) * MathHelper.cos(this.rotationPitch * 0.017453292F);
        double z = MathHelper.cos(this.rotationYaw * 0.017453292F) * MathHelper.cos(this.rotationPitch * 0.017453292F);
        float f = MathHelper.sqrt(x * x + z * z);
        x = x / (double) f;
        z = z / (double) f;
        x = x * (double) velocity;
        z = z * (double) velocity;
        this.setMotion(x, this.getMotion().y, z);
    }

    public boolean isTouchingMob(Entity entity) {
        if (this.getBoundingBox().expand(1, 1, 1).intersects(entity.getBoundingBox())) {
            return true;
        }
        for (Entity segment : segments) {
            if (segment.getBoundingBox().expand(1, 1, 1).intersects(entity.getBoundingBox())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    public void breakBlock() {
        if (IafConfig.seaSerpentGriefing) {
            for (int a = (int) Math.round(this.getBoundingBox().minX) - 2; a <= (int) Math.round(this.getBoundingBox().maxX) + 2; a++) {
                for (int b = (int) Math.round(this.getBoundingBox().minY) - 1; (b <= (int) Math.round(this.getBoundingBox().maxY) + 2) && (b <= 127); b++) {
                    for (int c = (int) Math.round(this.getBoundingBox().minZ) - 2; c <= (int) Math.round(this.getBoundingBox().maxZ) + 2; c++) {
                        BlockPos pos = new BlockPos(a, b, c);
                        BlockState state = world.getBlockState(pos);
                        FluidState fluidState = world.getFluidState(pos);
                        Block block = state.getBlock();
                        if (!state.isAir() && !state.getShape(world, pos).isEmpty() && (state.getMaterial() == Material.PLANTS || state.getMaterial() == Material.LEAVES) && fluidState.isEmpty()) {
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
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setVariant(this.getRNG().nextInt(7));
        boolean ancient = this.getRNG().nextInt(16) == 1;
        if (ancient) {
            this.setAncient(true);
            this.setSeaSerpentScale(6.0F + this.getRNG().nextFloat() * 3.0F);

        } else {
            this.setSeaSerpentScale(1.5F + this.getRNG().nextFloat() * 4.0F);
        }
        this.updateAttributes();
        return spawnDataIn;
    }

    public void onWorldSpawn(Random random) {
        this.setVariant(random.nextInt(7));
        boolean ancient = random.nextInt(15) == 1;
        if (ancient) {
            this.setAncient(true);
            this.setSeaSerpentScale(6.0F + random.nextFloat() * 3.0F);

        } else {
            this.setSeaSerpentScale(1.5F + random.nextFloat() * 4.0F);
        }
        this.updateAttributes();
    }

    @Nullable
    @Override
    public AgeableEntity createChild(ServerWorld serverWorld, AgeableEntity ageable) {
        return null;
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
        return new Animation[]{ANIMATION_BITE, ANIMATION_ROAR, ANIMATION_SPEAK};
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return IafSoundRegistry.SEA_SERPENT_IDLE;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return IafSoundRegistry.SEA_SERPENT_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return IafSoundRegistry.SEA_SERPENT_DIE;
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

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return true;
    }

    public boolean isBlinking() {
        return this.ticksExisted % 50 > 43;
    }

    private void shoot(LivingEntity entity) {
        if (!this.attackDecision) {
            if (!this.isInWater()) {
                this.setBreathing(false);
                this.attackDecision = true;
            }
            if (this.isBreathing()) {
                if (this.ticksExisted % 40 == 0) {
                    this.playSound(IafSoundRegistry.SEA_SERPENT_BREATH, 4, 1);
                }
                if (this.ticksExisted % 10 == 0) {
                    rotationYaw = renderYawOffset;
                    float f1 = 0;
                    float f2 = 0;
                    float f3 = 0;
                    float headPosX = f1 + (float) (this.segments[0].getPosX() + 1.3F * getSeaSerpentScale() * Math.cos((rotationYaw + 90) * Math.PI / 180));
                    float headPosZ = f2 + (float) (this.segments[0].getPosZ() + 1.3F * getSeaSerpentScale() * Math.sin((rotationYaw + 90) * Math.PI / 180));
                    float headPosY = f3 + (float) (this.segments[0].getPosY() + 0.2F * getSeaSerpentScale());
                    double d2 = entity.getPosX() - headPosX;
                    double d3 = entity.getPosY() - headPosY;
                    double d4 = entity.getPosZ() - headPosZ;
                    float inaccuracy = 1.0F;
                    d2 = d2 + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
                    d3 = d3 + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
                    d4 = d4 + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
                    EntitySeaSerpentBubbles entitylargefireball = new EntitySeaSerpentBubbles(IafEntityRegistry.SEA_SERPENT_BUBBLES, world, this, d2, d3, d4);
                    float size = 0.8F;
                    entitylargefireball.setPosition(headPosX, headPosY, headPosZ);
                    if (!world.isRemote) {
                        world.addEntity(entitylargefireball);
                    }
                    if (!entity.isAlive() || entity == null) {
                        this.setBreathing(false);
                        this.attackDecision = this.getRNG().nextBoolean();
                    }
                }
            } else {
                this.setBreathing(true);
            }
        }
        this.faceEntity(entity, 360, 360);
    }

    public EnumSeaSerpent getEnum() {
        switch (this.getVariant()) {
            default:
                return EnumSeaSerpent.BLUE;
            case 1:
                return EnumSeaSerpent.BRONZE;
            case 2:
                return EnumSeaSerpent.DEEPBLUE;
            case 3:
                return EnumSeaSerpent.GREEN;
            case 4:
                return EnumSeaSerpent.PURPLE;
            case 5:
                return EnumSeaSerpent.RED;
            case 6:
                return EnumSeaSerpent.TEAL;
        }
    }

    @Override
    public void travel(Vector3d vec) {
        if (this.isServerWorld() && this.isInWater()) {
            this.moveRelative(this.getAIMoveSpeed(), vec);
            this.move(MoverType.SELF, this.getMotion());
            this.setMotion(this.getMotion().scale(0.9D));
            if (this.getAttackTarget() == null) {
                this.setMotion(this.getMotion().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(vec);
        }
    }

    @Override
    public void onKillEntity(ServerWorld world, LivingEntity entity) {
        this.attackDecision = this.getRNG().nextBoolean();
    }

    @Override
    public boolean isNoDespawnRequired() {
        return true;
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    public int getMaxFallHeight() {
        return 1000;
    }

    public void onJumpHit(LivingEntity target) {
    }

    public boolean shouldUseJumpAttack(LivingEntity attackTarget) {
        return !attackTarget.isInWater() || isPreyAtSurface();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.FALL || source == DamageSource.DROWN || source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK || source == DamageSource.LAVA || source.isFireDamage() || super.isInvulnerableTo(source);
    }

    public class SwimmingMoveHelper extends MovementController {
        private final EntitySeaSerpent dolphin;

        public SwimmingMoveHelper(EntitySeaSerpent dolphinIn) {
            super(dolphinIn);
            this.dolphin = dolphinIn;
        }

        @Override
        public void tick() {
            if (this.dolphin.isInWater()) {
                this.dolphin.setMotion(this.dolphin.getMotion().add(0.0D, 0.005D, 0.0D));
            }

            if (this.action == MovementController.Action.MOVE_TO && !this.dolphin.getNavigator().noPath()) {
                double d0 = this.posX - this.dolphin.getPosX();
                double d1 = this.posY - this.dolphin.getPosY();
                double d2 = this.posZ - this.dolphin.getPosZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (d3 < (double) 2.5000003E-7F) {
                    this.mob.setMoveForward(0.0F);
                } else {
                    float f = (float) (MathHelper.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                    this.dolphin.rotationYaw = this.limitAngle(this.dolphin.rotationYaw, f, 10.0F);
                    this.dolphin.renderYawOffset = this.dolphin.rotationYaw;
                    this.dolphin.rotationYawHead = this.dolphin.rotationYaw;
                    float f1 = (float) (this.speed * 3);
                    if (this.dolphin.isInWater() ) {
                        this.dolphin.setAIMoveSpeed(f1 * 0.02F);
                        float f2 = -((float) (MathHelper.atan2(d1, MathHelper.sqrt(d0 * d0 + d2 * d2)) * (double) (180F / (float) Math.PI)));
                        f2 = MathHelper.clamp(MathHelper.wrapDegrees(f2), -85.0F, 85.0F);
                        this.dolphin.setMotion(this.dolphin.getMotion().add(0.0D, (double) this.dolphin.getAIMoveSpeed() * d1 * 0.6D, 0.0D));
                        this.dolphin.rotationPitch = this.limitAngle(this.dolphin.rotationPitch, f2, 1.0F);
                        float f3 = MathHelper.cos(this.dolphin.rotationPitch * ((float) Math.PI / 180F));
                        float f4 = MathHelper.sin(this.dolphin.rotationPitch * ((float) Math.PI / 180F));
                        this.dolphin.moveForward = f3 * f1;
                        this.dolphin.moveVertical = -f4 * f1;
                    } else {
                        this.dolphin.setAIMoveSpeed(f1 * 0.1F);
                    }

                }
            } else {
                this.dolphin.setAIMoveSpeed(0.0F);
                this.dolphin.setMoveStrafing(0.0F);
                this.dolphin.setMoveVertical(0.0F);
                this.dolphin.setMoveForward(0.0F);
            }
        }
    }
}
