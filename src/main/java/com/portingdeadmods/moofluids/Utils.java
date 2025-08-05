package com.portingdeadmods.moofluids;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.HashSet;
import java.util.Set;

public final class Utils {
    private static final Set<Fluid> FLUIDS = new HashSet<>();

    public static void add(Fluid fluid) {
        if (exists(fluid)) return;
        if (fluid == Fluids.EMPTY) return;
        if (!fluid.isSource(fluid.defaultFluidState())) return;
        FLUIDS.add(fluid);
    }

    public static boolean exists(Fluid fluid) {
        return FLUIDS.contains(fluid);
    }

    public static Fluid get(String registryName) {
        return FLUIDS.stream().filter(fluid -> BuiltInRegistries.FLUID.getKey(fluid).toString().equals(registryName)).findFirst().orElse(null);
    }

    public static ImmutableList<Fluid> getFluids() {
        return ImmutableList.copyOf(FLUIDS);
    }

    public static String idFromFluid(Fluid fluid) {
        String string = BuiltInRegistries.FLUID.getKey(fluid).toString();
        MooFluids.LOGGER.debug("fluid: {}", string);
        return string;
    }
}
