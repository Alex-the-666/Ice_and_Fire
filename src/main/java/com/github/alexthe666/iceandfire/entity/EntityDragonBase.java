package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.Utils;
import com.github.alexthe666.iceandfire.api.FoodUtils;
import com.github.alexthe666.iceandfire.client.model.IFChainBuffer;
import com.github.alexthe666.iceandfire.client.model.util.LegSolverQuadruped;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.core.ModKeys;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.ai.PathNavigateExperimentalGround;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDragonforgeInput;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import com.github.alexthe666.iceandfire.item.Interactable;
import com.github.alexthe666.iceandfire.message.MessageDragonArmor;
import com.github.alexthe666.iceandfire.message.MessageDragonControl;
import com.github.alexthe666.iceandfire.message.MessageDragonSetBurnBlock;
import net.ilexiconn.llibrary.client.model.tools.ChainBuffer;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.ilexiconn.llibrary.server.entity.multipart.IMultipartEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public abstract class EntityDragonBase extends EntityTameable implements IMultipartEntity, IAnimatedEntity, IDragonFlute, IDeadMob, IVillagerFear, IAnimalFear, IDropArmor {

    private static final int FLIGHT_CHANCE_PER_TICK = 1500;
    private static final DataParameter<Integer> HUNGER = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> AGE_TICKS = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> GENDER = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SLEEPING = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> FIREBREATHING = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HOVERING = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> HEAD_ARMOR = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> NECK_ARMOR = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> BODY_ARMOR = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TAIL_ARMOR = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> MODEL_DEAD = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> DEATH_STAGE = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.VARINT);
    private static final DataParameter<Byte> CONTROL_STATE = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.BYTE);
    private static final DataParameter<Boolean> TACKLE = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> AGINGDISABLED = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> COMMAND = EntityDataManager.createKey(EntityDragonBase.class, DataSerializers.VARINT);
    public static Animation ANIMATION_EAT;
    public static Animation ANIMATION_SPEAK;
    public static Animation ANIMATION_BITE;
    public static Animation ANIMATION_SHAKEPREY;
    public static Animation ANIMATION_WINGBLAST;
    public static Animation ANIMATION_ROAR;
    public static Animation ANIMATION_TAILWHACK;
    public double minimumDamage;
    public double maximumDamage;
    public double minimumHealth;
    public double maximumHealth;
    public double minimumSpeed;
    public double maximumSpeed;
    public double minimumArmor;
    public double maximumArmor;
    public float sitProgress;
    public float sleepProgress;
    public float hoverProgress;
    public float flyProgress;
    public float fireBreathProgress;
    public int fireStopTicks;
    public int flyTicks;
    public float modelDeadProgress;
    public float ridingProgress;
    public float tackleProgress;
    public ContainerHorseChest dragonInv;
    public boolean isDaytime;
    public boolean attackDecision;
    public int flightCycle;
    public BlockPos airTarget;
    public BlockPos homePos;
    public boolean hasHomePosition = false;
    @SideOnly(Side.CLIENT)
    public IFChainBuffer roll_buffer;
    @SideOnly(Side.CLIENT)
    public ReversedBuffer turn_buffer;
    @SideOnly(Side.CLIENT)
    public ChainBuffer tail_buffer;
    private int spacebarTicks;
    float[][] growth_stages;
    public boolean isFire = this instanceof EntityFireDragon;
    public LegSolverQuadruped legSolver;
    protected int flyHovering;
    private boolean isSleeping;
    private boolean isHovering;
    private boolean isFlying;
    private boolean isBreathingFire;
    private boolean isTackling;
    private int fireTicks;
    private int hoverTicks;
    private boolean isModelDead;
    private int animationTick;
    private Animation currentAnimation;
    private ItemStackHandler itemHandler = null;
    public int walkCycle;
    private int tacklingTicks;
    private int ticksStill;
    private float lastScale;
    //private EntityDragonPart[] parts = new EntityDragonPart[1];//head, neck, left wing, right wing, left leg, right leg, tail1, tail2, tail3, tail4, tail5
    private EntityDragonPart headPart;
    private EntityDragonPart neckPart;
    private EntityDragonPart rightWingUpperPart;
    private EntityDragonPart rightWingLowerPart;
    private EntityDragonPart leftWingUpperPart;
    private EntityDragonPart leftWingLowerPart;
    private EntityDragonPart tail1Part;
    private EntityDragonPart tail2Part;
    private EntityDragonPart tail3Part;
    private EntityDragonPart tail4Part;
    public BlockPos burningTarget;

    public EntityDragonBase(World world, double minimumDamage, double maximumDamage, double minimumHealth, double maximumHealth, double minimumSpeed, double maximumSpeed) {
        super(world);
        this.minimumDamage = minimumDamage;
        this.maximumDamage = maximumDamage;
        this.minimumHealth = minimumHealth;
        this.maximumHealth = maximumHealth;
        this.minimumSpeed = minimumSpeed;
        this.maximumSpeed = maximumSpeed;
        this.minimumArmor = 1D;
        this.maximumArmor = 20D;
        ANIMATION_EAT = Animation.create(20);
        updateAttributes();
        initDragonInv();
        if (FMLCommonHandler.instance().getSide().isClient()) {
            roll_buffer = new IFChainBuffer();
            turn_buffer = new ReversedBuffer();
            tail_buffer = new ChainBuffer();
        }
        legSolver = new LegSolverQuadruped(0.2F, 1.2F, 1.0F);
        resetParts(1);
    }// /entitydata @e {NoAI:1}

    private void resetParts(float scale) {
        removeParts();
        headPart = new EntityDragonPart(this, 1.55F * scale, 0, 0.6F * scale, 0.5F * scale, 0.35F * scale, 1.5F);
        neckPart = new EntityDragonPart(this, 0.85F * scale, 0, 0.7F * scale, 0.5F * scale, 0.2F * scale, 1);
        rightWingUpperPart = new EntityDragonPart(this, 1F * scale, 90, 0.5F * scale, 0.85F * scale, 0.3F * scale, 0.5F);
        rightWingLowerPart = new EntityDragonPart(this, 1.4F * scale, 100, 0.3F * scale, 0.85F * scale, 0.2F * scale, 0.5F);
        leftWingUpperPart = new EntityDragonPart(this, 1F * scale, -90, 0.5F * scale, 0.85F * scale, 0.3F * scale, 0.5F);
        leftWingLowerPart = new EntityDragonPart(this, 1.4F * scale, -100, 0.3F * scale, 0.85F * scale, 0.2F * scale, 0.5F);
        tail1Part = new EntityDragonPart(this, -0.75F * scale, 0, 0.6F * scale, 0.35F * scale, 0.35F * scale, 1);
        tail2Part = new EntityDragonPart(this, -1.15F * scale, 0, 0.45F * scale, 0.35F * scale, 0.35F * scale, 1);
        tail3Part = new EntityDragonPart(this, -1.5F * scale, 0, 0.35F * scale, 0.35F * scale, 0.35F * scale, 1);
        tail4Part = new EntityDragonPart(this, -1.95F * scale, 0, 0.25F * scale, 0.45F * scale, 0.3F * scale, 1.5F);
    }

    private void removeParts() {
        if (headPart != null) {
            world.removeEntityDangerously(headPart);
            headPart = null;
        }
        if (neckPart != null) {
            world.removeEntityDangerously(neckPart);
            neckPart = null;
        }
        if (rightWingUpperPart != null) {
            world.removeEntityDangerously(rightWingUpperPart);
            rightWingUpperPart = null;
        }
        if (rightWingLowerPart != null) {
            world.removeEntityDangerously(rightWingLowerPart);
            rightWingLowerPart = null;
        }
        if (leftWingUpperPart != null) {
            world.removeEntityDangerously(leftWingUpperPart);
            leftWingUpperPart = null;
        }
        if (leftWingLowerPart != null) {
            world.removeEntityDangerously(leftWingLowerPart);
            leftWingLowerPart = null;
        }
        if (tail1Part != null) {
            world.removeEntityDangerously(tail1Part);
            tail1Part = null;
        }
        if (tail2Part != null) {
            world.removeEntityDangerously(tail2Part);
            tail2Part = null;
        }
        if (tail3Part != null) {
            world.removeEntityDangerously(tail3Part);
            tail3Part = null;
        }
        if (tail4Part != null) {
            world.removeEntityDangerously(tail4Part);
            tail4Part = null;
        }
    }

    private void updateParts() {
        if (headPart != null) {
            headPart.onUpdate();
        }
        if (neckPart != null) {
            neckPart.onUpdate();
        }
        if (rightWingUpperPart != null) {
            rightWingUpperPart.onUpdate();
        }
        if (rightWingLowerPart != null) {
            rightWingLowerPart.onUpdate();
        }
        if (leftWingUpperPart != null) {
            leftWingUpperPart.onUpdate();
        }
        if (leftWingLowerPart != null) {
            leftWingLowerPart.onUpdate();
        }
        if (tail1Part != null) {
            tail1Part.onUpdate();
        }
        if (tail2Part != null) {
            tail2Part.onUpdate();
        }
        if (tail3Part != null) {
            tail3Part.onUpdate();
        }
        if (tail4Part != null) {
            tail4Part.onUpdate();
        }
    }

    protected void updateBurnTarget() {
        if (!world.isRemote && burningTarget != null) {
            if (world.getTileEntity(burningTarget) != null && world.getTileEntity(burningTarget) instanceof TileEntityDragonforgeInput && this.getDistanceSq(burningTarget) < 300) {
                this.getLookHelper().setLookPosition(burningTarget.getX() + 0.5D, burningTarget.getY() + 0.5D, burningTarget.getZ() + 0.5D, 180F, 180F);
                this.breathFireAtPos(burningTarget);
                this.setBreathingFire(true);
                IceAndFire.NETWORK_WRAPPER.sendToAll(new MessageDragonSetBurnBlock(this.getEntityId(), true));
            } else {
                IceAndFire.NETWORK_WRAPPER.sendToAll(new MessageDragonSetBurnBlock(this.getEntityId(), false));
                burningTarget = null;
            }
        }
    }

    @Override
    protected PathNavigate createNavigator(@Nonnull World worldIn) {
        return IceAndFire.CONFIG.experimentalPathFinder ? new PathNavigateExperimentalGround(this, worldIn) : super.createNavigator(worldIn);
    }

    private boolean canDestroyBlock(BlockPos pos) {
        float hardness = world.getBlockState(pos).getBlock().getBlockHardness(world.getBlockState(pos), world, pos);
        return world.getBlockState(pos).getBlock().canEntityDestroy(world.getBlockState(pos), world, pos, this) && hardness >= 0;
    }

    @Override
    public boolean isMobDead() {
        return this.isModelDead();
    }

    @Override
    public boolean hasHome() {
        return hasHomePosition;
    }

    @Override
    public BlockPos getHomePosition() {
        if (homePos == null) {
            return super.getHomePosition();
        }
        return homePos;
    }

    public void setHomePos(BlockPos homePos) {
        this.homePos = homePos;
        this.hasHomePosition = homePos != null;
    }

    @Override
    public int getHorizontalFaceSpeed() {
        return 10 * this.getDragonStage() / 5;
    }

    private void initDragonInv() {
        ContainerHorseChest animalchest = this.dragonInv;
        this.dragonInv = new ContainerHorseChest("dragonInventory", 4);
        this.dragonInv.setCustomName(this.getName());
        if (animalchest != null) {
            int i = Math.min(animalchest.getSizeInventory(), this.dragonInv.getSizeInventory());

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = animalchest.getStackInSlot(j);
                if (!itemstack.isEmpty()) {
                    this.dragonInv.setInventorySlotContents(j, itemstack.copy());
                }
            }
        }
        //this.updateDragonSlots();
        this.itemHandler = new ItemStackHandler(4) {
            public void onContentsChanged() {
                int dragonArmorHead = EntityDragonBase.this.getArmorInSlot(0);
                int dragonArmorNeck = EntityDragonBase.this.getArmorInSlot(1);
                int dragonArmorBody = EntityDragonBase.this.getArmorInSlot(2);
                int dragonArmorTail = EntityDragonBase.this.getArmorInSlot(3);
                EntityDragonBase.this.updateDragonSlots();
                if (EntityDragonBase.this.ticksExisted > 20) {
                    if (dragonArmorHead != getIntFromArmor(EntityDragonBase.this.dragonInv.getStackInSlot(0))) {
                        EntityDragonBase.this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
                    }
                    if (dragonArmorNeck != getIntFromArmor(EntityDragonBase.this.dragonInv.getStackInSlot(1))) {
                        EntityDragonBase.this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
                    }
                    if (dragonArmorBody != getIntFromArmor(EntityDragonBase.this.dragonInv.getStackInSlot(2))) {
                        EntityDragonBase.this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
                    }
                    if (dragonArmorTail != getIntFromArmor(EntityDragonBase.this.dragonInv.getStackInSlot(3))) {
                        EntityDragonBase.this.playSound(SoundEvents.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
                    }
                }
            }
        };
        if (world.isRemote) {
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 0, getIntFromArmor(this.dragonInv.getStackInSlot(0))));
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 1, getIntFromArmor(this.dragonInv.getStackInSlot(1))));
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 2, getIntFromArmor(this.dragonInv.getStackInSlot(2))));
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 3, getIntFromArmor(this.dragonInv.getStackInSlot(3))));
        }
    }

    public void updateDragonSlots() {
        this.setArmorInSlot(0, getIntFromArmor(this.dragonInv.getStackInSlot(0)));
        this.setArmorInSlot(1, getIntFromArmor(this.dragonInv.getStackInSlot(1)));
        this.setArmorInSlot(2, getIntFromArmor(this.dragonInv.getStackInSlot(2)));
        this.setArmorInSlot(3, getIntFromArmor(this.dragonInv.getStackInSlot(3)));
        if (world.isRemote) {
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 0, getIntFromArmor(this.dragonInv.getStackInSlot(0))));
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 1, getIntFromArmor(this.dragonInv.getStackInSlot(1))));
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 2, getIntFromArmor(this.dragonInv.getStackInSlot(2))));
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 3, getIntFromArmor(this.dragonInv.getStackInSlot(3))));
        }
        double armorStep = (maximumArmor - minimumArmor) / (125);
        double oldValue = minimumArmor + (armorStep * this.getAgeInDays());
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(oldValue + calculateArmorModifier());
    }

    private void openGUI(EntityPlayer playerEntity) {
        if (!this.world.isRemote && (!this.isBeingRidden() || this.isPassenger(playerEntity))) {
            playerEntity.openGui(IceAndFire.INSTANCE, 0, this.world, this.getEntityId(), 0, 0);
        }
    }

    @Override
    public int getTalkInterval() {
        return 90;
    }

    @Override
    protected void onDeathUpdate() {
        this.deathTime = 0;
        if (!this.isModelDead()) {
            if (!this.world.isRemote && this.recentlyHit > 0) {
                int i = this.getExperiencePoints(this.attackingPlayer);
                i = net.minecraftforge.event.ForgeEventFactory.getExperienceDrop(this, this.attackingPlayer, i);
                while (i > 0) {
                    int j = EntityXPOrb.getXPSplit(i);
                    i -= j;
                    this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j));
                }
            }
        }
        this.setModelDead(true);

        if (this.getDeathStage() >= this.getAgeInDays() / 5) {
            this.setDead();
            for (int k = 0; k < 40; ++k) {
                double d2 = this.rand.nextGaussian() * 0.02D;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                if (world.isRemote) {
                    this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d2, d0, d1);
                }
            }
            for (int k = 0; k < 3; ++k) {
                double d2 = this.rand.nextGaussian() * 0.02D;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                if (isFire) {
                    if (world.isRemote) {
                        this.world.spawnParticle(EnumParticleTypes.FLAME, this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d2, d0, d1);
                    }
                } else {
                    IceAndFire.PROXY.spawnParticle("snowflake", this.world, this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d2, d0, d1);
                }
            }
        }
    }

    @Override
    public void setDead() {
        removeParts();
        super.setDead();
    }

    @Override
    protected int getExperiencePoints(EntityPlayer player) {
        if (this.isChild()) {
            return 15;
        }
        return 15 + this.getDragonStage() * 35;
    }

    @Override
    public boolean isAIDisabled() {
        return this.isModelDead() || isStoned() || super.isAIDisabled();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(HUNGER, 0);
        this.dataManager.register(AGE_TICKS, 0);
        this.dataManager.register(GENDER, Boolean.FALSE);
        this.dataManager.register(VARIANT, 0);
        this.dataManager.register(SLEEPING, Boolean.FALSE);
        this.dataManager.register(FIREBREATHING, Boolean.FALSE);
        this.dataManager.register(HOVERING, Boolean.FALSE);
        this.dataManager.register(FLYING, Boolean.FALSE);
        this.dataManager.register(HEAD_ARMOR, 0);
        this.dataManager.register(NECK_ARMOR, 0);
        this.dataManager.register(BODY_ARMOR, 0);
        this.dataManager.register(TAIL_ARMOR, 0);
        this.dataManager.register(DEATH_STAGE, 0);
        this.dataManager.register(MODEL_DEAD, Boolean.FALSE);
        this.dataManager.register(CONTROL_STATE, (byte) 0);
        this.dataManager.register(TACKLE, Boolean.FALSE);
        this.dataManager.register(AGINGDISABLED, Boolean.FALSE);
        this.dataManager.register(COMMAND, 0);
    }

    public boolean up() {
        return (dataManager.get(CONTROL_STATE) & 1) == 1;
    }

    public boolean down() {
        return (dataManager.get(CONTROL_STATE) >> 1 & 1) == 1;
    }

    public boolean attack() {
        return (dataManager.get(CONTROL_STATE) >> 2 & 1) == 1;
    }

    public boolean strike() {
        return (dataManager.get(CONTROL_STATE) >> 3 & 1) == 1;
    }

    public boolean dismount() {
        return (dataManager.get(CONTROL_STATE) >> 4 & 1) == 1;
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

    public void strike(boolean strike) {
        setStateField(3, strike);
    }

    public void dismount(boolean dismount) {
        setStateField(4, dismount);
    }

    private void setStateField(int i, boolean newState) {
        byte prevState = dataManager.get(CONTROL_STATE);
        if (newState) {
            dataManager.set(CONTROL_STATE, (byte) (prevState | (1 << i)));
        } else {
            dataManager.set(CONTROL_STATE, (byte) (prevState & ~(1 << i)));
        }
    }

    public byte getControlState() {
        return dataManager.get(CONTROL_STATE);
    }

    public void setControlState(byte state) {
        dataManager.set(CONTROL_STATE, state);
    }

    public void setCommand(int command) {
        this.dataManager.set(COMMAND, command);
        if (command == 1) {
            this.setSitting(true);
        } else {
            this.setSitting(false);
        }
    }

    public int getCommand() {
        return this.dataManager.get(COMMAND);
    }

    public boolean isSittingCommand() {
        return  getCommand() == 1;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Hunger", this.getHunger());
        compound.setInteger("AgeTicks", this.getAgeInTicks());
        compound.setBoolean("Gender", this.isMale());
        compound.setInteger("Variant", this.getVariant());
        compound.setBoolean("Sleeping", this.isSleeping());
        compound.setBoolean("FireBreathing", this.isBreathingFire());
        compound.setBoolean("AttackDecision", attackDecision);
        compound.setBoolean("Hovering", this.isHovering());
        compound.setBoolean("Flying", this.isFlying());
        compound.setInteger("ArmorHead", this.getArmorInSlot(0));
        compound.setInteger("ArmorNeck", this.getArmorInSlot(1));
        compound.setInteger("ArmorBody", this.getArmorInSlot(2));
        compound.setInteger("ArmorTail", this.getArmorInSlot(3));
        compound.setInteger("DeathStage", this.getDeathStage());
        compound.setBoolean("ModelDead", this.isModelDead());
        compound.setFloat("DeadProg", this.modelDeadProgress);
        compound.setBoolean("Tackle", this.isTackling());
        if (dragonInv != null) {
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < this.dragonInv.getSizeInventory(); ++i) {
                ItemStack itemstack = this.dragonInv.getStackInSlot(i);
                if (!itemstack.isEmpty()) {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    nbttagcompound.setByte("Slot", (byte) i);
                    itemstack.writeToNBT(nbttagcompound);
                    nbttaglist.appendTag(nbttagcompound);
                }
            }
            compound.setTag("Items", nbttaglist);
        }
        if (this.getCustomNameTag() != null && !this.getCustomNameTag().isEmpty()) {
            compound.setString("CustomName", this.getCustomNameTag());
        }
        compound.setBoolean("HasHomePosition", this.hasHomePosition);
        if (homePos != null && this.hasHomePosition) {
            compound.setInteger("HomeAreaX", homePos.getX());
            compound.setInteger("HomeAreaY", homePos.getY());
            compound.setInteger("HomeAreaZ", homePos.getZ());
        }
        compound.setBoolean("AgingDisabled", this.isAgingDisabled());
        compound.setInteger("Command", this.getCommand());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.setHunger(compound.getInteger("Hunger"));
        this.setAgeInTicks(compound.getInteger("AgeTicks"));
        this.setGender(compound.getBoolean("Gender"));
        this.setVariant(compound.getInteger("Variant"));
        this.setSleeping(compound.getBoolean("Sleeping"));
        this.setBreathingFire(compound.getBoolean("FireBreathing"));
        this.attackDecision = compound.getBoolean("AttackDecision");
        this.setHovering(compound.getBoolean("Hovering"));
        this.setFlying(compound.getBoolean("Flying"));
        this.setArmorInSlot(0, compound.getInteger("ArmorHead"));
        this.setArmorInSlot(1, compound.getInteger("ArmorNeck"));
        this.setArmorInSlot(2, compound.getInteger("ArmorBody"));
        this.setArmorInSlot(3, compound.getInteger("ArmorTail"));
        if (dragonInv != null) {
            NBTTagList nbttaglist = compound.getTagList("Items", 10);
            this.initDragonInv();
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                int j = nbttagcompound.getByte("Slot") & 255;
                if (j <= 4) {
                    this.dragonInv.setInventorySlotContents(j, new ItemStack(nbttagcompound));
                }
            }
        } else {
            NBTTagList nbttaglist = compound.getTagList("Items", 10);
            this.initDragonInv();
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                int j = nbttagcompound.getByte("Slot") & 255;
                this.initDragonInv();
                this.dragonInv.setInventorySlotContents(j, new ItemStack(nbttagcompound));
                this.setArmorInSlot(j, getIntFromArmor(new ItemStack(nbttagcompound)));

                if (world.isRemote) {
                    IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 0, getIntFromArmor(new ItemStack(nbttagcompound))));
                    IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 1, getIntFromArmor(new ItemStack(nbttagcompound))));
                    IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 2, getIntFromArmor(new ItemStack(nbttagcompound))));
                    IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), 3, getIntFromArmor(new ItemStack(nbttagcompound))));
                }
            }
        }

        this.setDeathStage(compound.getInteger("DeathStage"));
        this.setModelDead(compound.getBoolean("ModelDead"));
        this.modelDeadProgress = compound.getFloat("DeadProg");
        if (!compound.getString("CustomName").isEmpty()) {
            this.setCustomNameTag(compound.getString("CustomName"));
        }
        this.hasHomePosition = compound.getBoolean("HasHomePosition");
        if (hasHomePosition && compound.getInteger("HomeAreaX") != 0 && compound.getInteger("HomeAreaY") != 0 && compound.getInteger("HomeAreaZ") != 0) {
            homePos = new BlockPos(compound.getInteger("HomeAreaX"), compound.getInteger("HomeAreaY"), compound.getInteger("HomeAreaZ"));
        }
        this.setTackling(compound.getBoolean("Tackle"));
        this.setAgingDisabled(compound.getBoolean("AgingDisabled"));
        this.setCommand(compound.getInteger("Command"));
    }

    @Nullable
    public Entity getControllingPassenger() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof EntityPlayer && this.getAttackTarget() != passenger) {
                EntityPlayer player = (EntityPlayer) passenger;
                if (this.isTamed() && this.getOwnerId() != null && this.getOwnerId().equals(player.getUniqueID())) {
                    return player;
                }
            }
        }
        return null;
    }

    public boolean isRidingPlayer(EntityPlayer player) {
        return this.getControllingPassenger() != null && this.getControllingPassenger() instanceof EntityPlayer && this.getControllingPassenger().getUniqueID().equals(player.getUniqueID());
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(Math.min(2048, IceAndFire.CONFIG.dragonTargetSearchLength));
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(4.0D);

    }

    private void updateAttributes() {
        double healthStep = (maximumHealth - minimumHealth) / (125);
        double attackStep = (maximumDamage - minimumDamage) / (125);
        double speedStep = (maximumSpeed - minimumSpeed) / (125);
        double armorStep = (maximumArmor - minimumArmor) / (125);
        if (this.getAgeInDays() <= 125) {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Math.round(minimumHealth + (healthStep * this.getAgeInDays())));
            this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Math.round(minimumDamage + (attackStep * this.getAgeInDays())));
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(minimumSpeed + (speedStep * this.getAgeInDays()));
            double oldValue = minimumArmor + (armorStep * this.getAgeInDays());
            this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(oldValue + calculateArmorModifier());
        }
    }

    public int getHunger() {
        return this.dataManager.get(HUNGER);
    }

    public void setHunger(int hunger) {
        this.dataManager.set(HUNGER, Math.min(100, hunger));
    }

    public int getVariant() {
        return this.dataManager.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.dataManager.set(VARIANT, variant);
    }

    public int getAgeInDays() {
        return this.dataManager.get(AGE_TICKS) / 24000;
    }

    public void setAgeInDays(int age) {
        this.dataManager.set(AGE_TICKS, age * 24000);
    }

    public int getAgeInTicks() {
        return this.dataManager.get(AGE_TICKS);
    }

    public void setAgeInTicks(int age) {
        this.dataManager.set(AGE_TICKS, age);
    }

    public int getDeathStage() {
        return this.dataManager.get(DEATH_STAGE);
    }

    public void setDeathStage(int stage) {
        this.dataManager.set(DEATH_STAGE, stage);
    }

    public boolean isMale() {
        return this.dataManager.get(GENDER);
    }

    public boolean isModelDead() {
        if (world.isRemote) {
            return this.isModelDead = this.dataManager.get(MODEL_DEAD);
        }
        return isModelDead;
    }

    public void setModelDead(boolean modeldead) {
        this.dataManager.set(MODEL_DEAD, modeldead);
        if (!world.isRemote) {
            this.isModelDead = modeldead;
        }
    }

    public boolean isHovering() {
        if (world.isRemote) {
            return this.isHovering = this.dataManager.get(HOVERING);
        }
        return isHovering;
    }

    public void setHovering(boolean hovering) {
        this.dataManager.set(HOVERING, hovering);
        if (!world.isRemote) {
            this.isHovering = hovering;
        }
    }

    public boolean isFlying() {
        if (world.isRemote) {
            return this.isFlying = this.dataManager.get(FLYING);
        }
        return isFlying;
    }

    public void setFlying(boolean flying) {
        this.dataManager.set(FLYING, flying);
        if (!world.isRemote) {
            this.isFlying = flying;
        }
    }

    public void setGender(boolean male) {
        this.dataManager.set(GENDER, male);
    }

    public boolean isSleeping() {
        if (world.isRemote) {
            boolean isSleeping = this.dataManager.get(SLEEPING);
            this.isSleeping = isSleeping;
            return isSleeping;
        }
        return isSleeping;
    }

    public void setSleeping(boolean sleeping) {
        this.dataManager.set(SLEEPING, sleeping);
        if (!world.isRemote) {
            this.isSleeping = sleeping;
        }
    }

    public boolean isBlinking() {
        return this.ticksExisted % 50 > 43;
    }

    public boolean isBreathingFire() {
        if (world.isRemote) {
            boolean breathing = this.dataManager.get(FIREBREATHING);
            this.isBreathingFire = breathing;
            return breathing;
        }
        return isBreathingFire;
    }

    public void setBreathingFire(boolean breathing) {
        this.dataManager.set(FIREBREATHING, breathing);
        if (!world.isRemote) {
            this.isBreathingFire = breathing;
        }
    }

    public void setTackling(boolean tackling) {
        this.dataManager.set(TACKLE, tackling);
        if (!world.isRemote) {
            this.isTackling = tackling;
        }
    }

    public void setAgingDisabled(boolean isAgingDisabled) {
        this.dataManager.set(AGINGDISABLED, isAgingDisabled);
    }

    protected boolean canFitPassenger(Entity passenger) {
        return this.getPassengers().size() < 2;
    }

    public int getArmorInSlot(int i) {
        switch (i) {
            default:
                return this.dataManager.get(HEAD_ARMOR);
            case 1:
                return this.dataManager.get(NECK_ARMOR);
            case 2:
                return this.dataManager.get(BODY_ARMOR);
            case 3:
                return this.dataManager.get(TAIL_ARMOR);
        }
    }

    public void riderShootFire(Entity controller) {
    }

    @Override
    public void onKillEntity(EntityLivingBase entity) {
        super.onKillEntity(entity);
        this.setHunger(this.getHunger() + FoodUtils.getFoodPoints(entity));
    }

    public void setArmorInSlot(int i, int armorType) {
        switch (i) {
            case 0:
                this.dataManager.set(HEAD_ARMOR, armorType);
                break;
            case 1:
                this.dataManager.set(NECK_ARMOR, armorType);
                break;
            case 2:
                this.dataManager.set(BODY_ARMOR, armorType);
                break;
            case 3:
                this.dataManager.set(TAIL_ARMOR, armorType);
                break;
        }
        if (world.isRemote) {
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonArmor(this.getEntityId(), i, armorType));
        }
        double armorStep = (maximumArmor - minimumArmor) / (125);
        double oldValue = minimumArmor + (armorStep * this.getAgeInDays());
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(oldValue + calculateArmorModifier());
    }

    private double calculateArmorModifier() {
        double val = 1D;
        for (int i = 0; i < 4; i++) {
            switch (getArmorInSlot(i)) {
                case 1:
                    val += 2D;
                    break;
                case 2:
                    val += 3D;
                    break;
                case 3:
                    val += 5D;
                    break;
            }
        }
        return val;
    }

    public boolean canMove() {
        return canMoveWithoutSleeping() && !this.isSleeping() && sleepProgress == 0;
    }

    public boolean canMoveWithoutSleeping() {
        if (isStoned()) {
            return false;
        }
        return !this.isSitting()
                && this.getControllingPassenger() == null
                && !this.isModelDead()
                && this.getAnimation() != ANIMATION_SHAKEPREY;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        int lastDeathStage = this.getAgeInDays() / 5;
        if (this.isModelDead() && this.getDeathStage() < lastDeathStage && player.capabilities.allowEdit) {
            //player.addStat(ModAchievements.dragonHarvest, 1);
            if (!world.isRemote && !stack.isEmpty() && stack.getItem() == Items.GLASS_BOTTLE && this.getDeathStage() < lastDeathStage / 2 && IceAndFire.CONFIG.dragonDropBlood) {
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
                this.setDeathStage(this.getDeathStage() + 1);
                player.inventory.addItemStackToInventory(new ItemStack(this instanceof EntityFireDragon ? ModItems.fire_dragon_blood : ModItems.ice_dragon_blood, 1));
                return true;
            } else if (!world.isRemote && stack.isEmpty() && IceAndFire.CONFIG.dragonDropSkull) {
                if (this.getDeathStage() == lastDeathStage - 1) {
                    ItemStack skull = new ItemStack(ModItems.dragon_skull, 1, this.isFire ? 0 : 1);
                    skull.setTagCompound(new NBTTagCompound());
                    skull.getTagCompound().setInteger("Stage", this.getDragonStage());
                    skull.getTagCompound().setInteger("DragonType", 0);
                    skull.getTagCompound().setInteger("DragonAge", this.getAgeInDays());
                    this.setDeathStage(this.getDeathStage() + 1);
                    if (!world.isRemote) {
                        this.entityDropItem(skull, 1);
                    }
                    this.setDead();
                } else if (this.getDeathStage() == lastDeathStage / 2 - 1 && IceAndFire.CONFIG.dragonDropHeart) {
                    ItemStack heart = new ItemStack(this instanceof EntityFireDragon ? ModItems.fire_dragon_heart : ModItems.ice_dragon_heart, 1);
                    ItemStack egg = new ItemStack(this.getVariantEgg(this.rand.nextInt(4)), 1);
                    if (!world.isRemote) {
                        this.entityDropItem(heart, 1);
                        if (!this.isMale() && isAdult()) {
                            this.entityDropItem(egg, 1);
                        }
                    }
                    this.setDeathStage(this.getDeathStage() + 1);
                } else {
                    this.setDeathStage(this.getDeathStage() + 1);
                    ItemStack drop = getRandomDrop();
                    if (!drop.isEmpty() && !world.isRemote) {
                        this.entityDropItem(drop, 1);
                    }
                }
            }
            return true;
        } else if (!this.isModelDead()) {

            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                if (item instanceof Interactable) {
                    if (((Interactable) item).processInteract(player, this, hand, stack)) {
                        return true;
                    }
                }
            }

            if (this.isOwner(player)) {
                if (!stack.isEmpty()) {
                    if (this.isBreedingItem(stack) && this.isAdult()) {
                        this.setGrowingAge(0);
                        this.consumeItemFromStack(player, stack);
                        this.setInLove(player);
                        return true;
                    }
                    int itemFoodAmount = FoodUtils.getFoodPoints(stack, true, !isFire);
                    if (itemFoodAmount > 0 && (this.getHunger() < 100 || this.getHealth() < this.getMaxHealth())) {
                        //this.growDragon(1);
                        this.setHunger(this.getHunger() + itemFoodAmount);
                        this.setHealth(Math.min(this.getMaxHealth(), (int) (this.getHealth() + (itemFoodAmount / 10))));
                        this.playEatSound();
                        this.spawnItemCrackParticles(stack.getItem());
                        this.eatFoodBonus(stack);
                        if (!player.isCreative()) {
                            stack.shrink(1);
                        }
                        return true;
                    }
                } else {
                    if (!player.isSneaking()) {
                        if (this.isChild()) {
                            getNavigator().clearPath();
                            this.startRiding(player, true);
                        } else if (!player.isRiding()) {
                            getNavigator().clearPath();
                            player.setSneaking(false);
                            player.startRiding(this, true);
                            this.setSleeping(false);
                        }
                        return true;
                    } else if (player.isSneaking()) {
                        this.openGUI(player);
                        return true;
                    }
                }
            }
        }
        return false;

    }

    private ItemStack getRandomDrop() {
        ItemStack stack = getItemFromLootTable();
        if (stack.getItem() == ModItems.dragonbone) {
            this.playSound(SoundEvents.ENTITY_SKELETON_AMBIENT, 1, 1);
        } else {
            this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1, 1);
        }
        return stack;
    }

    public ItemStack getItemFromLootTable() {
        LootTable loottable = this.world.getLootTableManager().getLootTableFromLocation(getDeadLootTable());
        LootContext.Builder lootcontext$builder = (new LootContext.Builder((WorldServer) this.world)).withLootedEntity(this).withDamageSource(DamageSource.GENERIC);
        if (this.attackingPlayer != null) {
            lootcontext$builder = lootcontext$builder.withPlayer(this.attackingPlayer).withLuck(this.attackingPlayer.getLuck());
        }
        List<ItemStack> loot = loottable.generateLootForPools(this.rand, lootcontext$builder.build());
        if (loot.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            return loot.get(0);
        }
    }

    public void eatFoodBonus(ItemStack stack) {

    }

    @Override
    protected void despawnEntity() {
        if (!IceAndFire.CONFIG.canDragonsDespawn) {
            super.despawnEntity();
        }
    }

    public void growDragon(int ageInDays) {
        if (this.isAgingDisabled()) {
            return;
        }
        this.setAgeInDays(this.getAgeInDays() + ageInDays);
        this.setScaleForAge(false);
        this.resetPositionToBB();
        if (this.getAgeInDays() % 25 == 0) {
            for (int i = 0; i < this.getRenderSize() * 4; i++) {
                double motionX = getRNG().nextGaussian() * 0.07D;
                double motionY = getRNG().nextGaussian() * 0.07D;
                double motionZ = getRNG().nextGaussian() * 0.07D;
                float f = (float) (getRNG().nextFloat() * (this.getEntityBoundingBox().maxX - this.getEntityBoundingBox().minX) + this.getEntityBoundingBox().minX);
                float f1 = (float) (getRNG().nextFloat() * (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) + this.getEntityBoundingBox().minY);
                float f2 = (float) (getRNG().nextFloat() * (this.getEntityBoundingBox().maxZ - this.getEntityBoundingBox().minZ) + this.getEntityBoundingBox().minZ);
                if (world.isRemote) {
                    this.world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, f, f1, f2, motionX, motionY, motionZ);
                }
            }
        }
        this.updateAttributes();
    }

    public void spawnItemCrackParticles(Item item) {
        for (int i = 0; i < 15; i++) {
            double motionX = getRNG().nextGaussian() * 0.07D;
            double motionY = getRNG().nextGaussian() * 0.07D;
            double motionZ = getRNG().nextGaussian() * 0.07D;
            float headPosX = (float) (posX + 1.9F * getRenderSize() * 0.3F * Math.cos((rotationYaw + 90) * Math.PI / 180));
            float headPosZ = (float) (posZ + 1.9F * getRenderSize() * 0.3F * Math.sin((rotationYaw + 90) * Math.PI / 180));
            float headPosY = (float) (posY + (getRenderSize() * 0.2F));
            if (world.isRemote) {
                this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, headPosX, headPosY, headPosZ, motionX, motionY, motionZ, Item.getIdFromItem(item));
            }
        }
    }

    private boolean isStuck() {
        return !this.isChained() && !this.isTamed() && (!this.getNavigator().noPath() && (this.getNavigator().getPath() == null || this.getNavigator().getPath().getFinalPathPoint() != null && this.getDistanceSq(new BlockPos(this.getNavigator().getPath().getFinalPathPoint().x, this.getNavigator().getPath().getFinalPathPoint().y, this.getNavigator().getPath().getFinalPathPoint().z)) > 15) || this.airTarget != null) && ticksStill > 80 && !this.isHovering() && canMove();
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!world.isRemote) {

            updateBurnTarget();

            updateSitting();

            if (this.isBeyondHeight()) {
                this.motionY -= 0.1F;
            }
            if (this.isInLove()) {
                this.world.setEntityState(this, (byte) 18);
            }
            if ((int) this.prevPosX == (int) this.posX && (int) this.prevPosZ == (int) this.posZ) {
                this.ticksStill++;
            } else {
                ticksStill = 0;
            }
            if (this.getDragonStage() >= 3 && isStuck() && this.world.getGameRules().getBoolean("mobGriefing") && IceAndFire.CONFIG.dragonGriefing != 2) {
                if (this.getAnimation() == NO_ANIMATION && this.ticksExisted % 5 == 0) {
                    this.setAnimation(ANIMATION_TAILWHACK);
                }
                if (this.getAnimation() == ANIMATION_TAILWHACK && this.getAnimationTick() == 10) {
                    IBlockState state = world.getBlockState(new BlockPos(this));
                    BlockBreakExplosion explosion = new BlockBreakExplosion(world, this, this.posX, this.posY, this.posZ, (4) * this.getDragonStage() - 2);
                    explosion.doExplosionA();
                    explosion.doExplosionB(true);
                    this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1, 1);

                }
            }
        }

        if (this.isTackling() && !this.isFlying() && this.onGround) {
            tacklingTicks++;
            if (tacklingTicks == 40) {
                tacklingTicks = 0;
                this.setTackling(false);
                this.setFlying(false);
            }
        }
        if (this.walkCycle < 39) {
            this.walkCycle++;
        } else {
            this.walkCycle = 0;
        }
        if (!world.isRemote && this.getRNG().nextInt(500) == 0 && !this.isModelDead() && !this.isSleeping()) {
            this.roar();
        }
        if (!world.isRemote
                && this.onGround
                && this.getNavigator().noPath()
                && this.getAttackTarget() != null
                && this.getAttackTarget().posY - 3 > this.posY
                && this.getRNG().nextInt(15) == 0
                && this.canMove()
                && !this.isHovering()
                && !this.isFlying()
                && !this.isChild()
                ) {
            this.setHovering(true);
            this.setSleeping(false);
            this.setSitting(false);
            this.flyHovering = 0;
            this.hoverTicks = 0;
            this.flyTicks = 0;
        }
        if (this.getAnimation() == ANIMATION_WINGBLAST && (this.getAnimationTick() == 17 || this.getAnimationTick() == 22 || this.getAnimationTick() == 28)) {
            this.spawnGroundEffects();
            if (this.getAttackTarget() != null) {
                boolean flag = this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()) / 4);
                this.getAttackTarget().knockBack(this.getAttackTarget(), this.getDragonStage() * 0.6F, 1, 1);
                this.attackDecision = this.getRNG().nextBoolean();
            }
        }
        if (!world.isRemote && this.isFlying()
                && this.getAttackTarget() != null
                && this.attackDecision
                && this.isDirectPathBetweenPoints(this.getPositionVector(), this.getAttackTarget().getPositionVector())) {
            this.setTackling(true);
        }
        if (!world.isRemote
                && this.isFlying()
                && this.getAttackTarget() != null
                && this.isTackling()
                && this.getEntityBoundingBox().expand(2.0D, 2.0D, 2.0D).intersects(this.getAttackTarget().getEntityBoundingBox())) {
            this.attackDecision = true;
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), this.getDragonStage() * 3);
            this.spawnGroundEffects();
            this.setFlying(false);
            this.setHovering(false);
        }
        if (!world.isRemote && this.isTackling() && this.getAttackTarget() == null) {
            this.setTackling(false);
        }
        if (isStoned()) {
            this.setFlying(false);
            this.setHovering(false);
            return;
        }
