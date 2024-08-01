package com.bibireden.opc.mixin;

import com.bibireden.opc.OfflinePlayerCacheInitializer;
import com.bibireden.opc.api.OfflinePlayerCacheData;
import com.bibireden.opc.cache.OfflinePlayerCacheImpl;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelVersion;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PrimaryLevelData.class)
public class PrimaryLevelDataMixin implements OfflinePlayerCacheData {
    @Unique
    OfflinePlayerCacheImpl opc$cache = new OfflinePlayerCacheImpl();

    @Inject(method = "setTagData", at = @At("HEAD"))
    private void setTagData(RegistryAccess registry, CompoundTag nbt, CompoundTag playerNBT, CallbackInfo ci) {
        nbt.put(OfflinePlayerCacheInitializer.MOD_ID, opc$cache.toTag());
    }

    @Inject(method = "parse", at = @At("RETURN"))
    private static <T> void parseTag(Dynamic<T> dynamic, DataFixer fixerUpper, int playerDataVersion, @Nullable CompoundTag loadedPlayerTag, LevelSettings settings, LevelVersion levelVersion, PrimaryLevelData.SpecialWorldProperty specialWorldProperty, WorldOptions worldOptions, Lifecycle worldGenSettingsLifecycle, CallbackInfoReturnable<PrimaryLevelData> cir) {
        var levelData = cir.getReturnValue();
        dynamic.get(OfflinePlayerCacheInitializer.MOD_ID).result()
            .map(Dynamic::getValue)
            .ifPresent(nbt -> ((OfflinePlayerCacheData) levelData).opc$data().parseTag((ListTag) nbt));
    }

    @NotNull
    @Override
    public OfflinePlayerCacheImpl opc$data() {
        return this.opc$cache;
    }
}
