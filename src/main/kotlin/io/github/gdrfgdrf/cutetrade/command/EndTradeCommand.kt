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

import io.github.gdrfgdrf.cutetrade.command.EndTradeCommand.start
import io.github.gdrfgdrf.cutetrade.extension.currentTrade
import io.github.gdrfgdrf.cutetrade.extension.translationScope
import io.github.gdrfgdrf.cutetrade.utils.command.CommandInvoker
import net.minecraft.server.command.ServerCommandSource

object EndTradeCommand : AbstractCommand(
    command = "end-trade",
    onlyPlayer = true,
    noArgument = true,
    tree = { literalArgumentBuilder ->
        literalArgumentBuilder.executes { context ->
            playerCheck(EndTradeCommand, context) {
                start(context.source)
            }
            0
        }
    }
) {
    private fun start(source: ServerCommandSource) {
        val commandInvoker = CommandInvoker.of(source)

        commandInvoker.translationScope {
            val currentTrade = source.player?.currentTrade()
            if (currentTrade == null) {
                toCommandTranslation("no_transaction_in_progress")
                    .send()
                return@translationScope
            }

            currentTrade.terminate()
        }
    }

}