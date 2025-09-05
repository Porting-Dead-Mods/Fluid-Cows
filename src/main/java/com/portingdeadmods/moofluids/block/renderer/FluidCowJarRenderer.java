package com.portingdeadmods.moofluids.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.portingdeadmods.moofluids.FluidUtils;
import com.portingdeadmods.moofluids.VertexUtil;
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
import org.joml.Vector3f;

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

    public static void renderFluid(float percentageFill, int color, int luminosity, ResourceLocation texture, PoseStack poseStack, MultiBufferSource bufferIn, int light, int combinedOverlayIn) {
        poseStack.pushPose();
        if (luminosity != 0) light = light & 15728640 | luminosity << 4;
        VertexConsumer builder = ModMaterials.get(texture).buffer(bufferIn, RenderType::entityTranslucentCull);
        Vector3f dimensions = JarBakedModel.getJarLiquidDimensions();
        poseStack.translate(0.5, dimensions.z(), 0.5);
        VertexUtil.addCube(builder, poseStack,
                dimensions.x(),
                percentageFill * dimensions.y(),
                light, color);
        poseStack.popPose();
    }
}