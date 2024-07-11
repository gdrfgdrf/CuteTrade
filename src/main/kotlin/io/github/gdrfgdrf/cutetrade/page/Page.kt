package io.github.gdrfgdrf.cutetrade.page

import io.github.gdrfgdrf.cutetrade.extension.logInfo
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

class Page(
    private val rows: Int
) {
    val slots: ArrayList<ItemStack> = ArrayList(rows * 9)

    fun initialize() {
        for (row in 0 until rows) {
            for (column in 0 until 9) {
                slots.add(ItemStack.EMPTY)
            }
        }
    }

    fun show(inventory: PageableInventory) {
        slots.forEachIndexed { index, itemStack ->
            inventory.setStack(index, itemStack)
        }
    }

}