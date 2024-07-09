package io.github.gdrfgdrf.cutetrade.manager

import io.github.gdrfgdrf.cutetrade.trade.ClientTradeContext

object ClientTradeManager {
    var currentTrade: ClientTradeContext? = null

    fun initializeTrade(
        tradeId: String,
        redName: String,
        blueName: String
    ) {
        if (currentTrade != null) {
            throw IllegalStateException("The current transaction is still going on")
        }
        currentTrade = ClientTradeContext.create(tradeId, redName, blueName)
        currentTrade!!.initialize()
    }

    fun startTrade() {
        if (currentTrade == null) {
            throw IllegalStateException("No transaction in progress")
        }
        currentTrade!!.start()
    }

    fun endTrade() {
        currentTrade = null
    }

}