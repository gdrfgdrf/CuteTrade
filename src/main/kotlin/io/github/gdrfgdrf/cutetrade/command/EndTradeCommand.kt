package io.github.gdrfgdrf.cutetrade.command

import io.github.gdrfgdrf.cutetrade.command.EndTradeCommand.start
import io.github.gdrfgdrf.cutetrade.extension.currentTrade
import io.github.gdrfgdrf.cutetrade.extension.send
import io.github.gdrfgdrf.cutetrade.extension.toCommandMessage
import io.github.gdrfgdrf.cutetrade.utils.command.CommandInvoker
import net.minecraft.server.command.ServerCommandSource

object EndTradeCommand : AbstractCommand(
    command = "end-trade",
    onlyPlayer = true,
    noArgument = true,
    tree = { literalArgumentBuilder ->
        literalArgumentBuilder.executes { context ->
            playerCheck(EndTradeCommand, context) {
                start(context.source)
            }
            0
        }
    }
) {
    private fun start(source: ServerCommandSource) {
        val commandInvoker = CommandInvoker.of(source)

        val currentTrade = source.player?.currentTrade()
        if (currentTrade == null) {
            "no_transaction_in_progress".toCommandMessage()
                .send(commandInvoker)
            return
        }

        currentTrade.terminate()
    }

}