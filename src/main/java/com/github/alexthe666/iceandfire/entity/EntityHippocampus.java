package com.github.alexthe666.iceandfire.entity;

import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.IafKeybindRegistry;
import com.github.alexthe666.iceandfire.entity.ai.AquaticAIFindWaterTarget;
import com.github.alexthe666.iceandfire.entity.ai.AquaticAIGetInWater;
import com.github.alexthe666.iceandfire.entity.ai.AquaticAITempt;
import com.github.alexthe666.iceandfire.entity.ai.HippocampusAIRide;
import com.github.alexthe666.iceandfire.entity.ai.HippocampusAIWander;
import com.github.alexthe666.iceandfire.entity.util.ChainBuffer;
import com.github.alexthe666.iceandfire.entity.util.IDropArmor;
import com.github.alexthe666.iceandfire.entity.util.ISyncMount;
import com.github.alexthe666.iceandfire.inventory.ContainerHippocampus;
import com.github.alexthe666.iceandfire.message.MessageDragonControl;
import com.github.alexthe666.iceandfire.message.MessageHippogryphArmor;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.github.alexthe666.iceandfire.pathfinding.PathNavigateAmphibious;

import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityHippocampus extends TameableEntity implements ISyncMount, IAnimatedEntity, IDropArmor {

    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityHippocampus.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SADDLE = EntityDataManager.createKey(EntityHippocampus.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ARMOR = EntityDataManager.createKey(EntityHippocampus.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> CHESTED = EntityDataManager.createKey(EntityHippocampus.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Byte> CONTROL_STATE = EntityDataManager.createKey(EntityHippocampus.class, DataSerializers.BYTE);
    public static Animation ANIMATION_SPEAK;
    public float onLandProgress;
    @OnlyIn(Dist.CLIENT)
    public ChainBuffer tail_buffer;
    public Inventory hippocampusInventory;
    public float sitProgress;
    public int airBorneCounter;
    private int animationTick;
    private Animation currentAnimation;
    private boolean isLandNavigator;
    private boolean isSitting;
    private boolean hasChestVarChanged = false;

    public EntityHippocampus(EntityType t, World worldIn) {
        super(t, worldIn);
        this.stepHeight = 1;
        ANIMATION_SPEAK = Animation.create(15);
        this.switchNavigator(true);
        if (worldIn.isRemote) {
            tail_buffer = new ChainBuffer();
        }
        initHippocampusInv();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new HippocampusAIRide(this));
        this.goalSelector.addGoal(0, new AquaticAITempt(this, 1.0D, Items.KELP, false));
        this.goalSelector.addGoal(0, new AquaticAITempt(this, 1.0D, Items.PRISMARINE_CRYSTALS, false));
        this.goalSelector.addGoal(1, new AquaticAIFindWaterTarget(this, 10, true));
        this.goalSelector.addGoal(2, new AquaticAIGetInWater(this, 1.0D));
        this.goalSelector.addGoal(3, new HippocampusAIWander(this, 1));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));

    }

    protected int getExperiencePoints(PlayerEntity player) {
        return 2;
    }

    public float getBlockPathWeight(BlockPos pos) {
        return this.world.getBlockState(pos.down()).getMaterial() == Material.WATER ? 10.0F : this.world.getLight(pos) - 0.5F;
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.WATER;
    }

    public boolean isPushedByWater() {
        return false;
    }


    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveController = new MovementController(this);
            this.navigator = new PathNavigateAmphibious(this, world);
            this.isLandNavigator = true;
        } else {
            this.moveController = new EntityHippocampus.SwimmingMoveHelper();
            this.navigator = new SwimmerPathNavigator(this, world);
            this.isLandNavigator = false;
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(VARIANT, Integer.valueOf(0));
        this.dataManager.register(ARMOR, Integer.valueOf(0));
        this.dataManager.register(SADDLE, Boolean.valueOf(false));
        this.dataManager.register(CHESTED, Boolean.valueOf(false));
        this.dataManager.register(CONTROL_STATE, Byte.valueOf((byte) 0));
    }

    private void initHippocampusInv() {
        Inventory animalchest = this.hippocampusInventory;
        this.hippocampusInventory = new Inventory(18);
        if (animalchest != null) {
            int i = Math.min(animalchest.getSizeInventory(), this.hippocampusInventory.getSizeInventory());
            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = animalchest.getStackInSlot(j);
                if (!itemstack.isEmpty()) {
                    this.hippocampusInventory.setInventorySlotContents(j, itemstack.copy());
                }
            }

            if (world.isRemote) {
                ItemStack saddle = animalchest.getStackInSlot(0);
                ItemStack chest = animalchest.getStackInSlot(1);
                IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 0, saddle != null && saddle.getItem() == Items.SADDLE && !saddle.isEmpty() ? 1 : 0));
                IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 1, chest != null && chest.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !chest.isEmpty() ? 1 : 0));
                IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 2, this.getIntFromArmor(animalchest.getStackInSlot(2))));
            }
        }
    }

    @Nullable
    public Entity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof PlayerEntity && this.getAttackTarget() != passenger) {
                PlayerEntity player = (PlayerEntity) passenger;
                return player;
            }
        }
        return null;
    }

    public static int getIntFromArmor(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() != null && stack.getItem() == Items.IRON_HORSE_ARMOR) {
            return 1;
        }
        if (!stack.isEmpty() && stack.getItem() != null && stack.getItem() == Items.GOLDEN_HORSE_ARMOR) {
            return 2;
        }
        if (!stack.isEmpty() && stack.getItem() != null && stack.getItem() == Items.DIAMOND_HORSE_ARMOR) {
            return 3;
        }
        return 0;
    }

    public boolean replaceItemInInventory(int inventorySlot, @Nullable ItemStack itemStackIn) {
        int j = inventorySlot - 500 + 2;
        if (j >= 0 && j < this.hippocampusInventory.getSizeInventory()) {
            this.hippocampusInventory.setInventorySlotContents(j, itemStackIn);
            return true;
        } else {
            return false;
        }
    }

    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (hippocampusInventory != null && !this.world.isRemote) {
            for (int i = 0; i < hippocampusInventory.getSizeInventory(); ++i) {
                ItemStack itemstack = hippocampusInventory.getStackInSlot(i);
                if (!itemstack.isEmpty()) {
                    this.entityDropItem(itemstack, 0.0F);
                }
            }
        }
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
        dataManager.set(CONTROL_STATE, state);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, 40.0D)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D)
                //ATTACK
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
    }
    
    @Override
    public boolean canBeRiddenInWater(Entity rider)
    {
        return true;
    }
    public void updatePassenger(Entity passenger) {
        super.updatePassenger(passenger);
        if (this.isPassenger(passenger)) {
            renderYawOffset = rotationYaw;
            this.rotationYaw = passenger.rotationYaw;
        }
        double ymod1 = this.onLandProgress * -0.02;
        passenger.setPosition(this.getPosX(), this.getPosY() + 0.6F + ymod1, this.getPosZ());
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (!this.world.isRemote) {
            if (this.rand.nextInt(900) == 0 && this.deathTime == 0) {
                this.heal(1.0F);
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
        if (getControllingPassenger() != null && getControllingPassenger() instanceof LivingEntity && this.ticksExisted % 20 == 0) {
            ((LivingEntity) getControllingPassenger()).addPotionEffect(new EffectInstance(Effects.WATER_BREATHING, 30, 0, true, false));
        }
        if (!this.onGround) {
            airBorneCounter++;
        } else {
            airBorneCounter = 0;
        }
        if (world.isRemote) {
            this.updateClientControls();
        }
        if (world.isRemote) {
            tail_buffer.calculateChainSwingBuffer(40, 10, 1F, this);
        }
        if (this.up()) {
            if (!this.isInWater() && this.airBorneCounter == 0 && this.onGround) {
                this.jump();
            } else if (this.isInWater()) {
                this.setMotion(this.getMotion().add(0, 0.4D, 0));
            }
        }
        if (this.down()) {
            this.setMotion(this.getMotion().add(0, -0.4D, 0));
        }
        if (this.isInWater() && this.isLandNavigator) {
            switchNavigator(false);
        }
        if (!this.isInWater() && !this.isLandNavigator) {
            switchNavigator(true);
        }
        boolean inWater = !this.isInWater();
        if (inWater && onLandProgress < 20.0F) {
            onLandProgress += 1F;
        } else if (!inWater && onLandProgress > 0.0F) {
            onLandProgress -= 1F;
        }
        boolean sitting = isQueuedToSit();
        if (sitting && sitProgress < 20.0F) {
            sitProgress += 0.5F;
        } else if (!sitting && sitProgress > 0.0F) {
            sitProgress -= 0.5F;
        }
        if (hasChestVarChanged && hippocampusInventory != null && !this.isChested()) {
            for (int i = 3; i < 18; i++) {
                if (!hippocampusInventory.getStackInSlot(i).isEmpty()) {
                    if (!world.isRemote) {
                        this.entityDropItem(hippocampusInventory.getStackInSlot(i), 1);
                    }
                    hippocampusInventory.removeStackFromSlot(i);
                }
            }
            hasChestVarChanged = false;
        }
    }

    public boolean up() {
        return (Byte.valueOf(dataManager.get(CONTROL_STATE).byteValue()) & 1) == 1;
    }

    public boolean down() {
        return (Byte.valueOf(dataManager.get(CONTROL_STATE).byteValue()) >> 1 & 1) == 1;
    }

    public boolean dismountIAF() {
        return (Byte.valueOf(dataManager.get(CONTROL_STATE).byteValue()) >> 2 & 1) == 1;
    }


    public boolean isBlinking() {
        return this.ticksExisted % 50 > 43;
    }

    public boolean getCanSpawnHere() {
        return this.getPosY() > 30 && this.getPosY() < (double) this.world.getSeaLevel();
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putBoolean("Chested", this.isChested());
        compound.putBoolean("Saddled", this.isSaddled());
        compound.putInt("Armor", this.getArmor());
        if (hippocampusInventory != null) {
            ListNBT nbttaglist = new ListNBT();
            for (int i = 0; i < this.hippocampusInventory.getSizeInventory(); ++i) {
                ItemStack itemstack = this.hippocampusInventory.getStackInSlot(i);
                if (!itemstack.isEmpty()) {
                    CompoundNBT CompoundNBT = new CompoundNBT();
                    CompoundNBT.putByte("Slot", (byte) i);
                    itemstack.write(CompoundNBT);
                    nbttaglist.add(CompoundNBT);
                }
            }
            compound.put("Items", nbttaglist);
        }

    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setVariant(compound.getInt("Variant"));
        this.setChested(compound.getBoolean("Chested"));
        this.setSaddled(compound.getBoolean("Saddled"));
        this.setArmor(compound.getInt("Armor"));
        if (hippocampusInventory != null) {
            ListNBT nbttaglist = compound.getList("Items", 10);
            this.initHippocampusInv();
            for (int i = 0; i < nbttaglist.size(); ++i) {
                CompoundNBT CompoundNBT = nbttaglist.getCompound(i);
                int j = CompoundNBT.getByte("Slot") & 255;
                this.hippocampusInventory.setInventorySlotContents(j, ItemStack.read(CompoundNBT));
            }
        } else {
            ListNBT nbttaglist = compound.getList("Items", 10);
            this.initHippocampusInv();
            for (int i = 0; i < nbttaglist.size(); ++i) {
                CompoundNBT CompoundNBT = nbttaglist.getCompound(i);
                int j = CompoundNBT.getByte("Slot") & 255;
                this.initHippocampusInv();
                this.hippocampusInventory.setInventorySlotContents(j, ItemStack.read(CompoundNBT));
                //this.setArmorInSlot(j, this.getIntFromArmor(ItemStack.loadItemStackFromNBT(CompoundNBT)));
                ItemStack saddle = hippocampusInventory.getStackInSlot(0);
                ItemStack chest = hippocampusInventory.getStackInSlot(1);
                if (world.isRemote) {
                    IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 0, saddle != null && saddle.getItem() == Items.SADDLE && !saddle.isEmpty() ? 1 : 0));
                    IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 1, chest != null && chest.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !chest.isEmpty() ? 1 : 0));
                    IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 2, this.getIntFromArmor(hippocampusInventory.getStackInSlot(2))));
                }
            }
        }
    }


    public boolean isSaddled() {
        return this.dataManager.get(SADDLE).booleanValue();
    }

    public void setSaddled(boolean saddle) {
        this.dataManager.set(SADDLE, saddle);
    }

    public boolean isChested() {
        return this.dataManager.get(CHESTED).booleanValue();
    }

    public void setChested(boolean chested) {
        this.dataManager.set(CHESTED, chested);
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

    public int getArmor() {
        return this.dataManager.get(ARMOR).intValue();
    }

    public void setArmor(int armorType) {
        this.dataManager.set(ARMOR, armorType);
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

    public int getVariant() {
        return this.dataManager.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.dataManager.set(VARIANT, variant);
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        ILivingEntityData data = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setVariant(this.getRNG().nextInt(6));
        return data;
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
        return new Animation[]{IAnimatedEntity.NO_ANIMATION, ANIMATION_SPEAK};
    }

    @Nullable
    @Override
    public AgeableEntity createChild(ServerWorld serverWorld, AgeableEntity ageable) {
        if (ageable instanceof EntityHippocampus) {
            EntityHippocampus hippo = new EntityHippocampus(IafEntityRegistry.HIPPOCAMPUS, this.world);
            hippo.setVariant(this.getRNG().nextBoolean() ? this.getVariant() : ((EntityHippocampus) ageable).getVariant());
            return hippo;
        }
        return null;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isInWater() {
        return super.isInWater();
    }

    @Override
    public void travel(Vector3d vec) {
        float f4;
        if (this.isQueuedToSit()) {
            super.travel(Vector3d.ZERO);
            return;
        }
        if (this.isServerWorld()) {
            float f5;
            if (this.isInWater()) {
                this.moveRelative(0.1F, vec);
                f4 = 0.6F;
                float d0 = (float) EnchantmentHelper.getDepthStriderModifier(this);
                if (d0 > 3.0F) {
                    d0 = 3.0F;
                }
                if (!this.onGround) {
                    d0 *= 0.5F;
                }
                if (d0 > 0.0F) {
                    f4 += (0.54600006F - f4) * d0 / 3.0F;
                }
                this.move(MoverType.SELF, this.getMotion());
                this.setMotion(this.getMotion().mul((double) f4 * 0.9D, (double) f4 * 0.9D, (double) f4 * 0.9D));
            } else {
                super.travel(vec);
            }
        }
        this.prevLimbSwingAmount = this.limbSwingAmount;
        double deltaX = this.getPosX() - this.prevPosX;
        double deltaZ = this.getPosZ() - this.prevPosZ;
        double deltaY = this.getPosY() - this.prevPosY;
        float delta = MathHelper.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 4.0F;
        if (delta > 1.0F) {
            delta = 1.0F;
        }
        this.limbSwingAmount += (delta - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;

    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.PRISMARINE_CRYSTALS;
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

    public ActionResultType getEntityInteractionResult(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (itemstack != null && itemstack.getItem() == Items.PRISMARINE_CRYSTALS && this.getGrowingAge() == 0 && !isInLove()) {
            this.setSitting(false);
            this.setInLove(player);
            this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1, 1);
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            return ActionResultType.SUCCESS;
        }
        if (itemstack != null && itemstack.getItem() == Items.KELP) {
            if (!world.isRemote) {
                this.heal(5);
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1, 1);
                for (int i = 0; i < 3; i++) {
                    this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, itemstack), this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getPosY() + (double) (this.rand.nextFloat() * this.getHeight()), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), 0, 0, 0);
                }
                if (!player.isCreative()) {
                    itemstack.shrink(1);
                }
            }
            if (!this.isTamed() && this.getRNG().nextInt(3) == 0) {
                this.setTamedBy(player);
                for (int i = 0; i < 6; i++) {
                    this.world.addParticle(ParticleTypes.HEART, this.getPosX() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.getPosY() + (double) (this.rand.nextFloat() * this.getHeight()), this.getPosZ() + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), 0, 0, 0);
                }
            }
            return ActionResultType.SUCCESS;

        }
        if (isOwner(player) && itemstack != null && itemstack.getItem() == Items.PRISMARINE_CRYSTALS && this.getGrowingAge() == 0 && !isInLove()) {
            this.setSitting(false);
            this.setInLove(player);
            this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1, 1);
            if (!player.isCreative()) {
                itemstack.shrink(1);
            }
            return ActionResultType.SUCCESS;
        }
        if (isOwner(player) && itemstack != null && itemstack.getItem() == Items.STICK) {
            this.setSitting(!this.isQueuedToSit());
            return ActionResultType.SUCCESS;
        }
        if (isOwner(player) && itemstack.isEmpty()) {
            if (player.isSneaking()) {
                this.openGUI(player);
                return ActionResultType.SUCCESS;
            } else if (this.isSaddled() && !this.isChild() && !player.isPassenger()) {
                player.startRiding(this, true);
                return ActionResultType.SUCCESS;
            }
        }
        return super.getEntityInteractionResult(player, hand);
    }

    public void openGUI(PlayerEntity playerEntity) {

        if (!this.world.isRemote && (!this.isBeingRidden() || this.isPassenger(playerEntity))) {
            NetworkHooks.openGui((ServerPlayerEntity) playerEntity, new INamedContainerProvider() {
                @Override
                public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
                    return new ContainerHippocampus(p_createMenu_1_, hippocampusInventory, p_createMenu_2_, EntityHippocampus.this);
                }

                @Override
                public ITextComponent getDisplayName() {
                    return new TranslationTextComponent("entity.iceandfire.hippocampus");
                }
            });
        }
        IceAndFire.PROXY.setReferencedMob(this);
    }

    @OnlyIn(Dist.CLIENT)
    protected void updateClientControls() {
        Minecraft mc = Minecraft.getInstance();
        if (this.isRidingPlayer(mc.player)) {
            byte previousState = getControlState();
            up(mc.gameSettings.keyBindJump.isKeyDown());
            dismount(mc.gameSettings.keyBindSneak.isKeyDown());
            down(IafKeybindRegistry.dragon_down.isKeyDown());
            byte controlState = getControlState();
            if (controlState != previousState) {
                IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonControl(this.getEntityId(), controlState, getPosX(), getPosY(), getPosZ()));
            }
        }
        if (this.getRidingEntity() != null && this.getRidingEntity() == mc.player) {
            byte previousState = getControlState();
            dismount(mc.gameSettings.keyBindSneak.isKeyDown());
            down(IafKeybindRegistry.dragon_down.isKeyDown());
            byte controlState = getControlState();
            if (controlState != previousState) {
                IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonControl(this.getEntityId(), controlState, getPosX(), getPosY(), getPosZ()));
            }
        }
    }

    public void up(boolean up) {
        setStateField(0, up);
    }

    public void down(boolean down) {
        setStateField(1, down);
    }

    public void dismount(boolean dismount) {
        setStateField(2, dismount);
    }

    public void refreshInventory() {
        //This isn't needed (anymore) since it's already being handled by minecraft
        if (!this.world.isRemote) {
            ItemStack saddle = this.hippocampusInventory.getStackInSlot(0);
            ItemStack chest = this.hippocampusInventory.getStackInSlot(1);
            this.setSaddled(saddle != null && saddle.getItem() == Items.SADDLE && !saddle.isEmpty());
            this.setChested(chest != null && chest.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !chest.isEmpty());
            this.setArmor(getIntFromArmor(this.hippocampusInventory.getStackInSlot(2)));
        }
        /*if (this.world.isRemote) {
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 0, saddle != null && saddle.getItem() == Items.SADDLE && !saddle.isEmpty() ? 1 : 0));
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 1, chest != null && chest.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !chest.isEmpty() ? 1 : 0));
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageHippogryphArmor(this.getEntityId(), 2, this.getIntFromArmor(this.hippocampusInventory.getStackInSlot(2))));
        }*/
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return IafSoundRegistry.HIPPOCAMPUS_IDLE;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return IafSoundRegistry.HIPPOCAMPUS_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return IafSoundRegistry.HIPPOCAMPUS_DIE;
    }

    public void dropArmor() {
        if (hippocampusInventory != null && !this.world.isRemote) {
            for (int i = 0; i < hippocampusInventory.getSizeInventory(); ++i) {
                ItemStack itemstack = hippocampusInventory.getStackInSlot(i);
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

    @Override
    public boolean canPassengerSteer() {
        return false;
    }

    @Override
    public boolean canBeSteered() {
        return true;
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

    public double getRideSpeedModifier() {
        return this.isInWater() ? 0.7F * IafConfig.hippocampusSwimSpeedMod : 0.55F;
    }

    class SwimmingMoveHelper extends MovementController {
        private EntityHippocampus hippo = EntityHippocampus.this;

        public SwimmingMoveHelper() {
            super(EntityHippocampus.this);
        }

        @Override
        public void tick() {
            if (this.hippo.isBeingRidden()) {
                double flySpeed = 0.8F * hippo.getRideSpeedModifier();
                Vector3d dragonVec = hippo.getPositionVec();
                Vector3d moveVec = new Vector3d(posX, posY, posZ);
                Vector3d normalized = moveVec.subtract(dragonVec).normalize();
                double dist = dragonVec.distanceTo(moveVec);
                hippo.setMotion(normalized.x * flySpeed, normalized.y * flySpeed, normalized.z * flySpeed);
                if (dist > 2.5E-7) {
                    float yaw = (float) Math.toDegrees(Math.PI * 2 - Math.atan2(normalized.x, normalized.y));
                    hippo.rotationYaw = limitAngle(hippo.rotationYaw, yaw, 5);
                    hippo.setAIMoveSpeed((float) (speed));
                }
                hippo.move(MoverType.SELF, hippo.getMotion());
            } else if (this.action == MovementController.Action.MOVE_TO && !this.hippo.getNavigator().noPath()) {
                double distanceX = this.posX - this.hippo.getPosX();
                double distanceY = this.posY - this.hippo.getPosY();
                double distanceZ = this.posZ - this.hippo.getPosZ();
                double distance = Math.abs(distanceX * distanceX + distanceZ * distanceZ);
                double distanceWithY = MathHelper.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
                distanceY = distanceY / distanceWithY;
                float angle = (float) (Math.atan2(distanceZ, distanceX) * 180.0D / Math.PI) - 90.0F;
                this.hippo.rotationYaw = this.limitAngle(this.hippo.rotationYaw, angle, 30.0F);
                this.hippo.setAIMoveSpeed(1F);
                float f1 = 0;
                float f2 = 0;
                if (distance < (double) Math.max(1.0F, this.hippo.getWidth())) {
                    float f = this.hippo.rotationYaw * 0.017453292F;
                    f1 -= (double) (MathHelper.sin(f) * 0.35F);
                    f2 += (double) (MathHelper.cos(f) * 0.35F);
                }
                this.hippo.setMotion(this.hippo.getMotion().add(f1, this.hippo.getAIMoveSpeed() * distanceY * 0.1D, f2));
            } else if (this.action == MovementController.Action.JUMPING) {
                this.hippo.setAIMoveSpeed((float) (this.speed * this.hippo.getAttribute(Attributes.MOVEMENT_SPEED).getValue()));
                if (this.hippo.onGround) {
                    this.action = MovementController.Action.WAIT;
                }
            } else {
                this.hippo.setAIMoveSpeed(0.0F);
            }
        }
    }
}
