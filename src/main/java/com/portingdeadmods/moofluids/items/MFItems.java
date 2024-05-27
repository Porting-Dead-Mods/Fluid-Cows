package com.portingdeadmods.moofluids.items;

import com.portingdeadmods.moofluids.MooFluids;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.portingdeadmods.moofluids.entity.MFEntities.FLUID_COW;

public final class MFItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MooFluids.MODID);

    public static final RegistryObject<ForgeSpawnEggItem> FLUID_COW_SPAWN_EGG = ITEMS.register("fluid_cow_spawn_egg",
            () -> new ForgeSpawnEggItem(FLUID_COW, 0X36302A, 0XD4B183, new Item.Properties()));
}
