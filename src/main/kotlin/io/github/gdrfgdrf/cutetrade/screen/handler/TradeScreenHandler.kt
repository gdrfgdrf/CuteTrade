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

package io.github.gdrfgdrf.cutetrade.screen.handler

import io.github.gdrfgdrf.cutetrade.CuteTrade
import io.github.gdrfgdrf.cutetrade.trade.TradeInventory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType

class TradeScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
) : ScreenHandler(CuteTrade.TRADE_SCREEN_HANDLER, syncId) {
    val inventory: Inventory = TradeInventory()

    init {
        checkSize(inventory, INVENTORY_SIZE)
        inventory.onOpen(playerInventory.player)
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val index = j + (i * 3)
                val slot = Slot(inventory, index, 83 + j * 18, 18 + i * 18)
                this.addSlot(slot)
            }
        }
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val index = j + (i * 3) + 9
                val slot = Slot(inventory, index, 83 + 72 + j * 18, 18 + i * 18)
                this.addSlot(slot)
            }
        }
        for (i in 0 until 3) {
            for (j in 0 until 9) {
                val index = j + i * 9 + 9
                this.addSlot(Slot(playerInventory, index, 65 + j * 18, 84 + i * 18))
            }
        }
        for (i in 0 until 9) {
            this.addSlot(Slot(playerInventory, i, 65 + i * 18, 142))
        }
    }

    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType?, player: PlayerEntity?) {
        if (slotIndex in 9 .. 17) {
            return
        }
        super.onSlotClick(slotIndex, button, actionType, player)
    }

    override fun insertItem(stack: ItemStack?, startIndex: Int, endIndex: Int, fromLast: Boolean): Boolean {
        if (startIndex in 9 .. 17) {
            return false
        }
        if (endIndex in 9 .. 17) {
            return false
        }

        return super.insertItem(stack, startIndex, endIndex, fromLast)
    }

    override fun quickMove(player: PlayerEntity?, slot: Int): ItemStack {
        if (slot in 9 .. 17) {
            return ItemStack.EMPTY
        }

        var itemStack = ItemStack.EMPTY
        val slot2 = slots[slot]
        if (slot2.hasStack()) {
            val itemStack2 = slot2.stack
            itemStack = itemStack2.copy()

            if (slot < inventory.size()) {
                if (!this.insertItem(itemStack2, inventory.size(), slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.insertItem(itemStack2, 0, 8, false)) {
                return ItemStack.EMPTY
            }

            if (itemStack2.isEmpty) {
                slot2.stack = ItemStack.EMPTY
            } else {
                slot2.markDirty()
            }
        }

        return itemStack
    }

    override fun canUse(player: PlayerEntity): Boolean {
        return inventory.canPlayerUse(player)
    }

    companion object {
        const val INVENTORY_SIZE = 18 // 3 * 6
    }
}