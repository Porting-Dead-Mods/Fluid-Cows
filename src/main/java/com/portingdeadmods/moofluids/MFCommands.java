package com.portingdeadmods.moofluids;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.command.ConfigCommand;

import java.util.List;

@Mod.EventBusSubscriber(modid = MooFluids.MODID)
public final class MFCommands {
    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        dumpFluidCommand(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }

    private static void dumpFluidCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("moofluids")
                .then(Commands.literal("getAllExistingFluids")
                        .executes(commandContext -> dumpAllExistingFluids(commandContext.getSource()))));
        dispatcher.register(Commands.literal("moofluids")
                .then(Commands.literal("getAllCowFluids")
                        .executes(commandContext -> dumpAllCowFluids(commandContext.getSource()))));
    }

    private static int dumpAllExistingFluids(CommandSourceStack source) {
        List<ResourceLocation> fluids = ForgeRegistries.FLUID_TYPES.get().getValues().stream()
                .map(fluidType -> ForgeRegistries.FLUID_TYPES.get().getKey(fluidType)).toList();
        source.getPlayer().sendSystemMessage(Component.literal("Existing Fluids: "+fluids));
        return 1;
    }

    private static int dumpAllCowFluids(CommandSourceStack source) {
        List<ResourceLocation> fluids = Utils.getFluids().stream().map(fluid -> ForgeRegistries.FLUID_TYPES.get().getKey(fluid.getFluidType())).toList();
        source.getPlayer().sendSystemMessage(Component.literal("Cow fluids: "+fluids));
        return 1;
    }
}