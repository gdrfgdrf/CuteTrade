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

package io.github.gdrfgdrf.cutetrade.command.suggest

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.gdrfgdrf.cutetrade.common.manager.ProtobufPlayerManager
import net.minecraft.server.command.ServerCommandSource
import java.util.concurrent.CompletableFuture

object AllPlayersSuggestProvider : SuggestionProvider<ServerCommandSource> {
    override fun getSuggestions(
        context: CommandContext<ServerCommandSource>?,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        context ?: return builder.buildFuture()

        val nameToPlayerMap = ProtobufPlayerManager.playerProtobuf?.message?.nameToPlayerMap
            ?: return builder.buildFuture()

        nameToPlayerMap.forEach { (name, _) ->
            builder.suggest(name)
        }

        return builder.buildFuture()
    }
}