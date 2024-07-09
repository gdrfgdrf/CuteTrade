package io.github.gdrfgdrf.cutetrade.common

import io.github.gdrfgdrf.cutetrade.extension.removeTradeRequest
import io.github.gdrfgdrf.cutetrade.extension.send
import io.github.gdrfgdrf.cutetrade.extension.toCommandMessage
import io.github.gdrfgdrf.cutetrade.manager.TradeManager
import io.github.gdrfgdrf.cutetrade.worker.CountdownWorker
import net.minecraft.server.network.ServerPlayerEntity
import java.util.concurrent.TimeUnit

class TradeRequest private constructor(
    val redPlayerEntity: ServerPlayerEntity,
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
        "request_received_for_blue".toCommandMessage()
            .format(redPlayerEntity.name.string)
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