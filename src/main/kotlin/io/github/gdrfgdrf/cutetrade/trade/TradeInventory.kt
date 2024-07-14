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

import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack

class TradeInventory : SimpleInventory(18) {
    override fun removeStack(slot: Int): ItemStack {
        if (slot in 9 .. 17) {
            return ItemStack.EMPTY
        }
        return super.removeStack(slot)
    }

    override fun removeStack(slot: Int, amount: Int): ItemStack {
        if (slot in 9 .. 17) {
            return ItemStack.EMPTY
        }
        return super.removeStack(slot, amount)
    }

    override fun canTransferTo(hopperInventory: Inventory?, slot: Int, stack: ItemStack?): Boolean {
        return slot !in 9 .. 17
    }
}

