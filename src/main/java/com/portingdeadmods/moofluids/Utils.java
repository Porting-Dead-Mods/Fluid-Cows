package com.portingdeadmods.moofluids;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class Utils {
    public static String fluidStackToString(FluidStack fluidStack) {
        return "FluidStack { fluid: " + fluidToString(fluidStack.getFluid()) + ", amount: " + fluidStack.getAmount() + " }";
    }

    public static String fluidToString(Fluid fluid) {
        return "Fluid { " + "type: " + fluid.getFluidType() + " }";
    }

    public static List<Fluid> getSpawnableFluids() {
        List<Fluid> fluids = new ArrayList<>();
        for (Fluid fluid : ForgeRegistries.FLUIDS) {
            Item bucket = fluid.getBucket();
            if (!bucket.equals(Items.AIR)) {
                fluids.add(fluid);
            }
        }
        return fluids;
    }
}
