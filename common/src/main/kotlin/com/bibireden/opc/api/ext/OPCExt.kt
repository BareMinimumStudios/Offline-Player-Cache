package com.bibireden.opc.api.ext

import com.bibireden.opc.api.OfflinePlayerCache
import net.minecraft.world.entity.player.Player
import java.util.*

/** Attempts to get an entry based on the [UUID] of the player. */
inline fun <reified R : Record> OfflinePlayerCache.getEntry(uuid: UUID): Optional<R> = this.getEntry(R::class.java, uuid)

/** Attempts to get an entry based on the **username** of the player. */
inline fun <reified R : Record> OfflinePlayerCache.getEntry(username: String): Optional<R> = this.getEntry(R::class.java, username)

/** Un-caches one entry based on the [Player] entity itself. */
inline fun <reified R : Record> OfflinePlayerCache.unCacheEntry(player: Player): Boolean = this.unCacheEntry(R::class.java, player)

/** Un-caches one entry based on the **username** of the player. */
inline fun <reified R : Record> OfflinePlayerCache.unCacheEntry(username: String): Boolean = this.unCacheEntry(R::class.java, username)

/** Un-caches one entry based on the [UUID] of the player. */
inline fun <reified R : Record> OfflinePlayerCache.unCacheEntry(uuid: UUID): Boolean = this.unCacheEntry(R::class.java, uuid)