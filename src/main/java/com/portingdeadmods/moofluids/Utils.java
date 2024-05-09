package com.portingdeadmods.moofluids;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public final class Utils {
    public static String fluidStackToString(FluidStack fluidStack) {
        return "FluidStack { fluid: " + fluidToString(fluidStack.getFluid()) + ", amount: " + fluidStack.getAmount() + " }";
    }

    public static String fluidToString(Fluid fluid) {
        return "Fluid { " + "type: " + fluid.getFluidType() + " }";
    }
}
