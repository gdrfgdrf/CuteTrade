package io.github.gdrfgdrf.cutetrade.page

import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class Pageable {
    var pageableScreenHandler: PageableScreenHandler? = null

    var inventory: PageableInventory? = null
    private var latestPageIndex: Int = 0
    private var itemCount = 0

    fun openScreen(displayName: Text, serverPlayerEntity: ServerPlayerEntity) {
        val factory = PageableScreenHandlerFactory(displayName)
        serverPlayerEntity.openHandledScreen(factory)
        pageableScreenHandler = serverPlayerEntity.currentScreenHandler as PageableScreenHandler
        inventory = pageableScreenHandler!!.inventory
    }

    fun addItemStack(itemStack: ItemStack) {
        if (itemCount == 53) {
            itemCount = 0
            latestPageIndex++
        }
        val pageSize = inventory?.navigator?.pages?.size
        if (latestPageIndex >= pageSize!!) {
            inventory?.addPage()
        }

        val page = inventory?.navigator?.pages?.get(latestPageIndex)

        page!!.slots[itemCount] = itemStack
        itemCount++
    }
}