package io.github.gdrfgdrf.cutetrade.operation

import io.github.gdrfgdrf.cutetrade.common.Operators
import io.github.gdrfgdrf.cutetrade.network.PacketContext
import io.github.gdrfgdrf.cutetrade.operation.base.Operator

object DevOperator : Operator {
    override fun run(context: PacketContext<*>, args: Array<*>?) {
//        val tradeScreenHandler =
//            TradeScreenHandler(MinecraftClient.getInstance().player?.inventory!!)
//        MinecraftClient.getInstance().setScreen(DevScreen(
//            tradeScreenHandler,
////            GenericContainerScreenHandler.createGeneric9x2(0, MinecraftClient.getInstance().player?.inventory),
//            MinecraftClient.getInstance().player?.inventory
//        ))
    }

    override fun getName(): String = Operators.CLIENT_DEV
}