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
            HistoryCommand.print(it.source, it.source.player!!.name.string)
            0
        }
    }
) {

     fun print(source: ServerCommandSource, playerName: String) {
        val commandInvoker = CommandInvoker.of(source)

        val protobufPlayer = playerName.findProtobufPlayer()
        if (protobufPlayer == null) {
            "not_found_player".toCommandMessage()
                .format(playerName)
                .send(commandInvoker)
            return
        }
        val tradeIdsList = protobufPlayer.tradeIdsList
        if (tradeIdsList == null || (tradeIdsList as List<String>).isEmpty()) {
            if (protobufPlayer.name != source.player!!.name.string) {
                "no_transaction_history_other".toCommandMessage()
                    .format(playerName)
                    .send(commandInvoker)
                return
            }

            "no_transaction_history".toCommandMessage()
                .send(commandInvoker)
            return
        }

        val pageable = Pageable()
        pageable.openScreen("history_title".toScreenMessage()
            .format(playerName)
            .toFriendlyText().toText(""), source.player!!)

        val tradeMap = TradeManager.tradeProtobuf?.message?.tradeIdToTradeMap
        tradeIdsList.forEach { tradeId ->
            val trade = tradeMap?.get(tradeId) ?: return@forEach
            val itemStack = trade.toItemStack()

            pageable.addItemStack(itemStack)
        }
        pageable.inventory!!.fullNavigationBar()

        pageable.inventory!!.navigator?.show(0)

        pageable.pageableScreenHandler?.onItemClick = onItemClick@ { index ->
            if (index >= tradeIdsList.size || index < 0) {
                return@onItemClick
            }
            val tradeId = tradeIdsList[index]
            val trade = tradeMap!![tradeId] ?: return@onItemClick

            trade.printInformation(source.player!!)
        }


    }

}