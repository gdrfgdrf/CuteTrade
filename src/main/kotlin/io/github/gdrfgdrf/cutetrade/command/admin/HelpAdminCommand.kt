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