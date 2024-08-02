package com.bibireden.opc

import com.bibireden.opc.api.OfflinePlayerCacheAPI
import com.mojang.logging.LogUtils
import org.slf4j.Logger

object OfflinePlayerCacheInitializer {
    /** The mod id for  opc.  */
    const val MOD_ID: String = "opc"

    /** The logger for opc.  */
    val LOGGER: Logger = LogUtils.getLogger()

    /**
     * Initializes the mod.
     */
    @JvmStatic
    fun init() {
        LOGGER.info("recorded [${OfflinePlayerCacheAPI.registeredKeys.size}] registered keys!")
    }
}
