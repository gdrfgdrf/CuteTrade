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

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import cutetrade.protobuf.CommonProto.Player
import io.github.gdrfgdrf.cutetrade.common.manager.ProtobufPlayerManager
import net.minecraft.server.command.ServerCommandSource

fun String.getContent(commandContext: CommandContext<ServerCommandSource>): String {
    return StringArgumentType.getString(commandContext, this)
}

fun length(string: String?): Int {
    if (string == null) {
        return 0
    }
    return string.length
}

fun notBlank(string: String?): Boolean {
    val length = length(string)
    if (length == 0) {
        return false
    }
    for (i in 0 until length) {
        if (!Character.isWhitespace(string!![i])) {
            return true
        }
    }
    return false
}