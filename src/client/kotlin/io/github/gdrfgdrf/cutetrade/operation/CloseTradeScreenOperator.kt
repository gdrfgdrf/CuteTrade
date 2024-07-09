package io.github.gdrfgdrf.cutetrade.operation

import io.github.gdrfgdrf.cutetrade.common.Operators
import io.github.gdrfgdrf.cutetrade.manager.ClientTradeManager
import io.github.gdrfgdrf.cutetrade.network.PacketContext
import io.github.gdrfgdrf.cutetrade.operation.base.Operator

object CloseTradeScreenOperator : Operator {
    override fun run(context: PacketContext<*>, args: Array<*>?) {
        ClientTradeManager.currentTrade?.clientTradeScreenContext?.closeTradeScreen()
    }

    override fun getName(): String = Operators.CLIENT_CLOSE_TRADE_SCREEN
}