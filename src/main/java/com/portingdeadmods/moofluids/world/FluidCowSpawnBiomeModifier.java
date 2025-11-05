package com.portingdeadmods.moofluids.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.moofluids.MFConfig;
import com.portingdeadmods.moofluids.MooFluids;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.common.world.MobSpawnSettingsBuilder;

import java.util.List;

public record FluidCowSpawnBiomeModifier(
        HolderSet<Biome> biomes,
        List<MobSpawnSettings.SpawnerData> spawners,
        List<String> blacklistedBiomes,
        List<String> whitelistedBiomes
) implements BiomeModifier {

    public static final MapCodec<FluidCowSpawnBiomeModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Biome.LIST_CODEC.fieldOf("biomes").forGetter(FluidCowSpawnBiomeModifier::biomes),
                    MobSpawnSettings.SpawnerData.CODEC.listOf().fieldOf("spawners").forGetter(FluidCowSpawnBiomeModifier::spawners),
                    Codec.STRING.listOf().lenientOptionalFieldOf("blacklisted_biomes", List.of()).forGetter(FluidCowSpawnBiomeModifier::blacklistedBiomes),
                    Codec.STRING.listOf().lenientOptionalFieldOf("whitelisted_biomes", List.of()).forGetter(FluidCowSpawnBiomeModifier::whitelistedBiomes)
            ).apply(instance, FluidCowSpawnBiomeModifier::new)
    );

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD) return;
        if (!this.biomes.contains(biome)) return;
        if (!MFConfig.naturalSpawning) return;

        ResourceLocation biomeId = biome.unwrapKey()
                .map(ResourceKey::location)
                .orElse(null);

        if (biomeId == null) return;

        if (shouldBlockSpawn(biome, biomeId)) return;

        MobSpawnSettingsBuilder spawnSettings = builder.getMobSpawnSettings();
        for (MobSpawnSettings.SpawnerData spawner : this.spawners) {
            EntityType<?> type = spawner.type;
            spawnSettings.addSpawn(type.getCategory(), spawner);
        }
    }

    private boolean shouldBlockSpawn(Holder<Biome> biome, ResourceLocation biomeId) {
        List<String> blacklist = MFConfig.biomeSpawnBlacklist;
        List<String> whitelist = MFConfig.biomeSpawnWhitelist;

        if (!whitelist.isEmpty()) {
            boolean isWhitelisted = false;
            for (String entry : whitelist) {
                if (entry.startsWith("#")) {
                    if (isBiomeInTag(biome, entry.substring(1))) {
                        isWhitelisted = true;
                        break;
                    }
                } else {
                    if (biomeId.toString().equals(entry)) {
                        isWhitelisted = true;
                        break;
                    }
                }
            }
            if (!isWhitelisted) return true;
        }

        for (String entry : blacklist) {
            if (entry.startsWith("#")) {
                if (isBiomeInTag(biome, entry.substring(1))) {
                    return true;
                }
            } else {
                if (biomeId.toString().equals(entry)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isBiomeInTag(Holder<Biome> biomeHolder, String tagId) {
        try {
            ResourceLocation tagLocation = ResourceLocation.parse(tagId);
            ResourceKey<Biome> tagKey = ResourceKey.create(Registries.BIOME, tagLocation);
            return biomeHolder.is(tagKey);
        } catch (Exception e) {
            MooFluids.LOGGER.error("Failed to check biome tag: {}", tagId, e);
        }
        return false;
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}
