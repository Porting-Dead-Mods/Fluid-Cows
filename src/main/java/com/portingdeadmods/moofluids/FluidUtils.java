/*
 * Taken from Industrial Foregoing licensed under MIT. All credits for this code go to their team <3
 */

package com.portingdeadmods.moofluids;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

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
}
