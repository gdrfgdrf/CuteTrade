package io.github.gdrfgdrf.cutetrade.operation

import io.github.gdrfgdrf.cutetrade.common.Operators
import io.github.gdrfgdrf.cutetrade.manager.ClientTradeManager
import io.github.gdrfgdrf.cutetrade.network.PacketContext
import io.github.gdrfgdrf.cutetrade.operation.base.Operator

object OpenTradeScreenOperator : Operator {
    override fun run(context: PacketContext<*>, args: Array<*>?) {
        ClientTradeManager.currentTrade?.clientTradeScreenContext?.openTradeScreen()
    }


    override fun getName(): String = Operators.CLIENT_OPEN_TRADE_SCREEN
}