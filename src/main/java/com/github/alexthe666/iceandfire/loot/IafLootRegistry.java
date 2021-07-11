package com.github.alexthe666.iceandfire.loot;

import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class IafLootRegistry {

    public static LootFunctionType CUSTOMIZE_TO_DRAGON;
    public static LootFunctionType CUSTOMIZE_TO_SERPENT;

    private static LootFunctionType register(String p_237451_0_, ILootSerializer<? extends ILootFunction> p_237451_1_) {
        return Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(p_237451_0_), new LootFunctionType(p_237451_1_));
    }

    public static void init(){
        CUSTOMIZE_TO_DRAGON = register("iceandfire:customize_to_dragon", new CustomizeToDragon.Serializer());
        CUSTOMIZE_TO_SERPENT = register("iceandfire:customize_to_sea_serpent", new CustomizeToSeaSerpent.Serializer());

    }

}
