package com.github.alexthe666.iceandfire.entity;

import java.util.List;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.ai.AquaticAIGetInWater;
import com.github.alexthe666.iceandfire.entity.ai.AquaticAIGetOutOfWater;
import com.github.alexthe666.iceandfire.entity.ai.SirenAIFindWaterTarget;
import com.github.alexthe666.iceandfire.entity.ai.SirenAIWander;
import com.github.alexthe666.iceandfire.entity.props.SirenEntityProperties;
import com.github.alexthe666.iceandfire.entity.util.ChainBuffer;
import com.github.alexthe666.iceandfire.entity.util.IHearsSiren;
import com.github.alexthe666.iceandfire.entity.util.IVillagerFear;
import com.github.alexthe666.iceandfire.event.ServerEvents;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.message.MessageSirenSong;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.github.alexthe666.iceandfire.pathfinding.PathNavigateAmphibious;
import com.google.common.base.Predicate;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntitySiren extends MonsterEntity implements IAnimatedEntity, IVillagerFear {

    public static final int SEARCH_RANGE = 32;
    public static final Predicate<Entity> SIREN_PREY = new Predicate<Entity>() {
        public boolean apply(@Nullable Entity p_apply_1_) {
            return (p_apply_1_ instanceof PlayerEntity && !((PlayerEntity) p_apply_1_).isCreative() && !p_apply_1_.isSpectator()) || p_apply_1_ instanceof AbstractVillagerEntity || p_apply_1_ instanceof IHearsSiren;
        }
    };
    private static final DataParameter<Integer> HAIR_COLOR = EntityDataManager.createKey(EntitySiren.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> AGGRESSIVE = EntityDataManager.createKey(EntitySiren.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> SING_POSE = EntityDataManager.createKey(EntitySiren.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SINGING = EntityDataManager.createKey(EntitySiren.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SWIMMING = EntityDataManager.createKey(EntitySiren.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CHARMED = EntityDataManager.createKey(EntitySiren.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(EntitySiren.class, DataSerializers.BYTE);
    public static Animation ANIMATION_BITE = Animation.create(20);
    public static Animation ANIMATION_PULL = Animation.create(20);
    @OnlyIn(Dist.CLIENT)
    public ChainBuffer tail_buffer;
    public float singProgress;
    public float swimProgress;
    public int singCooldown;
    private int animationTick;
    private Animation currentAnimation;
    private boolean isSinging;
    private boolean isSwimming;
    private boolean isLandNavigator;
    private int ticksAgressive;

    public EntitySiren(EntityType t, World worldIn) {
        super(t, worldIn);
        this.switchNavigator(true);
        this.stepHeight = 2;
        this.goalSelector.addGoal(0, new SirenAIFindWaterTarget(this));
        this.goalSelector.addGoal(1, new AquaticAIGetInWater(this, 1.0D));
        this.goalSelector.addGoal(1, new AquaticAIGetOutOfWater(this, 1.0D));
        this.goalSelector.addGoal(2, new SirenAIWander(this, 1));
        this.goalSelector.addGoal(3, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F, 1.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, PlayerEntity.class, 0, true, false, new Predicate<PlayerEntity>() {
            @Override
            public boolean apply(@Nullable PlayerEntity entity) {
                return EntitySiren.this.isAgressive() && !(entity.isCreative() || entity.isSpectator());
            }
        }));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, AbstractVillagerEntity.class, 0, true, false, new Predicate<AbstractVillagerEntity>() {
            @Override
            public boolean apply(@Nullable AbstractVillagerEntity entity) {
                return EntitySiren.this.isAgressive();
            }
        }));
        if (worldIn.isRemote) {
            tail_buffer = new ChainBuffer();
        }
    }

    public static boolean isWearingEarplugs(LivingEntity entity) {
        ItemStack helmet = entity.getItemStackFromSlot(EquipmentSlotType.HEAD);
        return helmet.getItem() == IafItemRegistry.EARPLUGS || helmet != ItemStack.EMPTY && helmet.getItem().getTranslationKey().contains("earmuff");
    }

    public static boolean isDrawnToSong(Entity entity) {
        return entity instanceof PlayerEntity && !((PlayerEntity) entity).isCreative() || entity instanceof AbstractVillagerEntity || entity instanceof IHearsSiren;
    }

    protected int getExperiencePoints(PlayerEntity player) {
        return 8;
    }

    public float getBlockPathWeight(BlockPos pos) {
        return world.getBlockState(pos).getMaterial() == Material.WATER ? 10F : super.getBlockPathWeight(pos);
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.getRNG().nextInt(2) == 0) {
            if (this.getAnimation() != ANIMATION_PULL) {
                this.setAnimation(ANIMATION_PULL);
                this.playSound(IafSoundRegistry.NAGA_ATTACK, 1, 1);
            }
        } else {
            if (this.getAnimation() != ANIMATION_BITE) {
                this.setAnimation(ANIMATION_BITE);
                this.playSound(IafSoundRegistry.NAGA_ATTACK, 1, 1);
            }
        }
        return true;
    }

    public boolean isDirectPathBetweenPoints(Vector3d vec1, Vector3d pos) {
        Vector3d Vector3d1 = new Vector3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        return this.world.rayTraceBlocks(new RayTraceContext(vec1, Vector3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() == RayTraceResult.Type.MISS;
    }

    public float getPathPriority(PathNodeType nodeType) {
        return nodeType == PathNodeType.WATER ? 0F : super.getPathPriority(nodeType);
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveController = new MovementController(this);
            this.navigator = new PathNavigateAmphibious(this, world);
            this.isLandNavigator = true;
        } else {
            this.moveController = new EntitySiren.SwimmingMoveHelper();
            this.navigator = new SwimmerPathNavigator(this, world);
            this.isLandNavigator = false;
        }
    }

    private boolean isPathOnHighGround() {
        if (this.navigator != null && this.navigator.getPath() != null && this.navigator.getPath().getFinalPathPoint() != null) {
            BlockPos target = new BlockPos(this.navigator.getPath().getFinalPathPoint().x, this.navigator.getPath().getFinalPathPoint().y, this.navigator.getPath().getFinalPathPoint().z);
            BlockPos siren = this.getPosition();
            return world.isAirBlock(siren.up()) && world.isAirBlock(target.up()) && target.getY() >= siren.getY();
        }
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public void livingTick() {
        super.livingTick();
        renderYawOffset = rotationYaw;
        if (singCooldown > 0) {
            singCooldown--;
            this.setSinging(false);
        }
        if (!world.isRemote && this.getAttackTarget() == null && !this.isAgressive()) {
            this.setSinging(true);
        }
        if (this.getAnimation() == ANIMATION_BITE && this.getAttackTarget() != null && this.getDistanceSq(this.getAttackTarget()) < 7D && this.getAnimationTick() == 5) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
        }
        if (this.getAnimation() == ANIMATION_PULL && this.getAttackTarget() != null && this.getDistanceSq(this.getAttackTarget()) < 16D && this.getAnimationTick() == 5) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
            double attackmotionX = (Math.signum(this.getPosX() - this.getAttackTarget().getPosX()) * 0.5D - this.getAttackTarget().getMotion().z) * 0.100000000372529 * 5;
            double attackmotionY = (Math.signum(this.getPosY() - this.getAttackTarget().getPosY() + 1) * 0.5D - this.getAttackTarget().getMotion().y) * 0.100000000372529 * 5;
            double attackmotionZ = (Math.signum(this.getPosZ() - this.getAttackTarget().getPosZ()) * 0.5D - this.getAttackTarget().getMotion().z) * 0.100000000372529 * 5;

            this.getAttackTarget().setMotion(this.getAttackTarget().getMotion().add(attackmotionX, attackmotionY, attackmotionZ));
            float angle = (float) (Math.atan2(this.getAttackTarget().getMotion().z, this.getAttackTarget().getMotion().x) * 180.0D / Math.PI) - 90.0F;
            //entity.moveForward = 0.5F;
            double d0 = this.getPosX() - this.getAttackTarget().getPosX();
            double d2 = this.getPosZ() - this.getAttackTarget().getPosZ();
            double d1 = this.getPosY() - 1 - this.getAttackTarget().getPosY();
            double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
            float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
            float f1 = (float) (-(MathHelper.atan2(d1, d3) * (180D / Math.PI)));
            this.getAttackTarget().rotationPitch = ServerEvents.updateRotation(this.getAttackTarget().rotationPitch, f1, 30F);
            this.getAttackTarget().rotationYaw = ServerEvents.updateRotation(this.getAttackTarget().rotationYaw, f, 30F);
        }
        if (world.isRemote) {
            tail_buffer.calculateChainSwingBuffer(40, 10, 2.5F, this);
        }
        if (this.isAgressive()) {
            ticksAgressive++;
        } else {
            ticksAgressive = 0;
        }
        if (ticksAgressive > 300 && this.isAgressive() && this.getAttackTarget() == null && !world.isRemote) {
            this.setAggressive(false);
            this.ticksAgressive = 0;
            this.setSinging(false);
        }

        if (this.isInWater() && !this.isSwimming()) {
            this.setSwimming(true);
        }
        if (!this.isInWater() && this.isSwimming()) {
            this.setSwimming(false);
        }
        boolean pathOnHighGround = this.isPathOnHighGround() || this.getAttackTarget() != null && !this.getAttackTarget().isInWater() && !this.getAttackTarget().isInWater();
        if (this.getAttackTarget() == null || !this.getAttackTarget().isInWater() && !this.getAttackTarget().isInWater()) {
            if (pathOnHighGround && this.isInWater()) {
                jump();
                doWaterSplashEffect();
            }
        }
        if ((this.isInWater() && !pathOnHighGround) && this.isLandNavigator) {
            switchNavigator(false);
        }
        if ((!this.isInWater() || pathOnHighGround) && !this.isLandNavigator) {
            switchNavigator(true);
        }
        if (this.getAttackTarget() != null && this.getAttackTarget() instanceof PlayerEntity && ((PlayerEntity) this.getAttackTarget()).isCreative()) {
            this.setAttackTarget(null);
            this.setAggressive(false);
        }
        if (this.getAttackTarget() != null && !this.isAgressive()) {
            this.setAggressive(true);
        }
        boolean singing = isActuallySinging() && !this.isAgressive() && !this.isInWater() && onGround;
        if (singing && singProgress < 20.0F) {
            singProgress += 1F;
        } else if (!singing && singProgress > 0.0F) {
            singProgress -= 1F;
        }
        boolean swimming = isSwimming();
        if (swimming && swimProgress < 20.0F) {
            swimProgress += 1F;
        } else if (!swimming && swimProgress > 0.0F) {
            swimProgress -= 0.5F;
        }
        if (!world.isRemote && !EntityGorgon.isStoneMob(this) && this.isActuallySinging()) {
            checkForPrey();
            updateLure();
        }
        if (!world.isRemote && EntityGorgon.isStoneMob(this) && this.isSinging()) {
            this.setSinging(false);
        }
        if (isActuallySinging() && !this.isInWater()) {
            if (this.getRNG().nextInt(3) == 0) {
                renderYawOffset = rotationYaw;
                if (this.world.isRemote) {
                    float radius = -0.9F;
                    float angle = (0.01745329251F * this.renderYawOffset) - 3F;
                    double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                    double extraY = 1.2F;
                    double extraZ = radius * MathHelper.cos(angle);
                    IceAndFire.PROXY.spawnParticle("siren_music", this.getPosX() + extraX + this.rand.nextFloat() - 0.5, this.getPosY() + extraY + this.rand.nextFloat() - 0.5, this.getPosZ() + extraZ + this.rand.nextFloat() - 0.5, 0, 0, 0);
                }
            }
        }
        if (this.isActuallySinging() && !this.isInWater() && this.ticksExisted % 200 == 0) {
            this.playSound(IafSoundRegistry.SIREN_SONG, 2, 1);
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    private void checkForPrey() {
        this.setSinging(true);
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.getTrueSource() != null && source.getTrueSource() instanceof LivingEntity) {
            this.triggerOtherSirens((LivingEntity) source.getTrueSource());
        }
        return super.attackEntityFrom(source, amount);
    }

    public void triggerOtherSirens(LivingEntity aggressor) {
        List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox().grow(12, 12, 12));
        for (Entity entity : entities) {
            if (entity instanceof EntitySiren) {
                ((EntitySiren) entity).setAttackTarget(aggressor);
                ((EntitySiren) entity).setAggressive(true);
                ((EntitySiren) entity).setSinging(false);

            }
        }
    }

    public void updateLure() {
        if (this.ticksExisted % 20 == 0) {
            List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(50, 12, 50), SIREN_PREY);
            for (LivingEntity entity : entities) {
                SirenEntityProperties sirenProps = EntityPropertiesHandler.INSTANCE.getProperties(entity, SirenEntityProperties.class);
                if (!isWearingEarplugs(entity) && sirenProps != null && (!sirenProps.isCharmed || sirenProps.getSiren(world) == null)) {
                    sirenProps.isCharmed = true;
                    sirenProps.sirenID = this.getEntityId();
                }
            }
        }
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        tag.putInt("HairColor", this.getHairColor());
        tag.putBoolean("Aggressive", this.isAgressive());
        tag.putInt("SingingPose", this.getSingingPose());
        tag.putBoolean("Singing", this.isSinging());
        tag.putBoolean("Swimming", this.isSwimming());
        tag.putBoolean("Passive", this.isCharmed());

    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        this.setHairColor(tag.getInt("HairColor"));
        this.setAggressive(tag.getBoolean("Aggressive"));
        this.setSingingPose(tag.getInt("SingingPose"));
        this.setSinging(tag.getBoolean("Singing"));
        this.setSwimming(tag.getBoolean("Swimming"));
        this.setCharmed(tag.getBoolean("Passive"));

    }

    public boolean isSinging() {
        if (world.isRemote) {
            return this.isSinging = this.dataManager.get(SINGING).booleanValue();
        }
        return isSinging;
    }

    public void setSinging(boolean singing) {
        if (singCooldown > 0) {
            singing = false;
        }
        this.dataManager.set(SINGING, singing);
        if (!world.isRemote) {
            this.isSinging = singing;
            IceAndFire.sendMSGToAll(new MessageSirenSong(this.getEntityId(), singing));
        }
    }

    public boolean wantsToSing() {
        return this.isSinging() && this.isInWater() && !this.isAgressive();
    }

    public boolean isActuallySinging() {
        return isSinging() && !wantsToSing();
    }

    public boolean isSwimming() {
        if (world.isRemote) {
            return this.isSwimming = this.dataManager.get(SWIMMING).booleanValue();
        }
        return isSwimming;
    }

    public void setSwimming(boolean swimming) {
        this.dataManager.set(SWIMMING, swimming);
        if (!world.isRemote) {
            this.isSwimming = swimming;
        }
    }

    public void setAggressive(boolean aggressive) {
        this.dataManager.set(AGGRESSIVE, aggressive);
    }

    public boolean isAgressive() {
        return this.dataManager.get(AGGRESSIVE).booleanValue();
    }

    public boolean isCharmed() {
        return this.dataManager.get(CHARMED).booleanValue();
    }

    public void setCharmed(boolean aggressive) {
        this.dataManager.set(CHARMED, aggressive);
    }

    public int getHairColor() {
        return this.dataManager.get(HAIR_COLOR).intValue();
    }

    public void setHairColor(int hairColor) {
        this.dataManager.set(HAIR_COLOR, hairColor);
    }

    public int getSingingPose() {
        return this.dataManager.get(SING_POSE).intValue();
    }

    public void setSingingPose(int pose) {
        this.dataManager.set(SING_POSE, MathHelper.clamp(pose, 0, 2));
    }


    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, IafConfig.sirenMaxHealth)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
                //ATTACK
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 6.0D);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(HAIR_COLOR, Integer.valueOf(0));
        this.dataManager.register(SING_POSE, Integer.valueOf(0));
        this.dataManager.register(AGGRESSIVE, Boolean.valueOf(false));
        this.dataManager.register(SINGING, Boolean.valueOf(false));
        this.dataManager.register(SWIMMING, Boolean.valueOf(false));
        this.dataManager.register(CHARMED, Boolean.valueOf(false));
        this.dataManager.register(CLIMBING, Byte.valueOf((byte) 0));
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setHairColor(this.getRNG().nextInt(3));
        this.setSingingPose(this.getRNG().nextInt(3));
        return spawnDataIn;
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
        return new Animation[]{NO_ANIMATION, ANIMATION_BITE, ANIMATION_PULL};
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return this.isAgressive() ? IafSoundRegistry.NAGA_IDLE : IafSoundRegistry.MERMAID_IDLE;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return this.isAgressive() ? IafSoundRegistry.NAGA_HURT : IafSoundRegistry.MERMAID_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return this.isAgressive() ? IafSoundRegistry.NAGA_DIE : IafSoundRegistry.MERMAID_DIE;
    }

    @Override
    public void travel(Vector3d motion) {
      super.travel(motion);
    }

    @Override
    public boolean isNoDespawnRequired() {
        return true;
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean shouldFear() {
        return isAgressive();
    }

    class SwimmingMoveHelper extends MovementController {
        private EntitySiren siren = EntitySiren.this;

        public SwimmingMoveHelper() {
            super(EntitySiren.this);
        }

        @Override
        public void tick() {
            if (this.action == MovementController.Action.MOVE_TO) {
                double distanceX = this.posX - siren.getPosX();
                double distanceY = this.posY - siren.getPosY();
                double distanceZ = this.posZ - siren.getPosZ();
                double distance = Math.abs(distanceX * distanceX + distanceZ * distanceZ);
                double distanceWithY = MathHelper.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
                distanceY = distanceY / distanceWithY;
                float angle = (float) (Math.atan2(distanceZ, distanceX) * 180.0D / Math.PI) - 90.0F;
                siren.rotationYaw = this.limitAngle(siren.rotationYaw, angle, 30.0F);
                siren.setAIMoveSpeed(1F);
                float f1 = 0;
                float f2 = 0;
                if (distance < (double) Math.max(1.0F, siren.getWidth())) {
                    float f = siren.rotationYaw * 0.017453292F;
                    f1 -= (double) (MathHelper.sin(f) * 0.35F);
                    f2 += (double) (MathHelper.cos(f) * 0.35F);
                }
                siren.setMotion(siren.getMotion().add(f1, siren.getAIMoveSpeed() * distanceY * 0.1D, f2));
            } else if (this.action == MovementController.Action.JUMPING) {
                siren.setAIMoveSpeed((float) (this.speed * siren.getAttribute(Attributes.MOVEMENT_SPEED).getValue()));
                if (siren.onGround) {
                    this.action = MovementController.Action.WAIT;
                }
            } else {
                siren.setAIMoveSpeed(0.0F);
            }
        }
    }
}
