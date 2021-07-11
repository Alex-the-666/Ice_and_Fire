package com.github.alexthe666.iceandfire.entity.tile;

import java.util.Random;
import java.util.UUID;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.EntityPixie;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.message.MessageUpdatePixieHouse;
import com.github.alexthe666.iceandfire.message.MessageUpdatePixieHouseModel;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;

public class TileEntityPixieHouse extends TileEntity implements ITickableTileEntity {

    private static final float PARTICLE_WIDTH = 0.3F;
    private static final float PARTICLE_HEIGHT = 0.6F;
    public int houseType;
    public boolean hasPixie;
    public boolean tamedPixie;
    public UUID pixieOwnerUUID;
    public int pixieType;
    public int ticksExisted;
    public NonNullList<ItemStack> pixieItems = NonNullList.withSize(1, ItemStack.EMPTY);
    private Random rand;

    public TileEntityPixieHouse() {
        super(IafTileEntityRegistry.PIXIE_HOUSE);
        this.rand = new Random();
    }

    public static int getHouseTypeFromBlock(Block block) {
        if (block == IafBlockRegistry.PIXIE_HOUSE_MUSHROOM_RED) {
            return 1;
        }
        if (block == IafBlockRegistry.PIXIE_HOUSE_MUSHROOM_BROWN) {
            return 0;
        }
        if (block == IafBlockRegistry.PIXIE_HOUSE_OAK) {
            return 3;
        }
        if (block == IafBlockRegistry.PIXIE_HOUSE_BIRCH) {
            return 2;
        }
        if (block == IafBlockRegistry.PIXIE_HOUSE_SPRUCE) {
            return 5;
        }
        if (block == IafBlockRegistry.PIXIE_HOUSE_DARK_OAK) {
            return 4;
        }
        return 0;
    }

    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putInt("HouseType", houseType);
        compound.putBoolean("HasPixie", hasPixie);
        compound.putInt("PixieType", pixieType);
        compound.putBoolean("TamedPixie", tamedPixie);
        if (pixieOwnerUUID != null) {
            compound.putUniqueId("PixieOwnerUUID", pixieOwnerUUID);
        }
        ItemStackHelper.saveAllItems(compound, this.pixieItems);
        return compound;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        read(this.getBlockState(), packet.getNbtCompound());
        if (!world.isRemote) {
            IceAndFire.sendMSGToAll(new MessageUpdatePixieHouseModel(pos.toLong(), packet.getNbtCompound().getInt("HouseType")));
        }
    }

    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    public void read(BlockState state, CompoundNBT compound) {
        houseType = compound.getInt("HouseType");
        hasPixie = compound.getBoolean("HasPixie");
        pixieType = compound.getInt("PixieType");
        tamedPixie = compound.getBoolean("TamedPixie");
        if(compound.hasUniqueId("PixieOwnerUUID")){
            pixieOwnerUUID = compound.getUniqueId("PixieOwnerUUID");
        }
        this.pixieItems = NonNullList.withSize(1, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, pixieItems);
        super.read(state, compound);
    }

    @Override
    public void tick() {
        ticksExisted++;
        if (!world.isRemote && this.hasPixie && new Random().nextInt(100) == 0) {
            releasePixie();
        }
        if (this.world.isRemote && this.hasPixie) {
            IceAndFire.PROXY.spawnParticle("if_pixie", this.pos.getX() + 0.5F + (double) (this.rand.nextFloat() * PARTICLE_WIDTH * 2F) - (double) PARTICLE_WIDTH, this.pos.getY() + (double) (this.rand.nextFloat() * PARTICLE_HEIGHT), this.pos.getZ() + 0.5F + (double) (this.rand.nextFloat() * PARTICLE_WIDTH * 2F) - (double) PARTICLE_WIDTH, EntityPixie.PARTICLE_RGB[this.pixieType][0], EntityPixie.PARTICLE_RGB[this.pixieType][1], EntityPixie.PARTICLE_RGB[this.pixieType][2]);
        }
    }

    public void releasePixie() {
        EntityPixie pixie = new EntityPixie(IafEntityRegistry.PIXIE, this.world);
        pixie.setPositionAndRotation(this.pos.getX() + 0.5F, this.pos.getY() + 1F, this.pos.getZ() + 0.5F, new Random().nextInt(360), 0);
        pixie.setHeldItem(Hand.MAIN_HAND, pixieItems.get(0));
        pixie.setColor(this.pixieType);
        if (!world.isRemote) {
            world.addEntity(pixie);
        }
        this.hasPixie = false;
        this.pixieType = 0;
        pixie.ticksUntilHouseAI = 500;
        pixie.setTamed(this.tamedPixie);
        pixie.setOwnerId(this.pixieOwnerUUID);
        if (!world.isRemote) {
            IceAndFire.sendMSGToAll(new MessageUpdatePixieHouse(pos.toLong(), false, 0));
        }
    }
}
