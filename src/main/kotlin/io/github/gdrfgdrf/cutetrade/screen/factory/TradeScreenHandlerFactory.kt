package io.github.gdrfgdrf.cutetrade.screen.factory

import io.github.gdrfgdrf.cutetrade.screen.handler.TradeScreenHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text

class TradeScreenHandlerFactory(private val name: String) : NamedScreenHandlerFactory {
    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return TradeScreenHandler(syncId, playerInventory)
    }

    override fun getDisplayName(): Text {
        return Text.of(name)
    }
}