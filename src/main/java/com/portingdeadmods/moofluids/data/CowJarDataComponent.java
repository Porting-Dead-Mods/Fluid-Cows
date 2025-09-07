package com.portingdeadmods.moofluids.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

public record CowJarDataComponent(boolean hasCow, Fluid fluid, int capacity) {
    public static final Codec<CowJarDataComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.BOOL.fieldOf("has_cow").forGetter(CowJarDataComponent::hasCow),
            ResourceLocation.CODEC.xmap(BuiltInRegistries.FLUID::get, BuiltInRegistries.FLUID::getKey).fieldOf("cow_fluid").forGetter(CowJarDataComponent::fluid),
            Codec.INT.fieldOf("capacity").forGetter(CowJarDataComponent::capacity)
    ).apply(inst, CowJarDataComponent::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, CowJarDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            CowJarDataComponent::hasCow,
            ResourceLocation.STREAM_CODEC.map(BuiltInRegistries.FLUID::get, BuiltInRegistries.FLUID::getKey),
            CowJarDataComponent::fluid,
            ByteBufCodecs.INT,
            CowJarDataComponent::capacity,
            CowJarDataComponent::new
    );
}
