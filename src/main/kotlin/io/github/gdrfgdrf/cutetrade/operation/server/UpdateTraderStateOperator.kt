package io.github.gdrfgdrf.cutetrade.operation.server

import io.github.gdrfgdrf.cutetrade.common.Operators
import io.github.gdrfgdrf.cutetrade.common.TraderState
import io.github.gdrfgdrf.cutetrade.extension.*
import io.github.gdrfgdrf.cutetrade.network.PacketContext
import io.github.gdrfgdrf.cutetrade.operation.base.Operator
import net.minecraft.server.network.ServerPlayerEntity

object UpdateTraderStateOperator : Operator {
    override fun run(context: PacketContext<*>, args: Array<*>?) {
        val traderState = TraderState.valueOf(args?.get(0) as String)

        val sender = context.sender as ServerPlayerEntity
        val currentTrade = sender.currentTrade()
        if (!sender.checkInTrade()) {
            return
        }

        if (sender.isRed()) {
            currentTrade!!.updateRedState(traderState)
        } else {
            currentTrade!!.updateBlueState(traderState)
        }
    }

    override fun getName(): String = Operators.SERVER_UPDATE_TRADER_STATE
}