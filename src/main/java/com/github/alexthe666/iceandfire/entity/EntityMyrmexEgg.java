package com.github.alexthe666.iceandfire.entity;

import java.util.UUID;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.util.IBlacklistedFromStatues;
import com.github.alexthe666.iceandfire.entity.util.IDeadMob;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;
import com.github.alexthe666.iceandfire.world.MyrmexWorldData;
import com.github.alexthe666.iceandfire.world.gen.WorldGenMyrmexHive;
import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.HandSide;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityMyrmexEgg extends LivingEntity implements IBlacklistedFromStatues, IDeadMob {

    private static final DataParameter<Boolean> MYRMEX_TYPE = EntityDataManager.createKey(EntityMyrmexEgg.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> MYRMEX_AGE = EntityDataManager.createKey(EntityMyrmexEgg.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> MYRMEX_CASTE = EntityDataManager.createKey(EntityMyrmexEgg.class, DataSerializers.VARINT);
    public UUID hiveUUID;

    public EntityMyrmexEgg(EntityType t, World worldIn) {
        super(t, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute bakeAttributes() {
        return MobEntity.func_233666_p_()
                //HEALTH
                .createMutableAttribute(Attributes.MAX_HEALTH, 10.0D)
                //SPEED
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.0D);
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        tag.putBoolean("Jungle", this.isJungle());
        tag.putInt("MyrmexAge", this.getMyrmexAge());
        tag.putInt("MyrmexCaste", this.getMyrmexCaste());
        tag.putUniqueId("HiveUUID", hiveUUID == null ? hiveUUID = UUID.randomUUID() : hiveUUID);
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        this.setJungle(tag.getBoolean("Jungle"));
        this.setMyrmexAge(tag.getInt("MyrmexAge"));
        this.setMyrmexCaste(tag.getInt("MyrmexCaste"));
        hiveUUID = tag.getUniqueId("HiveUUID");
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.getDataManager().register(MYRMEX_TYPE, false);
        this.getDataManager().register(MYRMEX_AGE, Integer.valueOf(0));
        this.getDataManager().register(MYRMEX_CASTE, Integer.valueOf(0));
    }


    public boolean isJungle() {
        return this.getDataManager().get(MYRMEX_TYPE).booleanValue();
    }

    public void setJungle(boolean jungle) {
        this.getDataManager().set(MYRMEX_TYPE, jungle);
    }

    public int getMyrmexAge() {
        return this.getDataManager().get(MYRMEX_AGE).intValue();
    }

    public void setMyrmexAge(int i) {
        this.getDataManager().set(MYRMEX_AGE, i);
    }

    public int getMyrmexCaste() {
        return this.getDataManager().get(MYRMEX_CASTE).intValue();
    }

    public void setMyrmexCaste(int i) {
        this.getDataManager().set(MYRMEX_CASTE, i);
    }

    public boolean canSeeSky() {
        return world.canBlockSeeSky(this.getPosition());
    }

    @Override
    public void tick() {
        super.tick();
        if (!canSeeSky()) {
            this.setMyrmexAge(this.getMyrmexAge() + 1);
        }
        if (this.getMyrmexAge() > IafConfig.myrmexEggTicks) {
            this.remove();
            EntityMyrmexBase myrmex;
            switch (this.getMyrmexCaste()) {
                default:
                    myrmex = new EntityMyrmexWorker(IafEntityRegistry.MYRMEX_WORKER, world);
                    break;
                case 1:
                    myrmex = new EntityMyrmexSoldier(IafEntityRegistry.MYRMEX_SOLDIER, world);
                    break;
                case 2:
                    myrmex = new EntityMyrmexRoyal(IafEntityRegistry.MYRMEX_ROYAL, world);
                    break;
                case 3:
                    myrmex = new EntityMyrmexSentinel(IafEntityRegistry.MYRMEX_SENTINEL, world);
                    break;
                case 4:
                    myrmex = new EntityMyrmexQueen(IafEntityRegistry.MYRMEX_QUEEN, world);
                    break;
            }
            myrmex.setJungleVariant(this.isJungle());
            myrmex.setGrowthStage(0);
            myrmex.setPositionAndRotation(this.getPosX(), this.getPosY(), this.getPosZ(), 0, 0);
            if (myrmex instanceof EntityMyrmexQueen) {
                MyrmexHive hive = new MyrmexHive(world, this.getPosition(), 100);
                PlayerEntity player = world.getClosestPlayer(this, 30);
                if (player != null) {
                    hive.hasOwner = true;
                    hive.ownerUUID = player.getUniqueID();
                    if (!world.isRemote) {
                        hive.modifyPlayerReputation(player.getUniqueID(), 100);
                    }
                }
                MyrmexWorldData.addHive(world, hive);
                myrmex.setHive(hive);


            } else {
                if(MyrmexWorldData.get(world) != null) {
                    MyrmexHive hive;
                    if(this.hiveUUID == null){
                        hive = MyrmexWorldData.get(world).getNearestHive(this.getPosition(), 400);
                    }else {
                        hive = MyrmexWorldData.get(world).getHiveFromUUID(hiveUUID);
                    }
                    if (!world.isRemote && hive != null && Math.sqrt(this.getDistanceSq(hive.getCenter().getX(), hive.getCenter().getY(), hive.getCenter().getZ())) < 2000) {
                        myrmex.setHive(hive);
                    }
                }
            }

            if (!world.isRemote) {
                world.addEntity(myrmex);
            }
            this.world.playSound(this.getPosX(), this.getPosY() + this.getEyeHeight(), this.getPosZ(), IafSoundRegistry.EGG_HATCH, this.getSoundCategory(), 2.5F, 1.0F, false);
        }
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return null;
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return ImmutableList.of();
    }

    @Override
    public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {

    }

    @Override
    public boolean attackEntityFrom(DamageSource dmg, float var2) {
        if (dmg == DamageSource.IN_WALL || dmg == DamageSource.FALL) {
            return false;
        }
        if (!world.isRemote && !dmg.canHarmInCreative()) {
            this.entityDropItem(this.getItem(), 0);
        }
        this.remove();
        return super.attackEntityFrom(dmg, var2);
    }

    private ItemStack getItem() {
        ItemStack egg = new ItemStack(this.isJungle() ? IafItemRegistry.MYRMEX_JUNGLE_EGG : IafItemRegistry.MYRMEX_DESERT_EGG, 1);
        CompoundNBT newTag = new CompoundNBT();
        newTag.putInt("EggOrdinal", this.getMyrmexCaste());
        egg.setTag(newTag);
        return egg;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public HandSide getPrimaryHand() {
        return null;
    }

    @Override
    protected void collideWithEntity(Entity entity) {
    }

    @Override
    public boolean canBeTurnedToStone() {
        return false;
    }

    public void onPlayerPlace(PlayerEntity player) {
    }

    @Override
    public boolean isMobDead() {
        return true;
    }

    public boolean isInNursery() {
        MyrmexHive hive = MyrmexWorldData.get(this.world).getNearestHive(this.getPosition(), 100);
        if (hive != null && hive.getRooms(WorldGenMyrmexHive.RoomType.NURSERY).isEmpty() && hive.getRandomRoom(WorldGenMyrmexHive.RoomType.NURSERY, this.getRNG(), this.getPosition()) != null) {
            return false;
        }
        if (hive != null) {
            BlockPos nursery = hive.getRandomRoom(WorldGenMyrmexHive.RoomType.NURSERY, this.getRNG(), this.getPosition());
            return this.getDistanceSq(nursery.getX(), nursery.getY(), nursery.getZ()) < 2025;
        }
        return false;
    }
}
