package com.portingdeadmods.moofluids.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

public record AlloyRecipeInput(List<Fluid> fluids) implements RecipeInput {
    
    @Override
    public ItemStack getItem(int index) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public int size() {
        return fluids.size();
    }
    
    @Override
    public boolean isEmpty() {
        return fluids.isEmpty();
    }
}