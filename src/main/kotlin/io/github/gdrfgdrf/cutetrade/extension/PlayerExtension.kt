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

package io.github.gdrfgdrf.cutetrade.extension

import cutetrade.protobuf.CommonProto.Player
import io.github.gdrfgdrf.cutetrade.common.TradeRequest
import io.github.gdrfgdrf.cutetrade.manager.PlayerManager
import io.github.gdrfgdrf.cutetrade.manager.TradeManager
import io.github.gdrfgdrf.cutetrade.manager.TradeRequestManager
import io.github.gdrfgdrf.cutetrade.trade.TradeContext
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity

fun Player.findServerEntity(server: MinecraftServer): ServerPlayerEntity? {
    return server.playerManager.getPlayer(this.name)
}

fun ServerPlayerEntity.findProtobufPlayer(): Player? {
    return PlayerManager.findPlayer(this.name.string)
}

fun ServerPlayerEntity.getTradeRequest(redPlayerEntity: ServerPlayerEntity): TradeRequest? {
    val requests = TradeRequestManager.getRequests(redPlayerEntity)
    return requests?.stream()
        ?.filter { tradeRequest ->
            tradeRequest.bluePlayerEntity == this@getTradeRequest
        }
        ?.findAny()
        ?.orElse(null)
}

fun ServerPlayerEntity.removeTradeRequest(redPlayerEntity: ServerPlayerEntity) {
    val tradeRequest = getTradeRequest(redPlayerEntity)
    tradeRequest?.let {
        TradeRequestManager.removeRequest(redPlayerEntity, tradeRequest)
    }
}

fun ServerPlayerEntity.isTrading(): Boolean {
    return TradeManager.trades.contains(this)
}

fun ServerPlayerEntity.currentTrade(): TradeContext? {
    return TradeManager.trades[this]
}

fun ServerPlayerEntity.checkInTrade(): Boolean {
    val currentTrade = currentTrade()
    if (currentTrade == null) {
        this.translationScope {
            toCommandTranslation("no_transaction_in_progress")
                .send()
        }
        return false
    }
    return true
}

fun ServerPlayerEntity.isRed(): Boolean {
    val currentTrade = currentTrade()
    return currentTrade?.redPlayer?.name?.string == this.name.string
}