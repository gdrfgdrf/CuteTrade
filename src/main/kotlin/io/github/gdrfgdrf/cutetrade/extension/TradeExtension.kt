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

import cutetrade.protobuf.CommonProto
import cutetrade.protobuf.CommonProto.Trade
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

fun Trade.toItemStack(playerEntity: ServerPlayerEntity): ItemStack {
    val item = if (this.tradeResult == CommonProto.TradeResult.TRADE_RESULT_FINISHED) {
        Items.GOLD_BLOCK
    } else {
        Items.REDSTONE_BLOCK
    }

    val itemStack = ItemStack(item)
    itemStack.setCustomName(Text.of(
        this.startTime.toInstant().formattedDate() +
                " | " +
                "click_view".toScreenTranslation(playerEntity).build().string))

    val nbtList = NbtList()
    nbtList.add(NbtString.of("click_view".toScreenText(playerEntity).build().string))

    return itemStack
}

fun Trade.printInformation(serverPlayerEntity: ServerPlayerEntity) {
    val emptyPrefix = "".toCuteText()

    "top".toCommandTranslation(serverPlayerEntity)
        .send(emptyPrefix, serverPlayerEntity)

    val finishedMessage = "finished_result".toTradeTranslation(serverPlayerEntity)
    val terminatedMessage = "terminated_result".toTradeTranslation(serverPlayerEntity)
    val resultMessage = if (this.tradeResult == CommonProto.TradeResult.TRADE_RESULT_FINISHED) {
        finishedMessage
    } else {
        terminatedMessage
    }

    "red_player_is".toInformationTranslation(serverPlayerEntity)
        .format0(this.redName)
        .send(emptyPrefix, serverPlayerEntity)
    "blue_player_is".toInformationTranslation(serverPlayerEntity)
        .format0(this.blueName)
        .send(emptyPrefix, serverPlayerEntity)
    "trade_result".toInformationTranslation(serverPlayerEntity)
        .format0(resultMessage.build().string)
        .send(emptyPrefix, serverPlayerEntity)

    val divider = "divider".toInformationTranslation(serverPlayerEntity)

    if (this.tradeResult == CommonProto.TradeResult.TRADE_RESULT_FINISHED) {
        val nothing = "nothing".toInformationTranslation(serverPlayerEntity)

        divider.send(emptyPrefix, serverPlayerEntity)

        "red_player_final_trade_item".toInformationTranslation(serverPlayerEntity)
            .format0(this.redName)
            .send(emptyPrefix, serverPlayerEntity)

        val redItemResultList = this.redItemResultList
        var allEmpty = redItemResultList.stream().filter {
            it.nbt != "{}"
        }.findAny()
            .orElse(null)

        if (redItemResultList == null || redItemResultList.isEmpty() || allEmpty == null) {
            nothing
                .send(emptyPrefix, serverPlayerEntity)
        } else {
            redItemResultList.forEach { tradeItem ->
                val message = "final_trade_item".toInformationTranslation(serverPlayerEntity)
                val itemStack = tradeItem.toItemStack()

                message
                    .format0(itemStack.name.string, itemStack.count)
                    .send(emptyPrefix, serverPlayerEntity)
            }
        }

        divider.send(emptyPrefix, serverPlayerEntity)

        "blue_player_final_trade_item".toInformationTranslation(serverPlayerEntity)
            .format0(this.blueName)
            .send(emptyPrefix, serverPlayerEntity)

        val blueItemResultList = this.blueItemResultList

        allEmpty = blueItemResultList.stream().filter {
            it.nbt != "{}"
        }.findAny()
            .orElse(null)
        if (blueItemResultList == null || blueItemResultList.isEmpty() || allEmpty == null) {
            nothing.send(emptyPrefix, serverPlayerEntity)
        } else {
            blueItemResultList.forEach { tradeItem ->
                val message = "final_trade_item".toInformationTranslation(serverPlayerEntity)
                val itemStack = tradeItem.toItemStack()

                message
                    .format0(itemStack.name.string, itemStack.count)
                    .send(emptyPrefix, serverPlayerEntity)
            }
        }

        divider.send(emptyPrefix, serverPlayerEntity)

        "trade_id".toInformationTranslation(serverPlayerEntity)
            .format0(this.id)
            .send(emptyPrefix, serverPlayerEntity)
    }


    "bottom".toCommandTranslation(serverPlayerEntity)
        .send(emptyPrefix, serverPlayerEntity)
}