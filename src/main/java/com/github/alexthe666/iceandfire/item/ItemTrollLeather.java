package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.enums.EnumTroll;

import net.minecraft.item.Item;

import java.util.Locale;

public class ItemTrollLeather extends Item {

    public ItemTrollLeather(EnumTroll troll) {
        super(new Item.Properties().group(IceAndFire.TAB_ITEMS));
        this.setRegistryName(IceAndFire.MODID, "troll_leather_" + troll.name().toLowerCase(Locale.ROOT));
    }
}
