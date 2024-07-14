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

package io.github.gdrfgdrf.cutetrade.utils

import io.github.gdrfgdrf.cutetrade.utils.command.CommandInvoker
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class FriendlyText private constructor(private val raw: String) {
    var enablePrefix: Boolean = true

    fun toText(): Text {
        return if (enablePrefix) {
            Text.of(prefix.replace("&", "§").format(raw.replace("&", "§")))
        } else {
            Text.of(raw.replace("&", "§"))
        }
    }

    fun toText(prefix: String): Text {
        return Text.of(prefix.replace("&", "§").format(raw.replace("&", "§")))
    }

    fun send(prefix: String, serverPlayerEntity: ServerPlayerEntity) {
        serverPlayerEntity.sendMessage(toText(prefix))
    }

    fun send(serverPlayerEntity: ServerPlayerEntity) {
        serverPlayerEntity.sendMessage(toText())
    }

    fun send(prefix: String, commandInvoker: CommandInvoker) {
        commandInvoker.sendMessage(toText(prefix))
    }

    fun send(commandInvoker: CommandInvoker) {
        commandInvoker.sendMessage(toText())
    }

    companion object {
        var prefix: String = ""

        fun of(text: String): FriendlyText = FriendlyText(text)
    }
}