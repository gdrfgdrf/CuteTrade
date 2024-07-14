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

package io.github.gdrfgdrf.cutetrade.extension

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.ClickEvent
import net.minecraft.text.MutableText
import net.minecraft.text.Text

fun Text.clickEvent(clickEvent: ClickEvent): Text {
    (this as MutableText).styled {
        it.withClickEvent(clickEvent)
    }
    return this
}

fun Text.fillPrefix(): Text {
    val filled = "prefix".toCommandMessage()
        .format(this.string)
    return Text.of(filled)
}

fun Text.send(serverPlayerEntity: ServerPlayerEntity) {
    serverPlayerEntity.sendMessage(this)
}

fun buildRunCommandClickEvent(command: String): ClickEvent {
    return ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
}

fun buildRunCommandText(textGetter: () -> String, command: String): Text {
    return Text.of(textGetter()).clickEvent(buildRunCommandClickEvent(command))
}
