package com.portingdeadmods.moofluids;

import com.google.common.collect.ImmutableList;
import com.portingdeadmods.moofluids.entity.MFEntities;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Utils {
    private static final Set<Fluid> FLUIDS = new HashSet<>();

    public static void add(Fluid fluid) {
        if (exists(fluid)) return;
        if (fluid == Fluids.EMPTY) return;
        if (!fluid.isSource(fluid.defaultFluidState())) return;
        FLUIDS.add(fluid);
    }

    public static boolean exists(Fluid fluid) {
        return FLUIDS.contains(fluid);
    }

    public static Fluid get(String registryName) {
        return FLUIDS.stream().filter(fluid -> BuiltInRegistries.FLUID.getKey(fluid).toString().equals(registryName)).findFirst().orElse(null);
    }

    public static ImmutableList<Fluid> getFluids() {
        return ImmutableList.copyOf(FLUIDS);
    }

    public static String idFromFluid(Fluid fluid) {
        String string = BuiltInRegistries.FLUID.getKey(fluid).toString();
        MooFluids.LOGGER.debug("fluid: {}", string);
        return string;
    }

    public static void createSpawnModifiers(MinecraftServer server, List<BiomeModifier> biomeModifiers1) {
        Map<ResourceLocation, ResourceLocation> spawnRestrictions = MFConfig.dimensionSpawnRestrictions;
        for (Map.Entry<ResourceLocation, ResourceLocation> entry : spawnRestrictions.entrySet()) {
            LevelStem levelStem = server.registryAccess().registryOrThrow(Registries.LEVEL_STEM).get(entry.getValue());
            Set<Holder<Biome>> biomes = levelStem.generator().getBiomeSource().possibleBiomes();

            biomeModifiers1.add(BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                    HolderSet.direct(List.copyOf(biomes)),
                    new MobSpawnSettings.SpawnerData(MFEntities.FLUID_COW.get(), 50, 16, 32)
            ));
        }
    }

}
