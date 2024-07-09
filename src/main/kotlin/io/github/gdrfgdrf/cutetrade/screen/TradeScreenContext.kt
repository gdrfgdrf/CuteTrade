package io.github.gdrfgdrf.cutetrade.screen

import io.github.gdrfgdrf.cutetrade.trade.TradeContext
import net.minecraft.server.network.ServerPlayerEntity

class TradeScreenContext private constructor(
    val context: TradeContext,
    private val redPlayer: ServerPlayerEntity,
    private val bluePlayer: ServerPlayerEntity
) {
    private var initialized = false

    private lateinit var screenPresenter: TradeScreenPresenter

    fun initialize() {
        screenPresenter = TradeScreenPresenter.create(
            this,
            redPlayer,
            bluePlayer
        )

        initialized = true
    }

    fun syncTradeInventory() {
        screenPresenter.syncTradeInventory()
    }

    fun start() {
        if (!initialized) {
            throw IllegalStateException("Trade is not initialized")
        }

        screenPresenter.openTradeScreen()
    }

    fun end() {
        if (!initialized) {
            throw IllegalStateException("Trade is not initialized")
        }
        screenPresenter.closeTradeScreen()
    }

    companion object {
        fun create(
            context: TradeContext,
            redPlayer: ServerPlayerEntity,
            bluePlayer: ServerPlayerEntity
        ): TradeScreenContext = TradeScreenContext(context, redPlayer, bluePlayer)
    }
}