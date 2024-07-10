package io.github.gdrfgdrf.cutetrade.command

import io.github.gdrfgdrf.cutetrade.command.DevCommand.dev
import io.github.gdrfgdrf.cutetrade.extension.logInfo
import io.github.gdrfgdrf.cutetrade.screen.factory.DevScreenHandlerFactory
import io.github.gdrfgdrf.cutetrade.utils.command.CommandInvoker
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerListener
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

        source.player!!.currentScreenHandler.addListener(object : ScreenHandlerListener {
            override fun onSlotUpdate(handler: ScreenHandler?, slotId: Int, stack: ItemStack?) {
                "onSlotUpdate $slotId | $stack".logInfo()
            }

            override fun onPropertyUpdate(handler: ScreenHandler?, property: Int, value: Int) {
            }

        })
    }

}