package com.github.alexthe666.iceandfire.inventory;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityHippocampus;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerHippocampus extends Container {
    private final IInventory hippocampusInventory;
    private final EntityHippocampus hippocampus;
    private final PlayerEntity player;

    public ContainerHippocampus(int i, PlayerInventory playerInventory) {
        this(i, new Inventory(18), playerInventory, null);
    }

    public ContainerHippocampus(int id, IInventory ratInventory, PlayerInventory playerInventory, EntityHippocampus hippocampus) {
        super(IafContainerRegistry.HIPPOCAMPUS_CONTAINER, id);
        this.hippocampusInventory = ratInventory;
        if(hippocampus == null && IceAndFire.PROXY.getReferencedMob() instanceof EntityHippocampus){
            hippocampus = (EntityHippocampus)IceAndFire.PROXY.getReferencedMob();
        }
        this.hippocampus = hippocampus;
        this.player = playerInventory.player;
        int i = 3;
        hippocampusInventory.openInventory(player);
        int j = -18;
        this.addSlot(new Slot(hippocampusInventory, 0, 8, 18) {
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == Items.SADDLE && !this.getHasStack();
            }

            public void onSlotChanged() {
                if (ContainerHippocampus.this.hippocampus != null) {
                    ContainerHippocampus.this.hippocampus.refreshInventory();
                }
            }

            @OnlyIn(Dist.CLIENT)
            public boolean isEnabled() {
                return true;
            }
        });
        this.addSlot(new Slot(hippocampusInventory, 1, 8, 36) {
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !this.getHasStack();
            }

            public void onSlotChanged() {
                if (ContainerHippocampus.this.hippocampus != null) {
                    ContainerHippocampus.this.hippocampus.refreshInventory();
                }
            }

            @OnlyIn(Dist.CLIENT)
            public boolean isEnabled() {
                return true;
            }
        });
        this.addSlot(new Slot(hippocampusInventory, 2, 8, 52) {

            public boolean isItemValid(ItemStack stack) {
                return EntityHippocampus.getIntFromArmor(stack) != 0;
            }

            public void onSlotChanged() {
                if (ContainerHippocampus.this.hippocampus != null) {
                    ContainerHippocampus.this.hippocampus.refreshInventory();
                }
            }

            public int getSlotStackLimit() {
                return 1;
            }

            @OnlyIn(Dist.CLIENT)
            public boolean isEnabled() {
                return true;
            }
        });

        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 5; ++l) {
                this.addSlot(new Slot(hippocampusInventory, 3 + l + k * 5, 80 + l * 18, 18 + k * 18) {
                    @OnlyIn(Dist.CLIENT)
                    public boolean isEnabled() {
                        return ContainerHippocampus.this.hippocampus != null && ContainerHippocampus.this.hippocampus.isChested();
                    }

                    public boolean isItemValid(ItemStack stack) {
                        return ContainerHippocampus.this.hippocampus != null && ContainerHippocampus.this.hippocampus.isChested();
                    }
                });
            }
        }

        for (int i1 = 0; i1 < 3; ++i1) {
            for (int k1 = 0; k1 < 9; ++k1) {
                this.addSlot(new Slot(player.inventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 102 + i1 * 18 + -18));
            }
        }

        for (int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot(player.inventory, j1, 8 + j1 * 18, 142));
        }

    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < this.hippocampusInventory.getSizeInventory()) {
                if (!this.mergeItemStack(itemstack1, this.hippocampusInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
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

            } else if (this.getSlot(0).isItemValid(itemstack1)) {
                if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.hippocampusInventory.getSizeInventory() <= 3 || !this.mergeItemStack(itemstack1, 3, this.hippocampusInventory.getSizeInventory(), false)) {
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

    public boolean canInteractWith(PlayerEntity playerIn) {
        return this.hippocampusInventory.isUsableByPlayer(playerIn) && this.hippocampus.isAlive() && this.hippocampus.getDistance(playerIn) < 8.0F;
    }

    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        this.hippocampusInventory.closeInventory(playerIn);
    }
}