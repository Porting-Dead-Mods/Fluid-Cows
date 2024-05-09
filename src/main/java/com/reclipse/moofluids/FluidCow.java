package com.reclipse.moofluids;

import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import org.jetbrains.annotations.NotNull;

public class FluidCow extends Cow {
    private static final int MILKING_COOLDOWN = MFConfig.defaultMilkingCooldown;
    private final FluidTank cowTank = new FluidTank(Integer.MAX_VALUE) {
        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return stack.getFluid().equals(Fluids.WATER);
        }
    };
    private LazyOptional<IFluidHandler> lazyFluidHandler;

    public FluidCow(EntityType<? extends Cow> p_28285_, Level p_28286_) {
        super(p_28285_, p_28286_);
        this.lazyFluidHandler = LazyOptional.of(() -> cowTank);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.20000000298023224);
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return cap == ForgeCapabilities.FLUID_HANDLER ? LazyOptional.of(() -> cowTank).cast() : super.getCapability(cap);
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        IFluidHandler cowTankHandler = getCapability(ForgeCapabilities.FLUID_HANDLER).orElseThrow(NullPointerException::new);

        if (itemstack.is(Items.LAVA_BUCKET) && !this.isBaby()) {
            cowTankHandler.fill(new FluidStack(Fluids.LAVA, 1000), IFluidHandler.FluidAction.EXECUTE);
            player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
            MooFluids.LOGGER.debug("Fluid in tank: " + Utils.fluidStackToString(cowTankHandler.getFluidInTank(0)));
        }
        if (itemstack.is(Items.BUCKET) && !this.isBaby()) {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            ItemStack fluidBucket = ItemUtils.createFilledResult(itemstack, player, Items.LAVA_BUCKET.getDefaultInstance());
            player.setItemInHand(hand, fluidBucket);
            cowTankHandler.drain(new FluidStack(Fluids.LAVA, 1000), IFluidHandler.FluidAction.EXECUTE);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        lazyFluidHandler = LazyOptional.of(() -> cowTank);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        CompoundTag subTag = new CompoundTag();
        cowTank.writeToNBT(subTag);
        tag.put("FluidHandler", subTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        CompoundTag subTag = tag.getCompound("FluidHandler");
        cowTank.readFromNBT(subTag);
    }
}