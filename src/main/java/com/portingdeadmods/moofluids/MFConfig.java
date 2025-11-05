package com.portingdeadmods.moofluids;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EventBusSubscriber(modid = MooFluids.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class MFConfig {
    public static final Pattern FLUIDNAME_REGEX = Pattern.compile("^[a-zA-Z0-9_]+:(\\*|[a-zA-Z0-9_]+)$");
    public static final Pattern DIMENSION_SPAWN_REGEX = Pattern.compile("^([a-zA-Z0-9_]+:[a-zA-Z0-9_]+)->([a-zA-Z0-9_]+:[a-zA-Z0-9_]+)$");

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.IntValue DEFAULT_MILKING_COOLDOWN = BUILDER
            .comment("The number of ticks before you can milk a fluid cow again")
            .defineInRange("defaultMilkingCooldown", 3600, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.BooleanValue NATURAL_SPAWNING = BUILDER
            .comment("Whether fluid cows should spawn naturally")
            .define("naturalSpawning", true);

    private static final ModConfigSpec.BooleanValue MILK_FROM_MODDED_COW = BUILDER
            .comment("Milk can be obtained from fluids cows when on cooldown.")
            .define("milkCow", false);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> SPAWN_BLACKLIST = BUILDER
            .comment("A list of modid:fluid to blacklist from spawning.")
            .comment("Can also use modid:* to disable all fluids from a mod.")
            .defineListAllowEmpty("fluidBlacklist", List.of(), () -> "", MFConfig::validateFluidName);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> DIMENSION_SPAWN_RESTRICTIONS = BUILDER
            .comment("Restrict specific fluid cows to spawn only in specific dimensions.")
            .comment("Format: 'modid:fluid->modid:dimension'")
            .comment("Example: 'minecraft:water->minecraft:overworld'")
            .comment("Example: 'minecraft:lava->minecraft:the_nether'")
            .comment("Example: 'kubejs:fluid_ender->minecraft:the_end'")
            .defineListAllowEmpty("dimensionSpawnRestrictions", List.of(), () -> "", MFConfig::validateDimensionSpawn);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> BIOME_SPAWN_BLACKLIST = BUILDER
            .comment("Blacklist specific biomes from spawning all fluid cows.")
            .comment("Format: 'modid:biome' or use tags like '#modid:biome_tag'")
            .comment("Example: 'minecraft:deep_dark'")
            .comment("Example: 'minecraft:mushroom_fields'")
            .comment("Example: '#minecraft:is_ocean' to blacklist all ocean biomes")
            .defineListAllowEmpty("biomeSpawnBlacklist", List.of("minecraft:deep_dark"), () -> "", obj -> obj instanceof String);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> BIOME_SPAWN_WHITELIST = BUILDER
            .comment("Whitelist specific biomes for spawning fluid cows (empty = all biomes allowed).")
            .comment("If this list is not empty, ONLY these biomes will allow fluid cow spawns.")
            .comment("Format: 'modid:biome' or use tags like '#modid:biome_tag'")
            .comment("Example: 'minecraft:plains'")
            .comment("Example: '#minecraft:is_overworld' to allow only overworld biomes")
            .defineListAllowEmpty("biomeSpawnWhitelist", List.of(), () -> "", obj -> obj instanceof String);

    public static final ModConfigSpec.IntValue COW_JAR_CAPACITY = BUILDER
            .comment("The amount of fluid the cow jar can hold")
            .defineInRange("cowJarCapacity", 32_000, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();
    public static boolean naturalSpawning;
    public static int defaultMilkingCooldown;
    public static boolean milkCow;
    public static Set<String> fluidBlacklist;
    public static Map<ResourceLocation, ResourceLocation> dimensionSpawnRestrictions;
    public static List<String> biomeSpawnBlacklist;
    public static List<String> biomeSpawnWhitelist;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        naturalSpawning = NATURAL_SPAWNING.get();
        defaultMilkingCooldown = DEFAULT_MILKING_COOLDOWN.get();
        milkCow = MILK_FROM_MODDED_COW.get();
        fluidBlacklist = new HashSet<>(SPAWN_BLACKLIST.get());

        dimensionSpawnRestrictions = new HashMap<>();
        for (String restriction : DIMENSION_SPAWN_RESTRICTIONS.get()) {
            String[] parts = restriction.split("->");
            if (parts.length == 2) {
                dimensionSpawnRestrictions.put(ResourceLocation.parse(parts[0].trim()), ResourceLocation.parse(parts[1].trim()));
            }
        }

        biomeSpawnBlacklist = new java.util.ArrayList<>(BIOME_SPAWN_BLACKLIST.get());
        biomeSpawnWhitelist = new java.util.ArrayList<>(BIOME_SPAWN_WHITELIST.get());
    }

    private static boolean validateFluidName(final Object obj) {
        if (obj instanceof final String fluidName) {
            Matcher matcher = FLUIDNAME_REGEX.matcher(fluidName);

            if (matcher.matches()) return true;

            return BuiltInRegistries.FLUID.containsKey(ResourceLocation.parse(fluidName));
        }
        return false;
    }

    private static boolean validateDimensionSpawn(final Object obj) {
        if (obj instanceof final String spawnConfig) {
            Matcher matcher = DIMENSION_SPAWN_REGEX.matcher(spawnConfig);
            return matcher.matches();
        }
        return false;
    }
}