/*
 * Copyright 2024 CuteTrade's contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.gdrfgdrf.cutetrade.trade

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
        itemArray[index] = null
//        val tradeItem = itemArray[index]
//        if (tradeItem != null) {
////            "remove trade item ${tradeItem.itemStack} from index $index".logInfo()
//            tradeItem.itemStack = ItemStack.EMPTY
//        }
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
        itemArray.forEachIndexed { index, it ->
            it?.let {
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