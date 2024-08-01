![Offline Player Cache Banner](https://cdn.modrinth.com/data/cached_images/8bf7b045806b81dba417cabafe08bed2d4fd4a1c.png)
[![GitHub license](https://img.shields.io/badge/MIT-MIT?style=for-the-badge&label=LICENCE&labelColor=1A1A1A&color=FFFFFF&link=https%3A%2F%2Fgithub.com%2FPlayerEXDirectorsCut%2Foffline-player-cache%2Fblob%2F1.20.1%2Fmain%2FLICENSE)](https://github.com/PlayerEXDirectorsCut/offline-player-cache/blob/1.20.1/main/LICENSE)
[![GitHub stars](https://img.shields.io/github/stars/PlayerEXDirectorsCut/offline-player-cache?style=for-the-badge&logo=github&labelColor=1A1A1A&color=FFFFFF&link=https%3A%2F%2Fgithub.com%2FPlayerEXDirectorsCut%2Foffline-player-cache%2Fstargazers
)](https://github.com/PlayerEXDirectorsCut/offline-player-cache/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/PlayerEXDirectorsCut/offline-player-cache?style=for-the-badge&logo=github&labelColor=1A1A1A&color=FFFFFF&link=https%3A%2F%2Fgithub.com%2FPlayerEXDirectorsCut%2Foffline-player-cache%2Fforks
)](https://github.com/PlayerEXDirectorsCut/offline-player-cache/forks)
[![GitHub issues](https://img.shields.io/github/issues/PlayerEXDirectorsCut/offline-player-cache?style=for-the-badge&logo=github&label=ISSUES&labelColor=1A1A1A&link=https%3A%2F%2Fgithub.com%2FPlayerEXDirectorsCut%2Foffline-player-cache%2Fissues
)](https://github.com/PlayerEXDirectorsCut/offline-player-cache/issues)

[![docs](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/documentation/generic_vector.svg)](https://playerexdirectorscut.github.io/Bare-Minimum-Docs/)
![mkdocs](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/built-with/mkdocs_vector.svg)
![java17](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/built-with/java17_vector.svg)
[![curseforge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/opc-directors-cut)
[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/opc-directors-cut)

![fabric](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/fabric_vector.svg)
![quilt](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/quilt_vector.svg)
![forge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/forge_vector.svg)
---

### Preamble

**Offline Player Cache: Directors Cut** has moved from being a port to a rewritten mod.
It allows the ability to cache player data to the server based on cached keys.

This was developed in mind to have **persistent leaderboards** for servers for their offline players.

### Summary
Mods may register **keys** that provide methods for obtaining data based on a player, and read and write methods based on `nbt` data.

Upon a player's disconnection from the server, their cached data is stored into the servers `level` data, and can be accessed.

Upon a player's reconnection to the server, their cached data is deleted, and the cache will prefer using the player's current data.

### Commands

#### `/opc get <uuid>|<name> <key>`
Provides details about the current player value. If they are online, it will provide their **current** value, but if they are offline, it will provide their **cached** value.

> **Notice ✨**
>
> *If the value is a `Number` and ran from a command block, the redstone output is the absolute modulus of 16.*

#### `/opc remove <uuid>|<name> <key>`
If the player with the associated username or UUID is offline, it will remove that players **cached** value based on the selected key.
If the player is **online**, nothing will occur with this command.

#### `/opc list <uuid>|<name>`
Lists all the keys and values this player has stored if they are offline, or if they are online, their current ones.

## Developers Guide

### Setup
Offline Player Cache has a [**Modrinth**](https://modrinth.com/mod/opc-directors-cut) and [**Curseforge**](https://curseforge.com/minecraft/mc-mods/opc-directors-cut) page.

In order to develop with the API, please add the following:

**`gradle.properties`**

```properties
opc_version=...
```

**`build.gradle`**

```groovy
repositories {
    maven {
        name = "Modrinth"
        url = "https://api.modrinth.com/maven"
        content {
            includeGroup "maven.modrinth"
        }
    }
}

dependencies {
    modImplementation "maven.modrinth:opc-directors-cut:${project.opc_version}"
    // include this if you do not want to force your users to install the mod.
    include "maven.modrinth:opc-directors-cut:${project.opc_version}"
}
```

<details><summary>Alternatively, if you are using Kotlin DSL:</summary>

**`build.gradle.kts`**

```kotlin
repositories {
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {
    modImplementation("maven.modrinth:opc-directors-cut:${properties["opc_version"]}")
    // include this if you do not want to force your users to install the mod.
    include("maven.modrinth:opc-directors-cut:${properties["opc_version"]}")
}
```

</details>

### Migration to 2.0.0

- A considerable amount of internal and external changes have happened with the mod (again).
- It's record based, so you are able to register any `Record` with an associated identifier.
- More docs coming soon...

### Using the Cache API
- In order to utilize the cache, utilize the new `OfflinePlayerCacheAPI`, which will allow you to register `CachedPlayerKeys` to the cache, and permit you to obtain data based on the keys provided.

### Registering & Using Keys

```kotlin
val LEVEL_KEY = ResourceLocation.tryBuild("opc", "level")!!

@JvmRecord
data class Level(val level: Int) {
    companion object {
        val CODEC: Codec<Level> = RecordCodecBuilder.create {
            it.group(Codec.INT.fieldOf("level").forGetter(Level::level)).apply(it, ::Level)
        }
    }
}

// static init
init {
    OfflinePlayerCacheAPI.register(LEVEL_KEY, TestingKeys.Level::class.java, TestingKeys.Level.CODEC) { player ->
        TestingKeys.Level(player.experienceLevel)
    }
}
```
Once your key is registered, you are good to go!

Keys will automatically be managed by the cache, and you should be able to see these keys through the commands we provide.

---

[![discord-plural](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-plural_vector.svg)](https://discord.gg/4kTmk8Skzm)
