package com.reclipse.moofluids.renderer;

import com.reclipse.moofluids.FluidCow;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class FluidCowRenderer extends MobRenderer<FluidCow, CowModel<FluidCow>>
{
    private static final ResourceLocation COW_LOCATION = new ResourceLocation("textures/entity/cow/cow.png");

    public FluidCowRenderer(EntityRendererProvider.Context p_173956_) {
        super(p_173956_, new CowModel(p_173956_.bakeLayer(ModelLayers.COW)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(FluidCow fluidCow) {
        return COW_LOCATION;
    }


}
