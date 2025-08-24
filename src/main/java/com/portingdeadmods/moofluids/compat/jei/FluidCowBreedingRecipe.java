package com.portingdeadmods.moofluids.compat.jei;

import net.minecraft.world.level.material.Fluid;

public class FluidCowBreedingRecipe {
    private final Fluid parent1;
    private final Fluid parent2;
    private final Fluid result;
    private final float successChance;

    public FluidCowBreedingRecipe(Fluid parent1, Fluid parent2, Fluid result, float successChance) {
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.result = result;
        this.successChance = successChance;
    }

    public Fluid getParent1() {
        return parent1;
    }

    public Fluid getParent2() {
        return parent2;
    }

    public Fluid getResult() {
        return result;
    }

    public float getSuccessChance() {
        return successChance;
    }
}