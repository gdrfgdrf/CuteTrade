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

package io.github.gdrfgdrf.cutetrade.command

import io.github.gdrfgdrf.cutetrade.command.DevCommand.dev
import io.github.gdrfgdrf.cutetrade.extension.logInfo
import io.github.gdrfgdrf.cutetrade.screen.factory.DevScreenHandlerFactory
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