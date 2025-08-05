package com.portingdeadmods.moofluids.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import java.util.ArrayList;

import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import java.util.Comparator;
import java.util.List;

public record AlloyRecipe(List<Fluid> inputs, Fluid output, float successChance) implements Recipe<AlloyRecipeInput> {

    @Override
    public boolean matches(AlloyRecipeInput input, Level level) {
        if (input.fluids().size() != inputs.size()) return false;
        
        List<Fluid> inputFluids = input.fluids().stream()
            .sorted(Comparator.comparing(BuiltInRegistries.FLUID::getKey))
            .toList();
        
        List<Fluid> recipeFluids = inputs.stream()
            .sorted(Comparator.comparing(BuiltInRegistries.FLUID::getKey))
            .toList();
        
        return inputFluids.equals(recipeFluids);
    }

    @Override
    public ItemStack assemble(AlloyRecipeInput input, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MFRecipes.ALLOY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return MFRecipes.ALLOY_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<AlloyRecipe> {
        public static final MapCodec<AlloyRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> 
            instance.group(
                BuiltInRegistries.FLUID.byNameCodec().listOf().fieldOf("inputs").forGetter(AlloyRecipe::inputs),
                BuiltInRegistries.FLUID.byNameCodec().fieldOf("output").forGetter(AlloyRecipe::output),
                ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("success_chance", 1.0f).forGetter(AlloyRecipe::successChance)
            ).apply(instance, AlloyRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, AlloyRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.registry(Registries.FLUID)),
            AlloyRecipe::inputs,
            ByteBufCodecs.registry(Registries.FLUID),
            AlloyRecipe::output,
            ByteBufCodecs.FLOAT,
            AlloyRecipe::successChance,
            AlloyRecipe::new
        );

        @Override
        public MapCodec<AlloyRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AlloyRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}