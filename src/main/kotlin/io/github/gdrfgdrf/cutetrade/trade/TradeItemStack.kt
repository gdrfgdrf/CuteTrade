package io.github.gdrfgdrf.cutetrade.trade

import io.github.gdrfgdrf.cutetrade.extension.send
import io.github.gdrfgdrf.cutetrade.extension.toScreenMessage
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

class TradeItemStack private constructor(private val playerEntity: ServerPlayerEntity) {
    var itemArray = arrayOfNulls<TradeItem>(9)

    fun size(): Int = itemArray.size

    fun get(index: Int): TradeItem? {
        if (index >= 9) {
            return null
        }
        return itemArray[index]
    }

    fun setTradeItem(
        index: Int,
        itemStack: ItemStack
    ) {
        if (index >= 9 || index < 0) {
            "trade_item_limited".toScreenMessage()
                .send(playerEntity)
            return
        }

        itemArray[index] = TradeItem(itemStack)
    }

    fun removeTradeItem(
        index: Int
    ) {
        itemArray[index] = null
    }

    fun removeAll() {
        itemArray = arrayOfNulls(9)
    }

    fun returnAll() {
        itemArray.forEach {
            it?.let {
                playerEntity.inventory.offerOrDrop(it.itemStack)
            }
        }
    }

    fun moveTo(serverPlayerEntity: ServerPlayerEntity) {
        itemArray.forEach {
            it?.let {
                serverPlayerEntity.inventory.offerOrDrop(it.itemStack)
            }
        }
    }

    class TradeItem(val itemStack: ItemStack) {
    }


    companion object {
        fun create(playerEntity: ServerPlayerEntity) = TradeItemStack(playerEntity)
    }
}