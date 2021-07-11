package com.github.alexthe666.iceandfire.entity;

import java.util.EnumSet;
import java.util.Random;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.ai.*;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.MyrmexTrades;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.AdvancedPathNavigate;
import com.google.common.base.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class EntityMyrmexRoyal extends EntityMyrmexBase {

    public static final Animation ANIMATION_BITE = Animation.create(15);
    public static final Animation ANIMATION_STING = Animation.create(15);
    public static final ResourceLocation DESERT_LOOT = new ResourceLocation("iceandfire", "entities/myrmex_royal_desert");
    public static final ResourceLocation JUNGLE_LOOT = new ResourceLocation("iceandfire", "entities/myrmex_royal_jungle");
    private static final ResourceLocation TEXTURE_DESERT = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_desert_royal.png");
    private static final ResourceLocation TEXTURE_JUNGLE = new ResourceLocation("iceandfire:textures/models/myrmex/myrmex_jungle_royal.png");
    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityMyrmexRoyal.class, DataSerializers.BOOLEAN);
    public int releaseTicks = 0;
    public int daylightTicks = 0;
    public float flyProgress;
    public EntityMyrmexRoyal mate;
    private int hiveTicks = 0;
    private int breedingTicks = 0;
    private boolean isFlying;
    private boolean isLandNavigator;
    private boolean isMating = false;

    public EntityMyrmexRoyal(EntityType t, World worldIn) {
        super(t, worldIn);
        this.switchNavigator(true);
    }

    @Override
    protected VillagerTrades.ITrade[] getLevel1Trades() {
        return isJungle() ? MyrmexTrades.JUNGLE_ROYAL.get(1) : MyrmexTrades.DESERT_ROYAL.get(1);
    }

    @Override
    protected VillagerTrades.ITrade[] getLevel2Trades() {
        return isJungle() ? MyrmexTrades.JUNGLE_ROYAL.get(2) : MyrmexTrades.DESERT_ROYAL.get(2);
    }

    public static BlockPos getPositionRelativetoGround(Entity entity, World world, double x, double z, Random rand) {
        BlockPos pos = new BlockPos(x, entity.getPosY(), z);
        for (int yDown = 0; yDown < 10; yDown++) {
            if (!world.isAirBlock(pos.down(yDown))) {
                return pos.up(yDown);
            }
        }
        return pos;
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return isJungle() ? JUNGLE_LOOT : DESERT_LOOT;
    }

    protected int getExperiencePoints(PlayerEntity player) {
        return 10;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(FLYING, Boolean.valueOf(false));
    }

    protected void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveController = new MovementController(this);
            this.navigator = createNavigator(world, AdvancedPathNavigate.MovementType.CLIMBING);
            this.isLandNavigator = true;
        } else {
            this.moveController = new EntityMyrmexRoyal.FlyMoveHelper(this);
            this.navigator = createNavigator(world, AdvancedPathNavigate.MovementType.FLYING);
            this.isLandNavigator = false;
        }
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

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        tag.putInt("HiveTicks", hiveTicks);
        tag.putInt("ReleaseTicks", releaseTicks);
        tag.putBoolean("Flying", this.isFlying());
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        this.hiveTicks = tag.getInt("HiveTicks");
        this.releaseTicks = tag.getInt("ReleaseTicks");
        this.setFlying(tag.getBoolean("Flying"));
    }

    public void livingTick() {
        super.livingTick();
        boolean flying = this.isFlying() && !this.onGround;
        if (flying && flyProgress < 20.0F) {
            flyProgress += 1F;
        } else if (!flying && flyProgress > 0.0F) {
            flyProgress -= 1F;
        }
        if (flying) {
            double up = isInWater() ? 0.16D : 0.08D;
            this.setMotion(this.getMotion().add(0, up, 0));
        }
        if (flying && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (!flying && !this.isLandNavigator) {
            switchNavigator(true);
        }
        if (this.canSeeSky()) {
            this.daylightTicks++;
        } else {
            this.daylightTicks = 0;
        }
        if (flying && this.canSeeSky() && this.isBreedingSeason()) {
            this.releaseTicks++;
        }
        if (!flying && this.canSeeSky() && daylightTicks > 300 && this.isBreedingSeason() && this.getAttackTarget() == null && this.canMove() && this.isOnGround() && !isMating) {
            this.setFlying(true);
            this.setMotion(this.getMotion().add(0, 0.42D, 0));
        }
        if (this.getGrowthStage() >= 2) {
            hiveTicks++;
        }
        if (this.getAnimation() == ANIMATION_BITE && this.getAttackTarget() != null && this.getAnimationTick() == 6) {
            this.playBiteSound();
            if (this.getAttackBounds().intersects(this.getAttackTarget().getBoundingBox())) {
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
            }
        }
        if (this.getAnimation() == ANIMATION_STING && this.getAttackTarget() != null && this.getAnimationTick() == 6) {
            this.playStingSound();
            if (this.getAttackBounds().intersects(this.getAttackTarget().getBoundingBox())) {
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 2));
                this.getAttackTarget().addPotionEffect(new EffectInstance(Effects.POISON, 70, 1));
            }
        }
        if (this.mate != null) {
            this.world.setEntityState(this, (byte) 77);
            if (this.getDistance(this.mate) < 10) {
                this.setFlying(false);
                this.mate.setFlying(false);
                isMating = true;
                if (this.isOnGround() && this.mate.onGround) {
                    breedingTicks++;
                    if (breedingTicks > 100) {
                        if (this.isAlive()) {
                            this.mate.remove();
                            this.remove();
                            EntityMyrmexQueen queen = new EntityMyrmexQueen(IafEntityRegistry.MYRMEX_QUEEN, this.world);
                            queen.copyLocationAndAnglesFrom(this);
                            queen.setJungleVariant(this.isJungle());
                            queen.setMadeHome(false);
                            if (!world.isRemote) {
                                world.addEntity(queen);
                            }
                        }
                        isMating = false;
                    }
                }
            }
            this.mate.mate = this;
            if (!this.mate.isAlive()) {
                this.mate.mate = null;
                this.mate = null;
            }
        }
    }

    protected double attackDistance() {
        return 8;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(0, new MyrmexAITradePlayer(this));
        this.goalSelector.addGoal(0, new MyrmexAILookAtTradePlayer(this));
        this.goalSelector.addGoal(0, new MyrmexAIMoveToMate(this, 1.0D));
        this.goalSelector.addGoal(1, new AIFlyAtTarget());
        this.goalSelector.addGoal(2, new AIFlyRandom());
        this.goalSelector.addGoal(3, new MyrmexAIAttackMelee(this, 1.0D, true));
        this.goalSelector.addGoal(4, new MyrmexAILeaveHive(this, 1.0D));
        this.goalSelector.addGoal(4, new MyrmexAIReEnterHive(this, 1.0D));
        this.goalSelector.addGoal(5, new MyrmexAIMoveThroughHive(this, 1.0D));
        this.goalSelector.addGoal(5, new MyrmexAIWanderHiveCenter(this, 1.0D));
        this.goalSelector.addGoal(6, new MyrmexAIWander(this, 1D));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new MyrmexAIDefendHive(this));
        this.targetSelector.addGoal(2, new MyrmexAIFindMate(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new MyrmexAIAttackPlayers(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, true, new Predicate<LivingEntity>() {
            public boolean apply(@Nullable LivingEntity entity) {
                if (entity instanceof EntityMyrmexBase && EntityMyrmexRoyal.this.isBreedingSeason() || entity instanceof EntityMyrmexRoyal) {
                    return false;
                }
                return entity != null && !EntityMyrmexBase.haveSameHive(EntityMyrmexRoyal.this, entity) && DragonUtils.isAlive(entity)  && !(entity instanceof IMob);
            }
        }));

    }

    public boolean canMateWith(AnimalEntity otherAnimal) {
        if (otherAnimal == this || otherAnimal == null) {
            return false;
        } else if (otherAnimal.getClass() != this.getClass()) {
            return false;
        } else {
            if (otherAnimal instanceof EntityMyrmexBase) {
                if (((EntityMyrmexBase) otherAnimal).getHive() != null && this.getHive() != null) {
                    return !this.getHive().equals(((EntityMyrmexBase) otherAnimal).getHive());
                } else {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean shouldMoveThroughHive() {
        return false;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, 50D)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D)
                //ATTACK
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, IafConfig.myrmexBaseAttackStrength * 2D)
                //FOLLOW RANGE
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 64.0D)
                //ARMOR
                .createMutableAttribute(Attributes.ARMOR, 9.0D);
    }

    @Override
    public ResourceLocation getAdultTexture() {
        return isJungle() ? TEXTURE_JUNGLE : TEXTURE_DESERT;
    }

    @Override
    public float getModelScale() {
        return 1.25F;
    }

    @Override
    public int getCasteImportance() {
        return 2;
    }

    public boolean shouldLeaveHive() {
        return isBreedingSeason();
    }

    public boolean shouldEnterHive() {
        return !isBreedingSeason();
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.getGrowthStage() < 2) {
            return false;
        }
        if (this.getAnimation() != ANIMATION_STING && this.getAnimation() != ANIMATION_BITE) {
            this.setAnimation(this.getRNG().nextBoolean() ? ANIMATION_STING : ANIMATION_BITE);
            return true;
        }
        return false;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_PUPA_WIGGLE, ANIMATION_BITE, ANIMATION_STING};
    }

    public boolean isBreedingSeason() {
        return this.getGrowthStage() >= 2 && (this.getHive() == null || this.getHive().reproduces);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 76) {
            this.playEffect(20);
        } else if (id == 77) {
            this.playEffect(7);
        } else {
            super.handleStatusUpdate(id);
        }
    }

    protected void playEffect(int hearts) {
        for (int i = 0; i < hearts; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.addParticle(ParticleTypes.HEART, this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getPosY() + 0.5D + (double) (this.rand.nextFloat() * this.getHeight()), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), d0, d1, d2);
        }
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {

    }

    @Override
    public int getXp() {
        return 0;
    }

    @Override
    public boolean hasXPBar() {
        return false;
    }

    protected boolean isDirectPathBetweenPoints(BlockPos posVec31, BlockPos posVec32)
    {
        Vector3d vector3d = Vector3d.copyCentered(posVec31);
        Vector3d vector3d1 = Vector3d.copyCentered(posVec32);
        return world.rayTraceBlocks(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() == RayTraceResult.Type.MISS;
    }
    class FlyMoveHelper extends MovementController {
        public FlyMoveHelper(EntityMyrmexRoyal pixie) {
            super(pixie);
            this.speed = 1.75F;
        }

        public void tick() {
            if (this.action == MovementController.Action.MOVE_TO) {
                if (EntityMyrmexRoyal.this.collidedHorizontally) {
                    EntityMyrmexRoyal.this.rotationYaw += 180.0F;
                    this.speed = 0.1F;
                    BlockPos target = EntityMyrmexRoyal.getPositionRelativetoGround(EntityMyrmexRoyal.this, EntityMyrmexRoyal.this.world, EntityMyrmexRoyal.this.getPosX() + EntityMyrmexRoyal.this.rand.nextInt(15) - 7, EntityMyrmexRoyal.this.getPosZ() + EntityMyrmexRoyal.this.rand.nextInt(15) - 7, EntityMyrmexRoyal.this.rand);
                    this.posX = target.getX();
                    this.posY = target.getY();
                    this.posZ = target.getZ();
                }
                double d0 = this.posX - EntityMyrmexRoyal.this.getPosX();
                double d1 = this.posY - EntityMyrmexRoyal.this.getPosY();
                double d2 = this.posZ - EntityMyrmexRoyal.this.getPosZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                d3 = MathHelper.sqrt(d3);

                if (d3 < EntityMyrmexRoyal.this.getBoundingBox().getAverageEdgeLength()) {
                    this.action = MovementController.Action.WAIT;
                    EntityMyrmexRoyal.this.setMotion(EntityMyrmexRoyal.this.getMotion().mul(0.5D, 0.5D, 0.5D));
                } else {
                    EntityMyrmexRoyal.this.setMotion(EntityMyrmexRoyal.this.getMotion().add(d0 / d3 * 0.1D * this.speed, d1 / d3 * 0.1D * this.speed, d2 / d3 * 0.1D * this.speed));

                    if (EntityMyrmexRoyal.this.getAttackTarget() == null) {
                        EntityMyrmexRoyal.this.rotationYaw = -((float) MathHelper.atan2(EntityMyrmexRoyal.this.getMotion().x, EntityMyrmexRoyal.this.getMotion().z)) * (180F / (float) Math.PI);
                        EntityMyrmexRoyal.this.renderYawOffset = EntityMyrmexRoyal.this.rotationYaw;
                    } else {
                        double d4 = EntityMyrmexRoyal.this.getAttackTarget().getPosX() - EntityMyrmexRoyal.this.getPosX();
                        double d5 = EntityMyrmexRoyal.this.getAttackTarget().getPosZ() - EntityMyrmexRoyal.this.getPosZ();
                        EntityMyrmexRoyal.this.rotationYaw = -((float) MathHelper.atan2(d4, d5)) * (180F / (float) Math.PI);
                        EntityMyrmexRoyal.this.renderYawOffset = EntityMyrmexRoyal.this.rotationYaw;
                    }
                }
            }
        }
    }

    class AIFlyRandom extends Goal {
        BlockPos target;

        public AIFlyRandom() {
            this.setMutexFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean shouldExecute() {
            if (EntityMyrmexRoyal.this.isFlying() && EntityMyrmexRoyal.this.getAttackTarget() == null) {
                if (EntityMyrmexRoyal.this instanceof EntityMyrmexSwarmer && ((EntityMyrmexSwarmer) EntityMyrmexRoyal.this).getSummoner() != null) {
                    Entity summon = ((EntityMyrmexSwarmer) EntityMyrmexRoyal.this).getSummoner();
                    target = EntityMyrmexRoyal.getPositionRelativetoGround(EntityMyrmexRoyal.this, EntityMyrmexRoyal.this.world, summon.getPosX() + EntityMyrmexRoyal.this.rand.nextInt(10) - 5, summon.getPosZ() + EntityMyrmexRoyal.this.rand.nextInt(10) - 5, EntityMyrmexRoyal.this.rand);
                } else {
                    target = EntityMyrmexRoyal.getPositionRelativetoGround(EntityMyrmexRoyal.this, EntityMyrmexRoyal.this.world, EntityMyrmexRoyal.this.getPosX() + EntityMyrmexRoyal.this.rand.nextInt(30) - 15, EntityMyrmexRoyal.this.getPosZ() + EntityMyrmexRoyal.this.rand.nextInt(30) - 15, EntityMyrmexRoyal.this.rand);
                }
                return isDirectPathBetweenPoints(EntityMyrmexRoyal.this.getPosition(), target) && !EntityMyrmexRoyal.this.getMoveHelper().isUpdating() && EntityMyrmexRoyal.this.rand.nextInt(2) == 0;
            } else {
                return false;
            }
        }


        public boolean shouldContinueExecuting() {
            return false;
        }

        public void tick() {
            if (!isDirectPathBetweenPoints(EntityMyrmexRoyal.this.getPosition(), target)) {
                if (EntityMyrmexRoyal.this instanceof EntityMyrmexSwarmer && ((EntityMyrmexSwarmer) EntityMyrmexRoyal.this).getSummoner() != null) {
                    Entity summon = ((EntityMyrmexSwarmer) EntityMyrmexRoyal.this).getSummoner();
                    target = EntityMyrmexRoyal.getPositionRelativetoGround(EntityMyrmexRoyal.this, EntityMyrmexRoyal.this.world, summon.getPosX() + EntityMyrmexRoyal.this.rand.nextInt(10) - 5, summon.getPosZ() + EntityMyrmexRoyal.this.rand.nextInt(10) - 5, EntityMyrmexRoyal.this.rand);
                } else {
                    target = EntityMyrmexRoyal.getPositionRelativetoGround(EntityMyrmexRoyal.this, EntityMyrmexRoyal.this.world, EntityMyrmexRoyal.this.getPosX() + EntityMyrmexRoyal.this.rand.nextInt(30) - 15, EntityMyrmexRoyal.this.getPosZ() + EntityMyrmexRoyal.this.rand.nextInt(30) - 15, EntityMyrmexRoyal.this.rand);
                }
            }
            if (EntityMyrmexRoyal.this.world.isAirBlock(target)) {
                EntityMyrmexRoyal.this.moveController.setMoveTo((double) target.getX() + 0.5D, (double) target.getY() + 0.5D, (double) target.getZ() + 0.5D, 0.25D);
                if (EntityMyrmexRoyal.this.getAttackTarget() == null) {
                    EntityMyrmexRoyal.this.getLookController().setLookPosition((double) target.getX() + 0.5D, (double) target.getY() + 0.5D, (double) target.getZ() + 0.5D, 180.0F, 20.0F);

                }
            }
        }
    }

    class AIFlyAtTarget extends Goal {
        public AIFlyAtTarget() {
        }

        public boolean shouldExecute() {
            if (EntityMyrmexRoyal.this.getAttackTarget() != null && !EntityMyrmexRoyal.this.getMoveHelper().isUpdating() && EntityMyrmexRoyal.this.rand.nextInt(7) == 0) {
                return EntityMyrmexRoyal.this.getDistanceSq(EntityMyrmexRoyal.this.getAttackTarget()) > 4.0D;
            } else {
                return false;
            }
        }

        public boolean shouldContinueExecuting() {
            return EntityMyrmexRoyal.this.getMoveHelper().isUpdating() && EntityMyrmexRoyal.this.getAttackTarget() != null && EntityMyrmexRoyal.this.getAttackTarget().isAlive();
        }

        public void startExecuting() {
            LivingEntity LivingEntity = EntityMyrmexRoyal.this.getAttackTarget();
            Vector3d Vector3d = LivingEntity.getEyePosition(1.0F);
            EntityMyrmexRoyal.this.moveController.setMoveTo(Vector3d.x, Vector3d.y, Vector3d.z, 1.0D);
        }

        public void resetTask() {

        }

        public void tick() {
            LivingEntity LivingEntity = EntityMyrmexRoyal.this.getAttackTarget();
            if (LivingEntity != null) {
                if (EntityMyrmexRoyal.this.getBoundingBox().intersects(LivingEntity.getBoundingBox())) {
                    EntityMyrmexRoyal.this.attackEntityAsMob(LivingEntity);
                } else {
                    double d0 = EntityMyrmexRoyal.this.getDistanceSq(LivingEntity);

                    if (d0 < 9.0D) {
                        Vector3d Vector3d = LivingEntity.getEyePosition(1.0F);
                        EntityMyrmexRoyal.this.moveController.setMoveTo(Vector3d.x, Vector3d.y, Vector3d.z, 1.0D);
                    }
                }
            }

        }
    }
}
