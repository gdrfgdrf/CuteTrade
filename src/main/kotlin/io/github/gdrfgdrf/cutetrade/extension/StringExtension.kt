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

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import cutetrade.protobuf.CommonProto.Player
import io.github.gdrfgdrf.cutetrade.manager.PlayerManager
import io.github.gdrfgdrf.cutetrade.utils.FriendlyText
import io.github.gdrfgdrf.cutetrade.utils.command.CommandInvoker
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import java.util.*

fun String.toText(): Text {
    return Text.of(this)
}

fun String.toCommandMessage(): String {
    return Text.translatable("command.cutetrade.$this").string.replace("&", "§")
}

fun String.toScreenMessage(): String {
    return Text.translatable("screen.cutetrade.$this").string.replace("&", "§")
}

fun String.toTradeMessage(): String {
    return Text.translatable("trade.cutetrade.$this").string.replace("&", "§")
}

fun String.toInformationMessage(): String {
    return Text.translatable("information.cutetrade.$this").string.replace("&", "§")
}

fun String.send(prefix: String, serverPlayerEntity: ServerPlayerEntity) {
    if (notBlank(prefix)) {
        toFriendlyText().send(prefix, serverPlayerEntity)
        return
    }
    toFriendlyText().send("$prefix%s", serverPlayerEntity)
}

fun String.send(serverPlayerEntity: ServerPlayerEntity) {
    if (!serverPlayerEntity.isDisconnected) {
        toFriendlyText().send(serverPlayerEntity)
    }
}

fun String.send(prefix: String, commandInvoker: CommandInvoker) {
    if (notBlank(prefix)) {
        toFriendlyText().send(prefix, commandInvoker)
        return
    }
    toFriendlyText().send("$prefix%s", commandInvoker)
}

fun String.send(commandInvoker: CommandInvoker) {
    toFriendlyText().send(commandInvoker)
}

fun String.toFriendlyText(): FriendlyText {
    return FriendlyText.of(this)
}

fun String.getContent(commandContext: CommandContext<ServerCommandSource>): String {
    return StringArgumentType.getString(commandContext, this)
}

fun String.findProtobufPlayer(): Player? {
    return PlayerManager.findPlayer(this)
}

fun length(string: String?): Int {
    if (string == null) {
        return 0
    }
    return string.length
}

fun notBlank(string: String?): Boolean {
    val length = length(string)
    if (length == 0) {
        return false
    }
    for (i in 0 until length) {
        if (!Character.isWhitespace(string!![i])) {
            return true
        }
    }
    return false
}

fun generateTradeId(): String {
    var randomId = UUID.randomUUID().toString()
        .lowercase()
        .replace("-", "")
    randomId += Math.random().toString()
        .replace(".", "")
    return randomId
}