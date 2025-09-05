package com.portingdeadmods.moofluids.block.entity;

import com.portingdeadmods.moofluids.MooFluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = MooFluids.MODID, bus = EventBusSubscriber.Bus.MOD)
public class FluidCowJarCapabilityProvider {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            MFBlockEntities.FLUID_COW_JAR.get(),
            (blockEntity, context) -> blockEntity.getFluidTank()
        );
    }
}