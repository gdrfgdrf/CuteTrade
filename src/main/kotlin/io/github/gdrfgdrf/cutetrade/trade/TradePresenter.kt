package io.github.gdrfgdrf.cutetrade.trade

import io.github.gdrfgdrf.cutetrade.common.Operators
import io.github.gdrfgdrf.cutetrade.common.TraderState
import io.github.gdrfgdrf.cutetrade.extension.*
import io.github.gdrfgdrf.cutetrade.network.packet.S2COperationPacket
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import java.time.Instant

class TradePresenter private constructor(
    private val tradeContext: TradeContext
) {
    var muteRedAddItemMessageStartTime: Long = -1
    var muteRedAddItemMessageTime: Long = -1

    var muteBlueAddItemMessageStartTime: Long = -1
    var muteBlueAddItemMessageTime: Long = -1

    fun initialize() {
        val s2COperationPacket = S2COperationPacket(Operators.CLIENT_INITIALIZE_TRADE)
        s2COperationPacket.stringArgs = arrayOf(
            tradeContext.tradeId,
            tradeContext.redPlayer.name.string,
            tradeContext.bluePlayer.name.string
        )
        broadcastOperation(s2COperationPacket)
    }

    fun start() {
        val s2COperationPacket = S2COperationPacket(Operators.CLIENT_TRADE_START)
        broadcastOperation(s2COperationPacket)
        broadcastMessage("trade_start".toCommandMessage())
//        broadcastMessage("notice".toTradeMessage())
    }

    fun updateState(
        redState: TraderState,
        blueState: TraderState
    ) {
        val stringStates: Array<String?> = arrayOf(redState.name, blueState.name)
        val s2COperationPacket = S2COperationPacket(Operators.CLIENT_UPDATE_TRADER_STATE)
        s2COperationPacket.stringArgs = stringStates

        broadcastOperation(s2COperationPacket)
    }

    fun broadcastRedStateChange(
        redState: TraderState
    ) {
        val rootOneself = if (redState == TraderState.CHECKED) {
            "state_checked_oneself"
        } else {
            "state_unchecked_oneself"
        }
        val rootOther = if (redState == TraderState.CHECKED) {
            "state_checked_other"
        } else {
            "state_unchecked_other"
        }

        val prefix = "presenter_prefix".toTradeMessage()
        val toRed = rootOneself.toTradeMessage()
        val toBlue = rootOther.toTradeMessage()
            .format(tradeContext.redPlayer.name.string)

        toRed.send(prefix, tradeContext.redPlayer)
        toBlue.send(prefix, tradeContext.bluePlayer)
    }

    fun broadcastBlueStateChange(
        blueState: TraderState
    ) {
        val rootOneself = if (blueState == TraderState.CHECKED) {
            "state_checked_oneself"
        } else {
            "state_unchecked_oneself"
        }
        val rootOther = if (blueState == TraderState.CHECKED) {
            "state_checked_other"
        } else {
            "state_unchecked_other"
        }

        val prefix = "presenter_prefix".toTradeMessage()
        val toBlue = rootOneself.toTradeMessage()
        val toRed = rootOther.toTradeMessage()
            .format(tradeContext.bluePlayer.name.string)

        toRed.send(prefix, tradeContext.redPlayer)
        toBlue.send(prefix, tradeContext.bluePlayer)
    }

    fun playStartSound() {
        val soundEvent = SoundEvent.of(Identifier("minecraft:entity.experience_orb.pickup"))
        tradeContext.redPlayer.playSound(soundEvent, 100F, 1F)
        tradeContext.bluePlayer.playSound(soundEvent, 100F, 1F)
    }

    fun playStatePositiveSound() {
        val soundEvent = SoundEvent.of(Identifier("minecraft:block.note_block.harp"))
        tradeContext.redPlayer.playSound(soundEvent, 100F, 1F)
        tradeContext.bluePlayer.playSound(soundEvent, 100F, 1F)
    }

    fun playStateNegativeSound() {
        val soundEvent = SoundEvent.of(Identifier("minecraft:block.note_block.bass"))
        tradeContext.redPlayer.playSound(soundEvent, 100F, 1F)
        tradeContext.bluePlayer.playSound(soundEvent, 100F, 1F)
    }

    fun broadcastRedAddItemMessage(
        itemStack: ItemStack
    ) {
        if (muteRedAddItemMessageStartTime != -1L && muteRedAddItemMessageTime != -1L) {
            val now = Instant.now().epochSecond

            if (now - muteRedAddItemMessageStartTime <= muteRedAddItemMessageTime) {
                return
            } else {
                muteRedAddItemMessageStartTime = -1
                muteRedAddItemMessageTime = -1
            }
        }

        val prefix = "presenter_prefix".toTradeMessage()
        "add_item_oneself".toTradeMessage()
            .format(itemStack.name.string, itemStack.count)
            .send(prefix, tradeContext.redPlayer)
        "add_item_other".toTradeMessage()
            .format(tradeContext.redPlayer.name.string, itemStack.name.string, itemStack.count)
            .send(prefix, tradeContext.bluePlayer)
    }

    fun muteRedAddTradeItemMessage(
        time: Long
    ) {
        muteRedAddItemMessageStartTime = Instant.now().epochSecond
        muteRedAddItemMessageTime = time
    }

    fun broadcastRedRemoveItemMessage(
        itemStack: ItemStack
    ) {
        val prefix = "presenter_prefix".toTradeMessage()
        "remove_item_oneself".toTradeMessage()
            .format(itemStack.name.string, itemStack.count)
            .send(prefix, tradeContext.redPlayer)
        "remove_item_other".toTradeMessage()
            .format(tradeContext.redPlayer.name.string)
            .send(prefix, tradeContext.bluePlayer)
    }

    fun playAddItemSound() {
        val soundEvent = SoundEvent.of(Identifier("minecraft:block.note_block.bell"))
        tradeContext.redPlayer.playSound(soundEvent, 100F, 1F)
        tradeContext.bluePlayer.playSound(soundEvent, 100F, 1F)
    }

    fun broadcastBlueAddItem(
        itemStack: ItemStack
    ) {
        if (muteBlueAddItemMessageStartTime != -1L && muteBlueAddItemMessageTime != -1L) {
            val now = Instant.now().epochSecond

            if (now - muteBlueAddItemMessageStartTime <= muteBlueAddItemMessageTime) {
                return
            } else {
                muteBlueAddItemMessageStartTime = -1
                muteBlueAddItemMessageTime = -1
            }
        }

        val prefix = "presenter_prefix".toTradeMessage()
        "add_item_oneself".toTradeMessage()
            .format(itemStack.name.string, itemStack.count)
            .send(prefix, tradeContext.bluePlayer)
        "add_item_other".toTradeMessage()
            .format(tradeContext.bluePlayer.name.string, itemStack.name.string, itemStack.count)
            .send(prefix, tradeContext.redPlayer)
    }

    fun muteBlueAddTradeItemMessage(
        time: Long
    ) {
        muteBlueAddItemMessageStartTime = Instant.now().epochSecond
        muteBlueAddItemMessageTime = time
    }

    fun broadcastBlueRemoveItemMessage(
        itemStack: ItemStack
    ) {
        val prefix = "presenter_prefix".toTradeMessage()
        "remove_item_oneself".toTradeMessage()
            .format(itemStack.name.string, itemStack.count)
            .send(prefix, tradeContext.bluePlayer)
        "remove_item_other".toTradeMessage()
            .format(tradeContext.bluePlayer.name.string)
            .send(prefix, tradeContext.redPlayer)
    }

    fun playRemoveItemSound() {
        val soundEvent = SoundEvent.of(Identifier("minecraft:block.note_block.didgeridoo"))
        tradeContext.redPlayer.playSound(soundEvent, 100F, 1F)
        tradeContext.bluePlayer.playSound(soundEvent, 100F, 1F)
    }

    fun playTerminateSound() {
        val soundEvent = SoundEvent.of(Identifier("minecraft:block.anvil.land"))
        tradeContext.redPlayer.playSound(soundEvent, 100F, 1F)
        tradeContext.bluePlayer.playSound(soundEvent, 100F, 1F)
    }

    fun playFinishSound() {
        val soundEvent = SoundEvents.ENTITY_PLAYER_LEVELUP
        tradeContext.redPlayer.playSound(soundEvent, 100F, 1F)
        tradeContext.bluePlayer.playSound(soundEvent, 100F, 1F)
    }

    fun broadcastFinishMessage() {
        broadcastMessage("trade_end".toCommandMessage())
    }

    fun broadcastTerminateMessage() {
        broadcastMessage("trade_terminate".toCommandMessage())
    }

    fun end() {
        val s2COperationPacket = S2COperationPacket(Operators.CLIENT_TRADE_END)
        broadcastOperation(s2COperationPacket)
    }

    private fun broadcastMessage(message: String) {
        message.send(tradeContext.redPlayer)
        message.send(tradeContext.bluePlayer)
    }

    private fun broadcastOperation(
        s2COperationPacket: S2COperationPacket
    ) {
        if (!tradeContext.redPlayer.isDisconnected) {
            sendOperationPacket(tradeContext.redPlayer, s2COperationPacket::write)
        }
        if (!tradeContext.bluePlayer.isDisconnected) {
            sendOperationPacket(tradeContext.bluePlayer, s2COperationPacket::write)
        }
    }

    companion object {
        fun create(tradeContext: TradeContext): TradePresenter = TradePresenter(tradeContext)
    }

}