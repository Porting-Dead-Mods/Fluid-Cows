package com.portingdeadmods.moofluids;

import com.portingdeadmods.moofluids.block.MFBlocks;
import com.portingdeadmods.moofluids.block.entity.MFBlockEntities;
import com.portingdeadmods.moofluids.compat.top.MFTOPPlugin;
import com.portingdeadmods.moofluids.entity.MFEntities;
import com.portingdeadmods.moofluids.items.FluidCowJarBEWLR;
import com.portingdeadmods.moofluids.items.FluidCowJarBlockItem;
import com.portingdeadmods.moofluids.items.MFItems;
import com.portingdeadmods.moofluids.recipe.MFRecipes;
import com.portingdeadmods.moofluids.world.MFBiomeModifiers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MooFluids.MODID)
public final class MooFluids {
    public static final String MODID = "moofluids";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // Deferred Registers
    public MooFluids(IEventBus modEventbus, ModContainer modContainer) {
        NeoForgeMod.enableMilkFluid();

        MFItems.ITEMS.register(modEventbus);
        FluidCowJarBlockItem.DATA_COMPONENTS.register(modEventbus);
        MFBlocks.BLOCKS.register(modEventbus);
        MFBlocks.BLOCK_ITEMS.register(modEventbus);
        MFBlockEntities.BLOCK_ENTITIES.register(modEventbus);
        MFEntities.ENTITIES.register(modEventbus);
        MFRecipes.RECIPE_TYPES.register(modEventbus);
        MFRecipes.RECIPE_SERIALIZERS.register(modEventbus);
        MFCreativeTabs.CREATIVE_MODE_TABS.register(modEventbus);
        MFBiomeModifiers.BIOME_MODIFIER_SERIALIZERS.register(modEventbus);
        
        if (ModList.get().isLoaded("theoneprobe")) {
            MFTOPPlugin.registerCompatibility();
        }

        modContainer.registerConfig(ModConfig.Type.COMMON, MFConfig.SPEC);
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientSetup {

        private static final FluidCowJarBEWLR TANK_RENDERER = new FluidCowJarBEWLR();


        @SubscribeEvent
        public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
            LOGGER.info("Registering client extensions");
            event.registerItem(new IClientItemExtensions() {
                @Override
                public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    return TANK_RENDERER;
                }
            }, MFBlocks.FLUID_COW_JAR.get().asItem());
        }
    }
}
