package com.bibireden.opc.forge

import com.bibireden.opc.OfflinePlayerCacheInitializer
import com.bibireden.opc.forge.bus.OPCEventHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod

/**
 * Main class for the mod on the Forge platform.
 */
@Mod(OfflinePlayerCacheInitializer.MOD_ID)
class OfflinePlayerCacheInitializerForge {
    init {
        OfflinePlayerCacheInitializer.init()

        MinecraftForge.EVENT_BUS.register(OPCEventHandler())
    }
}
