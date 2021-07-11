package com.github.alexthe666.iceandfire.entity.tile;

import javax.annotation.Nonnull;

import com.github.alexthe666.iceandfire.item.IafItemRegistry;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class PixieJarInvWrapper implements IItemHandlerModifiable {

    private TileEntityJar tile;
    private Direction side;

    public PixieJarInvWrapper(TileEntityJar tile, Direction side) {
        this.tile = tile;
        this.side = side;
    }

    public static LazyOptional<IItemHandler> create(TileEntityJar trashCan, Direction sides) {
        return LazyOptional.of(() -> new PixieJarInvWrapper(trashCan, sides));
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        // this.tile.hasProduced = false;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.tile.hasProduced ? new ItemStack(IafItemRegistry.PIXIE_DUST) : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (this.tile.hasProduced) {
            if (!simulate)
                this.tile.hasProduced = false;
            return new ItemStack(IafItemRegistry.PIXIE_DUST);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return false;
    }
}
