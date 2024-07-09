package io.github.gdrfgdrf.cutetrade.screen.factory

import io.github.gdrfgdrf.cutetrade.screen.handler.DevScreenHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text

object DevScreenHandlerFactory : NamedScreenHandlerFactory {
    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return DevScreenHandler(syncId, playerInventory)
    }

    override fun getDisplayName(): Text {
        return Text.of("Dev")
    }
}