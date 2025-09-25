package com.portingdeadmods.moofluids.compat.jei;

import mezz.jei.api.runtime.IRecipesGui;
import mezz.jei.api.runtime.IJeiRuntime;

import java.util.List;

public class AlchemyGlassJeiHandler {
    private static IJeiRuntime jeiRuntime;

    public static void setJeiRuntime(IJeiRuntime runtime) {
        jeiRuntime = runtime;
    }

    public static void showBreedingRecipes() {
        if (jeiRuntime != null) {
            IRecipesGui recipesGui = jeiRuntime.getRecipesGui();
            recipesGui.showTypes(List.of(MFJeiPlugin.BREEDING_TYPE));
        }
    }
}