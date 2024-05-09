package com.reclipse.moofluids;

import com.reclipse.moofluids.renderer.FluidCowRenderer;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.reclipse.moofluids.MFEntities.FLUID_COW;
import static com.reclipse.moofluids.MooFluids.MODID;

public final class MFEvents {
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onEntityAttributesCreation(EntityAttributeCreationEvent event) {
            event.put(FLUID_COW.get(), FluidCow.createAttributes().build());
        }

        @SubscribeEvent
        public static void onRegisterEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(FLUID_COW.get(), FluidCowRenderer::new);
        }

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class CommonForgeEvents{
        @SubscribeEvent
        public static void onCowSpawn(EntityEvent.EntityConstructing event){
            if(event.getEntity() instanceof FluidCow fluidCow){
                if(fluidCow.getCowFluid() == null){
                    fluidCow.setCowFluid(Fluids.LAVA);
                }
            }
        }
    }
}
