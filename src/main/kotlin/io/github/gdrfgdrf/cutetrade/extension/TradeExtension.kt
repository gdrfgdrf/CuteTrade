package io.github.gdrfgdrf.cutetrade.extension

import cutetrade.protobuf.CommonProto
import cutetrade.protobuf.CommonProto.Trade
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.server.network.ServerPlayerEntity
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

fun Trade.printInformation(serverPlayerEntity: ServerPlayerEntity) {
    "top".toCommandMessage()
        .send("", serverPlayerEntity)

    val finishedMessage = "finished_result".toTradeMessage()
    val terminatedMessage = "terminated_result".toTradeMessage()
    val resultMessage = if (this.tradeResult == CommonProto.TradeResult.TRADE_RESULT_FINISHED) {
        finishedMessage
    } else {
        terminatedMessage
    }

    "red_player_is".toInformationMessage()
        .format(this.redName)
        .send("", serverPlayerEntity)
    "blue_player_is".toInformationMessage()
        .format(this.blueName)
        .send("", serverPlayerEntity)
    "trade_result".toInformationMessage()
        .format(resultMessage)
        .send("", serverPlayerEntity)

    val divider = "divider".toInformationMessage()

    if (this.tradeResult == CommonProto.TradeResult.TRADE_RESULT_FINISHED) {
        val message = "final_trade_item".toInformationMessage()
        val nothing = "nothing".toInformationMessage()

        divider.send("", serverPlayerEntity)

        "red_player_final_trade_item".toInformationMessage()
            .format(this.redName)
            .send("", serverPlayerEntity)

        val redItemResultList = this.redItemResultList
        var allEmpty = redItemResultList.stream().filter {
            it.nbt != "{}"
        }.findAny()
            .orElse(null)

        if (redItemResultList == null || redItemResultList.isEmpty() || allEmpty == null) {
            nothing
                .send("", serverPlayerEntity)
        } else {
            redItemResultList.forEach { tradeItem ->
                val itemStack = tradeItem.toItemStack()

                message
                    .format(itemStack.name.string, itemStack.count)
                    .send("", serverPlayerEntity)
            }
        }

        divider.send("", serverPlayerEntity)

        "blue_player_final_trade_item".toInformationMessage()
            .format(this.blueName)
            .send("", serverPlayerEntity)

        val blueItemResultList = this.blueItemResultList

        allEmpty = blueItemResultList.stream().filter {
            it.nbt != "{}"
        }.findAny()
            .orElse(null)
        if (blueItemResultList == null || blueItemResultList.isEmpty() || allEmpty == null) {
            nothing.send("", serverPlayerEntity)
        } else {
            blueItemResultList.forEach { tradeItem ->
                val itemStack = tradeItem.toItemStack()

                message
                    .format(itemStack.name.string, itemStack.count)
                    .send("", serverPlayerEntity)
            }
        }

        divider.send("", serverPlayerEntity)

        "trade_id".toInformationMessage()
            .format(this.id)
            .send("", serverPlayerEntity)
    }


    "bottom".toCommandMessage()
        .send("", serverPlayerEntity)
}