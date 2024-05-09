package com.portingdeadmods.moofluids;

import com.mojang.logging.LogUtils;
import com.portingdeadmods.moofluids.items.MFItems;
import com.portingdeadmods.moofluids.compat.top.MFTOPPlugin;
import com.portingdeadmods.moofluids.entity.MFEntities;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MooFluids.MODID)
public final class MooFluids {
    public static final String MODID = "moofluids";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Deferred Registers


    public MooFluids() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MFItems.ITEMS.register(modEventBus);
        MFItems.CREATIVE_MODE_TABS.register(modEventBus);
        MFEntities.ENTITIES.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        if (ModList.get().isLoaded("theoneprobe")) {
            MFTOPPlugin.registerCompatibility();
        }

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MFConfig.SPEC);
    }
}
