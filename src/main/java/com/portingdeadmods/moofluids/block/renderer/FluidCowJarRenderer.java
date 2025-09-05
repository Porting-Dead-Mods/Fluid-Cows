package com.portingdeadmods.moofluids.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.portingdeadmods.moofluids.FluidUtils;
import com.portingdeadmods.moofluids.block.entity.FluidCowJarBlockEntity;
import com.portingdeadmods.moofluids.entity.FluidCow;
import com.portingdeadmods.moofluids.entity.MFEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidCowJarRenderer implements BlockEntityRenderer<FluidCowJarBlockEntity> {

    private static final ResourceLocation COW_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/cow/cow.png");
    private static FluidCow dummyCow;
    private final CowModel<FluidCow> cowModel;

    public FluidCowJarRenderer(BlockEntityRendererProvider.Context context) {
        this.cowModel = new CowModel<>(context.bakeLayer(ModelLayers.COW));
    }

    @Override
    public void render(FluidCowJarBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return;
        }

        if (blockEntity.hasCow()) {
            renderCowInJar(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay, level);
        }
    }

    private void renderFluidInJar(FluidCowJarBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!blockEntity.hasCow() || blockEntity.getCowFluid() == Fluids.EMPTY) {
            return;
        }

        int fluidAmount = blockEntity.getFluidTank().getFluidAmount();
        int capacity = blockEntity.getFluidTank().getCapacity();
        
        if (fluidAmount <= 0) {
            return;
        }

        float fillPercentage = (float) fluidAmount / capacity;
        
        poseStack.pushPose();

        poseStack.translate(0.5, 0.0625, 0.5);

        float maxHeight = 0.375f;
        float currentHeight = maxHeight * fillPercentage;
        
        poseStack.scale(0.375f, currentHeight, 0.375f);
        poseStack.translate(-0.5, 0, -0.5);

        FluidStack fluidStack = new FluidStack(blockEntity.getCowFluid(), 1000);
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(blockEntity.getCowFluid());
        int color = fluidTypeExtensions.getTintColor(fluidStack);
        ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture(fluidStack);
        
        if (stillTexture != null) {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.translucent());
            
            float red = ((color >> 16) & 0xFF) / 255.0F;
            float green = ((color >> 8) & 0xFF) / 255.0F;
            float blue = (color & 0xFF) / 255.0F;
            float alpha = ((color >> 24) & 0xFF) / 255.0F;
            
            if (alpha == 0) {
                alpha = 1.0F;
            }

            renderFluidCube(poseStack, vertexConsumer, packedLight, red, green, blue, alpha);
        }
        
        poseStack.popPose();
    }

    private void renderCowInJar(FluidCowJarBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Level level) {
        if (dummyCow == null) {
            dummyCow = new FluidCow(MFEntities.FLUID_COW.get(), level);
        }

        if (blockEntity.getCowFluid() != Fluids.EMPTY) {
            dummyCow.setFluid(blockEntity.getCowFluid().toString());
        }

        poseStack.pushPose();

        poseStack.translate(0.5, 0.0625, 0.5);

        float scale = 0.25f;
        poseStack.scale(scale, scale, scale);

        poseStack.mulPose(Axis.YP.rotationDegrees((level.getGameTime() + partialTick) * 0.5f));

        poseStack.mulPose(Axis.XP.rotationDegrees(180.0f));

        poseStack.translate(0, -1.25,0);

        int color = FluidUtils.getFluidColor(blockEntity.getCowFluid());

        cowModel.young = false;
        cowModel.prepareMobModel(dummyCow, 0, 0, partialTick);
        cowModel.setupAnim(dummyCow, 0, 0, level.getGameTime() + partialTick, 0, 0);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entitySolid(COW_TEXTURE));
        cowModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);
        
        poseStack.popPose();
    }

    private void renderFluidCube(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, float red, float green, float blue, float alpha) {
        PoseStack.Pose pose = poseStack.last();

        addVertex(vertexConsumer, pose, 0, 0, 0, 0, 0, red, green, blue, alpha, packedLight);
        addVertex(vertexConsumer, pose, 1, 0, 0, 1, 0, red, green, blue, alpha, packedLight);
        addVertex(vertexConsumer, pose, 1, 0, 1, 1, 1, red, green, blue, alpha, packedLight);
        addVertex(vertexConsumer, pose, 0, 0, 1, 0, 1, red, green, blue, alpha, packedLight);

        addVertex(vertexConsumer, pose, 0, 1, 1, 0, 1, red, green, blue, alpha, packedLight);
        addVertex(vertexConsumer, pose, 1, 1, 1, 1, 1, red, green, blue, alpha, packedLight);
        addVertex(vertexConsumer, pose, 1, 1, 0, 1, 0, red, green, blue, alpha, packedLight);
        addVertex(vertexConsumer, pose, 0, 1, 0, 0, 0, red, green, blue, alpha, packedLight);

    }

    private void addVertex(VertexConsumer vertexConsumer, PoseStack.Pose pose, float x, float y, float z, float u, float v, float red, float green, float blue, float alpha, int packedLight) {
        vertexConsumer.addVertex(pose, x, y, z)
                     .setColor(red, green, blue, alpha)
                     .setUv(u, v)
                     .setOverlay(OverlayTexture.NO_OVERLAY)
                     .setLight(packedLight)
                     .setNormal(pose, 0, 1, 0);
    }
}