package com.portingdeadmods.moofluids;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = MooFluids.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class MFConfig {
    public static final Pattern FLUIDNAME_REGEX = Pattern.compile("^[a-zA-Z0-9_]+:(\\*|[a-zA-Z0-9_]+)$");

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.IntValue DEFAULT_MILKING_COOLDOWN = BUILDER
            .comment("The number of ticks before you can milk a fluid cow agan")
            .defineInRange("defaultMilkingCooldown", 3600, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> SPAWN_BLACKLIST = BUILDER
            .comment("A list of modid:fluid to blacklist from spawning.")
            .comment("Can also use modid:* to disable all fluids from a mod.")
            .defineListAllowEmpty("fluidBlacklist", List.of(), MFConfig::validateFluidName);


    static final ForgeConfigSpec SPEC = BUILDER.build();
    public static int defaultMilkingCooldown;
    public static Set<String> fluidBlacklist;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        defaultMilkingCooldown = DEFAULT_MILKING_COOLDOWN.get();
        fluidBlacklist = new HashSet<>(SPAWN_BLACKLIST.get());
    }

    private static boolean validateFluidName(final Object obj) {
        if (obj instanceof final String fluidName) {
            Matcher matcher = FLUIDNAME_REGEX.matcher(fluidName);

            if (matcher.matches()) return true;

            return ForgeRegistries.FLUIDS.containsKey(new ResourceLocation(fluidName));
        }
        return false;
    }
}
