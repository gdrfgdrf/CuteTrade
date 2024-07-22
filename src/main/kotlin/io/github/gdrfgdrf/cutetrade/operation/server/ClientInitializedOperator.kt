package io.github.gdrfgdrf.cutetrade.operation.server

import io.github.gdrfgdrf.cutetrade.common.Operators
import io.github.gdrfgdrf.cutetrade.extension.currentTrade
import io.github.gdrfgdrf.cutetrade.extension.isRed
import io.github.gdrfgdrf.cutetrade.extension.sendTo
import io.github.gdrfgdrf.cutetrade.extension.toCommandTranslation
import io.github.gdrfgdrf.cutetrade.network.PacketContext
import io.github.gdrfgdrf.cutetrade.operation.base.Operator
import net.minecraft.server.network.ServerPlayerEntity

object ClientInitializedOperator : Operator {
    override fun run(context: PacketContext<*>, args: Array<*>?) {
        val sender = context.sender as ServerPlayerEntity

        val currentTrade = sender.currentTrade()
        if (currentTrade == null) {
            "no_transaction_in_progress".toCommandTranslation(sender)
                .sendTo(sender)
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