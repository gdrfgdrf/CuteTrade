package io.github.gdrfgdrf.cutetrade.trade

import io.github.gdrfgdrf.cutetrade.screen.TradeScreen
import net.minecraft.client.MinecraftClient

class ClientTradeScreenContext private constructor(
    private val context: ClientTradeContext,
) {
    private var initialized: Boolean = false
    var tradeScreen: TradeScreen? = null

    private var rawScale: Int = 2

    fun initialize() {
        initialized = true
    }

    fun openTradeScreen() {
        if (!initialized) {
            throw IllegalStateException("Trade is not initialized")
        }
        rawScale = MinecraftClient.getInstance().options.guiScale.value
        if (rawScale != 2) {
            MinecraftClient.getInstance().options.guiScale.value = 2
            MinecraftClient.getInstance().onResolutionChanged()
        }
    }

    fun closeTradeScreen() {
        if (!initialized) {
            throw IllegalStateException("Trade is not initialized")
        }
        if (rawScale != 2) {
            MinecraftClient.getInstance().options.guiScale.value = rawScale
            MinecraftClient.getInstance().onResolutionChanged()
        }

        tradeScreen?.close1()
    }

    companion object {
        fun create(context: ClientTradeContext) = ClientTradeScreenContext(context)
    }

}