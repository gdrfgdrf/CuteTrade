package io.github.gdrfgdrf.cutetrade.command

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.github.gdrfgdrf.cutetrade.extension.send
import io.github.gdrfgdrf.cutetrade.extension.toCommandMessage
import io.github.gdrfgdrf.cutetrade.utils.command.CommandInvoker
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

abstract class AbstractCommand(
    private val command: String,
    private val onlyPlayer: Boolean = false,
    private val needOp: Boolean = false,
    private val noArgument: Boolean = false,
    private val tree: (LiteralArgumentBuilder<ServerCommandSource>) -> ArgumentBuilder<ServerCommandSource, *>
) {
    fun register(builder: LiteralArgumentBuilder<ServerCommandSource>) {
        val requires = CommandManager.literal(command).requires { source ->
            if (needOp) {
                source.hasPermissionLevel(3)
            }
            true
        }

        if (!noArgument) {
            builder.then(requires.then(this.tree(requires)))
        } else {
            builder.then(this.tree(requires))
        }
    }

    companion object {
        fun playerCheck(
            abstractCommand: AbstractCommand,
            commandContext: CommandContext<ServerCommandSource>,
            success: () -> Unit
        ) {
            val source = commandContext.source
            val commandInvoker = CommandInvoker.of(source)

            if (abstractCommand.onlyPlayer && commandInvoker.isConsole()) {
                "only_player".toCommandMessage().send(commandInvoker)
                return
            }

            if (abstractCommand.needOp && !source.hasPermissionLevel(3)) {
                "no_permission".toCommandMessage().send(commandInvoker)
                return
            }

            success()
            return
        }

        fun argumentCheck(
            commandContext: CommandContext<ServerCommandSource>,
            argumentCheckers: () -> Boolean,
            success: () -> Unit
        ) {
            if (argumentCheckers()) {
                success()
                return
            }
            val commandInvoker = CommandInvoker.of(commandContext.source)
            "argument_error".toCommandMessage().send(commandInvoker)
        }
    }
}