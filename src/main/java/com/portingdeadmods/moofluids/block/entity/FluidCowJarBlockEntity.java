package com.portingdeadmods.moofluids.block.entity;

import com.portingdeadmods.moofluids.MFConfig;
import com.portingdeadmods.moofluids.entity.FluidCow;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class FluidCowJarBlockEntity extends BlockEntity {

    protected static final int FLUID_CAPACITY = 32000;
    private static final int UPDATE_INTERVAL = 20;
    private static final String TAG_FLUID_COW_FLUID = "FluidCowFluid";
    private static final String TAG_HAS_COW = "HasCow";
    private static final String TAG_CUSTOM_NAME = "CustomName";
    private static final String TAG_FLUID_TANK = "FluidTank";
    private static final String TAG_MILKING_COOLDOWN = "MilkingCooldown";
    private static final String TAG_CAN_BE_MILKED = "CanBeMilked";

    protected final FluidTank fluidTank = new FluidTank(FLUID_CAPACITY) {
        @Override
        protected void onContentsChanged() {
            FluidCowJarBlockEntity.this.setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };
    
    private Fluid cowFluid = Fluids.EMPTY;
    private boolean hasCow = false;
    private boolean isDirty;
    private int ticksSinceUpdate;
    private Component customName;
    private int milkingCooldown = 0;
    private boolean canBeMilked = true;

    public FluidCowJarBlockEntity(BlockPos pos, BlockState state) {
        this(MFBlockEntities.FLUID_COW_JAR.get(), pos, state);
    }

    public FluidCowJarBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        
        CompoundTag tankTag = new CompoundTag();
        fluidTank.writeToNBT(registries, tankTag);
        tag.put(TAG_FLUID_TANK, tankTag);
        
        if (cowFluid != Fluids.EMPTY) {
            tag.putString(TAG_FLUID_COW_FLUID, BuiltInRegistries.FLUID.getKey(cowFluid).toString());
        }
        tag.putBoolean(TAG_HAS_COW, hasCow);
        
        if (customName != null) {
            tag.putString(TAG_CUSTOM_NAME, Component.Serializer.toJson(customName, registries));
        }
        
        tag.putInt(TAG_MILKING_COOLDOWN, milkingCooldown);
        tag.putBoolean(TAG_CAN_BE_MILKED, canBeMilked);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        
        if (tag.contains(TAG_FLUID_TANK)) {
            fluidTank.readFromNBT(registries, tag.getCompound(TAG_FLUID_TANK));
        }
        
        String fluidName = tag.getString(TAG_FLUID_COW_FLUID);
        if (!fluidName.isEmpty()) {
            ResourceLocation fluidResourceLocation = ResourceLocation.tryParse(fluidName);
            if (fluidResourceLocation != null) {
                cowFluid = BuiltInRegistries.FLUID.get(fluidResourceLocation);
                if (cowFluid == null) {
                    cowFluid = Fluids.EMPTY;
                }
            }
        }
        
        hasCow = tag.getBoolean(TAG_HAS_COW);
        
        if (tag.contains(TAG_CUSTOM_NAME)) {
            customName = Component.Serializer.fromJson(tag.getString(TAG_CUSTOM_NAME), registries);
        }
        
        milkingCooldown = tag.getInt(TAG_MILKING_COOLDOWN);
        canBeMilked = tag.getBoolean(TAG_CAN_BE_MILKED);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FluidCowJarBlockEntity blockEntity) {
        blockEntity.serverTick(level, pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (hasCow && cowFluid != Fluids.EMPTY && fluidTank.getFluidAmount() < FLUID_CAPACITY) {
            FluidStack toAdd = new FluidStack(cowFluid, 1);
            int filled = fluidTank.fill(toAdd, IFluidHandler.FluidAction.EXECUTE);
            
            if (filled > 0) {
                isDirty = true;
            }
        }

        ticksSinceUpdate++;
        if (isDirty && ticksSinceUpdate > UPDATE_INTERVAL) {
            level.sendBlockUpdated(pos, state, state, 3);
            ticksSinceUpdate = 0;
            isDirty = false;
        }
    }

    public boolean addFluidCow(FluidCow cow) {
        if (!hasCow) {
            this.cowFluid = cow.getFluid();
            this.hasCow = true;
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
            return true;
        }
        return false;
    }

    public void removeFluidCow() {
        this.hasCow = false;
        this.cowFluid = Fluids.EMPTY;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean hasCow() {
        return hasCow;
    }

    public Fluid getCowFluid() {
        return cowFluid;
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public void setCustomName(Component customName) {
        this.customName = customName;
        setChanged();
    }

    public Component getCustomName() {
        return customName;
    }

    public boolean hasCustomName() {
        return customName != null;
    }

    public Component getDisplayName() {
        return getName();
    }

    public Component getName() {
        return customName != null ? customName : getDefaultName();
    }

    public Component getDefaultName() {
        if (hasCow) {
            return Component.translatable("block.moofluids.fluid_cow_jar_filled");
        }
        return Component.translatable("block.moofluids.fluid_cow_jar");
    }

    public boolean canBeMilked() {
        return true;
    }

    public int getMilkingCooldown() {
        return 0;
    }
}