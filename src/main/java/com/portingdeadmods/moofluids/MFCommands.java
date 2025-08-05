package com.portingdeadmods.moofluids;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.server.command.ConfigCommand;

import java.util.List;

@EventBusSubscriber(modid = MooFluids.MODID)
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
        List<ResourceLocation> fluids = BuiltInRegistries.FLUID.stream()
                .map(BuiltInRegistries.FLUID::getKey).toList();
        if (source.getPlayer() != null) {
            source.getPlayer().sendSystemMessage(Component.literal("Existing Fluids: "+fluids));
        } else {
            source.sendSystemMessage(Component.literal("Existing Fluids: "+fluids));
        }
        return 1;
    }

    private static int dumpAllCowFluids(CommandSourceStack source) {
        List<ResourceLocation> fluids = Utils.getFluids().stream().map(BuiltInRegistries.FLUID::getKey).toList();
        if (source.getPlayer() != null) {
            source.getPlayer().sendSystemMessage(Component.literal("Cow fluids: "+fluids));
        } else {
            source.sendSystemMessage(Component.literal("Cow fluids: "+fluids));
        }
        return 1;
    }
}