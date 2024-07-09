package io.github.gdrfgdrf.cutetrade.screen.handler

import io.github.gdrfgdrf.cutetrade.CuteTrade
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot

class DevScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory
) : ScreenHandler(CuteTrade.DEV_SCREEN_HANDLER, syncId) {
    private val inventory: Inventory

    init {
        this.inventory = SimpleInventory(18)
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
        var newStack = ItemStack.EMPTY
        val slot = slots[invSlot]
        if (slot.hasStack()) {
            val originalStack = slot.stack
            newStack = originalStack.copy()
            if (invSlot < inventory.size()) {
                if (!this.insertItem(
                        originalStack,
                        inventory.size(), slots.size, true
                    )
                ) {
                    return ItemStack.EMPTY
                }
            } else if (!this.insertItem(originalStack, 0, inventory.size(), false)) {
                return ItemStack.EMPTY
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