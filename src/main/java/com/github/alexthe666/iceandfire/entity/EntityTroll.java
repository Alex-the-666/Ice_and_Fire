package com.github.alexthe666.iceandfire.entity;

import java.util.Random;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.api.event.GenericGriefEvent;
import com.github.alexthe666.iceandfire.entity.ai.TrollAIFleeSun;
import com.github.alexthe666.iceandfire.entity.util.BlockBreakExplosion;
import com.github.alexthe666.iceandfire.entity.util.IHumanoid;
import com.github.alexthe666.iceandfire.entity.util.IVillagerFear;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;

import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
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
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.*;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.MinecraftForge;

public class EntityTroll extends MonsterEntity implements IAnimatedEntity, IVillagerFear, IHumanoid {

    public static final Animation ANIMATION_STRIKE_HORIZONTAL = Animation.create(20);
    public static final Animation ANIMATION_STRIKE_VERTICAL = Animation.create(20);
    public static final Animation ANIMATION_SPEAK = Animation.create(10);
    public static final Animation ANIMATION_ROAR = Animation.create(25);
    public static final ResourceLocation FOREST_LOOT = new ResourceLocation("iceandfire", "entities/troll_forest");
    public static final ResourceLocation FROST_LOOT = new ResourceLocation("iceandfire", "entities/troll_frost");
    public static final ResourceLocation MOUNTAIN_LOOT = new ResourceLocation("iceandfire", "entities/troll_mountain");
    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityTroll.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> WEAPON = EntityDataManager.createKey(EntityTroll.class, DataSerializers.VARINT);
    public float stoneProgress;
    private int animationTick;
    private Animation currentAnimation;
    private boolean avoidSun = true;

    public EntityTroll(EntityType t, World worldIn) {
        super(t, worldIn);
    }

