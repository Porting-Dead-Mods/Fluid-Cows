package com.portingdeadmods.moofluids;

import com.portingdeadmods.moofluids.items.MFItems;
import com.portingdeadmods.moofluids.compat.top.MFTOPPlugin;
import com.portingdeadmods.moofluids.entity.MFEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MooFluids.MODID)
public final class MooFluids {
    public static final String MODID = "moofluids";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // Deferred Registers

    public MooFluids(IEventBus modEventbus, ModContainer modContainer) {
        NeoForgeMod.enableMilkFluid();

        MFItems.ITEMS.register(modEventbus);
        MFEntities.ENTITIES.register(modEventbus);
        
        if (ModList.get().isLoaded("theoneprobe")) {
            //MFTOPPlugin.registerCompatibility();
        }

        modContainer.registerConfig(ModConfig.Type.COMMON, MFConfig.SPEC);
    }
}
