package com.portingdeadmods.moofluids.compat.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.portingdeadmods.moofluids.ColorUtils;
import com.portingdeadmods.moofluids.FluidUtils;
import com.portingdeadmods.moofluids.MooFluids;
import com.portingdeadmods.moofluids.entity.FluidCow;
import com.portingdeadmods.moofluids.entity.MFEntities;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class FluidCowBreedingCategory extends AbstractRecipeCategory<FluidCowBreedingRecipe> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MooFluids.MODID, "textures/gui/breeding.png");
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable heartIcon;

    public FluidCowBreedingCategory(IGuiHelper guiHelper) {
        super(MFJeiPlugin.BREEDING_TYPE, 
              Component.translatable("jei.moofluids.category.breeding"),
              guiHelper.createDrawableItemStack(Items.WHEAT.getDefaultInstance()),
              150, 70);
        
        this.background = guiHelper.createBlankDrawable(150, 70);
        this.icon = guiHelper.createDrawableItemStack(Items.WHEAT.getDefaultInstance());
        this.heartIcon = guiHelper.drawableBuilder(ResourceLocation.withDefaultNamespace("textures/particle/heart.png"), 0, 0, 8, 8)
            .setTextureSize(8, 8)
            .build();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidCowBreedingRecipe recipe, IFocusGroup focuses) {
    }

    @Override
    public void draw(FluidCowBreedingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        
        if (level != null) {
            FluidCow parent1 = createCowWithFluid(level, recipe.getParent1());
            FluidCow parent2 = createCowWithFluid(level, recipe.getParent2());
            FluidCow result = createCowWithFluid(level, recipe.getResult());
            
            renderCow(guiGraphics, parent1, 25, 35, 15);
            renderCow(guiGraphics, parent2, 75, 35, 15);
            
            heartIcon.draw(guiGraphics, 50 - 4, 20);
            
            renderCow(guiGraphics, result, 50, 55, 15);
            
            if (recipe.getSuccessChance() < 1.0f) {
                String chance = String.format("%.0f%%", recipe.getSuccessChance() * 100);
                Component chanceText = Component.literal(chance + " chance");
                int textWidth = mc.font.width(chanceText);
                guiGraphics.drawString(mc.font, chanceText, 115 - textWidth/2, 45, 0xFFAA00, true);
            }
        }
    }
    
    @Override
    public void getTooltip(ITooltipBuilder tooltip, FluidCowBreedingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (isMouseOverCow(mouseX, mouseY, 25, 35, 15)) {
            addFluidTooltip(tooltip, recipe.getParent1());
        } else if (isMouseOverCow(mouseX, mouseY, 75, 35, 15)) {
            addFluidTooltip(tooltip, recipe.getParent2());
        } else if (isMouseOverCow(mouseX, mouseY, 50, 55, 15)) {
            addFluidTooltip(tooltip, recipe.getResult());
        }
    }
    
    private boolean isMouseOverCow(double mouseX, double mouseY, int cowX, int cowY, float scale) {
        double halfWidth = scale * 0.8;
        double height = scale * 1.6;
        return mouseX >= cowX - halfWidth && mouseX <= cowX + halfWidth &&
               mouseY >= cowY - height && mouseY <= cowY;
    }

    private void addFluidTooltip(ITooltipBuilder tooltip, Fluid fluid) {
        FluidStack fluidStack = new FluidStack(fluid, FluidType.BUCKET_VOLUME);
        int color = FluidUtils.getFluidColor(fluid);
        Component fluidName = fluidStack.getHoverName().copy().setStyle(Style.EMPTY.withColor(color));
        tooltip.add(fluidName);
    }

    private FluidCow createCowWithFluid(Level level, Fluid fluid) {
        FluidCow cow = MFEntities.FLUID_COW.get().create(level);
        if (cow != null) {
            cow.setFluid(BuiltInRegistries.FLUID.getKey(fluid).toString());
            cow.setYRot(0);
            cow.yRotO = 0;
            cow.setXRot(0);
            cow.xRotO = 0;
            cow.yHeadRot = 0;
            cow.yHeadRotO = 0;
            cow.yBodyRot = 0;
            cow.yBodyRotO = 0;
        }
        return cow;
    }

    private void renderCow(GuiGraphics guiGraphics, Entity entity, int x, int y, float scale) {
        if (entity == null) return;
        
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(x, y, 50);
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        
        Minecraft mc = Minecraft.getInstance();
        EntityRenderDispatcher entityRenderDispatcher = mc.getEntityRenderDispatcher();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        
        entityRenderDispatcher.setRenderShadow(false);

        Vector3f light0 = new Vector3f(0.2f, 1.0f, -0.7f);
        Vector3f light1 = new Vector3f(-0.2f, 1.0f, 0.7f);
        light0.normalize();
        light1.normalize();
        
        RenderSystem.setShaderLights(light0, light1);
        
        RenderSystem.runAsFancy(() -> {
            entityRenderDispatcher.render(entity, 0, 0, 0, 0, 0, poseStack, bufferSource, 0xF000F0);
        });
        bufferSource.endBatch();
        entityRenderDispatcher.setRenderShadow(true);
        
        poseStack.popPose();
    }
}