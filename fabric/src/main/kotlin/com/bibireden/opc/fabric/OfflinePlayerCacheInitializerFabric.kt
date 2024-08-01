package com.bibireden.opc.fabric

import com.bibireden.opc.OfflinePlayerCacheCommands
import com.bibireden.opc.OfflinePlayerCacheInitializer
import com.bibireden.opc.api.OfflinePlayerCacheAPI
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents

/**
 * This class is the entrypoint for the mod on the Fabric platform.
 */
class OfflinePlayerCacheInitializerFabric : ModInitializer {
    override fun onInitialize() {
        OfflinePlayerCacheInitializer.init()

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            OfflinePlayerCacheCommands.register(dispatcher)
        }

        ServerPlayConnectionEvents.JOIN.register { handler, _, server ->
            OfflinePlayerCacheAPI.getCache(server).unCache(handler.player)
        }

        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            OfflinePlayerCacheAPI.getCache(server).cache(handler.player)
        }
    }
}
