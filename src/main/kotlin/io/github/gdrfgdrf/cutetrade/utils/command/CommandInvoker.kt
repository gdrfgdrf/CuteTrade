package io.github.gdrfgdrf.cutetrade.utils.command

import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

class CommandInvoker private constructor(private val source: ServerCommandSource) {

    fun sendMessage(str: String) = sendMessage(Text.of(str))

    fun sendMessage(text: Text) {
        if (!isConsole()) {
            source.entity?.sendMessage(text)
            return
        }
        source.server.sendMessage(text)
    }

    fun isOp(): Boolean = isConsole() || source.hasPermissionLevel(3)

    fun isConsole(): Boolean = source.entity == null

    companion object {
        fun of(source: ServerCommandSource): CommandInvoker = CommandInvoker(source)
    }
}