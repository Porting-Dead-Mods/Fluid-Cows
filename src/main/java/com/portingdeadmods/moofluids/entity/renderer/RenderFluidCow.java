package com.portingdeadmods.moofluids.entity.renderer;

import com.portingdeadmods.moofluids.entity.FluidCow;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import static com.portingdeadmods.moofluids.MooFluids.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RenderFluidCow extends CowRenderer {

    public RenderFluidCow(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    protected int getColorMultiplier(FluidCow entityLivingBase, float par2, float par3) {
        return ((FluidCow) entityLivingBase).getOverlay();
    }

    public static class Factory implements IRenderFactory<FluidCow> {
        public RenderFluidCow createRenderFor(EntityRendererProvider.Context renderManager) {
            return new RenderFluidCow(renderManager);
        }
    }
}
