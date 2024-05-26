package com.portingdeadmods.moofluids.entity;

import com.portingdeadmods.moofluids.MFConfig;
import com.portingdeadmods.moofluids.MooFluids;
import com.portingdeadmods.moofluids.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;


public class FluidCow extends Cow {
    private static final EntityDataAccessor<String> FLUID_NAME = SynchedEntityData.defineId(FluidCow.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DELAY = SynchedEntityData.defineId(FluidCow.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CAN_BE_MILKED = SynchedEntityData.defineId(FluidCow.class, EntityDataSerializers.BOOLEAN);

    private static final String TAG_FLUID = "FluidRegistryName";
    private static final String TAG_DELAY = "CurrentDelay";

    public FluidCow(EntityType<? extends Cow> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Cow.createAttributes()
                .add(Attributes.FOLLOW_RANGE, 16F)
                .add(Attributes.MAX_HEALTH, 10F)
                .add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLUID_NAME, ForgeRegistries.FLUIDS.getKey(Fluids.EMPTY).toString());
        this.entityData.define(DELAY, MFConfig.defaultMilkingCooldown);
        this.entityData.define(CAN_BE_MILKED, true);
    }

    @Override
    @ParametersAreNonnullByDefault
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        SpawnGroupData spawnGroupData = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        if (!worldIn.isClientSide()) {
            this.setFluid(Utils.idFromFluid(getRandomFluid()));
            if (this.getDelay() < 0) {
                this.entityData.set(CAN_BE_MILKED, true);
            }
        }
        return spawnGroupData;
    }

    @Override
    public void finalizeSpawnChildFromBreeding(ServerLevel worldIn, Animal animal, @org.jetbrains.annotations.Nullable AgeableMob newMob) {
        if (!worldIn.isClientSide() && newMob instanceof FluidCow fluidCow) {
            fluidCow.setFluid(Utils.idFromFluid(getRandomFluid()));
            if (this.getDelay() < 0) {
                fluidCow.getEntityData().set(CAN_BE_MILKED, false);
            }
        }
        super.finalizeSpawnChildFromBreeding(worldIn, animal, newMob);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.getDelay() > 0 && !this.canBeMilked()) {
            this.decreaseDelay();
        } else {
            this.setCanBeMilked(true);
            this.setDelay(1000);
        }
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            MooFluids.LOGGER.info(this.getFluid());
            if (this.canBeMilked()
                    && this.getFluid() != Fluids.EMPTY
                    && hand == InteractionHand.MAIN_HAND
                    && player.getItemInHand(hand).getItem() == Items.BUCKET) {
                ItemStack stack = FluidUtil.getFilledBucket(new FluidStack(this.getFluid(), 1000));
                if (player.getItemInHand(hand).getCount() > 1 || player.isCreative()) {
                    int slotID = player.getInventory().getFreeSlot();
                    if (slotID != -1) {
                        player.getInventory().items.set(slotID, stack);
                        if (!player.isCreative())
                            player.getItemInHand(hand).shrink(1);
                        this.setCanBeMilked(false);
                    }
                } else {
                    player.setItemInHand(hand, stack);
                    this.setCanBeMilked(false);
                }
                return InteractionResult.SUCCESS;
            } else {
                return super.mobInteract(player, hand);
            }
        }
        return InteractionResult.FAIL;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.getFluid() == null ? FluidStack.EMPTY.getDisplayName() : this.getFluidStack().getDisplayName();
    }

    public boolean canBeMilked() {
        return this.entityData.get(CAN_BE_MILKED);
    }

    public void setCanBeMilked(boolean value) {
        this.entityData.set(CAN_BE_MILKED, value);
    }

    public void setFluid(String reg) {
        if (this.getFluid() == Fluids.EMPTY) {
            this.entityData.set(FLUID_NAME, reg);
        }
    }

    public Fluid getFluid() {
        if (this.entityData.get(FLUID_NAME).equals(ForgeRegistries.FLUIDS.getKey(Fluids.EMPTY).toString()))
            return Fluids.EMPTY;

        return Utils.get(this.entityData.get(FLUID_NAME));
    }

    public FluidStack getFluidStack() {
        return new FluidStack(this.getFluid(), FluidType.BUCKET_VOLUME);
    }

    public int getDelay() {
        return this.entityData.get(DELAY);
    }

    public void setDelay(int delay) {
        this.entityData.set(DELAY, delay);
    }

    public void decreaseDelay() {
        this.setDelay(this.getDelay() - 1);
    }

    public Fluid getRandomFluid() {
        var rnd = RandomSource.create();
        int rndVal = Mth.nextInt(rnd, 0, Utils.getFluids().size() - 1);
        return Utils.getFluids().get(rndVal);
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(TAG_DELAY, this.getDelay());
        if (ForgeRegistries.FLUIDS.getKey(this.getFluid()) == null) {
            compound.putString(TAG_FLUID, ForgeRegistries.FLUIDS.getKey(Fluids.EMPTY).toString());
        } else {
            compound.putString(TAG_FLUID, ForgeRegistries.FLUIDS.getKey(this.getFluid()).toString());
        }
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFluid(compound.getString(TAG_FLUID));
        this.setDelay(compound.getInt(TAG_DELAY));
    }

    @Nullable
    @Override
    public FluidCow getBreedOffspring(ServerLevel p_148890_, AgeableMob p_148891_) {
        return MFEntities.FLUID_COW.get().create(p_148890_);
    }

    public boolean isFood(ItemStack p_27600_) {
        return p_27600_.is(Items.WHEAT);
    }
}