package com.github.alexthe666.iceandfire.entity;

import java.util.Random;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.datatracker.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.IafKeybindRegistry;
import com.github.alexthe666.iceandfire.client.model.IFChainBuffer;
import com.github.alexthe666.iceandfire.entity.ai.AmphithereAIAttackMelee;
import com.github.alexthe666.iceandfire.entity.ai.AmphithereAIFleePlayer;
import com.github.alexthe666.iceandfire.entity.ai.AmphithereAIFollowOwner;
import com.github.alexthe666.iceandfire.entity.ai.AmphithereAIHurtByTarget;
import com.github.alexthe666.iceandfire.entity.ai.AmphithereAITargetItems;
import com.github.alexthe666.iceandfire.entity.ai.DragonAIRide;
import com.github.alexthe666.iceandfire.entity.ai.EntityAIWatchClosestIgnoreRider;
import com.github.alexthe666.iceandfire.entity.props.MiscEntityProperties;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IDragonFlute;
import com.github.alexthe666.iceandfire.entity.util.IFlapable;
import com.github.alexthe666.iceandfire.entity.util.IFlyingMount;
import com.github.alexthe666.iceandfire.entity.util.IPhasesThroughBlock;
import com.github.alexthe666.iceandfire.entity.util.ISyncMount;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.message.MessageDragonControl;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.github.alexthe666.iceandfire.pathfinding.PathNavigateFlyingCreature;

