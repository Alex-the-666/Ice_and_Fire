package com.github.alexthe666.iceandfire.entity.props;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.github.alexthe666.citadel.server.entity.datatracker.EntityProperties;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.message.MessageAddChainedEntity;
import com.github.alexthe666.iceandfire.message.MessageRemoveChainedEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class ChainEntityProperties extends EntityProperties<LivingEntity> {

    public List<Entity> connectedEntities = new ArrayList<>();
    public boolean alreadyIgnoresCamera = false;
    public boolean wasJustDisconnected = false;
    private List<UUID> connectedEntityUUID = new ArrayList<UUID>();

    @Override
    public int getTrackingTime() {
        return 20;
    }

    @Override
    public void saveNBTData(CompoundNBT compound) {
        ListNBT nbttaglist = new ListNBT();
        for (UUID uuid : connectedEntityUUID) {
            CompoundNBT CompoundNBT = new CompoundNBT();
            CompoundNBT.putUniqueId("UUID", uuid);
            nbttaglist.add(CompoundNBT);
        }
        compound.put("ConnectedEntities", nbttaglist);
    }

    @Override
    public void loadNBTData(CompoundNBT compound) {
        ListNBT nbttaglist = compound.getList("ConnectedEntities", Constants.NBT.TAG_COMPOUND);
        this.connectedEntityUUID = new ArrayList<UUID>();
        for (int i = 0; i < nbttaglist.size(); ++i) {
            INBT cNbt = nbttaglist.get(i);
            if (cNbt instanceof CompoundNBT) {
                connectedEntityUUID.add(((CompoundNBT) cNbt).getUniqueId("UUID"));

            }
        }
        updateConnectedEntities(getEntity());
    }

    public void clearChained() {
        this.connectedEntities.clear();
        this.connectedEntityUUID.clear();
    }

    public boolean isChained() {
        return !connectedEntityUUID.isEmpty();
    }

    @Override
    public void init() {
        updateConnectedEntities(getEntity());
        alreadyIgnoresCamera = getEntity().ignoreFrustumCheck;
    }

    public void addChain(Entity parent, Entity entity) {
        minimizeLists();
        if (!connectedEntityUUID.contains(entity.getUniqueID())) {
            connectedEntityUUID.add(entity.getUniqueID());
        }
        updateConnectedEntities(parent);
    }

    public void removeChain(Entity us, Entity entity) {
        minimizeLists();
        connectedEntityUUID.remove(entity.getUniqueID());
        connectedEntities.remove(entity);
        if (entity != null && !entity.world.isRemote) {
            IceAndFire.sendMSGToAll(new MessageRemoveChainedEntity(us.getEntityId(), entity.getEntityId()));
        }
        wasJustDisconnected = true;
    }


    public List<Entity> getCurrentConnectedEntities() {
        return connectedEntities;
    }

    public void minimizeLists() {
        List<UUID> noDupesUUID = new ArrayList();
        for (UUID uuid : connectedEntityUUID) {
            if (!noDupesUUID.contains(uuid)) {
                noDupesUUID.add(uuid);
            }
        }
        connectedEntityUUID = noDupesUUID;
        List<Entity> noDupesEntity = new ArrayList();
        List<UUID> addedUUIDs = new ArrayList<UUID>();
        for (Entity entity : connectedEntities) {
            if (!addedUUIDs.contains(entity.getUniqueID())) {
                addedUUIDs.add(entity.getUniqueID());
                noDupesEntity.add(entity);
            }
        }
        connectedEntities = noDupesEntity;
    }

    public void updateConnectedEntities(Entity toUpdate) {
        connectedEntities.clear();
        List<UUID> addedUUIDs = new ArrayList<UUID>();
        if (toUpdate != null) {
            World world = toUpdate.world;
            if (!connectedEntityUUID.isEmpty() && world != null && !world.isRemote) {
                minimizeLists();
                for (UUID uuid : connectedEntityUUID) {
                    if (world.getServer() != null) {
                        Entity entity = world.getServer().getWorld(world.getDimensionKey()).getEntityByUuid(uuid);
                        if (entity != null) {
                            if (!addedUUIDs.contains(entity.getUniqueID())) {
                                addedUUIDs.add(entity.getUniqueID());
                                IceAndFire.sendMSGToAll(new MessageAddChainedEntity(toUpdate.getEntityId(), entity.getEntityId()));
                                connectedEntities.add(entity);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getID() {
        return "Ice And Fire - Chain Property Tracker";
    }

    @Override
    public Class<LivingEntity> getEntityClass() {
        return LivingEntity.class;
    }

    public boolean isConnectedToEntity(Entity parent, Entity entity) {
        updateConnectedEntities(parent);
        return this.connectedEntities.contains(entity);
    }
}
