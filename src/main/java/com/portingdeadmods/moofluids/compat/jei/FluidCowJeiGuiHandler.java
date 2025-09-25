package com.portingdeadmods.moofluids.compat.jei;

import com.portingdeadmods.moofluids.entity.FluidCow;
import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.Optional;

public class FluidCowJeiGuiHandler implements IGlobalGuiHandler {
    private static IJeiRuntime jeiRuntime;

    public static void setJeiRuntime(IJeiRuntime runtime) {
        jeiRuntime = runtime;
    }

    @Override
    public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(double mouseX, double mouseY) {
        if (jeiRuntime == null) {
            return Optional.empty();
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return Optional.empty();
        }

        HitResult hitResult = minecraft.hitResult;
        if (hitResult instanceof EntityHitResult entityHitResult) {
            Entity entity = entityHitResult.getEntity();
            if (entity instanceof FluidCow fluidCow) {
                Fluid fluid = fluidCow.getFluid();
                if (fluid != null && fluid != Fluids.EMPTY) {
                    FluidStack fluidStack = new FluidStack(fluid, FluidType.BUCKET_VOLUME);
                    IIngredientManager ingredientManager = jeiRuntime.getIngredientManager();
                    ITypedIngredient<FluidStack> typedIngredient = ingredientManager.createTypedIngredient(NeoForgeTypes.FLUID_STACK, fluidStack).orElse(null);
                    
                    if (typedIngredient != null) {
                        int screenX = (int) mouseX;
                        int screenY = (int) mouseY;
                        Rect2i area = new Rect2i(screenX - 8, screenY - 8, 16, 16);
                        
                        Optional<IClickableIngredient<FluidStack>> clickableIngredient = ingredientManager
                            .createClickableIngredient(NeoForgeTypes.FLUID_STACK, fluidStack, area, true);
                        
                        return clickableIngredient.map(ingredient -> (IClickableIngredient<?>) ingredient);
                    }
                }
            }
        }

        return Optional.empty();
    }
}