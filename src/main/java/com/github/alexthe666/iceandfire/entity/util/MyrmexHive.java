package com.github.alexthe666.iceandfire.entity.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexQueen;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.world.gen.WorldGenMyrmexHive;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class MyrmexHive {
    private final List<BlockPos> foodRooms = Lists.newArrayList();
    private final List<BlockPos> babyRooms = Lists.newArrayList();
    private final List<BlockPos> miscRooms = Lists.newArrayList();
    private final List<BlockPos> allRooms = Lists.newArrayList();
    private final Map<BlockPos, Direction> entrances = Maps.newHashMap();
    private final Map<BlockPos, Direction> entranceBottoms = Maps.newHashMap();
    private final Map<UUID, Integer> playerReputation = Maps.newHashMap();
    private final List<HiveAggressor> villageAgressors = Lists.newArrayList();
    private final List<UUID> myrmexList = Lists.newArrayList();
    public UUID hiveUUID;
    public String colonyName = "";
    public boolean reproduces = true;
    public boolean hasOwner = false;
    public UUID ownerUUID = null;
    private World world;
    private BlockPos centerHelper = BlockPos.ZERO;
    private BlockPos center = BlockPos.ZERO;
    private int villageRadius;
    private int lastAddDoorTimestamp;
    private int tickCounter;
    private int numMyrmex;
    private int noBreedTicks;
    private int wanderRadius = 16;

    public MyrmexHive() {
        this.hiveUUID = UUID.randomUUID();
    }

    public MyrmexHive(World worldIn) {
        this.world = worldIn;
        this.hiveUUID = UUID.randomUUID();
    }

    public MyrmexHive(World worldIn, BlockPos center, int radius) {
        this.world = worldIn;
        this.center = center;
        this.villageRadius = radius;
        this.hiveUUID = UUID.randomUUID();
    }

    public static BlockPos getGroundedPos(IWorld world, BlockPos pos) {
        BlockPos current = pos;
        while (world.isAirBlock(current.down()) && current.getY() > 0) {
            current = current.down();
        }
        return current;
    }

    public static MyrmexHive fromNBT(CompoundNBT hive) {
        MyrmexHive hive1 = new MyrmexHive();
        hive1.readVillageDataFromNBT(hive);
        return hive1;
    }

    public CompoundNBT toNBT() {
        CompoundNBT tag = new CompoundNBT();
        this.writeVillageDataToNBT(tag);
        return tag;
    }

    public void setWorld(World worldIn) {
        this.world = worldIn;
    }

    public void tick(int tickCounterIn, World world) {
        this.tickCounter++;
        this.removeDeadAndOldAgressors();
        if (tickCounter % 20 == 0) {
            this.updateNumMyrmex(world);
        }
    }

    private void updateNumMyrmex(World world) {
        this.numMyrmex = this.myrmexList.size();
        if (this.numMyrmex == 0) {
            this.playerReputation.clear();
        }
    }

    @Nullable
    public EntityMyrmexQueen getQueen() {
        List<EntityMyrmexQueen> ourQueens = new ArrayList<>();
        if (!world.isRemote) {
            ServerWorld serverWorld = world.getServer().getWorld(world.getDimensionKey());
            List<Entity> allQueens = serverWorld.getEntities(IafEntityRegistry.MYRMEX_QUEEN, EntityPredicates.NOT_SPECTATING);
            for (Entity queen : allQueens) {
                if (queen instanceof EntityMyrmexQueen && ((EntityMyrmexQueen) queen).getHive().equals(this)) {
                    ourQueens.add(((EntityMyrmexQueen) queen));
                }
            }
        }

        return ourQueens.isEmpty() ? null : ourQueens.get(0);
    }

    public BlockPos getCenter() {
        return this.center;
    }

    public BlockPos getCenterGround() {
        return getGroundedPos(this.world, this.center);
    }

    public int getVillageRadius() {
        return this.villageRadius;
    }

    public int getNumMyrmex() {
        return this.numMyrmex;
    }

    public int getWanderRadius(){
        return this.wanderRadius;
    }
    public void setWanderRadius(int wanderRadius){
        this.wanderRadius = Math.min(wanderRadius,IafConfig.myrmexMaximumWanderRadius);
    }

    public boolean isBlockPosWithinSqVillageRadius(BlockPos pos) {
        return this.center.distanceSq(pos) < (double) (this.villageRadius * this.villageRadius);
    }

    public boolean isAnnihilated() {
        return false;
    }

    public void addOrRenewAgressor(LivingEntity LivingEntityIn, int agressiveLevel) {
        for (HiveAggressor hive$villageaggressor : this.villageAgressors) {
            if (hive$villageaggressor.agressor == LivingEntityIn) {
                hive$villageaggressor.agressionTime = this.tickCounter;
                return;
            }
        }

        this.villageAgressors.add(new HiveAggressor(LivingEntityIn, this.tickCounter, agressiveLevel));
    }

    @Nullable
    public LivingEntity findNearestVillageAggressor(LivingEntity LivingEntityIn) {
        double d0 = Double.MAX_VALUE;
        int previousAgressionLevel = 0;
        HiveAggressor hive$villageaggressor = null;
        for (int i = 0; i < this.villageAgressors.size(); ++i) {
            HiveAggressor hive$villageaggressor1 = this.villageAgressors.get(i);
            double d1 = hive$villageaggressor1.agressor.getDistanceSq(LivingEntityIn);
            int agressionLevel = hive$villageaggressor1.agressionLevel;

            if (d1 <= d0 || agressionLevel > previousAgressionLevel) {
                hive$villageaggressor = hive$villageaggressor1;
                d0 = d1;
            }
            previousAgressionLevel = agressionLevel;
        }

        return hive$villageaggressor == null ? null : hive$villageaggressor.agressor;
    }

    public PlayerEntity getNearestTargetPlayer(LivingEntity villageDefender, World world) {
        double d0 = Double.MAX_VALUE;
        PlayerEntity PlayerEntity = null;

        for (UUID s : this.playerReputation.keySet()) {
            if (this.isPlayerReputationLowEnoughToFight(s)) {
                PlayerEntity PlayerEntity1 = world.getPlayerByUuid(s);

                if (PlayerEntity1 != null) {
                    double d1 = PlayerEntity1.getDistanceSq(villageDefender);

                    if (d1 <= d0) {
                        PlayerEntity = PlayerEntity1;
                        d0 = d1;
                    }
                }
            }
        }

        return PlayerEntity;
    }

    private void removeDeadAndOldAgressors() {
        Iterator<HiveAggressor> iterator = this.villageAgressors.iterator();

        while (iterator.hasNext()) {
            HiveAggressor hive$villageaggressor = iterator.next();

            if (!hive$villageaggressor.agressor.isAlive() || Math.abs(this.tickCounter - hive$villageaggressor.agressionTime) > 300) {
                iterator.remove();
            }
        }
    }

    public int getPlayerReputation(UUID playerName) {
        Integer integer = this.playerReputation.get(playerName);
        return integer == null ? 0 : integer.intValue();
    }

    private UUID findUUID(String name) {
        if (this.world == null || this.world.getServer() == null)
            return PlayerEntity.getOfflineUUID(name);
        GameProfile profile = this.world.getServer().getPlayerProfileCache().getGameProfileForUsername(name);
        return profile == null ? PlayerEntity.getOfflineUUID(name) : profile.getId();
    }

    public int modifyPlayerReputation(UUID playerName, int reputation) {
        int i = this.getPlayerReputation(playerName);
        int j = MathHelper.clamp(i + reputation, 0, 100);
        if (this.hasOwner && playerName.equals(ownerUUID)) {
            j = 100;
        }
        PlayerEntity player = null;
        try {
            player = world.getPlayerByUuid(playerName);
        } catch (Exception e) {
            IceAndFire.LOGGER.warn("Myrmex Hive could not find player with associated UUID");
        }
        if (player != null) {
            if (j - i != 0) {
                player.sendStatusMessage(new TranslationTextComponent(j - i >= 0 ? "myrmex.message.raised_reputation" : "myrmex.message.lowered_reputation", Math.abs(j - i), j), true);
            }
            if (i < 25 && j >= 25) {
                player.sendStatusMessage(new TranslationTextComponent("myrmex.message.peaceful"), false);
            }
            if (i >= 25 && j < 25) {
                player.sendStatusMessage(new TranslationTextComponent("myrmex.message.hostile"), false);
            }
            if (i < 50 && j >= 50) {
                player.sendStatusMessage(new TranslationTextComponent("myrmex.message.trade"), false);
            }
            if (i >= 50 && j < 50) {
                player.sendStatusMessage(new TranslationTextComponent("myrmex.message.no_trade"), false);
            }
            if (i < 75 && j >= 75) {
                player.sendStatusMessage(new TranslationTextComponent("myrmex.message.can_use_staff"), false);
            }
            if (i >= 75 && j < 75) {
                player.sendStatusMessage(new TranslationTextComponent("myrmex.message.cant_use_staff"), false);
            }
        }

        this.playerReputation.put(playerName, Integer.valueOf(j));
        return j;
    }

    public boolean isPlayerReputationTooLowToTrade(UUID uuid) {
        return this.getPlayerReputation(uuid) < 50;
    }

    public boolean canPlayerCommandHive(UUID uuid) {
        return this.getPlayerReputation(uuid) >= 75;
    }

    public boolean isPlayerReputationLowEnoughToFight(UUID uuid) {
        return this.getPlayerReputation(uuid) < 25;
    }

    /**
     * Read this village's data from NBT.
     */
    public void readVillageDataFromNBT(CompoundNBT compound) {
        this.numMyrmex = compound.getInt("PopSize");
        this.reproduces = compound.getBoolean("Reproduces");
        this.hasOwner = compound.getBoolean("HasOwner");
        if(compound.hasUniqueId("OwnerUUID")){
            this.ownerUUID = compound.getUniqueId("OwnerUUID");
        }
        this.colonyName = compound.getString("ColonyName");
        this.villageRadius = compound.getInt("Radius");
        if (compound.hasUniqueId("WanderRadius")) {
            this.wanderRadius = compound.getInt("WanderRadius");
        }
        this.lastAddDoorTimestamp = compound.getInt("Stable");
        this.tickCounter = compound.getInt("Tick");
        this.noBreedTicks = compound.getInt("MTick");
        this.center = new BlockPos(compound.getInt("CX"), compound.getInt("CY"), compound.getInt("CZ"));
        this.centerHelper = new BlockPos(compound.getInt("ACX"), compound.getInt("ACY"), compound.getInt("ACZ"));
        ListNBT hiveMembers = compound.getList("HiveMembers", 10);
        this.myrmexList.clear();
        for (int i = 0; i < hiveMembers.size(); ++i) {
            CompoundNBT CompoundNBT = hiveMembers.getCompound(i);
            this.myrmexList.add(CompoundNBT.getUniqueId("MyrmexUUID"));
        }
        ListNBT foodRoomList = compound.getList("FoodRooms", 10);
        this.foodRooms.clear();
        for (int i = 0; i < foodRoomList.size(); ++i) {
            CompoundNBT CompoundNBT = foodRoomList.getCompound(i);
            this.foodRooms.add(new BlockPos(CompoundNBT.getInt("X"), CompoundNBT.getInt("Y"), CompoundNBT.getInt("Z")));
        }
        ListNBT babyRoomList = compound.getList("BabyRooms", 10);
        this.babyRooms.clear();
        for (int i = 0; i < babyRoomList.size(); ++i) {
            CompoundNBT CompoundNBT = babyRoomList.getCompound(i);
            this.babyRooms.add(new BlockPos(CompoundNBT.getInt("X"), CompoundNBT.getInt("Y"), CompoundNBT.getInt("Z")));
        }
        ListNBT miscRoomList = compound.getList("MiscRooms", 10);
        this.miscRooms.clear();
        for (int i = 0; i < miscRoomList.size(); ++i) {
            CompoundNBT CompoundNBT = miscRoomList.getCompound(i);
            this.miscRooms.add(new BlockPos(CompoundNBT.getInt("X"), CompoundNBT.getInt("Y"), CompoundNBT.getInt("Z")));
        }
        ListNBT entrancesList = compound.getList("Entrances", 10);
        this.entrances.clear();
        for (int i = 0; i < entrancesList.size(); ++i) {
            CompoundNBT CompoundNBT = entrancesList.getCompound(i);
            this.entrances.put(new BlockPos(CompoundNBT.getInt("X"), CompoundNBT.getInt("Y"), CompoundNBT.getInt("Z")), Direction.byHorizontalIndex(CompoundNBT.getInt("Facing")));
        }

        ListNBT entranceBottomsList = compound.getList("EntranceBottoms", 10);
        this.entranceBottoms.clear();
        for (int i = 0; i < entranceBottomsList.size(); ++i) {
            CompoundNBT CompoundNBT = entranceBottomsList.getCompound(i);
            this.entranceBottoms.put(new BlockPos(CompoundNBT.getInt("X"), CompoundNBT.getInt("Y"), CompoundNBT.getInt("Z")), Direction.byHorizontalIndex(CompoundNBT.getInt("Facing")));
        }
        hiveUUID = compound.getUniqueId("HiveUUID");
        ListNBT nbttaglist1 = compound.getList("Players", 10);
        this.playerReputation.clear();
        for (int j = 0; j < nbttaglist1.size(); ++j) {
            CompoundNBT CompoundNBT1 = nbttaglist1.getCompound(j);

            if (CompoundNBT1.hasUniqueId("UUID")) {
                this.playerReputation.put(CompoundNBT1.getUniqueId("UUID"), Integer.valueOf(CompoundNBT1.getInt("S")));
            } else {
                //World is never set here, so this will always be offline UUIDs, sadly there is no way to convert this.
                this.playerReputation.put(findUUID(CompoundNBT1.getString("Name")), Integer.valueOf(CompoundNBT1.getInt("S")));
            }
        }
    }

    /**
     * Write this village's data to NBT.
     */
    public void writeVillageDataToNBT(CompoundNBT compound) {
        compound.putInt("PopSize", this.numMyrmex);
        compound.putBoolean("Reproduces", this.reproduces);
        compound.putBoolean("HasOwner", this.hasOwner);
        if (this.ownerUUID != null) {
            compound.putUniqueId("OwnerUUID", this.ownerUUID);
        }
        compound.putString("ColonyName", this.colonyName);
        compound.putInt("Radius", this.villageRadius);
        compound.putInt("WanderRadius",this.wanderRadius);
        compound.putInt("Stable", this.lastAddDoorTimestamp);
        compound.putInt("Tick", this.tickCounter);
        compound.putInt("MTick", this.noBreedTicks);
        compound.putInt("CX", this.center.getX());
        compound.putInt("CY", this.center.getY());
        compound.putInt("CZ", this.center.getZ());
        compound.putInt("ACX", this.centerHelper.getX());
        compound.putInt("ACY", this.centerHelper.getY());
        compound.putInt("ACZ", this.centerHelper.getZ());
        ListNBT hiveMembers = new ListNBT();
        for (UUID memberUUID : this.myrmexList) {
            CompoundNBT CompoundNBT = new CompoundNBT();
            CompoundNBT.putUniqueId("MyrmexUUID", memberUUID);
            hiveMembers.add(CompoundNBT);
        }
        compound.put("HiveMembers", hiveMembers);
        ListNBT foodRoomList = new ListNBT();
        for (BlockPos pos : this.foodRooms) {
            CompoundNBT CompoundNBT = new CompoundNBT();
            CompoundNBT.putInt("X", pos.getX());
            CompoundNBT.putInt("Y", pos.getY());
            CompoundNBT.putInt("Z", pos.getZ());
            foodRoomList.add(CompoundNBT);
        }
        compound.put("FoodRooms", foodRoomList);
        ListNBT babyRoomList = new ListNBT();
        for (BlockPos pos : this.babyRooms) {
            CompoundNBT CompoundNBT = new CompoundNBT();
            CompoundNBT.putInt("X", pos.getX());
            CompoundNBT.putInt("Y", pos.getY());
            CompoundNBT.putInt("Z", pos.getZ());
            babyRoomList.add(CompoundNBT);
        }
        compound.put("BabyRooms", babyRoomList);
        ListNBT miscRoomList = new ListNBT();
        for (BlockPos pos : this.miscRooms) {
            CompoundNBT CompoundNBT = new CompoundNBT();
            CompoundNBT.putInt("X", pos.getX());
            CompoundNBT.putInt("Y", pos.getY());
            CompoundNBT.putInt("Z", pos.getZ());
            miscRoomList.add(CompoundNBT);
        }
        compound.put("MiscRooms", miscRoomList);
        ListNBT entrancesList = new ListNBT();
        for (Map.Entry<BlockPos, Direction> entry : this.entrances.entrySet()) {
            CompoundNBT CompoundNBT = new CompoundNBT();
            CompoundNBT.putInt("X", entry.getKey().getX());
            CompoundNBT.putInt("Y", entry.getKey().getY());
            CompoundNBT.putInt("Z", entry.getKey().getZ());
            CompoundNBT.putInt("Facing", entry.getValue().getHorizontalIndex());
            entrancesList.add(CompoundNBT);
        }
        compound.put("Entrances", entrancesList);

        ListNBT entranceBottomsList = new ListNBT();
        for (Map.Entry<BlockPos, Direction> entry : this.entranceBottoms.entrySet()) {
            CompoundNBT CompoundNBT = new CompoundNBT();
            CompoundNBT.putInt("X", entry.getKey().getX());
            CompoundNBT.putInt("Y", entry.getKey().getY());
            CompoundNBT.putInt("Z", entry.getKey().getZ());
            CompoundNBT.putInt("Facing", entry.getValue().getHorizontalIndex());
            entranceBottomsList.add(CompoundNBT);
        }
        compound.put("EntranceBottoms", entranceBottomsList);
        compound.putUniqueId("HiveUUID", this.hiveUUID);
        ListNBT nbttaglist1 = new ListNBT();

        for (UUID s : this.playerReputation.keySet()) {
            CompoundNBT CompoundNBT1 = new CompoundNBT();

            try {
                {
                    CompoundNBT1.putUniqueId("UUID", s);
                    CompoundNBT1.putInt("S", this.playerReputation.get(s).intValue());
                    nbttaglist1.add(CompoundNBT1);
                }
            } catch (RuntimeException var9) {
            }
        }

        compound.put("Players", nbttaglist1);
    }

    public void addRoom(BlockPos center, WorldGenMyrmexHive.RoomType roomType) {
        if (roomType == WorldGenMyrmexHive.RoomType.FOOD && !this.foodRooms.contains(center)) {
            this.foodRooms.add(center);
        } else if (roomType == WorldGenMyrmexHive.RoomType.NURSERY && !this.babyRooms.contains(center)) {
            this.babyRooms.add(center);
        } else if (!this.miscRooms.contains(center) && !this.miscRooms.contains(center)) {
            this.miscRooms.add(center);
        }
        this.allRooms.add(center);
    }

    public void addRoomWithMessage(PlayerEntity player, BlockPos center, WorldGenMyrmexHive.RoomType roomType) {
        List<BlockPos> allCurrentRooms = new ArrayList<>(getAllRooms());
        allCurrentRooms.addAll(this.getEntrances().keySet());
        allCurrentRooms.addAll(this.getEntranceBottoms().keySet());
        if (roomType == WorldGenMyrmexHive.RoomType.FOOD) {
            if (!this.foodRooms.contains(center) && !allCurrentRooms.contains(center)) {
                this.foodRooms.add(center);
                player.sendStatusMessage(new TranslationTextComponent("myrmex.message.added_food_room", center.getX(), center.getY(), center.getZ()), false);
            } else {
                player.sendStatusMessage(new TranslationTextComponent("myrmex.message.dupe_room", center.getX(), center.getY(), center.getZ()), false);

            }
        } else if (roomType == WorldGenMyrmexHive.RoomType.NURSERY) {
            if (!this.babyRooms.contains(center) && !allCurrentRooms.contains(center)) {
                this.babyRooms.add(center);
                player.sendStatusMessage(new TranslationTextComponent("myrmex.message.added_nursery_room", center.getX(), center.getY(), center.getZ()), false);
            } else {
                player.sendStatusMessage(new TranslationTextComponent("myrmex.message.dupe_room", center.getX(), center.getY(), center.getZ()), false);
            }
        } else if (!this.miscRooms.contains(center) && !allCurrentRooms.contains(center)) {
            this.miscRooms.add(center);
            player.sendStatusMessage(new TranslationTextComponent("myrmex.message.added_misc_room", center.getX(), center.getY(), center.getZ()), false);
        } else {
            player.sendStatusMessage(new TranslationTextComponent("myrmex.message.dupe_room", center.getX(), center.getY(), center.getZ()), false);
        }
    }

    public void addEnteranceWithMessage(PlayerEntity player, boolean bottom, BlockPos center, Direction facing) {
        List<BlockPos> allCurrentRooms = new ArrayList<>(getAllRooms());
        allCurrentRooms.addAll(this.getEntrances().keySet());
        allCurrentRooms.addAll(this.getEntranceBottoms().keySet());
        if (bottom) {
            if (allCurrentRooms.contains(center)) {
                player.sendStatusMessage(new TranslationTextComponent("myrmex.message.dupe_room", center.getX(), center.getY(), center.getZ()), false);
            } else {
                this.getEntranceBottoms().put(center, facing);
                player.sendStatusMessage(new TranslationTextComponent("myrmex.message.added_enterance_bottom", center.getX(), center.getY(), center.getZ()), false);
            }
        } else {
            if (allCurrentRooms.contains(center)) {
                player.sendStatusMessage(new TranslationTextComponent("myrmex.message.dupe_room", center.getX(), center.getY(), center.getZ()), false);
            } else {
                this.getEntrances().put(center, facing);
                player.sendStatusMessage(new TranslationTextComponent("myrmex.message.added_enterance_surface", center.getX(), center.getY(), center.getZ()), false);
            }
        }
    }

    public List<BlockPos> getRooms(WorldGenMyrmexHive.RoomType roomType) {
        if (roomType == WorldGenMyrmexHive.RoomType.FOOD) {
            return foodRooms;
        } else if (roomType == WorldGenMyrmexHive.RoomType.NURSERY) {
            return babyRooms;
        } else {
            return miscRooms;
        }
    }

    public List<BlockPos> getAllRooms() {
        allRooms.clear();
        allRooms.add(center);
        allRooms.addAll(foodRooms);
        allRooms.addAll(babyRooms);
        allRooms.addAll(miscRooms);
        return allRooms;
    }

    public BlockPos getRandomRoom(Random random, BlockPos returnPos) {
        List<BlockPos> rooms = getAllRooms();
        return rooms.isEmpty() ? returnPos : rooms.get(random.nextInt(Math.max(rooms.size() - 1, 1)));
    }

    public BlockPos getRandomRoom(WorldGenMyrmexHive.RoomType roomType, Random random, BlockPos returnPos) {
        List<BlockPos> rooms = getRooms(roomType);
        return rooms.isEmpty() ? returnPos : rooms.get(random.nextInt(Math.max(rooms.size() - 1, 1)));
    }

    public BlockPos getClosestEntranceToEntity(Entity entity, Random random, boolean randomize) {
        Map.Entry<BlockPos, Direction> closest = getClosestEntrance(entity);
        if (closest != null) {
            if (randomize) {
                BlockPos pos = closest.getKey().offset(closest.getValue(), random.nextInt(7) + 7).up(4);
                return pos.add(10 - random.nextInt(20), 0, 10 - random.nextInt(20));
            } else {
                return closest.getKey().offset(closest.getValue(), 3);
            }
        }
        return entity.getPosition();
    }

    public BlockPos getClosestEntranceBottomToEntity(Entity entity, Random random) {
        Map.Entry<BlockPos, Direction> closest = null;
        for (Map.Entry<BlockPos, Direction> entry : this.entranceBottoms.entrySet()) {
            if (closest == null || closest.getKey().distanceSq(entity.getPosX(), entity.getPosY(), entity.getPosZ(), false) > entry.getKey().distanceSq(entity.getPosX(), entity.getPosY(), entity.getPosZ(), false)) {
                closest = entry;
            }
        }
        return closest != null ? closest.getKey() : entity.getPosition();
    }

    public PlayerEntity getOwner(World world) {
        if (hasOwner) {
            return world.getPlayerByUuid(ownerUUID);
        }
        return null;
    }

    public Map<BlockPos, Direction> getEntrances() {
        return entrances;
    }

    public Map<BlockPos, Direction> getEntranceBottoms() {
        return entranceBottoms;
    }

    private Map.Entry<BlockPos, Direction> getClosestEntrance(Entity entity) {
        Map.Entry<BlockPos, Direction> closest = null;
        for (Map.Entry<BlockPos, Direction> entry : this.entrances.entrySet()) {
            if (closest == null || closest.getKey().distanceSq(entity.getPosX(), entity.getPosY(), entity.getPosZ(), false) > entry.getKey().distanceSq(entity.getPosX(), entity.getPosY(), entity.getPosZ(), false)) {
                closest = entry;
            }
        }
        return closest;
    }

    public void setDefaultPlayerReputation(int defaultReputation) {
        for (UUID s : this.playerReputation.keySet()) {
            this.modifyPlayerReputation(s, defaultReputation);
        }
    }

    public boolean repopulate() {
        int roomCount = this.getAllRooms().size();
        return this.numMyrmex < Math.min(IafConfig.myrmexColonySize, roomCount * 9) && reproduces;
    }

    public void addMyrmex(EntityMyrmexBase myrmex) {
        if (!this.myrmexList.contains(myrmex.getUniqueID())) {
            this.myrmexList.add(myrmex.getUniqueID());
        }
    }

    public void removeRoom(BlockPos pos) {
        this.foodRooms.remove(pos);
        this.miscRooms.remove(pos);
        this.babyRooms.remove(pos);
        this.allRooms.remove(pos);
        this.getEntrances().remove(pos);
        this.getEntranceBottoms().remove(pos);
    }

    public String toString() {
        return "MyrmexHive(x=" + this.center.getX() + ",y=" + this.center.getY() + ",z=" + this.center.getZ() + "), population=" + this.getNumMyrmex() + "\nUUID: " + hiveUUID;
    }

    public boolean equals(MyrmexHive hive) {
        return this.hiveUUID.equals(hive.hiveUUID);
    }

    class HiveAggressor {
        public LivingEntity agressor;
        public int agressionTime;
        public int agressionLevel;

        HiveAggressor(LivingEntity agressorIn, int agressionTimeIn, int agressionLevel) {
            this.agressor = agressorIn;
            this.agressionTime = agressionTimeIn;
            this.agressionLevel = agressionLevel;
        }
    }
}
