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
        clientTradeScreenContext = ClientTradeScreenContext.create(this)
        clientTradeScreenContext.initialize()

        initialized = true
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

    fun start() {
        if (!initialized) {
            throw IllegalStateException("Trade is not initialized")
        }
    }

    companion object {
        fun create(tradeId: String, redName: String, blueName: String): ClientTradeContext =
            ClientTradeContext(tradeId, redName, blueName)
    }
}