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
import io.github.gdrfgdrf.cutetrade.command.RequestTradeCommand.start
import io.github.gdrfgdrf.cutetrade.command.suggest.NotTradePlayersSuggestProvider
import io.github.gdrfgdrf.cutetrade.extension.*
import io.github.gdrfgdrf.cutetrade.manager.PlayerManager
import io.github.gdrfgdrf.cutetrade.manager.TradeRequestManager
import io.github.gdrfgdrf.cutetrade.utils.command.CommandInvoker
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object RequestTradeCommand : AbstractCommand(
    command = "request",
    onlyPlayer = true,
    tree = {
        CommandManager.argument("player-name", StringArgumentType.string())
            .suggests(NotTradePlayersSuggestProvider)
            .executes { context ->
                playerCheck(RequestTradeCommand, context) {
                    argumentCheck(
                        context,
                        argumentCheckers = {
                            notBlank("player-name".getContent(context))
                        },
                        success = {
                            start(context.source, "player-name".getContent(context))
                        }
                    )
                }
                0
            }
    }
) {
    private fun start(source: ServerCommandSource, providedBlueName: String) {
        val commandInvoker = CommandInvoker.of(source)
        commandInvoker.translationScope {
            runCatching {
                if (providedBlueName == source.player?.name?.string) {
                    toCommandTranslation("trade_with_oneself")
                        .send()
                    return@translationScope
                }

                val bluePlayer = PlayerManager.findPlayer(providedBlueName)
                if (bluePlayer == null) {
                    toCommandTranslation("not_found_player")
                        .format0(providedBlueName)
                        .send()
                    return@translationScope
                }

                val bluePlayerEntity = bluePlayer.findServerEntity(source.server)
                if (bluePlayerEntity == null) {
                    toCommandTranslation("player_offline")
                        .format0(providedBlueName)
                        .send()
                    return@translationScope
                }

                if (bluePlayerEntity.isTrading()) {
                    toCommandTranslation("player_is_trading_oneself")
                        .format0(providedBlueName)
                        .send()
                    return@translationScope
                }

                TradeRequestManager.request(source.player!!, bluePlayerEntity)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}