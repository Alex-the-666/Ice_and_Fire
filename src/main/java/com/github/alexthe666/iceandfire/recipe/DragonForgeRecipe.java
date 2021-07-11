package com.github.alexthe666.iceandfire.recipe;

import com.google.gson.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;

import java.lang.reflect.Type;

public class DragonForgeRecipe {

    private Ingredient input;
    private Ingredient blood;
    private ItemStack output;
    private String dragonType;

    public DragonForgeRecipe(Ingredient input, Ingredient blood, ItemStack output, String dragonType) {
        this.input = input;
        this.blood = blood;
        this.output = output;
        this.dragonType = dragonType;
    }

    public Ingredient getInput() {

        return input;
    }

    public Ingredient getBlood() {
        return blood;
    }

    public ItemStack getOutput() {
        return output;
    }

    public String getDragonType() {
        return dragonType;
    }

    public static class Deserializer implements JsonDeserializer<DragonForgeRecipe> {

        @Override
        public DragonForgeRecipe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonobject = json.getAsJsonObject();
            String dragonType = JSONUtils.getString(jsonobject, "dragon_type");
            Ingredient input_left = Ingredient.EMPTY;
            if (jsonobject.has("input_left")) {
                input_left = Ingredient.deserialize(JSONUtils.getJsonObject(jsonobject, "input_left"));
            }
            Ingredient input_right = Ingredient.EMPTY;
            if (jsonobject.has("input_right")) {
                input_right = Ingredient.deserialize(JSONUtils.getJsonObject(jsonobject, "input_right"));
            }
            ItemStack result = ItemStack.EMPTY;
            if (jsonobject.has("result")) {
                result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(jsonobject, "result"));
            }
            return new DragonForgeRecipe(input_left, input_right, result, dragonType);
        }

    }

}
