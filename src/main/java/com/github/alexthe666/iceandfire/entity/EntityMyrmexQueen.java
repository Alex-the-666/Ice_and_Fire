package com.github.alexthe666.iceandfire.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.api.event.GenericGriefEvent;
import com.github.alexthe666.iceandfire.entity.ai.*;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.MyrmexTrades;
import com.github.alexthe666.iceandfire.world.gen.WorldGenMyrmexHive;
import com.google.common.base.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

public class EntityMyrmexQueen extends EntityMyrmexBase {

    public static final Animation ANIMATION_BITE = Animation.create(15);
    public static final Animation ANIMATION_STING = Animation.create(15);
    public static final Animation ANIMATION_EGG = Animation.create(20);
    public static final Animation ANIMATION_DIGNEST = Animation.create(45);
    public static final ResourceLocation DESERT_LOOT = new ResourceLocation("iceandfire", "entities/myrmex_queen_desert");
    public static final ResourceLocation JUNGLE_LOOT = new ResourceLocation("iceandfire", "entities/myrmex_queen_jungle");
    private static final ResourceLocation TEXTURE_DESERT = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_desert_queen.png");
    private static final ResourceLocation TEXTURE_JUNGLE = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_jungle_queen.png");
    private static final DataParameter<Boolean> HASMADEHOME = EntityDataManager.createKey(EntityMyrmexQueen.class, DataSerializers.BOOLEAN);
    private int eggTicks = 0;

    public EntityMyrmexQueen(EntityType t, World worldIn) {
        super(t, worldIn);
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return isJungle() ? JUNGLE_LOOT : DESERT_LOOT;
    }

    protected int getExperiencePoints(PlayerEntity player) {
        return 20;
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(HASMADEHOME, Boolean.valueOf(true));
    }

    @Override
    protected VillagerTrades.ITrade[] getLevel1Trades() {
        return isJungle() ? MyrmexTrades.JUNGLE_QUEEN.get(1) : MyrmexTrades.DESERT_QUEEN.get(1);
    }

    @Override
    protected VillagerTrades.ITrade[] getLevel2Trades() {
        return isJungle() ? MyrmexTrades.JUNGLE_QUEEN.get(2) : MyrmexTrades.DESERT_QUEEN.get(2);
    }

    public void setCustomName(ITextComponent name) {
        if (!this.getHive().colonyName.equals(name.getUnformattedComponentText())) {
            if (this.getHive() != null) {
                this.getHive().colonyName = name.getString();
            }
        }
        super.setCustomName(name);
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        tag.putInt("EggTicks", eggTicks);
        tag.putBoolean("MadeHome", this.hasMadeHome());

    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        this.eggTicks = tag.getInt("EggTicks");
        this.setMadeHome(tag.getBoolean("MadeHome"));
    }

    public boolean hasMadeHome() {
        return this.dataManager.get(HASMADEHOME).booleanValue();
    }

    public void setMadeHome(boolean madeHome) {
        this.dataManager.set(HASMADEHOME, madeHome);
    }

    public void livingTick() {
        super.livingTick();
        if (this.getAnimation() == ANIMATION_DIGNEST) {
            spawnGroundEffects(3);
        }
        if (this.getHive() != null) {
            this.getHive().tick(0, world);
        }

        if (hasMadeHome() && this.getGrowthStage() >= 2 && !this.canSeeSky()) {
            eggTicks++;
        } else if (this.canSeeSky()) {
            this.setAnimation(ANIMATION_DIGNEST);
            if (this.getAnimationTick() == 42) {
                int down = Math.max(15, this.getPosition().getY() - 20 + this.getRNG().nextInt(10));
                BlockPos genPos = new BlockPos(this.getPosX(), down, this.getPosZ());
                if (!MinecraftForge.EVENT_BUS.post(new GenericGriefEvent(this, genPos.getX(), genPos.getY(), genPos.getZ()))) {
                    WorldGenMyrmexHive hiveGen = new WorldGenMyrmexHive(true, this.isJungle(), NoFeatureConfig.CODEC);
                    if (!world.isRemote && world instanceof ServerWorld) {
                        hiveGen.placeSmallGen((ServerWorld)world, this.getRNG(), genPos);
                    }
                    this.setMadeHome(true);
                    this.setLocationAndAngles(genPos.getX(), down, genPos.getZ(), 0, 0);
                    this.addPotionEffect(new EffectInstance(Effects.INVISIBILITY, 30));
                    this.setHive(hiveGen.hive);
                    for (int i = 0; i < 3; i++) {
                        EntityMyrmexWorker worker = new EntityMyrmexWorker(IafEntityRegistry.MYRMEX_WORKER, world);
                        worker.copyLocationAndAnglesFrom(this);
                        worker.setHive(this.getHive());
                        worker.setJungleVariant(this.isJungle());
                        if (!world.isRemote) {
                            world.addEntity(worker);
                        }
                    }
                    return;
                }
            }
        }
        if (!world.isRemote && eggTicks > IafConfig.myrmexPregnantTicks && this.getHive() == null || !world.isRemote && this.getHive() != null && this.getHive().repopulate() && eggTicks > IafConfig.myrmexPregnantTicks) {
            float radius = -5.25F;
            float angle = (0.01745329251F * this.renderYawOffset);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            BlockPos eggPos = new BlockPos(this.getPosX() + extraX, this.getPosY() + 0.75F, this.getPosZ() + extraZ);
            if (world.isAirBlock(eggPos)) {
                this.setAnimation(ANIMATION_EGG);
                if (this.getAnimationTick() == 10) {
                    EntityMyrmexEgg egg = new EntityMyrmexEgg(IafEntityRegistry.MYRMEX_EGG, this.world);
                    egg.setJungle(this.isJungle());
                    int caste = getRandomCaste(world, this.getRNG(), getHive() == null || getHive().reproduces);
                    egg.setMyrmexCaste(caste);
                    egg.setLocationAndAngles(this.getPosX() + extraX, this.getPosY() + 0.75F, this.getPosZ() + extraZ, 0, 0);
                    if (getHive() != null) {
                        egg.hiveUUID = this.getHive().hiveUUID;
                    }
                    if (!world.isRemote) {
                        world.addEntity(egg);
                    }
                    eggTicks = 0;
                }

            }


        }
        if (this.getAnimation() == ANIMATION_BITE && this.getAttackTarget() != null && this.getAnimationTick() == 6) {
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
                LivingEntity attackTarget = this.getAttackTarget();
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 2));
                this.getAttackTarget().addPotionEffect(new EffectInstance(Effects.POISON, 200, 2));
                this.getAttackTarget().isAirBorne = true;
                float f = MathHelper.sqrt(0.5 * 0.5 + 0.5 * 0.5);
                this.getAttackTarget().isAirBorne = true;
                attackTarget.setMotion(attackTarget.getMotion().mul(0.5D, 1, 0.5D));
                attackTarget.setMotion(attackTarget.getMotion().add(-0.5 / (double) f * 4, 1, -0.5 / (double) f * 4));

