package com.github.alexthe666.iceandfire.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.IafKeybindRegistry;
import com.github.alexthe666.iceandfire.client.model.IFChainBuffer;
import com.github.alexthe666.iceandfire.entity.ai.DragonAIRide;
import com.github.alexthe666.iceandfire.entity.ai.HippogryphAIMate;
import com.github.alexthe666.iceandfire.entity.ai.HippogryphAITarget;
import com.github.alexthe666.iceandfire.entity.ai.HippogryphAITargetItems;
import com.github.alexthe666.iceandfire.entity.ai.HippogryphAIWander;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.util.IAnimalFear;
import com.github.alexthe666.iceandfire.entity.util.IDragonFlute;
import com.github.alexthe666.iceandfire.entity.util.IDropArmor;
import com.github.alexthe666.iceandfire.entity.util.IFlyingMount;
import com.github.alexthe666.iceandfire.entity.util.ISyncMount;
import com.github.alexthe666.iceandfire.entity.util.IVillagerFear;
import com.github.alexthe666.iceandfire.enums.EnumHippogryphTypes;
import com.github.alexthe666.iceandfire.inventory.ContainerHippogryph;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.message.MessageDragonControl;
import com.github.alexthe666.iceandfire.message.MessageHippogryphArmor;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.github.alexthe666.iceandfire.pathfinding.PathNavigateFlyingCreature;
import com.github.alexthe666.iceandfire.pathfinding.raycoms.AdvancedPathNavigate;
import com.github.alexthe666.iceandfire.world.IafWorldRegistry;
import com.google.common.base.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.EnumSet;

import net.minecraft.entity.ai.goal.Goal.Flag;

public class EntityHippogryph extends TameableEntity implements ISyncMount, IAnimatedEntity, IDragonFlute, IVillagerFear, IAnimalFear, IDropArmor, IFlyingMount {

    private static final int FLIGHT_CHANCE_PER_TICK = 1200;
    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityHippogryph.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SADDLE = EntityDataManager.createKey(EntityHippogryph.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ARMOR = EntityDataManager.createKey(EntityHippogryph.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> CHESTED = EntityDataManager.createKey(EntityHippogryph.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HOVERING = EntityDataManager.createKey(EntityHippogryph.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityHippogryph.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Byte> CONTROL_STATE = EntityDataManager.createKey(EntityHippogryph.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> COMMAND = EntityDataManager.createKey(EntityHippogryph.class, DataSerializers.VARINT);
    public static Animation ANIMATION_EAT;
    public static Animation ANIMATION_SPEAK;
    public static Animation ANIMATION_SCRATCH;
    public static Animation ANIMATION_BITE;
    public Inventory hippogryphInventory;
    @OnlyIn(Dist.CLIENT)
    public IFChainBuffer roll_buffer;
    public float sitProgress;
    public float hoverProgress;
    public float flyProgress;
    public int spacebarTicks;
    public int airBorneCounter;
    public BlockPos homePos;
    public boolean hasHomePosition = false;
    public int feedings = 0;
    private boolean isLandNavigator;
    private boolean isSitting;
    private boolean isHovering;
    private boolean isFlying;
    private int animationTick;
    private Animation currentAnimation;
    private int flyTicks;
    private int hoverTicks;
    private boolean hasChestVarChanged = false;
    private int navigatorType = -1;
    private boolean isOverAir;

    public EntityHippogryph(EntityType type, World worldIn) {
        super(type, worldIn);
        this.switchNavigator(true);
        ANIMATION_EAT = Animation.create(25);
        ANIMATION_SPEAK = Animation.create(15);
        ANIMATION_SCRATCH = Animation.create(25);
        ANIMATION_BITE = Animation.create(20);
        initHippogryphInv();
        if (worldIn.isRemote) {
            roll_buffer = new IFChainBuffer();
        }
        this.stepHeight = 1;
    }

    public static int getIntFromArmor(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() != null && stack.getItem() == IafItemRegistry.IRON_HIPPOGRYPH_ARMOR) {
            return 1;
        }
        if (!stack.isEmpty() && stack.getItem() != null && stack.getItem() == IafItemRegistry.GOLD_HIPPOGRYPH_ARMOR) {
            return 2;
        }
        if (!stack.isEmpty() && stack.getItem() != null && stack.getItem() == IafItemRegistry.DIAMOND_HIPPOGRYPH_ARMOR) {
            return 3;
        }
        return 0;
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, 40.0D)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D)
                //ATTACK
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 5.0D)
                //FOLLOW RANGE
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D);
    }

    protected void switchNavigator() {
        if (this.isBeingRidden() && this.isOverAir()) {
            if (navigatorType != 1) {
                this.moveController = new IafDragonFlightManager.PlayerFlightMoveHelper(this);
                this.navigator = new PathNavigateFlyingCreature(this, world);
                navigatorType = 1;
            }
        }
        if (!this.isBeingRidden() || !this.isOverAir()) {
            if (navigatorType != 0) {
                this.moveController = new MovementController(this);
                this.navigator = new GroundPathNavigator(this, world);
                navigatorType = 0;
            }
        }
    }

