package com.bibireden.opc.neoforge

import com.bibireden.opc.OfflinePlayerCacheInitializer
import com.bibireden.opc.neoforge.bus.OPCEventHandler
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge

/** Main class for the mod on the Forge platform. */
@Mod(OfflinePlayerCacheInitializer.MOD_ID)
class OfflinePlayerCacheInitializerNeoForge {
    init {
        OfflinePlayerCacheInitializer.init()

        NeoForge.EVENT_BUS.register(OPCEventHandler())
    }
}
