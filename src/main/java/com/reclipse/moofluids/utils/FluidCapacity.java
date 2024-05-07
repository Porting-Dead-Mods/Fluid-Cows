package com.reclipse.moofluids.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidCapacity {
    public static int getFluidCapacity(ItemStack stack) {
        LazyOptional< IFluidHandler> handler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER, null);
        return handler.map(handler1 -> {
            return handler1.getTankCapacity(0);
        }).orElse(0);
    }
}
