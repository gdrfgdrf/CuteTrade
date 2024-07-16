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

import io.github.gdrfgdrf.cutetrade.common.Constants
import io.github.gdrfgdrf.cutetrade.common.Operators
import io.github.gdrfgdrf.cutetrade.common.TraderState
import io.github.gdrfgdrf.cutetrade.extension.sendPacket
import io.github.gdrfgdrf.cutetrade.network.packet.C2SOperationPacket
import net.minecraft.client.MinecraftClient

class ClientTradeContext private constructor(
    val tradeId: String,
    val redName: String,
    val blueName: String
) {
    private var initialized: Boolean = false

    lateinit var clientTradeScreenContext: ClientTradeScreenContext

    var ownState: TraderState = TraderState.UNCHECKED
    var otherState: TraderState = TraderState.UNCHECKED

    fun initialize() {
        clientTradeScreenContext = ClientTradeScreenContext.create()
        clientTradeScreenContext.initialize()

        initialized = true

        val c2SOperationPacket = C2SOperationPacket(Operators.SERVER_CLIENT_INITIALIZED)
        Constants.C2S_OPERATION.sendPacket(c2SOperationPacket::write)
    }

    fun sendTraderStateToServer(
        targetState: TraderState
    ) {
        val c2SOperationPacket = C2SOperationPacket(Operators.SERVER_UPDATE_TRADER_STATE)
        c2SOperationPacket.stringArgs = arrayOf(targetState.name)

        Constants.C2S_OPERATION.sendPacket {
            c2SOperationPacket.write(it)
        }
    }

    fun updateTraderStateFromServer(
        redState: TraderState,
        blueState: TraderState
    ) {
        val name = MinecraftClient.getInstance().player?.name?.string

        if (redName == name) {
            ownState = redState
            otherState = blueState
        } else {
            ownState = blueState
            otherState = redState
        }
    }

    companion object {
        fun create(tradeId: String, redName: String, blueName: String): ClientTradeContext =
            ClientTradeContext(tradeId, redName, blueName)
    }
}