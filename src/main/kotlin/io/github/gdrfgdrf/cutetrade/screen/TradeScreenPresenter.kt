package io.github.gdrfgdrf.cutetrade.screen

import io.github.gdrfgdrf.cutetrade.extension.logInfo
import io.github.gdrfgdrf.cutetrade.extension.sendOperationPacket
import io.github.gdrfgdrf.cutetrade.network.packet.S2COperationPacket
import io.github.gdrfgdrf.cutetrade.screen.factory.TradeScreenHandlerFactory
import io.github.gdrfgdrf.cutetrade.trade.TradeInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtString
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerListener
import net.minecraft.server.network.ServerPlayerEntity


class TradeScreenPresenter private constructor(
    private val tradeScreenContext: TradeScreenContext,
    private val redPlayer: ServerPlayerEntity,
    private val bluePlayer: ServerPlayerEntity,
) {
    private var redScreenHandler: ScreenHandler? = null
    private var blueScreenHandler: ScreenHandler? = null

    fun openTradeScreen() {
        val factory = TradeScreenHandlerFactory(tradeScreenContext.context.tradeId)
        redPlayer.openHandledScreen(factory)
        bluePlayer.openHandledScreen(factory)

        redScreenHandler = redPlayer.currentScreenHandler
        blueScreenHandler = bluePlayer.currentScreenHandler

        redScreenHandler!!.addListener(object : ScreenHandlerListener {
            override fun onSlotUpdate(handler: ScreenHandler?, slotId: Int, stack: ItemStack?) {
                if (slotId !in 0 .. 8) {
                    return
                }
                val slot = handler?.getSlot(slotId) ?: return
                if (slot.hasStack()) {
                    tradeScreenContext.context.redAddTradeItem(slotId, slot.stack.copy())
                } else {
                    tradeScreenContext.context.redRemoveTradeItem(slotId)
                }
            }

            override fun onPropertyUpdate(handler: ScreenHandler?, property: Int, value: Int) {
            }
        })
        blueScreenHandler!!.addListener(object : ScreenHandlerListener {
            override fun onSlotUpdate(handler: ScreenHandler?, slotId: Int, stack: ItemStack?) {
                if (slotId !in 0 .. 8) {
                    return
                }
                val slot = handler?.getSlot(slotId) ?: return
                if (slot.hasStack()) {
                    tradeScreenContext.context.blueAddTradeItem(slotId, slot.stack.copy())
                } else {
                    tradeScreenContext.context.blueRemoveTradeItem(slotId)
                }
            }

            override fun onPropertyUpdate(handler: ScreenHandler?, property: Int, value: Int) {
            }

        })
    }

    fun syncTradeInventory() {
        if (redScreenHandler == null || blueScreenHandler == null) {
            throw IllegalStateException("screen handler is not initialized")
        }

        val redTradeInventory = tradeScreenContext.context.redTradeItemStack
        val blueTradeInventory = tradeScreenContext.context.blueTradeItemStack

        redTradeInventory.itemArray.forEachIndexed { index, tradeItem ->
            tradeItem?.let {
                val redRevision = redScreenHandler!!.nextRevision()
                redScreenHandler!!.setStackInSlot(index, redRevision, tradeItem.itemStack)

                val blueRevision = blueScreenHandler!!.nextRevision()
                blueScreenHandler!!.setStackInSlot(index + 9, blueRevision, tradeItem.itemStack)
            }
        }
        blueTradeInventory.itemArray.forEachIndexed { index, tradeItem ->
            tradeItem?.let {
                val redRevision = redScreenHandler!!.nextRevision()
                redScreenHandler!!.setStackInSlot(index + 9, redRevision, tradeItem.itemStack)

                val blueRevision = blueScreenHandler!!.nextRevision()
                blueScreenHandler!!.setStackInSlot(index, blueRevision, tradeItem.itemStack)
            }
        }
    }

    fun closeTradeScreen() {
        redPlayer.closeHandledScreen()
        bluePlayer.closeHandledScreen()
    }

    private fun broadcastOperation(
        s2COperationPacket: S2COperationPacket,
    ) {
        sendOperationPacket(redPlayer, s2COperationPacket::write)
        sendOperationPacket(bluePlayer, s2COperationPacket::write)
    }

    companion object {
        fun create(
            tradeScreenContext: TradeScreenContext,
            redPlayer: ServerPlayerEntity,
            bluePlayer: ServerPlayerEntity,
        ): TradeScreenPresenter = TradeScreenPresenter(
            tradeScreenContext,
            redPlayer,
            bluePlayer
        )
    }

}