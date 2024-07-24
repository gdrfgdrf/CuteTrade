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
import io.github.gdrfgdrf.cutetrade.command.DeclineTradeResultCommand.decline
import io.github.gdrfgdrf.cutetrade.command.suggest.TradeRequestBindSuggestProvider
import io.github.gdrfgdrf.cutetrade.extension.*
import io.github.gdrfgdrf.cutetrade.manager.PlayerManager
import io.github.gdrfgdrf.cutetrade.utils.command.CommandInvoker
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object DeclineTradeResultCommand : AbstractCommand(
  command = "decline",
    onlyPlayer = true,
    tree = {
        CommandManager.argument("player-name", StringArgumentType.string())
            .suggests(TradeRequestBindSuggestProvider)
            .executes { context ->
                playerCheck(DeclineTradeResultCommand, context) {
                    argumentCheck(
                        context,
                        argumentCheckers = {
                            notBlank("player-name".getContent(context))
                        },
                        success = {
                            decline(context.source, "player-name".getContent(context))
                        }
                    )
                }
                0
            }
    }
) {
    private fun decline(source: ServerCommandSource, providedRedName: String) {
        val commandInvoker = CommandInvoker.of(source)
        commandInvoker.translationScope {
            if (providedRedName == source.player?.name?.string) {
                toCommandTranslation("decline_request_from_oneself")
                    .send()
                return@translationScope
            }

            val redPlayer = PlayerManager.findPlayer(providedRedName)
            if (redPlayer == null) {
                toCommandTranslation("not_found_player")
                    .format0(providedRedName)
                    .send()
                return@translationScope
            }

            val redPlayerEntity = redPlayer.findServerEntity(source.server)
            if (redPlayerEntity == null) {
                toCommandTranslation("player_offline")
                    .format0(providedRedName)
                    .send()
                return@translationScope
            }

            val tradeRequest = source.player?.getTradeRequest(redPlayerEntity)
            if (tradeRequest == null) {
                toCommandTranslation("not_found_request")
                    .format0()
                    .send()
                return@translationScope
            }

            tradeRequest.decline()
        }
    }
}