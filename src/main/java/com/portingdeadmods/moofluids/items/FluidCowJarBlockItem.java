package com.portingdeadmods.moofluids.items;

import com.portingdeadmods.moofluids.FluidUtils;
import com.portingdeadmods.moofluids.MooFluids;
import com.portingdeadmods.moofluids.data.CowJarDataComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class FluidCowJarBlockItem extends BlockItem {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(MooFluids.MODID);

    public static final Supplier<DataComponentType<CowJarDataComponent>> COW_JAR_DATA = DATA_COMPONENTS.registerComponentType("cow_jar_data", builder -> builder
            .persistent(CowJarDataComponent.CODEC)
            .networkSynchronized(CowJarDataComponent.STREAM_CODEC));
    public static final Supplier<DataComponentType<SimpleFluidContent>> FLUID_TANK = DATA_COMPONENTS.registerComponentType("fluid_tank", builder -> builder
            .persistent(SimpleFluidContent.CODEC)
            .networkSynchronized(SimpleFluidContent.STREAM_CODEC));

    public FluidCowJarBlockItem(Block block, Properties properties) {
        super(block, properties.component(COW_JAR_DATA, new CowJarDataComponent(false, Fluids.EMPTY, 0)).component(FLUID_TANK, SimpleFluidContent.EMPTY));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        CowJarDataComponent cowJarDataComponent = stack.get(COW_JAR_DATA.get());
        Fluid fluid = cowJarDataComponent.fluid();
        if (fluid != Fluids.EMPTY) {
            FluidStack fluidStack = new FluidStack(fluid, 1);
            int color = FluidUtils.getFluidColor(fluid);
            tooltip.add(Component.translatable("tooltip.moofluids.fluid_cow_jar.contains", fluidStack.getDisplayName().copy().setStyle(Style.EMPTY.withColor(color))));
            tooltip.add(Component.translatable("tooltip.moofluids.fluid_cow_jar.release").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("tooltip.moofluids.fluid_cow_jar.hint").withStyle(ChatFormatting.GRAY));
        }

        FluidStack fluidStack = stack.get(FLUID_TANK).copy();
        if (!fluidStack.isEmpty()) {
            int color = FluidUtils.getFluidColor(fluidStack.getFluid());
            tooltip.add(Component.translatable("tooltip.moofluids.fluid_cow_jar.fluid", fluidStack.getDisplayName().copy().setStyle(Style.EMPTY.withColor(color))));
            tooltip.add(Component.translatable("tooltip.moofluids.fluid_cow_jar.amount", fluidStack.getAmount()));
        }
    }
}