                if (this.getAttackTarget().isOnGround()) {
                    attackTarget.setMotion(attackTarget.getMotion().add(0, 0.4, 0));
                }
            }
        }

    }

    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || this.getAnimation() == ANIMATION_DIGNEST;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(0, new MyrmexAITradePlayer(this));
        this.goalSelector.addGoal(0, new MyrmexAILookAtTradePlayer(this));
        this.goalSelector.addGoal(1, new MyrmexAIAttackMelee(this, 1.0D, true));
        this.goalSelector.addGoal(3, new MyrmexAIReEnterHive(this, 1.0D));
        this.goalSelector.addGoal(4, new MyrmexAIWanderHiveCenter(this, 1.0D));
        this.goalSelector.addGoal(5, new MyrmexQueenAIWander(this, 1D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new MyrmexAIDefendHive(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new MyrmexAIAttackPlayers(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, true, new Predicate<LivingEntity>() {
            public boolean apply(@Nullable LivingEntity entity) {
                return entity != null && !EntityMyrmexBase.haveSameHive(EntityMyrmexQueen.this, entity) && DragonUtils.isAlive(entity) && !(entity instanceof IMob);
            }
        }));

    }

    public void fall(float distance, float damageMultiplier) {
    }

    public boolean shouldMoveThroughHive() {
        return false;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, 120D)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2D)
                //ATTACK
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, IafConfig.myrmexBaseAttackStrength * 3.5D)
                //FOLLOW RANGE
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 128.0D)
                //ARMOR
                .createMutableAttribute(Attributes.ARMOR, 15.0D);
    }


    @Override
    public ResourceLocation getAdultTexture() {
        return isJungle() ? TEXTURE_JUNGLE : TEXTURE_DESERT;
    }

    @Override
    public float getModelScale() {
        return 1.75F;
    }

    @Override
    public int getCasteImportance() {
        return 3;
    }

    public boolean shouldLeaveHive() {
        return false;
    }

    public boolean shouldEnterHive() {
        return true;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.getGrowthStage() < 2) {
            return false;
        }
        if (this.getAnimation() != ANIMATION_STING && this.getAnimation() != ANIMATION_BITE) {
            this.setAnimation(this.getRNG().nextBoolean() ? ANIMATION_STING : ANIMATION_BITE);
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

    public boolean canMove() {
        return super.canMove() && this.hasMadeHome();
    }

    public void spawnGroundEffects(float size) {
        for (int i = 0; i < size * 3; i++) {
            for (int i1 = 0; i1 < 10; i1++) {
                double motionX = getRNG().nextGaussian() * 0.07D;
                double motionY = getRNG().nextGaussian() * 0.07D;
                double motionZ = getRNG().nextGaussian() * 0.07D;
                float radius = size * rand.nextFloat();
                float angle = (0.01745329251F * this.renderYawOffset) * 3.14F * rand.nextFloat();
                double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                double extraY = 0.8F;
                double extraZ = radius * MathHelper.cos(angle);

                BlockState BlockState = this.world.getBlockState(new BlockPos(MathHelper.floor(this.getPosX() + extraX), MathHelper.floor(this.getPosY() + extraY) - 1, MathHelper.floor(this.getPosZ() + extraZ)));
                if (BlockState.getMaterial() != Material.AIR) {
                    if (world.isRemote) {
                        world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, BlockState), true, this.getPosX() + extraX, this.getPosY() + extraY, this.getPosZ() + extraZ, motionX, motionY, motionZ);
                    }
                }
            }
        }
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_PUPA_WIGGLE, ANIMATION_BITE, ANIMATION_STING, ANIMATION_EGG, ANIMATION_DIGNEST};
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
