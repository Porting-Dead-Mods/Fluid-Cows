package com.portingdeadmods.moofluids.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.portingdeadmods.moofluids.FluidUtils;
import com.portingdeadmods.moofluids.entity.FluidCow;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;

public class RenderFluidCow extends CowRenderer {

    public RenderFluidCow(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public void render(Cow cow, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        FluidCow fCow = (FluidCow) cow;
        int color = FluidUtils.getFluidColor(fCow.getFluid());
        super.render(cow, yaw ,partialTicks, poseStack, buffer, packedLight);
        renderWithColor(cow, yaw, partialTicks, poseStack, buffer, packedLight, color);
    }

    public void renderWithColor(Cow cow, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int color) {
        poseStack.pushPose();

        this.model.attackTime = this.getAttackAnim(cow, partialTicks);
        this.model.young = cow.isBaby();

        float bodyRotation = Mth.rotLerp(partialTicks, cow.yBodyRotO, cow.yBodyRot);
        float headRotation = Mth.rotLerp(partialTicks, cow.yHeadRotO, cow.yHeadRot);
        float headBodyRotationDifference = headRotation - bodyRotation;
        float pitch = Mth.lerp(partialTicks, cow.xRotO, cow.getXRot());

        if (isEntityUpsideDown(cow)) {
            pitch *= -1.0F;
            headBodyRotationDifference *= -1.0F;
        }

        this.setupRotations(cow, poseStack, this.getBob(cow, partialTicks), bodyRotation, partialTicks, 1);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(cow, poseStack, partialTicks);
        poseStack.translate(0.0F, -1.501F, 0.0F);

        float walkSpeed = cow.walkAnimation.speed(partialTicks);
        float walkPosition = cow.walkAnimation.position(partialTicks);

        if (cow.isBaby()) {
            walkPosition *= 3.0F;
        }

        if (walkSpeed > 1.0F) {
            walkSpeed = 1.0F;
        }

        this.model.prepareMobModel(cow, walkPosition, walkSpeed, partialTicks);
        this.model.setupAnim(cow, walkPosition, walkSpeed, this.getBob(cow, partialTicks), headBodyRotationDifference, pitch);

        Minecraft minecraft = Minecraft.getInstance();
        boolean isVisible = this.isBodyVisible(cow);
        boolean isGlowing = minecraft.shouldEntityAppearGlowing(cow);

        RenderType renderType = this.getRenderType(cow, isVisible, !isVisible && !cow.isInvisibleTo(minecraft.player), isGlowing);
        if (renderType != null) {
            VertexConsumer vertexConsumer = buffer.getBuffer(renderType);
            int overlayCoords = getOverlayCoords(cow, this.getWhiteOverlayProgress(cow, partialTicks));
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, overlayCoords, color);
        }

        poseStack.popPose();
    }
}
