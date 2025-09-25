package com.portingdeadmods.moofluids.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

public class FluidBreedingRecipeManagerPlugin implements ISimpleRecipeManagerPlugin<FluidCowBreedingRecipe> {
    
    @Override
    public boolean isHandledInput(ITypedIngredient<?> ingredient) {
        if (ingredient.getType() == NeoForgeTypes.FLUID_STACK) {
            return true;
        }
        if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
            ItemStack itemStack = (ItemStack) ingredient.getIngredient();
            return getFluidFromItem(itemStack) != null;
        }
        return false;
    }

    @Override
    public boolean isHandledOutput(ITypedIngredient<?> ingredient) {
        if (ingredient.getType() == NeoForgeTypes.FLUID_STACK) {
            return true;
        }
        if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
            ItemStack itemStack = (ItemStack) ingredient.getIngredient();
            return getFluidFromItem(itemStack) != null;
        }
        return false;
    }
    
    @Override
    public List<FluidCowBreedingRecipe> getRecipesForInput(ITypedIngredient<?> input) {
        Fluid fluid = extractFluid(input);
        if (fluid != null) {
            return FluidCowBreedingRecipeProvider.getAllRecipes()
                .stream()
                .filter(recipe -> recipe.getParent1().equals(fluid) || recipe.getParent2().equals(fluid))
                .toList();
        }
        
        return List.of();
    }

    @Override
    public List<FluidCowBreedingRecipe> getRecipesForOutput(ITypedIngredient<?> output) {
        Fluid fluid = extractFluid(output);
        if (fluid != null) {
            return FluidCowBreedingRecipeProvider.getAllRecipes()
                .stream()
                .filter(recipe -> recipe.getResult().equals(fluid))
                .toList();
        }
        
        return List.of();
    }

    @Override
    public List<FluidCowBreedingRecipe> getAllRecipes() {
        return FluidCowBreedingRecipeProvider.getAllRecipes();
    }
    
    private Fluid extractFluid(ITypedIngredient<?> ingredient) {
        if (ingredient.getType() == NeoForgeTypes.FLUID_STACK) {
            FluidStack fluidStack = (FluidStack) ingredient.getIngredient();
            return fluidStack.getFluid();
        }
        if (ingredient.getType() == VanillaTypes.ITEM_STACK) {
            ItemStack itemStack = (ItemStack) ingredient.getIngredient();
            return getFluidFromItem(itemStack);
        }
        return null;
    }
    
    private Fluid getFluidFromItem(ItemStack itemStack) {
        IFluidHandler fluidHandler = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandler != null) {
            for (int i = 0; i < fluidHandler.getTanks(); i++) {
                FluidStack fluidInTank = fluidHandler.getFluidInTank(i);
                if (!fluidInTank.isEmpty()) {
                    return fluidInTank.getFluid();
                }
            }
        }

        if (itemStack.getItem() instanceof BucketItem bucketItem) {
            return bucketItem.content;
        }
        
        return null;
    }
}