package io.github.gdrfgdrf.cutetrade.extension

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import io.github.gdrfgdrf.cutetrade.utils.FriendlyText
import io.github.gdrfgdrf.cutetrade.utils.command.CommandInvoker
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import java.util.UUID

fun String.toCommandMessage(): String {
    return Text.translatable("command.cutetrade.$this").string
}

fun String.toScreenMessage(): String {
    return Text.translatable("screen.cutetrade.$this").string
}

fun String.toTradeMessage(): String {
    return Text.translatable("trade.cutetrade.$this").string
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