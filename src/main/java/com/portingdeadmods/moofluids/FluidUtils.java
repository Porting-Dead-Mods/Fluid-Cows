package com.portingdeadmods.moofluids;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

import java.util.concurrent.ConcurrentHashMap;

public class FluidUtils {
    // TODO
    // should item colors from ItemStackUtils also be cached?
    // invalidate cache on resource reload
    public static ConcurrentHashMap<ResourceLocation, Integer> colorCache = new ConcurrentHashMap<>();

    public static int getFluidColor(FluidStack stack) {
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(stack.getFluid());
        ResourceLocation location = renderProperties.getStillTexture(stack);
        int tint = renderProperties.getTintColor(stack);
        int textureColor = colorCache.computeIfAbsent(location, ColorUtils::getColorFrom);
        return FastColor.ARGB32.multiply(textureColor, tint);
    }

    public static int getFluidColor(Fluid fluid) {
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation location = renderProperties.getStillTexture();
        int tint = renderProperties.getTintColor();
        int textureColor = colorCache.computeIfAbsent(location, ColorUtils::getColorFrom);
        return FastColor.ARGB32.multiply(textureColor, tint);
    }
}
