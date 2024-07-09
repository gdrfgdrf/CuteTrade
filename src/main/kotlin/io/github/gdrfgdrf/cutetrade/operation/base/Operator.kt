package io.github.gdrfgdrf.cutetrade.operation.base

import io.github.gdrfgdrf.cutetrade.network.PacketContext

interface Operator {
    fun run(context: PacketContext<*>, args: Array<*>?)
    fun getName(): String
}