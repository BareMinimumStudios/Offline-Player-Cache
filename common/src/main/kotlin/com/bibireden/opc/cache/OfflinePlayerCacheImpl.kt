package com.bibireden.opc.cache

import com.bibireden.opc.OfflinePlayerCacheInitializer
import com.bibireden.opc.api.PlayerSerializer
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.mojang.serialization.Codec
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.player.Player
import org.jetbrains.annotations.ApiStatus
import java.util.*

@ApiStatus.Internal
class OfflinePlayerCacheImpl(
    private val cache: MutableMap<UUID, MutableMap<Class<out Record>, Record>> = mutableMapOf(),
    private val usernamesToUUID: BiMap<String, UUID> = HashBiMap.create()
) {
    companion object {
        val keys: Map<ResourceLocation, Class<out Record>>
            get() = _keys

        private val _keys: BiMap<ResourceLocation, Class<out Record>> = HashBiMap.create()
        private val codecs: MutableMap<ResourceLocation, Codec<in Record>> = mutableMapOf()
        private val serializers: MutableMap<ResourceLocation, PlayerSerializer<Record>> = mutableMapOf()

        fun <R : Record> register(id: ResourceLocation, key: Class<R>, codec: Codec<R>, getter: PlayerSerializer<R>) {
            if (_keys.containsKey(id)) throw IllegalStateException("Cache ID#: $id is already registered!")

            _keys[id] = key
            codecs[id] = codec as Codec<in Record>
            serializers[id] = getter
        }
    }

    val uuids: Collection<UUID>
        get() = this.usernamesToUUID.values.toList()

    val usernames: Collection<String>
        get() = this.usernamesToUUID.keys.toList()

    fun getUsernameFromUUID(uuid: UUID): String? = this.usernamesToUUID.inverse()[uuid]

    fun getUUIDFromUsername(username: String): UUID? = this.usernamesToUUID[username]

    @Suppress("UNCHECKED_CAST")
    private fun <R : Record> getFromPlayer(id: Class<R>, player: Player): R? = serializers[_keys.inverse()[id]]?.invoke(player) as R?

    @Suppress("UNCHECKED_CAST")
    private fun <R : Record> getFromCache(id: Class<R>, uuid: UUID): R? = cache[uuid]?.get(id) as? R

    fun <R : Record> getEntry(id: Class<R>, uuid: UUID, server: MinecraftServer): Optional<R> {
        val player = server.playerList.players.find { it.uuid == uuid }
        if (player != null) {
            val entry = this.getFromPlayer(id, player)
            if (entry != null) return Optional.of(entry)
        }
        return Optional.ofNullable(this.getFromCache(id, uuid))
    }

    fun <R : Record> getEntry(id: Class<R>, username: String, server: MinecraftServer): Optional<R> {
        val player = server.playerList.players.find { it?.gameProfile?.name == username }
        if (player != null) {
            val entry = this.getFromPlayer(id, player)
            if (entry != null) return Optional.of(entry)
        }
        val uuid = usernamesToUUID[username] ?: return Optional.empty()
        return Optional.ofNullable(this.getFromCache(id, uuid))
    }

    fun cache(player: Player): Boolean {
        if (player.gameProfile.name == null) return false
        this.cache[player.uuid] = keys.entries.associate { (id, key) -> key to serializers[id]?.invoke(player)!! }.toMutableMap()
        this.usernamesToUUID[player.gameProfile.name] = player.uuid
        return true
    }

    fun unCache(player: Player): Boolean {
        if (this.cache.remove(player.uuid) == null) return false
        this.usernamesToUUID.inverse().remove(player.uuid)
        return true
    }

    fun unCache(uuid: UUID): Boolean {
        if (this.cache.remove(uuid) == null) return false
        this.usernamesToUUID.inverse().remove(uuid)
        return true
    }

    fun unCache(username: String): Boolean {
        if (this.cache.remove(usernamesToUUID[username]) == null) return false
        this.usernamesToUUID.remove(username)
        return true
    }

    fun <R : Record> unCacheEntry(id: Class<R>, player: Player): Boolean {
        return this.cache[player.uuid]?.remove(id) != null
    }

    fun <R : Record> unCacheEntry(id: Class<R>, username: String): Boolean {
        return this.cache[this.usernamesToUUID[username]]?.remove(id) != null
    }

    fun <R : Record> unCacheEntry(id: Class<R>, uuid: UUID): Boolean {
        return this.cache[uuid]?.remove(id) != null
    }

    fun parseTag(list: ListTag) {
        if (list.isEmpty()) return

        this.cache.clear()
        this.usernamesToUUID.clear()

        for (index in list.indices) {
            val entry = list.getCompound(index)
            val keysCompound = entry.getCompound("keys")

            val uuid = entry.getUUID("uuid")
            val name = entry.getString("name")

            if (name.isEmpty()) continue

            val data = mutableMapOf<Class<out Record>, Record>()

            for (id in keysCompound.allKeys) {
                ResourceLocation.tryParse(id)?.let {
                    codecs[it]?.decode(NbtOps.INSTANCE, keysCompound.get(id))?.resultOrPartial(OfflinePlayerCacheInitializer.LOGGER::error)?.ifPresent { result ->
                        data.put(_keys[it] ?: return@ifPresent, result.first as Record)
                    }
                }
            }

            this.cache[uuid] = data
            this.usernamesToUUID[name] = uuid
        }
    }

    fun toTag(): ListTag {
        val list = ListTag()
        val uuidToUsernames = this.usernamesToUUID.inverse()

        for (uuid in this.cache.keys) {
            val data = this.cache[uuid] ?: continue

            val entry = CompoundTag()
            val keys = CompoundTag()

            entry.putUUID("uuid", uuid)
            entry.putString("name", uuidToUsernames.getOrDefault(uuid, ""))

            for ((key, record) in data) {
                codecs[_keys.inverse()[key]]?.encodeStart(NbtOps.INSTANCE, record)
                    ?.resultOrPartial(OfflinePlayerCacheInitializer.LOGGER::error)
                    ?.ifPresent { result ->
                        keys.put(_keys.inverse()[key].toString(), result)
                    }
            }

            entry.put("keys", keys)
            list.add(entry)
        }
        return list
    }

    fun getPlayerCache(uuid: UUID): Optional<Map<Class<out Record>, Record>> = Optional.ofNullable(this.cache[uuid])

    fun getPlayerCache(username: String): Optional<Map<Class<out Record>, Record>> = Optional.ofNullable(this.cache[usernamesToUUID[username]])

    fun isPlayerCached(uuid: UUID): Boolean = this.cache[uuid] != null

    fun isPlayerCached(username: String): Boolean = this.cache[usernamesToUUID[username]] != null
}