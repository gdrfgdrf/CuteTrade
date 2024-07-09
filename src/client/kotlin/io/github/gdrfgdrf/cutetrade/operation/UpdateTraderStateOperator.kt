package io.github.gdrfgdrf.cutetrade.operation

import io.github.gdrfgdrf.cutetrade.common.Operators
import io.github.gdrfgdrf.cutetrade.common.TraderState
import io.github.gdrfgdrf.cutetrade.manager.ClientTradeManager
import io.github.gdrfgdrf.cutetrade.network.PacketContext
import io.github.gdrfgdrf.cutetrade.operation.base.Operator

object UpdateTraderStateOperator : Operator {
    override fun run(context: PacketContext<*>, args: Array<*>?) {
        val redState = TraderState.valueOf(args!![0] as String)
        val blueState = TraderState.valueOf(args[1] as String)
        ClientTradeManager.currentTrade?.updateTraderStateFromServer(redState, blueState)
    }

    override fun getName(): String = Operators.CLIENT_UPDATE_TRADER_STATE
}