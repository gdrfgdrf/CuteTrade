package io.github.gdrfgdrf.cutetrade.command.admin

import io.github.gdrfgdrf.cutetrade.command.AbstractCommand
import io.github.gdrfgdrf.cutetrade.extension.send
import io.github.gdrfgdrf.cutetrade.extension.toCommandMessage
import io.github.gdrfgdrf.cutetrade.utils.command.CommandInvoker
import net.minecraft.server.command.ServerCommandSource

object HelpAdminCommand : AbstractCommand(
    command = "help",
    onlyPlayer = true,
    needOp = true,
    noArgument = true,
    tree = { literalArgumentBuilder ->
        literalArgumentBuilder.executes {
            HelpAdminCommand.help(it.source)
            0
        }
    }
){

    private fun help(source: ServerCommandSource) {
        val commandInvoker = CommandInvoker.of(source)

        "top".toCommandMessage().send("", commandInvoker)
        "admin_help".toCommandMessage().send("", commandInvoker)
        "bottom".toCommandMessage().send("", commandInvoker)
    }

}