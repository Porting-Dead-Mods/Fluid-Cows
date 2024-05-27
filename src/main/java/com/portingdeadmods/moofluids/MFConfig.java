package com.portingdeadmods.moofluids;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;


// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = MooFluids.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class MFConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.IntValue DEFAULT_MILKING_COOLDOWN = BUILDER
            .comment("The number of ticks before you can milk a fluid cow agan")
            .defineInRange("defaultMilkingCooldown", 3600, 0, Integer.MAX_VALUE);

    static List<String> test = new ArrayList<>();

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> SPAWN_BLACKLIST = BUILDER
            .comment("A list of modid:fluid to blacklist from spawning.")
            .defineList("fluidBlacklist", test, obj -> validateRuleSyntax((String)obj));


    static final ForgeConfigSpec SPEC = BUILDER.build();
    public static int defaultMilkingCooldown;
    public static List fluidBlacklist;
    public static int spawnRate = 100;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        defaultMilkingCooldown = DEFAULT_MILKING_COOLDOWN.get();
        fluidBlacklist = SPAWN_BLACKLIST.get();
    }

    public static boolean validateRuleSyntax (String rule) {
        String[] parts = rule.split("\\s*,\\s*");
        if (parts.length != 3)
            return false;

        ResourceLocation upperResource = ResourceLocation.tryParse(parts[0]);
        ResourceLocation lowerResource = ResourceLocation.tryParse(parts[1]);
        if (upperResource == null || lowerResource == null)
            return false;

        try {
            int conv = Integer.parseInt(parts[2]);
            return conv >= 1;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

}
