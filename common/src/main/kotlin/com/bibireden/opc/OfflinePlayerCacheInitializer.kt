package com.bibireden.opc

import com.bibireden.opc.api.OfflinePlayerCacheAPI
import com.bibireden.opc.test.TestingKeys
import com.mojang.logging.LogUtils
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import org.jetbrains.annotations.ApiStatus
import org.slf4j.Logger

object OfflinePlayerCacheInitializer {
    /** The mod id for  opc.  */
    const val MOD_ID: String = "opc"

    val LEVEL_KEY = ResourceLocation.tryBuild("opc", "level")!!

    /** The logger for opc.  */
    val LOGGER: Logger = LogUtils.getLogger()

    /**
     * Initializes the mod.
     */
    @JvmStatic
    fun init() {}

    init {
        OfflinePlayerCacheAPI.register(LEVEL_KEY, TestingKeys.Level::class.java, TestingKeys.Level.CODEC) { player ->
            TestingKeys.Level(player.experienceLevel)
        }
    }
}
