package com.bibireden.opc.api
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.player.Player
import java.util.*

interface OfflinePlayerCache {
    /** Current [UUID]s in the cache. */
    val uuids: Collection<UUID>
    /** Current **usernames** in the cache. */
    val usernames: Collection<String>

    /** Checks if the player [UUID] is in the cache or not. */
    fun isPlayerCached(uuid: UUID): Boolean

    /** Checks if the player with the username is in the cache or not. */
    fun isPlayerCached(username: String): Boolean

    /** Attempts to get a username from a cached player based on the [UUID] of that player. */
    fun getUsernameFromUUID(uuid: UUID): String?

    /** Attempts to get a [UUID] from a cached player based on the **username** of that player. */
    fun getUUIDFromUsername(username: String): UUID?

    /** Attempts to get an entry based on the [UUID] of the player. */
    fun <R : Record> getEntry(id: Class<R>, uuid: UUID): Optional<R>
    /** Attempts to get an entry based on the **username** of the player. */
    fun <R : Record> getEntry(id: Class<R>, username: String): Optional<R>

    /** Caches a [Player]s key-associated values that are currently registered. */
    fun cache(player: Player): Boolean
    /** Un-caches a [Player]s key-associated values currently registered. */
    fun unCache(player: Player): Boolean

    /** Un-caches based on [UUID]. */
    fun unCache(uuid: UUID): Boolean

    /** Un-caches based on a **username**. */
    fun unCache(username: String): Boolean

    /** Un-caches one entry based on the [Player] entity itself. */
    fun <R : Record> unCacheEntry(id: Class<R>, player: Player): Boolean
    /** Un-caches one entry based on the **username** of the player. */
    fun <R : Record> unCacheEntry(id: Class<R>, username: String): Boolean
    /** Un-caches one entry based on the [UUID] of the player. */
    fun <R : Record> unCacheEntry(id: Class<R>, uuid: UUID): Boolean

    /** Gets the currently cached entry related to this player by the [UUID]. */
    fun getPlayerCache(uuid: UUID): Optional<Map<Class<out Record>, Record>>

    /** Gets the currently cached entry related to this player by the username. */
    fun getPlayerCache(username: String): Optional<Map<Class<out Record>, Record>>
}