package com.portingdeadmods.moofluids.compat.top;

import com.portingdeadmods.moofluids.MooFluids;
import com.portingdeadmods.moofluids.block.entity.FluidCowJarBlockEntity;
import mcjty.theoneprobe.api.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidType;

public final class FluidCowJarTopProvider implements IProbeInfoProvider {

    @Override
    public ResourceLocation getID() {
        return ResourceLocation.fromNamespaceAndPath(MooFluids.MODID, "top.fluid_cow_jar");
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo probeInfo, Player player, Level level, BlockState blockState, IProbeHitData data) {
        if (level.getBlockEntity(data.getPos()) instanceof FluidCowJarBlockEntity fluidCowJar) {
            if (fluidCowJar.hasCow()) {
                if (fluidCowJar.getCowFluid() != Fluids.EMPTY) {
                    FluidType fluidType = fluidCowJar.getCowFluid().getFluidType();
                    probeInfo.horizontal()
                            .text(Component.translatable("moofluids.top.fluid")
                                    .append(": ")
                                    .append(Component.translatable(fluidType.getDescriptionId())
                                            .withStyle(ChatFormatting.AQUA)));
                }

                int fluidAmount = fluidCowJar.getFluidTank().getFluidAmount();
                int capacity = fluidCowJar.getFluidTank().getCapacity();
                
                probeInfo.horizontal()
                        .text(Component.literal("Fluid: ")
                                .append(Component.literal(fluidAmount + " / " + capacity + " mB")
                                        .withStyle(ChatFormatting.YELLOW)));

                if (fluidCowJar.canBeMilked()) {
                    probeInfo.horizontal()
                            .text(Component.translatable("moofluids.top.ready")
                                    .withStyle(ChatFormatting.GREEN));
                } else {
                    int cooldown = fluidCowJar.getMilkingCooldown();
                    probeInfo.horizontal()
                            .text(Component.translatable("moofluids.top.cooldown")
                                    .append(": ")
                                    .append(Component.literal(String.valueOf((cooldown / 20) / 60)).withStyle(ChatFormatting.AQUA))
                                    .append("m ")
                                    .append(Component.literal(String.valueOf((cooldown / 20) % 60)).withStyle(ChatFormatting.AQUA))
                                    .append("s"));
                }

                if (fluidAmount < capacity) {
                    probeInfo.horizontal()
                            .text(Component.literal("Tank: Not Full")
                                    .withStyle(ChatFormatting.YELLOW));
                } else {
                    probeInfo.horizontal()
                            .text(Component.literal("Tank: Full")
                                    .withStyle(ChatFormatting.RED));
                }
            } else {
                probeInfo.horizontal()
                        .text(Component.literal("Empty - Place cow and crush with anvil")
                                .withStyle(ChatFormatting.GRAY));
            }
        }
    }
}