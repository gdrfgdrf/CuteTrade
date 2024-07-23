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
import net.minecraft.text.HoverEvent
import net.minecraft.text.Text

fun Trade.toItemStack(serverPlayerEntity: ServerPlayerEntity): ItemStack {
    val item = if (this.tradeResult == CommonProto.TradeResult.TRADE_RESULT_FINISHED) {
        Items.GOLD_BLOCK
    } else {
        Items.REDSTONE_BLOCK
    }

    return serverPlayerEntity.translationScope {
        val itemStack = ItemStack(item)
        itemStack.setCustomName(Text.of(
            startTime.toInstant().formattedDate() +
                    " | " +
                    toScreenTranslation("click_view").build().string))

        itemStack
    }
}

fun Trade.printInformation(serverPlayerEntity: ServerPlayerEntity) {
    serverPlayerEntity.translationScope {
        toCommandTranslation("top")
            .send("")

        val finishedMessage = toTradeTranslation("finished_result")
        val terminatedMessage = toTradeTranslation("terminated_result")
        val resultMessage = if (tradeResult == CommonProto.TradeResult.TRADE_RESULT_FINISHED) {
            finishedMessage
        } else {
            terminatedMessage
        }

        toInformationTranslation("red_player_is")
            .format0(redName)
            .send("")
        toInformationTranslation("blue_player_is")
            .format0(blueName)
            .send("")
        toInformationTranslation("trade_result")
            .format0(resultMessage.build().string)
            .send("")

        val divider = toInformationTranslation("divider")

        if (tradeResult == CommonProto.TradeResult.TRADE_RESULT_FINISHED) {
            val nothing = toInformationTranslation("nothing")

            divider.send("")

            toInformationTranslation("red_player_final_trade_item")
                .format0(redName)
                .send("")

            val redItemResultList = redItemResultList
            var allEmpty = redItemResultList.stream().filter {
                it.nbt != "{}"
            }.findAny()
                .orElse(null)

            if (redItemResultList == null || redItemResultList.isEmpty() || allEmpty == null) {
                nothing.send("")
            } else {
                redItemResultList.forEach { tradeItem ->
                    val itemStack = tradeItem.toItemStack()
                    val itemStackContent = HoverEvent.ItemStackContent(itemStack)
                    val message = toInformationTranslation("final_trade_item")

                    message.get0()
                        .showItem(itemStackContent)

                    message.format0(itemStack.name.string, itemStack.count)
                        .send("")
                }
            }

            divider.send("")

            toInformationTranslation("blue_player_final_trade_item")
                .format0(blueName)
                .send("")

            val blueItemResultList = blueItemResultList

            allEmpty = blueItemResultList.stream().filter {
                it.nbt != "{}"
            }.findAny()
                .orElse(null)
            if (blueItemResultList == null || blueItemResultList.isEmpty() || allEmpty == null) {
                nothing.send("")
            } else {
                blueItemResultList.forEach { tradeItem ->
                    val itemStack = tradeItem.toItemStack()
                    val itemStackContent = HoverEvent.ItemStackContent(itemStack)
                    val message = toInformationTranslation("final_trade_item")

                    message.get0()
                        .showItem(itemStackContent)

                    message.format0(itemStack.name.string, itemStack.count)
                        .send("")
                }
            }

            divider.send("")

            toInformationTranslation("trade_id")
                .format0(id)
                .send("")
        }

        toCommandTranslation("bottom")
            .send("")
    }
}