import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.OwnerHurtByTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtTargetGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.ClimberPathNavigator;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityAmphithere extends TameableEntity implements ISyncMount, IAnimatedEntity, IPhasesThroughBlock, IFlapable, IDragonFlute, IFlyingMount {

    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityAmphithere.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityAmphithere.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> FLAP_TICKS = EntityDataManager.createKey(EntityAmphithere.class, DataSerializers.VARINT);
    private static final DataParameter<Byte> CONTROL_STATE = EntityDataManager.createKey(EntityAmphithere.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> COMMAND = EntityDataManager.createKey(EntityAmphithere.class, DataSerializers.VARINT);
    public static Animation ANIMATION_BITE = Animation.create(15);
    public static Animation ANIMATION_BITE_RIDER = Animation.create(15);
    public static Animation ANIMATION_WING_BLAST = Animation.create(30);
    public static Animation ANIMATION_TAIL_WHIP = Animation.create(30);
    public static Animation ANIMATION_SPEAK = Animation.create(10);
    public float flapProgress;
    public float groundProgress = 0;
    public float sitProgress = 0;
    public float diveProgress = 0;
    @OnlyIn(Dist.CLIENT)
    public IFChainBuffer roll_buffer;
    @OnlyIn(Dist.CLIENT)
    public IFChainBuffer tail_buffer;
    @OnlyIn(Dist.CLIENT)
    public IFChainBuffer pitch_buffer;
    @Nullable
    public BlockPos orbitPos = null;
    public float orbitRadius = 0.0F;
    public boolean isFallen;
    public BlockPos homePos;
    public boolean hasHomePosition = false;
    protected FlightBehavior flightBehavior = FlightBehavior.WANDER;
    protected int ticksCircling = 0;
    private int animationTick;
    private Animation currentAnimation;
    private int flapTicks = 0;
    private int flightCooldown = 0;
    private int ticksFlying = 0;
    private boolean isFlying;
    private boolean changedFlightBehavior = false;
    private int ticksStill = 0;
    private int ridingTime = 0;
    private boolean isSitting;
    /*
          0 = ground/walking
          1 = ai flight
          2 = controlled flight
       */
    private int navigatorType = 0;

    public EntityAmphithere(EntityType type, World worldIn) {
        super(type, worldIn);
        this.stepHeight = 1;
        if (worldIn.isRemote) {
            roll_buffer = new IFChainBuffer();
            pitch_buffer = new IFChainBuffer();
            tail_buffer = new IFChainBuffer();
        }
        switchNavigator(0);
    }

    public static BlockPos getPositionRelativetoGround(Entity entity, World world, double x, double z, Random rand) {
        BlockPos pos = new BlockPos(x, entity.getPosY(), z);
        for (int yDown = 0; yDown < 6 + rand.nextInt(6); yDown++) {
            if (!world.isAirBlock(pos.down(yDown))) {
                return pos.up(yDown);
            }
        }
        return pos;
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        if(worldIn instanceof IServerWorld && !IafWorldRegistry.isDimensionListedForMobs((IServerWorld)world)){
            return false;
        }
        return super.canSpawn(worldIn, spawnReasonIn);
    }

    public static boolean canAmphithereSpawnOn(EntityType<EntityAmphithere> parrotIn, IWorld worldIn, SpawnReason reason, BlockPos p_223317_3_, Random random) {
        Block block = worldIn.getBlockState(p_223317_3_.down()).getBlock();
        return (block.isIn(BlockTags.LEAVES) || block == Blocks.GRASS_BLOCK || block.isIn(BlockTags.LOGS) || block == Blocks.AIR);
    }

    public boolean isNotColliding(IWorldReader worldIn) {
        if (worldIn.checkNoEntityCollision(this) && !worldIn.containsAnyLiquid(this.getBoundingBox())) {
            BlockPos blockpos = this.getPosition();
            if (blockpos.getY() < worldIn.getSeaLevel()) {
                return false;
            }

            BlockState blockstate = worldIn.getBlockState(blockpos.down());
            if (blockstate.matchesBlock(Blocks.GRASS_BLOCK) || blockstate.isIn(BlockTags.LEAVES)) {
                return true;
            }
        }

        return false;
    }

    public static BlockPos getPositionInOrbit(EntityAmphithere entity, World world, BlockPos orbit, Random rand) {
        float possibleOrbitRadius = (entity.orbitRadius + 10.0F);
        float radius = 10;
        if (entity.getCommand() == 2) {
            if (entity.getOwner() != null) {
                orbit = entity.getOwner().getPosition().up(7);
                radius = 5;
            }
        } else if (entity.hasHomePosition) {
            orbit = entity.homePos.up(30);
            radius = 30;
        }
        float angle = (0.01745329251F * possibleOrbitRadius);
        double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
        double extraZ = radius * MathHelper.cos(angle);
        BlockPos radialPos = new BlockPos(orbit.getX() + extraX, orbit.getY(), orbit.getZ() + extraZ);
        //world.setBlockState(radialPos.down(4), Blocks.QUARTZ_BLOCK.getDefaultState());
        // world.setBlockState(orbit.down(4), Blocks.GOLD_BLOCK.getDefaultState());
        entity.orbitRadius = possibleOrbitRadius;
        return radialPos;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public float getBlockPathWeight(BlockPos pos) {
        if (this.isFlying()) {
            if (world.isAirBlock(pos)) {
                return 10F;
            } else {
                return 0F;
            }
        } else {
            return super.getBlockPathWeight(pos);
        }
    }

    public ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);

        if (itemstack != null && itemstack.getItem() == Items.COOKIE) {
            if (this.getGrowingAge() == 0 && !isInLove()) {
                this.setSitting(false);
                this.setInLove(player);
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1, 1);
                if (!player.isCreative()) {
                    itemstack.shrink(1);
                }
            }
            return ActionResultType.SUCCESS;
        }
        if (itemstack != null && itemstack.getItem() == Items.COCOA_BEANS && this.getHealth() < this.getMaxHealth()) {
            this.heal(5);
            this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1, 1);
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            return ActionResultType.SUCCESS;
        }
        if (super.getEntityInteractionResult(player, hand) == ActionResultType.PASS) {
            if (itemstack != null && itemstack.getItem() == IafItemRegistry.DRAGON_STAFF && this.isOwner(player)) {
                if (player.isSneaking()) {
                    BlockPos pos = this.getPosition();
                    this.homePos = pos;
                    this.hasHomePosition = true;
                    player.sendStatusMessage(new TranslationTextComponent("amphithere.command.new_home", homePos.getX(), homePos.getY(), homePos.getZ()), true);
                    return ActionResultType.SUCCESS;
                }
                return ActionResultType.SUCCESS;
            }
            if (player.isSneaking() && this.isOwner(player)) {
                if (player.getHeldItem(hand).isEmpty()) {
                    this.setCommand(this.getCommand() + 1);
                    if (this.getCommand() > 2) {
                        this.setCommand(0);
                    }
                    player.sendStatusMessage(new TranslationTextComponent("amphithere.command." + this.getCommand()), true);
                    this.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 1, 1);
                    return ActionResultType.SUCCESS;
                }
                return ActionResultType.SUCCESS;
            } else if ((!this.isTamed() || this.isOwner(player)) && !this.isChild()) {
                player.startRiding(this);
                return ActionResultType.SUCCESS;
            }

        }
        return ActionResultType.SUCCESS;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new DragonAIRide(this));
        this.goalSelector.addGoal(0, new SitGoal(this));
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(1, new AmphithereAIAttackMelee(this, 1.0D, true));
        this.goalSelector.addGoal(2, new AmphithereAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(3, new AmphithereAIFleePlayer(this, 32.0F, 0.8D, 1.8D));
        this.goalSelector.addGoal(3, new AIFlyWander());
        this.goalSelector.addGoal(3, new AIFlyCircle());
        this.goalSelector.addGoal(3, new AILandWander(this, 1.0D));
        this.goalSelector.addGoal(4, new EntityAIWatchClosestIgnoreRider(this, LivingEntity.class, 6.0F));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new AmphithereAIHurtByTarget(this, false, new Class[0]));
        this.targetSelector.addGoal(3, new AmphithereAITargetItems(this, false));
    }

    public boolean isStill() {
        return Math.abs(this.getMotion().x) < 0.05 && Math.abs(this.getMotion().z) < 0.05;
    }

    protected void switchNavigator(int navigatorType) {
        if (navigatorType == 0) {
            this.moveController = new MovementController(this);
            this.navigator = new ClimberPathNavigator(this, world);
            this.navigatorType = 0;
        } else if (navigatorType == 1) {
            this.moveController = new EntityAmphithere.FlyMoveHelper(this);
            this.navigator = new PathNavigateFlyingCreature(this, world);
            this.navigatorType = 1;
        } else {
            this.moveController = new IafDragonFlightManager.PlayerFlightMoveHelper(this);
            this.navigator = new PathNavigateFlyingCreature(this, world);
            this.navigatorType = 2;
        }
    }

    public boolean onLeaves() {
        BlockState state = world.getBlockState(this.getPosition().down());
        return state.getBlock() instanceof LeavesBlock;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (!this.isTamed() && this.isFlying() && !isOnGround() && source.isProjectile() && !world.isRemote) {
            this.isFallen = true;
        }
        if (source.getTrueSource() instanceof LivingEntity && source.getTrueSource().isRidingSameEntity(this) && this.isTamed() && this.isOwner((LivingEntity) source.getTrueSource())) {
            return false;
        }
        return super.attackEntityFrom(source, damage);
    }

    public void updatePassenger(Entity passenger) {
        super.updatePassenger(passenger);
        if (this.isPassenger(passenger) && this.isTamed()) {
            this.rotationYaw = passenger.rotationYaw;
            //renderYawOffset = rotationYaw;
        }
        if (!this.world.isRemote && !this.isTamed() && passenger instanceof PlayerEntity && this.getAnimation() == NO_ANIMATION && rand.nextInt(15) == 0) {
            this.setAnimation(ANIMATION_BITE_RIDER);
        }
        if (!this.world.isRemote && this.getAnimation() == ANIMATION_BITE_RIDER && this.getAnimationTick() == 6 && !this.isTamed()) {
            passenger.attackEntityFrom(DamageSource.causeMobDamage(this), 1);
        }
        float pitch_forward = 0;
        if (this.rotationPitch > 0 && this.isFlying()) {
            pitch_forward = (rotationPitch / 45F) * 0.45F;
        } else {
            pitch_forward = 0;
        }
        float scaled_ground = this.groundProgress * 0.1F;
        float radius = (this.isTamed() ? 0.5F : 0.3F) - scaled_ground * 0.5F + pitch_forward;
        float angle = (0.01745329251F * this.renderYawOffset);
        double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
        double extraZ = radius * MathHelper.cos(angle);
        passenger.setPosition(this.getPosX() + extraX, this.getPosY() + 0.7F - scaled_ground * 0.14F + pitch_forward, this.getPosZ() + extraZ);

    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.COOKIE;
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (world.getDifficulty() == Difficulty.PEACEFUL && this.getAttackTarget() instanceof PlayerEntity) {
            this.setAttackTarget(null);
        }
        if (this.isInWater() && this.isJumping) {
            this.setMotion(this.getMotion().x, this.getMotion().y + 0.1D, this.getMotion().z);
        }
        if (this.isChild() && this.getAttackTarget() != null) {
            this.setAttackTarget(null);
        }
        if (this.isInLove()) {
            this.setFlying(false);
        }
        if (this.isQueuedToSit() && this.getAttackTarget() != null) {
            this.setAttackTarget(null);
        }
        boolean flapping = this.isFlapping();
        boolean flying = this.isFlying() && this.isOverAir() || (this.isOverAir() && !onLeaves());
        boolean diving = flying && this.getMotion().y <= -0.1F || this.isFallen;
        boolean sitting = isQueuedToSit() && !isFlying();
        boolean notGrounded = flying || this.getAnimation() == ANIMATION_WING_BLAST;
        if (!world.isRemote) {
            if (this.isQueuedToSit() && (this.getCommand() != 1 || this.getControllingPassenger() != null)) {
                this.setSitting(false);
            }
            if (!this.isQueuedToSit() && this.getCommand() == 1 && this.getControllingPassenger() == null) {
                this.setSitting(true);
            }
            if (this.isQueuedToSit()) {
                this.getNavigator().clearPath();
                //TODO
                //this.getMoveHelper().action = MovementController.Action.WAIT;
            }
            if (flying) {
                ticksFlying++;
            } else {
                ticksFlying = 0;
            }
        }
        if (isFlying() && this.onGround) {
            this.setFlying(false);
        }
        if (sitting && sitProgress < 20.0F) {
            sitProgress += 0.5F;
        } else if (!sitting && sitProgress > 0.0F) {
            sitProgress -= 0.5F;
        }
        if (flightCooldown > 0) {
            flightCooldown--;
        }
        if (!world.isRemote) {
            if (this.flightBehavior == FlightBehavior.CIRCLE) {
                ticksCircling++;
            } else {
                ticksCircling = 0;
            }
        }
        if (this.getUntamedRider() != null && !this.isTamed()) {
            ridingTime++;
        }
        if (this.getUntamedRider() == null) {
            ridingTime = 0;
        }
        if (!this.isTamed() && ridingTime > IafConfig.amphithereTameTime && this.getUntamedRider() != null && this.getUntamedRider() instanceof PlayerEntity) {
            this.world.setEntityState(this, (byte) 45);
            this.setTamedBy((PlayerEntity) this.getUntamedRider());
        }
        if (world.isRemote) {
            this.updateClientControls();
        }
        if (isStill()) {
            this.ticksStill++;
        } else {
            this.ticksStill = 0;
        }
        if (!this.isFlying() && !this.isChild() && ((this.isOnGround() && this.rand.nextInt(200) == 0 && flightCooldown == 0 && this.getPassengers().isEmpty() && !this.isAIDisabled() && canMove()) || this.getPosY() < -1)) {
            this.setMotion(this.getMotion().x, this.getMotion().y + 0.5D, this.getMotion().z);
            this.setFlying(true);
        }
        if (this.getControllingPassenger() != null && this.isFlying() && !this.onGround) {
            this.rotationPitch = this.getControllingPassenger().rotationPitch / 2;

            if (this.getControllingPassenger().rotationPitch > 25 && this.getMotion().y > -1.0F) {
                this.setMotion(this.getMotion().x, this.getMotion().y - 0.1D, this.getMotion().z);

            }
            if (this.getControllingPassenger().rotationPitch < -25 && this.getMotion().y < 1.0F) {
                this.setMotion(this.getMotion().x, this.getMotion().y + 0.1D, this.getMotion().z);

            }
        }
        if (notGrounded && groundProgress > 0.0F) {
            groundProgress -= 2F;
        } else if (!notGrounded && groundProgress < 20.0F) {
            groundProgress += 2F;
        }
        if (diving && diveProgress < 20.0F) {
            diveProgress += 1F;
        } else if (!diving && diveProgress > 0.0F) {
            diveProgress -= 1F;
        }
        if (this.isFlying()) {
            this.setMotion(this.getMotion().x, this.getMotion().y + 0.08D, this.getMotion().z);
        }
        if (this.isFallen && this.flightBehavior != FlightBehavior.NONE) {
            this.flightBehavior = FlightBehavior.NONE;
        }
        if (this.flightBehavior == FlightBehavior.NONE && this.getControllingPassenger() == null && this.isFlying()) {
            this.setMotion(this.getMotion().x, this.getMotion().y - 0.3D, this.getMotion().z);
        }
        if (this.isFlying() && !this.isOnGround() && this.isFallen && this.getControllingPassenger() == null) {
            this.setMotion(this.getMotion().x, this.getMotion().y - 0.2D, this.getMotion().z);
            this.rotationPitch = Math.max(this.rotationPitch + 5, 75);
        }
        if (this.isFallen && this.onGround) {
            this.setFlying(false);
            if (this.isTamed()) {
                flightCooldown = 50;
            } else {
                flightCooldown = 12000;
            }
            this.isFallen = false;
        }
        if (flying && this.isOverAir()) {
            if (this.getRidingPlayer() == null && this.navigatorType != 1) {
                switchNavigator(1);
            }
            if (this.getRidingPlayer() != null && this.navigatorType != 2) {
                switchNavigator(2);
            }
        }
        if (!flying && this.navigatorType != 0) {
            switchNavigator(0);
        }
        if ((this.hasHomePosition || this.getCommand() == 2) && this.flightBehavior == FlightBehavior.WANDER) {
            this.flightBehavior = FlightBehavior.CIRCLE;
        }
        if (flapping && flapProgress < 10.0F) {
            flapProgress += 1F;
        } else if (!flapping && flapProgress > 0.0F) {
            flapProgress -= 1F;
        }
        if (flapTicks > 0) {
            flapTicks--;
        }
        renderYawOffset = rotationYaw;
        if (world.isRemote) {
            if (!onGround) {
                roll_buffer.calculateChainFlapBuffer(this.isBeingRidden() ? 55 : 90, 1, 10F, 0.5F, this);
                pitch_buffer.calculateChainPitchBuffer(90, 10, 10F, 0.5F, this);
            }
            tail_buffer.calculateChainSwingBuffer(70, 20, 5F, this);
        }
        if (changedFlightBehavior) {
            changedFlightBehavior = false;
        }
        if (!flapping && (this.getMotion().y > 0.15F || this.getMotion().y > 0 && this.ticksExisted % 200 == 0) && this.isOverAir()) {
            flapWings();
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public boolean isFlapping() {
        return flapTicks > 0;
    }

    public int getCommand() {
        return Integer.valueOf(this.dataManager.get(COMMAND).intValue());
    }

    public void setCommand(int command) {
        this.dataManager.set(COMMAND, Integer.valueOf(command));
        if (command == 1) {
            this.setSitting(true);
        } else {
            this.setSitting(false);
        }
    }

    public void flapWings() {
        this.flapTicks = 20;
    }

    public boolean isQueuedToSit() {
        if (world.isRemote) {
            boolean isSitting = (this.dataManager.get(TAMED).byteValue() & 1) != 0;
            this.isSitting = isSitting;
            return isSitting;
        }
        return isSitting;
    }

    public void setSitting(boolean sitting) {
        if (!world.isRemote) {
            this.isSitting = sitting;
        }
        byte b0 = this.dataManager.get(TAMED).byteValue();
        if (sitting) {
            this.dataManager.set(TAMED, Byte.valueOf((byte) (b0 | 1)));
        } else {
            this.dataManager.set(TAMED, Byte.valueOf((byte) (b0 & -2)));
        }
    }

    @Nullable
    public Entity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof PlayerEntity && this.getAttackTarget() != passenger) {
                PlayerEntity player = (PlayerEntity) passenger;
                if (this.isTamed() && this.getOwnerId() != null && this.getOwnerId().equals(player.getUniqueID())) {
                    return player;
                }
            }
        }
        return null;
    }

    @Nullable
    public Entity getUntamedRider() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof PlayerEntity) {
                return passenger;
            }
        }
        return null;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, IafConfig.amphithereMaxHealth)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.4D)
                //ATTACK
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, IafConfig.amphithereAttackStrength)
                //FOLLOW RANGE
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(VARIANT, Integer.valueOf(0));
        this.dataManager.register(FLYING, false);
        this.dataManager.register(FLAP_TICKS, Integer.valueOf(0));
        this.dataManager.register(CONTROL_STATE, Byte.valueOf((byte) 0));
        this.dataManager.register(COMMAND, Integer.valueOf(0));
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putBoolean("Flying", this.isFlying());
        compound.putInt("FlightCooldown", flightCooldown);
        compound.putInt("RidingTime", ridingTime);
        compound.putBoolean("HasHomePosition", this.hasHomePosition);
        if (homePos != null && this.hasHomePosition) {
            compound.putInt("HomeAreaX", homePos.getX());
            compound.putInt("HomeAreaY", homePos.getY());
            compound.putInt("HomeAreaZ", homePos.getZ());
        }
        compound.putInt("Command", this.getCommand());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setVariant(compound.getInt("Variant"));
        this.setFlying(compound.getBoolean("Flying"));
        flightCooldown = compound.getInt("FlightCooldown");
        ridingTime = compound.getInt("RidingTime");
        this.hasHomePosition = compound.getBoolean("HasHomePosition");
        if (hasHomePosition && compound.getInt("HomeAreaX") != 0 && compound.getInt("HomeAreaY") != 0 && compound.getInt("HomeAreaZ") != 0) {
            homePos = new BlockPos(compound.getInt("HomeAreaX"), compound.getInt("HomeAreaY"), compound.getInt("HomeAreaZ"));
        }
        this.setCommand(compound.getInt("Command"));
    }

    public boolean getCanSpawnHere() {
        int i = MathHelper.floor(this.getPosX());
        int j = MathHelper.floor(this.getBoundingBox().minY);
        int k = MathHelper.floor(this.getPosZ());
        BlockPos blockpos = new BlockPos(i, j, k);
        Block block = this.world.getBlockState(blockpos.down()).getBlock();
        return this.world.canBlockSeeSky(blockpos.up());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getAnimation() == ANIMATION_BITE && this.getAttackTarget() != null && this.getAnimationTick() == 7) {
            double dist = this.getDistanceSq(this.getAttackTarget());
            if (dist < 10) {
                this.getAttackTarget().applyKnockback(0.6F, MathHelper.sin(this.rotationYaw * 0.017453292F), -MathHelper.cos(this.rotationYaw * 0.017453292F));
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
            }
        }
        if (this.getAnimation() == ANIMATION_WING_BLAST && this.getAnimationTick() == 5) {
            this.playSound(IafSoundRegistry.AMPHITHERE_GUST, 1, 1);
        }
        if ((this.getAnimation() == ANIMATION_BITE || this.getAnimation() == ANIMATION_BITE_RIDER) && this.getAnimationTick() == 1) {
            this.playSound(IafSoundRegistry.AMPHITHERE_BITE, 1, 1);
        }
        if (this.getAnimation() == ANIMATION_WING_BLAST && this.getAttackTarget() != null && this.getAnimationTick() > 5 && this.getAnimationTick() < 22) {
            LivingEntity target = this.getAttackTarget();
            double dist = this.getDistanceSq(target);
            if (dist < 25) {
                target.attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() / 2));
                target.isAirBorne = true;
                if (!(this.rand.nextDouble() < this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue())) {
                    this.isAirBorne = true;
                    double d1 = target.getPosX() - this.getPosX();

                    double d0;
                    for (d0 = target.getPosZ() - this.getPosZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
                        d1 = (Math.random() - Math.random()) * 0.01D;
                    }
                    Vector3d Vector3d = this.getMotion();
                    Vector3d Vector3d1 = (new Vector3d(d0, 0.0D, d1)).normalize().scale(0.5);
                    this.setMotion(Vector3d.x / 2.0D - Vector3d1.x, this.isOnGround() ? Math.min(0.4D, Vector3d.y / 2.0D + 0.5) : Vector3d.y, Vector3d.z / 2.0D - Vector3d1.z);
                }
            }
        }
        if (this.getAnimation() == ANIMATION_TAIL_WHIP && this.getAttackTarget() != null && this.getAnimationTick() == 7) {
            LivingEntity target = this.getAttackTarget();
            double dist = this.getDistanceSq(this.getAttackTarget());
            if (dist < 10) {
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
                this.getAttackTarget().isAirBorne = true;
                float f = MathHelper.sqrt(0.5 * 0.5 + 0.5 * 0.5);
                double d0;
                double d1 = target.getPosX() - this.getPosX();
                for (d0 = target.getPosZ() - this.getPosZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
                    d1 = (Math.random() - Math.random()) * 0.01D;
                }
                Vector3d Vector3d = this.getMotion();
                Vector3d Vector3d1 = (new Vector3d(d0, 0.0D, d1)).normalize().scale(0.5);
                this.setMotion(Vector3d.x / 2.0D - Vector3d1.x, this.isOnGround() ? Math.min(0.4D, Vector3d.y / 2.0D + 0.5) : Vector3d.y, Vector3d.z / 2.0D - Vector3d1.z);

            }
        }
        if (world.isRemote) {
            this.updateClientControls();
        }
        if (this.isGoingUp() && !world.isRemote) {
            if (!this.isFlying()) {
                this.setMotion(this.getMotion().add(0, 1, 0));
                this.setFlying(true);
            }
        }
        if (!this.isOverAir() && this.isFlying() && ticksFlying > 25) {
            this.setFlying(false);
        }
        if (this.dismountIAF()) {
            if (this.isFlying()) {
                if (this.onGround) {
                    this.setFlying(false);
                }
            }
        }
        if (this.getUntamedRider() != null && this.getUntamedRider().isSneaking()) {
            MiscEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(this.getUntamedRider(), MiscEntityProperties.class);
            if (properties != null) {
                properties.hasDismountedDragon = true;
            }
            this.getUntamedRider().stopRiding();
        }
        if (this.attack() && this.getControllingPassenger() != null && this.getControllingPassenger() instanceof PlayerEntity) {
            LivingEntity target = DragonUtils.riderLookingAtEntity(this, (PlayerEntity) this.getControllingPassenger(), 2.5D);
            if (this.getAnimation() != ANIMATION_BITE) {
                this.setAnimation(ANIMATION_BITE);
            }
            if (target != null) {
                target.attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
            }
        }
        if (this.getAttackTarget() != null && this.isOwner(this.getAttackTarget())) {
            this.setAttackTarget(null);
        }
        if (this.getAttackTarget() != null && this.isOnGround() && this.isFlying() && ticksFlying > 40) {
            this.setFlying(false);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.getAnimation() != ANIMATION_BITE && this.getAnimation() != ANIMATION_TAIL_WHIP && this.getAnimation() != ANIMATION_WING_BLAST && this.getControllingPassenger() == null) {
            if (rand.nextBoolean()) {
                this.setAnimation(ANIMATION_BITE);
            } else {
                this.setAnimation(this.getRNG().nextBoolean() || this.isFlying() ? ANIMATION_WING_BLAST : ANIMATION_TAIL_WHIP);
            }
            return true;
        }
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

    @OnlyIn(Dist.CLIENT)
    protected void updateClientControls() {
        Minecraft mc = Minecraft.getInstance();
        if (this.isRidingPlayer(mc.player)) {
            byte previousState = getControlState();
            up(mc.gameSettings.keyBindJump.isKeyDown());
            down(IafKeybindRegistry.dragon_down.isKeyDown());
            attack(IafKeybindRegistry.dragon_strike.isKeyDown());
            dismount(mc.gameSettings.keyBindSneak.isKeyDown());
            byte controlState = getControlState();
            if (controlState != previousState) {
                IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonControl(this.getEntityId(), controlState, this.getPosX(), this.getPosY(), this.getPosZ()));
            }
        }
        if (this.getRidingEntity() != null && this.getRidingEntity() == mc.player) {
            byte previousState = getControlState();
            dismount(mc.gameSettings.keyBindSneak.isKeyDown());
            byte controlState = getControlState();
            if (controlState != previousState) {
                IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonControl(this.getEntityId(), controlState, this.getPosX(), this.getPosY(), this.getPosZ()));
            }
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

    public int getVariant() {
        return Integer.valueOf(this.dataManager.get(VARIANT).intValue());
    }

    public void setVariant(int variant) {
        this.dataManager.set(VARIANT, Integer.valueOf(variant));
    }

    public boolean isGoingUp() {
        return (dataManager.get(CONTROL_STATE).byteValue() & 1) == 1;
    }

    public boolean isGoingDown() {
        return (dataManager.get(CONTROL_STATE).byteValue() >> 1 & 1) == 1;
    }

    public boolean attack() {
        return (dataManager.get(CONTROL_STATE).byteValue() >> 2 & 1) == 1;
    }

    public boolean dismountIAF() {
        return (dataManager.get(CONTROL_STATE).byteValue() >> 3 & 1) == 1;
    }

    public void up(boolean up) {
        setStateField(0, up);
    }

    public void down(boolean down) {
        setStateField(1, down);
    }

    public void attack(boolean attack) {
        setStateField(2, attack);
    }

    public void dismount(boolean dismount) {
        setStateField(3, dismount);
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
        return dataManager.get(CONTROL_STATE).byteValue();
    }

    public void setControlState(byte state) {
        dataManager.set(CONTROL_STATE, Byte.valueOf(state));
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return IafSoundRegistry.AMPHITHERE_IDLE;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return IafSoundRegistry.AMPHITHERE_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return IafSoundRegistry.AMPHITHERE_DIE;
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        this.animationTick = tick;
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
        return new Animation[]{ANIMATION_BITE, ANIMATION_BITE_RIDER, ANIMATION_WING_BLAST, ANIMATION_TAIL_WHIP, ANIMATION_SPEAK};
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

    public boolean isBlinking() {
        return this.ticksExisted % 50 > 40;
    }

    @Nullable
    @Override
    public AgeableEntity createChild(ServerWorld serverWorld, AgeableEntity ageableEntity) {
        EntityAmphithere amphithere = new EntityAmphithere(IafEntityRegistry.AMPHITHERE, world);
        amphithere.setVariant(this.getVariant());
        return amphithere;
    }

    protected int getExperiencePoints(PlayerEntity player) {
        return 10;
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        spawnDataIn = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setVariant(this.getRNG().nextInt(5));
        return spawnDataIn;
    }



    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    public boolean canPhaseThroughBlock(IWorld world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() instanceof LeavesBlock;
    }

    @Override
    public void travel(Vector3d travelVector) {
        if (!this.canMove() && !this.isBeingRidden()) {
            super.travel(travelVector.mul(0, 1, 0));
            return;
        }
        super.travel(travelVector);
    }

    public boolean canMove() {
        return this.getControllingPassenger() == null && sitProgress == 0 && !this.isQueuedToSit();
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 45) {
            this.playEffect();
        } else {
            super.handleStatusUpdate(id);
        }
    }

    protected void playEffect() {
        for (int i = 0; i < 7; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.addParticle(ParticleTypes.HEART, this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getPosY() + 0.5D + (this.rand.nextFloat() * this.getHeight()), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), d0, d1, d2);
        }
    }

    @Override
    public void onHearFlute(PlayerEntity player) {
        if (!this.isOnGround() && this.isTamed()) {
            this.isFallen = true;
        }
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
    public boolean canPassengerSteer() {
        return false;
    }

    @Override
    public boolean canBeSteered() {
        return true;
    }

    @Override
    public double getFlightSpeedModifier() {
        return 0.555D;
    }

    @Override
    public boolean fliesLikeElytra() {
        return !this.onGround;
    }

    private boolean isOverAir() {
        return world.isAirBlock(this.getPosition().down());
    }

    public boolean canBlockPosBeSeen(BlockPos pos) {
        Vector3d Vector3d = new Vector3d(this.getPosX(), this.getPosYEye(), this.getPosZ());
        Vector3d Vector3d1 = new Vector3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        return this.world.rayTraceBlocks(new RayTraceContext(Vector3d, Vector3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)).getType() == RayTraceResult.Type.MISS;
    }

    public enum FlightBehavior {
        CIRCLE,
        WANDER,
        NONE
    }

    class AILandWander extends WaterAvoidingRandomWalkingGoal {
        public AILandWander(CreatureEntity creature, double speed) {
            super(creature, speed, 10);
        }

        public boolean shouldExecute() {
            return this.creature.isOnGround() && super.shouldExecute() && ((EntityAmphithere) this.creature).canMove();
        }
    }

    class AIFlyWander extends Goal {
        BlockPos target;

        public AIFlyWander() {
        }

        public boolean shouldExecute() {
            if (EntityAmphithere.this.flightBehavior != FlightBehavior.WANDER || !EntityAmphithere.this.canMove()) {
                return false;
            }
            if (EntityAmphithere.this.isFlying()) {
                target = EntityAmphithere.getPositionRelativetoGround(EntityAmphithere.this, EntityAmphithere.this.world, EntityAmphithere.this.getPosX() + EntityAmphithere.this.rand.nextInt(30) - 15, EntityAmphithere.this.getPosZ() + EntityAmphithere.this.rand.nextInt(30) - 15, EntityAmphithere.this.rand);
                EntityAmphithere.this.orbitPos = null;
                return (!EntityAmphithere.this.getMoveHelper().isUpdating() || EntityAmphithere.this.ticksStill >= 50);
            } else {
                return false;
            }
        }

        protected boolean isDirectPathBetweenPoints(Entity e) {
            return EntityAmphithere.this.canBlockPosBeSeen(target);
        }

        public boolean shouldContinueExecuting() {
            return false;
        }

        public void tick() {
            if (!isDirectPathBetweenPoints(EntityAmphithere.this)) {
                target = EntityAmphithere.getPositionRelativetoGround(EntityAmphithere.this, EntityAmphithere.this.world, EntityAmphithere.this.getPosX() + EntityAmphithere.this.rand.nextInt(30) - 15, EntityAmphithere.this.getPosZ() + EntityAmphithere.this.rand.nextInt(30) - 15, EntityAmphithere.this.rand);
            }
            if (EntityAmphithere.this.world.isAirBlock(target)) {
                EntityAmphithere.this.moveController.setMoveTo((double) target.getX() + 0.5D, (double) target.getY() + 0.5D, (double) target.getZ() + 0.5D, 0.25D);
                if (EntityAmphithere.this.getAttackTarget() == null) {
                    EntityAmphithere.this.getLookController().setLookPosition((double) target.getX() + 0.5D, (double) target.getY() + 0.5D, (double) target.getZ() + 0.5D, 180.0F, 20.0F);

                }
            }
        }
    }

    class AIFlyCircle extends Goal {
        BlockPos target;

        public AIFlyCircle() {
        }

        public boolean shouldExecute() {
            if (EntityAmphithere.this.flightBehavior != FlightBehavior.CIRCLE || !EntityAmphithere.this.canMove()) {
                return false;
            }
            if (EntityAmphithere.this.isFlying()) {
                EntityAmphithere.this.orbitPos = EntityAmphithere.getPositionRelativetoGround(EntityAmphithere.this, EntityAmphithere.this.world, EntityAmphithere.this.getPosX() + EntityAmphithere.this.rand.nextInt(30) - 15, EntityAmphithere.this.getPosZ() + EntityAmphithere.this.rand.nextInt(30) - 15, EntityAmphithere.this.rand);
                target = EntityAmphithere.getPositionInOrbit(EntityAmphithere.this, world, EntityAmphithere.this.orbitPos, EntityAmphithere.this.rand);
                return true;
            } else {
                return false;
            }
        }

        protected boolean isDirectPathBetweenPoints() {
            return EntityAmphithere.this.canBlockPosBeSeen(target);
        }

        public boolean shouldContinueExecuting() {
            return false;
        }

        public void tick() {
            if (!isDirectPathBetweenPoints()) {
                target = EntityAmphithere.getPositionInOrbit(EntityAmphithere.this, world, EntityAmphithere.this.orbitPos, EntityAmphithere.this.rand);
            }
            if (EntityAmphithere.this.world.isAirBlock(target)) {
                EntityAmphithere.this.moveController.setMoveTo((double) target.getX() + 0.5D, (double) target.getY() + 0.5D, (double) target.getZ() + 0.5D, 0.25D);
                if (EntityAmphithere.this.getAttackTarget() == null) {
                    EntityAmphithere.this.getLookController().setLookPosition((double) target.getX() + 0.5D, (double) target.getY() + 0.5D, (double) target.getZ() + 0.5D, 180.0F, 20.0F);

                }
            }
        }
    }

    class FlyMoveHelper extends MovementController {
        public FlyMoveHelper(EntityAmphithere entity) {
            super(entity);
            this.speed = 1.75F;
        }

        public void tick() {
            if (!EntityAmphithere.this.canMove()) {
                return;
            }
            if (EntityAmphithere.this.collidedHorizontally) {
                EntityAmphithere.this.rotationYaw += 180.0F;
                this.speed = 0.1F;
                BlockPos target = EntityAmphithere.getPositionRelativetoGround(EntityAmphithere.this, EntityAmphithere.this.world, EntityAmphithere.this.getPosX() + EntityAmphithere.this.rand.nextInt(15) - 7, EntityAmphithere.this.getPosZ() + EntityAmphithere.this.rand.nextInt(15) - 7, EntityAmphithere.this.rand);
                this.posX = target.getX();
                this.posY = target.getY();
                this.posZ = target.getZ();
            }
            if (this.action == MovementController.Action.MOVE_TO) {

                double d0 = this.posX - EntityAmphithere.this.getPosX();
                double d1 = this.posY - EntityAmphithere.this.getPosY();
                double d2 = this.posZ - EntityAmphithere.this.getPosZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                d3 = MathHelper.sqrt(d3);
                if (d3 < 6 && EntityAmphithere.this.getAttackTarget() == null) {
                    if (!EntityAmphithere.this.changedFlightBehavior && EntityAmphithere.this.flightBehavior == FlightBehavior.WANDER && EntityAmphithere.this.rand.nextInt(30) == 0) {
                        EntityAmphithere.this.flightBehavior = FlightBehavior.CIRCLE;
                        EntityAmphithere.this.changedFlightBehavior = true;
                    }
                    if (!EntityAmphithere.this.changedFlightBehavior && EntityAmphithere.this.flightBehavior == FlightBehavior.CIRCLE && EntityAmphithere.this.rand.nextInt(5) == 0 && ticksCircling > 150) {
                        EntityAmphithere.this.flightBehavior = FlightBehavior.WANDER;
                        EntityAmphithere.this.changedFlightBehavior = true;
                    }
                    if (EntityAmphithere.this.hasHomePosition && EntityAmphithere.this.flightBehavior != FlightBehavior.NONE || EntityAmphithere.this.getCommand() == 2) {
                        EntityAmphithere.this.flightBehavior = FlightBehavior.CIRCLE;
                    }
                }
                if (d3 < 1 && EntityAmphithere.this.getAttackTarget() == null) {
                    this.action = MovementController.Action.WAIT;
                    EntityAmphithere.this.setMotion(EntityAmphithere.this.getMotion().mul(0.5D, 0.5D, 0.5D));
                } else {
                    EntityAmphithere.this.setMotion(EntityAmphithere.this.getMotion().add(d0 / d3 * 0.5D * this.speed, d1 / d3 * 0.5D * this.speed, d2 / d3 * 0.5D * this.speed));
                    float f1 = (float) (-(MathHelper.atan2(d1, d3) * (180D / Math.PI)));
                    EntityAmphithere.this.rotationPitch = f1;
                    if (EntityAmphithere.this.getAttackTarget() == null) {
                        EntityAmphithere.this.rotationYaw = -((float) MathHelper.atan2(EntityAmphithere.this.getMotion().x, EntityAmphithere.this.getMotion().z)) * (180F / (float) Math.PI);
                        EntityAmphithere.this.renderYawOffset = EntityAmphithere.this.rotationYaw;
                    } else {
                        double d4 = EntityAmphithere.this.getAttackTarget().getPosX() - EntityAmphithere.this.getPosX();
                        double d5 = EntityAmphithere.this.getAttackTarget().getPosZ() - EntityAmphithere.this.getPosZ();
                        EntityAmphithere.this.rotationYaw = -((float) MathHelper.atan2(d4, d5)) * (180F / (float) Math.PI);
                        EntityAmphithere.this.renderYawOffset = EntityAmphithere.this.rotationYaw;
                    }
                }
            }
        }
    }
}
