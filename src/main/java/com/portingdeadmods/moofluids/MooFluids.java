package com.portingdeadmods.moofluids;

import com.portingdeadmods.moofluids.compat.top.MFTOPPlugin;
import com.portingdeadmods.moofluids.entity.MFEntities;
import com.portingdeadmods.moofluids.items.MFItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MooFluids.MODID)
public final class MooFluids {
    public static final String MODID = "moofluids";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // Deferred Registers


    public MooFluids() {
        ForgeMod.enableMilkFluid();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MFItems.ITEMS.register(modEventBus);
        MFEntities.ENTITIES.register(modEventBus);
        modEventBus.addListener(this::onLoadComplete);
        
        if (ModList.get().isLoaded("theoneprobe")) {
            MFTOPPlugin.registerCompatibility();
        }

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MFConfig.SPEC);
    }


    public void onLoadComplete(FMLLoadCompleteEvent event) {
        List<String> blackListedMods = new ArrayList<>();

        for (String blackListedFluid : MFConfig.fluidBlacklist) {
            if (blackListedFluid.contains("*"))
                blackListedMods.add(blackListedFluid.split(":")[0]);
        }

        for (Fluid fluid : ForgeRegistries.FLUIDS) {
            ResourceLocation key = ForgeRegistries.FLUID_TYPES.get().getKey(fluid.getFluidType());
            if (key != null) {
                String namespace = key.getNamespace();
                if (!MFConfig.fluidBlacklist.contains(Utils.idFromFluid(fluid)) && !blackListedMods.contains(namespace)) {
                    if (fluid.getBucket() != ItemStack.EMPTY.getItem()) {
                        Utils.add(fluid);
                    }
                }
            }
        }
    }
}
