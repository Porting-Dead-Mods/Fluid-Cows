package com.portingdeadmods.moofluids;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ColorUtils {
    @SuppressWarnings("deprecation")
    public static int getColorFrom(ResourceLocation location) {
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
        if (texture instanceof TextureAtlas) {
            return getColorFrom(((TextureAtlas) texture).getSprite(location));
        }
        return 0;
    }

    public static int getColorFrom(TextureAtlasSprite sprite) {
        if (sprite == null) return -1;
        if (sprite.contents().getUniqueFrames().count() == 0) return -1;
        float total = 0, red = 0, blue = 0, green = 0;
        for (int x = 0; x < sprite.contents().width(); x++) {
            for (int y = 0; y < sprite.contents().height(); y++) {
                int color = sprite.getPixelRGBA(0, x, y);
                int alpha = color >> 24 & 0xFF;
                // if (alpha != 255) continue; // this creates problems for translucent textures
                total += alpha;
                red += (color & 0xFF) * alpha;
                green += (color >> 8 & 0xFF) * alpha;
                blue += (color >> 16 & 0xFF) * alpha;
            }
        }

        if (total > 0)
            return FastColor.ARGB32.color(255, (int) (red / total), (int) (green / total), (int) (blue / total));
        return -1;
    }
}
