package com.portingdeadmods.moofluids.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.portingdeadmods.moofluids.MFConfig;
import com.portingdeadmods.moofluids.entity.FluidCow;
import com.portingdeadmods.moofluids.entity.MFEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidCowJarBEWLR extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation COW_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/cow/cow.png");
    private static final ResourceLocation JAR_TEXTURE = ResourceLocation.fromNamespaceAndPath("moofluids", "textures/block/jar.png");
    private CowModel<FluidCow> cowModel;
    private FluidCow dummyCow;
    private BakedModel itemModel;


    public FluidCowJarBEWLR() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        // Lazy-init models
        if (this.cowModel == null) {
            this.cowModel = new CowModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.COW));
            this.dummyCow = new FluidCow(MFEntities.FLUID_COW.get(), Minecraft.getInstance().level);
        }

        poseStack.pushPose();
        // Apply the correct perspective transform manually
        applyDisplayTransforms(poseStack, displayContext);

        // Render the jar manually
        renderJar(poseStack, buffer, packedLight, packedOverlay);

        // Render contents
        CompoundTag blockEntityData = stack.get(FluidCowJarBlockItem.COW_JAR_DATA.get());
        if (blockEntityData != null && !blockEntityData.isEmpty()) {
            if (blockEntityData.getBoolean("HasCow")) {
                String fluidName = blockEntityData.getString("FluidCowFluid");
                if (!fluidName.isEmpty()) {
                    ResourceLocation fluidRl = ResourceLocation.tryParse(fluidName);
                    if (fluidRl != null) {
                        Fluid fluid = BuiltInRegistries.FLUID.get(fluidRl);
                        if (fluid != Fluids.EMPTY) {
                            renderCow(new FluidStack(fluid, 1), poseStack, buffer, packedLight, packedOverlay);
                        }
                    }
                }
            }

            CompoundTag fluidTag = blockEntityData.getCompound("FluidTank");
            if (!fluidTag.isEmpty() && fluidTag.contains("fluid")) {
                FluidStack fluidStack = FluidStack.parse(Minecraft.getInstance().level.registryAccess(), fluidTag.getCompound("fluid")).orElse(FluidStack.EMPTY);
                if (!fluidStack.isEmpty()) {
                    float height = (0.5625F / MFConfig.COW_JAR_CAPACITY.getAsInt()) * fluidStack.getAmount();
                    renderFluid(fluidStack, height, poseStack, buffer, packedLight);
                }
            }
        }
        poseStack.popPose();
    }

    private void applyDisplayTransforms(PoseStack poseStack, ItemDisplayContext displayContext) {
        poseStack.translate(0.5, 0.5, 0.5);
        switch (displayContext) {
            case GUI:
                poseStack.mulPose(Axis.XP.rotationDegrees(30));
                poseStack.mulPose(Axis.YP.rotationDegrees(225));
                poseStack.scale(0.625f, 0.625f, 0.625f);
                break;
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
                poseStack.translate(0, 2.5 / 16.0, 0);
                poseStack.mulPose(Axis.XP.rotationDegrees(75));
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                poseStack.scale(0.375f, 0.375f, 0.375f);
                break;
            case FIRST_PERSON_RIGHT_HAND:
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                poseStack.scale(0.4f, 0.4f, 0.4f);
                break;
            case FIRST_PERSON_LEFT_HAND:
                poseStack.mulPose(Axis.YP.rotationDegrees(225));
                poseStack.scale(0.4f, 0.4f, 0.4f);
                break;
            case GROUND:
                poseStack.translate(0, 3.0 / 16.0, 0);
                poseStack.scale(0.25f, 0.25f, 0.25f);
                break;
            case FIXED:
                poseStack.scale(0.5f, 0.5f, 0.5f);
                break;
            default:
                break;
        }
        poseStack.translate(-0.5, -0.5, -0.5);
    }

    private void renderJar(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(JAR_TEXTURE));

        // Main Body
        float x1 = 4 / 16f, y1 = 0 / 16f, z1 = 4 / 16f;
        float x2 = 12 / 16f, y2 = 10 / 16f, z2 = 12 / 16f;
        // North
        addVertex(consumer, poseStack, x1, y1, z1, 0, 10/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x1, y2, z1, 0, 0, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x2, y2, z1, 8/16f, 0, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x2, y1, z1, 8/16f, 10/16f, packedLight, packedOverlay);
        // East
        addVertex(consumer, poseStack, x2, y1, z1, 0, 10/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x2, y2, z1, 0, 0, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x2, y2, z2, 8/16f, 0, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x2, y1, z2, 8/16f, 10/16f, packedLight, packedOverlay);
        // South
        addVertex(consumer, poseStack, x2, y1, z2, 0, 10/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x2, y2, z2, 0, 0, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x1, y2, z2, 8/16f, 0, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x1, y1, z2, 8/16f, 10/16f, packedLight, packedOverlay);
        // West
        addVertex(consumer, poseStack, x1, y1, z2, 0, 10/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x1, y2, z2, 0, 0, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x1, y2, z1, 8/16f, 0, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x1, y1, z1, 8/16f, 10/16f, packedLight, packedOverlay);
        // Up
        addVertex(consumer, poseStack, x1, y2, z1, 8/16f, 0, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x1, y2, z2, 8/16f, 8/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x2, y2, z2, 16/16f, 8/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x2, y2, z1, 16/16f, 0, packedLight, packedOverlay);
        // Down
        addVertex(consumer, poseStack, x1, y1, z2, 8/16f, 0, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x1, y1, z1, 8/16f, 8/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x2, y1, z1, 16/16f, 8/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x2, y1, z2, 16/16f, 0, packedLight, packedOverlay);

        // Lid
        x1 = 5 / 16f; y1 = 10 / 16f; z1 = 5 / 16f;
        x2 = 11 / 16f; y2 = 12 / 16f; z2 = 11 / 16f;
        // North
        addVertex(consumer, poseStack, x1, y1, z1, 9/16f, 10/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x1, y2, z1, 9/16f, 8/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x2, y2, z1, 15/16f, 8/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x2, y1, z1, 15/16f, 10/16f, packedLight, packedOverlay);
        // East
        addVertex(consumer, poseStack, x2, y1, z1, 15/16f, 10/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x2, y2, z1, 15/16f, 8/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x2, y2, z2, 9/16f, 8/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x2, y1, z2, 9/16f, 10/16f, packedLight, packedOverlay);
        // South
        addVertex(consumer, poseStack, x2, y1, z2, 9/16f, 10/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x2, y2, z2, 9/16f, 8/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x1, y2, z2, 15/16f, 8/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x1, y1, z2, 15/16f, 10/16f, packedLight, packedOverlay);
        // West
        addVertex(consumer, poseStack, x1, y1, z2, 15/16f, 10/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x1, y2, z2, 15/16f, 8/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x1, y2, z1, 9/16f, 8/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x1, y1, z1, 9/16f, 10/16f, packedLight, packedOverlay);
        // Up
        addVertex(consumer, poseStack, x1, y2, z1, 9/16f, 10/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x1, y2, z2, 9/16f, 16/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x2, y2, z2, 15/16f, 16/16f, packedLight, packedOverlay);
        addVertex(consumer, poseStack, x2, y2, z1, 15/16f, 10/16f, packedLight, packedOverlay);
    }

    private void addVertex(VertexConsumer consumer, PoseStack pose, float x, float y, float z, float u, float v, int packedLight, int packedOverlay) {
        int lightU = packedLight & '\uffff';
        int lightV = packedLight >> 16 & '\uffff';
        int overlayU = packedOverlay & '\uffff';
        int overlayV = packedOverlay >> 16 & '\uffff';
        consumer.addVertex(pose.last().pose(), x, y, z).setColor(1f, 1f, 1f, 1f).setUv(u, v).setUv1(overlayU, overlayV).setUv2(lightU, lightV).setNormal(0, 1, 0);
    }

    private void renderCow(FluidStack fluid, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.1625, 0.5);
        float scale = 0.25f;
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(Axis.YP.rotationDegrees((Minecraft.getInstance().level.getGameTime()) * 0.5f));
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0f));
        poseStack.translate(0, -1.25, 0);

        int color = IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor(fluid);
        cowModel.young = false;
        cowModel.prepareMobModel(dummyCow, 0, 0, 0);
        cowModel.setupAnim(dummyCow, 0, 0, Minecraft.getInstance().level.getGameTime(), 0, 0);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entitySolid(COW_TEXTURE));
        cowModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, color);
        poseStack.popPose();
    }

    private void renderFluid(FluidStack fluidStack, float height, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLight) {
        var fluidExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        TextureAtlasSprite fluidStillSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidExtensions.getStillTexture());
        VertexConsumer buffer = bufferIn.getBuffer(RenderType.translucent());
        int fluidColor = fluidExtensions.getTintColor();

        matrixStack.pushPose();
        float xMax = 0.75F, zMax = 0.75F, xMin = 0.25F, zMin = 0.25F, yMin = 0.0625F;
        float alpha = 1F;
        float red = (fluidColor >> 16 & 0xFF) / 255.0F;
        float green = (fluidColor >> 8 & 0xFF) / 255.0F;
        float blue = (fluidColor & 0xFF) / 255.0F;
        renderCuboid(buffer, matrixStack, xMax, xMin, yMin, height, zMin, zMax, fluidStillSprite, red, green, blue, alpha, combinedLight);
        matrixStack.popPose();
    }

    private void renderCuboid(VertexConsumer buffer, PoseStack matrixStack, float xMax, float xMin, float yMin, float height, float zMin, float zMax, TextureAtlasSprite textureAtlasSprite, float red, float green, float blue, float alpha, int combinedLight) {
        if (height <= 0) return;
        float yMax = yMin + height;
        float uMin = textureAtlasSprite.getU0();
        float uMax = textureAtlasSprite.getU1();
        float vMin = textureAtlasSprite.getV0();
        float vMax = textureAtlasSprite.getV1();
        float vHeight = vMax - vMin;

        // top
        addVertexWithUV(buffer, matrixStack, xMax, yMax, zMax, uMax, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMax, zMin, uMin, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMax, zMin, uMin, vMax, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMax, zMax, uMax, vMax, red, green, blue, alpha, combinedLight);

        // north
        addVertexWithUV(buffer, matrixStack, xMax, yMin, zMin, uMax, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMin, zMin, uMin, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMax, zMin, uMin, vMin + (vHeight * height), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMax, zMin, uMax, vMin + (vHeight * height), red, green, blue, alpha, combinedLight);

        // south
        addVertexWithUV(buffer, matrixStack, xMax, yMin, zMax, uMin, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMax, zMax, uMin, vMin + (vHeight * height), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMax, zMax, uMax, vMin + (vHeight * height), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMin, zMax, uMax, vMin, red, green, blue, alpha, combinedLight);

        // east
        addVertexWithUV(buffer, matrixStack, xMax, yMin, zMin, uMin, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMax, zMin, uMin, vMin + (vHeight * height), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMax, zMax, uMax, vMin + (vHeight * height), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMin, zMax, uMax, vMin, red, green, blue, alpha, combinedLight);

        // west
        addVertexWithUV(buffer, matrixStack, xMin, yMin, zMax, uMin, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMax, zMax, uMin, vMin + (vHeight * height), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMax, zMin, uMax, vMin + (vHeight * height), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMin, zMin, uMax, vMin, red, green, blue, alpha, combinedLight);

        // down
        addVertexWithUV(buffer, matrixStack, xMax, yMin, zMin, uMax, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMin, zMax, uMin, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMin, zMax, uMin, vMax, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMin, zMin, uMax, vMax, red, green, blue, alpha, combinedLight);
    }

    private void addVertexWithUV(VertexConsumer buffer, PoseStack matrixStack, float x, float y, float z, float u, float v, float red, float green, float blue, float alpha, int combinedLight) {
        int lightU = combinedLight & '\uffff';
        int lightV = combinedLight >> 16 & '\uffff';
        buffer.addVertex(matrixStack.last().pose(), x, y, z).setColor(red, green, blue, alpha).setUv(u, v).setUv2(lightU, lightV).setNormal(1, 0, 0);
    }
}