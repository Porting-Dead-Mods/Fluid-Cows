package com.portingdeadmods.moofluids.compat.jei;

import com.portingdeadmods.moofluids.MooFluids;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@JeiPlugin
public class MFJeiPlugin implements IModPlugin {
    public static final RecipeType<FluidCowBreedingRecipe> BREEDING_TYPE = 
        RecipeType.create(MooFluids.MODID, "fluid_cow_breeding", FluidCowBreedingRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(MooFluids.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        
        registration.addRecipeCategories(new FluidCowBreedingCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(BREEDING_TYPE, FluidCowBreedingRecipeProvider.getBreedingRecipes(registration.getJeiHelpers()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Items.WHEAT), BREEDING_TYPE);
    }
}