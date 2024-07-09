package io.github.gdrfgdrf.cutetrade.command

import io.github.gdrfgdrf.cutetrade.command.DevCommand.dev
import io.github.gdrfgdrf.cutetrade.screen.factory.DevScreenHandlerFactory
import io.github.gdrfgdrf.cutetrade.utils.command.CommandInvoker
import net.minecraft.server.command.ServerCommandSource

object DevCommand : AbstractCommand(
    command = "dev",
    onlyPlayer = true,
    noArgument = true,
    tree = { literalArgumentBuilder ->
        literalArgumentBuilder.executes {
            dev(it.source)
            0
        }
    }
) {

    private fun dev(source: ServerCommandSource) {
        source.player!!.openHandledScreen(DevScreenHandlerFactory)
    }

}