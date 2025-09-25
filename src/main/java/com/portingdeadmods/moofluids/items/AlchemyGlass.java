package com.portingdeadmods.moofluids.items;

import com.portingdeadmods.moofluids.compat.jei.AlchemyGlassJeiHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

import java.util.List;

public class AlchemyGlass extends Item {
    public AlchemyGlass(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide && ModList.get().isLoaded("jei")) {
            AlchemyGlassJeiHandler.showBreedingRecipes();
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.moofluids.alchemy_glass.tooltip"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}