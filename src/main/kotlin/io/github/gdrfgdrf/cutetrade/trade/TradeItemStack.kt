package io.github.gdrfgdrf.cutetrade.trade

import io.github.gdrfgdrf.cutetrade.extension.logInfo
import io.github.gdrfgdrf.cutetrade.extension.send
import io.github.gdrfgdrf.cutetrade.extension.toScreenMessage
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

class TradeItemStack private constructor(val playerEntity: ServerPlayerEntity) {
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

//        "set trade item $itemStack to index $index".logInfo()
        itemArray[index] = TradeItem(itemStack)
    }

    fun removeTradeItem(
        index: Int
    ) {
        val tradeItem = itemArray[index]
        if (tradeItem != null) {
//            "remove trade item ${tradeItem.itemStack} from index $index".logInfo()
            tradeItem.itemStack = ItemStack.EMPTY
        }
    }

    fun removeAll() {
        itemArray = arrayOfNulls(9)
    }

    fun returnAll() {
        itemArray.forEach {
            it?.let {
                it.itemStack.getOrCreateNbt().remove("cutetrade-add-by")
                playerEntity.inventory.offerOrDrop(it.itemStack)
            }
        }
    }

    fun moveTo(serverPlayerEntity: ServerPlayerEntity) {
        itemArray.forEachIndexed { index, it ->
            it?.let {
//                "offer ${it.itemStack}(index $index) to ${serverPlayerEntity.name.string}".logInfo()
                it.itemStack.getOrCreateNbt().remove("cutetrade-add-by")
                serverPlayerEntity.inventory.offerOrDrop(it.itemStack)
            }
        }
    }

    fun copy(): TradeItemStack {
        val tradeItemStack = TradeItemStack(playerEntity)
        itemArray.forEachIndexed { index, it ->
            it?.let {
                if (!it.itemStack.isEmpty) {
                    tradeItemStack.setTradeItem(index, it.itemStack.copy())
                }
            }
        }
        return tradeItemStack
    }

    class TradeItem(var itemStack: ItemStack) {
    }


    companion object {
        fun create(playerEntity: ServerPlayerEntity) = TradeItemStack(playerEntity)
    }
}