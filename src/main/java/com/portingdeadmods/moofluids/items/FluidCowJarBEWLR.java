package com.portingdeadmods.moofluids.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.portingdeadmods.moofluids.block.MFBlocks;
import com.portingdeadmods.moofluids.block.renderer.FluidCowJarRenderer;
import com.portingdeadmods.moofluids.data.CowJarDataComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidCowJarBEWLR extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation JAR_TEXTURE = ResourceLocation.fromNamespaceAndPath("moofluids", "textures/block/jar.png");
    private BakedModel itemModel;

    public FluidCowJarBEWLR() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (this.itemModel == null) {
            this.itemModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(MFBlocks.FLUID_COW_JAR.get().defaultBlockState());
        }

        poseStack.pushPose();
        {
            poseStack.pushPose();
            {
                poseStack.translate(0.5, 0.5, 0.5);
                Minecraft.getInstance().getItemRenderer().render(stack, displayContext, false, poseStack, buffer, packedLight, packedOverlay, this.itemModel);
            }
            poseStack.popPose();

            CowJarDataComponent data = stack.get(FluidCowJarBlockItem.COW_JAR_DATA);
            FluidStack fluidStack = stack.get(FluidCowJarBlockItem.FLUID_TANK).copy();
            if (data.hasCow()) {
                applyDisplayTransforms(poseStack, displayContext);
                FluidCowJarRenderer.renderCowInJar(fluidStack, data.capacity(), data.fluid(), 0, poseStack, buffer, packedLight, packedOverlay, Minecraft.getInstance().level);
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

}