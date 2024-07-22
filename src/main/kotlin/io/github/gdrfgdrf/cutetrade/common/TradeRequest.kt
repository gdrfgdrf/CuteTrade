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
        "request_timeout_for_red".toCommandTranslation(redPlayerEntity)
            .format0(bluePlayerEntity.name.string)
            .sendTo(redPlayerEntity)
        "request_timeout_for_blue".toCommandTranslation(bluePlayerEntity)
            .format0(redPlayerEntity.name.string)
            .sendTo(bluePlayerEntity)

        timeout()
    }

    fun send() {
        val acceptMessage = "click_to_accept".toCommandText(bluePlayerEntity)
            .runCommand("/trade-public accept ${redPlayerEntity.name.string}")
        val declineMessage = "click_to_decline".toCommandText(bluePlayerEntity)
            .runCommand("/trade-public decline ${redPlayerEntity.name.string}")

        "request_received_for_blue".toCommandTranslation(bluePlayerEntity)
            .format0(redPlayerEntity.name.string)
            .append(acceptMessage)
            .append(declineMessage)
            .sendTo(bluePlayerEntity)

        "request_sent_for_red".toCommandTranslation(redPlayerEntity)
            .format0(bluePlayerEntity.name.string)
            .sendTo(redPlayerEntity)
        CountdownWorker.add(task)
    }

    fun accept() {
        end()

        "accept_request_for_red".toCommandTranslation(redPlayerEntity)
            .format0(bluePlayerEntity.name.string)
            .sendTo(redPlayerEntity)
        "accept_request_for_blue".toCommandTranslation(bluePlayerEntity)
            .format0(redPlayerEntity.name.string)
            .sendTo(bluePlayerEntity)

        TradeManager.createTrade(redPlayerEntity, bluePlayerEntity)
    }

    fun decline() {
        end()

        "decline_request_for_red".toCommandTranslation(redPlayerEntity)
            .format0(bluePlayerEntity.name.string)
            .sendTo(redPlayerEntity)
        "decline_request_for_blue".toCommandTranslation(bluePlayerEntity)
            .format0(redPlayerEntity.name.string)
            .sendTo(bluePlayerEntity)
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