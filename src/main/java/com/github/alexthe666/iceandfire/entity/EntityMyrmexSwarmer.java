package com.github.alexthe666.iceandfire.entity;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.entity.ai.EntityAIAttackMeleeNoCooldown;
import com.github.alexthe666.iceandfire.entity.ai.MyrmexAIFollowSummoner;
import com.github.alexthe666.iceandfire.entity.ai.MyrmexAISummonerHurtByTarget;
import com.github.alexthe666.iceandfire.entity.ai.MyrmexAISummonerHurtTarget;
import com.github.alexthe666.iceandfire.entity.ai.MyrmexAIWander;

import com.github.alexthe666.iceandfire.pathfinding.raycoms.AdvancedPathNavigate;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.github.alexthe666.iceandfire.entity.EntityMyrmexRoyal.AIFlyAtTarget;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexRoyal.AIFlyRandom;

public class EntityMyrmexSwarmer extends EntityMyrmexRoyal {

    private static final DataParameter<Optional<UUID>> SUMMONER_ID = EntityDataManager.createKey(EntityMyrmexSwarmer.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private static final DataParameter<Integer> TICKS_ALIVE = EntityDataManager.createKey(EntityMyrmexSwarmer.class, DataSerializers.VARINT);

    public EntityMyrmexSwarmer(EntityType type, World worldIn) {
        super(type, worldIn);
        this.moveController = new EntityMyrmexRoyal.FlyMoveHelper(this);
        this.navigator = createNavigator(world, AdvancedPathNavigate.MovementType.FLYING);
        switchNavigator(false);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, 5)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.35D)
                //ATTACK
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 2)
                //FOLLOW RANGE
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 64.0D)
                //ARMOR
                .createMutableAttribute(Attributes.ARMOR, 0D);
    }

    protected int getExperiencePoints(PlayerEntity player) {
        return 0;
    }

    protected void switchNavigator(boolean onLand) {
    }

    protected double attackDistance() {
        return 25;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new MyrmexAIFollowSummoner(this, 1.0D, 10.0F, 5.0F));
        this.goalSelector.addGoal(2, new AIFlyAtTarget());
        this.goalSelector.addGoal(3, new AIFlyRandom());
        this.goalSelector.addGoal(4, new EntityAIAttackMeleeNoCooldown(this, 1.0D, true));
        this.goalSelector.addGoal(5, new MyrmexAIWander(this, 1D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new MyrmexAISummonerHurtByTarget(this));
        this.targetSelector.addGoal(3, new MyrmexAISummonerHurtTarget(this));
    }

    protected void collideWithEntity(Entity entityIn) {
        if (entityIn instanceof EntityMyrmexSwarmer) {
            super.collideWithEntity(entityIn);
        }
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(SUMMONER_ID, Optional.empty());
        this.dataManager.register(TICKS_ALIVE, 0);
    }

    @Nullable
    public LivingEntity getSummoner() {
        try {
            UUID uuid = this.getSummonerUUID();
            return uuid == null ? null : this.world.getPlayerByUuid(uuid);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    public boolean isOnSameTeam(Entity entityIn) {
        if (entityIn == null) {
            return false;
        }
        if (this.getSummonerUUID() == null || entityIn instanceof EntityMyrmexSwarmer && ((EntityMyrmexSwarmer) entityIn).getSummonerUUID() == null) {
            return false;
        }
        if (entityIn instanceof TameableEntity) {
            UUID ownerID = ((TameableEntity) entityIn).getOwnerId();
            return ownerID != null && ownerID.equals(this.getSummonerUUID());
        }
        return entityIn.getUniqueID().equals(this.getSummonerUUID()) || entityIn instanceof EntityMyrmexSwarmer && ((EntityMyrmexSwarmer) entityIn).getSummonerUUID() != null && ((EntityMyrmexSwarmer) entityIn).getSummonerUUID().equals(this.getSummonerUUID());
    }

    public void setSummonerID(@Nullable UUID uuid) {
        this.dataManager.set(SUMMONER_ID, Optional.ofNullable(uuid));
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if (this.getSummonerUUID() == null) {
            compound.putString("SummonerUUID", "");
        } else {
            compound.putString("SummonerUUID", this.getSummonerUUID().toString());
        }
        compound.putInt("SummonTicks", this.getTicksAlive());

    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        String s = "";
        if (compound.hasUniqueId("SummonerUUID")) {
            s = compound.getString("SummonerUUID");
        }
        if (!s.isEmpty()) {
            try {
                this.setSummonerID(UUID.fromString(s));
            } catch (Throwable var4) {
            }
        }
        this.setTicksAlive(compound.getInt("SummonTicks"));
    }

    public void setSummonedBy(PlayerEntity player) {
        this.setSummonerID(player.getUniqueID());
    }

    @Nullable
    public UUID getSummonerUUID() {
        return (UUID) ((Optional) this.dataManager.get(SUMMONER_ID)).orElse(null);
    }

    public int getTicksAlive() {
        return this.dataManager.get(TICKS_ALIVE).intValue();
    }

    public void setTicksAlive(int ticks) {
        this.dataManager.set(TICKS_ALIVE, ticks);
    }

    public void livingTick() {
        super.livingTick();
        setFlying(true);
        boolean flying = this.isFlying() && !this.onGround;
        setTicksAlive(getTicksAlive() + 1);
        if (flying) {
            this.setMotion(this.getMotion().add(0, -0.08D, 0));
            if (this.moveController.getY() > this.getPosY()) {
                this.setMotion(this.getMotion().add(0, 0.08D, 0));
            }
        }
        if (this.onGround) {
            this.setMotion(this.getMotion().add(0, 0.2D, 0));
        }
        if (this.getAttackTarget() != null) {
            this.moveController.setMoveTo(this.getAttackTarget().getPosX(), this.getAttackTarget().getBoundingBox().minY, this.getAttackTarget().getPosZ(), 1.0F);
            if (this.getAttackBounds().intersects(this.getAttackTarget().getBoundingBox())) {
                this.setAnimation(rand.nextBoolean() ? ANIMATION_BITE : ANIMATION_STING);
            }
        }
        if (this.getTicksAlive() > 1800) {
            this.onKillCommand();
        }
        if (this.getAnimation() == ANIMATION_BITE && this.getAttackTarget() != null && this.getAnimationTick() == 6) {
            this.playBiteSound();
            double dist = this.getDistanceSq(this.getAttackTarget());
            if (dist < attackDistance()) {
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
            }
        }
        if (this.getAnimation() == ANIMATION_STING && this.getAttackTarget() != null && this.getAnimationTick() == 6) {
            this.playStingSound();
            double dist = this.getDistanceSq(this.getAttackTarget());
            if (dist < attackDistance()) {
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 2));
                this.getAttackTarget().addPotionEffect(new EffectInstance(Effects.POISON, 70, 1));
            }
        }
    }

    public int getGrowthStage() {
        return 2;
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return null;
    }

    @Override
    public float getModelScale() {
        return 0.25F;
    }

    public boolean shouldHaveNormalAI() {
        return false;
    }

    @Override
    public int getCasteImportance() {
        return 0;
    }

    public boolean isBreedingSeason() {
        return false;
    }

    public boolean shouldAttackEntity(LivingEntity attacker, LivingEntity LivingEntity) {
        return !isOnSameTeam(attacker);
    }
}
