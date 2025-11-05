package com.portingdeadmods.moofluids.mixins;

import com.portingdeadmods.moofluids.MFConfig;
import com.portingdeadmods.moofluids.MooFluids;
import com.portingdeadmods.moofluids.Utils;
import com.portingdeadmods.moofluids.entity.MFEntities;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Mixin(ServerLifecycleHooks.class)
public class ServerLifecycleHooksMixin {
    @ModifyVariable(method = "runModifiers", at = @At(value = "STORE"), ordinal = 0)
    private static List<BiomeModifier> mooFluids$runModifiers(final List<BiomeModifier> biomeModifiers, MinecraftServer server) {
        MooFluids.LOGGER.debug("creating spawn modifiers");

        List<BiomeModifier> biomeModifiers1 = new ArrayList<>(biomeModifiers);

        BiomeModifier modifier = null;
        for (BiomeModifier biomeModifier : biomeModifiers1) {
            if (biomeModifier instanceof BiomeModifiers.AddSpawnsBiomeModifier(HolderSet<Biome> biomes, List<MobSpawnSettings.SpawnerData> spawners)) {
                if (spawners.size() == 1 && spawners.getFirst().type == MFEntities.FLUID_COW.get()) {
                    modifier = biomeModifier;
                    break;
                }
            }
        }
        biomeModifiers1.remove(modifier);

        Utils.createSpawnModifiers(server, biomeModifiers1);

        return Collections.unmodifiableList(biomeModifiers1);
    }
}
