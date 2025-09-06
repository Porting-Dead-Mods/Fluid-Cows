package com.portingdeadmods.moofluids.block;

import com.portingdeadmods.moofluids.MooFluids;
import com.portingdeadmods.moofluids.items.FluidCowJarBlockItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class MFBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(BuiltInRegistries.BLOCK, MooFluids.MODID);

    public static final DeferredRegister<Item> BLOCK_ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, MooFluids.MODID);

    public static final DeferredHolder<Block, FluidCowJarBlock> FLUID_COW_JAR = registerBlock("fluid_cow_jar",
            () -> new FluidCowJarBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(0.6f)
                    .noOcclusion()),
            FluidCowJarBlockItem::new);

    private static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> block) {
        return registerBlock(name, block, BlockItem::new);
    }

    private static <T extends Block> DeferredHolder<Block, T> registerBlock(String name, Supplier<T> block, BiFunction<Block, Item.Properties, Item> blockItemFactory) {
        DeferredHolder<Block, T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, blockItemFactory);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredHolder<Block, T> block, BiFunction<Block, Item.Properties, Item> blockItemFactory) {
        BLOCK_ITEMS.register(name, () -> blockItemFactory.apply(block.get(), new Item.Properties()));
    }
}