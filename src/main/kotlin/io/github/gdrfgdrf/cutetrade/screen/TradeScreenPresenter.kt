package io.github.gdrfgdrf.cutetrade.screen

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
                val slot = handler?.getSlot(slotId) ?: return
                if (slotId in 9..17) {
                    if (slot.hasStack()) {
                        val itemStack = slot.stack
                        val addBy = itemStack.getOrCreateNbt().get("cutetrade-add-by")?.asString()

                        if ("server" == addBy) {
                            val cursorStack = handler.cursorStack
                            val cursorStackAddBy = cursorStack.getOrCreateNbt().get("cutetrade-add-by")?.asString()
                            if (cursorStackAddBy == "server") {
                                itemStack.increment(cursorStack.count)

                                tradeScreenContext.context.blueAddTradeItem(
                                    slotId - 9,
                                    itemStack,
                                    playSound = false,
                                    updateState = false,
                                    broadcastMessage = false,
                                    bypass = true
                                )
                                handler.cursorStack = ItemStack.EMPTY
                            }
                            return
                        }
                    } else {
                        val cursorStack = handler.cursorStack
                        val cursorStackAddBy = cursorStack.getOrCreateNbt().get("cutetrade-add-by")?.asString()
                        if (cursorStackAddBy == "server") {
                            tradeScreenContext.context.blueAddTradeItem(
                                slotId - 9,
                                cursorStack.copy(),
                                playSound = false,
                                updateState = false,
                                broadcastMessage = false,
                                bypass = true
                            )
                            handler.cursorStack = ItemStack.EMPTY
                            return
                        }
                    }

                    val count = slot.stack?.count ?: return
                    val itemStack = (slot.inventory as TradeInventory).forceRemoveStack(slot.index, count)
                    redPlayer.inventory.offerOrDrop(itemStack)
                    return
                }
                if (slotId in 0 .. 8) {
                    val cursorStack = handler.cursorStack
                    cursorStack.getOrCreateNbt().remove("cutetrade-add-by")

                    if (slot.hasStack()) {
                        slot.stack.getOrCreateNbt().put("cutetrade-add-by", NbtString.of("server"))
                        tradeScreenContext.context.redAddTradeItem(slotId, slot.stack.copy())
                    } else {
                        tradeScreenContext.context.redRemoveTradeItem(slotId)
                    }
                    return
                }
                if (slotId in 15 .. 53) {
                    if (slot.hasStack()) {
                        slot.stack.getOrCreateNbt().remove("cutetrade-add-by")
                    }
                }
            }

            override fun onPropertyUpdate(handler: ScreenHandler?, property: Int, value: Int) {
            }
        })
        blueScreenHandler!!.addListener(object : ScreenHandlerListener {
            override fun onSlotUpdate(handler: ScreenHandler?, slotId: Int, stack: ItemStack?) {
                val slot = handler?.getSlot(slotId) ?: return
                if (slotId in 9..17) {
                    if (slot.hasStack()) {
                        val itemStack = slot.stack
                        val addBy = itemStack.getOrCreateNbt().get("cutetrade-add-by")?.asString()

                        if ("server" == addBy) {
                            val cursorStack = handler.cursorStack
                            val cursorStackAddBy = cursorStack.getOrCreateNbt().get("cutetrade-add-by")?.asString()
                            if (cursorStackAddBy == "server") {
                                itemStack.increment(cursorStack.count)

                                tradeScreenContext.context.redAddTradeItem(
                                    slotId - 9,
                                    itemStack,
                                    playSound = false,
                                    updateState = false,
                                    broadcastMessage = false,
                                    bypass = true
                                )
                                handler.cursorStack = ItemStack.EMPTY
                            }
                            return
                        }
                    } else {
                        val cursorStack = handler.cursorStack
                        val cursorStackAddBy = cursorStack.getOrCreateNbt().get("cutetrade-add-by")?.asString()
                        if (cursorStackAddBy == "server") {
                            tradeScreenContext.context.redAddTradeItem(
                                slotId - 9,
                                cursorStack.copy(),
                                playSound = false,
                                updateState = false,
                                broadcastMessage = false,
                                bypass = true
                            )
                            handler.cursorStack = ItemStack.EMPTY
                            return
                        }
                    }

                    val count = slot.stack?.count ?: return
                    val itemStack = (slot.inventory as TradeInventory).forceRemoveStack(slot.index, count)
                    bluePlayer.inventory.offerOrDrop(itemStack)
                    return
                }
                if (slotId in 0 .. 8) {
                    val cursorStack = handler.cursorStack
                    cursorStack.getOrCreateNbt().remove("cutetrade-add-by")

                    if (slot.hasStack()) {
                        slot.stack.getOrCreateNbt().put("cutetrade-add-by", NbtString.of("server"))
                        tradeScreenContext.context.blueAddTradeItem(slotId, slot.stack.copy())
                    } else {
                        tradeScreenContext.context.blueRemoveTradeItem(slotId)
                    }
                    return
                }
                if (slotId in 15 .. 53) {
                    if (slot.hasStack()) {
                        slot.stack.getOrCreateNbt().remove("cutetrade-add-by")
                    }
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