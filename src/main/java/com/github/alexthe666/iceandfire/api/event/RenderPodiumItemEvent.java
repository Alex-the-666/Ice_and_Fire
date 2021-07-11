package com.github.alexthe666.iceandfire.api.event;

import com.github.alexthe666.iceandfire.client.render.tile.RenderPodium;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityPodium;

import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

/*
    Called before an item is rendered on a podium. Cancel to remove default render of item
 */
public class RenderPodiumItemEvent extends Event {
    float partialTicks;
    double x, y, z;
    private RenderPodium render;
    private TileEntityPodium podium;

    public RenderPodiumItemEvent(RenderPodium renderPodium, TileEntityPodium podium, float partialTicks, double x, double y, double z) {
        this.render = renderPodium;
        this.podium = podium;
        this.partialTicks = partialTicks;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public RenderPodium getRender() {
        return render;
    }

    public ItemStack getItemStack() {
        return podium.getStackInSlot(0);
    }

    public TileEntityPodium getPodium() {
        return podium;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
