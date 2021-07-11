package com.github.alexthe666.iceandfire.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.ai.*;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.MyrmexTrades;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.item.ItemMyrmexEgg;
import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class EntityMyrmexWorker extends EntityMyrmexBase {

    public static final Animation ANIMATION_BITE = Animation.create(15);
    public static final Animation ANIMATION_STING = Animation.create(15);
    public static final ResourceLocation DESERT_LOOT = new ResourceLocation("iceandfire", "entities/myrmex_worker_desert");
    public static final ResourceLocation JUNGLE_LOOT = new ResourceLocation("iceandfire", "entities/myrmex_worker_jungle");
    private static final ResourceLocation TEXTURE_DESERT = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_desert_worker.png");
    private static final ResourceLocation TEXTURE_JUNGLE = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_jungle_worker.png");
    public boolean keepSearching = true;

    public EntityMyrmexWorker(EntityType t, World worldIn) {
        super(t, worldIn);
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return isJungle() ? JUNGLE_LOOT : DESERT_LOOT;
    }



    protected int getExperiencePoints(PlayerEntity player) {
        return 3;
    }

    public boolean isSmallerThanBlock(){
        return true;
    }

    public void livingTick() {
        super.livingTick();
        /*if (this.getAnimation() == ANIMATION_BITE && this.getAttackTarget() != null && this.getAnimationTick() == 6) {
            this.playBiteSound();
            if (this.getAttackBounds().intersects(this.getAttackTarget().getBoundingBox())) {
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
            }
        }
        if (this.getAnimation() == ANIMATION_STING && this.getAnimationTick() == 0) {
            this.playStingSound();
        }
        if (this.getAnimation() == ANIMATION_STING && this.getAttackTarget() != null && this.getAnimationTick() == 6) {
            if (this.getAttackBounds().intersects(this.getAttackTarget().getBoundingBox())) {
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 2));
                this.getAttackTarget().addPotionEffect(new EffectInstance(Effects.POISON, 60, 1));
            }
        }*/
        if (!this.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
            if (this.getHeldItem(Hand.MAIN_HAND).getItem() instanceof ItemMyrmexEgg) {
                boolean isJungle = this.getHeldItem(Hand.MAIN_HAND).getItem() == IafItemRegistry.MYRMEX_JUNGLE_EGG;
                CompoundNBT tag = this.getHeldItem(Hand.MAIN_HAND).getTag();
                int metadata = 0;
                if (tag != null) {
                    metadata = tag.getInt("EggOrdinal");
                }
                EntityMyrmexEgg egg = new EntityMyrmexEgg(IafEntityRegistry.MYRMEX_EGG, world);
                egg.copyLocationAndAnglesFrom(this);
                egg.setJungle(isJungle);
                egg.setMyrmexCaste(metadata);
                if (!world.isRemote) {
                    world.addEntity(egg);
                }
                egg.startRiding(this);
                this.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
            }
        }
        if (!this.getPassengers().isEmpty()) {
            for (Entity entity : this.getPassengers()) {
                if (entity instanceof EntityMyrmexBase && ((EntityMyrmexBase) entity).getGrowthStage() >= 2) {
                    entity.stopRiding();
                }
            }
        }
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(0, new MyrmexAITradePlayer(this));
        this.goalSelector.addGoal(0, new MyrmexAILookAtTradePlayer(this));
        this.goalSelector.addGoal(1, new MyrmexAIAttackMelee(this, 1.0D, true));
        this.goalSelector.addGoal(2, new MyrmexAIStoreBabies(this, 1.0D));
        this.goalSelector.addGoal(3, new MyrmexAIStoreItems(this, 1.0D));
        this.goalSelector.addGoal(4, new MyrmexAIReEnterHive(this, 1.0D));
        this.goalSelector.addGoal(4, new MyrmexAILeaveHive(this, 1.0D));
        this.goalSelector.addGoal(6, new MyrmexAIForage(this,2));
        this.goalSelector.addGoal(7, new MyrmexAIMoveThroughHive(this, 1.0D));
        this.goalSelector.addGoal(8, new MyrmexAIWander(this, 1D));
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new MyrmexAIDefendHive(this));
        this.targetSelector.addGoal(2, new MyrmexAIForageForItems(this));
        this.targetSelector.addGoal(3, new MyrmexAIPickupBabies(this));
        this.targetSelector.addGoal(4, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new MyrmexAIAttackPlayers(this));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, true, new Predicate<LivingEntity>() {
            public boolean apply(@Nullable LivingEntity entity) {
                return EntityMyrmexWorker.this.getHeldItemMainhand().isEmpty() && entity != null && !EntityMyrmexBase.haveSameHive(EntityMyrmexWorker.this, entity) && DragonUtils.isAlive(entity) && !(entity instanceof IMob);
            }
        }));


    }

    public boolean shouldWander() {
        return super.shouldWander() && this.canSeeSky();
    }

    @Override
    protected VillagerTrades.ITrade[] getLevel1Trades() {
        return isJungle() ? MyrmexTrades.JUNGLE_WORKER.get(1) : MyrmexTrades.DESERT_WORKER.get(1);
    }

    @Override
    protected VillagerTrades.ITrade[] getLevel2Trades() {
        return isJungle() ? MyrmexTrades.JUNGLE_WORKER.get(2) : MyrmexTrades.DESERT_WORKER.get(2);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, 20)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D)
                //ATTACK
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, IafConfig.myrmexBaseAttackStrength)
                //FOLLOW RANGE
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 32D)
                //ARMOR
                .createMutableAttribute(Attributes.ARMOR, 4D);
    }

    @Override
    public ResourceLocation getAdultTexture() {
        return isJungle() ? TEXTURE_JUNGLE : TEXTURE_DESERT;
    }

    @Override
    public float getModelScale() {
        return 0.6F;
    }

    public boolean shouldLeaveHive() {
        return !holdingSomething();
    }

    public boolean shouldEnterHive() {
        return holdingSomething() || !world.isDaytime();
    }

    public boolean shouldMoveThroughHive() {
        return !shouldLeaveHive() && !holdingSomething();
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.getGrowthStage() < 2) {
            return false;
        }

        if (this.getAnimation() != ANIMATION_STING && this.getAnimation() != ANIMATION_BITE) {
            this.setAnimation(this.getRNG().nextBoolean() ? ANIMATION_STING : ANIMATION_BITE);
            float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            this.setLastAttackedEntity(entityIn);
            boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);
            if (this.getAnimation() == ANIMATION_STING && flag){
                this.playStingSound();
                if(entityIn instanceof LivingEntity) {
                    ((LivingEntity)entityIn).addPotionEffect(new EffectInstance(Effects.POISON, 200, 2));
                    this.setAttackTarget((LivingEntity)entityIn);
                }
            }
            else{
                this.playBiteSound();
            }
            if (!this.world.isRemote && this.getRNG().nextInt(3) == 0 && this.getHeldItem(Hand.MAIN_HAND) != ItemStack.EMPTY) {
                this.entityDropItem(this.getHeldItem(Hand.MAIN_HAND), 0);
                this.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
            }
            if (!this.getPassengers().isEmpty()) {
                for (Entity entity : this.getPassengers()) {
                    entity.stopRiding();
                }
            }
            return true;
        }
        return false;
    }


    public boolean holdingSomething() {
        return this.getHeldEntity() != null || !this.getHeldItem(Hand.MAIN_HAND).isEmpty() || this.getAttackTarget() != null;
    }

    public boolean holdingBaby() {
        return this.getHeldEntity() != null && (this.getHeldEntity() instanceof EntityMyrmexBase || this.getHeldEntity() instanceof EntityMyrmexEgg);
    }

    @Override
    public int getCasteImportance() {
        return 0;
    }
    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_PUPA_WIGGLE, ANIMATION_BITE, ANIMATION_STING};
    }

    public void updatePassenger(Entity passenger) {
        super.updatePassenger(passenger);
        if (this.isPassenger(passenger)) {
            renderYawOffset = rotationYaw;
            float radius = 1.05F;
            float angle = (0.01745329251F * this.renderYawOffset);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            passenger.setPosition(this.getPosX() + extraX, this.getPosY() + 0.25F, this.getPosZ() + extraZ);
        }
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (amount >= 1.0D && !this.world.isRemote && this.getRNG().nextInt(3) == 0 && this.getHeldItem(Hand.MAIN_HAND) != ItemStack.EMPTY) {
            this.entityDropItem(this.getHeldItem(Hand.MAIN_HAND), 0);
            this.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
        }
        if (amount >= 1.0D && !this.getPassengers().isEmpty()) {
            for (Entity entity : this.getPassengers()) {
                entity.stopRiding();
            }
        }
        return super.attackEntityFrom(source, amount);
    }

    public Entity getHeldEntity() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    public void onPickupItem(ItemEntity itemEntity) {
        Item item = itemEntity.getItem().getItem();
        if (item == IafItemRegistry.MYRMEX_JUNGLE_RESIN && this.isJungle() || item == IafItemRegistry.MYRMEX_DESERT_RESIN && !this.isJungle()) {

            PlayerEntity owner = null;
            try {
                if (itemEntity.getThrowerId() != null) {
                    owner = this.world.getPlayerByUuid(itemEntity.getThrowerId());
                }
            } catch (Exception e) {
                IceAndFire.LOGGER.warn("Myrmex picked up resin that wasn't thrown!");
            }
            if (owner != null && this.getHive() != null) {
                this.getHive().modifyPlayerReputation(owner.getUniqueID(), 5);
                this.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1, 1);
                if (!world.isRemote) {
                    world.addEntity(new ExperienceOrbEntity(world, owner.getPosX(), owner.getPosY(), owner.getPosZ(), 1 + rand.nextInt(3)));
                }
            }
        }
    }

    @Override
    public int getXp() {
        return 0;
    }

    @Override
    public boolean hasXPBar() {
        return false;
    }
}
