package com.bibireden.opc.api

import com.bibireden.opc.cache.OfflinePlayerCacheImpl
import com.bibireden.opc.cache.OfflinePlayerCacheProvider
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.mojang.serialization.Codec
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.player.Player

typealias PlayerSerializer<R> = (Player) -> R

/**
 * The API for the [OfflinePlayerCacheImpl] that allows for the registering and accessing of offline player data.
 *
 * @author bibi-reden, DataEncoded, OverlordsIII
 */
object OfflinePlayerCacheAPI {
    /** The static registry of keys. Contains a view of what is currently registered in the API. */
    @JvmStatic
    val cachedKeys: BiMap<ResourceLocation, Class<out Record>>
        get() = HashBiMap.create(OfflinePlayerCacheImpl.keys)

    /**
     * Register a [java.lang.Record] key based on a given
     * [ResourceLocation] (used for identifying in commands, etc...)
     *
     * @param id The [ResourceLocation] used to look up the [Record] entry.
     * @param key The class instance of the [Record].
     * @param codec A given [Codec] to ser/de the data.
     * @param getter The function used to transform data on the [ServerPlayer] to the given [Record].
     *
     * @throws IllegalStateException If the given [id] is already registered.
     */
    @JvmStatic
    fun <R : Record> register(id: ResourceLocation, key: Class<R>, codec: Codec<R>, getter: PlayerSerializer<R>) = OfflinePlayerCacheImpl.register(id, key, codec, getter)

    /** Gets the cache located in the [MinecraftServer]'s level-data. */
    @JvmStatic
    fun getCache(server: MinecraftServer): OfflinePlayerCacheProvider = OfflinePlayerCacheProvider(server)
}