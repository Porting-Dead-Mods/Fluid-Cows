package com.portingdeadmods.moofluids;

import com.portingdeadmods.moofluids.block.MFBlocks;
import com.portingdeadmods.moofluids.block.entity.MFBlockEntities;
import com.portingdeadmods.moofluids.block.renderer.FluidCowJarRenderer;
import com.portingdeadmods.moofluids.entity.FluidCow;
import com.portingdeadmods.moofluids.entity.MFEntities;
import com.portingdeadmods.moofluids.entity.renderer.RenderFluidCow;
import com.portingdeadmods.moofluids.items.MFItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import java.util.ArrayList;
import java.util.List;

public final class MFEvents {
    @EventBusSubscriber(modid = MooFluids.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onEntityAttributesCreation(EntityAttributeCreationEvent event) {
            event.put(MFEntities.FLUID_COW.get(), FluidCow.createAttributes().build());
        }

        @SubscribeEvent
        public static void onRegisterEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(MFEntities.FLUID_COW.get(), RenderFluidCow::new);
            event.registerBlockEntityRenderer(MFBlockEntities.FLUID_COW_JAR.get(), FluidCowJarRenderer::new);
        }

        @SubscribeEvent
        public static void registerSpawnPlacement(RegisterSpawnPlacementsEvent event) {
            event.register(MFEntities.FLUID_COW.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MFEvents::checkFluidCowSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        }

        @SubscribeEvent
        public static void onLoadComplete(FMLLoadCompleteEvent event) {
            List<String> blackListedMods = new ArrayList<>();

            for (String blackListedFluid : MFConfig.fluidBlacklist) {
                if (blackListedFluid.contains("*"))
                    blackListedMods.add(blackListedFluid.split(":")[0]);
            }

            for (Fluid fluid : BuiltInRegistries.FLUID) {
                String namespace = BuiltInRegistries.FLUID.getKey(fluid).getNamespace();
                if(!MFConfig.fluidBlacklist.contains(Utils.idFromFluid(fluid)) && !blackListedMods.contains(namespace)){
                    if (fluid.getBucket() != ItemStack.EMPTY.getItem()) {
                        Utils.add(fluid);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void addItemToCreativeTab(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
                event.accept(MFItems.FLUID_COW_SPAWN_EGG);
            }
            if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
                event.accept(MFBlocks.FLUID_COW_JAR.get());
            }
        }

    }

    @EventBusSubscriber(modid = MooFluids.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static class CommonForgeEvents {
        @SubscribeEvent
        public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
            if (event.getLevel().isClientSide) return;

            if (event.getEntity() instanceof FluidCow mooFluidEntity) {
                if (mooFluidEntity.getFluid() == null || mooFluidEntity.getFluid() == Fluids.EMPTY) {
                    if (mooFluidEntity.isBaby()) {
                        if (event.loadedFromDisk()) {
                            event.setCanceled(true);
                            return;
                        }

                        Fluid randomFluid = mooFluidEntity.getRandomFluidForDimension((ServerLevelAccessor) event.getLevel());
                        if (randomFluid != null) {
                            mooFluidEntity.setFluid(Utils.idFromFluid(randomFluid));
                        } else {
                            event.setCanceled(true);
                        }
                    } else {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    public static boolean checkFluidCowSpawnRules(EntityType<? extends Animal> entityType, ServerLevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        //if (!Animal.checkAnimalSpawnRules(entityType, level, spawnType, pos, random)) {
        //    return false;
        //}

        if (!MFConfig.dimensionSpawnRestrictions.isEmpty()) {
            ResourceKey<Level> currentDimension = level.getLevel().dimension();
            ResourceLocation currentDimensionId = currentDimension.location();

            boolean hasAnyAllowedFluid = false;
            for (Fluid fluid : Utils.getFluids()) {
                ResourceLocation fluidId = BuiltInRegistries.FLUID.getKey(fluid);

                if (MFConfig.dimensionSpawnRestrictions.containsKey(fluidId)) {
                    ResourceLocation allowedDimension = MFConfig.dimensionSpawnRestrictions.get(fluidId);
                    if (allowedDimension.equals(currentDimensionId)) {
                        hasAnyAllowedFluid = true;
                        break;
                    }
                } else {
                    hasAnyAllowedFluid = true;
                    break;
                }
            }

            return hasAnyAllowedFluid;
        }

        return true;
    }
}