package com.github.alexthe666.iceandfire.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.ai.GorgonAIStareAttack;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IAnimalFear;
import com.github.alexthe666.iceandfire.entity.util.IBlacklistedFromStatues;
import com.github.alexthe666.iceandfire.entity.util.IHumanoid;
import com.github.alexthe666.iceandfire.entity.util.IVillagerFear;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.misc.IafDamageRegistry;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.google.common.base.Predicate;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.FleeSunGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RestrictSunGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EntityGorgon extends MonsterEntity implements IAnimatedEntity, IVillagerFear, IAnimalFear, IHumanoid {

    public static Animation ANIMATION_SCARE;
    public static Animation ANIMATION_HIT;
    private int animationTick;
    private Animation currentAnimation;
    private GorgonAIStareAttack aiStare;
    private MeleeAttackGoal aiMelee;
    private int playerStatueCooldown;

    public EntityGorgon(EntityType type, World worldIn) {
        super(type, worldIn);
        ANIMATION_SCARE = Animation.create(30);
        ANIMATION_HIT = Animation.create(10);
    }

    public static boolean isEntityLookingAt(LivingEntity looker, LivingEntity seen, double degree) {
        degree *= 1 + (looker.getDistance(seen) * 0.1);
        Vector3d Vector3d = looker.getLook(1.0F).normalize();
        Vector3d Vector3d1 = new Vector3d(seen.getPosX() - looker.getPosX(), seen.getBoundingBox().minY + (double) seen.getEyeHeight() - (looker.getPosY() + (double) looker.getEyeHeight()), seen.getPosZ() - looker.getPosZ());
        double d0 = Vector3d1.length();
        Vector3d1 = Vector3d1.normalize();
        double d1 = Vector3d.dotProduct(Vector3d1);
        return d1 > 1.0D - degree / d0 && (looker.canEntityBeSeen(seen) && !isStoneMob(seen));
    }

    public static boolean isStoneMob(LivingEntity mob) {
        return mob instanceof EntityStoneStatue;
    }

    public static boolean isBlindfolded(LivingEntity attackTarget) {
        return attackTarget != null && (attackTarget.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == IafItemRegistry.BLINDFOLD || attackTarget.isPotionActive(Effects.BLINDNESS));
    }

    public boolean isTargetBlocked(Vector3d target) {
        Vector3d Vector3d = new Vector3d(this.getPosX(), this.getPosYEye(), this.getPosZ());
        RayTraceResult result = this.world.rayTraceBlocks(new RayTraceContext(Vector3d, target, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
        return result.getType() != RayTraceResult.Type.MISS;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(3, aiStare = new GorgonAIStareAttack(this, 1.0D, 0, 15.0F));
        this.goalSelector.addGoal(3, aiMelee = new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D) {
            public boolean shouldExecute() {
                executionChance = 20;
                return super.shouldExecute();
            }
        });
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F, 1.0F) {
            public boolean shouldContinueExecuting() {
                if (this.closestEntity != null && this.closestEntity instanceof PlayerEntity && ((PlayerEntity) this.closestEntity).isCreative()) {
                    return false;
                }
                return super.shouldContinueExecuting();
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
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, LivingEntity.class, 0, true, false, new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity entity) {
                return entity instanceof LivingEntity && DragonUtils.isAlive((LivingEntity) entity)  || (entity instanceof IBlacklistedFromStatues && ((IBlacklistedFromStatues) entity).canBeTurnedToStone());
            }
        }));
        this.goalSelector.removeGoal(aiMelee);
    }

    public void attackEntityWithRangedAttack(LivingEntity entity) {
        if (!(entity instanceof MobEntity) && entity instanceof LivingEntity) {
            forcePreyToLook((MobEntity) entity);
        }
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        boolean blindness = this.isPotionActive(Effects.BLINDNESS) || this.getAttackTarget() != null && this.getAttackTarget().isPotionActive(Effects.BLINDNESS) || this.getAttackTarget() != null && this.getAttackTarget() instanceof IBlacklistedFromStatues && !((IBlacklistedFromStatues) this.getAttackTarget()).canBeTurnedToStone();
        if (blindness && this.deathTime == 0) {
            if (this.getAnimation() != ANIMATION_HIT) {
                this.setAnimation(ANIMATION_HIT);
            }
            if (entityIn instanceof LivingEntity) {
                ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(Effects.POISON, 100, 2, false, true));
            }
        }
        return super.attackEntityAsMob(entityIn);
    }

    public void setAttackTarget(@Nullable LivingEntity LivingEntityIn) {
        super.setAttackTarget(LivingEntityIn);
        if (LivingEntityIn != null && !world.isRemote) {


            boolean blindness = this.isPotionActive(Effects.BLINDNESS) || LivingEntityIn.isPotionActive(Effects.BLINDNESS) || LivingEntityIn instanceof IBlacklistedFromStatues && !((IBlacklistedFromStatues) LivingEntityIn).canBeTurnedToStone() || isBlindfolded(LivingEntityIn);
            if (blindness && this.deathTime == 0) {
                this.goalSelector.addGoal(3, aiMelee);
                this.goalSelector.removeGoal(aiStare);
            } else {
                this.goalSelector.addGoal(3, aiStare);
                this.goalSelector.removeGoal(aiMelee);
            }
        }
    }

    protected int getExperiencePoints(PlayerEntity player) {
        return 30;
    }

    protected void onDeathUpdate() {
        ++this.deathTime;
        this.livingSoundTime = 20;
        if (this.world.isRemote) {
            for (int k = 0; k < 5; ++k) {
                double d2 = 0.4;
                double d0 = 0.1;
                double d1 = 0.1;
                IceAndFire.PROXY.spawnParticle("blood", this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getPosY(), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), d2, d0, d1);
            }
        }
        if (this.deathTime >= 200) {
            if (!this.world.isRemote && (this.isPlayer() || this.recentlyHit > 0 && this.canDropLoot() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))) {
                int i = this.getExperiencePoints(this.attackingPlayer);
                i = net.minecraftforge.event.ForgeEventFactory.getExperienceDrop(this, this.attackingPlayer, i);
                while (i > 0) {
                    int j = ExperienceOrbEntity.getXPSplit(i);
                    i -= j;
                    this.world.addEntity(new ExperienceOrbEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), j));
                }
            }
            this.remove();

            for (int k = 0; k < 20; ++k) {
                double d2 = this.rand.nextGaussian() * 0.02D;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                this.world.addParticle(ParticleTypes.CLOUD, this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getPosY() + (double) (this.rand.nextFloat() * this.getHeight()), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), d2, d0, d1);
            }
        }
    }

    public void livingTick() {
        super.livingTick();
        if (playerStatueCooldown > 0) {
            playerStatueCooldown--;
        }
        if (this.getAttackTarget() != null) {
            boolean blindness = this.isPotionActive(Effects.BLINDNESS) || this.getAttackTarget().isPotionActive(Effects.BLINDNESS);
            if (!blindness && this.deathTime == 0 && this.getAttackTarget() instanceof MobEntity && !(this.getAttackTarget() instanceof PlayerEntity)) {
                forcePreyToLook((MobEntity) this.getAttackTarget());
            }
            if(isEntityLookingAt(this.getAttackTarget(), this, 0.4)){
                this.getLookController().setLookPosition(this.getAttackTarget().getPosX(), this.getAttackTarget().getPosY() + (double) this.getAttackTarget().getEyeHeight(), this.getAttackTarget().getPosZ(), (float) this.getHorizontalFaceSpeed(), (float) this.getVerticalFaceSpeed());
            }
        }


        if (this.getAttackTarget() != null && isEntityLookingAt(this, this.getAttackTarget(), 0.4) && isEntityLookingAt(this.getAttackTarget(), this, 0.4) && !isBlindfolded(this.getAttackTarget())) {
            boolean blindness = this.isPotionActive(Effects.BLINDNESS) || this.getAttackTarget().isPotionActive(Effects.BLINDNESS) || this.getAttackTarget() instanceof IBlacklistedFromStatues && !((IBlacklistedFromStatues) this.getAttackTarget()).canBeTurnedToStone();
            if (!blindness && this.deathTime == 0) {
                if (this.getAnimation() != ANIMATION_SCARE) {
                    this.playSound(IafSoundRegistry.GORGON_ATTACK, 1, 1);
                    this.setAnimation(ANIMATION_SCARE);
                }
                if (this.getAnimation() == ANIMATION_SCARE) {
                    if (this.getAnimationTick() > 10) {
                        if (!world.isRemote) {
                            if (playerStatueCooldown == 0) {
                                EntityStoneStatue statue = EntityStoneStatue.buildStatueEntity(this.getAttackTarget());
                                statue.setPositionAndRotation(this.getAttackTarget().getPosX(), this.getAttackTarget().getPosY(), this.getAttackTarget().getPosZ(), this.getAttackTarget().rotationYaw, this.getAttackTarget().rotationPitch);
                                if (!world.isRemote) {
                                    world.addEntity(statue);
                                }
                                statue.prevRotationYaw = this.getAttackTarget().rotationYaw;
                                statue.rotationYaw = this.getAttackTarget().rotationYaw;
                                statue.rotationYawHead = this.getAttackTarget().rotationYaw;
                                statue.renderYawOffset = this.getAttackTarget().rotationYaw;
                                statue.prevRenderYawOffset = this.getAttackTarget().rotationYaw;
                                playerStatueCooldown = 40;
                                if(this.getAttackTarget() instanceof PlayerEntity){
                                    this.getAttackTarget().attackEntityFrom(IafDamageRegistry.GORGON_DMG, Integer.MAX_VALUE);
                                }else{
                                    this.getAttackTarget().remove();
                                }
                                this.setAttackTarget(null);
                            }
                        }
                    }
                }
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public int getVerticalFaceSpeed() {
        return 10;
    }

    public int getHorizontalFaceSpeed() {
        return 30;
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.UNDEAD;
    }

    public void forcePreyToLook(LivingEntity mob) {
        if(mob instanceof MobEntity){
            MobEntity mobEntity = (MobEntity)mob;
            mobEntity.getLookController().setLookPosition(this.getPosX(), this.getPosY() + (double) this.getEyeHeight(), this.getPosZ(), (float) mobEntity.getHorizontalFaceSpeed(), (float) mobEntity.getVerticalFaceSpeed());

        }
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, IafConfig.gorgonMaxHealth)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
                //ATTACK
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D)
                //ARMOR
                .createMutableAttribute(Attributes.ARMOR, 1.0D);
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
        return new Animation[]{ANIMATION_SCARE, ANIMATION_HIT};
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return IafSoundRegistry.GORGON_IDLE;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return IafSoundRegistry.GORGON_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return IafSoundRegistry.GORGON_DIE;
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return true;
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
