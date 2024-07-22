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

import io.github.gdrfgdrf.cutetrade.extension.*
import io.github.gdrfgdrf.cutetrade.manager.TradeManager
import io.github.gdrfgdrf.cutetrade.page.Pageable
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
            "not_found_player".toCommandTranslation(commandInvoker)
                .format0(playerName)
                .send(commandInvoker)
            return
        }
        val tradeIdsList = protobufPlayer.tradeIdsList
        if (tradeIdsList == null || (tradeIdsList as List<String>).isEmpty()) {
            if (protobufPlayer.name != source.player!!.name.string) {
                "no_transaction_history_other".toCommandTranslation(commandInvoker)
                    .format0(playerName)
                    .send(commandInvoker)
                return
            }

            "no_transaction_history".toCommandTranslation(commandInvoker)
                .send(commandInvoker)
            return
        }

        val pageable = Pageable()
        pageable.openScreen("history_title".toScreenTranslation(commandInvoker)
            .format0(playerName)
            .build(), source.player!!)

        val tradeMap = TradeManager.tradeProtobuf?.message?.tradeIdToTradeMap
        tradeIdsList.forEach { tradeId ->
            val trade = tradeMap?.get(tradeId) ?: return@forEach
            val itemStack = trade.toItemStack(commandInvoker.source.player!!)

            pageable.addItemStack(itemStack)
        }
        pageable.inventory!!.fullNavigationBar(source.player!!)

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