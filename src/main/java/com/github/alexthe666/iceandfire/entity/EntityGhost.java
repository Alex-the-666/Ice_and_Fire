package com.github.alexthe666.iceandfire.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.ai.GhostAICharge;
import com.github.alexthe666.iceandfire.entity.ai.GhostPathNavigator;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IAnimalFear;
import com.github.alexthe666.iceandfire.entity.util.IBlacklistedFromStatues;
import com.github.alexthe666.iceandfire.entity.util.IHumanoid;
import com.github.alexthe666.iceandfire.entity.util.IVillagerFear;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.google.common.base.Predicate;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.FleeSunGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RestrictSunGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import net.minecraft.entity.ai.controller.MovementController.Action;

public class EntityGhost extends MonsterEntity implements IAnimatedEntity, IVillagerFear, IAnimalFear, IHumanoid, IBlacklistedFromStatues {

    private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityGhost.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> CHARGING = EntityDataManager.createKey(EntityGhost.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_DAYTIME_MODE = EntityDataManager.createKey(EntityGhost.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> WAS_FROM_CHEST = EntityDataManager.createKey(EntityGhost.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> DAYTIME_COUNTER = EntityDataManager.createKey(EntityGhost.class, DataSerializers.VARINT);
    public static Animation ANIMATION_SCARE;
    public static Animation ANIMATION_HIT;
    private int animationTick;
    private Animation currentAnimation;


    public EntityGhost(EntityType type, World worldIn) {
        super(type, worldIn);
        ANIMATION_SCARE = Animation.create(30);
        ANIMATION_HIT = Animation.create(10);
        this.moveController = new MoveHelper(this);
    }


    protected ResourceLocation getLootTable() {
        return this.wasFromChest() ? LootTables.EMPTY : this.getType().getLootTable();
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return IafSoundRegistry.GHOST_IDLE;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return IafSoundRegistry.GHOST_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return IafSoundRegistry.GHOST_DIE;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, IafConfig.ghostMaxHealth)
                //FOLLOW_RANGE
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 64D)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15D)
                //ATTACK
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, IafConfig.ghostAttackStrength)
                //ARMOR
                .createMutableAttribute(Attributes.ARMOR, 1D);
    }

    public boolean isPotionApplicable(EffectInstance potioneffectIn) {
        return potioneffectIn.getPotion() != Effects.POISON  && potioneffectIn.getPotion() != Effects.WITHER && super.isPotionApplicable(potioneffectIn);
    }

    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source.isFireDamage() || source == DamageSource.IN_WALL || source == DamageSource.CACTUS
                || source == DamageSource.DROWN || source == DamageSource.FALLING_BLOCK || source == DamageSource.ANVIL || source == DamageSource.SWEET_BERRY_BUSH;
    }

    protected PathNavigator createNavigator(World worldIn) {
        return new GhostPathNavigator(this, worldIn);
    }

    public boolean isCharging() {
        return this.dataManager.get(CHARGING);
    }

    public void setCharging(boolean moving) {
        this.dataManager.set(CHARGING, moving);
    }

    public boolean isDaytimeMode() {
        return this.dataManager.get(IS_DAYTIME_MODE);
    }

    public void setDaytimeMode(boolean moving) {
        this.dataManager.set(IS_DAYTIME_MODE, moving);
    }

    public boolean wasFromChest() {
        return this.dataManager.get(WAS_FROM_CHEST);
    }

    public void setFromChest(boolean moving) {
        this.dataManager.set(WAS_FROM_CHEST, moving);
    }


    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.UNDEAD;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeTurnedToStone() {
        return false;
    }

    @Override
    protected void collideWithEntity(Entity entity) {
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new GhostAICharge(this));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F, 1.0F) {
            public boolean shouldContinueExecuting() {
                if (this.closestEntity != null && this.closestEntity instanceof PlayerEntity && ((PlayerEntity) this.closestEntity).isCreative()) {
                    return false;
                }
                return super.shouldContinueExecuting();
            }
        });
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.6D) {
            public boolean shouldExecute() {
                executionChance = 60;
                return super.shouldExecute();
            }
        });
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, PlayerEntity.class, 0, false, false, new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity entity) {
                return entity.isAlive();
            }
        }));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, LivingEntity.class, 0, false, false, new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity entity) {
                return entity instanceof LivingEntity && DragonUtils.isAlive((LivingEntity) entity) && DragonUtils.isVillager(entity);
            }
        }));
    }

    public void livingTick() {
        super.livingTick();
        this.noClip = true;
        if(!world.isRemote){
            boolean day = isInDaylight() && !this.wasFromChest();
            if(day){
                if(!this.isDaytimeMode()){
                    this.setAnimation(ANIMATION_SCARE);
                }
                this.setDaytimeMode(true);
            }else{
                this.setDaytimeMode(false);
                this.setDaytimeCounter(0);
            }
            if(isDaytimeMode()){
                this.setMotion(Vector3d.ZERO);
                this.setDaytimeCounter(this.getDaytimeCounter() + 1);
                if(getDaytimeCounter() >= 100){
                    this.setInvisible(true);
                }
            }else{
                this.setInvisible(this.isPotionActive(Effects.INVISIBILITY));
                this.setDaytimeCounter(0);
            }
        }else{
            if(this.getAnimation() == ANIMATION_SCARE && this.getAnimationTick() == 3 && !this.isHauntedShoppingList() && rand.nextInt(3) == 0){
                this.playSound(IafSoundRegistry.GHOST_JUMPSCARE, this.getSoundVolume(), this.getSoundPitch());
                if(world.isRemote){
                    IceAndFire.PROXY.spawnParticle("ghost_appearance", this.getPosX(), this.getPosY(), this.getPosZ(), this.getEntityId(), 0, 0);
                }
            }
        }
        if(this.getAnimation() == ANIMATION_HIT && this.getAttackTarget() != null){
            if(this.getDistance(this.getAttackTarget()) < 1.4D && this.getAnimationTick() >= 4 && this.getAnimationTick() < 6) {
                this.playSound(IafSoundRegistry.GHOST_ATTACK, this.getSoundVolume(), this.getSoundPitch());
                this.attackEntityAsMob(this.getAttackTarget());
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean isAIDisabled() {
        return this.isDaytimeMode() || super.isAIDisabled();
    }

    public boolean isSilent() {
        return this.isDaytimeMode() || super.isSilent();
    }

    protected boolean isInDaylight() {
        if (this.world.isDaytime() && !this.world.isRemote) {
            float f = this.getBrightness();
            BlockPos blockpos = this.getRidingEntity() instanceof BoatEntity ? (new BlockPos(this.getPosX(), (double)Math.round(this.getPosY()), this.getPosZ())).up() : new BlockPos(this.getPosX(), (double)Math.round(this.getPosY() + 4), this.getPosZ());
            if (f > 0.5F  && this.world.canSeeSky(blockpos)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasNoGravity() {
        return true;
    }

    public ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (itemstack != null && itemstack.getItem() == IafItemRegistry.MANUSCRIPT && !this.isHauntedShoppingList()) {
            this.setColor(-1);
            this.playSound(IafSoundRegistry.BESTIARY_PAGE, 1, 1);
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            return ActionResultType.SUCCESS;
        }
        return super.getEntityInteractionResult(player, hand);
    }

    @Override
    public void travel(Vector3d vec) {
        float f4;
        if (this.isDaytimeMode()) {
            super.travel(Vector3d.ZERO);
            return;
        }
        super.travel(vec);
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setColor(this.rand.nextInt(3));
        if (rand.nextInt(200) == 0) {
            this.setColor(-1);
        }

        return spawnDataIn;
    }


    @Override
    protected void registerData() {
        super.registerData();
        this.getDataManager().register(COLOR, Integer.valueOf(0));
        this.getDataManager().register(CHARGING, false);
        this.getDataManager().register(IS_DAYTIME_MODE, false);
        this.getDataManager().register(WAS_FROM_CHEST, false);
        this.getDataManager().register(DAYTIME_COUNTER, Integer.valueOf(0));
    }

    public int getColor() {
        return MathHelper.clamp(this.getDataManager().get(COLOR).intValue(), -1, 2);
    }

    public void setColor(int color) {
        this.getDataManager().set(COLOR, color);
    }

    public int getDaytimeCounter() {
        return this.getDataManager().get(DAYTIME_COUNTER).intValue();
    }

    public void setDaytimeCounter(int counter) {
        this.getDataManager().set(DAYTIME_COUNTER, counter);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        this.setColor(compound.getInt("Color"));
        this.setDaytimeMode(compound.getBoolean("DaytimeMode"));
        this.setDaytimeCounter(compound.getInt("DaytimeCounter"));
        this.setFromChest(compound.getBoolean("FromChest"));
        super.readAdditional(compound);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        compound.putInt("Color", this.getColor());
        compound.putBoolean("DaytimeMode", this.isDaytimeMode());
        compound.putInt("DaytimeCounter", this.getDaytimeCounter());
        compound.putBoolean("FromChest", this.wasFromChest());
        super.writeAdditional(compound);
    }

    public boolean isHauntedShoppingList() {
        return this.getColor() == -1;
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
        return new Animation[]{NO_ANIMATION, ANIMATION_SCARE, ANIMATION_HIT};
    }


    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return false;
    }

    class MoveHelper extends MovementController {
        EntityGhost ghost;

        public MoveHelper(EntityGhost ghost) {
            super(ghost);
            this.ghost = ghost;
        }

        public void tick() {
            if (this.action == Action.MOVE_TO) {
                Vector3d vec3d = new Vector3d(this.getX() - ghost.getPosX(), this.getY() - ghost.getPosY(), this.getZ() - ghost.getPosZ());
                double d0 = vec3d.length();
                double edgeLength = ghost.getBoundingBox().getAverageEdgeLength();
                if (d0 < edgeLength) {
                    this.action = Action.WAIT;
                    ghost.setMotion(ghost.getMotion().scale(0.5D));
                } else {
                    ghost.setMotion(ghost.getMotion().add(vec3d.scale(this.speed * 0.5D * 0.05D / d0)));
                    if (ghost.getAttackTarget() == null) {
                        Vector3d vec3d1 = ghost.getMotion();
                        ghost.rotationYaw = -((float) MathHelper.atan2(vec3d1.x, vec3d1.z)) * (180F / (float) Math.PI);
                        ghost.renderYawOffset = ghost.rotationYaw;
                    } else {
                        double d4 = ghost.getAttackTarget().getPosX() - ghost.getPosX();
                        double d5 = ghost.getAttackTarget().getPosZ() - ghost.getPosZ();
                        ghost.rotationYaw = -((float) MathHelper.atan2(d4, d5)) * (180F / (float) Math.PI);
                        ghost.renderYawOffset = ghost.rotationYaw;
                    }
                }
            }
        }
    }
}
