package io.github.gdrfgdrf.cutetrade.extension

import cutetrade.protobuf.CommonProto.TradeItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

fun ItemStack.toProtobufTradeItem(addByName: String): TradeItem {
    return TradeItem.newBuilder()
        .setNbt(this.nbt?.asString())
        .setAddByName(addByName)
        .build()
}