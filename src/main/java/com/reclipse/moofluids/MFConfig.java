package com.reclipse.moofluids;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;


// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = MooFluids.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MFConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.IntValue DEFAULT_MILKING_COOLDOWN = BUILDER
            .comment("The number of ticks before you can milk a fluid cow agan")
            .defineInRange("defaultMilkingCooldown", 3600, 0, Integer.MAX_VALUE);
    static final ForgeConfigSpec SPEC = BUILDER.build();
    public static int defaultMilkingCooldown;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        defaultMilkingCooldown = DEFAULT_MILKING_COOLDOWN.get();
    }
}
