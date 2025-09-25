package com.portingdeadmods.moofluids;

import com.portingdeadmods.moofluids.block.MFBlocks;
import com.portingdeadmods.moofluids.items.MFItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MFCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MooFluids.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOO_FLUIDS_TAB = 
        CREATIVE_MODE_TABS.register("moo_fluids", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.moofluids.moo_fluids"))
            .icon(() -> new ItemStack(MFBlocks.FLUID_COW_JAR.get().asItem()))
            .displayItems((parameters, output) -> {
                output.accept(MFItems.FLUID_COW_SPAWN_EGG.get());
                output.accept(MFItems.ALCHEMY_GLASS.get());
                output.accept(MFBlocks.FLUID_COW_JAR.get());
            })
            .build());
}