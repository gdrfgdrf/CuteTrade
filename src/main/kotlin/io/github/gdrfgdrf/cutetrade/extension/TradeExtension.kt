package io.github.gdrfgdrf.cutetrade.extension

import cutetrade.protobuf.CommonProto
import cutetrade.protobuf.CommonProto.Trade
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.text.Text

fun Trade.toItemStack(): ItemStack {
    val item = if (this.tradeResult == CommonProto.TradeResult.TRADE_RESULT_FINISHED) {
        Items.GOLD_BLOCK
    } else {
        Items.REDSTONE_BLOCK
    }

    val itemStack = ItemStack(item)
    itemStack.setCustomName(Text.of(this.startTime.toInstant().formattedDate() + " | " + "click_view".toScreenMessage()))

    val nbtList = NbtList()
    nbtList.add(NbtString.of("click_view".toScreenMessage()))

    return itemStack
}