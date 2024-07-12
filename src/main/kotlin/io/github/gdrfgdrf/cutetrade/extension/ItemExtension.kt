package io.github.gdrfgdrf.cutetrade.extension

import cutetrade.protobuf.CommonProto.TradeItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.StringNbtReader
import java.io.DataInputStream

fun ItemStack.toProtobufTradeItem(addByName: String): TradeItem {
    val nbtCompound = NbtCompound()
    this.writeNbt(nbtCompound)

    return TradeItem.newBuilder()
        .setNbt(nbtCompound.asString())
        .setAddByName(addByName)
        .build()
}

fun TradeItem.toItemStack(): ItemStack {
    if (this.nbt == "{}") {
        return ItemStack.EMPTY
    }

    val nbtCompound = StringNbtReader.parse(this.nbt)
    return ItemStack.fromNbt(nbtCompound)
}