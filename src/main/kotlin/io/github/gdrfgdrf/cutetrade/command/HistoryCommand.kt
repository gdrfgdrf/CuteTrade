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

import io.github.gdrfgdrf.cutetrade.common.command.HistoryCommandExecutor
import io.github.gdrfgdrf.cutetrade.extension.findPlayerProxy
import net.minecraft.server.command.ServerCommandSource

object HistoryCommand : AbstractCommand(
    command = "history",
    onlyPlayer = true,
    noArgument = true,
    tree = { literalArgumentBuilder ->
        literalArgumentBuilder.executes {
            playerCheck(HistoryCommand, it) {
                HistoryCommand.print(it.source, it.source.player!!.name.string)
            }
            0
        }
    }
) {

    fun print(source: ServerCommandSource, playerName: String) {
        runCatching {
            source.findPlayerProxy()?.let {
                HistoryCommandExecutor.execute(it, playerName)
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

}