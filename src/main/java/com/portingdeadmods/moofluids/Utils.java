package com.portingdeadmods.moofluids;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class Utils {
    private static final List<Fluid> FLUIDS = Lists.newArrayList();

    public static void add(Fluid fluid) {
        if (exists(fluid)) return;
        if (fluid == Fluids.EMPTY) return;
        if (!fluid.isSource(fluid.defaultFluidState())) return;
        FLUIDS.add(fluid);
    }

    public static boolean exists(Fluid fluid) {
        return FLUIDS.stream().anyMatch(fluidIn -> fluid == fluidIn);
    }

    public static Fluid get(String registryName) {
        return FLUIDS.stream().filter(fluid -> ForgeRegistries.FLUIDS.getKey(fluid).toString().equals(registryName)).findFirst().orElse(null);
    }

    public static ImmutableList<Fluid> getFluids() {
        return ImmutableList.copyOf(FLUIDS);
    }

    public static String idFromFluid(Fluid fluid) {
        return ForgeRegistries.FLUIDS.getKey(fluid).toString();
    }
}
