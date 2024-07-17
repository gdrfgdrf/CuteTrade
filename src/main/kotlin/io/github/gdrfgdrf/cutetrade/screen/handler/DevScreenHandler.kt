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
import io.github.gdrfgdrf.cutetrade.extension.logInfo
import io.github.gdrfgdrf.cutetrade.trade.TradeInventory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType

class DevScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory
) : ScreenHandler(CuteTrade.DEV_SCREEN_HANDLER, syncId) {
    private val inventory: Inventory = TradeInventory()

    init {
        checkSize(inventory, INVENTORY_SIZE)
        inventory.onOpen(playerInventory.player)

        // left side 0 - 8 (9)
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val index = j + (i * 3)
                val slot = Slot(inventory, index, 83 + j * 18, 18 + i * 18)
                this.addSlot(slot)
            }
        }

        // right side 9 - 17 (9)
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val index = j + (i * 3) + 9
                val slot = Slot(inventory, index, 83 + 72 + j * 18, 18 + i * 18)
                this.addSlot(slot)
            }
        }

        // Player inventory 9 - 35 (27)
        for (i in 0 until 3) {
            for (j in 0 until 9) {
                val index = j + i * 9 + 9
                this.addSlot(Slot(playerInventory, index, 65 + j * 18, 84 + i * 18))
            }
        }

        // Player hotbar 0 - 8 (9)
        for (i in 0 until 9) {
            this.addSlot(Slot(playerInventory, i, 65 + i * 18, 142))
        }
    }

    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType?, player: PlayerEntity?) {
//        "onSlotClick $slotIndex | $button | $actionType | $player".logInfo()
        if (slotIndex in 9 .. 17) {
            return
        }
//        if (slotIndex in 0 .. 8 && player is ServerPlayerEntity) {
//            val currentTrade = player.currentTrade()
//            if (currentTrade != null) {
//                if (player.isRed()) {
//                    currentTrade.redAddTradeItem()
//                }
//            }
//        }
        super.onSlotClick(slotIndex, button, actionType, player)
    }

    override fun insertItem(stack: ItemStack?, startIndex: Int, endIndex: Int, fromLast: Boolean): Boolean {
        "insertItem $stack | $startIndex | $endIndex | $fromLast".logInfo()
        if (startIndex in 9 .. 17) {
            return false
        }
        if (endIndex in 9 .. 17) {
            return false
        }

        return super.insertItem(stack, startIndex, endIndex, fromLast)
    }

    override fun transferSlot(player: PlayerEntity?, invSlot: Int): ItemStack {
//        return ItemStack.EMPTY

        var newStack = ItemStack.EMPTY
        val slot = slots[invSlot]
        if (slot.hasStack()) {
            val originalStack = slot.stack
            newStack = originalStack.copy()

            if (invSlot < inventory.size()) {
                cursorStack = originalStack

                val result = this.insertItem(
                    originalStack,
                    inventory.size(),
                    slots.size,
                    true
                )

                cursorStack = ItemStack.EMPTY

                if (!result) {
                    return ItemStack.EMPTY
                }
            } else {
                cursorStack = originalStack

                val result = this.insertItem(originalStack, 0, inventory.size(), false)

                cursorStack = ItemStack.EMPTY

                if (!result) {
                    return ItemStack.EMPTY
                }

            }
            if (originalStack.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }
        }

        return newStack!!
    }

    override fun canUse(player: PlayerEntity): Boolean {
        return this.inventory.canPlayerUse(player)
    }

    companion object {
        const val INVENTORY_SIZE = 18 // 3 * 6
    }
}