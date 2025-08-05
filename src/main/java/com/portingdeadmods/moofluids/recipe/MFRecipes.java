package com.portingdeadmods.moofluids.recipe;

import com.portingdeadmods.moofluids.MooFluids;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MFRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = 
        DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, MooFluids.MODID);
    
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = 
        DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MooFluids.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<AlloyRecipe>> ALLOY_TYPE = 
        RECIPE_TYPES.register("alloy", () -> RecipeType.simple(ResourceLocation.parse(MooFluids.MODID + ":alloy")));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlloyRecipe>> ALLOY_SERIALIZER = 
        RECIPE_SERIALIZERS.register("alloy", AlloyRecipe.Serializer::new);
}