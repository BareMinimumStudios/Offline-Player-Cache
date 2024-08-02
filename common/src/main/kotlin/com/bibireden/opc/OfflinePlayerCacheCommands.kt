package com.bibireden.opc

import com.bibireden.opc.api.OfflinePlayerCacheAPI
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.commands.arguments.UuidArgument
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import java.util.UUID

/**
 * The command-tree for the **`OfflinePlayerCache`**.
 *
 * Was converted to the tree-format of `brigadier` for a cleaner look.
 *
 * @author OverlordsIII, bibi-reden
 * */
object OfflinePlayerCacheCommands {
    private val SUGGEST_KEYS = SuggestionProvider<CommandSourceStack> { _, builder ->
        SharedSuggestionProvider.suggestResource(OfflinePlayerCacheAPI.registeredKeys.keys, builder)
        builder.buildFuture()
    }
    private val SUGGEST_NAMES = SuggestionProvider<CommandSourceStack> { ctx, builder ->
        OfflinePlayerCacheAPI.getCache(ctx.source.server).usernames.forEach(builder::suggest)
        builder.buildFuture()
    }
    private val SUGGEST_UUIDS = SuggestionProvider<CommandSourceStack> { ctx, builder ->
        OfflinePlayerCacheAPI.getCache(ctx.source.server).uuids.forEach { builder.suggest(it.toString()) }
        builder.buildFuture()
    }

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(Commands.literal("opc")
            .requires { it.hasPermission(2) }
            .then(Commands.literal("get")
                .then(Commands.literal("name")
                    .then(Commands.argument("name", StringArgumentType.string())
                        .suggests(SUGGEST_NAMES)
                        .then(Commands.argument("key", ResourceLocationArgument.id())
                            .suggests(SUGGEST_KEYS)
                            .executes { context -> executeGetKey(context) { ctx -> StringArgumentType.getString(ctx, "name") } }
                        )
                    )
                )
                .then(Commands.literal("uuid")
                    .then(Commands.argument("uuid", UuidArgument.uuid())
                        .suggests(SUGGEST_UUIDS)
                        .then(Commands.argument("key", ResourceLocationArgument.id())
                            .suggests(SUGGEST_KEYS)
                            .executes { context -> executeGetKey(context) {
                                    ctx -> UuidArgument.getUuid(ctx, "uuid") }
                            }
                        )
                    )
                )
            )
            .then(Commands.literal("remove")
                .then(Commands.literal("name")
                    .then(Commands.argument("name", StringArgumentType.string())
                        .suggests(SUGGEST_NAMES)
                        .executes { context ->
                            executeRemoveAllCachedTo(context) { ctx -> StringArgumentType.getString(ctx, "name") }
                        }
                        .then(Commands.argument("key", ResourceLocationArgument.id())
                            .suggests(SUGGEST_KEYS)
                            .executes { context ->
                                executeRemoveKey(context) { ctx -> StringArgumentType.getString(ctx, "name") }
                            }
                        )
                    )
                )
                .then(Commands.literal("uuid")
                    .then(Commands.argument("uuid", UuidArgument.uuid())
                        .suggests(SUGGEST_UUIDS)
                        .executes { context ->
                            executeRemoveAllCachedTo(context) { ctx -> UuidArgument.getUuid(ctx, "uuid") }
                        }
                        .then(Commands.argument("key", ResourceLocationArgument.id())
                            .suggests(SUGGEST_KEYS)
                            .executes { context ->
                                executeRemoveKey(context) { ctx: CommandContext<CommandSourceStack> -> UuidArgument.getUuid(ctx,"uuid") }
                            }
                        )
                    )
                )
            )
            .then(Commands.literal("list")
                .then(Commands.literal("name")
                    .then(Commands.argument("name", StringArgumentType.string())
                        .suggests(SUGGEST_NAMES)
                        .executes { context ->
                            executeListKeys(context) { ctx -> StringArgumentType.getString(ctx, "name")}
                        }
                    )
                )
                .then(Commands.literal("uuid")
                    .then(Commands.argument("uuid", UuidArgument.uuid())
                        .suggests(SUGGEST_UUIDS)
                        .executes { context ->
                            executeListKeys(context) { ctx -> UuidArgument.getUuid(ctx, "uuid")}
                        }
                    )
                )
            )
        )
    }

    private fun <T> executeListKeys(ctx: CommandContext<CommandSourceStack>, input: (CommandContext<CommandSourceStack>) -> T): Int {
        val id = input(ctx);

        val api = OfflinePlayerCacheAPI.getCache(ctx.source.server)

        val (values, otherID) = when (id) {
            is String -> api.getPlayerCache(id).orElse(null) to api.getUUIDFromUsername(id)
            is UUID -> api.getPlayerCache(id).orElse(null) to api.getUsernameFromUUID(id)
            else -> null
        } ?: return -1

        ctx.source.sendSuccess(fetchingMessage(id), false)

        if (values.isEmpty()) {
            ctx.source.sendSuccess({Component.literal("No values for: $id@$otherID").withStyle(ChatFormatting.GRAY)}, false)
        }
        else {
            ctx.source.sendSuccess({Component.literal("Found: $otherID").withStyle(ChatFormatting.GREEN)}, false)
            ctx.source.sendSuccess({Component.literal("Listing [${values.size}] value(s):").withStyle(ChatFormatting.GREEN)}, false)
            values.forEach { (key, value) ->
                ctx.source.sendSuccess({Component.literal( "${OfflinePlayerCacheAPI.registeredKeys.inverse()[key]} = $value").withStyle(ChatFormatting.WHITE)}, false);
            }
        }

        return 1;
    }

    private fun <T> executeRemoveKey(ctx: CommandContext<CommandSourceStack>, input: (CommandContext<CommandSourceStack>) -> T): Int {
        val id = input(ctx)
        val identifier = ResourceLocationArgument.getId(ctx, "key")

        val value = OfflinePlayerCacheAPI.registeredKeys[identifier]

        if (value == null) {
            ctx.source.sendSuccess(nullKeyMessage(id), false)
            return -1
        }

        val opc = OfflinePlayerCacheAPI.getCache(ctx.source.server)

        when (id) {
            is String -> opc.unCacheEntry(value, id)
            is UUID -> opc.unCacheEntry(value, id)
        }

        ctx.source.sendSuccess({ Component.literal("$id: un-cached [$identifier]").withStyle(ChatFormatting.WHITE) }, false)

        return 1
    }

    private fun <T> executeRemoveAllCachedTo(context: CommandContext<CommandSourceStack>, input: (CommandContext<CommandSourceStack>) -> T): Int {
        val uuidOrPlayer = input(context)
        val opc = OfflinePlayerCacheAPI.getCache(context.source.server)

        val executed = when (uuidOrPlayer) {
            is String -> opc.unCache(uuidOrPlayer)
            is UUID -> opc.unCache(uuidOrPlayer)
            else -> false;
        }

        context.source.sendSuccess({ Component.literal( "$uuidOrPlayer: cleared" ).withStyle(ChatFormatting.WHITE) }, false)

        return if (executed) 1 else -1
    }

    private fun <T> executeGetKey(ctx: CommandContext<CommandSourceStack>, input: (CommandContext<CommandSourceStack>) -> T): Int {
        val id = input(ctx)
        val identifier = ResourceLocationArgument.getId(ctx, "key")
        val key = OfflinePlayerCacheAPI.registeredKeys[identifier]

        if (key == null) {
            ctx.source.sendSuccess(nullKeyMessage(id), false)
            return -1
        }

        val server = ctx.source.server

        val api = OfflinePlayerCacheAPI.getCache(server)

        val (value, otherId) = when (id) {
            is String -> (api.getEntry(key, id) to api.getUUIDFromUsername(id))
            is UUID -> (api.getEntry(key, id) to api.getUsernameFromUUID(id))
            else -> null
        } ?: return -1;

        ctx.source.sendSuccess(fetchingMessage(id), false)
        ctx.source.sendSuccess({Component.literal("Found: $otherId").withStyle(ChatFormatting.GREEN)}, false)
        if (value.isPresent) {
            ctx.source.sendSuccess({ Component.literal("$identifier = ${value.get()}").withStyle(ChatFormatting.WHITE)}, false)
        }
        else {
            ctx.source.sendSuccess(nullKeyMessage(identifier), false)
        }

        return 1
    }

    private fun <T> fetchingMessage(id: T): () -> MutableComponent = { Component.literal("Fetching: $id").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD) }
    private fun <T> nullKeyMessage(id: T): () -> MutableComponent = { Component.literal("$id -> <null_key>").withStyle(ChatFormatting.RED) }
}