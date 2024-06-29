package com.portingdeadmods.moofluids.compat.jade;

import com.portingdeadmods.moofluids.entity.FluidCow;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

public enum MFJadePlugin implements IEntityComponentProvider{
    INSTANCE;

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        Entity entity = entityAccessor.getEntity();
        if (entity instanceof FluidCow fluidCow){
            boolean canBeMilk = fluidCow.canBeMilked();
            iTooltip.add(Component.literal("Fluid: ").append(fluidCow.getFluid().getFluidType().toString()));
            if(canBeMilk){
                iTooltip.add(Component.literal("Can be Milked"));
            }else{
                iTooltip.add(Component.literal("Cooldown: " + (fluidCow.getDelay() / 20) / 60 + "m " + (fluidCow.getDelay() / 20) % 60 + "s"));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation("moofluids", "fluid_cow");

    }
}