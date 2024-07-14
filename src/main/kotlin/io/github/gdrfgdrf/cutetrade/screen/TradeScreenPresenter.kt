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

package io.github.gdrfgdrf.cutetrade.screen

import io.github.gdrfgdrf.cutetrade.screen.factory.TradeScreenHandlerFactory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerListener
import net.minecraft.server.network.ServerPlayerEntity


class TradeScreenPresenter private constructor(
    private val tradeScreenContext: TradeScreenContext,
    private val redPlayer: ServerPlayerEntity,
    private val bluePlayer: ServerPlayerEntity,
) {
    private var redScreenHandler: ScreenHandler? = null
    private var blueScreenHandler: ScreenHandler? = null

    fun openTradeScreen() {
        val factory = TradeScreenHandlerFactory(tradeScreenContext.context.tradeId)
        redPlayer.openHandledScreen(factory)
        bluePlayer.openHandledScreen(factory)

        redScreenHandler = redPlayer.currentScreenHandler
        blueScreenHandler = bluePlayer.currentScreenHandler

        redScreenHandler!!.addListener(object : ScreenHandlerListener {
            override fun onSlotUpdate(handler: ScreenHandler?, slotId: Int, stack: ItemStack?) {
                if (slotId !in 0 .. 8) {
                    return
                }
                val slot = handler?.getSlot(slotId) ?: return
                if (slot.hasStack()) {
                    tradeScreenContext.context.redSetTradeItem(slotId, slot.stack)
                } else {
                    tradeScreenContext.context.redRemoveTradeItem(slotId)
                }
            }

            override fun onPropertyUpdate(handler: ScreenHandler?, property: Int, value: Int) {
            }
        })
        blueScreenHandler!!.addListener(object : ScreenHandlerListener {
            override fun onSlotUpdate(handler: ScreenHandler?, slotId: Int, stack: ItemStack?) {
                if (slotId !in 0 .. 8) {
                    return
                }
                val slot = handler?.getSlot(slotId) ?: return
                if (slot.hasStack()) {
                    tradeScreenContext.context.blueSetTradeItem(slotId, slot.stack)
                } else {
                    tradeScreenContext.context.blueRemoveTradeItem(slotId)
                }
            }

            override fun onPropertyUpdate(handler: ScreenHandler?, property: Int, value: Int) {
            }

        })
    }

    fun syncTradeInventory() {
        if (redScreenHandler == null || blueScreenHandler == null) {
            throw IllegalStateException("screen handler is not initialized")
        }

        val redTradeInventory = tradeScreenContext.context.redTradeItemStack
        val blueTradeInventory = tradeScreenContext.context.blueTradeItemStack

        redTradeInventory.itemArray.forEachIndexed { index, tradeItem ->
            tradeItem?.let {
                val redRevision = redScreenHandler!!.nextRevision()
                redScreenHandler!!.setStackInSlot(index, redRevision, tradeItem.itemStack)

                val blueRevision = blueScreenHandler!!.nextRevision()
                blueScreenHandler!!.setStackInSlot(index + 9, blueRevision, tradeItem.itemStack)
            }
        }
        blueTradeInventory.itemArray.forEachIndexed { index, tradeItem ->
            tradeItem?.let {
                val redRevision = redScreenHandler!!.nextRevision()
                redScreenHandler!!.setStackInSlot(index + 9, redRevision, tradeItem.itemStack)

                val blueRevision = blueScreenHandler!!.nextRevision()
                blueScreenHandler!!.setStackInSlot(index, blueRevision, tradeItem.itemStack)
            }
        }
    }

    fun closeTradeScreen() {
        redPlayer.closeHandledScreen()
        bluePlayer.closeHandledScreen()
    }

    companion object {
        fun create(
            tradeScreenContext: TradeScreenContext,
            redPlayer: ServerPlayerEntity,
            bluePlayer: ServerPlayerEntity,
        ): TradeScreenPresenter = TradeScreenPresenter(
            tradeScreenContext,
            redPlayer,
            bluePlayer
        )
    }

}