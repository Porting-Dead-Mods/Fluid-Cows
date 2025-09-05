package com.portingdeadmods.moofluids;

import com.portingdeadmods.moofluids.block.MFBlocks;
import com.portingdeadmods.moofluids.block.entity.MFBlockEntities;
import com.portingdeadmods.moofluids.compat.top.MFTOPPlugin;
import com.portingdeadmods.moofluids.entity.MFEntities;
import com.portingdeadmods.moofluids.items.MFItems;
import com.portingdeadmods.moofluids.recipe.MFRecipes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MooFluids.MODID)
public final class MooFluids {
    public static final String MODID = "moofluids";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    // Deferred Registers
    public MooFluids(IEventBus modEventbus, ModContainer modContainer) {
        NeoForgeMod.enableMilkFluid();

        MFItems.ITEMS.register(modEventbus);
        MFBlocks.BLOCKS.register(modEventbus);
        MFBlocks.BLOCK_ITEMS.register(modEventbus);
        MFBlockEntities.BLOCK_ENTITIES.register(modEventbus);
        MFEntities.ENTITIES.register(modEventbus);
        MFRecipes.RECIPE_TYPES.register(modEventbus);
        MFRecipes.RECIPE_SERIALIZERS.register(modEventbus);
        
        if (ModList.get().isLoaded("theoneprobe")) {
            MFTOPPlugin.registerCompatibility();
        }

        modContainer.registerConfig(ModConfig.Type.COMMON, MFConfig.SPEC);
    }
}