//        if (this.isFlying() && this.ticksExisted % 40 == 0 && !this.isSleeping()) {
//            this.setFlying(true);
//        }

        if (!this.canMove()) {
            this.getNavigator().clearPath();
        }
        if (this.getControllingPassenger() != null) {
            if (motionY > 0.5) {
                this.motionY = 0.5;
            }
            if (motionY < -0.5) {
                this.motionY = -0.5;
            }
        } else {
            if (motionY > 0.5) {
                this.motionY = 0.5;
            }
            if (motionY < -0.5) {
                this.motionY = -0.5;
            }
            if (motionY > 1) {
                this.motionY = 0;
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
        this.legSolver.update(this);
        if ((this.isFlying() || this.isHovering()) && !this.isModelDead()) {
            if (flightCycle < 58) {
                flightCycle += 2;
            } else {
                flightCycle = 0;
            }
            if (flightCycle == 2) {
                this.playSound(ModSounds.DRAGON_FLIGHT, this.getSoundVolume() * IceAndFire.CONFIG.dragonFlapNoiseDistance, getSoundPitch());
            }
            if (flightCycle > 10 && flightCycle < 12) {
                this.spawnGroundEffects();
            }
            if (this.isModelDead() && flightCycle != 0) {
                flightCycle = 0;
            }
        }
        if (this.isModelDead() && (this.isFlying() || this.isHovering())) {
            this.setFlying(false);
            this.setHovering(false);
        }

        boolean sitting = isSitting() && !isModelDead() && !isSleeping() && !isHovering() && !isFlying();
        if (sitting && sitProgress < 20.0F) {
            sitProgress += 0.5F;
        } else if (!sitting && sitProgress > 0.0F) {
            sitProgress -= 0.5F;
        }
        boolean sleeping = isSleeping() && !isHovering() && !isFlying();
        if (sleeping && sleepProgress < 20.0F) {
            sleepProgress += 0.5F;
        } else if (!sleeping && sleepProgress > 0.0F) {
            sleepProgress -= 0.5F;
        }
        boolean fireBreathing = isBreathingFire();
        if (fireBreathing && fireBreathProgress < 5.0F) {
            fireBreathProgress += 0.5F;
        } else if (!fireBreathing && fireBreathProgress > 0.0F) {
            fireBreathProgress -= 0.5F;
        }
        boolean hovering = isHovering();
        if (hovering && hoverProgress < 20.0F) {
            hoverProgress += 0.5F;
        } else if (!hovering && hoverProgress > 0.0F) {
            hoverProgress -= 0.5F;
        }
        boolean tackling = isTackling();
        if (tackling && tackleProgress < 5F) {
            tackleProgress += 0.5F;
        } else if (!tackling && tackleProgress > 0.0F) {
            tackleProgress -= 1.5F;
        }
        boolean flying = !tackling && this.isFlying() || !this.onGround && !this.isHovering() && this.airTarget != null;
        if (flying && flyProgress < 20.0F) {
            flyProgress += 0.5F;
        } else if (!flying && flyProgress > 0.0F) {
            flyProgress -= 0.5F;
        }
        boolean modeldead = isModelDead();
        if (modeldead && modelDeadProgress < 20.0F) {
            modelDeadProgress += 0.5F;
        } else if (!modeldead && modelDeadProgress > 0.0F) {
            modelDeadProgress -= 0.5F;
        }
        boolean riding = isRiding() && this.getRidingEntity() != null && this.getRidingEntity() instanceof EntityPlayer;
        if (riding && ridingProgress < 20.0F) {
            ridingProgress += 0.5F;
        } else if (!riding && ridingProgress > 0.0F) {
            ridingProgress -= 0.5F;
        }

        if (this.isModelDead()) {
            return;
        }
        if (!this.world.isRemote && this.onGround && this.doesWantToLand() && (this.isFlying() || this.isHovering())) {
            this.setFlying(false);
            this.setHovering(false);
        }
        if (this.getControllingPassenger() != null && !this.onGround && (this.isFlying() || this.isHovering())) {
            this.motionY *= 0D;
        }
        if (this.isHovering()) {
            if (this.isSleeping()) {
                this.setHovering(false);
            }
            this.hoverTicks++;
            if (this.doesWantToLand() && !this.onGround) {
                this.motionY -= 0.25D;
            } else {
                if (this.getControllingPassenger() == null && !this.isBeyondHeight()) {
                    this.motionY += 0.08;
                }
                if (this.hoverTicks > 40) {
                    if (!this.isChild()) {
                        this.setFlying(true);
                    }
                    this.setHovering(false);
                    this.flyHovering = 0;
                    this.hoverTicks = 0;
                    this.flyTicks = 0;
                }
            }

            if (flyHovering == 0) {
                // move upwards
            }
            if (flyHovering == 1) {
                // move down
            }
            if (flyHovering == 2) {
                // stay still
            }
        }
        if (this.isSleeping()) {
            this.getNavigator().clearPath();
        }
        if (!this.isFlying() && !this.isHovering() && this.airTarget != null && this.onGround) {
            this.airTarget = null;
        }
        if (this.isFlying() && this.airTarget == null && this.onGround && this.getControllingPassenger() == null) {
            this.setFlying(false);
        }

        if (this.isFlying() && getAttackTarget() == null) {
            flyAround();
        } else if (getAttackTarget() != null) {
            flyTowardsTarget();
        }
        if (this.onGround && flyTicks != 0) {
            flyTicks = 0;
        }
        if (this.isFlying() && this.doesWantToLand()) {
            this.setFlying(false);
            this.setHovering(!this.onGround);
            if (this.onGround) {
                flyTicks = 0;
                this.setFlying(false);
            }
            //this.motionY -= 0.26D;
        }
        if (this.isFlying()) {
            this.flyTicks++;
        }
        if ((this.isHovering() || this.isFlying()) && this.isSleeping()) {
            this.setFlying(false);
            this.setHovering(false);
        }
        if (!isStoned() && (!world.isRemote && this.getRNG().nextInt(FLIGHT_CHANCE_PER_TICK) == 0 && !this.isSitting() && !this.isFlying() && this.getPassengers().isEmpty() && !this.isChild() && !this.isHovering() && !this.isSleeping() && this.canMove() && this.onGround || this.posY < -1)) {
            this.setHovering(true);
            this.setSleeping(false);
            this.setSitting(false);
            this.flyHovering = 0;
            this.hoverTicks = 0;
            this.flyTicks = 0;
        }

        if (this.getAttackTarget() != null && this.getAttackTarget().posY + 5 < this.posY && !isStoned() && (!world.isRemote && !this.isSitting() && !this.isFlying() && this.getPassengers().isEmpty() && !this.isChild() && !this.isHovering() && !this.isSleeping() && this.canMove() && this.onGround)) {
            this.setHovering(true);
            this.setSleeping(false);
            this.setSitting(false);
            this.flyHovering = 0;
            this.hoverTicks = 0;
            this.flyTicks = 0;
        }

        updateAttackTarget();

        if (!this.isAgingDisabled()) {
            this.setAgeInTicks(this.getAgeInTicks() + 1);
            if (this.getAgeInTicks() % 24000 == 0) {
                this.updateAttributes();
                this.growDragon(0);
            }
        }
        if (this.getAgeInTicks() % IceAndFire.CONFIG.dragonHungerTickRate == 0) {
            if (this.getHunger() > 0) {
                this.setHunger(this.getHunger() - 1);
            }
        }
        if (this.attackDecision && this.getAttackTarget() != null && this.getDistance(this.getAttackTarget()) > Math.min(this.getEntityBoundingBox().getAverageEdgeLength() * 5, 25) && !this.isChild()) {
            this.attackDecision = false;
        }
        if ((!this.attackDecision || this.getRNG().nextInt(750) == 0) && this.isChild()) {
            this.attackDecision = this.getRNG().nextBoolean();
            for (int i = 0; i < 5; i++) {
                float radiusAdd = i * 0.15F;
                float headPosX = (float) (posX + 1.8F * getRenderSize() * (0.3F + radiusAdd) * Math.cos((rotationYaw + 90) * Math.PI / 180));
                float headPosZ = (float) (posZ + 1.8F * getRenderSize() * (0.3F + radiusAdd) * Math.sin((rotationYaw + 90) * Math.PI / 180));
                float headPosY = (float) (posY + 0.5 * getRenderSize() * 0.3F);
                if (this.isFire && world.isRemote) {
                    this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, headPosX, headPosY, headPosZ, 0, 0, 0);
                } else if (world.isRemote) {
                    IceAndFire.PROXY.spawnParticle("dragonice", this.world, headPosX, headPosY, headPosZ, 0, 0, 0);
                }
            }
            if (this.isFire) {
                this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1, 1);
            } else {
                this.playSound(SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, 1, 1);
            }
        }

        updateBreathingFire();

        if (this.isFlying() && this.getAttackTarget() != null && this.getEntityBoundingBox().expand(3.0F, 3.0F, 3.0F).intersects(this.getAttackTarget().getEntityBoundingBox())) {
            this.attackEntityAsMob(this.getAttackTarget());
        }
        this.breakBlock();
    }

    private void updateSitting() {
        if (this.isSitting() && (this.getCommand() != 1 || this.getControllingPassenger() != null)) {
            this.setSitting(false);
        }
        if (!this.isSitting() && this.isSittingCommand() && this.getControllingPassenger() == null) {
            this.setSitting(true);
        }

        if (this.isSitting()) {
            this.getNavigator().clearPath();
        }
    }

    private void updateAttackTarget() {
        Entity attackTarget = this.getAttackTarget();
        if (attackTarget != null) {
            if (Utils.isEntityDead(attackTarget)
                    || !this.canMoveWithoutSleeping()
                    || (this.getOwner() != null && this.getPassengers().contains(this.getOwner()))
                    ) {
                this.setAttackTarget(null);
            }
        } else {
            if (!this.isTamed() && this.isSleeping()) {
                double checkLength = getMaxAttackPlayerDistance();
                EntityPlayer player = world.getClosestPlayerToEntity(this, checkLength);
                if (player != null && !this.isOwner(player) && !player.capabilities.isCreativeMode) {
                    this.setSleeping(false);
                    this.setSitting(false);
                    this.setAttackTarget(player);
                }
            }
        }
    }

    private void updateBreathingFire() {
        if (this.isBreathingFire()) {
            this.fireTicks++;
            if (!world.isRemote && fireTicks > this.getDragonStage() * 25 || this.getOwner() != null && this.getPassengers().contains(this.getOwner()) && this.fireStopTicks <= 0) {
                this.setBreathingFire(false);
                this.attackDecision = this.getRNG().nextBoolean();
                fireTicks = 0;
            }
            if (fireStopTicks > 0 && this.getOwner() != null && this.getPassengers().contains(this.getOwner())) {
                fireStopTicks--;
            }
        }
    }

    private void breakBlock() {
        if (IceAndFire.CONFIG.dragonGriefing != 2 || this.isTamed() && !IceAndFire.CONFIG.tamedDragonGriefing) {
            float hardness = IceAndFire.CONFIG.dragonGriefing == 1 || this.getDragonStage() <= 3 ? 1.6F : 5F;
            if (!isModelDead() && this.getDragonStage() >= 3 && this.canMove()) {
                for (int a = (int) Math.round(this.getEntityBoundingBox().minX) - 1; a <= (int) Math.round(this.getEntityBoundingBox().maxX) + 1; a++) {
                    for (int b = (int) Math.round(this.getEntityBoundingBox().minY) + 1; (b <= (int) Math.round(this.getEntityBoundingBox().maxY) + 2) && (b <= 127); b++) {
                        for (int c = (int) Math.round(this.getEntityBoundingBox().minZ) - 1; c <= (int) Math.round(this.getEntityBoundingBox().maxZ) + 1; c++) {
                            IBlockState state = world.getBlockState(new BlockPos(a, b, c));
                            Block block = state.getBlock();
                            if (state.getMaterial() != Material.AIR && !(block instanceof BlockBush) && !(block instanceof BlockLiquid) && block != Blocks.BEDROCK && state.getBlockHardness(world, new BlockPos(a, b, c)) < hardness && DragonUtils.canDragonBreak(state.getBlock()) && this.canDestroyBlock(new BlockPos(a, b, c))) {
                                this.motionX *= 0.6D;
                                this.motionZ *= 0.6D;
                                if (block != Blocks.AIR) {
                                    if (!world.isRemote) {
                                        world.destroyBlock(new BlockPos(a, b, c), true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void spawnGroundEffects() {
        for (int i = 0; i < this.getRenderSize(); i++) {
            for (int i1 = 0; i1 < 20; i1++) {
                double motionX = getRNG().nextGaussian() * 0.07D;
                double motionY = getRNG().nextGaussian() * 0.07D;
                double motionZ = getRNG().nextGaussian() * 0.07D;
                float radius = 0.75F * (0.7F * getRenderSize() / 3) * -3;
                float angle = (0.01745329251F * this.renderYawOffset) + i1 * 1F;
                double extraX = (double) (radius * MathHelper.sin((float) (Math.PI + angle)));
                double extraY = 0.8F;
                double extraZ = (double) (radius * MathHelper.cos(angle));

                IBlockState iblockstate = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX + extraX), MathHelper.floor(this.posY + extraY) - 1, MathHelper.floor(this.posZ + extraZ)));
                if (iblockstate.getMaterial() != Material.AIR) {
                    if (world.isRemote) {
                        world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, true, this.posX + extraX, this.posY + extraY, this.posZ + extraZ, motionX, motionY, motionZ, Block.getStateId(iblockstate));
                    }
                }
            }
        }
    }

    @Override
    public void fall(float distance, float damageMultiplier) {

    }

    protected boolean isActuallyBreathingFire() {
        return this.fireTicks > 20 && this.isBreathingFire();
    }

    public boolean doesWantToLand() {
        return this.flyTicks > 6000 || down() || flyTicks > 40 && this.flyProgress == 0 || this.isStoned() || this.isChained() && flyTicks > 100;
    }

    @Override
    public void updatePassenger(@Nonnull Entity passenger) {
        super.updatePassenger(passenger);
        if (this.isPassenger(passenger)) {
            if (this.getControllingPassenger() == null || this.getControllingPassenger().getEntityId() != passenger.getEntityId()) {
                updatePreyInMouth(passenger);
            } else {
                if (this.isModelDead()) {
                    passenger.dismountRidingEntity();
                }
                float speed_walk = 0.2F;
                float speed_idle = 0.05F;
                float speed_fly = 0.2F;
                float degree_walk = 0.5F;
                float degree_idle = 0.5F;
                float degree_fly = 0.5F;
                //this.walk(BodyLower, speed_fly, (float) (degree_fly * 0.15), false, 0, 0, entity.ticksExisted, 1);
                //this.walk(BodyUpper, speed_fly, (float) (degree_fly * -0.15), false, 0, 0, entity.ticksExisted, 1);
                renderYawOffset = rotationYaw;
                this.rotationYaw = passenger.rotationYaw;
                float hoverAddition = hoverProgress * -0.001F;
                float flyAddition = flyProgress * -0.0001F;
                float flyBody = Math.max(flyProgress, hoverProgress) * 0.0065F;
                float radius = 0.75F * ((0.3F - flyBody) * getRenderSize()) + ((this.getRenderSize() / 3) * flyAddition * 0.0065F);
                float angle = (0.01745329251F * this.renderYawOffset);
                double extraX = (double) (radius * MathHelper.sin((float) (Math.PI + angle)));
                double extraZ = (double) (radius * MathHelper.cos(angle));
                float bob0 = this.isFlying() || this.isHovering() ? (hoverProgress > 0 || flyProgress > 0 ? this.bob(-speed_fly, degree_fly * 5, false, this.ticksExisted, -0.0625F) : 0) : 0;
                float bob1 = this.bob(speed_walk * 2, degree_walk * 1.7F, false, this.limbSwing, this.limbSwingAmount * -0.0625F);
                float bob2 = this.bob(speed_idle, degree_idle * 1.3F, false, this.ticksExisted, -0.0625F);

                double extraY_pre = 0.8F;
                double extraY = ((extraY_pre - (hoverAddition) + (flyAddition)) * (this.getRenderSize() / 3)) - (0.35D * (1 - (this.getRenderSize() / 30))) + bob0 + bob1 + bob2;

                passenger.setPosition(this.posX + extraX, this.posY + extraY, this.posZ + extraZ);

                this.stepHeight = 1;
            }
        }
    }

    private float bob(float speed, float degree, boolean bounce, float f, float f1) {
        float bob = (float) (Math.sin(f * speed) * f1 * degree - f1 * degree);
        if (bounce) {
            bob = (float) -Math.abs((Math.sin(f * speed) * f1 * degree));
        }
        return bob * this.getRenderSize() / 3;
    }

    private void updatePreyInMouth(Entity prey) {
        this.setAnimation(ANIMATION_SHAKEPREY);
        if (this.getAnimation() == ANIMATION_SHAKEPREY && this.getAnimationTick() > 55 && prey != null) {
            prey.attackEntityFrom(DamageSource.causeMobDamage(this), prey instanceof EntityPlayer ? 17F : (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * 4);
            prey.dismountRidingEntity();
        }
        renderYawOffset = rotationYaw;
        float modTick_0 = this.getAnimationTick() - 25;
        float modTick_1 = this.getAnimationTick() > 25 && this.getAnimationTick() < 55 ? 8 * MathHelper.clamp(MathHelper.sin((float) (Math.PI + modTick_0 * 0.25)), -0.8F, 0.8F) : 0;
        float modTick_2 = this.getAnimationTick() > 30 ? 10 : Math.max(0, this.getAnimationTick() - 20);
        float radius = 0.75F * (0.6F * getRenderSize() / 3) * -3;
        float angle = (0.01745329251F * this.renderYawOffset) + 3.15F + (modTick_1 * 2F) * 0.015F;
        double extraX = (double) (radius * MathHelper.sin((float) (Math.PI + angle)));
        double extraZ = (double) (radius * MathHelper.cos(angle));
        double extraY = modTick_2 == 0 ? 0 : 0.035F * ((getRenderSize() / 3) + (modTick_2 * 0.5 * (getRenderSize() / 3)));
        if (prey != null) {
            prey.setPosition(this.posX + extraX, this.posY + extraY, this.posZ + extraZ);
        }
    }

    public int getDragonStage() {
        int age = this.getAgeInDays();
        if (age >= 100) {
            return 5;
        } else if (age >= 75) {
            return 4;
        } else if (age >= 50) {
            return 3;
        } else if (age >= 25) {
            return 2;
        } else {
            return 1;
        }
    }

    public boolean isTeen() {
        return getDragonStage() < 4 && getDragonStage() > 2;
    }

    public boolean isAdult() {
        return getDragonStage() >= 4;
    }

    @Override
    public boolean isChild() {
        return getDragonStage() < 2;
    }

    @Override
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        this.setGender(this.getRNG().nextBoolean());
        int age = this.getRNG().nextInt(80) + 1;
        this.growDragon(age);
        this.setVariant(new Random().nextInt(4));
        this.setSleeping(false);
        this.updateAttributes();
        double healthStep = (maximumHealth - minimumHealth) / (125);
        this.heal((Math.round(minimumHealth + (healthStep * age))));
        this.attackDecision = true;
        this.setHunger(50);
        return livingdata;
    }

    @Override
    public boolean attackEntityFrom(DamageSource dmg, float i) {
        if (this.isModelDead()) {
            return false;
        }

        Entity trueSource = dmg.getTrueSource();

        if (trueSource != null) {
            if (trueSource.isEntityEqual(this)) {
                return false;
            }
            if (trueSource.isEntityEqual(getOwner())) {
                return false;
            }
            if (this.isBeingRidden() && trueSource.isEntityEqual(this.getControllingPassenger())) {
                return false;
            }
        }

        if (this.isRiding() && (dmg.damageType.contains("arrow") || getRidingEntity() != null && trueSource != null && trueSource.isEntityEqual(this.getRidingEntity()))) {
            return false;
        }

        if (dmg == DamageSource.IN_WALL || dmg == DamageSource.FALLING_BLOCK) {
            return false;
        }

        if (!world.isRemote && trueSource != null && this.getRNG().nextInt(4) == 0) {
            this.roar();
        }
        return super.attackEntityFrom(dmg, i);

    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        updateParts();
        this.setScaleForAge(true);
        if (world.isRemote) {
            this.updateClientControls();
        }
        if (this.isModelDead()) {
            return;
        }
        if (this.up()) {
            if (!this.isFlying() && !this.isHovering()) {
                this.spacebarTicks += 2;
            }
            if (this.isFlying() || this.isHovering()) {
                this.motionY += 0.4D;
            }
        } else if (this.dismount()) {
            if (this.isFlying() || this.isHovering()) {
                this.motionY -= 0.4D;
                this.setFlying(false);
                this.setHovering(false);
            }
        }
        if (this.down() && (this.isFlying() || this.isHovering())) {
            this.motionY -= 0.4D;
        }
        if (!this.dismount() && (this.isFlying() || this.isHovering())) {
            this.motionY += 0.01D;
        }
        if (this.attack() && this.getControllingPassenger() != null && this.getDragonStage() > 1) {
            this.setBreathingFire(true);
            this.riderShootFire(this.getControllingPassenger());
            this.fireStopTicks = 10;
        }
        if (this.strike() && this.getControllingPassenger() != null && this.getControllingPassenger() instanceof EntityPlayer) {
            EntityLivingBase target = DragonUtils.riderLookingAtEntity(this, (EntityPlayer) this.getControllingPassenger(), this.getDragonStage() + (this.getEntityBoundingBox().maxX - this.getEntityBoundingBox().minX));
            if (this.getAnimation() != ANIMATION_BITE) {
                this.setAnimation(ANIMATION_BITE);
            }
            if (target != null && !DragonUtils.hasSameOwner(this, target)) {
                target.attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
            }
        }
        if (this.getControllingPassenger() != null && this.getControllingPassenger().isSneaking()) {
            MiscPlayerProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(this.getControllingPassenger(), MiscPlayerProperties.class);
            if (properties != null) {
                properties.hasDismountedDragon = true;
            }
            this.getControllingPassenger().dismountRidingEntity();
        }
        if (this.isFlying() && !this.isHovering() && this.getControllingPassenger() != null && !this.onGround && Math.max(Math.abs(motionZ), Math.abs(motionX)) < 0.1F) {
            this.setHovering(true);
            this.setFlying(false);
        }
        if ((this.isFlying() || this.isHovering()) && this.isInWater()) {
            //this.motionY += 0.2;
        }
        if (this.isHovering() && !this.isFlying() && this.getControllingPassenger() != null && !this.onGround && Math.max(Math.abs(motionZ), Math.abs(motionX)) > 0.1F) {
            this.setFlying(true);
            this.setHovering(false);
        }
        if (this.spacebarTicks > 0) {
            this.spacebarTicks--;
        }
        if (this.spacebarTicks > 20 && this.getOwner() != null && this.getPassengers().contains(this.getOwner()) && !this.isFlying() && !this.isHovering()) {
            this.setHovering(true);
        }
        if (world.isRemote && !this.isModelDead()) {
            roll_buffer.calculateChainFlapBuffer(50, 10, 4, this);
            turn_buffer.calculateChainSwingBuffer(50, 0, 4, this);
            tail_buffer.calculateChainSwingBuffer(90, 10, 2.5F, this);

        }
        if (!world.isRemote
                && !this.isInWater()
                && !this.isSleeping()
                && this.onGround
                && !this.isFlying()
                && !this.isHovering()
                && this.getAttackTarget() == null
                && !this.isDaytime()
                && this.getRNG().nextInt(250) == 0
                && this.getAttackTarget() == null
                && this.getPassengers().isEmpty()) {
            this.setSleeping(true);
        }

        if (!world.isRemote
                && this.isSleeping()
                && (this.isFlying() || this.isHovering() || this.isInWater() || (this.world.canBlockSeeSky(new BlockPos(this))
                && this.isDaytime()
                && !this.isTamed() || this.isDaytime() && this.isTamed()) || this.getAttackTarget() != null || !this.getPassengers().isEmpty())) {
            this.setSleeping(false);
        }

        if (this.isSitting() && this.getControllingPassenger() != null) {
            this.setSitting(false);
        }
    }

    @Override
    public void setScaleForAge(boolean par1) {
        float scale = Math.min(this.getRenderSize() * 0.35F, 7F);
        this.setScale(scale);
        if (scale != lastScale) {
            resetParts(this.getRenderSize() / 3);
        }
        lastScale = scale;
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, @Nonnull IBlockState state, @Nonnull BlockPos pos) {
    }

    public float getRenderSize() {
        float step = (growth_stages[this.getDragonStage() - 1][1] - growth_stages[this.getDragonStage() - 1][0]) / 25;
        if (this.getAgeInDays() > 125) {
            return growth_stages[this.getDragonStage() - 1][0] + ((step * 25));
        }
        return growth_stages[this.getDragonStage() - 1][0] + ((step * this.getAgeFactor()));
    }

    private int getAgeFactor() {
        return (this.getDragonStage() > 1 ? this.getAgeInDays() - (25 * (this.getDragonStage() - 1)) : this.getAgeInDays());
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (this.isTackling()) {
            return false;
        }
        if (this.isModelDead()) {
            return false;
        }
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), ((int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));

        if (flag) {
            this.applyEnchantments(this, entityIn);
        }

        return flag;
    }

    @Override
    public void updateRidden() {
        Entity entity = this.getRidingEntity();
        if (entity != null && Utils.isEntityDead(entity)) {
            this.dismountRidingEntity();
        } else {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
            this.onUpdate();
            if (this.isRiding()) {
                this.updateRiding(entity);
            }
        }
    }

    private void updateRiding(Entity riding) {
        if (riding != null && riding.isPassenger(this) && riding instanceof EntityPlayer) {
            int i = riding.getPassengers().indexOf(this);
            float radius = (i == 2 ? 0F : 0.4F) + (((EntityPlayer) riding).isElytraFlying() ? 2 : 0);
            float angle = (0.01745329251F * ((EntityPlayer) riding).renderYawOffset) + (i == 1 ? -90 : i == 0 ? 90 : 0);
            double extraX = (double) (radius * MathHelper.sin((float) (Math.PI + angle)));
            double extraZ = (double) (radius * MathHelper.cos(angle));
            double extraY = (riding.isSneaking() ? 1.2D : 1.4D) + (i == 2 ? 0.4D : 0D);
            this.rotationYaw = ((EntityPlayer) riding).rotationYawHead;
            this.rotationYawHead = ((EntityPlayer) riding).rotationYawHead;
            this.prevRotationYaw = ((EntityPlayer) riding).rotationYawHead;
            this.setPosition(riding.posX + extraX, riding.posY + extraY, riding.posZ + extraZ);
            if (this.getControlState() == 1 << 4 || ((EntityPlayer) riding).isElytraFlying()) {
                this.dismountRidingEntity();
            }

        }
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
        if (this.isModelDead()) {
            return this.NO_ANIMATION;
        }
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        if (this.isModelDead()) {
            return;
        }
        currentAnimation = animation;
    }

    @Override
    public void playLivingSound() {
        if (!this.isSleeping() && !this.isModelDead() && !this.world.isRemote) {
            if (this.getAnimation() == this.NO_ANIMATION) {
                this.setAnimation(ANIMATION_SPEAK);
            }
            super.playLivingSound();
        }
    }

    @Override
    protected void playHurtSound(@Nonnull DamageSource source) {
        if (!this.isModelDead()) {
            if (this.getAnimation() == this.NO_ANIMATION && !this.world.isRemote) {
                this.setAnimation(ANIMATION_SPEAK);
            }
            super.playHurtSound(source);
        }
    }

    public void playEatSound() {
        this.playSound(SoundEvents.ENTITY_GENERIC_EAT, this.getSoundVolume(), this.getSoundPitch());
    }

    public void playCureSound() {
        this.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, this.getSoundVolume(), this.getSoundPitch());
    }

    public void playOwnerCommandSound() {
        this.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, this.getSoundVolume(), this.getSoundPitch());
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{IAnimatedEntity.NO_ANIMATION, EntityDragonBase.ANIMATION_EAT};
    }

    @Override
    public EntityAgeable createChild(@Nonnull EntityAgeable ageable) {
        return null;
    }

    @Override
    public boolean canMateWith(@Nonnull EntityAnimal otherAnimal) {
        if (otherAnimal instanceof EntityDragonBase && otherAnimal != this && otherAnimal.getClass() == this.getClass()) {
            EntityDragonBase dragon = (EntityDragonBase) otherAnimal;
            return (this.isMale() ^ dragon.isMale()) && isInLove() && dragon.isInLove();
        }
        return false;
    }

    public EntityDragonEgg createEgg(EntityDragonBase ageable) {
        int i = MathHelper.floor(this.posX);
        int j = MathHelper.floor(this.posY);
        int k = MathHelper.floor(this.posZ);
        BlockPos pos = new BlockPos(i, j, k);
        EntityDragonEgg dragon = new EntityDragonEgg(this.world);
        dragon.setType(EnumDragonEgg.byMetadata(new Random().nextInt(3) + (this.isFire ? 0 : 4)));
        dragon.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
        return dragon;
    }

    public boolean isTargetBlocked(Vec3d target) {
        if (target != null) {
            RayTraceResult rayTrace = world.rayTraceBlocks(new Vec3d(this.getPosition()), target, false);
            if (rayTrace != null && rayTrace.hitVec != null) {
                BlockPos pos = new BlockPos(rayTrace.hitVec);
                if (!world.isAirBlock(pos) || world.getBlockState(pos).getMaterial() == Material.WATER && !isFire) {
                    return true;
                }
                return rayTrace.typeOfHit != RayTraceResult.Type.BLOCK;
            }
        }
        return false;
    }

    private void flyAround() {
        if (airTarget != null) {
            if (!isTargetInAir() || flyTicks > 6000 || !this.isFlying()) {
                airTarget = null;
            }
            flyTowardsTarget();
        }
    }

    private void flyTowardsTarget() {
        if (airTarget != null && airTarget.getY() > IceAndFire.CONFIG.maxDragonFlight) {
            airTarget = new BlockPos(airTarget.getX(), IceAndFire.CONFIG.maxDragonFlight, airTarget.getZ());
        }
        if (airTarget != null && isTargetInAir() && this.isFlying() && this.getDistanceSquared(new Vec3d(airTarget.getX(), this.posY, airTarget.getZ())) > 3) {
            double y = this.attackDecision ? airTarget.getY() : this.posY;

            double targetX = airTarget.getX() + 0.5D - posX;
            double targetY = Math.min(y, 256) + 1D - posY;
            double targetZ = airTarget.getZ() + 0.5D - posZ;
            motionX += (Math.signum(targetX) * 0.5D - motionX) * 0.100000000372529 * getFlySpeed();
            motionY += (Math.signum(targetY) * 0.5D - motionY) * 0.100000000372529 * getFlySpeed();
            motionZ += (Math.signum(targetZ) * 0.5D - motionZ) * 0.100000000372529 * getFlySpeed();
            float angle = (float) (Math.atan2(motionZ, motionX) * 180.0D / Math.PI) - 90.0F;
            moveForward = 0.5F;
            double d0 = airTarget.getX() + 0.5D - this.posX;
            double d2 = airTarget.getZ() + 0.5D - this.posZ;
            double d1 = y + 0.5D - this.posY;
            double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
            float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
            float f1 = (float) (-(MathHelper.atan2(d1, d3) * (180D / Math.PI)));
            this.rotationPitch = this.updateRotation(this.rotationPitch, f1, 30F);
            this.rotationYaw = this.updateRotation(this.rotationYaw, f, 30F);

            if (!this.isFlying()) {
                this.setFlying(true);
            }
        } else {
            this.airTarget = null;
        }
        if (airTarget != null && isTargetInAir() && this.isFlying() && this.getDistanceSquared(new Vec3d(airTarget.getX(), this.posY, airTarget.getZ())) < 3 && this.doesWantToLand()) {
            this.setFlying(false);
            this.setHovering(true);
            this.flyHovering = 1;
        }
    }

    private double getFlySpeed() {
        return (2 + (this.getAgeInDays() / 125) * 2) * (this.isTackling() ? 2 : 1);
    }

    private boolean isTackling() {
        if (world.isRemote) {
            boolean tackling = this.dataManager.get(TACKLE);
            this.isTackling = tackling;
            return tackling;
        }
        return isTackling;
    }

    public boolean isAgingDisabled() {
        return this.dataManager.get(AGINGDISABLED);
    }

    private boolean isTargetInAir() {
        return airTarget != null
                && ((world.getBlockState(airTarget).getMaterial() == Material.AIR) || (this instanceof EntityIceDragon && (world.getBlockState(airTarget).getMaterial() == Material.WATER || world.getBlockState(airTarget).getMaterial() == Material.AIR)));
    }

    private float updateRotation(float angle, float targetAngle, float maxIncrease) {
        float f = MathHelper.wrapDegrees(targetAngle - angle);

        if (f > maxIncrease) {
            f = maxIncrease;
        }

        if (f < -maxIncrease) {
            f = -maxIncrease;
        }

        return angle + f;
    }


    @Override
    public boolean replaceItemInInventory(int inventorySlot, @Nullable ItemStack itemStackIn) {
        int j = inventorySlot - 500 + 2;
        if (j >= 0 && j < this.dragonInv.getSizeInventory()) {
            this.dragonInv.setInventorySlotContents(j, itemStackIn);
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing) {
        if (capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) itemHandler;
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(@Nonnull net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing) {
        return capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SideOnly(Side.CLIENT)
    protected void updateClientControls() {
        Minecraft mc = Minecraft.getMinecraft();
        if (this.isRidingPlayer(mc.player)) {
            byte previousState = getControlState();
            up(mc.gameSettings.keyBindJump.isKeyDown());
            down(ModKeys.dragon_down.isKeyDown());
            attack(ModKeys.dragon_fireAttack.isKeyDown());
            strike(ModKeys.dragon_strike.isKeyDown());
            dismount(mc.gameSettings.keyBindSneak.isKeyDown());
            byte controlState = getControlState();
            if (controlState != previousState) {
                IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonControl(this.getEntityId(), controlState, posX, posY, posZ));
            }
        }
        if (this.getRidingEntity() != null && this.getRidingEntity() == mc.player) {
            byte previousState = getControlState();
            dismount(mc.gameSettings.keyBindSneak.isKeyDown());
            byte controlState = getControlState();
            if (controlState != previousState) {
                IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonControl(this.getEntityId(), controlState, posX, posY, posZ));
            }
        }
    }

    @Override
    public boolean canBeSteered() {
        return true;
    }

    @Override
    public void travel(float strafe, float forward, float vertical) {
        if (this.getAnimation() == ANIMATION_SHAKEPREY || !this.canMove() && !this.isBeingRidden()) {
            strafe = 0;
            forward = 0;
            vertical = 0;
            super.travel(strafe, forward, vertical);
            return;
        }
        if (this.isBeingRidden() && this.canBeSteered()) {
            EntityLivingBase controller = (EntityLivingBase) this.getControllingPassenger();
            if (controller != null && controller != this.getAttackTarget()) {
                this.rotationYaw = controller.rotationYaw;
                this.prevRotationYaw = this.rotationYaw;
                this.rotationPitch = controller.rotationPitch * 0.5F;
                this.setRotation(this.rotationYaw, this.rotationPitch);
                this.renderYawOffset = this.rotationYaw;
                this.rotationYawHead = this.renderYawOffset;
                strafe = controller.moveStrafing * 0.5F;
                forward = controller.moveForward;
                if (forward <= 0.0F) {
                    forward *= 0.25F;
                }
                if (this.isFlying() || this.isHovering()) {
                    motionX *= 1.06;
                    motionZ *= 1.06;
                }
                jumpMovementFactor = 0.05F;
                this.setAIMoveSpeed(onGround ? (float) this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() : (float) getFlySpeed());
                super.travel(strafe, vertical = 0, forward);
                return;
            }
        }
        super.travel(strafe, forward, vertical);
    }

    private double getMaxAttackPlayerDistance() {
        return this.getEntityBoundingBox().getAverageEdgeLength() * 3;
    }

    @Override
    public boolean shouldDismountInWater(Entity rider) {
        return false;
    }

    @Override
    public void onDeath(@Nonnull DamageSource cause) {
        super.onDeath(cause);
        if (dragonInv != null && !this.world.isRemote) {
            for (int i = 0; i < dragonInv.getSizeInventory(); ++i) {
                ItemStack itemstack = dragonInv.getStackInSlot(i);
                if (!itemstack.isEmpty()) {
                    this.entityDropItem(itemstack, 0.0F);
                }
            }
        }
    }

    @Override
    public void onHearFlute(EntityPlayer player) {
        if (this.isTamed() && this.isOwner(player)) {
            if (this.isFlying() || this.isHovering()) {
                this.setFlying(false);
                this.setHovering(false);
            }
        }
    }

    private void roar() {
        if (EntityGorgon.isStoneMob(this)) {
            return;
        }
        if (this.getAnimation() != ANIMATION_ROAR) {
            this.setAnimation(ANIMATION_ROAR);
            this.playSound(this.getRoarSound(), this.getSoundVolume() + 2 + Math.max(0, this.getDragonStage() - 3), this.getSoundPitch());
        }
        if (isAdult()) {
            int size = (this.getDragonStage() - 3) * 30;
            List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(size, size, size));
            for (Entity entity : entities) {
                boolean isStrongerDragon = entity instanceof EntityDragonBase && ((EntityDragonBase) entity).getDragonStage() >= this.getDragonStage();
                if (entity instanceof EntityLivingBase && !isStrongerDragon) {
                    EntityLivingBase living = (EntityLivingBase) entity;
                    if (this.isOwner(living) || this.isOwnersPet(living)) {
                        living.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 30 * size));
                    } else {
                        living.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 30 * size));
                    }
                }
            }
        }
    }

    private boolean isOwnersPet(EntityLivingBase living) {
        return this.isTamed() && this.getOwner() != null && living instanceof EntityTameable && ((EntityTameable) living).getOwner() != null && this.getOwner().isEntityEqual(((EntityTameable) living).getOwner());
    }

    public boolean isChained() {
        ChainEntityProperties chainProperties = EntityPropertiesHandler.INSTANCE.getProperties(this, ChainEntityProperties.class);
        return chainProperties != null && chainProperties.isChained();
    }

    public boolean shouldRenderEyes() {
        return !this.isSleeping() && !this.isModelDead() && !this.isBlinking();
    }

    @Override
    public boolean shouldAnimalsFear(Entity entity) {
        return DragonUtils.canTameDragonAttack(this, entity);
    }

    @Override
    public void dropArmor() {
        if (dragonInv != null && !this.world.isRemote) {
            for (int i = 0; i < dragonInv.getSizeInventory(); ++i) {
                ItemStack itemstack = dragonInv.getStackInSlot(i);
                if (!itemstack.isEmpty()) {
                    this.entityDropItem(itemstack, 0.0F);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldRender(ICamera camera) {
        return isBoundingBoxInFrustum(camera, headPart)
                || isBoundingBoxInFrustum(camera, neckPart)
                || isBoundingBoxInFrustum(camera, leftWingLowerPart)
                || isBoundingBoxInFrustum(camera, rightWingLowerPart)
                || isBoundingBoxInFrustum(camera, rightWingUpperPart)
                || isBoundingBoxInFrustum(camera, rightWingLowerPart)
                || isBoundingBoxInFrustum(camera, tail1Part)
                || isBoundingBoxInFrustum(camera, tail2Part)
                || isBoundingBoxInFrustum(camera, tail3Part)
                || isBoundingBoxInFrustum(camera, tail4Part);
    }

    public boolean isStoned() {
        StoneEntityProperties properties = EntityPropertiesHandler.INSTANCE.getProperties(this, StoneEntityProperties.class);
        return properties != null && properties.isStone;
    }

    private boolean isDaytime() {
        return this.world.isDaytime();
    }

    private boolean isBeyondHeight() {
        if (this.posY > this.world.getHeight()) {
            return true;
        }
        return this.posY > IceAndFire.CONFIG.maxDragonFlight;
    }

    protected float getDistanceSquared(Vec3d vec3d) {
        float f = (float) (this.posX - vec3d.x);
        float f1 = (float) (this.posY - vec3d.y);
        float f2 = (float) (this.posZ - vec3d.z);
        return f * f + f1 * f1 + f2 * f2;
    }

    public boolean isDirectPathBetweenPoints(Vec3d vec1, Vec3d vec2) {
        RayTraceResult movingobjectposition = this.world.rayTraceBlocks(vec1, new Vec3d(vec2.x, vec2.y + (double) this.height * 0.5D, vec2.z), false, true, false);
        return movingobjectposition == null || movingobjectposition.typeOfHit != RayTraceResult.Type.BLOCK;
    }

    protected abstract void breathFireAtPos(BlockPos burningTarget);

    public abstract ResourceLocation getDeadLootTable();

    public abstract String getVariantName(int variant);

    public abstract Item getVariantScale(int variant);

    public abstract Item getVariantEgg(int variant);

    public abstract SoundEvent getRoarSound();

    public static int getIntFromArmor(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() == ModItems.dragon_armor_iron) {
            return 1;
        }
        if (!stack.isEmpty() && stack.getItem() == ModItems.dragon_armor_gold) {
            return 2;
        }
        if (!stack.isEmpty() && stack.getItem() == ModItems.dragon_armor_diamond) {
            return 3;
        }
        return 0;
    }

    public static boolean isDirectPathBetweenPoints(Entity entity, Vec3d vec1, Vec3d vec2) {
        RayTraceResult movingobjectposition = entity.world.rayTraceBlocks(vec1, new Vec3d(vec2.x, vec2.y + (double) entity.height * 0.5D, vec2.z), false, true, false);
        return movingobjectposition == null || movingobjectposition.typeOfHit != RayTraceResult.Type.BLOCK;
    }

    private static boolean isBoundingBoxInFrustum(ICamera camera, EntityDragonPart entityDragonPart) {
        return entityDragonPart != null && camera.isBoundingBoxInFrustum(entityDragonPart.getEntityBoundingBox());
    }
}