package com.bibireden.opc.api

import com.bibireden.opc.cache.OfflinePlayerCacheImpl

interface OfflinePlayerCacheData {
    fun `opc$data`(): OfflinePlayerCacheImpl
}