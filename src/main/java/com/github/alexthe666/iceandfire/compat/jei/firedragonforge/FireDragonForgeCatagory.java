package com.github.alexthe666.iceandfire.compat.jei.firedragonforge;

import java.util.ArrayList;
import java.util.List;

import com.github.alexthe666.iceandfire.compat.jei.IceAndFireJEIPlugin;
import com.github.alexthe666.iceandfire.recipe.DragonForgeRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class FireDragonForgeCatagory implements IRecipeCategory<DragonForgeRecipe> {

    public FireDragonForgeDrawable drawable;

    public FireDragonForgeCatagory() {
        drawable = new FireDragonForgeDrawable();
    }


    @Override
    public ResourceLocation getUid() {
        return IceAndFireJEIPlugin.FIRE_DRAGON_FORGE_ID;
    }

    @Override
    public Class<? extends DragonForgeRecipe> getRecipeClass() {
        return DragonForgeRecipe.class;
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getTitle() {
        return I18n.format("iceandfire.fire_dragon_forge");
    }

    @Override
    public IDrawable getBackground() {
        return drawable;
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setIngredients(DragonForgeRecipe dragonForgeRecipe, IIngredients iIngredients) {
        List<Ingredient> ingredientsList = new ArrayList<>();
        ingredientsList.add(dragonForgeRecipe.getInput());
        ingredientsList.add(dragonForgeRecipe.getBlood());
        iIngredients.setInputIngredients(ingredientsList);
        iIngredients.setOutput(VanillaTypes.ITEM, dragonForgeRecipe.getOutput());

    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, DragonForgeRecipe dragonForgeRecipe, IIngredients iIngredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        guiItemStacks.init(0, true, 64, 29);
        guiItemStacks.init(1, true, 82, 29);
        guiItemStacks.init(2, false, 144, 30);
        guiItemStacks.set(iIngredients);
    }

}
