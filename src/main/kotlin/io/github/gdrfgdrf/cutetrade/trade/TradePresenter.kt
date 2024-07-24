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
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.HoverEvent
import net.minecraft.util.Identifier

class TradePresenter private constructor(
    private val tradeContext: TradeContext
) {
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

        tradeContext.redPlayer.translationScope {
            val prefix = toTradeText("presenter_prefix")
            val checkStateMessage = toTradeTranslation(rootOneself)

            checkStateMessage.send(prefix)
        }
        tradeContext.bluePlayer.translationScope {
            val prefix = toTradeText("presenter_prefix")
            val checkStateMessage = toTradeTranslation(rootOther)
                .format0(tradeContext.redPlayer.name.string)

            checkStateMessage.send(prefix)
        }
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

        tradeContext.redPlayer.translationScope {
            val prefix = toTradeText("presenter_prefix")
            val checkStateMessage = toTradeTranslation(rootOther)
                .format0(tradeContext.bluePlayer.name.string)

            checkStateMessage.send(prefix)
        }
        tradeContext.bluePlayer.translationScope {
            val prefix = toTradeText("presenter_prefix")
            val checkStateMessage = toTradeTranslation(rootOneself)

            checkStateMessage.send(prefix)
        }
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
        tradeContext.redPlayer.translationScope {
            val itemStackContent = HoverEvent.ItemStackContent(itemStack)

            val prefix = toTradeText("presenter_prefix")
            val addItemMessage = toTradeTranslation("add_item_oneself")
            val itemMessage = toTradeText("item")
                .format(itemStack.name.string, itemStack.count)
                .showItem(itemStackContent)

            addItemMessage
                .append(itemMessage)

            addItemMessage.send(prefix)
        }
        tradeContext.bluePlayer.translationScope {
            val itemStackContent = HoverEvent.ItemStackContent(itemStack)

            val prefix = toTradeText("presenter_prefix")
            val addItemMessage = toTradeTranslation("add_item_other")
                .format0(tradeContext.redPlayer.name.string)
            val itemMessage = toTradeText("item")
                .format(itemStack.name.string, itemStack.count)
                .showItem(itemStackContent)

            addItemMessage.append(itemMessage)

            addItemMessage.send(prefix)
        }
    }

    fun broadcastRedRemoveItemMessage(
        itemStack: ItemStack
    ) {
        tradeContext.redPlayer.translationScope {
            val prefix = toTradeText("presenter_prefix")
            val removeItemMessage = toTradeTranslation("remove_item_oneself")

            removeItemMessage.send(prefix)
        }
        tradeContext.bluePlayer.translationScope {
            val prefix = toTradeText("presenter_prefix")
            val removeItemMessage = toTradeTranslation("remove_item_other")
                .format0(tradeContext.redPlayer.name.string)

            removeItemMessage.send(prefix)
        }
    }

    fun playAddItemSound() {
        val soundEvent = SoundEvent.of(Identifier("minecraft:block.note_block.bell"))
        tradeContext.redPlayer.playSound(soundEvent, 100F, 1F)
        tradeContext.bluePlayer.playSound(soundEvent, 100F, 1F)
    }

    fun broadcastBlueAddItem(
        itemStack: ItemStack
    ) {
        tradeContext.redPlayer.translationScope {
            val itemStackContent = HoverEvent.ItemStackContent(itemStack)

            val prefix = toTradeText("presenter_prefix")
            val addItemMessage = toTradeTranslation("add_item_other")
                .format0(tradeContext.bluePlayer.name.string)
            val itemMessage = toTradeText("item")
                .format(itemStack.name.string, itemStack.count)
                .showItem(itemStackContent)

            addItemMessage.append(itemMessage)

            addItemMessage.send(prefix)
        }
        tradeContext.bluePlayer.translationScope {
            val itemStackContent = HoverEvent.ItemStackContent(itemStack)

            val prefix = toTradeText("presenter_prefix")
            val addItemMessage = toTradeTranslation("add_item_oneself")
            val itemMessage = toTradeText("item")
                .format(itemStack.name.string, itemStack.count)
                .showItem(itemStackContent)

            addItemMessage.append(itemMessage)

            addItemMessage.send(prefix)
        }
    }

    fun broadcastBlueRemoveItemMessage(
        itemStack: ItemStack
    ) {
        tradeContext.redPlayer.translationScope {
            val prefix = toTradeText("presenter_prefix")
            val removeItemMessage = toTradeTranslation("remove_item_other")
                .format0(tradeContext.bluePlayer.name.string)

            removeItemMessage.send(prefix)
        }
        tradeContext.bluePlayer.translationScope {
            val prefix = toTradeText("presenter_prefix")
            val removeItemMessage = toTradeTranslation("remove_item_oneself")

            removeItemMessage.send(prefix)
        }
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
        broadcastTradeMessage("trade_end")
    }

    fun broadcastTerminateMessage() {
        broadcastTradeMessage("trade_terminate")
    }

    fun end() {
        val s2COperationPacket = S2COperationPacket(Operators.CLIENT_TRADE_END)
        broadcastOperation(s2COperationPacket)
    }

    private fun broadcastTradeMessage(key: String, processor: (TranslationTextProxy) -> Unit = {}) {
        tradeContext.redPlayer.translationScope {
            val tradeTranslation = toTradeTranslation(key)
            processor(tradeTranslation)
            tradeTranslation.send()
        }
        tradeContext.bluePlayer.translationScope {
            val tradeTranslation = toTradeTranslation(key)
            processor(tradeTranslation)
            tradeTranslation.send()
        }
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