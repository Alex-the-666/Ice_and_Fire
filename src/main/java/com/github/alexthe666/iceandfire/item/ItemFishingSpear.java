package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;

import net.minecraft.item.Item;

public class ItemFishingSpear extends Item {

    public ItemFishingSpear() {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS).maxDamage(64));
        this.setRegistryName(IceAndFire.MODID, "fishing_spear");
    }
}
