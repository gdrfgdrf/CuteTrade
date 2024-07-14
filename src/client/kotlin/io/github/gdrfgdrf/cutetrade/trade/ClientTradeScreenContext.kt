/*
 * Copyright 2024 CuteTrade's contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.gdrfgdrf.cutetrade.trade

import io.github.gdrfgdrf.cutetrade.screen.TradeScreen
import net.minecraft.client.MinecraftClient

class ClientTradeScreenContext private constructor() {
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
        fun create() = ClientTradeScreenContext()
    }

}