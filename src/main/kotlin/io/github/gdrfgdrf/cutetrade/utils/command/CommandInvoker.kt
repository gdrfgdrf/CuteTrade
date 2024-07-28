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

package io.github.gdrfgdrf.cutetrade.utils.command

import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

class CommandInvoker private constructor(val source: ServerCommandSource) {
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