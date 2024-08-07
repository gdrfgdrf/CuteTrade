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

import com.mojang.brigadier.arguments.StringArgumentType
import io.github.gdrfgdrf.cutetrade.command.AcceptTradeResultCommand.accept
import io.github.gdrfgdrf.cutetrade.command.suggest.TradeRequestBindSuggestProvider
import io.github.gdrfgdrf.cutetrade.common.command.AcceptTradeResultCommandExecutor
import io.github.gdrfgdrf.cutetrade.extension.*
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object AcceptTradeResultCommand : AbstractCommand(
    command = "accept",
    onlyPlayer = true,
    tree = {
        CommandManager.argument("player-name", StringArgumentType.string())
            .suggests(TradeRequestBindSuggestProvider)
            .executes { context ->
                playerCheck(AcceptTradeResultCommand, context) {
                    argumentCheck(
                        context,
                        argumentCheckers = {
                            notBlank("player-name".getContent(context))
                        },
                        success = {
                            accept(context.source, "player-name".getContent(context))
                        }
                    )
                }
                0
            }
    }
) {
    private fun accept(source: ServerCommandSource, providedRedName: String) {
        source.findPlayerProxy()?.let {
            AcceptTradeResultCommandExecutor.execute(it, providedRedName)
        }
    }
}