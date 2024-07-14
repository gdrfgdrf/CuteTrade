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

package io.github.gdrfgdrf.cutetrade.manager

import cutetrade.protobuf.CommonProto.Trade
import cutetrade.protobuf.CommonProto.TradeResult
import cutetrade.protobuf.StorableProto.TradeStore
import io.github.gdrfgdrf.cutetrade.common.TradeStatus
import io.github.gdrfgdrf.cutetrade.extension.findProtobufPlayer
import io.github.gdrfgdrf.cutetrade.extension.runSyncTask
import io.github.gdrfgdrf.cutetrade.extension.toProtobufTradeItem
import io.github.gdrfgdrf.cutetrade.extension.toTimestamp
import io.github.gdrfgdrf.cutetrade.trade.TradeContext
import io.github.gdrfgdrf.cutetrade.utils.Protobuf
import net.minecraft.server.network.ServerPlayerEntity
import java.util.concurrent.ConcurrentHashMap

object TradeManager {
    var tradeProtobuf: Protobuf<TradeStore>? = null
    var trades = ConcurrentHashMap<ServerPlayerEntity, TradeContext>()

    fun createTrade(
        redPlayerEntity: ServerPlayerEntity,
        bluePlayerEntity: ServerPlayerEntity
    ) {
        val tradeContext = TradeContext.create(redPlayerEntity, bluePlayerEntity)
        tradeContext.initialize()
        tradeContext.start()
    }

    fun tradeStart(
        tradeContext: TradeContext
    ) {
        val redPlayer = tradeContext.redPlayer
        val bluePlayer = tradeContext.bluePlayer
        trades[redPlayer] = tradeContext
        trades[bluePlayer] = tradeContext
    }

    fun tradeEnd(
        tradeContext: TradeContext
    ) {
        val redPlayer = tradeContext.redPlayer
        val bluePlayer = tradeContext.bluePlayer
        trades.remove(redPlayer)
        trades.remove(bluePlayer)
    }

    fun recordTrade(
        tradeContext: TradeContext
    ) = runSyncTask(tradeProtobuf!!) {
        val builder = Trade.newBuilder()
            .setRedName(tradeContext.redPlayer.name.string)
            .setBlueName(tradeContext.bluePlayer.name.string)
            .setId(tradeContext.tradeId)

        if (tradeContext.startTime != null) {
            builder.setStartTime(tradeContext.startTime!!.toTimestamp())
        }
        if (tradeContext.endTime != null) {
            builder.setEndTime(tradeContext.endTime!!.toTimestamp())
        }
        if (tradeContext.status != TradeStatus.FINISHED) {
            builder.setTradeResult(TradeResult.TRADE_RESULT_TERMINATED)
        } else {
            builder.setTradeResult(TradeResult.TRADE_RESULT_FINISHED)
        }

        if (tradeContext.status == TradeStatus.FINISHED) {
            val redAddBy = tradeContext.redTradeItemStack.playerEntity
            val blueAddBy = tradeContext.blueTradeItemStack.playerEntity

            tradeContext.redTradeItemStack.itemArray.forEach { tradeItem ->
                tradeItem?.let {
                    if (!it.itemStack.isEmpty) {
                        val protobufTradeItem = it.itemStack.toProtobufTradeItem(redAddBy.name.string)
                        builder.addRedItemResult(protobufTradeItem)
                    }
                }
            }
            tradeContext.blueTradeItemStack.itemArray.forEach { tradeItem ->
                tradeItem?.let {
                    if (!it.itemStack.isEmpty) {
                        val protobufTradeItem = it.itemStack.toProtobufTradeItem(blueAddBy.name.string)
                        builder.addBlueItemResult(protobufTradeItem)
                    }
                }
            }
        }

        val redProtobufPlayer = tradeContext.redPlayer.findProtobufPlayer()
        val blueProtobufPlayer = tradeContext.bluePlayer.findProtobufPlayer()
        if (redProtobufPlayer != null) {
            PlayerManager.recordTrade(redProtobufPlayer, tradeContext.tradeId)
        }
        if (blueProtobufPlayer != null) {
            PlayerManager.recordTrade(blueProtobufPlayer, tradeContext.tradeId)
        }

        tradeProtobuf!!.rebuild { tradeStore ->
            val tradeStoreBuilder = tradeStore!!.toBuilder()
                .putTradeIdToTrade(tradeContext.tradeId, builder.build())
            if (redProtobufPlayer != null) {
                tradeStoreBuilder.putTradeIdToPlayerName(
                    tradeContext.tradeId,
                    redProtobufPlayer.name
                )
            }
            if (blueProtobufPlayer != null) {
                tradeStoreBuilder.putTradeIdToPlayerName(
                    tradeContext.tradeId,
                    blueProtobufPlayer.name
                )
            }
            tradeStoreBuilder.build()
        }
        tradeProtobuf!!.save()
    }
}