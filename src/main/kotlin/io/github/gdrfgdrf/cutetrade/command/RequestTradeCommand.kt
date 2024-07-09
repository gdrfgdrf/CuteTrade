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
        if (providedBlueName == source.player?.name?.string) {
            "trade_with_oneself".toCommandMessage()
                .send(commandInvoker)
            return
        }

        val bluePlayer = PlayerManager.findPlayer(providedBlueName)
        if (bluePlayer == null) {
            "not_found_player".toCommandMessage()
                .format(providedBlueName)
                .send(commandInvoker)
            return
        }

        val bluePlayerEntity = bluePlayer.findServerEntity(source.server)
        if (bluePlayerEntity == null) {
            "player_offline".toCommandMessage()
                .format(providedBlueName)
                .send(commandInvoker)
            return
        }

        if (bluePlayerEntity.isTrading()) {
            "player_is_trading_oneself".toCommandMessage()
                .format(providedBlueName)
                .send(commandInvoker)
            return
        }

        TradeRequestManager.request(source.player!!, bluePlayerEntity)
    }
}