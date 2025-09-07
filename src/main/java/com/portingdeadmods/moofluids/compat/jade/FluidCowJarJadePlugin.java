package com.portingdeadmods.moofluids.compat.jade;

import com.portingdeadmods.moofluids.FluidUtils;
import com.portingdeadmods.moofluids.block.entity.FluidCowJarBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

public enum FluidCowJarJadePlugin implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockEntity() instanceof FluidCowJarBlockEntity fluidCowJar) {
            if (fluidCowJar.hasCow()) {
                if (fluidCowJar.getCowFluid() != Fluids.EMPTY) {
                    int color = FluidUtils.getFluidColor(fluidCowJar.getCowFluid());
                    tooltip.add(Component.literal("Fluid: ")
                            .append(Component.translatable(fluidCowJar.getCowFluid().getFluidType().getDescriptionId())
                                    .withStyle(Style.EMPTY.withColor(color))));
                }

                int fluidAmount = fluidCowJar.getFluidTank().getFluidAmount();
                int capacity = fluidCowJar.getFluidTank().getCapacity();

                if (fluidCowJar.canBeMilked()) {
                    tooltip.add(Component.literal("Status: Ready to generate"));
                } else {
                    int cooldown = fluidCowJar.getMilkingCooldown();
                    tooltip.add(Component.literal("Cooldown: " + (cooldown / 20) / 60 + "m " + (cooldown / 20) % 60 + "s"));
                }

                if (fluidAmount >= capacity) {
                    tooltip.add(Component.literal("Tank: Full"));
                }
            } else {
                tooltip.add(Component.literal("Empty"));
                tooltip.add(Component.literal("Place cow and crush with anvil"));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("moofluids", "fluid_cow_jar");
    }
}