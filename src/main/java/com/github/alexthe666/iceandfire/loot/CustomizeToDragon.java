package com.github.alexthe666.iceandfire.loot;

import java.util.Random;

import com.github.alexthe666.iceandfire.entity.DragonType;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.item.IafItemRegistry;
import com.github.alexthe666.iceandfire.item.ItemDragonEgg;
import com.github.alexthe666.iceandfire.item.ItemDragonFlesh;
import com.github.alexthe666.iceandfire.item.ItemDragonScales;
import com.github.alexthe666.iceandfire.item.ItemDragonSkull;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;

public class CustomizeToDragon extends LootFunction {

    public CustomizeToDragon(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    protected ItemStack doApply(ItemStack stack, LootContext context) {
        if (!stack.isEmpty() && context.get(LootParameters.THIS_ENTITY) instanceof EntityDragonBase) {
            Random random = new Random();
            EntityDragonBase dragon = (EntityDragonBase) context.get(LootParameters.THIS_ENTITY);
            if (dragon == null){
                return stack;
            }
            if (stack.getItem() == IafItemRegistry.DRAGON_BONE) {
                stack.setCount(1 + random.nextInt(1 + (dragon.getAgeInDays() / 25)));
                return stack;
            }
            if (stack.getItem() instanceof ItemDragonScales) {
                stack.setCount(dragon.getAgeInDays() / 25 + random.nextInt(1 + (dragon.getAgeInDays() / 5)));
                return new ItemStack(dragon.getVariantScale(dragon.getVariant()), stack.getCount());
            }
            if (stack.getItem() instanceof ItemDragonEgg) {
                if (dragon.isAdult()) {
                    return new ItemStack(dragon.getVariantEgg(dragon.getVariant()), stack.getCount());
                } else {
                    stack.setCount(1 + random.nextInt(1 + (dragon.getAgeInDays() / 5)));
                    return new ItemStack(dragon.getVariantScale(dragon.getVariant()), stack.getCount());
                }
            }
            if (stack.getItem() instanceof ItemDragonFlesh) {
                stack.setCount(1 + random.nextInt(1 + (dragon.getAgeInDays() / 25)));
                return new ItemStack(stack.getItem(), stack.getCount());
            }
            if (stack.getItem() instanceof ItemDragonSkull) {
                ItemStack stack1 = new ItemStack(dragon.dragonType == DragonType.FIRE ? IafItemRegistry.DRAGON_SKULL_FIRE : IafItemRegistry.DRAGON_SKULL_ICE, stack.getCount());
                stack1.setTag(stack.getTag());
                return stack1;
            }
            if (stack.getItem() == IafItemRegistry.FIRE_DRAGON_BLOOD || stack.getItem() == IafItemRegistry.ICE_DRAGON_BLOOD) {
                return new ItemStack(dragon.dragonType == DragonType.FIRE ? IafItemRegistry.FIRE_DRAGON_BLOOD : IafItemRegistry.ICE_DRAGON_BLOOD, stack.getCount());
            }
            if (stack.getItem() == IafItemRegistry.FIRE_DRAGON_HEART || stack.getItem() == IafItemRegistry.ICE_DRAGON_HEART) {
                return new ItemStack(dragon.dragonType == DragonType.FIRE ? IafItemRegistry.FIRE_DRAGON_HEART : IafItemRegistry.ICE_DRAGON_HEART, stack.getCount());
            }
        }
        return stack;
    }

    @Override
    public LootFunctionType getFunctionType() {
        return IafLootRegistry.CUSTOMIZE_TO_DRAGON;
    }


    public static class Serializer extends LootFunction.Serializer<CustomizeToDragon> {
        public Serializer() {
            super();
        }

        public void serialize(JsonObject object, CustomizeToDragon functionClazz, JsonSerializationContext serializationContext) {
        }

        @Override
        public CustomizeToDragon deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
            return new CustomizeToDragon(conditionsIn);
        }
    }
}