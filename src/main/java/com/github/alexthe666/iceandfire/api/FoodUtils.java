package com.github.alexthe666.iceandfire.api;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

public class FoodUtils {

    public static int getFoodPoints(Entity entity) {
        int foodPoints = Math.round(entity.getWidth() * entity.getHeight() * 10);
        if (entity instanceof AgeableEntity) {
            return foodPoints;
        }
        if (entity instanceof PlayerEntity) {
            return 15;
        }
        return 0;
    }

    public static int getFoodPoints(ItemStack item, boolean meatOnly, boolean includeFish) {
        if (item != null && item != ItemStack.EMPTY && item.getItem() != null && item.getItem().getFood() != null) {
            int food = item.getItem().getFood().getHealing() * 10;
            if (!meatOnly) {
                return food;
            } else if (item.getItem().getFood().isMeat()) {
                return food;
            } else if (includeFish && item.getItem() == Items.COD) {
                return food;
            }
        }
        return 0;
    }

    public static boolean isSeeds(ItemStack stack) {
        return Tags.Items.SEEDS.contains(stack.getItem());
    }
}
