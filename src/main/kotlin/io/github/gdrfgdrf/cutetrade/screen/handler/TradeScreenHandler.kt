package io.github.gdrfgdrf.cutetrade.screen.handler

import io.github.gdrfgdrf.cutetrade.CuteTrade
import io.github.gdrfgdrf.cutetrade.trade.TradeInventory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot

class TradeScreenHandler: ScreenHandler {
    val inventory: Inventory

    constructor(
        syncId: Int,
        playerInventory: PlayerInventory,
    ) : super(CuteTrade.TRADE_SCREEN_HANDLER, syncId) {
        this.inventory = TradeInventory()
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

    override fun quickMove(player: PlayerEntity?, invSlot: Int): ItemStack {
        return ItemStack.EMPTY

//        var newStack = ItemStack.EMPTY
//        val slot = slots[invSlot]
//        if (slot.hasStack()) {
//            val originalStack = slot.stack
//            newStack = originalStack.copy()
//
//            if (invSlot < inventory.size()) {
//                cursorStack = originalStack
//
//                val result = this.insertItem(
//                    originalStack,
//                    inventory.size(),
//                    slots.size,
//                    true
//                )
//
//                cursorStack = ItemStack.EMPTY
//
//                if (!result) {
//                    return ItemStack.EMPTY
//                }
//            } else {
//                cursorStack = originalStack
//
//                val result = this.insertItem(originalStack, 0, inventory.size(), false)
//
//                cursorStack = ItemStack.EMPTY
//
//                if (!result) {
//                    return ItemStack.EMPTY
//                }
//
//            }
//            if (originalStack.isEmpty) {
//                slot.stack = ItemStack.EMPTY
//            } else {
//                slot.markDirty()
//            }
//        }
//
//        return newStack!!
    }

    override fun canUse(player: PlayerEntity): Boolean {
        return true
    }

    companion object {
        val INVENTORY_SIZE = 18 // 3 * 6
        val INVENTORY_LEFT_SIZE = 9 // 3 * 3
        val INVENTORY_RIGHT_SIZE = 9 // 3 * 3
    }
}