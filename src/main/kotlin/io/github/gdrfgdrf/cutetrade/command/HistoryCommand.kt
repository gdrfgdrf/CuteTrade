package io.github.gdrfgdrf.cutetrade.command

import io.github.gdrfgdrf.cutetrade.extension.*
import io.github.gdrfgdrf.cutetrade.manager.TradeManager
import io.github.gdrfgdrf.cutetrade.page.Pageable
import io.github.gdrfgdrf.cutetrade.page.PageableScreenHandler
import io.github.gdrfgdrf.cutetrade.page.PageableScreenHandlerFactory
import io.github.gdrfgdrf.cutetrade.utils.command.CommandInvoker
import net.minecraft.server.command.ServerCommandSource

object HistoryCommand : AbstractCommand(
    command = "history",
    onlyPlayer = true,
    noArgument = true,
    tree = { literalArgumentBuilder ->
        literalArgumentBuilder.executes {
            HistoryCommand.print(it.source)
            0
        }
    }
) {

    private fun print(source: ServerCommandSource) {
        val commandInvoker = CommandInvoker.of(source)

        val protobufPlayer = source.player?.findProtobufPlayer()
        if (protobufPlayer == null) {
            "not_found_player".toCommandMessage()
                .format(source.player?.name?.string)
                .send(commandInvoker)
            return
        }
        val tradeIdsList = protobufPlayer.tradeIdsList
        if (tradeIdsList == null || (tradeIdsList as List<String>).isEmpty()) {
            "no_transaction_history".toCommandMessage()
                .send(commandInvoker)
            return
        }

        val pageable = Pageable()
        pageable.openScreen("history_title".toScreenMessage()
            .format(source.player!!.name.string)
            .toFriendlyText().toText(""), source.player!!)

        val tradeMap = TradeManager.tradeProtobuf?.message?.tradeIdToTradeMap
        tradeIdsList.forEach { tradeId ->
            val trade = tradeMap?.get(tradeId) ?: return@forEach
            val itemStack = trade.toItemStack()

            pageable.addItemStack(itemStack)
        }
        pageable.inventory!!.fullNavigationBar()

        pageable.inventory!!.navigator?.show(0)

    }

}