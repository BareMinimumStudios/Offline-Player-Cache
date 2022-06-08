package com.github.clevernucleus.opc.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.clevernucleus.opc.impl.OfflinePlayerCacheCommand;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

@Mixin(CommandManager.class)
abstract class CommandManagerMixin {
	
	@Final
	@Shadow
	private CommandDispatcher<ServerCommandSource> dispatcher;
	
	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;findAmbiguities(Lcom/mojang/brigadier/AmbiguityConsumer;)V"))
	private void opc_init(CommandManager.RegistrationEnvironment environment, CallbackInfo info) {
		OfflinePlayerCacheCommand.register(this.dispatcher);
	}
}
