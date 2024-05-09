package com.reclipse.moofluids;

import com.reclipse.moofluids.renderer.FluidCowRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
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
}
