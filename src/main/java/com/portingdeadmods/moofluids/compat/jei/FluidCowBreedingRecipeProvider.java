package com.portingdeadmods.moofluids.compat.jei;

import com.portingdeadmods.moofluids.recipe.AlloyRecipe;
import com.portingdeadmods.moofluids.recipe.MFRecipes;
import mezz.jei.api.helpers.IJeiHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;

public class FluidCowBreedingRecipeProvider {
    
    public static List<FluidCowBreedingRecipe> getBreedingRecipes(IJeiHelpers jeiHelpers) {
        List<FluidCowBreedingRecipe> recipes = new ArrayList<>();
        Minecraft minecraft = Minecraft.getInstance();
        
        if (minecraft.level != null) {
            RecipeManager recipeManager = minecraft.level.getRecipeManager();
            List<RecipeHolder<AlloyRecipe>> alloyRecipes = recipeManager.getAllRecipesFor(MFRecipes.ALLOY_TYPE.get());
            
            for (RecipeHolder<AlloyRecipe> holder : alloyRecipes) {
                AlloyRecipe recipe = holder.value();
                List<Fluid> inputs = recipe.inputs();
                
                if (inputs.size() == 2) {
                    Fluid parent1 = inputs.get(0);
                    Fluid parent2 = inputs.get(1);
                    Fluid result = recipe.output();
                    float successChance = recipe.successChance();
                    
                    recipes.add(new FluidCowBreedingRecipe(parent1, parent2, result, successChance));
                }
            }
        }
        
        return recipes;
    }
}