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

class TradeScreenHandler: ScreenHandler {
    val inventory: Inventory
    val playerInventory: PlayerInventory

    constructor(
        syncId: Int,
        playerInventory: PlayerInventory,
    ) : super(CuteTrade.TRADE_SCREEN_HANDLER, syncId) {
        this.inventory = TradeInventory()
        this.playerInventory = playerInventory

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
        val INVENTORY_SIZE = 18 // 3 * 6
        val INVENTORY_LEFT_SIZE = 9 // 3 * 3
        val INVENTORY_RIGHT_SIZE = 9 // 3 * 3
    }
}