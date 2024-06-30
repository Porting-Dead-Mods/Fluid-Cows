package com.portingdeadmods.moofluids.compat.top;

import com.portingdeadmods.moofluids.MooFluids;
import com.portingdeadmods.moofluids.entity.FluidCow;
import mcjty.theoneprobe.api.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidType;

public final class MFTopProvider implements IProbeInfoEntityProvider {
    @Override
    public String getID() {
        return MooFluids.MODID + ":top.entity";
    }

    @Override
    public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, Entity entity, IProbeHitEntityData iProbeHitEntityData) {
        if (entity instanceof FluidCow fluidCow) {
            FluidType fluidType = fluidCow.getFluid().getFluidType();
            boolean canBeMilk = fluidCow.canBeMilked();
            iProbeInfo.horizontal().text(Component.translatable("moofluids.top.fluid")
                    .append(": ")
                    .append(Component.translatable(fluidType.getDescriptionId())
                            .withStyle(ChatFormatting.AQUA)));
            if (canBeMilk) {
                iProbeInfo.horizontal().text(Component.translatable("moofluids.top.ready"));
            } else {
                iProbeInfo.horizontal().text(Component.translatable("moofluids.top.cooldown")
                        .append(": ")
                        .append(Component.literal(String.valueOf((fluidCow.getDelay() / 20) / 60)).withStyle(ChatFormatting.AQUA))
                        .append("m ")
                        .append(Component.literal(String.valueOf((fluidCow.getDelay() / 20) % 60)).withStyle(ChatFormatting.AQUA))
                        .append("s"));
            }
        }
    }
}
