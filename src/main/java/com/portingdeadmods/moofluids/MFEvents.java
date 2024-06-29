package com.portingdeadmods.moofluids;

import com.portingdeadmods.moofluids.entity.FluidCow;
import com.portingdeadmods.moofluids.entity.MFEntities;
import com.portingdeadmods.moofluids.entity.renderer.RenderFluidCow;
import com.portingdeadmods.moofluids.items.MFItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.Event;
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

        @SubscribeEvent
        public static void registerSpawnPlacement(SpawnPlacementRegisterEvent event) {
            if (MFConfig.naturalSpawning) {
                event.register(MFEntities.FLUID_COW.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        Animal::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            }
        }

        @SubscribeEvent
        public static void addItemToCreativeTab(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS)
                event.accept(MFItems.FLUID_COW_SPAWN_EGG);
        }

    }

    @Mod.EventBusSubscriber(modid = MooFluids.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class CommonForgeEvents {
        @SubscribeEvent
        public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
            if (event.getLevel().isClientSide) return;

            if (event.getEntity() instanceof FluidCow mooFluidEntity) {
                if (mooFluidEntity.getFluid() == null || mooFluidEntity.getFluid() == Fluids.EMPTY) {
                    if (mooFluidEntity.isBaby()) {
                        if (event.loadedFromDisk()) {
                            event.setCanceled(true);
                            event.setResult(Event.Result.DENY);
                            return;
                        }

                        Fluid randomFluid = mooFluidEntity.getRandomFluid();
                        if (randomFluid != null) {
                            mooFluidEntity.setFluid(Utils.idFromFluid(randomFluid));
                        } else {
                            event.setCanceled(true);
                            event.setResult(Event.Result.DENY);
                        }
                    } else {
                        event.setCanceled(true);
                        event.setResult(Event.Result.DENY);
                    }
                    printError();
                }
            }
        }

        private static void printError() {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.sendSystemMessage(Component.literal("Cannot spawn fluid cows, since all fluids are disabled.").withStyle(ChatFormatting.RED));
            }
        }
    }
}
