package com.portingdeadmods.moofluids.block.entity;

import com.portingdeadmods.moofluids.MooFluids;
import com.portingdeadmods.moofluids.block.MFBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MFBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MooFluids.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidCowJarBlockEntity>> FLUID_COW_JAR =
            BLOCK_ENTITIES.register("fluid_cow_jar", () -> BlockEntityType.Builder.of(
                    FluidCowJarBlockEntity::new,
                    MFBlocks.FLUID_COW_JAR.get()
            ).build(null));
}