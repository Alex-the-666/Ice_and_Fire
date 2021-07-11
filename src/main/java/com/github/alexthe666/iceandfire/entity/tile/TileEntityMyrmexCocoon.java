package com.github.alexthe666.iceandfire.entity.tile;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileEntityMyrmexCocoon extends LockableLootTileEntity {


    private NonNullList<ItemStack> chestContents = NonNullList.withSize(18, ItemStack.EMPTY);

    public TileEntityMyrmexCocoon() {
        super(IafTileEntityRegistry.MYRMEX_COCOON);
    }

    public int getSizeInventory() {
        return 18;
    }

    public boolean isEmpty() {
        for (ItemStack itemstack : this.chestContents) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }


    public void read(BlockState bs, CompoundNBT compound) {
        super.read(bs, compound);
        this.chestContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);

        if (!this.checkLootAndRead(compound)) {
            ItemStackHelper.loadAllItems(compound, this.chestContents);
        }
    }

    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if (!this.checkLootAndWrite(compound)) {
            ItemStackHelper.saveAllItems(compound, this.chestContents);
        }

        return compound;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.myrmex_cocoon");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new ChestContainer(ContainerType.GENERIC_9X2, id, player, this, 2);
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return new ChestContainer(ContainerType.GENERIC_9X2, id, playerInventory, this, 2);
    }


    public int getInventoryStackLimit() {
        return 64;
    }


    protected NonNullList<ItemStack> getItems() {
        return this.chestContents;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {

    }

    public void openInventory(PlayerEntity player) {
        this.fillWithLoot(null);
        player.world.playSound(this.pos.getX(), this.pos.getY(), this.pos.getZ(), SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.BLOCKS, 1, 1, false);
    }

    public void closeInventory(PlayerEntity player) {
        this.fillWithLoot(null);
        player.world.playSound(this.pos.getX(), this.pos.getY(), this.pos.getZ(), SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.BLOCKS, 1, 1, false);
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

    public boolean isFull(ItemStack heldStack) {
        for (ItemStack itemstack : chestContents) {
            if (itemstack.isEmpty() || heldStack != null && !heldStack.isEmpty() && itemstack.isItemEqual(heldStack) && itemstack.getCount() + heldStack.getCount() < itemstack.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }
}