    protected boolean isOverAir() {
        return isOverAir;
    }

    private boolean isOverAirLogic() {
        return world.isAirBlock(new BlockPos(this.getPosX(), this.getBoundingBox().minY - 1, this.getPosZ()));
    }

    protected int getExperiencePoints(PlayerEntity player) {
        return 10;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new DragonAIRide(this));
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new SitGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.5D, true));
        this.goalSelector.addGoal(4, new HippogryphAIMate(this, 1.0D));
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.0D, Ingredient.fromItems(Items.RABBIT, Items.COOKED_RABBIT), false));
        this.goalSelector.addGoal(6, new AIFlyRandom());
        this.goalSelector.addGoal(7, new HippogryphAIWander(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtGoal(this, LivingEntity.class, 6.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new HippogryphAITargetItems(this, false));
        this.targetSelector.addGoal(5, new HippogryphAITarget(this, LivingEntity.class, false, new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity entity) {
                return entity instanceof LivingEntity && !(entity instanceof AbstractHorseEntity) && DragonUtils.isAlive((LivingEntity) entity);
            }
        }));
        this.targetSelector.addGoal(5, new HippogryphAITarget(this, PlayerEntity.class, 350, false, new Predicate<PlayerEntity>() {
            @Override
            public boolean apply(@Nullable PlayerEntity entity) {
                return !entity.isCreative();
            }
        }));
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(VARIANT, Integer.valueOf(0));
        this.dataManager.register(ARMOR, Integer.valueOf(0));
        this.dataManager.register(SADDLE, Boolean.valueOf(false));
        this.dataManager.register(CHESTED, Boolean.valueOf(false));
        this.dataManager.register(HOVERING, Boolean.valueOf(false));
        this.dataManager.register(FLYING, Boolean.valueOf(false));
        this.dataManager.register(CONTROL_STATE, Byte.valueOf((byte) 0));
        this.dataManager.register(COMMAND, Integer.valueOf(0));

    }

    public double getYSpeedMod() {
        return 4;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        if (worldIn instanceof IServerWorld && !IafWorldRegistry.isDimensionListedForMobs((IServerWorld) world)) {
            return false;
        }
        return super.canSpawn(worldIn, spawnReasonIn);
    }

    @Override
    public boolean canPassengerSteer() {
        return false;
    }

    @Override
    public boolean canBeSteered() {
        return true;
    }

    public void updatePassenger(Entity passenger) {
        super.updatePassenger(passenger);
        if (this.isPassenger(passenger)) {
            renderYawOffset = rotationYaw;
            this.rotationYaw = passenger.rotationYaw;
        }
        passenger.setPosition(this.getPosX(), this.getPosY() + 1.05F, this.getPosZ());
    }

    private void initHippogryphInv() {
        Inventory animalchest = this.hippogryphInventory;
        this.hippogryphInventory = new Inventory(18);
        if (animalchest != null) {
            int i = Math.min(animalchest.getSizeInventory(), this.hippogryphInventory.getSizeInventory());
            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = animalchest.getStackInSlot(j);
                if (!itemstack.isEmpty()) {
                    this.hippogryphInventory.setInventorySlotContents(j, itemstack.copy());
                }
            }

            if (world.isRemote) {
                ItemStack saddle = animalchest.getStackInSlot(0);
                ItemStack chest = animalchest.getStackInSlot(1);
                IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 0, saddle != null && saddle.getItem() == Items.SADDLE && !saddle.isEmpty() ? 1 : 0));
                IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 1, chest != null && chest.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !chest.isEmpty() ? 1 : 0));
                IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 2, getIntFromArmor(animalchest.getStackInSlot(2))));
            }
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

    public boolean isBlinking() {
        return this.ticksExisted % 50 > 43;
    }

    public ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        String s = TextFormatting.getTextWithoutFormattingCodes(player.getName().getUnformattedComponentText());
        boolean isDev = s.equals("Alexthe666") || s.equals("Raptorfarian") || s.equals("tweakbsd");
        if (this.isTamed() && this.isOwner(player)) {
            if (itemstack != null && itemstack.getItem() == Items.RED_DYE && this.getEnumVariant() != EnumHippogryphTypes.ALEX && isDev) {
                this.setEnumVariant(EnumHippogryphTypes.ALEX);
                if (!player.isCreative()) {
                    itemstack.shrink(1);
                }
                this.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 1, 1);
                for (int i = 0; i < 20; i++) {
                    this.world.addParticle(ParticleTypes.CLOUD, this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getPosY() + (double) (this.rand.nextFloat() * this.getHeight()), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), 0, 0, 0);
                }
                return ActionResultType.SUCCESS;
            }
            if (itemstack != null && itemstack.getItem() == Items.LIGHT_GRAY_DYE && this.getEnumVariant() != EnumHippogryphTypes.RAPTOR && isDev) {
                this.setEnumVariant(EnumHippogryphTypes.RAPTOR);
                if (!player.isCreative()) {
                    itemstack.shrink(1);
                }
                this.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 1, 1);
                for (int i = 0; i < 20; i++) {
                    this.world.addParticle(ParticleTypes.CLOUD, this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getPosY() + (double) (this.rand.nextFloat() * this.getHeight()), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), 0, 0, 0);
                }
                return ActionResultType.SUCCESS;
            }
            if (itemstack != null && itemstack.getItem() == Items.RABBIT_STEW && this.getGrowingAge() == 0 && !isInLove()) {
                this.setInLove(player);
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1, 1);
                if (!player.isCreative()) {
                    itemstack.shrink(1);
                }
                return ActionResultType.SUCCESS;
            }
            if (itemstack != null && itemstack.getItem() == Items.STICK) {
                if (player.isSneaking()) {
                    if (this.hasHomePosition) {
                        this.hasHomePosition = false;
                        player.sendStatusMessage(new TranslationTextComponent("hippogryph.command.remove_home"), true);
                        return ActionResultType.SUCCESS;
                    } else {
                        BlockPos pos = this.getPosition();
                        this.homePos = pos;
                        this.hasHomePosition = true;
                        player.sendStatusMessage(new TranslationTextComponent("hippogryph.command.new_home", homePos.getX(), homePos.getY(), homePos.getZ()), true);
                        return ActionResultType.SUCCESS;
                    }
                } else {
                    this.setCommand(this.getCommand() + 1);
                    if (this.getCommand() > 1) {
                        this.setCommand(0);
                    }
                    player.sendStatusMessage(new TranslationTextComponent("hippogryph.command." + (this.getCommand() == 1 ? "sit" : "stand")), true);

                }
                return ActionResultType.SUCCESS;
            }
            if (itemstack != null && itemstack.getItem() == Items.GLISTERING_MELON_SLICE && this.getEnumVariant() != EnumHippogryphTypes.DODO) {
                this.setEnumVariant(EnumHippogryphTypes.DODO);
                if (!player.isCreative()) {
                    itemstack.shrink(1);
                }
                this.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 1, 1);
                for (int i = 0; i < 20; i++) {
                    this.world.addParticle(ParticleTypes.ENCHANT, this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getPosY() + (double) (this.rand.nextFloat() * this.getHeight()), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), 0, 0, 0);
                }
                return ActionResultType.SUCCESS;
            }
            if (itemstack != null && itemstack.getItem().isFood() && itemstack.getItem().getFood() != null && itemstack.getItem().getFood().isMeat() && this.getHealth() < this.getMaxHealth()) {
                this.heal(5);
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1, 1);
                for (int i = 0; i < 3; i++) {
                    this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, itemstack), this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getPosY() + (double) (this.rand.nextFloat() * this.getHeight()), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), 0, 0, 0);
                }
                if (!player.isCreative()) {
                    itemstack.shrink(1);
                }
                return ActionResultType.SUCCESS;
            }
            if (itemstack.isEmpty()) {
                if (player.isSneaking()) {
                    this.openGUI(player);
                    return ActionResultType.SUCCESS;
                } else if (this.isSaddled() && !this.isChild() && !player.isPassenger()) {
                    player.startRiding(this, true);
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return super.getEntityInteractionResult(player, hand);
    }

    public void openGUI(PlayerEntity playerEntity) {
        if (!this.world.isRemote && (!this.isBeingRidden() || this.isPassenger(playerEntity))) {
            NetworkHooks.openGui((ServerPlayerEntity) playerEntity, new INamedContainerProvider() {
                @Override
                public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
                    return new ContainerHippogryph(p_createMenu_1_, hippogryphInventory, p_createMenu_2_, EntityHippogryph.this);
                }

                @Override
                public ITextComponent getDisplayName() {
                    return new TranslationTextComponent("entity.iceandfire.hippogryph");
                }
            });
        }
        IceAndFire.PROXY.setReferencedMob(this);
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

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putBoolean("Chested", this.isChested());
        compound.putBoolean("Saddled", this.isSaddled());
        compound.putBoolean("Hovering", this.isHovering());
        compound.putBoolean("Flying", this.isFlying());
        compound.putInt("Armor", this.getArmor());
        compound.putInt("Feedings", feedings);
        if (hippogryphInventory != null) {
            ListNBT nbttaglist = new ListNBT();
            for (int i = 0; i < this.hippogryphInventory.getSizeInventory(); ++i) {
                ItemStack itemstack = this.hippogryphInventory.getStackInSlot(i);
                if (!itemstack.isEmpty()) {
                    CompoundNBT CompoundNBT = new CompoundNBT();
                    CompoundNBT.putByte("Slot", (byte) i);
                    itemstack.write(CompoundNBT);
                    nbttaglist.add(CompoundNBT);
                }
            }
            compound.put("Items", nbttaglist);
        }
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
        this.setChested(compound.getBoolean("Chested"));
        this.setSaddled(compound.getBoolean("Saddled"));
        this.setHovering(compound.getBoolean("Hovering"));
        this.setFlying(compound.getBoolean("Flying"));
        this.setArmor(compound.getInt("Armor"));
        feedings = compound.getInt("Feedings");
        if (hippogryphInventory != null) {
            ListNBT nbttaglist = compound.getList("Items", 10);
            this.initHippogryphInv();
            for (int i = 0; i < nbttaglist.size(); ++i) {
                CompoundNBT CompoundNBT = nbttaglist.getCompound(i);
                int j = CompoundNBT.getByte("Slot") & 255;
                this.hippogryphInventory.setInventorySlotContents(j, ItemStack.read(CompoundNBT));
            }
        } else {
            ListNBT nbttaglist = compound.getList("Items", 10);
            this.initHippogryphInv();
            for (int i = 0; i < nbttaglist.size(); ++i) {
                CompoundNBT CompoundNBT = nbttaglist.getCompound(i);
                int j = CompoundNBT.getByte("Slot") & 255;
                this.initHippogryphInv();
                this.hippogryphInventory.setInventorySlotContents(j, ItemStack.read(CompoundNBT));
                //this.setArmorInSlot(j, this.getIntFromArmor(ItemStack.loadItemStackFromNBT(CompoundNBT)));
                ItemStack saddle = hippogryphInventory.getStackInSlot(0);
                ItemStack chest = hippogryphInventory.getStackInSlot(1);
                if (world.isRemote) {
                    IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 0, saddle != null && saddle.getItem() == Items.SADDLE && !saddle.isEmpty() ? 1 : 0));
                    IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 1, chest != null && chest.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !chest.isEmpty() ? 1 : 0));
                    IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 2, getIntFromArmor(hippogryphInventory.getStackInSlot(2))));
                }
            }
        }
        this.hasHomePosition = compound.getBoolean("HasHomePosition");
        if (hasHomePosition && compound.getInt("HomeAreaX") != 0 && compound.getInt("HomeAreaY") != 0 && compound.getInt("HomeAreaZ") != 0) {
            homePos = new BlockPos(compound.getInt("HomeAreaX"), compound.getInt("HomeAreaY"), compound.getInt("HomeAreaZ"));
        }
        this.setCommand(compound.getInt("Command"));

        if (this.isQueuedToSit()) {
            this.sitProgress = 20.0F;
        }
    }

    public int getVariant() {
        return Integer.valueOf(this.dataManager.get(VARIANT).intValue());
    }

    public void setVariant(int variant) {
        this.dataManager.set(VARIANT, Integer.valueOf(variant));
    }

    public EnumHippogryphTypes getEnumVariant() {
        return EnumHippogryphTypes.values()[this.getVariant()];
    }

    public void setEnumVariant(EnumHippogryphTypes variant) {
        this.setVariant(variant.ordinal());
    }

    public boolean isSaddled() {
        return Boolean.valueOf(this.dataManager.get(SADDLE).booleanValue());
    }

    public void setSaddled(boolean saddle) {
        this.dataManager.set(SADDLE, Boolean.valueOf(saddle));
    }

    public boolean isChested() {
        return Boolean.valueOf(this.dataManager.get(CHESTED).booleanValue());
    }

    public void setChested(boolean chested) {
        this.dataManager.set(CHESTED, Boolean.valueOf(chested));
        this.hasChestVarChanged = true;
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

    public boolean isHovering() {
        if (world.isRemote) {
            return this.isHovering = Boolean.valueOf(this.dataManager.get(HOVERING).booleanValue());
        }
        return isHovering;
    }

    public void setHovering(boolean hovering) {
        this.dataManager.set(HOVERING, Boolean.valueOf(hovering));
        if (!world.isRemote) {
            this.isHovering = Boolean.valueOf(hovering);
        }
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
    public double getFlightSpeedModifier() {
        return IafConfig.hippogryphFlightSpeedMod * 0.9F;
    }

    public boolean isFlying() {
        if (world.isRemote) {
            return this.isFlying = Boolean.valueOf(this.dataManager.get(FLYING).booleanValue());
        }
        return isFlying;
    }

    public void setFlying(boolean flying) {
        this.dataManager.set(FLYING, Boolean.valueOf(flying));
        if (!world.isRemote) {
            this.isFlying = Boolean.valueOf(flying);
        }
    }

    public int getArmor() {
        return Integer.valueOf(this.dataManager.get(ARMOR).intValue());
    }

    public void setArmor(int armorType) {
        this.dataManager.set(ARMOR, Integer.valueOf(armorType));
        double armorValue = 0;
        switch (armorType) {
            case 1:
                armorValue = 10;
                break;
            case 2:
                armorValue = 20;
                break;
            case 3:
                armorValue = 30;
        }
        this.getAttribute(Attributes.ARMOR).setBaseValue(armorValue);
    }

    public boolean canMove() {
        return !this.isQueuedToSit() && this.getControllingPassenger() == null && sitProgress == 0;
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        ILivingEntityData data = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setEnumVariant(EnumHippogryphTypes.getBiomeType(worldIn.getBiome(this.getPosition())));
        return data;
    }

    @Override
    public boolean attackEntityFrom(DamageSource dmg, float i) {
        if (this.isBeingRidden() && dmg.getTrueSource() != null && this.getControllingPassenger() != null && dmg.getTrueSource() == this.getControllingPassenger()) {
            return false;
        }
        return super.attackEntityFrom(dmg, i);
    }

    @Nullable
    @Override
    public AgeableEntity createChild(ServerWorld serverWorld, AgeableEntity ageable) {
        return null;
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

    @Nullable
    protected SoundEvent getAmbientSound() {
        return IafSoundRegistry.HIPPOGRYPH_IDLE;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return IafSoundRegistry.HIPPOGRYPH_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return IafSoundRegistry.HIPPOGRYPH_DIE;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{IAnimatedEntity.NO_ANIMATION, EntityHippogryph.ANIMATION_EAT, EntityHippogryph.ANIMATION_BITE, EntityHippogryph.ANIMATION_SPEAK, EntityHippogryph.ANIMATION_SCRATCH};
    }

    public boolean shouldDismountInWater(Entity rider) {
        return true;
    }

    public boolean isDirectPathBetweenPoints(Vector3d vec1, Vector3d vec2) {
        RayTraceResult movingobjectposition = this.world.rayTraceBlocks(new RayTraceContext(vec1, new Vector3d(vec2.x, vec2.y + (double) this.getHeight() * 0.5D, vec2.z), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));

        return movingobjectposition == null || movingobjectposition.getType() != RayTraceResult.Type.BLOCK;
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

    @Override
    public void travel(Vector3d move) {
        if (!this.canMove() && !this.isBeingRidden()) {
            super.travel(Vector3d.ZERO);
            return;
        }
        super.travel(move);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.getAnimation() != ANIMATION_SCRATCH && this.getAnimation() != ANIMATION_BITE) {
            this.setAnimation(this.getRNG().nextBoolean() ? ANIMATION_SCRATCH : ANIMATION_BITE);
        } else {
            return true;
        }
        return false;
    }

    public ItemEntity createEgg(EntityHippogryph partner) {
        int i = MathHelper.floor(this.getPosX());
        int j = MathHelper.floor(this.getPosY());
        int k = MathHelper.floor(this.getPosZ());
        ItemStack stack = new ItemStack(IafItemRegistry.HIPPOGRYPH_EGG);
        ItemEntity egg = new ItemEntity(this.world, i, j, k, stack);
        return egg;
    }

    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    public void livingTick() {
        super.livingTick();
        //switchNavigator();
        if (world.getDifficulty() == Difficulty.PEACEFUL && this.getAttackTarget() instanceof PlayerEntity) {
            this.setAttackTarget(null);
        }
        if (!this.world.isRemote) {
            if (this.isQueuedToSit() && (this.getCommand() != 1 || this.getControllingPassenger() != null)) {
                this.setSitting(false);
            }
            if (!this.isQueuedToSit() && this.getCommand() == 1 && this.getControllingPassenger() == null) {
                this.setSitting(true);
            }
            if (this.isQueuedToSit()) {
                this.getNavigator().clearPath();
            }
            if (this.rand.nextInt(900) == 0 && this.deathTime == 0) {
                this.heal(1.0F);
            }
        }
        if (this.getAnimation() == ANIMATION_BITE && this.getAttackTarget() != null && this.getAnimationTick() == 6) {
            double dist = this.getDistanceSq(this.getAttackTarget());
            if (dist < 8) {
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
            }
        }
        LivingEntity attackTarget = this.getAttackTarget();
        if (this.getAnimation() == ANIMATION_SCRATCH && attackTarget != null && this.getAnimationTick() == 6) {
            double dist = this.getDistanceSq(this.getAttackTarget());

            if (dist < 8) {
                this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
                this.getAttackTarget().isAirBorne = true;
                float f = MathHelper.sqrt(0.5 * 0.5 + 0.5 * 0.5);
                attackTarget.setMotion(attackTarget.getMotion().add(-0.5 / (double) f, 1, -0.5 / (double) f));
                attackTarget.setMotion(attackTarget.getMotion().mul(0.5D, 1, 0.5D));

                if (this.getAttackTarget().isOnGround()) {
                    attackTarget.setMotion(attackTarget.getMotion().add(0, 0.3, 0));
                }
            }
        }
        if (!world.isRemote && !this.isOverAir() && this.getNavigator().noPath() && this.getAttackTarget() != null && this.getAttackTarget().getPosY() - 3 > this.getPosY() && this.getRNG().nextInt(15) == 0 && this.canMove() && !this.isHovering() && !this.isFlying()) {
            this.setHovering(true);
            this.hoverTicks = 0;
            this.flyTicks = 0;
        }
        if (this.isOverAir()) {
            airBorneCounter++;
        } else {
            airBorneCounter = 0;
        }
        if (hasChestVarChanged && hippogryphInventory != null && !this.isChested()) {
            for (int i = 3; i < 18; i++) {
                if (!hippogryphInventory.getStackInSlot(i).isEmpty()) {
                    if (!world.isRemote) {
                        this.entityDropItem(hippogryphInventory.getStackInSlot(i), 1);
                    }
                    hippogryphInventory.removeStackFromSlot(i);
                }
            }
            hasChestVarChanged = false;
        }
        if (this.isFlying() && this.ticksExisted % 40 == 0 || this.isFlying() && this.isQueuedToSit()) {
            this.setFlying(true);
        }
        if (!this.canMove() && this.getAttackTarget() != null) {
            this.setAttackTarget(null);
        }
        if (!this.canMove()) {
            this.getNavigator().clearPath();

        }
        AnimationHandler.INSTANCE.updateAnimations(this);
        boolean sitting = isQueuedToSit() && !isHovering() && !isFlying();
        if (sitting && sitProgress < 20.0F) {
            sitProgress += 0.5F;
        } else if (!sitting && sitProgress > 0.0F) {
            sitProgress -= 0.5F;
        }

        boolean hovering = isHovering();
        if (hovering && hoverProgress < 20.0F) {
            hoverProgress += 0.5F;
        } else if (!hovering && hoverProgress > 0.0F) {
            hoverProgress -= 0.5F;
        }
        boolean flying = this.isFlying() || this.isHovering() && airBorneCounter > 10;
        if (flying && flyProgress < 20.0F) {
            flyProgress += 0.5F;
        } else if (!flying && flyProgress > 0.0F) {
            flyProgress -= 0.5F;
        }
        if (flying && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (!flying && !this.isLandNavigator) {
            switchNavigator(true);
        }
        if ((flying || hovering) && !doesWantToLand()) {
            double up = isInWater() ? 0.16D : 0.08D;
            this.setMotion(this.getMotion().add(0, up, 0));
        }
        if ((flying || hovering) && ticksExisted % 20 == 0 && this.isOverAir()) {
            this.playSound(SoundEvents.ENTITY_ENDER_DRAGON_FLAP, this.getSoundVolume() * (IafConfig.dragonFlapNoiseDistance / 2), 0.6F + this.rand.nextFloat() * 0.6F * this.getSoundPitch());
        }
        if (this.isOnGround() && this.doesWantToLand() && (this.isFlying() || this.isHovering())) {
            this.setFlying(false);
            this.setHovering(false);
        }
        if (this.isHovering()) {
            if (this.isQueuedToSit()) {
                this.setHovering(false);
            }
            this.hoverTicks++;
            if (this.doesWantToLand()) {
                this.setMotion(this.getMotion().add(0, -0.25D, 0));
            } else {
                if (this.getControllingPassenger() == null) {
                    this.setMotion(this.getMotion().add(0, 0.08D, 0));
                }
                if (this.hoverTicks > 40) {
                    if (!this.isChild()) {
                        this.setFlying(true);
                    }
                    this.setHovering(false);
                    this.hoverTicks = 0;
                    this.flyTicks = 0;
                }
            }
        }
        if (this.isQueuedToSit()) {
            this.getNavigator().clearPath();
        }
        if (this.isOnGround() && flyTicks != 0) {
            flyTicks = 0;
        }
        if (this.isFlying() && this.doesWantToLand() && this.getControllingPassenger() == null) {
            this.setHovering(false);
            if (this.isOnGround()) {
                flyTicks = 0;
            }
            this.setFlying(false);
        }
        if (this.isFlying()) {
            this.flyTicks++;
        }
        if ((this.isHovering() || this.isFlying()) && this.isQueuedToSit()) {
            this.setFlying(false);
            this.setHovering(false);
        }
        if(this.isBeingRidden() && this.isGoingDown() && this.isOnGround()){
            this.setHovering(false);
            this.setFlying(false);
        }
        if ((!world.isRemote && this.getRNG().nextInt(FLIGHT_CHANCE_PER_TICK) == 0 && !this.isQueuedToSit() && !this.isFlying() && this.getPassengers().isEmpty() && !this.isChild() && !this.isHovering() && !this.isQueuedToSit() && this.canMove() && !this.isOverAir() || this.getPosY() < -1)) {
            this.setHovering(true);
            this.hoverTicks = 0;
            this.flyTicks = 0;
        }
        if (getAttackTarget() != null && !this.getPassengers().isEmpty() && this.getOwner() != null && this.getPassengers().contains(this.getOwner())) {
            this.setAttackTarget(null);
        }
    }

    public boolean doesWantToLand() {
        return (this.flyTicks > 200 || flyTicks > 40 && this.flyProgress == 0) && !this.isBeingRidden();
    }

    @Override
    public void tick() {
        super.tick();
        isOverAir = this.isOverAirLogic();
        if (world.isRemote) {
            this.updateClientControls();
        }
        if (this.isGoingUp()) {
            if (this.airBorneCounter == 0) {
                this.setMotion(this.getMotion().add(0, 0.4F, 0));
            }
            if (!this.isFlying() && !this.isHovering()) {
                this.spacebarTicks += 2;
            }
        } else if (this.dismountIAF()) {
            if (this.isFlying() || this.isHovering()) {
                this.setFlying(false);
                this.setHovering(false);
            }
        }
        if (this.attack() && this.getControllingPassenger() != null && this.getControllingPassenger() instanceof PlayerEntity) {

            LivingEntity target = DragonUtils.riderLookingAtEntity(this, (PlayerEntity) this.getControllingPassenger(), 3);
            if (this.getAnimation() != ANIMATION_BITE && this.getAnimation() != ANIMATION_SCRATCH) {
                this.setAnimation(this.getRNG().nextBoolean() ? ANIMATION_SCRATCH : ANIMATION_BITE);
            }
            if (target != null && this.getAnimationTick() >= 10 && this.getAnimationTick() < 13) {
                target.attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue()));
            }
        }
        if (this.getControllingPassenger() != null && this.getControllingPassenger().isSneaking()) {
            this.getControllingPassenger().stopRiding();
        }
        
        double motion = this.getMotion().x*this.getMotion().x+this.getMotion().z*this.getMotion().z;//Use squared norm2

        if (this.isFlying() && !this.isHovering() && this.getControllingPassenger() != null && this.isOverAir() &&  motion < 0.01F) {
            this.setHovering(true);
            this.setFlying(false);
        }
        if (this.isHovering() && !this.isFlying() && this.getControllingPassenger() != null && this.isOverAir() && motion > 0.01F) {
            this.setFlying(true);
            this.setHovering(false);
        }
        if (this.spacebarTicks > 0) {
            this.spacebarTicks--;
        }
        if (this.spacebarTicks > 10 && this.getOwner() != null && this.getPassengers().contains(this.getOwner()) && !this.isFlying() && !this.isHovering()) {
            this.setHovering(true);
        }
        if (world.isRemote) {
            roll_buffer.calculateChainFlapBuffer(35, 8, 6, this);
        }
        if (this.getAttackTarget() != null && this.getRidingEntity() == null && !this.getAttackTarget().isAlive() || this.getAttackTarget() != null && this.getAttackTarget() instanceof EntityDragonBase && !this.getAttackTarget().isAlive()) {
            this.setAttackTarget(null);
        }
    }


    public boolean isTargetBlocked(Vector3d target) {
        if (target != null) {
            RayTraceResult rayTrace = this.world.rayTraceBlocks(new RayTraceContext(this.getEyePosition(1.0F), target, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));

            if (rayTrace != null && rayTrace.getHitVec() != null) {
                BlockPos pos = new BlockPos(rayTrace.getHitVec());
                return !world.isAirBlock(pos);
            }
        }
        return false;
    }

    public float getDistanceSquared(Vector3d Vector3d) {
        float f = (float) (this.getPosX() - Vector3d.x);
        float f1 = (float) (this.getPosY() - Vector3d.y);
        float f2 = (float) (this.getPosZ() - Vector3d.z);
        return f * f + f1 * f1 + f2 * f2;
    }

    public boolean replaceItemInInventory(int inventorySlot, @Nullable ItemStack itemStackIn) {
        int j = inventorySlot - 500 + 2;
        if (j >= 0 && j < this.hippogryphInventory.getSizeInventory()) {
            this.hippogryphInventory.setInventorySlotContents(j, itemStackIn);
            return true;
        } else {
            return false;
        }
    }

    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (hippogryphInventory != null && !this.world.isRemote) {
            for (int i = 0; i < hippogryphInventory.getSizeInventory(); ++i) {
                ItemStack itemstack = hippogryphInventory.getStackInSlot(i);
                if (!itemstack.isEmpty()) {
                    this.entityDropItem(itemstack, 0.0F);
                }
            }
        }
    }

    public void refreshInventory() {
        //This isn't needed (anymore) since it's already being handled by minecraft
        if (!this.world.isRemote) {
            ItemStack saddle = this.hippogryphInventory.getStackInSlot(0);
            ItemStack chest = this.hippogryphInventory.getStackInSlot(1);
            this.setSaddled(saddle != null && saddle.getItem() == Items.SADDLE && !saddle.isEmpty());
            this.setChested(chest != null && chest.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !chest.isEmpty());
            this.setArmor(getIntFromArmor(this.hippogryphInventory.getStackInSlot(2)));
        }
        /*if (this.world.isRemote) {
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 0, saddle != null && saddle.getItem() == Items.SADDLE && !saddle.isEmpty() ? 1 : 0));
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 1, chest != null && chest.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !chest.isEmpty() ? 1 : 0));
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 2, this.getIntFromArmor(this.hippogryphInventory.getStackInSlot(2))));
        }*/

    }

    protected void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveController = new MovementController(this);
            this.navigator = createNavigator(world, AdvancedPathNavigate.MovementType.CLIMBING);
            this.isLandNavigator = true;
        } else {
            this.moveController = new EntityHippogryph.FlyMoveHelper(this);
            this.navigator = createNavigator(world, AdvancedPathNavigate.MovementType.FLYING);
            this.isLandNavigator = false;
        }
    }

    protected PathNavigator createNavigator(World worldIn) {
        return createNavigator(worldIn, AdvancedPathNavigate.MovementType.CLIMBING);
    }

    protected PathNavigator createNavigator(World worldIn, AdvancedPathNavigate.MovementType type) {
        return createNavigator(worldIn, type, 2, 2);
    }

    protected PathNavigator createNavigator(World worldIn, AdvancedPathNavigate.MovementType type, float width, float height) {
        AdvancedPathNavigate newNavigator = new AdvancedPathNavigate(this, world, type, width, height);
        this.navigator = newNavigator;
        newNavigator.setCanSwim(true);
        newNavigator.getNodeProcessor().setCanOpenDoors(true);
        return newNavigator;
    }

    @Override
    public void onHearFlute(PlayerEntity player) {
        if (this.isTamed() && this.isOwner(player)) {
            if (this.isFlying() || this.isHovering()) {
                this.setFlying(false);
                this.setHovering(false);
            }
        }
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return DragonUtils.canTameDragonAttack(this, entity);
    }

    public void dropArmor() {
        if (hippogryphInventory != null && !this.world.isRemote) {
            for (int i = 0; i < hippogryphInventory.getSizeInventory(); ++i) {
                ItemStack itemstack = hippogryphInventory.getStackInSlot(i);
                if (!itemstack.isEmpty()) {
                    this.entityDropItem(itemstack, 0.0F);
                }
            }
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

    class FlyMoveHelper extends MovementController {
        public FlyMoveHelper(EntityHippogryph pixie) {
            super(pixie);
            this.speed = 1.75F;
        }

        public void tick() {
            if (this.action == MovementController.Action.MOVE_TO) {
                if (EntityHippogryph.this.collidedHorizontally) {
                    EntityHippogryph.this.rotationYaw += 180.0F;
                    BlockPos target = DragonUtils.getBlockInViewHippogryph(EntityHippogryph.this, 180);
                    this.speed = 0.1F;
                    if (target != null) {
                        this.posX = target.getX() + 0.5F;
                        this.posY = target.getY() + 0.5F;
                        this.posZ = target.getZ() + 0.5F;
                    }
                }
                double d0 = this.posX - EntityHippogryph.this.getPosX();
                double d1 = this.posY - EntityHippogryph.this.getPosY();
                double d2 = this.posZ - EntityHippogryph.this.getPosZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                d3 = MathHelper.sqrt(d3);

                if (d3 < EntityHippogryph.this.getBoundingBox().getAverageEdgeLength()) {
                    this.action = MovementController.Action.WAIT;
                    EntityHippogryph.this.setMotion(EntityHippogryph.this.getMotion().mul(0.5D, 0.5D, 0.5D));
                } else {
                    EntityHippogryph.this.setMotion(EntityHippogryph.this.getMotion().add(d0 / d3 * 0.1D * this.speed, d1 / d3 * 0.1D * this.speed, d2 / d3 * 0.1D * this.speed));

                    if (EntityHippogryph.this.getAttackTarget() == null) {
                        EntityHippogryph.this.rotationYaw = -((float) MathHelper.atan2(EntityHippogryph.this.getMotion().x, EntityHippogryph.this.getMotion().z)) * (180F / (float) Math.PI);
                        EntityHippogryph.this.renderYawOffset = EntityHippogryph.this.rotationYaw;
                    } else {
                        double d4 = EntityHippogryph.this.getAttackTarget().getPosX() - EntityHippogryph.this.getPosX();
                        double d5 = EntityHippogryph.this.getAttackTarget().getPosZ() - EntityHippogryph.this.getPosZ();
                        EntityHippogryph.this.rotationYaw = -((float) MathHelper.atan2(d4, d5)) * (180F / (float) Math.PI);
                        EntityHippogryph.this.renderYawOffset = EntityHippogryph.this.rotationYaw;
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
            return EntityHippogryph.this.isFlying() && !EntityHippogryph.this.isBeingRidden();
        }

        public boolean shouldContinueExecuting() {
            return shouldExecute();
        }

        public void tick() {
            if (target == null || !isDirectPathBetweenPoints(EntityHippogryph.this.getPositionVec(), Vector3d.copyCentered(target)) || EntityHippogryph.this.getDistanceSquared(Vector3d.copyCentered(target)) < 9) {
                if (EntityHippogryph.this.getAttackTarget() != null) {
                    target = EntityHippogryph.this.getAttackTarget().getPosition();
                } else {
                    target = DragonUtils.getBlockInViewHippogryph(EntityHippogryph.this, 0);
                    if (EntityHippogryph.this.doesWantToLand()) {
                        while (target != null && target.getY() > 3 && EntityHippogryph.this.world.isAirBlock(target)) {
                            target = target.down();
                        }
                    }
                }
            }

            if (target != null && (EntityHippogryph.this.doesWantToLand() || EntityHippogryph.this.world.isAirBlock(target))) {
                EntityHippogryph.this.getMoveHelper().setMoveTo((double) target.getX() + 0.5D, (double) target.getY() + 0.5D, (double) target.getZ() + 0.5D, 0.75D);
                if (EntityHippogryph.this.getAttackTarget() == null) {
                    EntityHippogryph.this.getLookController().setLookPosition((double) target.getX() + 0.5D, (double) target.getY() + 0.5D, (double) target.getZ() + 0.5D, 180.0F, 20.0F);
                }
            }
        }
    }

}
