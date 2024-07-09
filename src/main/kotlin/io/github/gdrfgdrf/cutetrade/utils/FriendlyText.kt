package io.github.gdrfgdrf.cutetrade.utils

import io.github.gdrfgdrf.cutetrade.utils.command.CommandInvoker
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class FriendlyText private constructor(private val raw: String) {
    var enablePrefix: Boolean = true

    fun toText(): Text {
        return if (enablePrefix) {
            Text.of(prefix.replace("&", "§") +
                    raw.replace("&", "§"))
        } else {
            Text.of(raw.replace("&", "§"))
        }
    }

    fun toText(prefix: String): Text {
        return Text.of(prefix.replace("&", "§") +
                raw.replace("&", "§"))
    }

    fun send(prefix: String, serverPlayerEntity: ServerPlayerEntity) {
        serverPlayerEntity.sendMessage(toText(prefix))
    }

    fun send(serverPlayerEntity: ServerPlayerEntity) {
        serverPlayerEntity.sendMessage(toText())
    }

    fun send(prefix: String, commandInvoker: CommandInvoker) {
        commandInvoker.sendMessage(toText(prefix))
    }

    fun send(commandInvoker: CommandInvoker) {
        commandInvoker.sendMessage(toText())
    }

    fun middleWork(
        commandInvoker: CommandInvoker,
        runnable: () -> Unit
    ) {

    }

    companion object {
        var prefix: String = ""

        fun of(text: String): FriendlyText = FriendlyText(text)
    }
}