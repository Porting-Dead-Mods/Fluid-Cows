package com.reclipse.moofluids;

import com.reclipse.moofluids.utils.FluidCapacity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import net.minecraftforge.fluids.FluidStack;
import com.reclipse.moofluids.utils.FluidCapacity.*;

public class FluidCow extends Cow {
    public FluidCow(EntityType<? extends Cow> p_28285_, Level p_28286_) {super(p_28285_, p_28286_);}
    int milkingCooldown = Config.defaultMilkingCooldown;



    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (0 <= FluidCapacity.getFluidCapacity(itemstack) && FluidCapacity.getFluidCapacity(itemstack) <= 1000) {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);

        }
        if (itemstack.is(Items.BUCKET) && !this.isBaby()) {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            ItemStack fluidBucket = ItemUtils.createFilledResult(itemstack, player, Items.MILK_BUCKET.getDefaultInstance());
            player.setItemInHand(hand, fluidBucket);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }
}