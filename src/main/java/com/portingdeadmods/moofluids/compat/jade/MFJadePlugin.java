package com.portingdeadmods.moofluids.compat.jade;

import com.portingdeadmods.moofluids.FluidUtils;
import com.portingdeadmods.moofluids.entity.FluidCow;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

public enum MFJadePlugin implements IEntityComponentProvider{
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        Entity entity = entityAccessor.getEntity();
        if (entity instanceof FluidCow fluidCow){
            boolean canBeMilk = fluidCow.canBeMilked();
            int color = FluidUtils.getFluidColor(fluidCow.getFluid());
            iTooltip.add(Component.literal("Fluid: ").append(Component.translatable(fluidCow.getFluid().getFluidType().getDescriptionId())
                    .withStyle(Style.EMPTY.withColor(color))));
            if(canBeMilk){
                iTooltip.add(Component.literal("Can be Milked"));
            }else{
                iTooltip.add(Component.literal("Cooldown: " + (fluidCow.getDelay() / 20) / 60 + "m " + (fluidCow.getDelay() / 20) % 60 + "s"));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("moofluids", "fluid_cow");
    }
}