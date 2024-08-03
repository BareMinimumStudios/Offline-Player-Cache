package com.bibireden.opc.neoforge.bus

import com.bibireden.opc.OfflinePlayerCacheCommands
import com.bibireden.opc.OfflinePlayerCacheInitializer
import com.bibireden.opc.api.OfflinePlayerCacheAPI
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.EventBusSubscriber.Bus
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent

@EventBusSubscriber(modid = OfflinePlayerCacheInitializer.MOD_ID, bus = Bus.GAME, value = [Dist.DEDICATED_SERVER])
class OPCEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onPlayerConnect(event: PlayerEvent.PlayerLoggedInEvent) {
        OfflinePlayerCacheAPI.getCache(event.entity.server ?: return).unCache(event.entity)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onPlayerDisconnect(event: PlayerEvent.PlayerLoggedOutEvent) {
        OfflinePlayerCacheAPI.getCache(event.entity.server ?: return).cache(event.entity)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun registerCommands(event: RegisterCommandsEvent) {
        OfflinePlayerCacheCommands.register(event.dispatcher)
    }
}