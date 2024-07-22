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

import io.github.gdrfgdrf.cutetrade.common.TradeRequest
import io.github.gdrfgdrf.cutetrade.extension.*
import net.minecraft.server.network.ServerPlayerEntity
import java.util.concurrent.ConcurrentHashMap

object TradeRequestManager {
    private val tradeRequests = ConcurrentHashMap<ServerPlayerEntity, ArrayList<TradeRequest>>()
    private val bluePlayerToRedPlayer = ConcurrentHashMap<ServerPlayerEntity, ArrayList<ServerPlayerEntity>>()

    fun request(
        redPlayerEntity: ServerPlayerEntity,
        bluePlayerEntity: ServerPlayerEntity
    ) {
        val redSentToBlue = redPlayerEntity.getTradeRequest(bluePlayerEntity)
        val blueSentToRed = bluePlayerEntity.getTradeRequest(redPlayerEntity)

        if (redSentToBlue != null || blueSentToRed != null) {
            "request_exist".toCommandTranslation(redPlayerEntity)
                .format0(bluePlayerEntity.name.string)
                .sendTo(redPlayerEntity)
            return
        }

        val tradeRequest = TradeRequest.create(redPlayerEntity, bluePlayerEntity)
        addRequest(redPlayerEntity, tradeRequest)

        tradeRequest.send()
    }

    fun getRedPlayersByBluePlayer(
        bluePlayerEntity: ServerPlayerEntity
    ): List<ServerPlayerEntity>? {
        return bluePlayerToRedPlayer[bluePlayerEntity]
    }

    fun getRequests(
        redPlayerEntity: ServerPlayerEntity,
    ): List<TradeRequest>? {
        return tradeRequests[redPlayerEntity]
    }

    fun addRequest(
        redPlayerEntity: ServerPlayerEntity,
        tradeRequest: TradeRequest
    ) {
        val redList = tradeRequests.computeIfAbsent(redPlayerEntity) { _ ->
            ArrayList()
        }
        redList.add(tradeRequest)

        val computeIfAbsent = bluePlayerToRedPlayer.computeIfAbsent(tradeRequest.bluePlayerEntity) { _ ->
            ArrayList()
        }
        computeIfAbsent.add(redPlayerEntity)
    }

    fun removeRequest(
        redPlayerEntity: ServerPlayerEntity,
        tradeRequest: TradeRequest
    ) {
        val redList = tradeRequests.computeIfAbsent(redPlayerEntity) { _ ->
            ArrayList()
        }
        redList.remove(tradeRequest)

        val computeIfAbsent = bluePlayerToRedPlayer.computeIfAbsent(tradeRequest.bluePlayerEntity) { _ ->
            ArrayList()
        }
        computeIfAbsent.remove(redPlayerEntity)
    }
}