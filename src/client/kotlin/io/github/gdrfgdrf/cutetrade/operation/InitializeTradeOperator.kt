package io.github.gdrfgdrf.cutetrade.operation

import io.github.gdrfgdrf.cutetrade.common.Operators
import io.github.gdrfgdrf.cutetrade.manager.ClientTradeManager
import io.github.gdrfgdrf.cutetrade.network.PacketContext
import io.github.gdrfgdrf.cutetrade.operation.base.Operator

object InitializeTradeOperator : Operator {
    override fun run(context: PacketContext<*>, args: Array<*>?) {
        ClientTradeManager.initializeTrade(
            args!![0] as String,
            args[1] as String,
            args[2] as String
        )
    }

    override fun getName(): String = Operators.CLIENT_INITIALIZE_TRADE
}