package com.portingdeadmods.moofluids.entity;

import com.portingdeadmods.moofluids.MooFluids;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class MFEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MooFluids.MODID);

    public static final Supplier<EntityType<FluidCow>> FLUID_COW = register("fluid_cow", EntityType.Builder.of(FluidCow::new, MobCategory.CREATURE));

    private static <T extends Entity> Supplier<EntityType<T>> register(String name, EntityType.Builder<T> builder) {
        return ENTITIES.register(name, () -> builder.build(MooFluids.MODID + ":" + name));
    }
}
