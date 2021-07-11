package com.github.alexthe666.iceandfire.inventory;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.item.ItemDragonArmor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;

public class ContainerDragon extends Container {
    private IInventory dragonInventory;
    private EntityDragonBase dragon;

    public ContainerDragon(int i, PlayerInventory playerInventory) {
        this(i, new Inventory(5), playerInventory, null);
    }

    public ContainerDragon(int id, IInventory ratInventory, PlayerInventory playerInventory, EntityDragonBase rat) {
        super(IafContainerRegistry.DRAGON_CONTAINER, id);
        this.dragonInventory = ratInventory;
        this.dragon = rat;
        byte b0 = 3;
        dragonInventory.openInventory(playerInventory.player);
        int i = (b0 - 4) * 18;
        this.addSlot(new Slot(ratInventory, 0, 8, 54) {
            public void onSlotChanged() {
                this.inventory.markDirty();
            }

            @Override
            public boolean isItemValid(ItemStack stack) {
                return super.isItemValid(stack) && stack.getItem() instanceof BannerItem;
            }
        });
        this.addSlot(new Slot(ratInventory, 1, 8, 18) {
            public void onSlotChanged() {
                this.inventory.markDirty();
            }

            @Override
            public boolean isItemValid(ItemStack stack) {
                return super.isItemValid(stack) && !stack.isEmpty() && stack.getItem() != null && stack.getItem() instanceof ItemDragonArmor && ((ItemDragonArmor) stack.getItem()).dragonSlot == 0;
            }
        });
        this.addSlot(new Slot(ratInventory, 2, 8, 36) {
            public void onSlotChanged() {
                this.inventory.markDirty();
            }

            @Override
            public boolean isItemValid(ItemStack stack) {
                return super.isItemValid(stack) && !stack.isEmpty() && stack.getItem() != null && stack.getItem() instanceof ItemDragonArmor && ((ItemDragonArmor) stack.getItem()).dragonSlot == 1;
            }
        });
        this.addSlot(new Slot(ratInventory, 3, 153, 18) {
            public void onSlotChanged() {
                this.inventory.markDirty();
            }

            @Override
            public boolean isItemValid(ItemStack stack) {
                return super.isItemValid(stack) && !stack.isEmpty() && stack.getItem() != null && stack.getItem() instanceof ItemDragonArmor && ((ItemDragonArmor) stack.getItem()).dragonSlot == 2;
            }
        });
        this.addSlot(new Slot(ratInventory, 4, 153, 36) {
            public void onSlotChanged() {
                this.inventory.markDirty();
            }

            @Override
            public boolean isItemValid(ItemStack stack) {
                return super.isItemValid(stack) && !stack.isEmpty() && stack.getItem() != null && stack.getItem() instanceof ItemDragonArmor && ((ItemDragonArmor) stack.getItem()).dragonSlot == 3;
            }
        });
        int j;
        int k;
        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 150 + j * 18 + i));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 208 + i));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return this.dragonInventory.isUsableByPlayer(playerIn) && this.dragon.isAlive() && this.dragon.getDistance(playerIn) < 8.0F;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < this.dragonInventory.getSizeInventory()) {
                if (!this.mergeItemStack(itemstack1, this.dragonInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.getSlot(1).isItemValid(itemstack1) && !this.getSlot(1).getHasStack()) {
                if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }

            } else if (this.getSlot(2).isItemValid(itemstack1) && !this.getSlot(2).getHasStack()) {
                if (!this.mergeItemStack(itemstack1, 2, 3, false)) {
                    return ItemStack.EMPTY;
                }

            } else if (this.getSlot(3).isItemValid(itemstack1) && !this.getSlot(3).getHasStack()) {
                if (!this.mergeItemStack(itemstack1, 3, 4, false)) {
                    return ItemStack.EMPTY;
                }

            } else if (this.getSlot(4).isItemValid(itemstack1) && !this.getSlot(4).getHasStack()) {
                if (!this.mergeItemStack(itemstack1, 4, 5, false)) {
                    return ItemStack.EMPTY;
                }

            } else if (this.getSlot(0).isItemValid(itemstack1)) {
                if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.dragonInventory.getSizeInventory() <= 5 || !this.mergeItemStack(itemstack1, 5, this.dragonInventory.getSizeInventory(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        this.dragonInventory.closeInventory(playerIn);
    }

}