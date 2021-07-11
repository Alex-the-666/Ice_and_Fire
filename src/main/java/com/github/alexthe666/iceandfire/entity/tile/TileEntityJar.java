package com.github.alexthe666.iceandfire.entity.tile;

import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityPixie;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.message.MessageUpdatePixieHouse;
import com.github.alexthe666.iceandfire.message.MessageUpdatePixieHouseModel;
import com.github.alexthe666.iceandfire.message.MessageUpdatePixieJar;
import com.github.alexthe666.iceandfire.misc.IafSoundRegistry;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;

public class TileEntityJar extends TileEntity implements ITickableTileEntity {

    private static final float PARTICLE_WIDTH = 0.3F;
    private static final float PARTICLE_HEIGHT = 0.6F;
    public boolean hasPixie;
    public boolean prevHasProduced;
    public boolean hasProduced;
    public boolean tamedPixie;
    public UUID pixieOwnerUUID;
    public int pixieType;
    public int ticksExisted;
    public NonNullList<ItemStack> pixieItems = NonNullList.withSize(1, ItemStack.EMPTY);
    public float rotationYaw;
    public float prevRotationYaw;
    net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler> downHandler = PixieJarInvWrapper.create(this, Direction.DOWN);
    private Random rand;

    public TileEntityJar() {
        super(IafTileEntityRegistry.PIXIE_JAR);
        this.rand = new Random();
        this.hasPixie = true;
    }

    public TileEntityJar(boolean empty) {
        super(IafTileEntityRegistry.PIXIE_JAR);
        this.rand = new Random();
        this.hasPixie = !empty;
    }

    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putBoolean("HasPixie", hasPixie);
        compound.putInt("PixieType", pixieType);
        compound.putBoolean("HasProduced", hasProduced);
        compound.putBoolean("TamedPixie", tamedPixie);
        if (pixieOwnerUUID != null) {
            compound.putUniqueId("PixieOwnerUUID", pixieOwnerUUID);
        }
        compound.putInt("TicksExisted", ticksExisted);
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
            IceAndFire.sendMSGToAll(new MessageUpdatePixieHouseModel(pos.toLong(), packet.getNbtCompound().getInt("PixieType")));
        }
    }

    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    public void read(BlockState state, CompoundNBT compound) {
        hasPixie = compound.getBoolean("HasPixie");
        pixieType = compound.getInt("PixieType");
        hasProduced = compound.getBoolean("HasProduced");
        ticksExisted = compound.getInt("TicksExisted");
        tamedPixie = compound.getBoolean("TamedPixie");
        if (compound.hasUniqueId("PixieOwnerUUID")){
            pixieOwnerUUID = compound.getUniqueId("PixieOwnerUUID");
        }
        this.pixieItems = NonNullList.withSize(1, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, pixieItems);
        super.read(state, compound);
    }

    @Override
    public void tick() {
        ticksExisted++;
        if (this.world.isRemote && this.hasPixie) {
            IceAndFire.PROXY.spawnParticle("if_pixie", this.pos.getX() + 0.5F + (double) (this.rand.nextFloat() * PARTICLE_WIDTH * 2F) - (double) PARTICLE_WIDTH, this.pos.getY() + (double) (this.rand.nextFloat() * PARTICLE_HEIGHT), this.pos.getZ() + 0.5F + (double) (this.rand.nextFloat() * PARTICLE_WIDTH * 2F) - (double) PARTICLE_WIDTH, EntityPixie.PARTICLE_RGB[this.pixieType][0], EntityPixie.PARTICLE_RGB[this.pixieType][1], EntityPixie.PARTICLE_RGB[this.pixieType][2]);
        }
        if (ticksExisted % 24000 == 0 && !this.hasProduced && this.hasPixie) {
            this.hasProduced = true;
            if (!this.getWorld().isRemote) {
                IceAndFire.sendMSGToAll(new MessageUpdatePixieJar(pos.toLong(), hasProduced));
            }
        }
        if (this.hasPixie && hasProduced != prevHasProduced && ticksExisted > 5) {
            if (!this.getWorld().isRemote) {
                IceAndFire.sendMSGToAll(new MessageUpdatePixieJar(pos.toLong(), hasProduced));
            }else{
                world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5, IafSoundRegistry.PIXIE_HURT, SoundCategory.BLOCKS, 1, 1, false);
            }
        }
        prevRotationYaw = rotationYaw;
        if (rand.nextInt(30) == 0) {
            this.rotationYaw = (rand.nextFloat() * 360F) - 180F;
        }
        if (this.hasPixie && ticksExisted % 40 == 0 && this.rand.nextInt(6) == 0 && world.isRemote) {
            this.world.playSound(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5, IafSoundRegistry.PIXIE_IDLE, SoundCategory.BLOCKS, 1, 1, false);
        }
        prevHasProduced = hasProduced;
    }

    public void releasePixie() {
        EntityPixie pixie = new EntityPixie(IafEntityRegistry.PIXIE, this.world);
        pixie.setPositionAndRotation(this.pos.getX() + 0.5F, this.pos.getY() + 1F, this.pos.getZ() + 0.5F, new Random().nextInt(360), 0);
        pixie.setHeldItem(Hand.MAIN_HAND, pixieItems.get(0));
        pixie.setColor(this.pixieType);
        world.addEntity(pixie);
        this.hasPixie = false;
        this.pixieType = 0;
        pixie.ticksUntilHouseAI = 500;
        pixie.setTamed(this.tamedPixie);
        pixie.setOwnerId(this.pixieOwnerUUID);

        if (!world.isRemote) {
            IceAndFire.sendMSGToAll(new MessageUpdatePixieHouse(pos.toLong(), false, 0));
        }
    }

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return downHandler.cast();
        return super.getCapability(capability, facing);
    }

    private float updateRotation(float float1, float float2, float float3) {
        float f = MathHelper.wrapDegrees(float2 - float1);

        if (f > float3) {
            f = float3;
        }

        if (f < -float3) {
            f = -float3;
        }

        return float1 + f;
    }
}
