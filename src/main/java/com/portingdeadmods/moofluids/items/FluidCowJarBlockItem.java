package com.portingdeadmods.moofluids.items;

import com.portingdeadmods.moofluids.MooFluids;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FluidCowJarBlockItem extends BlockItem {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(MooFluids.MODID);

    public static final Supplier<DataComponentType<CompoundTag>> COW_JAR_DATA = DATA_COMPONENTS.registerComponentType("cow_jar_data", builder -> builder.persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG));


    public FluidCowJarBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        CompoundTag blockEntityData = stack.get(COW_JAR_DATA.get());
        if (blockEntityData != null && !blockEntityData.isEmpty()) {
            if (blockEntityData.getBoolean("HasCow")) {
                String fluidName = blockEntityData.getString("FluidCowFluid");
                if (!fluidName.isEmpty()) {
                    ResourceLocation fluidRl = ResourceLocation.tryParse(fluidName);
                    if (fluidRl != null) {
                        Fluid fluid = BuiltInRegistries.FLUID.get(fluidRl);
                        if (fluid != Fluids.EMPTY) {
                            tooltip.add(Component.translatable("tooltip.moofluids.fluid_cow_jar.contains", new FluidStack(fluid, 1).getDisplayName()));
                        }
                    }
                }
            }

            CompoundTag fluidTag = blockEntityData.getCompound("FluidTank");
            if (!fluidTag.isEmpty()) {
                FluidStack fluidStack = FluidStack.parse(context.registries(), fluidTag).orElse(FluidStack.EMPTY);
                if (!fluidStack.isEmpty()) {
                    tooltip.add(Component.translatable("tooltip.moofluids.fluid_cow_jar.fluid", fluidStack.getDisplayName()));
                    tooltip.add(Component.translatable("tooltip.moofluids.fluid_cow_jar.amount", fluidStack.getAmount()));
                }
            }
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new FluidCowJarBEWLR();
            }
        });
    }
}
