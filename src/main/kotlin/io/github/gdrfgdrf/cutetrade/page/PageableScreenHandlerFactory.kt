package io.github.gdrfgdrf.cutetrade.page

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.text.Text

class PageableScreenHandlerFactory(
    private val displayName: Text
) : NamedScreenHandlerFactory {
    override fun createMenu(
        syncId: Int,
        playerInventory: PlayerInventory,
        player: PlayerEntity,
    ): ScreenHandler {
        val pageableScreenHandler = PageableScreenHandler(syncId, playerInventory)

        return pageableScreenHandler
    }

    override fun getDisplayName(): Text {
        return displayName
    }
}