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

import io.github.gdrfgdrf.cutetrade.common.Operators
import io.github.gdrfgdrf.cutetrade.common.TraderState
import io.github.gdrfgdrf.cutetrade.extension.*
import io.github.gdrfgdrf.cutetrade.network.packet.S2COperationPacket
import io.github.gdrfgdrf.cutetranslationapi.text.CuteTranslation
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundEvents
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

    fun broadcastRedInitialized() {
        broadcastTradeMessage("player_trade_initialized") {
            it.format0(tradeContext.redPlayer.name.string)
        }
    }

    fun broadcastBlueInitialized() {
        broadcastTradeMessage("player_trade_initialized") {
            it.format0(tradeContext.bluePlayer.name.string)
        }
    }

    fun start() {
        val s2COperationPacket = S2COperationPacket(Operators.CLIENT_TRADE_START)
        broadcastOperation(s2COperationPacket)

        broadcastTradeMessage("trade_start")
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

        val prefixRed = "presenter_prefix".toTradeText(tradeContext.redPlayer)
        val prefixBlue = "presenter_prefix".toTradeText(tradeContext.bluePlayer)

        val toRed = rootOneself.toTradeTranslation(tradeContext.redPlayer)
        val toBlue = rootOther.toTradeTranslation(tradeContext.bluePlayer)
            .format0(tradeContext.redPlayer.name.string)

        toRed.send(prefixRed, tradeContext.redPlayer)
        toBlue.send(prefixBlue, tradeContext.bluePlayer)
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

        val prefixRed = "presenter_prefix".toTradeText(tradeContext.redPlayer)
        val prefixBlue = "presenter_prefix".toTradeText(tradeContext.bluePlayer)

        val toBlue = rootOneself.toTradeTranslation(tradeContext.bluePlayer)
        val toRed = rootOther.toTradeTranslation(tradeContext.redPlayer)
            .format0(tradeContext.bluePlayer.name.string)

        toRed.send(prefixRed, tradeContext.redPlayer)
        toBlue.send(prefixBlue, tradeContext.bluePlayer)
    }

    fun playStartSound() {
        val soundEvent = SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP
        tradeContext.redPlayer.playSound(soundEvent, 100F, 1F)
        tradeContext.bluePlayer.playSound(soundEvent, 100F, 1F)
    }

    fun playStatePositiveSound() {
        val soundEvent = SoundEvents.BLOCK_NOTE_BLOCK_HARP
        tradeContext.redPlayer.playSound(soundEvent, 100F, 1F)
        tradeContext.bluePlayer.playSound(soundEvent, 100F, 1F)
    }

    fun playStateNegativeSound() {
        val soundEvent = SoundEvents.BLOCK_NOTE_BLOCK_BASS
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

        val prefixRed = "presenter_prefix".toTradeText(tradeContext.redPlayer)
        val prefixBlue = "presenter_prefix".toTradeText(tradeContext.bluePlayer)

        "add_item_oneself".toTradeTranslation(tradeContext.redPlayer)
            .format0(itemStack.name.string, itemStack.count)
            .send(prefixRed, tradeContext.redPlayer)
        "add_item_other".toTradeTranslation(tradeContext.bluePlayer)
            .format0(tradeContext.redPlayer.name.string, itemStack.name.string, itemStack.count)
            .send(prefixBlue, tradeContext.bluePlayer)
    }

    fun broadcastRedRemoveItemMessage(
        itemStack: ItemStack
    ) {
        val prefixRed = "presenter_prefix".toTradeText(tradeContext.redPlayer)
        val prefixBlue = "presenter_prefix".toTradeText(tradeContext.bluePlayer)

        "remove_item_oneself".toTradeTranslation(tradeContext.redPlayer)
            .format0(itemStack.name.string, itemStack.count)
            .send(prefixRed, tradeContext.redPlayer)
        "remove_item_other".toTradeTranslation(tradeContext.bluePlayer)
            .format0(tradeContext.redPlayer.name.string)
            .send(prefixBlue, tradeContext.bluePlayer)
    }

    fun playAddItemSound() {
        val soundEvent = SoundEvents.BLOCK_NOTE_BLOCK_BELL
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

        val prefixRed = "presenter_prefix".toTradeText(tradeContext.redPlayer)
        val prefixBlue = "presenter_prefix".toTradeText(tradeContext.bluePlayer)

        "add_item_oneself".toTradeTranslation(tradeContext.bluePlayer)
            .format0(itemStack.name.string, itemStack.count)
            .send(prefixBlue, tradeContext.bluePlayer)
        "add_item_other".toTradeTranslation(tradeContext.redPlayer)
            .format0(tradeContext.bluePlayer.name.string, itemStack.name.string, itemStack.count)
            .send(prefixRed, tradeContext.redPlayer)
    }

    fun broadcastBlueRemoveItemMessage(
        itemStack: ItemStack
    ) {
        val prefixRed = "presenter_prefix".toTradeText(tradeContext.redPlayer)
        val prefixBlue = "presenter_prefix".toTradeText(tradeContext.bluePlayer)

        "remove_item_oneself".toTradeTranslation(tradeContext.bluePlayer)
            .format0(itemStack.name.string, itemStack.count)
            .send(prefixBlue, tradeContext.bluePlayer)
        "remove_item_other".toTradeTranslation(tradeContext.redPlayer)
            .format0(tradeContext.bluePlayer.name.string)
            .send(prefixRed, tradeContext.redPlayer)
    }

    fun playRemoveItemSound() {
        val soundEvent = SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO
        tradeContext.redPlayer.playSound(soundEvent, 100F, 1F)
        tradeContext.bluePlayer.playSound(soundEvent, 100F, 1F)
    }

    fun playTerminateSound() {
        val soundEvent = SoundEvents.BLOCK_ANVIL_LAND
        tradeContext.redPlayer.playSound(soundEvent, 100F, 1F)
        tradeContext.bluePlayer.playSound(soundEvent, 100F, 1F)
    }

    fun playFinishSound() {
        val soundEvent = SoundEvents.ENTITY_PLAYER_LEVELUP
        tradeContext.redPlayer.playSound(soundEvent, 100F, 1F)
        tradeContext.bluePlayer.playSound(soundEvent, 100F, 1F)
    }

    fun broadcastFinishMessage() {
        broadcastTradeMessage("trade_end")
    }

    fun broadcastTerminateMessage() {
        broadcastTradeMessage("trade_terminate")
    }

    fun end() {
        val s2COperationPacket = S2COperationPacket(Operators.CLIENT_TRADE_END)
        broadcastOperation(s2COperationPacket)
    }

    private fun broadcastTradeMessage(key: String, processor: (CuteTranslation) -> Unit = {}) {
        val redTranslation = key.toTradeTranslation(tradeContext.redPlayer)
        processor(redTranslation)
        redTranslation.sendTo(tradeContext.redPlayer)

        val blueTranslation = key.toTradeTranslation(tradeContext.bluePlayer)
        processor(blueTranslation)
        blueTranslation.sendTo(tradeContext.bluePlayer)
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