package com.portingdeadmods.moofluids.compat.top;

import com.portingdeadmods.moofluids.MooFluids;
import com.portingdeadmods.moofluids.entity.FluidCow;
import mcjty.theoneprobe.api.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public final class MFTopProvider implements IProbeInfoEntityProvider {
    @Override
    public String getID() {
        return MooFluids.MODID + ":top.entity";
    }

    @Override
    public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, Entity entity, IProbeHitEntityData iProbeHitEntityData) {
        if (entity instanceof FluidCow fluidCow) {
            MooFluids.LOGGER.debug("Render cow top info!!!");
            fluidCow.getCapability(ForgeCapabilities.FLUID_HANDLER)
                    .ifPresent(tank -> iProbeInfo.tankHandler(tank));
        }
    }
}
