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

package io.github.gdrfgdrf.cutetrade.common

import io.github.gdrfgdrf.cutetrade.extension.*
import io.github.gdrfgdrf.cutetrade.manager.TradeManager
import io.github.gdrfgdrf.cutetrade.worker.CountdownWorker
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import java.util.concurrent.TimeUnit

class TradeRequest private constructor(
    private val redPlayerEntity: ServerPlayerEntity,
    val bluePlayerEntity: ServerPlayerEntity
) {
    private val task = CountdownWorker.CountdownTask(30000L, TimeUnit.MILLISECONDS) {
        "request_timeout_for_red".toCommandMessage()
            .format(bluePlayerEntity.name.string)
            .send(redPlayerEntity)
        "request_timeout_for_blue".toCommandMessage()
            .format(redPlayerEntity.name.string)
            .send(bluePlayerEntity)

        timeout()
    }

    fun send() {
        val acceptMessage = buildRunCommandText(
            {
                "click_to_accept".toCommandMessage()
            },
            "/trade-public accept ${redPlayerEntity.name.string}"
        )
        val declineMessage = buildRunCommandText(
            {
                "click_to_decline".toCommandMessage()
            },
            "/trade-public decline ${redPlayerEntity.name.string}"
        )

        "request_received_for_blue".toCommandMessage()
            .format(redPlayerEntity.name.string)
            .toText()
            .fillPrefix()
            .apply {
                (this as MutableText)
                    .append(" ")
                    .append(acceptMessage)
                    .append(" ")
                    .append(declineMessage)
            }
            .send(bluePlayerEntity)

        "request_sent_for_red".toCommandMessage()
            .format(bluePlayerEntity.name.string)
            .send(redPlayerEntity)
        CountdownWorker.add(task)
    }

    fun accept() {
        end()

        "accept_request_for_red".toCommandMessage()
            .format(bluePlayerEntity.name.string)
            .send(redPlayerEntity)
        "accept_request_for_blue".toCommandMessage()
            .format(redPlayerEntity.name.string)
            .send(bluePlayerEntity)

        TradeManager.createTrade(redPlayerEntity, bluePlayerEntity)
    }

    fun decline() {
        end()

        "decline_request_for_red".toCommandMessage()
            .format(bluePlayerEntity.name.string)
            .send(redPlayerEntity)
        "decline_request_for_blue".toCommandMessage()
            .format(redPlayerEntity.name.string)
            .send(bluePlayerEntity)
    }

    fun timeout() {
        end()
    }

    fun end() {
        task.end = true
        bluePlayerEntity.removeTradeRequest(redPlayerEntity)
    }

    companion object {
        fun create(redPlayerEntity: ServerPlayerEntity, bluePlayerEntity: ServerPlayerEntity) =
            TradeRequest(redPlayerEntity, bluePlayerEntity)
    }

}