package com.github.alexthe666.iceandfire.entity.tile;

import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.entity.EntityDragonEgg;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityEggInIce extends TileEntity implements ITickableTileEntity {
    public EnumDragonEgg type;
    public int age;
    public int ticksExisted;
    @Nullable
    public UUID ownerUUID;

    public TileEntityEggInIce() {
        super(IafTileEntityRegistry.EGG_IN_ICE);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        if (type != null) {
            tag.putByte("Color", (byte) type.ordinal());
        } else {
            tag.putByte("Color", (byte) 0);
        }
        tag.putInt("Age", (byte) age);
        if (ownerUUID == null) {
            tag.putString("OwnerUUID", "");
        } else {
            tag.putString("OwnerUUID", ownerUUID.toString());
        }

        return super.write(tag);
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state,tag);
        type = EnumDragonEgg.values()[tag.getByte("Color")];
        age = tag.getByte("Age");
        UUID s;

        if (tag.hasUniqueId("OwnerUUID")) {
            s = tag.getUniqueId("OwnerUUID");
        } else {
            String s1 = tag.getString("OwnerUUID");
            s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.world.getServer(), s1);
        }
        if (s != null) {
            ownerUUID = s;
        }
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        read(this.getBlockState(), packet.getNbtCompound());
    }

    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    public void spawnEgg() {
        if (type != null) {
            EntityDragonEgg egg = new EntityDragonEgg(IafEntityRegistry.DRAGON_EGG, world);
            egg.setEggType(type);
            egg.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            egg.setOwnerId(this.ownerUUID);
            if (!world.isRemote) {
                world.addEntity(egg);
            }
        }
    }

    @Override
    public void tick() {
        age++;
        if (age >= IafConfig.dragonEggTime && type != null) {
            world.destroyBlock(pos, false);
            world.setBlockState(pos, Blocks.WATER.getDefaultState());
            EntityIceDragon dragon = new EntityIceDragon(world);
            dragon.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            dragon.setVariant(type.ordinal() - 4);
            dragon.setGender(new Random().nextBoolean());
            dragon.setTamed(true);
            dragon.setHunger(50);
            dragon.setOwnerId(ownerUUID);
            if (!world.isRemote) {
                world.addEntity(dragon);
            }
        }
        ticksExisted++;
    }
}
