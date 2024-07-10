package io.github.gdrfgdrf.cutetrade.trade

import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

class TradeInventory : SimpleInventory(18) {
    fun forceRemoveStack(slot: Int): ItemStack {
        return super.removeStack(slot)
    }

    fun forceRemoveStack(slot: Int, amount: Int): ItemStack {
        return super.removeStack(slot, amount)
    }

    override fun removeStack(slot: Int): ItemStack {
        if (slot in 9 .. 17) {
            return ItemStack.EMPTY
        }
        return super.removeStack(slot)
    }

    override fun removeStack(slot: Int, amount: Int): ItemStack {
        if (slot in 9 .. 17) {
            return ItemStack.EMPTY
        }
        return super.removeStack(slot, amount)
    }
//
    override fun canTransferTo(hopperInventory: Inventory?, slot: Int, stack: ItemStack?): Boolean {
        return slot !in 9 .. 17
    }
}

