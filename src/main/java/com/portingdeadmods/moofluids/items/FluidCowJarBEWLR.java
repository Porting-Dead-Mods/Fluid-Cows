package com.portingdeadmods.moofluids.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.moofluids.block.entity.FluidCowJarBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class FluidCowJarBEWLR extends BlockEntityWithoutLevelRenderer {
    private FluidCowJarBlockEntity dummyBlockEntity;
    private BlockEntityRenderer<FluidCowJarBlockEntity> renderer;

    public FluidCowJarBEWLR() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        CompoundTag blockEntityData = stack.get(FluidCowJarBlockItem.COW_JAR_DATA.get());
        if (blockEntityData == null || blockEntityData.isEmpty()) {
            return;
        }

        if (dummyBlockEntity == null) {
            if (stack.getItem() instanceof BlockItem blockItem) {
                BlockState defaultState = blockItem.getBlock().defaultBlockState();
                dummyBlockEntity = new FluidCowJarBlockEntity(BlockPos.ZERO, defaultState);
            } else {
                return;
            }
        }

        dummyBlockEntity.setLevel(Minecraft.getInstance().level);

        if (renderer == null) {
            renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(dummyBlockEntity);
        }

        if (renderer != null) {
            dummyBlockEntity.loadAdditional(blockEntityData, Minecraft.getInstance().level.registryAccess());
            renderer.render(dummyBlockEntity, 0, poseStack, buffer, packedLight, packedOverlay);
        }
    }
}
