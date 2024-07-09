package io.github.gdrfgdrf.cutetrade.manager

import cutetrade.protobuf.StorableProto.TradeStore
import io.github.gdrfgdrf.cutetrade.trade.TradeContext
import io.github.gdrfgdrf.cutetrade.utils.Protobuf
import net.minecraft.server.network.ServerPlayerEntity
import java.util.concurrent.ConcurrentHashMap

object TradeManager {
    var tradeProtobuf: Protobuf<TradeStore>? = null
    var trades = ConcurrentHashMap<ServerPlayerEntity, TradeContext>()

    fun createTrade(
        redPlayerEntity: ServerPlayerEntity,
        bluePlayerEntity: ServerPlayerEntity
    ) {
        val tradeContext = TradeContext.create(redPlayerEntity, bluePlayerEntity)
        tradeContext.initialize()
        tradeContext.start()
    }

    fun tradeStart(
        tradeContext: TradeContext
    ) {
        val redPlayer = tradeContext.redPlayer
        val bluePlayer = tradeContext.bluePlayer
        trades[redPlayer] = tradeContext
        trades[bluePlayer] = tradeContext
    }

    fun tradeEnd(
        tradeContext: TradeContext
    ) {
        val redPlayer = tradeContext.redPlayer
        val bluePlayer = tradeContext.bluePlayer
        trades.remove(redPlayer)
        trades.remove(bluePlayer)
    }
}