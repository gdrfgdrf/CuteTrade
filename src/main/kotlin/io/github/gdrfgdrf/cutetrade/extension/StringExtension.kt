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
import io.github.gdrfgdrf.cutetrade.utils.command.CommandInvoker
import io.github.gdrfgdrf.cutetranslationapi.text.CuteText
import io.github.gdrfgdrf.cutetranslationapi.text.CuteTranslation
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

fun String.toCommandTranslation(commandInvoker: CommandInvoker): CuteTranslation {
    return toCommandTranslation(commandInvoker.source.player)
}

fun String.toCommandTranslation(serverPlayerEntity: ServerPlayerEntity? = null): CuteTranslation {
    if (serverPlayerEntity != null) {
        return translatable(serverPlayerEntity.name.string, "command.cutetrade.$this")
    }
    return translatable("command.cutetrade.$this")
}

fun String.toCommandText(serverPlayerEntity: ServerPlayerEntity? = null): CuteText {
    if (serverPlayerEntity != null) {
        return translatableText(serverPlayerEntity.name.string, "command.cutetrade.$this")
    }
    return translatableText("command.cutetrade.$this")
}

fun String.toScreenTranslation(commandInvoker: CommandInvoker): CuteTranslation {
    return toScreenTranslation(commandInvoker.source.player)
}

fun String.toScreenTranslation(serverPlayerEntity: ServerPlayerEntity? = null): CuteTranslation {
    if (serverPlayerEntity != null) {
        return translatable(serverPlayerEntity.name.string, "screen.cutetrade.$this")
    }
    return translatable("screen.cutetrade.$this")
}

fun String.toScreenText(serverPlayerEntity: ServerPlayerEntity? = null): CuteText {
    if (serverPlayerEntity != null) {
        return translatableText(serverPlayerEntity.name.string, "screen.cutetrade.$this")
    }
    return translatableText("screen.cutetrade.$this")
}

fun String.toTradeTranslation(commandInvoker: CommandInvoker): CuteTranslation {
    return toTradeTranslation(commandInvoker.source.player)
}

fun String.toTradeTranslation(serverPlayerEntity: ServerPlayerEntity? = null): CuteTranslation {
    if (serverPlayerEntity != null) {
        return translatable(serverPlayerEntity.name.string, "trade.cutetrade.$this")
    }
    return translatable("trade.cutetrade.$this")
}

fun String.toTradeText(serverPlayerEntity: ServerPlayerEntity? = null): CuteText {
    if (serverPlayerEntity != null) {
        return translatableText(serverPlayerEntity.name.string, "trade.cutetrade.$this")
    }
    return translatableText("trade.cutetrade.$this")
}

fun String.toInformationTranslation(commandInvoker: CommandInvoker): CuteTranslation {
    return toInformationTranslation(commandInvoker.source.player)
}

fun String.toInformationTranslation(serverPlayerEntity: ServerPlayerEntity? = null): CuteTranslation {
    if (serverPlayerEntity != null) {
        return translatable(serverPlayerEntity.name.string, "information.cutetrade.$this")
    }
    return translatable("information.cutetrade.$this")
}

fun CuteTranslation.send(prefix: CuteText, serverPlayerEntity: ServerPlayerEntity) {
    if (serverPlayerEntity.isDisconnected) {
        return
    }
    val text = prefix.build()

    if (notBlank(text.string)) {
        this.insert(0, prefix)
        send(serverPlayerEntity)
        return
    }
    send(serverPlayerEntity)
}

fun CuteTranslation.sendTo(serverPlayerEntity: ServerPlayerEntity) {
    if (!serverPlayerEntity.isDisconnected) {
        val prefix = "prefix".toCommandText(serverPlayerEntity)
        send(prefix, serverPlayerEntity)
    }
}

fun CuteTranslation.send(prefix: String, commandInvoker: CommandInvoker) {
    if (commandInvoker.source.player == null || commandInvoker.source.player!!.isDisconnected) {
        return
    }

    if (notBlank(prefix)) {
        val prefixText = CuteText.of(prefix)
        this.insert(0, prefixText)
        send(commandInvoker.source.player!!)
        return
    }
    send(commandInvoker.source.player!!)
}

fun CuteTranslation.send(commandInvoker: CommandInvoker) {
    if (commandInvoker.source.player == null || commandInvoker.source.player!!.isDisconnected) {
        return
    }
    val prefix = "prefix".toCommandText(commandInvoker.source.player)

    send(prefix, commandInvoker.source.player!!)
}

fun CuteTranslation.format0(vararg any: Any): CuteTranslation {
    this.get(0).format(*any)
    return this
}

fun CuteTranslation.format(index: Int, vararg any: Any): CuteTranslation {
    this.get(index).format(*any)
    return this
}

fun String.toCuteText(): CuteText {
    return CuteText.of(this)
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