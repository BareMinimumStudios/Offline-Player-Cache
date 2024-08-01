package com.bibireden.opc.mixin;

import com.bibireden.opc.api.OfflinePlayerCacheData;
import com.bibireden.opc.cache.OfflinePlayerCacheImpl;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DerivedLevelData.class)
public class DerivedLevelDataMixin implements OfflinePlayerCacheData {
    @Shadow @Final private ServerLevelData wrapped;

    @NotNull
    @Override
    public OfflinePlayerCacheImpl opc$data() {
        return ((OfflinePlayerCacheData) this.wrapped).opc$data();
    }
}
