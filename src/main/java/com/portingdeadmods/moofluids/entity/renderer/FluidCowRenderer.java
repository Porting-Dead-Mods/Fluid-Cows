package com.portingdeadmods.moofluids.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.moofluids.entity.FluidCow;
import net.minecraft.client.model.ColorableHierarchicalModel;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class FluidCowRenderer extends MobRenderer<FluidCow, CowModel<FluidCow>> {
    private static final ResourceLocation COW_LOCATION = new ResourceLocation("textures/entity/cow/cow.png");

    public FluidCowRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, new CowModel<>(p_173956_.bakeLayer(ModelLayers.COW)), 0.7F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FluidCow fluidCow) {
        return COW_LOCATION;
    }

    @Override
    public void render(FluidCow p_115455_, float p_115456_, float p_115457_, PoseStack p_115458_, MultiBufferSource p_115459_, int p_115460_) {

        super.render(p_115455_, p_115456_, p_115457_, p_115458_, p_115459_, p_115460_);
    }
}
