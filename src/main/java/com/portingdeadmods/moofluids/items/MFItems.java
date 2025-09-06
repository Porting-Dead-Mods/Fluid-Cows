package com.portingdeadmods.moofluids.items;

import com.portingdeadmods.moofluids.MooFluids;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.portingdeadmods.moofluids.entity.MFEntities.FLUID_COW;

public final class MFItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MooFluids.MODID);

    public static final DeferredItem<DeferredSpawnEggItem> FLUID_COW_SPAWN_EGG = ITEMS.register("fluid_cow_spawn_egg",
            () -> new DeferredSpawnEggItem(FLUID_COW, 0X36302A, 0XD4B183, new Item.Properties()));
}
