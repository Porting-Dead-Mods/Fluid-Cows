package com.reclipse.moofluids;

import com.reclipse.moofluids.utils.FluidCapacity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;


import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class FluidCow extends Cow {
    public FluidCow(EntityType<? extends Cow> p_28285_, Level p_28286_) {
        super(p_28285_, p_28286_);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
    }

    int milkingCooldown = MFConfig.defaultMilkingCooldown;

    FluidTank cowTank = new FluidTank(Integer.MAX_VALUE){
        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return stack.getFluid().equals(Fluids.WATER);
        }
    };

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return LazyOptional.of(()->cowTank).cast();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        IFluidHandler cowTankHandler = (IFluidHandler) getCapability(null).orElseThrow(NullPointerException::new);

        if(itemstack.is(Items.LAVA_BUCKET) && !this.isBaby()){
            cowTankHandler.fill(new FluidStack(Fluids.LAVA, 1000), IFluidHandler.FluidAction.EXECUTE);
            player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
            System.out.println(cowTank.getFluid().getFluid().getFluidType());
        }
        if (itemstack.is(Items.BUCKET) && !this.isBaby()) {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            ItemStack fluidBucket = ItemUtils.createFilledResult(itemstack, player, Items.LAVA_BUCKET.getDefaultInstance());
            player.setItemInHand(hand, fluidBucket);
            cowTankHandler.drain(new FluidStack(Fluids.LAVA,1000),IFluidHandler.FluidAction.EXECUTE);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }
}