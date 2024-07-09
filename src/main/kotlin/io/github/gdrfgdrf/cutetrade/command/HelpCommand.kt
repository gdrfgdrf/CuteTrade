package io.github.gdrfgdrf.cutetrade.command

import io.github.gdrfgdrf.cutetrade.command.HelpCommand.help
import io.github.gdrfgdrf.cutetrade.extension.send
import io.github.gdrfgdrf.cutetrade.extension.toCommandMessage
import io.github.gdrfgdrf.cutetrade.utils.command.CommandInvoker
import net.minecraft.server.command.ServerCommandSource

object HelpCommand : AbstractCommand(
    command = "help",
    onlyPlayer = true,
    noArgument = true,
    tree = { literalArgumentBuilder ->
        literalArgumentBuilder.executes {
            help(it.source)
            0
        }
    }
) {

    private fun help(source: ServerCommandSource) {
        val commandInvoker = CommandInvoker.of(source)

        "top".toCommandMessage().send("", commandInvoker)
        "help".toCommandMessage().send("", commandInvoker)
        "bottom".toCommandMessage().send("", commandInvoker)
    }

}