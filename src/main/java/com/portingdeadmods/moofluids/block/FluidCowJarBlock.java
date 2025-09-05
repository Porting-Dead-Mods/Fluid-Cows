package com.portingdeadmods.moofluids.block;

import com.mojang.serialization.MapCodec;
import com.portingdeadmods.moofluids.block.entity.FluidCowJarBlockEntity;
import com.portingdeadmods.moofluids.block.entity.MFBlockEntities;
import com.portingdeadmods.moofluids.entity.FluidCow;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluidCowJarBlock extends BaseEntityBlock {

    public static final MapCodec<FluidCowJarBlock> CODEC = simpleCodec(FluidCowJarBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    
    private static final VoxelShape SHAPE = Block.box(4.8, 0, 4.8, 11.2, 8.0, 11.2);

    public FluidCowJarBlock(Properties properties) {
        super(properties.sound(SoundType.GLASS).strength(0.6f));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof FluidCowJarBlockEntity fluidCowJar)) {
            return ItemInteractionResult.FAIL;
        }

        if (stack.getItem() == Items.BUCKET) {
            FluidStack drained = fluidCowJar.getFluidTank().drain(1000, IFluidHandler.FluidAction.SIMULATE);
            if (!drained.isEmpty() && drained.getAmount() >= 1000) {
                if (stack.getCount() == 1) {
                    fluidCowJar.getFluidTank().drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    ItemStack filledBucket = FluidUtil.getFilledBucket(drained);
                    if (!player.getAbilities().instabuild) {
                        player.setItemInHand(hand, filledBucket);
                    }
                } else {
                    ItemStack filledBucket = FluidUtil.getFilledBucket(drained);
                    if (player.getInventory().add(filledBucket)) {
                        fluidCowJar.getFluidTank().drain(1000, IFluidHandler.FluidAction.EXECUTE);
                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }
                    }
                }
                return ItemInteractionResult.SUCCESS;
            }
        } else {
            FluidStack fluidStack = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);
            if (!fluidStack.isEmpty()) {
                int filled = fluidCowJar.getFluidTank().fill(fluidStack, IFluidHandler.FluidAction.SIMULATE);
                if (filled >= 1000) {
                    fluidCowJar.getFluidTank().fill(new FluidStack(fluidStack.getFluid(), 1000), IFluidHandler.FluidAction.EXECUTE);
                    if (!player.getAbilities().instabuild) {
                        player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                    }
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        
        if (!level.isClientSide) {
            checkForAnvilCrushing(level, pos, state, entity);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        
        if (!level.isClientSide) {
            checkForAnvilAbove(level, pos, state);
        }
    }

    private void checkForAnvilCrushing(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof FallingBlockEntity fallingBlock) {
            if (fallingBlock.getBlockState().is(Blocks.ANVIL) || 
                fallingBlock.getBlockState().is(Blocks.CHIPPED_ANVIL) ||
                fallingBlock.getBlockState().is(Blocks.DAMAGED_ANVIL)) {

                AABB searchArea = new AABB(pos).expandTowards(0, 2, 0);
                List<FluidCow> nearbyCows = level.getEntitiesOfClass(FluidCow.class, searchArea);
                
                if (!nearbyCows.isEmpty()) {
                    FluidCow cow = nearbyCows.get(0);
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity instanceof FluidCowJarBlockEntity fluidCowJar) {
                        if (fluidCowJar.addFluidCow(cow)) {
                            cow.discard();
                        }
                    }
                }
            }
        }
    }

    private void checkForAnvilAbove(Level level, BlockPos pos, BlockState state) {
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        
        if (aboveState.is(Blocks.ANVIL) || aboveState.is(Blocks.CHIPPED_ANVIL) || aboveState.is(Blocks.DAMAGED_ANVIL)) {
            AABB searchArea = new AABB(pos).expandTowards(0, 2, 0);
            List<FluidCow> nearbyCows = level.getEntitiesOfClass(FluidCow.class, searchArea);
            
            if (!nearbyCows.isEmpty()) {
                FluidCow cow = nearbyCows.get(0);
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof FluidCowJarBlockEntity fluidCowJar) {
                    if (fluidCowJar.addFluidCow(cow)) {
                        cow.discard();
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidCowJarBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, MFBlockEntities.FLUID_COW_JAR.get(), FluidCowJarBlockEntity::serverTick);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}