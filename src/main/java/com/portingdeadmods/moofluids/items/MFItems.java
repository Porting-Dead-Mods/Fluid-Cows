package com.portingdeadmods.moofluids.items;

import com.portingdeadmods.moofluids.MooFluids;
import com.portingdeadmods.moofluids.Utils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

import static com.portingdeadmods.moofluids.entity.MFEntities.FLUID_COW;

public final class MFItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MooFluids.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MooFluids.MODID);

    public static final RegistryObject<ForgeSpawnEggItem> FLUID_COW_SPAWN_EGG = ITEMS.register("fluid_cow_spawn_egg",
            () -> new ForgeSpawnEggItem(FLUID_COW, 0X36302A, 0XD4B183, new Item.Properties()));
    public static final RegistryObject<Item> DEBUG_ITEM = ITEMS.register("debug_item",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<CreativeModeTab> MOOFLUIDS_TAB = CREATIVE_MODE_TABS.register("moofluids_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.moofluids"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(Items.NETHER_STAR::getDefaultInstance)
            .displayItems((parameters, output) -> {
                output.accept(FLUID_COW_SPAWN_EGG.get());
            }).build());
}
