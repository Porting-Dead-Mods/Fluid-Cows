package com.portingdeadmods.moofluids;

import com.portingdeadmods.moofluids.entity.FluidCow;
import com.portingdeadmods.moofluids.entity.MFEntities;
import com.portingdeadmods.moofluids.entity.renderer.RenderFluidCow;
import com.portingdeadmods.moofluids.networking.CowFluidS2C;
import com.portingdeadmods.moofluids.networking.ModPackets;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

public final class MFEvents {
    @Mod.EventBusSubscriber(modid = MooFluids.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onEntityAttributesCreation(EntityAttributeCreationEvent event) {
            event.put(MFEntities.FLUID_COW.get(), FluidCow.createAttributes().build());
        }

        @SubscribeEvent
        public static void onRegisterEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(MFEntities.FLUID_COW.get(), RenderFluidCow::new);
        }

    }

    @Mod.EventBusSubscriber(modid = MooFluids.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class CommonForgeEvents {
        private static final List<Fluid> SPAWNABLE_FLUIDS = Utils.getSpawnableFluids();
        private static final double CUMULATIVE_SPAWN_WEIGHT = getCumulativeSpawnWeight();

        @SubscribeEvent
        public static void onCowSpawn(EntityEvent.EntityConstructing event) {
            if (event.getEntity() instanceof FluidCow fluidCow) {
                if (fluidCow.getCowFluid() == null) {
                    Fluid entityFluid = getEntityFluid();
                    if (entityFluid instanceof FlowingFluid flowingFluid) {
                        fluidCow.setCowFluid(flowingFluid.getSource());
                        ModPackets.sendToClients(new CowFluidS2C(new FluidStack(flowingFluid.getSource(), 1000), fluidCow.getId()));
                    }
                }
            }
        }

        private static Fluid getEntityFluid() {
            if (!SPAWNABLE_FLUIDS.isEmpty()) {
                // Weighted random chance for activation
                double activationWeight = Math.random() * CUMULATIVE_SPAWN_WEIGHT;
                // Accumulated chance from iteration
                double accumulatedWeight = 0.0;

                for (final Fluid potentialEntityFluid : SPAWNABLE_FLUIDS) {
                    accumulatedWeight += MFConfig.spawnRate;
                    if (accumulatedWeight >= activationWeight) {
                        return potentialEntityFluid;
                    }
                }
            }

            return null;
        }

        private static double getCumulativeSpawnWeight() {
            double cumulativeSpawnWeight = 0.0;

            for (final Fluid ignored : SPAWNABLE_FLUIDS) {
                cumulativeSpawnWeight += MFConfig.spawnRate;
            }

            return cumulativeSpawnWeight;
        }
    }
}
