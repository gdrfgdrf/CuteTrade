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
        if (providedRedName == source.player?.name?.string) {
            "decline_request_from_oneself".toCommandMessage()
                .send(commandInvoker)
            return
        }

        val redPlayer = PlayerManager.findPlayer(providedRedName)
        if (redPlayer == null) {
            "not_found_player".toCommandMessage()
                .format(providedRedName)
                .send(commandInvoker)
            return
        }

        val redPlayerEntity = redPlayer.findServerEntity(source.server)
        if (redPlayerEntity == null) {
            "player_offline".toCommandMessage()
                .format(providedRedName)
                .send(commandInvoker)
            return
        }

        val tradeRequest = source.player?.getTradeRequest(redPlayerEntity)
        if (tradeRequest == null) {
            "not_found_request".toCommandMessage()
                .format(providedRedName)
                .send(commandInvoker)
            return
        }

        tradeRequest.decline()
    }
}