package com.portingdeadmods.moofluids.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.portingdeadmods.moofluids.FluidUtils;
import com.portingdeadmods.moofluids.VertexUtil;
import com.portingdeadmods.moofluids.block.entity.FluidCowJarBlockEntity;
import com.portingdeadmods.moofluids.entity.FluidCow;
import com.portingdeadmods.moofluids.entity.MFEntities;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class FluidCowJarRenderer implements BlockEntityRenderer<FluidCowJarBlockEntity> {
    public static Map<ResourceLocation, Material> FLUID_TEXTURE_CACHE;
    private static final ResourceLocation COW_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/cow/cow.png");
    private static FluidCow dummyCow;
    public static CowModel<FluidCow> COW_MODEL;

    public FluidCowJarRenderer(BlockEntityRendererProvider.Context context) {
        COW_MODEL = new CowModel<>(context.bakeLayer(ModelLayers.COW));
        FLUID_TEXTURE_CACHE = new HashMap<>();
    }

    @Override
    public void render(FluidCowJarBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return;
        }

        if (blockEntity.hasCow()) {
            renderCowInJar(blockEntity.getFluidTank().getFluid(), blockEntity.getFluidTank().getCapacity(), blockEntity.getCowFluid(), partialTick, poseStack, bufferSource, packedLight, packedOverlay, level);
        }
    }

    public static void renderCowInJar(FluidStack fluidStack, int capacity, Fluid cowFluid, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Level level) {
        if (dummyCow == null) {
            dummyCow = new FluidCow(MFEntities.FLUID_COW.get(), level);
        }

        if (cowFluid != Fluids.EMPTY) {
            dummyCow.setFluid(cowFluid.toString());
        }

        poseStack.pushPose();
        {
            poseStack.translate(0.5, 0.1625, 0.5);

            float scale = 0.25f;
            poseStack.scale(scale, scale, scale);

            poseStack.mulPose(Axis.YP.rotationDegrees((level.getGameTime() + partialTick) * 0.5f));

            poseStack.mulPose(Axis.XP.rotationDegrees(180.0f));

            poseStack.translate(0, -1.25, 0);

            int color = FluidUtils.getFluidColor(cowFluid);

            COW_MODEL.young = false;
            COW_MODEL.prepareMobModel(dummyCow, 0, 0, partialTick);
            COW_MODEL.setupAnim(dummyCow, 0, 0, level.getGameTime() + partialTick, 0, 0);

            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entitySolid(COW_TEXTURE));
            COW_MODEL.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);
        }
        poseStack.popPose();

        if (!fluidStack.isEmpty()) {
            IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            int tintColor = fluidTypeExtensions.getTintColor();
            renderFluid((float) fluidStack.getAmount() / capacity, tintColor,
                    0, fluidTypeExtensions.getStillTexture(),
                    poseStack, bufferSource, packedLight);
        }
    }

    public static void renderFluid(float percentageFill, int color, int luminosity, ResourceLocation texture, PoseStack poseStack, MultiBufferSource bufferIn, int light) {
        poseStack.pushPose();
        if (luminosity != 0) light = light & 15728640 | luminosity << 4;
        VertexConsumer builder = FLUID_TEXTURE_CACHE.computeIfAbsent(texture, k -> new Material(TextureAtlas.LOCATION_BLOCKS, k)).buffer(bufferIn, RenderType::entityTranslucentCull);
        // z is actually the y value at the bottom lol
        Vector3f dimensions = new Vector3f(6.5f / 16f, 9f / 16f, 0.1f / 16f);
        poseStack.translate(0.5, dimensions.z(), 0.5);
        VertexUtil.addCube(builder, poseStack,
                dimensions.x(),
                percentageFill * dimensions.y(),
                light, color);
        poseStack.popPose();
    }
}