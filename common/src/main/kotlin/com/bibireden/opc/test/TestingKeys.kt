package com.bibireden.opc.test

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

object TestingKeys {
    @JvmRecord
    data class Level(val level: Int) {
        companion object {
            val CODEC: Codec<Level> = RecordCodecBuilder.create {
                it.group(Codec.INT.fieldOf("level").forGetter(Level::level)).apply(it, ::Level)
            }
        }
    }
}