package com.bibireden.opc.cache

import com.bibireden.opc.api.OfflinePlayerCache
import com.bibireden.opc.api.OfflinePlayerCacheData
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.player.Player
import java.util.*

class OfflinePlayerCacheProvider(val server: MinecraftServer) : OfflinePlayerCache {
    private val cached: OfflinePlayerCacheImpl = (server.worldData.overworldData() as OfflinePlayerCacheData).`opc$data`()
    
    override val uuids: Collection<UUID>
        get() = cached.uuids

    override val usernames: Collection<String>
        get() = cached.usernames

    override fun getUsernameFromUUID(uuid: UUID): String? = cached.getUsernameFromUUID(uuid)

    override fun getUUIDFromUsername(username: String): UUID? = cached.getUUIDFromUsername(username)

    override fun <R : Record> getEntry(id: Class<R>, uuid: UUID): Optional<R> = cached.getEntry(id, uuid, server)

    override fun <R : Record> getEntry(id: Class<R>, username: String): Optional<R> = cached.getEntry(id, username, server)

    override fun cache(player: Player): Boolean = cached.cache(player)

    override fun unCache(player: Player): Boolean = cached.unCache(player)

    override fun unCache(uuid: UUID): Boolean = cached.unCache(uuid)

    override fun unCache(username: String): Boolean = cached.unCache(username)

    override fun <R : Record> unCacheEntry(id: Class<R>, player: Player): Boolean = cached.unCacheEntry(id, player)

    override fun <R : Record> unCacheEntry(id: Class<R>, username: String): Boolean = cached.unCacheEntry(id, username)

    override fun <R : Record> unCacheEntry(id: Class<R>, uuid: UUID): Boolean = cached.unCacheEntry(id, uuid)

    override fun getPlayerCache(uuid: UUID): Optional<Map<Class<out Record>, Record>> = cached.getPlayerCache(uuid)

    override fun getPlayerCache(username: String): Optional<Map<Class<out Record>, Record>> = cached.getPlayerCache(username)

    override fun isPlayerCached(uuid: UUID): Boolean = cached.isPlayerCached(uuid)

    override fun isPlayerCached(username: String): Boolean = cached.isPlayerCached(username)
}