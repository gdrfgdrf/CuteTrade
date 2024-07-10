package io.github.gdrfgdrf.cutetrade.command

import io.github.gdrfgdrf.cutetrade.extension.send
import io.github.gdrfgdrf.cutetrade.extension.toCommandMessage
import io.github.gdrfgdrf.cutetrade.utils.command.CommandInvoker
import net.minecraft.server.command.ServerCommandSource

object TutorialCommand : AbstractCommand(
    command = "tutorial",
    onlyPlayer = true,
    noArgument = true,
    tree = { literalArgumentBuilder ->
        literalArgumentBuilder.executes {
            TutorialCommand.print(it.source)
            0
        }
    }
) {

    private fun print(source: ServerCommandSource) {
        val commandInvoker = CommandInvoker.of(source)

        "top".toCommandMessage().send("", commandInvoker)
        "tutorial".toCommandMessage().send("", commandInvoker)
        "bottom".toCommandMessage().send("", commandInvoker)
    }

}