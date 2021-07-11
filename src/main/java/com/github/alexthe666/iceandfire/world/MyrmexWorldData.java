package com.github.alexthe666.iceandfire.world;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.util.MyrmexHive;
import com.google.common.collect.Lists;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

public class MyrmexWorldData extends WorldSavedData {

    private static final String IDENTIFIER = "iceandfire_myrmex";
    private final List<BlockPos> villagerPositionsList = Lists.newArrayList();
    private final List<MyrmexHive> hiveList = Lists.newArrayList();
    private World world;
    private int tickCounter;

    public MyrmexWorldData() {
        super(IDENTIFIER);
    }

    public MyrmexWorldData(World world) {
        super(IDENTIFIER);
        this.world = world;
        this.markDirty();
    }

    public static MyrmexWorldData get(World world) {
        if (world instanceof ServerWorld) {
            ServerWorld overworld = world.getServer().getWorld(world.getDimensionKey());

            DimensionSavedDataManager storage = overworld.getSavedData();
            MyrmexWorldData data = storage.getOrCreate(MyrmexWorldData::new, IDENTIFIER);
            if (data != null) {
                data.world = world;
                data.markDirty();
            }
            return data;
        }
        //If the world is ClientLevel just return empty non significant data object
        return new MyrmexWorldData();
    }

    public static void addHive(World world, MyrmexHive hive) {
        get(world).hiveList.add(hive);
    }

    public void setWorldsForAll(World worldIn) {
        this.world = worldIn;
        for (MyrmexHive village : this.hiveList) {
            village.setWorld(worldIn);
        }
    }

    public void tick() {
        ++this.tickCounter;
        for (MyrmexHive hive : this.hiveList) {
            hive.tick(this.tickCounter, world);
        }
        //this.removeAnnihilatedHives();

    }

    private void removeAnnihilatedHives() {
        Iterator<MyrmexHive> iterator = this.hiveList.iterator();

        while (iterator.hasNext()) {
            MyrmexHive village = iterator.next();

            if (village.isAnnihilated()) {
                iterator.remove();
                this.markDirty();
            }
        }
    }

    public List<MyrmexHive> getHivelist() {
        return this.hiveList;
    }

    public MyrmexHive getNearestHive(BlockPos doorBlock, int radius) {
        MyrmexHive village = null;
        double d0 = 3.4028234663852886E38D;

        for (MyrmexHive village1 : this.hiveList) {
            double d1 = village1.getCenter().distanceSq(doorBlock);

            if (d1 < d0) {
                float f = (float) (radius + village1.getVillageRadius());

                if (d1 <= (double) (f * f)) {
                    village = village1;
                    d0 = d1;
                }
            }
        }

        return village;
    }

    private boolean positionInList(BlockPos pos) {
        for (BlockPos blockpos : this.villagerPositionsList) {
            if (blockpos.equals(pos)) {
                return true;
            }
        }

        return false;
    }

    public void debug() {
        for (MyrmexHive hive : this.hiveList) {
            IceAndFire.LOGGER.warn(hive.toString());
        }
    }

    public void read(CompoundNBT nbt) {
        this.tickCounter = nbt.getInt("Tick");
        ListNBT nbttaglist = nbt.getList("Hives", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            CompoundNBT CompoundNBT = nbttaglist.getCompound(i);
            MyrmexHive village = new MyrmexHive();
            village.readVillageDataFromNBT(CompoundNBT);
            this.hiveList.add(village);
        }
    }

    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("Tick", this.tickCounter);
        ListNBT nbttaglist = new ListNBT();

        for (MyrmexHive village : this.hiveList) {
            CompoundNBT CompoundNBT = new CompoundNBT();
            village.writeVillageDataToNBT(CompoundNBT);
            nbttaglist.add(CompoundNBT);
        }

        compound.put("Hives", nbttaglist);
        return compound;
    }

    public MyrmexHive getHiveFromUUID(UUID id) {
        for (MyrmexHive hive : this.hiveList) {
            if (hive.hiveUUID != null && hive.hiveUUID.equals(id)) {
                return hive;
            }
        }
        return null;
    }
}
