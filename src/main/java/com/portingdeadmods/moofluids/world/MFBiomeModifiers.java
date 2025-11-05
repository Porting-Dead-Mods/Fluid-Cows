package com.portingdeadmods.moofluids.world;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.moofluids.MooFluids;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class MFBiomeModifiers {
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MooFluids.MODID);

    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<FluidCowSpawnBiomeModifier>> FLUID_COW_SPAWN =
            BIOME_MODIFIER_SERIALIZERS.register("fluid_cow_spawn", () -> FluidCowSpawnBiomeModifier.CODEC);
}
