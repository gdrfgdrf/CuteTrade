package io.github.gdrfgdrf.cutetrade.page

import io.github.gdrfgdrf.cutetrade.extension.logInfo
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.network.ServerPlayerEntity

class PageableScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
) :
    ScreenHandler(
        PageableRegistry.PAGEABLE_SCREEN_HANDLER,
        syncId
    ) {
    var onItemClick: ((Int) -> Unit)? = null
    var inventory: PageableInventory? = null

    init {
        inventory = PageableInventory(rows)
        inventory!!.navigator = Navigator(inventory!!)
        inventory!!.onOpen(playerInventory.player)

        for (row in 0 until rows) {
            for (column in 0 until 9) {
                val index = column + row * 9
                this.addSlot(Slot(inventory, index, 8 + column * 18, 18 + row * 18))
            }
        }

        val i = (rows - 4) * 18
        for (j in 0 until 3) {
            for (k in 0 until 9) {
                this.addSlot(Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i))
            }
        }

        for (j in 0 until 9) {
            this.addSlot(Slot(playerInventory, j, 8 + j * 18, 161 + i))
        }
    }

    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType?, player: PlayerEntity?) {
        if (player !is ServerPlayerEntity) {
            return
        }
        if (slotIndex != 45 && slotIndex != 49 && slotIndex != 53) {
            if (onItemClick != null) {
                onItemClick!!(slotIndex)
            }
            return
        }
        when (slotIndex) {
            45 -> inventory?.navigator?.previous()
            49 -> player.closeHandledScreen()
            53 -> inventory?.navigator?.next()
        }
    }

    override fun insertItem(stack: ItemStack?, startIndex: Int, endIndex: Int, fromLast: Boolean): Boolean {
        return false
    }

    override fun quickMove(player: PlayerEntity?, slot: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun canUse(player: PlayerEntity?): Boolean {
        return inventory!!.canPlayerUse(player)
    }

    companion object {
        var rows: Int = 6
    }
}