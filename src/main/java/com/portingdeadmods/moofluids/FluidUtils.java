/*
 * Taken from Industrial Foregoing licensed under MIT. All credits for this code go to their team <3
 */

package com.portingdeadmods.moofluids;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class FluidUtils {
    public static ConcurrentHashMap<ResourceLocation, Integer> colorCache = new ConcurrentHashMap<>();

    public static int getFluidColor(Fluid fluid) {
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation location = renderProperties.getStillTexture();
        int tint = renderProperties.getTintColor();
        int textureColor = 0;
        try {
            textureColor = colorCache.computeIfAbsent(location, ColorUtils::getColorFrom);
        } catch (Exception ignored) {
        }
        return FastColor.ARGB32.multiply(textureColor, tint);
    }

    public static boolean isFluidBlacklisted(Fluid fluid) {
        String fluidId = BuiltInRegistries.FLUID.getKey(fluid).toString();
        String namespace = BuiltInRegistries.FLUID.getKey(fluid).getNamespace();

        List<String> blackListedMods = new ArrayList<>();
        for (String blackListedFluid : MFConfig.fluidBlacklist) {
            if (blackListedFluid.contains("*"))
                blackListedMods.add(blackListedFluid.split(":")[0]);
        }

        return MFConfig.fluidBlacklist.contains(fluidId) || blackListedMods.contains(namespace);
    }
}
