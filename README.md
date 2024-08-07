![Offline Player Cache Banner](https://cdn.modrinth.com/data/cached_images/38a9b779772577bb5924af170e38f9cb1e313855.png)

<p style="text-align: center">
    <img href="https://github.com/PlayerEXDirectorsCut/offline-player-cache/blob/1.20.1/main/LICENSE" src="https://img.shields.io/badge/MIT-MIT?style=for-the-badge&label=LICENCE&labelColor=1A1A1A&color=FFFFFF&link=https%3A%2F%2Fgithub.com%2FPlayerEXDirectorsCut%2Foffline-player-cache%2Fblob%2F1.20.1%2Fmain%2FLICENSE">
    <img href="https://github.com/PlayerEXDirectorsCut/offline-player-cache/stargazers" src="https://img.shields.io/github/stars/PlayerEXDirectorsCut/offline-player-cache?style=for-the-badge&logo=github&labelColor=1A1A1A&color=FFFFFF&link=https%3A%2F%2Fgithub.com%2FPlayerEXDirectorsCut%2Foffline-player-cache%2Fstargazers">
    <img href="https://github.com/PlayerEXDirectorsCut/offline-player-cache/forks" src="https://img.shields.io/github/forks/PlayerEXDirectorsCut/offline-player-cache?style=for-the-badge&logo=github&labelColor=1A1A1A&color=FFFFFF&link=https%3A%2F%2Fgithub.com%2FPlayerEXDirectorsCut%2Foffline-player-cache%2Fforks">
    <img href="https://github.com/PlayerEXDirectorsCut/offline-player-cache/issues" src="https://img.shields.io/github/issues/BareMinimumStudios/offline-player-cache?style=for-the-badge&logo=github&label=ISSUES&link=https%3A%2F%2Fgithub.com%2FBareMinimumStudios%2Foffline-player-cache%2Fissues&labelColor=1A1A1A">  
</p>

<p style="text-align: center">
    <img alt="fabric" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/fabric_vector.svg">
    <img alt="quilt" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/quilt_vector.svg">
    <img alt="forge" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/forge_vector.svg">
</p>

<p style="text-align: center">
    <a href="https://bareminimumstudios.github.io/Bare-Minimum-Docs/">
        <img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/documentation/generic_vector.svg">
    </a>
    <a href="https://github.com/BareMinimumStudios/offline-player-cache">
        <img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg">
    </a>
    <img alt="java" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/built-with/java_vector.svg">
    <img alt="gradle" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/built-with/gradle_vector.svg">
    <a href="https://discord.gg/pcRw79hwey">
        <img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-plural_vector.svg">
    </a>
</p>

---

### Preamble 📝

**Offline Player Cache: Directors Cut** has been completely redone from the ground up, and its framework predates `1.*` and below.
This was developed in mind to have **persistent leaderboards** for servers for their offline players.

### Content 📦
Modders may register `Record`'s linked with an id and a `Codec` to serialize/deserialize it.

Upon a player's disconnection from the server, their cached data is stored into the servers `level` data, which then can be accessed through code or through the commands the mod provides.

Upon a player's reconnection to the server, their cached data is deleted.

### Commands

#### `/opc get <uuid>|<name> <key>`
Provides details about the current player value. If they are online, it will provide their **current** value, but if they are offline, it will provide their **cached** value.

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

## Migration to `2.*`

- There are no longer any concepts of keys. Instead, you are to register an id, a `Record`, and a `Codec`.

### Creating a Record

```java
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Contract(String label, boolean signed) {
    public static Codec<Contract> CODEC = RecordCodecBuilder.create((instance) ->
        instance.group(
            Codec.STRING.fieldOf("label").forGetter(Contract::label),
            Codec.BOOL.fieldOf("signed").forGetter(Contract::signed)
        ).apply(instance, Contract::new)
    );
}
```

### Registering

```java
import maven_group.modid.concept.Contract; // Контракт :)

// somewhere during static/mod initialization
private void init() {
    OfflinePlayerCacheAPI.register(CONTRACT_RECORD_ID, Contract.class, Contract.CODEC, (Player player) -> {
        // within this block, you decide how to translate a player's data to the Record you chose.
        return new Contract(player.getName() + ":contracted", true);
    });
}

```

### Obtaining

```java
var cache = OfflinePlayerCacheAPI.getCache(server);
cache.getEntry(Contract.class, "bibi_reden").ifPresent(contract -> {
    // we now know that there is a valid Contract entry for this player.
    // You can also use a UUID to fetch an entry as well.
});
```

### A Special Thanks to our Sponsor

---

<p><img src="https://i.imgur.com/V38aMzY.png" alt="Sponsor Banner"/></p>
<p><b>Use code &quot;BAREMINIMUM&quot; to get 15% off your first month!</b></p>
