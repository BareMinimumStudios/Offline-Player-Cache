package com.bibireden.opc.cache

import net.minecraft.resources.ResourceLocation

/**
 * A key that is meant to be registered into the [com.bibireden.opc.api.OfflinePlayerCacheAPI], and
 * is extended from to implement values meant to be stored on the server no matter a player's online or offline state.
 *
 * @author bibi-reden, DataEncoded
 * @see [OfflinePlayerCacheImpl]
 */
abstract class CachedPlayerKey(val id: ResourceLocation)