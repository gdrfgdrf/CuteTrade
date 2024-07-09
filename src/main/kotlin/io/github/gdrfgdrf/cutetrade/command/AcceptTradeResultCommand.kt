package io.github.gdrfgdrf.cutetrade.command

import com.mojang.brigadier.arguments.StringArgumentType
import io.github.gdrfgdrf.cutetrade.command.AcceptTradeResultCommand.accept
import io.github.gdrfgdrf.cutetrade.command.suggest.TradeRequestBindSuggestProvider
import io.github.gdrfgdrf.cutetrade.extension.*
import io.github.gdrfgdrf.cutetrade.manager.PlayerManager
import io.github.gdrfgdrf.cutetrade.utils.command.CommandInvoker
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
        val commandInvoker = CommandInvoker.of(source)
        if (providedRedName == source.player?.name?.string) {
            "accept_request_from_oneself".toCommandMessage()
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

        if (redPlayerEntity.isTrading()) {
            "player_is_trading".toCommandMessage()
                .format(providedRedName)
                .send(commandInvoker)
            return
        }

        tradeRequest.accept()
    }
}