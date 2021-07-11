package com.github.alexthe666.iceandfire.enums;

import java.util.Map;

import com.github.alexthe666.iceandfire.entity.DragonType;
import com.google.common.collect.Maps;

import net.minecraft.util.text.TextFormatting;

public enum EnumDragonEgg {
    RED(0, TextFormatting.DARK_RED, DragonType.FIRE), GREEN(1, TextFormatting.DARK_GREEN, DragonType.FIRE), BRONZE(2, TextFormatting.GOLD, DragonType.FIRE), GRAY(3, TextFormatting.GRAY, DragonType.FIRE),
    BLUE(4, TextFormatting.AQUA, DragonType.ICE), WHITE(5, TextFormatting.WHITE, DragonType.ICE), SAPPHIRE(6, TextFormatting.BLUE, DragonType.ICE), SILVER(7, TextFormatting.DARK_GRAY, DragonType.ICE),
    ELECTRIC(8, TextFormatting.DARK_BLUE, DragonType.LIGHTNING), AMYTHEST(9, TextFormatting.LIGHT_PURPLE, DragonType.LIGHTNING), COPPER(10, TextFormatting.GOLD, DragonType.LIGHTNING), BLACK(11, TextFormatting.DARK_GRAY, DragonType.LIGHTNING);

    private static final Map<Integer, EnumDragonEgg> META_LOOKUP = Maps.newHashMap();

    static {
        EnumDragonEgg[] var0 = values();
        int var1 = var0.length;

        for (EnumDragonEgg var3 : var0) {
            META_LOOKUP.put(var3.meta, var3);
        }
    }

    public int meta;
    public TextFormatting color;
    public DragonType dragonType;

    EnumDragonEgg(int meta, TextFormatting color, DragonType dragonType) {
        this.meta = meta;
        this.color = color;
        this.dragonType = dragonType;
    }

    public static EnumDragonEgg byMetadata(int meta) {
        EnumDragonEgg i = META_LOOKUP.get(meta);
        return i == null ? RED : i;
    }
}
