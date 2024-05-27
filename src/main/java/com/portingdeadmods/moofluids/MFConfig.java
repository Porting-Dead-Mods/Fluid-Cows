package com.portingdeadmods.moofluids;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = MooFluids.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class MFConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.IntValue DEFAULT_MILKING_COOLDOWN = BUILDER
            .comment("The number of ticks before you can milk a fluid cow agan")
            .defineInRange("defaultMilkingCooldown", 3600, 0, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> SPAWN_BLACKLIST = BUILDER
            .comment("A list of modid:fluid to blacklist from spawning.")
            .defineListAllowEmpty("fluidBlacklist", List.of(), MFConfig::validateEntityName);


    static final ForgeConfigSpec SPEC = BUILDER.build();
    public static int defaultMilkingCooldown;
    public static Set<String> fluidBlacklist;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        defaultMilkingCooldown = DEFAULT_MILKING_COOLDOWN.get();
        fluidBlacklist = SPAWN_BLACKLIST.get().stream()
                .map(str -> (String) str)
                .collect(Collectors.toSet());
    }

    private static boolean validateEntityName(final Object obj) {
        return obj instanceof final String fluidName
                && ForgeRegistries.FLUIDS.containsKey(new ResourceLocation(fluidName));
    }
}
