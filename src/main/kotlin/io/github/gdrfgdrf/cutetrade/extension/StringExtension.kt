package io.github.gdrfgdrf.cutetrade.extension

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import cutetrade.protobuf.CommonProto.Player
import io.github.gdrfgdrf.cutetrade.manager.PlayerManager
import io.github.gdrfgdrf.cutetrade.utils.FriendlyText
import io.github.gdrfgdrf.cutetrade.utils.command.CommandInvoker
import io.github.gdrfgdrf.cutetrade.utils.text.CuteTranslatableTextContent
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableTextContent
import java.util.*

fun String.toCommandMessage(): String {
    return MutableText.of(CuteTranslatableTextContent(
        "command.cutetrade.$this",
        null,
        TranslatableTextContent.EMPTY_ARGUMENTS
    )).string
}

fun String.toScreenMessage(): String {
    return MutableText.of(CuteTranslatableTextContent(
        "screen.cutetrade.$this",
        null,
        TranslatableTextContent.EMPTY_ARGUMENTS
    )).string
}

fun String.toTradeMessage(): String {
    return MutableText.of(CuteTranslatableTextContent(
        "trade.cutetrade.$this",
        null,
        TranslatableTextContent.EMPTY_ARGUMENTS
    )).string
}

fun String.toInformationMessage(): String {
    return MutableText.of(CuteTranslatableTextContent(
        "information.cutetrade.$this",
        null,
        TranslatableTextContent.EMPTY_ARGUMENTS
    )).string
}


fun String.send(prefix: String, serverPlayerEntity: ServerPlayerEntity) {
    if (!serverPlayerEntity.isDisconnected) {
        toFriendlyText().send(prefix, serverPlayerEntity)
    }
}

fun String.send(serverPlayerEntity: ServerPlayerEntity) {
    if (!serverPlayerEntity.isDisconnected) {
        toFriendlyText().send(serverPlayerEntity)
    }
}

fun String.send(prefix: String, commandInvoker: CommandInvoker) {
    toFriendlyText().send(prefix, commandInvoker)
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