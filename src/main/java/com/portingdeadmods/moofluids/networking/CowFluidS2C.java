package com.portingdeadmods.moofluids.networking;

import com.portingdeadmods.moofluids.entity.FluidCow;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record CowFluidS2C(FluidStack fluidStack, int entityId) {
    public CowFluidS2C(FriendlyByteBuf buf) {
        this(buf.readFluidStack(), buf.readInt());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFluidStack(fluidStack);
        buf.writeInt(entityId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(entityId);
            if (entity instanceof FluidCow fluidCow) {
                fluidCow.setCowFluid(fluidStack.getFluid());
            }
        });
        return true;
    }
}
