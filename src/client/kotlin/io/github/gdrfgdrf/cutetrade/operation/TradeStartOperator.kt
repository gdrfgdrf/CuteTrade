package io.github.gdrfgdrf.cutetrade.operation

import io.github.gdrfgdrf.cutetrade.common.Operators
import io.github.gdrfgdrf.cutetrade.network.PacketContext
import io.github.gdrfgdrf.cutetrade.operation.base.Operator

object TradeStartOperator : Operator {
    override fun run(context: PacketContext<*>, args: Array<*>?) {

    }

    override fun getName(): String = Operators.CLIENT_TRADE_START
}