    public static boolean canTrollSpawnOn(EntityType<? extends MobEntity> typeIn, IServerWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        return worldIn.getDifficulty() != Difficulty.PEACEFUL && isValidLightLevel(worldIn, pos, randomIn) && canSpawnOn(IafEntityRegistry.TROLL, worldIn, reason, pos, randomIn);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, IafConfig.trollMaxHealth)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.35D)
                //ATTACK
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, IafConfig.trollAttackStrength)
                //KNOCKBACK RESIST
                .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                //ARMOR
                .createMutableAttribute(Attributes.ARMOR, 9.0D);
    }

    private void setAvoidSun(boolean day) {
        if (day && !avoidSun) {
            ((GroundPathNavigator) this.getNavigator()).setAvoidSun(true);
            avoidSun = true;
        }
        if (!day && avoidSun) {
            ((GroundPathNavigator) this.getNavigator()).setAvoidSun(false);
            avoidSun = false;
        }
    }

    public boolean isNotColliding(IWorldReader worldIn) {
        return worldIn.checkNoEntityCollision(this);
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        BlockPos pos = this.getPosition();
        BlockPos heightAt = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE, pos);
        boolean rngCheck = true;
        if (IafConfig.trollSpawnCheckChance > 0) {
            rngCheck = this.getRNG().nextInt(IafConfig.trollSpawnCheckChance) == 0;
        }
        if (worldIn instanceof IServerWorld && !IafWorldRegistry.isDimensionListedForMobs((IServerWorld) world)) {
            return false;
        }
        return rngCheck && pos.getY() < heightAt.getY() - 10 && super.canSpawn(worldIn, spawnReasonIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new TrollAIFleeSun(this, 1.0D));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 8.0F, 1.0F));
        this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, AbstractVillagerEntity.class, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, PlayerEntity.class, false));
        setAvoidSun(true);
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.getRNG().nextBoolean()) {
            this.setAnimation(ANIMATION_STRIKE_VERTICAL);

        } else {
            this.setAnimation(ANIMATION_STRIKE_HORIZONTAL);
        }
        return true;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(VARIANT, Integer.valueOf(0));
        this.dataManager.register(WEAPON, Integer.valueOf(0));
    }

    private int getVariant() {
        return this.dataManager.get(VARIANT).intValue();
    }

    private void setVariant(int variant) {
        this.dataManager.set(VARIANT, variant);
    }

    public EnumTroll getTrollType() {
        return EnumTroll.values()[getVariant()];
    }

    public void setTrollType(EnumTroll variant) {
        this.setVariant(variant.ordinal());
    }

    private int getWeapon() {
        return this.dataManager.get(WEAPON).intValue();
    }

    private void setWeapon(int variant) {
        this.dataManager.set(WEAPON, variant);
    }

    public EnumTroll.Weapon getWeaponType() {
        return EnumTroll.Weapon.values()[getWeapon()];
    }

    public void setWeaponType(EnumTroll.Weapon variant) {
        this.setWeapon(variant.ordinal());
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putInt("Weapon", this.getWeapon());
        compound.putFloat("StoneProgress", stoneProgress);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setVariant(compound.getInt("Variant"));
        this.setWeapon(compound.getInt("Weapon"));
        this.stoneProgress = compound.getFloat("StoneProgress");
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setTrollType(EnumTroll.getBiomeType(world.getBiome(this.getPosition())));
        this.setWeaponType(EnumTroll.getWeaponForType(this.getTrollType()));
        return spawnDataIn;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (source.getDamageType().contains("arrow")) {
            return false;
        }
        return super.attackEntityFrom(source, damage);
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        switch (this.getTrollType()) {
            case MOUNTAIN:
                return MOUNTAIN_LOOT;
            case FROST:
                return FROST_LOOT;
            case FOREST:
                return FOREST_LOOT;
        }
        return null;
    }

    protected int getExperiencePoints(PlayerEntity player) {
        return 15;
    }

    protected void onDeathUpdate() {
        super.onDeathUpdate();
        if (this.deathTime == 20 && !this.world.isRemote) {
            if (IafConfig.trollsDropWeapon) {
                if (this.getRNG().nextInt(3) == 0) {
                    ItemStack weaponStack = new ItemStack(this.getWeaponType().item, 1);
                    weaponStack.attemptDamageItem(this.getRNG().nextInt(250), this.getRNG(), null);
                    dropItemAt(weaponStack, this.getPosX(), this.getPosY(), this.getPosZ());
                } else {
                    ItemStack brokenDrop = new ItemStack(Blocks.STONE_BRICKS, this.getRNG().nextInt(2) + 1);
                    ItemStack brokenDrop2 = new ItemStack(Blocks.STONE_BRICKS, this.getRNG().nextInt(2) + 1);
                    if (this.getWeaponType() == EnumTroll.Weapon.AXE) {
                        brokenDrop = new ItemStack(Items.STICK, this.getRNG().nextInt(2) + 1);
                        brokenDrop2 = new ItemStack(Blocks.COBBLESTONE, this.getRNG().nextInt(2) + 1);
                    }
                    if (this.getWeaponType() == EnumTroll.Weapon.COLUMN) {
                        brokenDrop = new ItemStack(Blocks.STONE_BRICKS, this.getRNG().nextInt(2) + 1);
                        brokenDrop2 = new ItemStack(Blocks.STONE_BRICKS, this.getRNG().nextInt(2) + 1);
                    }
                    if (this.getWeaponType() == EnumTroll.Weapon.COLUMN_FOREST) {
                        brokenDrop = new ItemStack(Blocks.STONE_BRICKS, this.getRNG().nextInt(2) + 1);
                        brokenDrop2 = new ItemStack(Blocks.STONE_BRICKS, this.getRNG().nextInt(2) + 1);
                    }
                    if (this.getWeaponType() == EnumTroll.Weapon.COLUMN_FROST) {
                        brokenDrop = new ItemStack(Blocks.STONE_BRICKS, this.getRNG().nextInt(2) + 1);
                        brokenDrop2 = new ItemStack(Items.SNOWBALL, this.getRNG().nextInt(4) + 1);
                    }
                    if (this.getWeaponType() == EnumTroll.Weapon.HAMMER) {
                        brokenDrop = new ItemStack(Items.BONE, this.getRNG().nextInt(2) + 1);
                        brokenDrop2 = new ItemStack(Blocks.COBBLESTONE, this.getRNG().nextInt(2) + 1);
                    }
                    if (this.getWeaponType() == EnumTroll.Weapon.TRUNK) {
                        brokenDrop = new ItemStack(Blocks.OAK_LOG, this.getRNG().nextInt(2) + 1);
                        brokenDrop2 = new ItemStack(Blocks.OAK_LOG, this.getRNG().nextInt(2) + 1);
                    }
                    if (this.getWeaponType() == EnumTroll.Weapon.TRUNK_FROST) {
                        brokenDrop = new ItemStack(Blocks.SPRUCE_LOG, this.getRNG().nextInt(4) + 1);
                        brokenDrop2 = new ItemStack(Items.SNOWBALL, this.getRNG().nextInt(4) + 1);
                    }
                    dropItemAt(brokenDrop, this.getPosX(), this.getPosY(), this.getPosZ());
                    dropItemAt(brokenDrop2, this.getPosX(), this.getPosY(), this.getPosZ());

                }
            }
        }
    }

    @Nullable
    private ItemEntity dropItemAt(ItemStack stack, double x, double y, double z) {
        if (stack.getCount() > 0) {
            ItemEntity entityitem = new ItemEntity(this.world, x, y, z, stack);
            entityitem.setDefaultPickupDelay();
            this.world.addEntity(entityitem);
            return entityitem;
        }
        return null;

    }

    public void livingTick() {
        super.livingTick();
        if (world.getDifficulty() == Difficulty.PEACEFUL && this.getAttackTarget() instanceof PlayerEntity) {
            this.setAttackTarget(null);
        }
        boolean stone = EntityGorgon.isStoneMob(this);
        if (stone && stoneProgress < 20.0F) {
            stoneProgress += 2F;
        } else if (!stone && stoneProgress > 0.0F) {
            stoneProgress -= 2F;
        }
        if (!stone && this.getAnimation() == NO_ANIMATION && this.getAttackTarget() != null && this.getRNG().nextInt(100) == 0) {
            this.setAnimation(ANIMATION_ROAR);
        }
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() == 5) {
            this.playSound(IafSoundRegistry.TROLL_ROAR, 1, 1);
        }
        if (!stone && this.getHealth() < this.getMaxHealth() && this.ticksExisted % 30 == 0) {
            this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 30, 1, false, false));
        }
        setAvoidSun(this.world.isDaytime());
        if (this.world.isDaytime() && !this.world.isRemote) {
            float f = this.getBrightness();
            BlockPos blockpos = this.getRidingEntity() instanceof BoatEntity ? (new BlockPos(this.getPosX(), (double) Math.round(this.getPosY()), this.getPosZ())).up() : new BlockPos(this.getPosX(), (double) Math.round(this.getPosY()), this.getPosZ());
            if (f > 0.5F && this.world.canSeeSky(blockpos)) {
                this.setMotion(0, 0, 0);
                this.setAnimation(NO_ANIMATION);
                this.playSound(IafSoundRegistry.TURN_STONE, 1, 1);
                this.stoneProgress = 20;
                EntityStoneStatue statue = EntityStoneStatue.buildStatueEntity(this);
                statue.getTrappedTag().putFloat("StoneProgress", 20);
                statue.setPositionAndRotation(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, this.rotationPitch);
                if (!world.isRemote) {
                    world.addEntity(statue);
                }
                statue.prevRotationYaw = this.rotationYaw;
                statue.rotationYaw = this.rotationYaw;
                statue.rotationYawHead = this.rotationYaw;
                statue.renderYawOffset = this.rotationYaw;
                statue.prevRenderYawOffset = this.rotationYaw;
                this.remove();
            }
        }
        if (this.getAnimation() == ANIMATION_STRIKE_VERTICAL && this.getAnimationTick() == 10) {
            float weaponX = (float) (getPosX() + 1.9F * Math.cos((renderYawOffset + 90) * Math.PI / 180));
            float weaponZ = (float) (getPosZ() + 1.9F * Math.sin((renderYawOffset + 90) * Math.PI / 180));
            float weaponY = (float) (getPosY() + (0.2F));
            BlockState state = world.getBlockState(new BlockPos(weaponX, weaponY - 1, weaponZ));
            for (int i = 0; i < 20; i++) {
                double motionX = getRNG().nextGaussian() * 0.07D;
                double motionY = getRNG().nextGaussian() * 0.07D;
                double motionZ = getRNG().nextGaussian() * 0.07D;
                if (state.getMaterial().isSolid() && world.isRemote) {
                    this.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, state), weaponX + (this.getRNG().nextFloat() - 0.5F), weaponY + (this.getRNG().nextFloat() - 0.5F), weaponZ + (this.getRNG().nextFloat() - 0.5F), motionX, motionY, motionZ);
                }
            }
        }
        if (this.getAnimation() == ANIMATION_STRIKE_VERTICAL && this.getAttackTarget() != null && this.getDistanceSq(this.getAttackTarget()) < 4D && this.getAnimationTick() == 10 && this.deathTime <= 0) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
        }
        if (this.getAnimation() == ANIMATION_STRIKE_HORIZONTAL && this.getAttackTarget() != null && this.getDistanceSq(this.getAttackTarget()) < 4D && this.getAnimationTick() == 10 && this.deathTime <= 0) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
            float f1 = 0.5F;
            float f2 = this.getAttackTarget().moveForward;
            float f3 = 0.6F;
            float f4 = MathHelper.sqrt(f2 * f2 + f3 * f3);

            if (f4 < 1.0F) {
                f4 = 1.0F;
            }

            f4 = f1 / f4;
            f2 = f2 * f4;
            f3 = f3 * f4;
            float f5 = MathHelper.sin(this.rotationYaw * 0.017453292F);
            float f6 = MathHelper.cos(this.rotationYaw * 0.017453292F);
            float f7 = f2 * f6 - f3 * f5;
            float f8 = f3 * f6 + f2 * f5;
            this.getAttackTarget().setMotion(f5, f6, 0.4F);
        }
        if (this.getNavigator().noPath() && this.getAttackTarget() != null && this.getDistanceSq(this.getAttackTarget()) > 3 && this.getDistanceSq(this.getAttackTarget()) < 30 && this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
            this.faceEntity(this.getAttackTarget(), 30, 30);
            if (this.getAnimation() == NO_ANIMATION && this.rand.nextInt(15) == 0) {
                this.setAnimation(ANIMATION_STRIKE_VERTICAL);
            }
            if (this.getAnimation() == ANIMATION_STRIKE_VERTICAL && this.getAnimationTick() == 10) {
                float weaponX = (float) (getPosX() + 1.9F * Math.cos((renderYawOffset + 90) * Math.PI / 180));
                float weaponZ = (float) (getPosZ() + 1.9F * Math.sin((renderYawOffset + 90) * Math.PI / 180));
                float weaponY = (float) (getPosY() + (this.getEyeHeight() / 2));
                BlockState state = world.getBlockState(new BlockPos(weaponX, weaponY, weaponZ));
                BlockBreakExplosion explosion = new BlockBreakExplosion(world, this, weaponX, weaponY, weaponZ, 1F + this.getRNG().nextFloat());
                if (!MinecraftForge.EVENT_BUS.post(new GenericGriefEvent(this, weaponX, weaponY, weaponZ))) {
                    explosion.doExplosionA();
                    explosion.doExplosionB(true);
                }

                this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1, 1);

            }
        }
        if (this.getAnimation() == ANIMATION_STRIKE_VERTICAL && this.getAnimationTick() == 10) {
            this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 2.5F, 0.5F);

        }
        if (this.getAnimation() == ANIMATION_STRIKE_HORIZONTAL && this.getAnimationTick() == 10) {
            this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 2.5F, 0.5F);
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
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

    @Nullable
    protected SoundEvent getAmbientSound() {
        return IafSoundRegistry.TROLL_IDLE;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return IafSoundRegistry.TROLL_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return IafSoundRegistry.TROLL_DIE;
    }


    @Override
    public Animation[] getAnimations() {
        return new Animation[]{NO_ANIMATION, ANIMATION_STRIKE_HORIZONTAL, ANIMATION_STRIKE_VERTICAL, ANIMATION_SPEAK, ANIMATION_ROAR};
    }
}
