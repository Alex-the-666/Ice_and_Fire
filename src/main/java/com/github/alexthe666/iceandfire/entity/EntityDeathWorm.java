package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.collision.ICustomCollisions;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.api.event.GenericGriefEvent;
import com.github.alexthe666.iceandfire.client.IafKeybindRegistry;
import com.github.alexthe666.iceandfire.entity.ai.*;
import com.github.alexthe666.iceandfire.entity.util.*;
import com.github.alexthe666.iceandfire.message.MessageDeathWormHitbox;
import com.github.alexthe666.iceandfire.message.MessageDragonControl;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.github.alexthe666.iceandfire.pathfinding.PathNavigateDeathWormLand;
import com.github.alexthe666.iceandfire.pathfinding.PathNavigateDeathWormSand;
import com.google.common.base.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtByTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;

public class EntityDeathWorm extends TameableEntity implements ISyncMount, ICustomCollisions, IBlacklistedFromStatues, IAnimatedEntity, IVillagerFear, IAnimalFear, IGroundMount {

    public static final ResourceLocation TAN_LOOT = new ResourceLocation("iceandfire", "entities/deathworm_tan");
    public static final ResourceLocation WHITE_LOOT = new ResourceLocation("iceandfire", "entities/deathworm_white");
    public static final ResourceLocation RED_LOOT = new ResourceLocation("iceandfire", "entities/deathworm_red");
    public static final ResourceLocation TAN_GIANT_LOOT = new ResourceLocation("iceandfire", "entities/deathworm_tan_giant");
    public static final ResourceLocation WHITE_GIANT_LOOT = new ResourceLocation("iceandfire", "entities/deathworm_white_giant");
    public static final ResourceLocation RED_GIANT_LOOT = new ResourceLocation("iceandfire", "entities/deathworm_red_giant");
    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityDeathWorm.class, DataSerializers.VARINT);
    private static final DataParameter<Float> SCALE = EntityDataManager.createKey(EntityDeathWorm.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> JUMP_TICKS = EntityDataManager.createKey(EntityDeathWorm.class, DataSerializers.VARINT);
    private static final DataParameter<Byte> CONTROL_STATE = EntityDataManager.createKey(EntityDeathWorm.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> WORM_AGE = EntityDataManager.createKey(EntityDeathWorm.class, DataSerializers.VARINT);
    private static final DataParameter<BlockPos> HOME = EntityDataManager.createKey(EntityDeathWorm.class, DataSerializers.BLOCK_POS);
    public static Animation ANIMATION_BITE = Animation.create(10);
    @OnlyIn(Dist.CLIENT)
    public ChainBuffer tail_buffer;
    public float jumpProgress;
    public float prevJumpProgress;
    private int animationTick;
    private boolean willExplode = false;
    private int ticksTillExplosion = 60;
    private Animation currentAnimation;
    private EntityMutlipartPart[] segments = new EntityMutlipartPart[6];
    private boolean isSandNavigator;
    private float prevScale = 0.0F;
    private LookController lookHelper;
    private int growthCounter = 0;

    public EntityDeathWorm(EntityType type, World worldIn) {
        super(type, worldIn);
        this.lookHelper = new IAFLookHelper(this);
        this.ignoreFrustumCheck = true;
        this.stepHeight = 1;
        if (worldIn.isRemote) {
            tail_buffer = new ChainBuffer();
        }
        this.switchNavigator(false);
        this.goalSelector.addGoal(0, new EntityGroundAIRide<>(this));
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new DeathWormAIAttack(this));
        this.goalSelector.addGoal(3, new DeathWormAIJump(this, 12));
        this.goalSelector.addGoal(4, new DeathWormAIFindSandTarget(this, 10));
        this.goalSelector.addGoal(5, new DeathWormAIGetInSand(this, 1.0D));
        this.goalSelector.addGoal(6, new DeathWormAIWander(this, 1));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(4, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new DeathwormAITargetItems(this, false, false));
        this.targetSelector.addGoal(1, new DeathWormAITarget(this, LivingEntity.class, false, new Predicate<LivingEntity>() {
            @Override
            public boolean apply(@Nullable LivingEntity input) {
                if (EntityDeathWorm.this.isTamed()) {
                    return input instanceof MonsterEntity;
                } else {
                    return (IafConfig.deathWormAttackMonsters ? input instanceof LivingEntity && DragonUtils.isAlive(input) : (input instanceof AnimalEntity || input instanceof PlayerEntity)) && DragonUtils.isAlive(input) && !(input instanceof EntityDragonBase && ((EntityDragonBase) input).isModelDead()) && !EntityDeathWorm.this.isOwner(input);
                }
            }
        }));
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, IafConfig.deathWormMaxHealth)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.15D)
                //ATTACK
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, IafConfig.deathWormAttackStrength)
                //FOLLOW RANGE
                .createMutableAttribute(Attributes.FOLLOW_RANGE, IafConfig.deathWormTargetSearchLength)
                //ARMOR
                .createMutableAttribute(Attributes.ARMOR, 3);
    }

    public LookController getLookController() {
        return this.lookHelper;
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    public boolean getCanSpawnHere() {
        int i = MathHelper.floor(this.getPosX());
        int j = MathHelper.floor(this.getBoundingBox().minY);
        int k = MathHelper.floor(this.getPosZ());
        BlockPos blockpos = new BlockPos(i, j, k);
        return BlockTags.SAND.contains(this.world.getBlockState(blockpos.down()).getBlock()) && this.getRNG().nextInt(1 + IafConfig.deathWormSpawnCheckChance) == 0 && this.world.getLight(blockpos) > 8;
    }

    public void onUpdateParts() {
        addSegmentsToWorld();
        if (isSandBelow()) {
            int i = MathHelper.floor(this.getPosX());
            int j = MathHelper.floor(this.getPosY() - 1);
            int k = MathHelper.floor(this.getPosZ());
            BlockPos blockpos = new BlockPos(i, j, k);
            BlockState BlockState = this.world.getBlockState(blockpos);

            if (world.isRemote) {
                // world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, BlockState), this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getSurface((int) Math.floor(this.getPosX()), (int) Math.floor(this.getPosY()), (int) Math.floor(this.getPosZ())) + 0.5F, this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D);
            }
        }
    }

    protected int getExperiencePoints(PlayerEntity player) {
        return this.getRenderScale() > 3 ? 20 : 10;
    }

    public void initSegments(float scale) {
        segments = new EntityMutlipartPart[7];
        for (int i = 0; i < segments.length; i++) {
            segments[i] = new EntitSlowPart(this, (-0.8F - (i * 0.8F)) * scale, 0, 0, 0.7F * scale, 0.7F * scale, 1);
            segments[i].copyLocationAndAnglesFrom(this);
            segments[i].setParent(this);
        }
    }

    private void addSegmentsToWorld() {
        for (Entity entity : segments) {
            if (entity != null) {
                if (!((EntityMutlipartPart) entity).shouldContinuePersisting()) {
                    world.addEntity(entity);
                }
                ((EntityMutlipartPart) entity).setParent(this);
            }
        }
    }

    private void clearSegments() {
        for (Entity entity : segments) {
            if (entity != null) {
                entity.onKillCommand();
                entity.remove();
            }
        }
    }

    public void setExplosive(boolean explosive) {
        this.willExplode = true;
        this.ticksTillExplosion = 60;
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.getAnimation() != ANIMATION_BITE) {
            this.setAnimation(ANIMATION_BITE);
            this.playSound(this.getRenderScale() > 3 ? IafSoundRegistry.DEATHWORM_GIANT_ATTACK : IafSoundRegistry.DEATHWORM_ATTACK, 1, 1);
        }
        if (this.getRNG().nextInt(3) == 0 && this.getRenderScale() > 1 && this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
            if (!MinecraftForge.EVENT_BUS.post(new GenericGriefEvent(this, entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ()))) {
                BlockLaunchExplosion explosion = new BlockLaunchExplosion(world, this, entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ(), this.getRenderScale());
                explosion.doExplosionA();
                explosion.doExplosionB(true);
            }
        }
        return false;
    }

    public void onDeath(DamageSource cause) {
        clearSegments();
        super.onDeath(cause);
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        switch (this.getVariant()) {
            case 0:
                return this.getRenderScale() > 3 ? TAN_GIANT_LOOT : TAN_LOOT;
            case 1:
                return this.getRenderScale() > 3 ? RED_GIANT_LOOT : RED_LOOT;
            case 2:
                return this.getRenderScale() > 3 ? WHITE_GIANT_LOOT : WHITE_LOOT;
        }
        return null;
    }

    @Nullable
    @Override
    public AgeableEntity createChild(ServerWorld serverWorld, AgeableEntity ageable) {
        return null;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(VARIANT, Integer.valueOf(0));
        this.dataManager.register(SCALE, Float.valueOf(1F));
        this.dataManager.register(CONTROL_STATE, Byte.valueOf((byte) 0));
        this.dataManager.register(WORM_AGE, Integer.valueOf(10));
        this.dataManager.register(HOME, BlockPos.ZERO);
        this.dataManager.register(JUMP_TICKS, 0);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putInt("GrowthCounter", this.growthCounter);
        compound.putFloat("Scale", this.getScale());
        compound.putInt("WormAge", this.getWormAge());
        compound.putLong("WormHome", this.getWormHome().toLong());
        compound.putBoolean("WillExplode", this.willExplode);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setVariant(compound.getInt("Variant"));
        this.growthCounter = compound.getInt("GrowthCounter");
        this.setDeathWormScale(compound.getFloat("Scale"));
        this.setWormAge(compound.getInt("WormAge"));
        this.setWormHome(BlockPos.fromLong(compound.getLong("WormHome")));
        this.willExplode = compound.getBoolean("WillExplode");
    }

    private void setStateField(int i, boolean newState) {
        byte prevState = dataManager.get(CONTROL_STATE).byteValue();
        if (newState) {
            dataManager.set(CONTROL_STATE, (byte) (prevState | (1 << i)));
        } else {
            dataManager.set(CONTROL_STATE, (byte) (prevState & ~(1 << i)));
        }
    }

    public byte getControlState() {
        return Byte.valueOf(dataManager.get(CONTROL_STATE));
    }

    public void setControlState(byte state) {
        dataManager.set(CONTROL_STATE, Byte.valueOf(state));
    }

    public int getVariant() {
        return Integer.valueOf(this.dataManager.get(VARIANT).intValue());
    }

    public void setVariant(int variant) {
        this.dataManager.set(VARIANT, Integer.valueOf(variant));
    }

    public int getWormJumping() {
        return this.dataManager.get(JUMP_TICKS);
    }

    public void setWormJumping(int jump) {
        this.dataManager.set(JUMP_TICKS, jump);
    }

    public BlockPos getWormHome() {
        return this.dataManager.get(HOME);
    }

    public void setWormHome(BlockPos home) {
        if (home instanceof BlockPos) {
            this.dataManager.set(HOME, home);
        }
    }

    public int getWormAge() {
        return Math.max(1, Integer.valueOf(dataManager.get(WORM_AGE).intValue()));
    }

    public void setWormAge(int age) {
        this.dataManager.set(WORM_AGE, Integer.valueOf(age));
    }

    public float getScale() {
        return Float.valueOf(this.dataManager.get(SCALE).floatValue());
    }

    public float getRenderScale() {
        return Math.min(this.getScale() * (this.getWormAge() / 5F), 7F);
    }

    public void setDeathWormScale(float scale) {
        this.dataManager.set(SCALE, Float.valueOf(scale));
        this.updateAttributes();
        clearSegments();
        if (!this.world.isRemote) {
            initSegments(scale * (this.getWormAge() / 5F));
            IceAndFire.sendMSGToAll(new MessageDeathWormHitbox(this.getEntityId(), scale * (this.getWormAge() / 5F)));
        }
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setVariant(this.getRNG().nextInt(3));
        float size = 0.25F + (float) (Math.random() * 0.35F);
        this.setDeathWormScale(this.getRNG().nextInt(20) == 0 ? size * 4 : size);
        return spawnDataIn;
    }

    public void updatePassenger(Entity passenger) {
        super.updatePassenger(passenger);
        if (this.isPassenger(passenger)) {
            renderYawOffset = rotationYaw;
            this.rotationYaw = passenger.rotationYaw;
            float radius = -0.5F * this.getRenderScale();
            float angle = (0.01745329251F * this.renderYawOffset);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            passenger.setPosition(this.getPosX() + extraX, this.getPosY() + this.getEyeHeight() - 0.55F, this.getPosZ() + extraZ);
        }
    }

    @Nullable
    public Entity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) passenger;
                return player;
            }
        }
        return null;
    }

    public ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (this.getWormAge() > 4 && player.getRidingEntity() == null && player.getHeldItemMainhand().getItem() == Items.FISHING_ROD && player.getHeldItemOffhand().getItem() == Items.FISHING_ROD && !this.world.isRemote) {
            player.startRiding(this);
            return ActionResultType.SUCCESS;
        }
        return super.getEntityInteractionResult(player, hand);
    }

    private void switchNavigator(boolean inSand) {
        if (inSand) {
            this.moveController = new EntityDeathWorm.SandMoveHelper();
            this.navigator = new PathNavigateDeathWormSand(this, world);
            this.isSandNavigator = true;
        } else {
            this.moveController = new MovementController(this);
            this.navigator = new PathNavigateDeathWormLand(this, world);
            this.isSandNavigator = false;
        }
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source == DamageSource.IN_WALL || source == DamageSource.FALLING_BLOCK) {
            return false;
        }
        if (this.isBeingRidden() && source.getTrueSource() != null && this.getControllingPassenger() != null && source.getTrueSource() == this.getControllingPassenger()) {
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    public void move(MoverType typeIn, Vector3d pos) {
        super.move(typeIn, pos);
    }

    @Override
    public Vector3d getAllowedMovement(Vector3d vec) {
        return ICustomCollisions.getAllowedMovementForEntity(this, vec);
    }

    @Override
    public boolean isEntityInsideOpaqueBlock() {
        if (this.isInSand()) {
            return false;
        } else {
            return super.isEntityInsideOpaqueBlock();
        }
    }


    protected void pushOutOfBlocks(double x, double y, double z) {
        BlockPos blockpos = new BlockPos(x, y, z);
        Vector3d vector3d = new Vector3d(x - (double) blockpos.getX(), y - (double) blockpos.getY(), z - (double) blockpos.getZ());
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        Direction direction = Direction.UP;
        double d0 = Double.MAX_VALUE;

        for (Direction direction1 : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP}) {
            blockpos$mutable.setAndMove(blockpos, direction1);
            if (!this.world.getBlockState(blockpos$mutable).hasOpaqueCollisionShape(this.world, blockpos$mutable) || BlockTags.SAND.contains(world.getBlockState(blockpos$mutable).getBlock())) {
                double d1 = vector3d.getCoordinate(direction1.getAxis());
                double d2 = direction1.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0D - d1 : d1;
                if (d2 < d0) {
                    d0 = d2;
                    direction = direction1;
                }
            }
        }

        float f = this.rand.nextFloat() * 0.2F + 0.1F;
        float f1 = (float) direction.getAxisDirection().getOffset();
        Vector3d vector3d1 = this.getMotion().scale(0.75D);
        if (direction.getAxis() == Direction.Axis.X) {
            this.setMotion(f1 * f, vector3d1.y, vector3d1.z);
        } else if (direction.getAxis() == Direction.Axis.Y) {
            this.setMotion(vector3d1.x, f1 * f, vector3d1.z);
        } else if (direction.getAxis() == Direction.Axis.Z) {
            this.setMotion(vector3d1.x, vector3d1.y, f1 * f);
        }
    }

    private void updateAttributes() {
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(Math.min(0.2D, 0.15D * this.getRenderScale()));
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(Math.max(1, IafConfig.deathWormAttackStrength * this.getRenderScale()));
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(Math.max(6, IafConfig.deathWormMaxHealth * this.getRenderScale()));
        this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(IafConfig.deathWormTargetSearchLength);
        this.setHealth((float) this.getAttribute(Attributes.MAX_HEALTH).getBaseValue());
    }

    @Override
    public void onKillEntity(ServerWorld world, LivingEntity entity) {
        if (this.isTamed()) {
            this.heal(14);
        }
    }

    public void livingTick() {
        super.livingTick();
        prevJumpProgress = jumpProgress;
        if (this.getWormJumping() > 0 && jumpProgress < 5F) {
            jumpProgress++;
        }
        if (this.getWormJumping() == 0 && jumpProgress > 0F) {
            jumpProgress--;
        }
        if(this.isInSand() && this.collidedHorizontally){
            this.setMotion(this.getMotion().add(0, 0.05, 0));
        }
        if (this.getWormJumping() > 0) {
            float f2 = (float) -((float) this.getMotion().y * (double) (180F / (float) Math.PI));
            this.rotationPitch = f2;
            if (this.isInSand() || this.isOnGround()) {
                this.setWormJumping(this.getWormJumping() - 1);
            }
        }
        if (world.getDifficulty() == Difficulty.PEACEFUL && this.getAttackTarget() instanceof PlayerEntity) {
            this.setAttackTarget(null);
        }
        if (this.getAttackTarget() != null && (!this.getAttackTarget().isAlive() || !DragonUtils.isAlive(this.getAttackTarget()))) {
            this.setAttackTarget(null);
        }
        if (this.willExplode) {
            if (this.ticksTillExplosion == 0) {
                boolean b = !MinecraftForge.EVENT_BUS.post(new GenericGriefEvent(this, this.getPosX(), this.getPosY(), this.getPosZ()));
                if (b) {
                    world.createExplosion(null, this.getPosX(), this.getPosY(), this.getPosZ(), 2.5F * this.getRenderScale(), false, Explosion.Mode.DESTROY);
                }
            } else {
                this.ticksTillExplosion--;
            }
        }
        if (this.ticksExisted == 1) {
            initSegments(this.getRenderScale());
        }
        if (isInSandStrict()) {
            this.setMotion(this.getMotion().add(0, 0.08D, 0));
        }
        if (growthCounter > 1000 && this.getWormAge() < 5) {
            growthCounter = 0;
            this.setWormAge(Math.min(5, this.getWormAge() + 1));
            this.clearSegments();
            this.heal(15);
            this.setDeathWormScale(this.getScale());
            if (world.isRemote) {
                for (int i = 0; i < 10 * this.getRenderScale(); i++) {
                    this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getSurface((int) Math.floor(this.getPosX()), (int) Math.floor(this.getPosY()), (int) Math.floor(this.getPosZ())) + 0.5F, this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D);
                    /*
                    for (int j = 0; j < segments.length; j++) {
                        this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, segments[j].getPosX() + (double) (this.rand.nextFloat() * segments[j].getWidth() * 2.0F) - (double) segments[j].getWidth(), this.getSurface((int) Math.floor(segments[j].getPosX()), (int) Math.floor(segments[j].getPosY()), (int) Math.floor(segments[j].getPosZ())) + 0.5F, segments[j].getPosZ() + (double) (this.rand.nextFloat() * segments[j].getWidth() * 2.0F) - (double) segments[j].getWidth(), this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D);
                    }
                     */
                }
            }
        }
        if (this.getWormAge() < 5) {
            growthCounter++;
        }
        if (this.getControllingPassenger() != null) {
            if (this.isEntityInsideOpaqueBlock()) {
                this.setMotion(this.getMotion().add(0, 0.4F, 0));
            }
        }
        if (this.getControllingPassenger() != null && this.getAttackTarget() != null) {
            this.getNavigator().clearPath();
            this.setAttackTarget(null);
        }
        //this.faceEntity(this.getAttackTarget(), 10.0F, 10.0F);
           /* if (dist >= 4.0D * getRenderScale() && dist <= 16.0D * getRenderScale() && (this.isInSand() || this.onGround)) {
                this.setWormJumping(true);
                double d0 = this.getAttackTarget().getPosX() - this.getPosX();
                double d1 = this.getAttackTarget().getPosZ() - this.getPosZ();
                float leap = MathHelper.sqrt(d0 * d0 + d1 * d1);
                if ((double) leap >= 1.0E-4D) {
                    this.setMotion(this.getMotion().add(d0 / (double) leap * 0.5D, 0.15F, d1 / (double) leap * 0.5D));
                }
                this.setAnimation(ANIMATION_BITE);
            }*/
        if (this.getAttackTarget() != null && this.getDistance(this.getAttackTarget()) < Math.min(4, 4D * getRenderScale()) && this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 5) {
            float f = (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), f);
            this.setMotion(this.getMotion().add(0, -0.4F, 0));
        }

    }

    public int getWormBrightness(boolean sky) {
        BlockPos eyePos = new BlockPos(this.getEyePosition(1.0F));
        while (eyePos.getY() < 256 && !world.isAirBlock(eyePos)) {
            eyePos = eyePos.up();
        }
        int light = this.world.getLightFor(sky ? LightType.SKY : LightType.BLOCK, eyePos.up());
        return light;
    }

    public int getSurface(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        while (!world.isAirBlock(pos)) {
            pos = pos.up();
        }
        return pos.getY();
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return this.getRenderScale() > 3 ? IafSoundRegistry.DEATHWORM_GIANT_IDLE : IafSoundRegistry.DEATHWORM_IDLE;
    }


    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return this.getRenderScale() > 3 ? IafSoundRegistry.DEATHWORM_GIANT_HURT : IafSoundRegistry.DEATHWORM_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return this.getRenderScale() > 3 ? IafSoundRegistry.DEATHWORM_GIANT_DIE : IafSoundRegistry.DEATHWORM_DIE;
    }

    @Override
    public void tick() {
        super.tick();
        recalculateSize();
        onUpdateParts();
        if (this.attack() && this.getControllingPassenger() != null && this.getControllingPassenger() instanceof PlayerEntity) {
            LivingEntity target = DragonUtils.riderLookingAtEntity(this, (PlayerEntity) this.getControllingPassenger(), 3);
            if (this.getAnimation() != ANIMATION_BITE) {
                this.setAnimation(ANIMATION_BITE);
                this.playSound(this.getRenderScale() > 3 ? IafSoundRegistry.DEATHWORM_GIANT_ATTACK : IafSoundRegistry.DEATHWORM_ATTACK, 1, 1);
                if (this.getRNG().nextInt(3) == 0 && this.getRenderScale() > 1) {
                    float radius = 1.5F * this.getRenderScale();
                    float angle = (0.01745329251F * this.renderYawOffset);
                    double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
                    double extraZ = radius * MathHelper.cos(angle);
                    BlockLaunchExplosion explosion = new BlockLaunchExplosion(world, this, this.getPosX() + extraX, this.getPosY() - this.getEyeHeight(), this.getPosZ() + extraZ, this.getRenderScale() * 0.75F);
                    explosion.doExplosionA();
                    explosion.doExplosionB(true);
                }
            }
            if (target != null) {
                target.attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
            }
        }
        if (!this.isInSand()) {

        } else {
            BlockPos pos = new BlockPos(this.getPosX(), this.getSurface((int) Math.floor(this.getPosX()), (int) Math.floor(this.getPosY()), (int) Math.floor(this.getPosZ())), this.getPosZ()).down();
            BlockState state = world.getBlockState(pos);
            if (state.isOpaqueCube(world, pos)) {
                if (world.isRemote) {
                    this.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, state), this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getSurface((int) Math.floor(this.getPosX()), (int) Math.floor(this.getPosY()), (int) Math.floor(this.getPosZ())) + 0.5F, this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D, this.rand.nextGaussian() * 0.02D);
                }
            }
            if (this.ticksExisted % 10 == 0) {
                this.playSound(SoundEvents.BLOCK_SAND_BREAK, 1, 0.5F);
            }
        }
        if (this.up() && this.onGround) {
            this.jump();
        }
        boolean inSand = isInSand();
        if (inSand && !this.isSandNavigator) {
            switchNavigator(true);
        }
        if (!inSand && this.isSandNavigator) {
            switchNavigator(false);
        }
        if (world.isRemote) {
            tail_buffer.calculateChainSwingBuffer(90, 20, 5F, this);
        }
        if (this.getControllingPassenger() != null) {
            this.pushOutOfBlocks(this.getPosX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getPosZ());
        }
        if (world.isRemote) {
            this.updateClientControls();
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @OnlyIn(Dist.CLIENT)
    protected void updateClientControls() {
        Minecraft mc = Minecraft.getInstance();
        if (this.isRidingPlayer(mc.player)) {
            byte previousState = getControlState();
            up(mc.gameSettings.keyBindJump.isKeyDown());
            dismount(mc.gameSettings.keyBindSneak.isKeyDown());
            attack(IafKeybindRegistry.dragon_strike.isKeyDown());
            byte controlState = getControlState();
            if (controlState != previousState) {
                IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonControl(this.getEntityId(), controlState, getPosX(), getPosY(), getPosZ()));
            }
        }
        if (this.getRidingEntity() != null && this.getRidingEntity() == mc.player) {
            byte previousState = getControlState();
            dismount(mc.gameSettings.keyBindSneak.isKeyDown());
            byte controlState = getControlState();
            if (controlState != previousState) {
                IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonControl(this.getEntityId(), controlState, getPosX(), getPosY(), getPosZ()));
            }
        }
    }

    public boolean up() {
        return (dataManager.get(CONTROL_STATE).byteValue() & 1) == 1;
    }

    public boolean dismountIAF() {
        return (dataManager.get(CONTROL_STATE).byteValue() >> 1 & 1) == 1;
    }

    public boolean attack() {
        return (dataManager.get(CONTROL_STATE).byteValue() >> 2 & 1) == 1;
    }

    public void up(boolean up) {
        setStateField(0, up);
    }

    public void dismount(boolean dismount) {
        setStateField(1, dismount);
    }

    public void attack(boolean attack) {
        setStateField(2, attack);
    }

    public boolean isSandBelow() {
        int i = MathHelper.floor(this.getPosX());
        int j = MathHelper.floor(this.getPosY() + 1);
        int k = MathHelper.floor(this.getPosZ());
        BlockPos blockpos = new BlockPos(i, j, k);
        BlockState BlockState = this.world.getBlockState(blockpos);
        return BlockState.getMaterial() == Material.SAND;
    }

    public boolean isInSand() {
        return this.getControllingPassenger() == null && isInSandStrict();
    }

    public boolean isInSandStrict() {
        return world.getBlockState(getPosition()).isIn(BlockTags.SAND);
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
        return new Animation[]{ANIMATION_BITE};
    }

    public Entity[] getWormParts() {
        return segments;
    }

    public int getHorizontalFaceSpeed() {
        return 10;
    }

    @Override
    public boolean canPassengerSteer() {
        return false;
    }

    @Override
    public boolean canBeSteered() {
        return true;
    }


    @Override
    public void travel(Vector3d vec) {
        super.travel(vec);
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return true;
    }

    public boolean canBeTurnedToStone() {
        return false;
    }

    @Override
    public boolean canPassThrough(BlockPos pos, BlockState state, VoxelShape shape) {
        return world.getBlockState(pos).getMaterial() == Material.SAND;
    }

    @Override
    public boolean isNoDespawnRequired() {
        return true;
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    public boolean isRidingPlayer(PlayerEntity player) {
        return getRidingPlayer() != null && player != null && getRidingPlayer().getUniqueID().equals(player.getUniqueID());
    }

    @Nullable
    public PlayerEntity getRidingPlayer() {
        if (this.getControllingPassenger() instanceof PlayerEntity) {
            return (PlayerEntity) this.getControllingPassenger();
        }
        return null;
    }

    @Override
    public double getRideSpeedModifier() {
        return 1;
    }

    public class SandMoveHelper extends MovementController {
        private EntityDeathWorm worm = EntityDeathWorm.this;

        public SandMoveHelper() {
            super(EntityDeathWorm.this);
        }

        @Override
        public void tick() {
            if (this.action == MovementController.Action.MOVE_TO) {
                Vector3d Vector3d = new Vector3d(this.posX - EntityDeathWorm.this.getPosX(), this.posY - EntityDeathWorm.this.getPosY(), this.posZ - EntityDeathWorm.this.getPosZ());
                double d0 = Vector3d.length();
                if (d0 < EntityDeathWorm.this.getBoundingBox().getAverageEdgeLength()) {
                    this.action = MovementController.Action.WAIT;
                    EntityDeathWorm.this.setMotion(EntityDeathWorm.this.getMotion().scale(0.5D));
                } else {
                    this.speed = 1.0F;
                    EntityDeathWorm.this.setMotion(EntityDeathWorm.this.getMotion().add(Vector3d.scale(this.speed * 0.05D / d0)));
                    Vector3d Vector3d1 = EntityDeathWorm.this.getMotion();
                    EntityDeathWorm.this.rotationYaw = -((float) MathHelper.atan2(Vector3d1.x, Vector3d1.z)) * (180F / (float) Math.PI);
                    EntityDeathWorm.this.renderYawOffset = EntityDeathWorm.this.rotationYaw;
                }

            }
        }
    }
}
