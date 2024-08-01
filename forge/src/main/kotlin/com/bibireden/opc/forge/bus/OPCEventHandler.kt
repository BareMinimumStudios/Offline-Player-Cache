package com.bibireden.opc.forge.bus

import com.bibireden.opc.OfflinePlayerCacheCommands
import com.bibireden.opc.OfflinePlayerCacheInitializer
import com.bibireden.opc.api.OfflinePlayerCacheAPI
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus

@Mod.EventBusSubscriber(modid = OfflinePlayerCacheInitializer.MOD_ID, bus = Bus.FORGE, value = [Dist.DEDICATED_SERVER])
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