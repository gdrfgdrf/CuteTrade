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

package io.github.gdrfgdrf.cutetrade.extension

import cutetrade.protobuf.CommonProto.TradeItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.StringNbtReader

fun ItemStack.toProtobufTradeItem(addByName: String): TradeItem {
    val nbtCompound = NbtCompound()
    this.writeNbt(nbtCompound)

    return TradeItem.newBuilder()
        .setNbt(nbtCompound.asString())
        .setAddByName(addByName)
        .build()
}

fun TradeItem.toItemStack(): ItemStack {
    if ("{}" == this.nbt) {
        return ItemStack.EMPTY
    }

    val nbtCompound = StringNbtReader.parse(this.nbt)
    return ItemStack.fromNbt(nbtCompound)
}