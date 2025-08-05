package com.portingdeadmods.moofluids.entity;

import com.google.common.collect.ImmutableList;
import com.portingdeadmods.moofluids.MFConfig;
import com.portingdeadmods.moofluids.Utils;
import com.portingdeadmods.moofluids.recipe.AlloyRecipe;
import com.portingdeadmods.moofluids.recipe.AlloyRecipeInput;
import com.portingdeadmods.moofluids.recipe.MFRecipes;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
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
import java.util.List;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(FLUID_NAME, BuiltInRegistries.FLUID.getKey(Fluids.EMPTY).toString());
        pBuilder.define(DELAY, MFConfig.defaultMilkingCooldown);
        pBuilder.define(CAN_BE_MILKED, true);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pSpawnType, @Nullable SpawnGroupData pSpawnGroupData) {
        if (!pLevel.isClientSide()) {
            Fluid randomFluid = getRandomFluid();
            if (randomFluid != null) {
                this.setFluid(Utils.idFromFluid(randomFluid));
                if (this.getDelay() < 0) {
                    this.entityData.set(CAN_BE_MILKED, true);
                }
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pSpawnType, pSpawnGroupData);
    }

    @Override
    public void finalizeSpawnChildFromBreeding(ServerLevel worldIn, Animal animal, @org.jetbrains.annotations.Nullable AgeableMob newMob) {
        if (!worldIn.isClientSide() && newMob instanceof FluidCow fluidCow && animal instanceof FluidCow fluidCowParent) {
            Fluid parentFluid1 = this.getFluid();
            Fluid parentFluid2 = fluidCowParent.getFluid();
            
            AlloyRecipeInput input = new AlloyRecipeInput(List.of(parentFluid1, parentFluid2));
            var recipeManager = worldIn.getRecipeManager();
            var alloyRecipe = recipeManager.getRecipeFor(MFRecipes.ALLOY_TYPE.get(), input, worldIn);
            
            if (alloyRecipe.isPresent()) {
                var rnd = RandomSource.create();
                float successChance = alloyRecipe.get().value().successChance();
                if (rnd.nextFloat() < successChance) {
                    fluidCow.setFluid(Utils.idFromFluid(alloyRecipe.get().value().output()));
                } else {
                    int rndVal = Mth.nextInt(rnd, 0, 1);
                    fluidCow.setFluid(Utils.idFromFluid(rndVal == 0 ? parentFluid1 : parentFluid2));
                }
            } else {
                var rnd = RandomSource.create();
                int rndVal = Mth.nextInt(rnd, 0, 1);
                fluidCow.setFluid(Utils.idFromFluid(rndVal == 0 ? parentFluid1 : parentFluid2));
            }
            
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
            this.setDelay(MFConfig.defaultMilkingCooldown);
        }
    }

    @Override
    @NotNull
    @ParametersAreNonnullByDefault
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
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
            } else if (player.getItemInHand(hand).getItem() == Items.WHEAT) {
                return super.mobInteract(player, hand);
            } else if (MFConfig.milkCow && hand == InteractionHand.MAIN_HAND 
                       && player.getItemInHand(hand).getItem() == Items.BUCKET) {
                return super.mobInteract(player, hand);
            } else {
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.FAIL;
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
        if (this.entityData.get(FLUID_NAME).equals(BuiltInRegistries.FLUID.getKey(Fluids.EMPTY).toString()))
            return Fluids.EMPTY;

        Fluid fluid = Utils.get(this.entityData.get(FLUID_NAME));
        return fluid != null ? fluid : Fluids.EMPTY;
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

    public @Nullable Fluid getRandomFluid() {
        ImmutableList<Fluid> fluids = Utils.getFluids();
        if (fluids.isEmpty()) return null;

        var rnd = RandomSource.create();
        int rndVal = Mth.nextInt(rnd, 0, fluids.size() - 1);
        return fluids.get(rndVal);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(TAG_DELAY, this.getDelay());
        if (this.getFluid() != Fluids.EMPTY && BuiltInRegistries.FLUID.containsValue(this.getFluid())) {
            compound.putString(TAG_FLUID, BuiltInRegistries.FLUID.getKey(this.getFluid()).toString());
        } else {
            compound.putString(TAG_FLUID, BuiltInRegistries.FLUID.getKey(Fluids.EMPTY).toString());
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
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