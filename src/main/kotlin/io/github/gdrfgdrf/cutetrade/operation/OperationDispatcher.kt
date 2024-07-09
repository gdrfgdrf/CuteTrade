package io.github.gdrfgdrf.cutetrade.operation

import io.github.gdrfgdrf.cutetrade.network.PacketContext
import io.github.gdrfgdrf.cutetrade.operation.base.Operator
import java.util.concurrent.ConcurrentHashMap

object OperationDispatcher {
    private val map: ConcurrentHashMap<String, Operator> = ConcurrentHashMap()

    fun add(aOperator: Operator) {
        map[aOperator.getName()] = aOperator
    }

    fun dispatch(name: String, context: PacketContext<*>, args: Array<*>?) {
        val aOperator = map[name] ?: return
        aOperator.run(context, args)
    }

}