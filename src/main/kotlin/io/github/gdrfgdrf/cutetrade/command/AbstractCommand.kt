/*
 * Copyright 2024 CuteTrade's contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.gdrfgdrf.cutetrade.command

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.github.gdrfgdrf.cutetrade.common.extension.translationScope
import io.github.gdrfgdrf.cutetrade.extension.findPlayerProxy
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

//            if (abstractCommand.onlyPlayer && commandInvoker.isConsole()) {
//                "only_player".toCommandTranslation().send(commandInvoker)
//                return
//            }

            source.findPlayerProxy()?.let {
                if (abstractCommand.needOp && !source.hasPermissionLevel(3)) {
                    it.translationScope {
                        toCommandTranslation("no_permission")
                            .send()
                    }
                    return
                }
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
            commandContext.source.findPlayerProxy()?.let {
                it.translationScope {
                    toCommandTranslation("argument_error")
                        .send()
                }
            }
        }
    }
}