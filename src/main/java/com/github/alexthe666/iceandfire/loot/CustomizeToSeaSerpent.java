package com.github.alexthe666.iceandfire.loot;

import java.util.Random;

import com.github.alexthe666.iceandfire.entity.EntitySeaSerpent;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.item.ItemSeaSerpentScales;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;

public class CustomizeToSeaSerpent extends LootFunction {

    public CustomizeToSeaSerpent(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    public ItemStack doApply(ItemStack stack, LootContext context) {
        if (!stack.isEmpty() && context.get(LootParameters.THIS_ENTITY) instanceof EntitySeaSerpent) {
            Random random = new Random();
            EntitySeaSerpent seaSerpent = (EntitySeaSerpent) context.get(LootParameters.THIS_ENTITY);
            if (seaSerpent == null){
                return stack;
            }
            int ancientModifier = seaSerpent.isAncient() ? 2 : 1;
            if (stack.getItem() instanceof ItemSeaSerpentScales) {
                stack.setCount(1 + random.nextInt(1 + (int) Math.ceil(seaSerpent.getSeaSerpentScale() * 3 * ancientModifier)));
                return new ItemStack(seaSerpent.getEnum().scale, stack.getCount());
            }
            if (stack.getItem() == IafItemRegistry.SERPENT_FANG) {
                stack.setCount(1 + random.nextInt(1 + (int) Math.ceil(seaSerpent.getSeaSerpentScale() * 2 * ancientModifier)));
                return stack;
            }
        }
        return stack;
    }

    @Override
    public LootFunctionType getFunctionType() {
        return IafLootRegistry.CUSTOMIZE_TO_SERPENT;
    }

    public static class Serializer extends LootFunction.Serializer<CustomizeToSeaSerpent> {
        public Serializer() {
            super();
        }

        public void serialize(JsonObject object, CustomizeToSeaSerpent functionClazz, JsonSerializationContext serializationContext) {
        }

        public CustomizeToSeaSerpent deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
            return new CustomizeToSeaSerpent(conditionsIn);
        }
    }
}