package io.github.gdrfgdrf.cutetrade.operation.server

import io.github.gdrfgdrf.cutetrade.common.Operators
import io.github.gdrfgdrf.cutetrade.extension.*
import io.github.gdrfgdrf.cutetrade.network.PacketContext
import io.github.gdrfgdrf.cutetrade.operation.base.Operator
import net.minecraft.server.network.ServerPlayerEntity

object ClientInitializedOperator : Operator {
    override fun run(context: PacketContext<*>, args: Array<*>?) {
        val sender = context.sender as ServerPlayerEntity

        val currentTrade = sender.currentTrade()
        if (currentTrade == null) {
            sender.translationScope {
                toCommandTranslation("no_transaction_in_progress")
                    .send()
            }
            return
        }

        if (sender.isRed()) {
            currentTrade.redPlayerInitialized()
        } else {
            currentTrade.bluePlayerInitialized()
        }
    }

    override fun getName(): String = Operators.SERVER_CLIENT_INITIALIZED
}